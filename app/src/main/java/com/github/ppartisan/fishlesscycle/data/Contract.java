package com.github.ppartisan.fishlesscycle.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.IntDef;

import com.github.ppartisan.fishlesscycle.BuildConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Contract {

    private Contract() { throw new AssertionError(); }

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".data.Provider";
    public static final String FREE_AUTHORITY = "com.github.ppartisan.fishlesscycle.free.data.Provider";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final Uri BASE_FREE_CONTENT_URI = Uri.parse("content://" + FREE_AUTHORITY);

    static final String PATH_TANKS = "tanks";
    static final String PATH_READINGS = "readings";
    static final String PATH_API_COLOR_CHART = "apicolors";

    public static final class TankEntry implements BaseColumns {

        static final String TABLE_NAME = "tanks";

        public static final String COLUMN_NAME = TABLE_NAME + "_name";
        public static final String COLUMN_VOLUME = TABLE_NAME + "_vol_in_litres";
        public static final String COLUMN_DOSAGE = TABLE_NAME + "_ammonia_dose";
        public static final String COLUMN_CONCENTRATION = TABLE_NAME + "_target_concentration";
        public static final String COLUMN_IS_HEATED = TABLE_NAME + "_is_heated";
        public static final String COLUMN_IS_SEEDED = TABLE_NAME + "_is_seeded";
        public static final String COLUMN_PLANT_STATUS = TABLE_NAME + "_plant_status";
        public static final String COLUMN_IMAGE = TABLE_NAME + "_image";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_TANKS).build();

        public static final Uri FREE_URI =
                BASE_FREE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_TANKS).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TANKS;

        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TANKS;

        public static long getTankId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        static Uri buildTankUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class ReadingEntry implements BaseColumns {

        static final String TABLE_NAME = "readings";

        public static final String COLUMN_IDENTIFIER = TABLE_NAME + "_identifier";
        public static final String COLUMN_DATE = TABLE_NAME + "_date";
        public static final String COLUMN_AMMONIA = TABLE_NAME + "_ammonia";
        public static final String COLUMN_NITRITE = TABLE_NAME + "_nitrite";
        public static final String COLUMN_NITRATE = TABLE_NAME + "_nitrate";
        public static final String COLUMN_NOTES = TABLE_NAME + "_notes";
        public static final String COLUMN_IS_CONTROL = TABLE_NAME + "_is_control";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_READINGS).build();
        public static final Uri FREE_URI =
                BASE_FREE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_READINGS).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_READINGS;

        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_READINGS;

        static long getReadingId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Uri buildReadingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class ApiColorChartEntry implements BaseColumns {

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({AMMONIA,NITRITE,NITRATE})
        public @interface Categories{}
        public static final int AMMONIA = 10;
        public static final int NITRITE = 20;
        public static final int NITRATE = 30;

        static final String TABLE_NAME = "apichart";

        public static final String COLUMN_CATEGORY = TABLE_NAME + "_category";
        public static final String COLUMN_COLOR = TABLE_NAME + "_color";
        public static final String COLUMN_VALUE = TABLE_NAME + "_label";

        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_API_COLOR_CHART).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_API_COLOR_CHART;

        public static Uri buildApiColorChartUri(@Categories int category) {
            return ContentUris.withAppendedId(CONTENT_URI, category);
        }

        static @Categories int getApiColorChatCategory(Uri uri) {
            final @Categories int category = Integer.parseInt(uri.getPathSegments().get(1));
            return category;
        }

    }

}
