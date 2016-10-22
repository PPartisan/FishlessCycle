package com.github.ppartisan.fishlesscycle.util;

import android.content.res.Resources;
import android.support.annotation.IntDef;

import com.github.ppartisan.fishlesscycle.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ConversionUtils {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MGL, PPM})
    public @interface UnitType {}
    public static final int MGL = 0;
    public static final int PPM = 1;

    private ConversionUtils() { throw new AssertionError(); }

    public static String getUnitFormattedString(Resources res, float value, @UnitType int type) {

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
                        + UnitType.class.getCanonicalName());
        }

        return res.getString(R.string.quantity_template, value, unit);

    }

}
