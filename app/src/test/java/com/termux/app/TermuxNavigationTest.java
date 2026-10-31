package com.termux.app;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.termux.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28, 30, 33})
public class TermuxNavigationTest {

    @Test
    public void sessionsButtonOpensDrawerWithoutEnablingEdgeSwipe() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        DrawerLayout drawer = activity.getDrawer();
        activity.findViewById(R.id.sessions_button).performClick();
        assertTrue(drawer.isDrawerVisible(GravityCompat.START));
        activity.mTermuxTerminalViewClient.copyModeChanged(true);
        assertFalse(activity.findViewById(R.id.sessions_button).isEnabled());
        activity.mTermuxTerminalViewClient.copyModeChanged(false);
        assertTrue(activity.findViewById(R.id.sessions_button).isEnabled());
        assertEquals(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawer.getDrawerLockMode(GravityCompat.START));

        activity.findViewById(R.id.close_sessions_button).performClick();
        assertFalse(drawer.isDrawerOpen(GravityCompat.START));

        drawer.measure(View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY));
        drawer.layout(0, 0, 1080, 1920);
        drawer.openDrawer(GravityCompat.START, false);
        assertEquals(DrawerLayout.LOCK_MODE_UNLOCKED, drawer.getDrawerLockMode(GravityCompat.START));
        drawer.closeDrawer(GravityCompat.START, false);
        assertEquals(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawer.getDrawerLockMode(GravityCompat.START));
    }

    @Test
    @Config(sdk = 30)
    public void keyboardInsetsReplaceNavigationInsetsAndResetAfterResize() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        View root = activity.getTermuxActivityRootView();
        WindowInsetsCompat.Builder insets = new WindowInsetsCompat.Builder()
            .setInsets(WindowInsetsCompat.Type.statusBars(), Insets.of(0, 24, 0, 0))
            .setInsets(WindowInsetsCompat.Type.captionBar(), Insets.of(0, 36, 0, 0))
            .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(0, 0, 0, 24))
            .setInsets(WindowInsetsCompat.Type.displayCutout(), Insets.of(48, 0, 0, 0));

        root.dispatchApplyWindowInsets(insets.setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, 300))
            .build().toWindowInsets());
        assertEquals(48, root.getPaddingLeft());
        assertEquals(36, root.getPaddingTop());
        assertEquals(300, root.getPaddingBottom());

        root.dispatchApplyWindowInsets(insets.setInsets(WindowInsetsCompat.Type.ime(), Insets.NONE)
            .setInsets(WindowInsetsCompat.Type.displayCutout(), Insets.NONE).build().toWindowInsets());
        assertEquals(0, root.getPaddingLeft());
        assertEquals(24, root.getPaddingBottom());
    }
}
