package com.github.ppartisan.fishlesscycle.strategy.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.github.ppartisan.fishlesscycle.R;

public final class SyncUtil {

    private static final String FREE_APP_PACKAGE_NAME = "com.github.ppartisan.fishlesscycle.free";

    private SyncUtil() { throw new AssertionError(); }

    public static boolean isFreeVersionInstalled(PackageManager pm) {
        try {
            pm.getPackageInfo(
                    FREE_APP_PACKAGE_NAME,
                    PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isSyncNoticeAlreadyDisplayed(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_sync_key), false);
    }

    public static void setSyncNoticeAlreadyDisplayed(Context context, boolean isAlreadyDisplayed) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(context.getString(R.string.pref_sync_key), isAlreadyDisplayed)
                .apply();
    }

}
