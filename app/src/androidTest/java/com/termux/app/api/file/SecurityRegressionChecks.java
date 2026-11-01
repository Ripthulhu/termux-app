package com.termux.app.api.file;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.system.Os;
import android.system.ErrnoException;
import android.util.Log;
import android.view.Gravity;

import com.termux.R;
import com.termux.app.TermuxActivity;
import com.termux.filepicker.TermuxDocumentsProvider;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalSession;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Native checks for Linux file semantics and a blocked PTY; run on a disposable emulator. */
public class SecurityRegressionChecks extends Instrumentation {
    @Override public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        start();
    }

    @Override public void onStart() {
        Bundle result = new Bundle();
        Bundle status = new Bundle();
        status.putString("class", getClass().getName());
        status.putString("test", "fileAndInputSecurity");
        status.putInt("numtests", 1);
        status.putInt("current", 1);
        sendStatus(1, status);
        try {
            checkFiles();
            checkStalledTerminal();
            result.putString("stream", "PASS: file boundaries, exclusive saves, partial-file cleanup and responsive stalled-terminal input\n");
            sendStatus(0, status);
            finish(Activity.RESULT_OK, result);
        } catch (Throwable failure) {
            status.putString("stack", Log.getStackTraceString(failure));
            sendStatus(-2, status);
            result.putString("stream", Log.getStackTraceString(failure));
            finish(Activity.RESULT_CANCELED, result);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private void checkFiles() throws Exception {
        List<File> cleanup = new ArrayList<>();
        File root = File.createTempFile("termux-security-", "", getTargetContext().getCacheDir());
        require(root.delete() && root.mkdir(), "Create fixture directory");
        cleanup.add(root);
        try {
            File downloads = new File(root, "downloads");
            File outside = new File(root, "downloads-private");
            require(downloads.mkdir() && outside.mkdir(), "Create fixture folders");
            cleanup.add(downloads);
            cleanup.add(outside);
            File secret = FileReceiverActivity.saveStreamWithName(new ByteArrayInputStream(new byte[]{1, 2, 3}), "secret", outside);
            cleanup.add(secret);
            File normal = FileReceiverActivity.saveStreamWithName(new ByteArrayInputStream(new byte[]{4, 5, 6}), "normal", downloads);
            cleanup.add(normal);
            require((Os.stat(normal.getPath()).st_mode & 0777) == 0600, "Private download permissions");
            for (File descriptor : new File("/proc/self/fd").listFiles()) {
                try {
                    require(!Os.readlink(descriptor.getPath()).equals(normal.getPath()), "Close completed download descriptor");
                } catch (ErrnoException disappeared) {
                    // Directory enumeration can include an already closed descriptor.
                }
            }
            for (String name : new String[]{"normal", "../downloads-private/secret"}) {
                try {
                    FileReceiverActivity.saveStreamWithName(new ByteArrayInputStream(new byte[]{9}), name, downloads);
                    throw new AssertionError("Accepted unsafe or existing name: " + name);
                } catch (IOException expected) { }
            }
            for (String name : new String[]{"outside-link", "dangling-link"}) {
                File link = new File(downloads, name);
                Os.symlink(name.equals("outside-link") ? secret.getPath() : new File(downloads, "unused").getPath(), link.getPath());
                cleanup.add(link);
                try {
                    FileReceiverActivity.saveStreamWithName(new ByteArrayInputStream(new byte[]{9}), name, downloads);
                    throw new AssertionError("Followed a symlink: " + name);
                } catch (IOException expected) { }
            }
            require(!new File(downloads, "unused").exists(), "Do not follow a dangling link inside Downloads");
            try (InputStream input = new FileInputStream(secret)) { require(input.read() == 1, "Outside file unchanged"); }
            try (InputStream input = new FileInputStream(normal)) { require(input.read() == 4, "Existing file unchanged"); }
            InputStream broken = new InputStream() {
                private boolean first = true;
                @Override public int read() throws IOException {
                    if (first) { first = false; return 7; }
                    throw new IOException("Simulated interrupted transfer");
                }
            };
            try {
                FileReceiverActivity.saveStreamWithName(broken, "partial", downloads);
                throw new AssertionError("Expected the transfer to fail");
            } catch (IOException expected) {
                require(!new File(downloads, "partial").exists(), "Remove incomplete download");
            }
            TermuxDocumentsProvider provider = new TermuxDocumentsProvider();
            File storageLink = new File(downloads, "storage");
            Os.symlink(outside.getPath(), storageLink.getPath());
            cleanup.add(storageLink);
            require(!provider.isChildDocument(downloads.getPath(), secret.getPath()), "Reject sibling prefix");
            require(!provider.isChildDocument(downloads.getPath(), new File(downloads, "../downloads-private/secret").getPath()), "Reject traversal");
            require(!provider.isChildDocument(downloads.getPath(), new File(storageLink, "secret").getPath()), "Reject symlink escape");
            require(provider.isChildDocument(storageLink.getPath(), new File(storageLink, "secret").getPath()), "Allow explicitly selected storage-link tree");
            Uri tree = DocumentsContract.buildTreeDocumentUri("com.termux.documents", downloads.getPath());
            Uri valid = DocumentsContract.buildDocumentUriUsingTree(tree, normal.getPath());
            try (InputStream input = getTargetContext().getContentResolver().openInputStream(valid)) {
                require(input != null && input.read() == 4, "Read file inside selected folder");
            }
            Uri escaped = DocumentsContract.buildDocumentUriUsingTree(tree, new File(storageLink, "secret").getPath());
            try (InputStream ignored = getTargetContext().getContentResolver().openInputStream(escaped)) {
                throw new AssertionError("Framework allowed an escaping tree URI");
            } catch (SecurityException expected) { }
        } finally {
            Collections.reverse(cleanup);
            for (File file : cleanup) require(file.delete(), "Remove test fixture: " + file.getName());
        }
    }

    private void checkStalledTerminal() throws Exception {
        TermuxActivity activity = (TermuxActivity) startActivitySync(new Intent(getTargetContext(), TermuxActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        for (int i = 0; i < 100 && activity.getTermuxService() == null; i++) SystemClock.sleep(100);
        require(activity.getTermuxService() != null, "Terminal service available");
        TerminalSession previous = activity.getCurrentSession();
        TerminalSession[] stalled = new TerminalSession[1];
        try {
            runOnMainSync(() -> {
                TermuxSession session = activity.getTermuxService().createTermuxSession("/system/bin/sh",
                    new String[]{"-c", "/system/bin/stty raw -echo; printf TEST_INPUT_READY; /system/bin/sleep 10"},
                    null, "/", false, "Input regression");
                require(session != null, "Create isolated terminal process");
                stalled[0] = session.getTerminalSession();
                activity.getTermuxTerminalSessionClient().setCurrentSession(stalled[0]);
            });
            boolean[] ready = {false};
            for (int i = 0; i < 100 && !ready[0]; i++) {
                runOnMainSync(() -> ready[0] = stalled[0].getEmulator() != null &&
                    stalled[0].getEmulator().getScreen().getTranscriptText().contains("TEST_INPUT_READY"));
                if (!ready[0]) SystemClock.sleep(50);
            }
            require(ready[0], "Raw terminal is sleeping without reading input");
            char[] chars = new char[512 * 1024];
            Arrays.fill(chars, 'a');
            String paste = new String(chars);
            long start = SystemClock.uptimeMillis();
            runOnMainSync(() -> {
                stalled[0].getEmulator().paste(paste);
                stalled[0].getEmulator().paste(paste + paste + paste);
                activity.findViewById(R.id.sessions_button).performClick();
                activity.getDrawer().openDrawer(Gravity.START, false);
                require(activity.getDrawer().isDrawerVisible(Gravity.START), "Session menu stays usable");
            });
            require(SystemClock.uptimeMillis() - start < 2000, "Paste must not wait for the sleeping terminal");
        } finally {
            runOnMainSync(() -> {
                if (stalled[0] != null) {
                    activity.getTermuxTerminalSessionClient().setCurrentSession(previous);
                    stalled[0].finishIfRunning();
                    activity.getTermuxService().removeTermuxSession(stalled[0]);
                }
                activity.finish();
            });
        }
    }
}
