package com.github.ppartisan.fishlesscycle.util;

import android.content.res.Resources;
import android.support.annotation.IntDef;

import com.github.ppartisan.fishlesscycle.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ConversionUtils {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MGL, PPM})
    public @interface DosageUnit {}
    public static final int MGL = 0;
    public static final int PPM = 1;

    private ConversionUtils() { throw new AssertionError(); }

    public static String getUnitFormattedString(Resources res, float value, @DosageUnit int type) {

        String unit;

        switch (type) {
            case MGL:
                unit = res.getString(R.string.unit_metric);
                break;
            case PPM:
                unit = res.getString(R.string.unit_imperial);
                break;
            default:
                throw new IllegalArgumentException("'type' parameter must be of type "
                        + DosageUnit.class.getCanonicalName());
        }

        return res.getString(R.string.quantity_template, value, unit);

    }

    public static float getCubicInchesAsImperialGallon(float cubicInches) {
        return cubicInches*0.0036047f;
    }

    public static float getCubicInchesAsUsGallon(float cubicInches) {
        return cubicInches*0.004329f;
    }

    public static float getMlAsLitres(float ml) {
        return ml/1000;
    }

    public static float getLitresAsImperialGallons(float litres) {
        return litres*0.219969f;
    }

    public static float getLitresAsUsGallons(float litres) {
        return litres*0.264172f;
    }

    public static float getImperialGallonsAsLitres(float imperialGallons) {
        return imperialGallons*4.54609f;
    }

    public static float getUsGallonsAsLitres(float usGallons) {
        return usGallons*3.78541f;
    }

    public static float getCubicInchesAsLitres(float cubicInches) {
        return cubicInches*0.0163871f;
    }

    public static float getAmmoniaDosage(float tankVolumeInLitres, float targetDose, float percentSolution) {
        return (float)((tankVolumeInLitres*1000)*(targetDose/1e+6)*(100/percentSolution));
    }

}
