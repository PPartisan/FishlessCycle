package com.github.ppartisan.fishlesscycle.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.ImagePack;
import com.github.ppartisan.fishlesscycle.reminder.ReminderReceiver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Date;

public final class PreferenceUtils {

    public static final Calendar NO_TIME = buildNoTimeCalendar();
    private static final String NO_TIME_VALUE = "99:99";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({METRIC,IMPERIAL,US})
    public @interface VolumeUnit {}
    public static final int METRIC = 200;
    public static final int IMPERIAL = 201;
    public static final int US = 202;

    private PreferenceUtils() { throw new AssertionError(); }

    public static @PreferenceUtils.VolumeUnit int getVolumeUnit(Context context) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String[] unitOptions =
                context.getResources().getStringArray(R.array.pref_volume_unit_values);
        final String unitString =
                prefs.getString(context.getString(R.string.pref_volume_unit_type_key), unitOptions[0]);

        @VolumeUnit int unit = METRIC;

        if (unitString.equals(unitOptions[0])) {
            unit = METRIC;
        } else if (unitString.equals(unitOptions[1])) {
            unit = IMPERIAL;
        } else if(unitString.equals(unitOptions[2])) {
            unit = US;
        }

        return unit;

    }

    public static @ConversionUtils.DosageUnit
    int getDosageUnitType(Context context) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String[] unitOptions =
                context.getResources().getStringArray(R.array.pref_dosage_unit_values);
        final String unitString =
                prefs.getString(context.getString(R.string.pref_dosage_unit_type_key), unitOptions[0]);

        return (unitString.equals(unitOptions[0])) ? ConversionUtils.MGL : ConversionUtils.PPM;

    }

    public static boolean isDosageMetric(Context context) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String[] unitOptions =
                context.getResources().getStringArray(R.array.pref_dosage_unit_values);
        final String unitString =
                prefs.getString(context.getString(R.string.pref_dosage_unit_type_key), unitOptions[0]);

        return unitString.equals(unitOptions[0]);

    }

    public static Calendar getReminderTime(Context context) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String time =
                prefs.getString(context.getString(R.string.pref_reminder_time_key), NO_TIME_VALUE);

        if(time.equals(NO_TIME_VALUE)) {
            return NO_TIME;
        }

        final String [] times = time.split(":");
        final int hour = Integer.parseInt(times[0]);
        final int mins = Integer.parseInt(times[1]);

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, mins);

        //If we've missed the current time, set it for next interval
        if(c.getTimeInMillis() < System.currentTimeMillis()) {
            final int interval = getReminderIntervalInDays(context);
            c.add(Calendar.DAY_OF_MONTH, interval);
        }

        return c;

    }

    public static void clearReminderTime(Context context) {
        final String key = context.getString(R.string.pref_reminder_time_key);
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(key, NO_TIME_VALUE).apply();
    }

    public static int getReminderIntervalInDays(Context context) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String interval =
                prefs.getString(
                        context.getString(R.string.pref_reminder_interval_key),
                        String.valueOf(2)
                );
        return Integer.parseInt(interval);

    }

    @Nullable
    public static String getWidgetImagePath(Context context) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String key = context.getString(R.string.pref_widget_image_path_key);
        return prefs.getString(key, null);

    }

    public static void setWidgetImagePath(Context context, String path) {
        final String key = context.getString(R.string.pref_widget_image_path_key);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, path).apply();
    }

    private static Calendar buildNoTimeCalendar() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.MIN_VALUE);
        return c;
    }

    public static ImagePack buildImagePack(Cursor cursor) {

        if(cursor == null || cursor.getCount() == 0) {
            return new ImagePack(new CharSequence[0], new CharSequence[0]);
        }

        final int count = cursor.getCount();
        final CharSequence[] titles = new CharSequence[count];
        final CharSequence[] paths = new CharSequence[count];

        int counter = 0;
        cursor.moveToFirst();

        do {
            String title = cursor.getString(cursor.getColumnIndex(Contract.TankEntry.COLUMN_NAME));
            String path = cursor.getString(cursor.getColumnIndex(Contract.TankEntry.COLUMN_IMAGE));
            //PreferenceFragment can't handle null values
            titles[counter] = (title == null) ? "" : title;
            paths[counter] = (path == null) ? "" : path;
            counter++;
        } while (cursor.moveToNext());

        return new ImagePack(titles, paths);

    }


}
