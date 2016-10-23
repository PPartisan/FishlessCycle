package com.github.ppartisan.fishlesscycle.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class DataUtils {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM", Locale.getDefault());

    private DataUtils() { throw new AssertionError(); }

    public static String getReadableDateString(long date) {
        return DATE_FORMAT.format(date);
    }

    public static String toOneDecimalPlace(double input) {
        return String.valueOf(String.format(Locale.getDefault(), "%.1f", input));
    }

}
