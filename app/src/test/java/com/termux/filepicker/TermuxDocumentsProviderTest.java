package com.termux.filepicker;

import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.provider.DocumentsContract;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.rules.TemporaryFolder;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class TermuxDocumentsProviderTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test public void folderGrantsRejectTraversalAndSiblingPrefixes() throws Exception {
        TermuxDocumentsProvider provider = new TermuxDocumentsProvider();
        File folder = temporary.newFolder("granted");
        File inside = new File(folder, "inside.txt");
        assertTrue(inside.createNewFile());
        File sibling = temporary.newFolder("granted-private");
        File outside = new File(sibling, "secret.txt");
        assertTrue(outside.createNewFile());
        assertTrue(provider.isChildDocument(folder.getPath(), inside.getPath()));
        assertFalse(provider.isChildDocument(folder.getPath(), outside.getPath()));
        assertFalse(provider.isChildDocument(folder.getPath(),
            new File(folder, "../granted-private/secret.txt").getPath()));
        assertFalse(provider.isChildDocument(folder.getPath(), new File(folder, "missing").getPath()));
    }

    @Test public void frameworkOpenRejectsAnEscapingTreeUri() throws Exception {
        TermuxDocumentsProvider provider = new TermuxDocumentsProvider();
        ProviderInfo info = new ProviderInfo();
        info.authority = "com.termux.documents";
        info.exported = true;
        info.grantUriPermissions = true;
        info.readPermission = info.writePermission = "android.permission.MANAGE_DOCUMENTS";
        provider.attachInfo(RuntimeEnvironment.getApplication(), info);
        File folder = temporary.newFolder("selected");
        File outside = temporary.newFile("private.txt");
        Uri tree = DocumentsContract.buildTreeDocumentUri(info.authority, folder.getPath());
        Uri escaped = DocumentsContract.buildDocumentUriUsingTree(tree,
            new File(folder, "../private.txt").getPath());
        try {
            provider.openFile(escaped, "r");
            fail("A folder grant must not open an outside file");
        } catch (SecurityException expected) {
            assertTrue(outside.exists());
        }
    }

    @Test public void creatingDocumentsAcceptsOnlyNamesAndKeepsExistingFiles() throws Exception {
        TermuxDocumentsProvider provider = new TermuxDocumentsProvider();
        File folder = temporary.newFolder("documents");
        for (String name : new String[]{null, "", ".", "..", "../outside", "/absolute", "sub/file", "sub\\file", "bad\0name"}) {
            try {
                provider.createDocument(folder.getPath(), "text/plain", name);
                fail("Accepted invalid name: " + name);
            } catch (FileNotFoundException expected) {
                assertEquals(0, folder.list().length);
            }
        }
        String first = provider.createDocument(folder.getPath(), "text/plain", "notes.txt");
        String second = provider.createDocument(folder.getPath(), "text/plain", "notes.txt");
        assertNotEquals(first, second);
        assertTrue(new File(first).isFile());
        assertTrue(new File(second).isFile());
        assertTrue(new File(provider.createDocument(folder.getPath(), DocumentsContract.Document.MIME_TYPE_DIR, "folder")).isDirectory());
    }

    @Test(expected = FileNotFoundException.class)
    public void listingAFileFailsAsAFileError() throws Exception {
        new TermuxDocumentsProvider().queryChildDocuments(temporary.newFile("not-a-directory").getPath(), null, (String) null);
    }
}
