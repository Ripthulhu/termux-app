package com.termux.app;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.view.View;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.termux.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class TermuxPictureInPictureTest {

    @Test
    public void keepingTerminalVisibleIsOptInAndPersists() {
        shadowOf(RuntimeEnvironment.getApplication().getPackageManager())
            .setSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE, true);
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        MaterialCheckBox keepVisible = activity.findViewById(R.id.keep_terminal_visible_checkbox);
        assertEquals(View.VISIBLE, keepVisible.getVisibility());
        assertFalse(keepVisible.isChecked());
        keepVisible.performClick();
        assertTrue(activity.getPreferences().shouldKeepTerminalVisible());
        TermuxActivity recreated = Robolectric.buildActivity(TermuxActivity.class).create().get();
        assertTrue(((MaterialCheckBox) recreated.findViewById(R.id.keep_terminal_visible_checkbox)).isChecked());
    }

    @Test
    public void previewHidesControlsAndRestoresTheUsersKeyboardStripPreference() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        activity.getPreferences().setShowTerminalToolbar(true);
        activity.onPictureInPictureModeChanged(true, new Configuration());
        assertEquals(View.GONE, activity.findViewById(R.id.terminal_navigation_bar).getVisibility());
        assertEquals(View.GONE, activity.getTerminalToolbarViewPager().getVisibility());
        assertTrue(activity.getPreferences().shouldShowTerminalToolbar());
        activity.onPictureInPictureModeChanged(false, new Configuration());
        assertEquals(View.VISIBLE, activity.findViewById(R.id.terminal_navigation_bar).getVisibility());
        assertEquals(View.VISIBLE, activity.getTerminalToolbarViewPager().getVisibility());

        activity.getPreferences().setShowTerminalToolbar(false);
        activity.onPictureInPictureModeChanged(true, new Configuration());
        activity.onPictureInPictureModeChanged(false, new Configuration());
        assertEquals(View.GONE, activity.getTerminalToolbarViewPager().getVisibility());
    }

    @Test
    @Config(sdk = 23)
    public void olderAndroidDoesNotOfferPictureInPicture() {
        TermuxActivity activity = Robolectric.buildActivity(TermuxActivity.class).create().get();
        assertEquals(View.GONE, activity.findViewById(R.id.keep_terminal_visible_checkbox).getVisibility());
        activity.onUserLeaveHint();
    }
}
