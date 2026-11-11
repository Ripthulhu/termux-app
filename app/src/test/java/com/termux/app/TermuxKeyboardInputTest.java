package com.termux.app;

import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.TextAttribute;
import android.view.KeyEvent;

import androidx.preference.SwitchPreferenceCompat;

import com.termux.R;
import com.termux.app.activities.SettingsActivity;
import com.termux.app.fragments.settings.termux.TerminalIOPreferencesFragment;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.termux.terminal.TermuxTerminalViewClientBase;
import com.termux.shared.termux.terminal.TermuxTerminalSessionClientBase;
import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalSession;
import com.termux.view.TerminalView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class TermuxKeyboardInputTest {
    private TerminalSession prepareTerminal(TerminalView view) {
        TerminalSession session = new TerminalSession("/system/bin/sh", "/", new String[0], new String[0], 100,
            new TermuxTerminalSessionClientBase());
        ReflectionHelpers.setField(session, "mShellPid", 1);
        view.mTermSession = session;
        view.mEmulator = new TerminalEmulator(session, 80, 24, 8, 16, 100, null);
        ReflectionHelpers.setField(session, "mEmulator", view.mEmulator);
        return session;
    }

    private boolean replaceText(InputConnection connection, int start, int end, String text) {
        return ReflectionHelpers.callInstanceMethod(connection, "replaceText",
            ClassParameter.from(int.class, start), ClassParameter.from(int.class, end),
            ClassParameter.from(CharSequence.class, text), ClassParameter.from(int.class, 1),
            ClassParameter.from(TextAttribute.class, null));
    }

    private String readQueuedText(TerminalSession session) {
        byte[] received = new byte[256];
        Object queue = ReflectionHelpers.getField(session, "mTerminalToProcessIOQueue");
        int count = ReflectionHelpers.callInstanceMethod(queue, "read",
            ClassParameter.from(byte[].class, received), ClassParameter.from(boolean.class, false));
        return new String(received, 0, count, StandardCharsets.UTF_8);
    }

    @Test public void textInputIsOptInAndKeepsEnterAndFullscreenBehavior() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        assertFalse(activity.getPreferences().isSoftKeyboardTextInputEnabled());
        EditorInfo info = new EditorInfo();
        activity.getTerminalView().onCreateInputConnection(info);
        assertEquals(InputType.TYPE_NULL, info.inputType);

        activity.getPreferences().setSoftKeyboardTextInputEnabled(true);
        assertTrue(TermuxAppSharedPreferences.build(activity).isSoftKeyboardTextInputEnabled());
        activity.getTerminalView().onCreateInputConnection(info);
        assertEquals(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT |
            InputType.TYPE_TEXT_FLAG_MULTI_LINE, info.inputType);
        assertEquals(0, info.initialSelStart);
        assertEquals(0, info.initialSelEnd);
        assertEquals(EditorInfo.IME_FLAG_NO_FULLSCREEN, info.imeOptions);

        activity.getPreferences().setSoftKeyboardTextInputEnabled(false);
        activity.getTerminalView().onCreateInputConnection(info);
        assertEquals(InputType.TYPE_NULL, info.inputType);
    }

    @Test public void textInputOverridesTheLegacyCharacterModeOnlyWhenEnabled() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        TerminalView view = activity.getTerminalView();
        view.setTerminalViewClient(new TermuxTerminalViewClientBase() {
            @Override public boolean shouldEnforceCharBasedInput() { return true; }
            @Override public boolean shouldUseTextInput() {
                return activity.getPreferences().isSoftKeyboardTextInputEnabled();
            }
        });
        EditorInfo info = new EditorInfo();
        view.onCreateInputConnection(info);
        int legacyType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        assertEquals(legacyType, info.inputType);
        activity.getPreferences().setSoftKeyboardTextInputEnabled(true);
        view.onCreateInputConnection(info);
        assertEquals(InputType.TYPE_CLASS_TEXT, info.inputType & InputType.TYPE_MASK_CLASS);
        assertEquals(0, info.inputType & InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        activity.getPreferences().setSoftKeyboardTextInputEnabled(false);
        view.onCreateInputConnection(info);
        assertEquals(legacyType, info.inputType);
    }

    @Test public void settingsSwitchReadsAndWritesTheTerminalPreference() {
        SettingsActivity activity = Robolectric.buildActivity(SettingsActivity.class).create().get();
        TerminalIOPreferencesFragment fragment = new TerminalIOPreferencesFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.settings, fragment).commitNow();
        SwitchPreferenceCompat preference = fragment.findPreference("soft_keyboard_text_input");
        assertNotNull(preference);
        assertFalse(preference.isChecked());
        preference.setChecked(true);
        assertTrue(TermuxAppSharedPreferences.build(activity).isSoftKeyboardTextInputEnabled());
        preference.setChecked(false);
        assertFalse(TermuxAppSharedPreferences.build(activity).isSoftKeyboardTextInputEnabled());
    }

    @Test public void returningFromSettingsRefreshesTheKeyboardMode() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        activity.getPreferences().setSoftKeyboardTextInputEnabled(true);
        activity.mTermuxTerminalViewClient.onResume();
        assertTrue(ReflectionHelpers.<Boolean>getField(activity.mTermuxTerminalViewClient, "mSoftKeyboardTextInputEnabled"));
        activity.getPreferences().setSoftKeyboardTextInputEnabled(false);
        activity.mTermuxTerminalViewClient.onResume();
        assertFalse(ReflectionHelpers.<Boolean>getField(activity.mTermuxTerminalViewClient, "mSoftKeyboardTextInputEnabled"));
    }

    @Test public void autocorrectAndVoiceCompositionCommitOnlyTheAcceptedText() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        activity.getPreferences().setSoftKeyboardTextInputEnabled(true);
        TerminalView view = activity.getTerminalView();
        TerminalSession session = prepareTerminal(view);
        InputConnection connection = view.onCreateInputConnection(new EditorInfo());
        connection.setComposingText("teh", 1);
        connection.setComposingText("the", 1);
        assertEquals("", readQueuedText(session));
        connection.commitText("the ", 1);
        connection.finishComposingText();
        assertEquals("the ", readQueuedText(session));

        connection.setComposingText("write", 1);
        connection.setComposingText("write a test", 1);
        connection.commitText("write a test 😀", 1);
        connection.finishComposingText();
        assertEquals("write a test 😀", readQueuedText(session));
        assertEquals("", readQueuedText(session));

        connection.setComposingText("done", 1);
        connection.finishComposingText();
        connection.commitText("\n", 1);
        assertEquals("done\r", readQueuedText(session));
    }

    @Test @Config(sdk = 33) public void modernGboardCorrectionsReplaceTheTypedSuffixInOrder() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        activity.getPreferences().setSoftKeyboardTextInputEnabled(true);
        TerminalView view = activity.getTerminalView();
        TerminalSession session = prepareTerminal(view);
        InputConnection connection = view.onCreateInputConnection(new EditorInfo());
        for (String letter : new String[]{"t", "e", "h"}) connection.commitText(letter, 1);
        assertEquals("teh", readQueuedText(session));
        assertTrue(replaceText(connection, 1, 3, "he "));
        assertEquals("\u007f\u007fhe ", readQueuedText(session));
        assertEquals("the ", connection.getTextBeforeCursor(20, 0).toString());
        assertFalse("An earlier range must not edit unrelated terminal text", replaceText(connection, 0, 2, "no"));
        assertEquals("", readQueuedText(session));

        connection.commitText("😀", 1);
        readQueuedText(session);
        connection.deleteSurroundingText(1, 0);
        assertEquals("\u007f", readQueuedText(session));
        assertEquals("the ", connection.getTextBeforeCursor(20, 0).toString());
        view.onKeyDown(KeyEvent.KEYCODE_ENTER, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        assertEquals("", connection.getTextBeforeCursor(20, 0).toString());
        assertFalse("A stale correction cannot reach the next prompt", replaceText(connection, 1, 3, "no"));
        connection.commitText("text", 1);
        view.inputCodePoint(TerminalView.KEY_EVENT_SOURCE_VIRTUAL_KEYBOARD, '/', false, false);
        assertEquals("", connection.getTextBeforeCursor(20, 0).toString());
        readQueuedText(session);
        int[] controlReads = {0};
        view.setTerminalViewClient(new TermuxTerminalViewClientBase() {
            @Override public boolean shouldUseTextInput() { return true; }
            @Override public boolean readControlKey() { return controlReads[0]++ == 0; }
        });
        connection.commitText("c", 1);
        assertEquals("\u0003", readQueuedText(session));
        assertEquals(1, controlReads[0]);
        assertEquals("", connection.getTextBeforeCursor(20, 0).toString());
    }
}
