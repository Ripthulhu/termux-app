package com.termux.app;

import android.view.View;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;

import com.termux.R;
import com.google.android.material.button.MaterialButton;
import com.termux.shared.termux.extrakeys.ExtraKeysView;

import com.termux.shared.termux.extrakeys.ExtraKeyButton;
import com.termux.shared.termux.extrakeys.ExtraKeysConstants;
import com.termux.shared.termux.extrakeys.ExtraKeysInfo;
import com.termux.shared.termux.settings.properties.TermuxPropertyConstants;
import com.termux.shared.termux.terminal.io.TerminalExtraKeys;
import com.termux.view.TerminalView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class TermuxExtraKeysTest {
    @Test public void keyboardShortcutHasAnIconAndTogglesTheImeWithoutHidingExtraKeys() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        HorizontalScrollView scroll = (HorizontalScrollView) activity.getLayoutInflater()
            .inflate(R.layout.view_terminal_toolbar_extra_keys, null);
        ExtraKeysView keys = scroll.findViewById(R.id.terminal_toolbar_extra_keys);
        keys.setExtraKeysViewClient(activity.getTermuxTerminalExtraKeys());
        keys.reload(activity.getTermuxTerminalExtraKeys().getExtraKeysInfo(), 48);
        MaterialButton keyboard = (MaterialButton) keys.getChildAt(6);
        assertTrue(keyboard.getText().length() == 0);
        assertTrue(keyboard.getIcon() != null);
        assertEquals(activity.getString(com.termux.shared.R.string.extra_keys_toggle_keyboard), keyboard.getContentDescription());
        InputMethodManager ime = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.performClick();
        assertTrue(Shadows.shadowOf(ime).isSoftInputVisible());
        keyboard.performClick();
        assertFalse(Shadows.shadowOf(ime).isSoftInputVisible());
        assertEquals(View.VISIBLE, keys.getVisibility());
    }


    @Test
    public void defaultKeysIncludePromptControlsAndSendShiftTabAsAKeyChord() throws Exception {
        ExtraKeysInfo info = new ExtraKeysInfo(TermuxPropertyConstants.DEFAULT_IVALUE_EXTRA_KEYS,
            TermuxPropertyConstants.DEFAULT_IVALUE_EXTRA_KEYS_STYLE, ExtraKeysConstants.CONTROL_CHARS_ALIASES);
        Map<String, ExtraKeyButton> keys = new HashMap<>();
        for (ExtraKeyButton[] row : info.getMatrix()) {
            assertEquals(7, row.length);
            for (ExtraKeyButton key : row) keys.put(key.getKey(), key);
        }
        for (String key : new String[]{"ESC", "TAB", "SHIFT TAB", "LEFT", "UP", "DOWN", "RIGHT", "ENTER", "KEYBOARD"})
            assertTrue(key, keys.containsKey(key));
        assertFalse(keys.containsKey("-"));
        assertEquals("PGUP", keys.get("HOME").getPopup().getKey());
        assertEquals("PGDN", keys.get("END").getPopup().getKey());

        String[] received = new String[1];
        boolean[] shifted = new boolean[1];
        TerminalExtraKeys extraKeys = new TerminalExtraKeys(new TerminalView(RuntimeEnvironment.getApplication(), null)) {
            @Override
            protected void onTerminalExtraKeyButtonClick(View view, String key, boolean ctrlDown,
                                                        boolean altDown, boolean shiftDown, boolean fnDown) {
                received[0] = key;
                shifted[0] = shiftDown;
            }
        };
        extraKeys.onExtraKeyButtonClick(null, keys.get("SHIFT TAB"), null);
        assertEquals("TAB", received[0]);
        assertTrue(shifted[0]);
        extraKeys.onExtraKeyButtonClick(null, keys.get("ENTER"), null);
        assertEquals("ENTER", received[0]);
        assertFalse(shifted[0]);
    }
}
