package com.github.ppartisan.fishlesscycle.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.github.ppartisan.fishlesscycle.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PreferenceUtils {

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

}
