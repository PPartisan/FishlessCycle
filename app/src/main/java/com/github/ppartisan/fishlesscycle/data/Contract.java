package com.github.ppartisan.fishlesscycle.data;

import android.provider.BaseColumns;

public final class Contract {

    public static final class TankEntry implements BaseColumns {

        public static final String TABLE_NAME = "tanks";

        public static final String COLUMN_NAME = TABLE_NAME + "_name";
        public static final String COLUMN_VOLUME = TABLE_NAME + "_vol_in_litres";
        public static final String COLUMN_DOSAGE = TABLE_NAME + "_ammonia_dose";
        public static final String COLUMN_CONCENTRATION = TABLE_NAME + "_target_concentration";
        public static final String COLUMN_IS_HEATED = TABLE_NAME + "_is_heated";
        public static final String COLUMN_IS_SEEDED = TABLE_NAME + "_is_seeded";
        public static final String COLUMN_PLANT_STATUS = TABLE_NAME + "_plant_status";
        public static final String COLUMN_IMAGE = TABLE_NAME + "_image";
        public static final String COLUMN_IDENTIFIER = TABLE_NAME + "_identifier";

    }

    public static final class ReadingEntry implements BaseColumns {

        public static final String TABLE_NAME = "readings";

        public static final String COLUMN_IDENTIFIER = TABLE_NAME + "_identifier";
        public static final String COLUMN_DATE = TABLE_NAME + "_date";
        public static final String COLUMN_AMMONIA = TABLE_NAME + "_ammonia";
        public static final String COLUMN_NITRITE = TABLE_NAME + "_nitrite";
        public static final String COLUMN_NITRATE = TABLE_NAME + "_nitrate";
        public static final String COLUMN_NOTES = TABLE_NAME + "_notes";

    }

}
