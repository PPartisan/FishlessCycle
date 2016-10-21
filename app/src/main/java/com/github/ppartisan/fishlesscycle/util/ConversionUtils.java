package com.github.ppartisan.fishlesscycle.util;

import android.content.res.Resources;
import android.support.annotation.IntDef;

import com.github.ppartisan.fishlesscycle.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ConversionUtils {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({METRIC,IMPERIAL})
    public @interface UnitType {}
    public static final int METRIC = 0;
    public static final int IMPERIAL = 1;

    private ConversionUtils() { throw new AssertionError(); }

    public static String getUnitFormattedString(Resources res, float value, @UnitType int type) {

        String unit = null;

        switch (type) {
            case METRIC:
                unit = res.getString(R.string.unit_metric);
                break;
            case IMPERIAL:
                unit = res.getString(R.string.unit_imperial);
                break;
            default:
                throw new IllegalArgumentException("'type' parameter must be of type "
                        + UnitType.class.getSimpleName());
        }

        return res.getString(R.string.quantity_template, value, unit);

    }

}
