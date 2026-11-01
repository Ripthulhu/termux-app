package com.termux.app.api.file;

import com.termux.app.api.file.FileReceiverActivity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class FileReceiverActivityTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test public void refusesPathNamesBeforeCreatingAnything() throws Exception {
        File directory = new File(temporary.getRoot(), "downloads");
        for (String name : new String[]{null, "", ".", "..", "../outside", "/absolute", "sub/file", "sub\\file", "bad\0name"}) {
            try {
                FileReceiverActivity.saveStreamWithName(new ByteArrayInputStream(new byte[]{1}), name, directory);
                Assert.fail("Accepted invalid name: " + name);
            } catch (IOException expected) {
                Assert.assertFalse(directory.exists());
            }
        }
    }

    @Test public void refusesToTruncateAnExistingDownload() throws Exception {
        File directory = temporary.newFolder("downloads");
        File target = new File(directory, "notes.txt");
        try (FileOutputStream output = new FileOutputStream(target)) { output.write(new byte[]{1, 2, 3}); }
        try {
            FileReceiverActivity.saveStreamWithName(new ByteArrayInputStream(new byte[]{9}), "notes.txt", directory);
            Assert.fail("Overwrote an existing download");
        } catch (IOException expected) {
            Assert.assertEquals(3, target.length());
        }
    }

    @Test
    public void testIsSharedTextAnUrl() {
        List<String> validUrls = new ArrayList<>();
        validUrls.add("http://example.com");
        validUrls.add("https://example.com");
        validUrls.add("https://example.com/path/parameter=foo");
        validUrls.add("magnet:?xt=urn:btih:d540fc48eb12f2833163eed6421d449dd8f1ce1f&dn=Ubuntu+desktop+19.04+%2864bit%29&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80&tr=udp%3A%2F%2Ftracker.ccc.de%3A80");
        for (String url : validUrls) {
            Assert.assertTrue(FileReceiverActivity.isSharedTextAnUrl(url));
        }

        List<String> invalidUrls = new ArrayList<>();
        invalidUrls.add("a test with example.com");
        invalidUrls.add("");
        invalidUrls.add(null);
        for (String url : invalidUrls) {
            Assert.assertFalse(FileReceiverActivity.isSharedTextAnUrl(url));
        }
    }

}
