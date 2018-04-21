package com.game.sdk.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.game.sdk.configurator.MCSDK;

/**
 * Created
 */

public final class DimenUtil {

    public static int getScreenWidth() {
        final Resources resources = MCSDK.getApplicationContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        final Resources resources = MCSDK.getApplicationContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
}
