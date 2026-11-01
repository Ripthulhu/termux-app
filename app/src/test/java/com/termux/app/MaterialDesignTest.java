package com.termux.app;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import androidx.core.graphics.ColorUtils;
import com.google.android.material.color.MaterialColors;
import com.termux.R;
import com.termux.shared.termux.extrakeys.ExtraKeysView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MaterialDesignTest {
    private void layout(View view, int widthDp) {
        float density = view.getResources().getDisplayMetrics().density;
        int width = Math.round(widthDp * density);
        int height = Math.round(800 * density);
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, width, height);
    }

    @Test public void sessionsAdaptWithoutReplacingControlsOrHidingTheTerminal() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().start().get();
        View root = activity.findViewById(R.id.activity_termux_root_relative_layout);
        View panel = activity.findViewById(R.id.left_drawer);
        View resume = activity.findViewById(R.id.resume_codex_button);
        layout(root, 1000);
        assertSame(root, panel.getParent());
        assertEquals(View.GONE, activity.findViewById(R.id.sessions_button).getVisibility());
        activity.closeSessionDrawer();
        assertSame(root, panel.getParent());
        assertSame(resume, panel.findViewById(R.id.resume_codex_button));
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_YES, activity.getTerminalView().getImportantForAccessibility());
        layout(root, 400);
        assertSame(activity.getDrawer(), panel.getParent());
        assertEquals(View.VISIBLE, activity.findViewById(R.id.sessions_button).getVisibility());
        layout(root, 400);
        activity.getDrawer().openDrawer(androidx.core.view.GravityCompat.START, false);
        activity.getOnBackPressedDispatcher().onBackPressed();
        assertFalse(activity.isFinishing());
    }

    @Test public void narrowWindowsScrollKeysWithoutShrinkingTheirTouchTargets() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        HorizontalScrollView scroll = (HorizontalScrollView) LayoutInflater.from(activity)
            .inflate(R.layout.view_terminal_toolbar_extra_keys, null);
        ExtraKeysView keys = scroll.findViewById(R.id.terminal_toolbar_extra_keys);
        keys.setButtonColors(android.graphics.Color.MAGENTA, android.graphics.Color.YELLOW,
            android.graphics.Color.BLACK, android.graphics.Color.DKGRAY);
        keys.reload(activity.getTermuxTerminalExtraKeys().getExtraKeysInfo(), 48);
        assertEquals("Typography preserves configured key colors", android.graphics.Color.MAGENTA,
            ((android.widget.TextView) keys.getChildAt(0)).getCurrentTextColor());
        layout(scroll, 240);
        float minimum = 48 * activity.getResources().getDisplayMetrics().density;
        assertTrue(keys.getWidth() > scroll.getWidth());
        for (int i = 0; i < keys.getChildCount(); i++)
            assertTrue("Key must stay touchable", keys.getChildAt(i).getWidth() >= minimum);
        layout(scroll, 411);
        assertEquals("Default keys fit a typical phone", scroll.getWidth(), keys.getWidth());
    }

    @Test public void sessionHeaderStacksItsActionsWhenThePanelIsNarrow() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        View panel = activity.findViewById(R.id.left_drawer);
        android.widget.LinearLayout header = panel.findViewById(R.id.session_panel_header);
        layout(panel, 164);
        assertEquals(android.widget.LinearLayout.VERTICAL, header.getOrientation());
        layout(panel, 320);
        assertEquals(android.widget.LinearLayout.HORIZONTAL, header.getOrientation());
    }

    @Test public void drawerFitsACompactWindowAsItResizes() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        android.view.ViewGroup root = activity.findViewById(R.id.activity_termux_root_relative_layout);
        View panel = activity.findViewById(R.id.left_drawer);
        int previousWidth = 0;
        for (int width : new int[]{228, 300}) {
            layout(root, width);
            int panelWidth = panel.getLayoutParams().width;
            assertTrue("Panel stays within window", panelWidth > 0 && panelWidth <= root.getWidth());
            assertTrue("Compact-to-compact resize updates width", panelWidth > previousWidth);
            previousWidth = panelWidth;
        }
    }

    @Test @Config(qualifiers = "notnight") public void lightThemeHasReadableColorPairs() { checkContrast(); }
    @Test @Config(qualifiers = "night") public void darkThemeHasReadableColorPairs() { checkContrast(); }

    @Test public void terminalKeepsItsPaletteBackgroundWhenTheAppSurfaceChanges() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        com.termux.terminal.TerminalSession session = new com.termux.terminal.TerminalSession(
            "/system/bin/sh", "/", new String[0], new String[0], 100, activity.getTermuxTerminalSessionClient());
        com.termux.terminal.TerminalEmulator emulator = new com.termux.terminal.TerminalEmulator(session, 20, 5, 13, 15, 100, activity.getTermuxTerminalSessionClient());
        org.robolectric.util.ReflectionHelpers.setField(session, "mEmulator", emulator);
        org.robolectric.util.ReflectionHelpers.setField(activity.getTerminalView(), "mTermSession", session);
        org.robolectric.util.ReflectionHelpers.setField(activity, "mIsVisible", true);
        activity.getTermuxTerminalSessionClient().onColorsChanged(session);
        assertEquals(emulator.mColors.mCurrentColors[com.termux.terminal.TextStyle.COLOR_INDEX_BACKGROUND],
            ((android.graphics.drawable.ColorDrawable) activity.getTerminalView().getBackground()).getColor());
        assertEquals(MaterialColors.getColor(activity.getTerminalView(), com.google.android.material.R.attr.colorSurface),
            ((android.graphics.drawable.ColorDrawable) activity.getWindow().getDecorView().getBackground()).getColor());
    }

    private void checkContrast() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        View bar = activity.findViewById(R.id.terminal_navigation_bar);
        assertTrue(ColorUtils.calculateContrast(MaterialColors.getColor(bar, com.google.android.material.R.attr.colorOnSurface),
            MaterialColors.getColor(bar, com.google.android.material.R.attr.colorSurface)) >= 4.5);
        assertTrue(ColorUtils.calculateContrast(MaterialColors.getColor(bar, com.google.android.material.R.attr.colorOnPrimary),
            MaterialColors.getColor(bar, androidx.appcompat.R.attr.colorPrimary)) >= 4.5);
        assertTrue(ColorUtils.calculateContrast(MaterialColors.getColor(bar, com.google.android.material.R.attr.colorOnSecondaryContainer),
            MaterialColors.getColor(bar, com.google.android.material.R.attr.colorSecondaryContainer)) >= 4.5);
    }
}
