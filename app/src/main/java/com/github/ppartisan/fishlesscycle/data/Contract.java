package com.github.ppartisan.fishlesscycle.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.github.ppartisan.fishlesscycle.BuildConfig;

public final class Contract {

    private Contract() { throw new AssertionError(); }

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".data.Provider";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_TANKS = "tanks";
    static final String PATH_READINGS = "readings";

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

}
