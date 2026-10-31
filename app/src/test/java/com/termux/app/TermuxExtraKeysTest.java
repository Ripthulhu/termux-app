package com.termux.app;

import android.view.View;

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
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class TermuxExtraKeysTest {

    @Test
    public void defaultKeysIncludePromptControlsAndSendShiftTabAsAKeyChord() throws Exception {
        ExtraKeysInfo info = new ExtraKeysInfo(TermuxPropertyConstants.DEFAULT_IVALUE_EXTRA_KEYS,
            TermuxPropertyConstants.DEFAULT_IVALUE_EXTRA_KEYS_STYLE, ExtraKeysConstants.CONTROL_CHARS_ALIASES);
        Map<String, ExtraKeyButton> keys = new HashMap<>();
        for (ExtraKeyButton[] row : info.getMatrix()) {
            assertEquals(7, row.length);
            for (ExtraKeyButton key : row) keys.put(key.getKey(), key);
        }
        for (String key : new String[]{"ESC", "TAB", "SHIFT TAB", "LEFT", "UP", "DOWN", "RIGHT", "ENTER"})
            assertTrue(key, keys.containsKey(key));
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
