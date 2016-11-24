package com.github.ppartisan.fishlesscycle.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry;
import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry.Categories;
import com.github.ppartisan.fishlesscycle.data.Contract.TankEntry;
import com.github.ppartisan.fishlesscycle.data.Contract.ReadingEntry;

final class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fishlesscycle.db";
    private static final int SCHEMA = 2;

    private static DatabaseHelper sInstance = null;

    static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TANK_TABLE = "CREATE TABLE " + TankEntry.TABLE_NAME + " (" +
                TankEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TankEntry.COLUMN_NAME + " TEXT, " +
                TankEntry.COLUMN_IMAGE + " TEXT, " +
                TankEntry.COLUMN_VOLUME + " REAL, " +
                TankEntry.COLUMN_DOSAGE + " REAL, " +
                TankEntry.COLUMN_CONCENTRATION + " REAL, " +
                TankEntry.COLUMN_IS_HEATED + " INTEGER, " +
                TankEntry.COLUMN_IS_SEEDED + " INTEGER, " +
                TankEntry.COLUMN_PLANT_STATUS + " INTEGER" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TANK_TABLE);

        final String SQL_CREATE_READINGS_TABLE = "CREATE TABLE " + ReadingEntry.TABLE_NAME + " (" +
                ReadingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReadingEntry.COLUMN_IDENTIFIER + " INTEGER NOT NULL, " +
                ReadingEntry.COLUMN_DATE + " INTEGER UNIQUE, " +
                ReadingEntry.COLUMN_AMMONIA + " INTEGER, " +
                ReadingEntry.COLUMN_NITRITE + " INTEGER, " +
                ReadingEntry.COLUMN_NITRATE + " INTEGER, " +
                ReadingEntry.COLUMN_NOTES + " TEXT, " +
                ReadingEntry.COLUMN_IS_CONTROL + " INTEGER" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_READINGS_TABLE);

        //Added in v.2
        createAndPopulateApiColorChartTable(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i < 2) {
            logDatabaseUpgrade(i,i1);
            createAndPopulateApiColorChartTable(sqLiteDatabase);
        }
    }

    private void createAndPopulateApiColorChartTable(SQLiteDatabase db) {

        //As colors are hex values, store them as String and use Color.parseColor() to decode;
        db.beginTransaction();

        try {
            final String SQL_CREATE_API_COLOR_CHART_TABLE =
                    "CREATE TABLE " + ApiColorChartEntry.TABLE_NAME + " (" +
                            ApiColorChartEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            ApiColorChartEntry.COLUMN_CATEGORY + " INTEGER NOT NULL, " +
                            ApiColorChartEntry.COLUMN_COLOR + " TEXT NOT NULL, " +
                            ApiColorChartEntry.COLUMN_VALUE + " REAL NOT NULL " +
                            ");";

            db.execSQL(SQL_CREATE_API_COLOR_CHART_TABLE);

            final ContentValues cv = new ContentValues();
            @Categories int category;

            category = ApiColorChartEntry.AMMONIA;

            insertRowIntoApiColorChart(db, cv, category, "#f3ee11", 0f);
            insertRowIntoApiColorChart(db, cv, category, "#dfea10", 0.25f);
            insertRowIntoApiColorChart(db, cv, category, "#d0e71d", 0.5f);
            insertRowIntoApiColorChart(db, cv, category, "#b8d344", 1f);
            insertRowIntoApiColorChart(db, cv, category, "#66b30f", 2f);
            insertRowIntoApiColorChart(db, cv, category, "#12971f", 4f);
            insertRowIntoApiColorChart(db, cv, category, "#0e4913", 8f);

            category = ApiColorChartEntry.NITRITE;

            insertRowIntoApiColorChart(db, cv, category, "#8aa7d1", 0f);
            insertRowIntoApiColorChart(db, cv, category, "#b9cafe", 0.25f);
            insertRowIntoApiColorChart(db, cv, category, "#a593e1", 0.5f);
            insertRowIntoApiColorChart(db, cv, category, "#8e4594", 1f);
            insertRowIntoApiColorChart(db, cv, category, "#8c1e75", 2f);
            insertRowIntoApiColorChart(db, cv, category, "#650751", 5f);

            category = ApiColorChartEntry.NITRATE;

            insertRowIntoApiColorChart(db, cv, category, "#fefe0a", 0f);
            insertRowIntoApiColorChart(db, cv, category, "#f0d611", 5f);
            insertRowIntoApiColorChart(db, cv, category, "#ecab29", 10f);
            insertRowIntoApiColorChart(db, cv, category, "#dd8e36", 20f);
            insertRowIntoApiColorChart(db, cv, category, "#dd3417", 40f);
            insertRowIntoApiColorChart(db, cv, category, "#c42120", 80f);
            insertRowIntoApiColorChart(db, cv, category, "#940717", 160f);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

    }

    private void insertRowIntoApiColorChart(
            SQLiteDatabase db, ContentValues cv, @Categories int category, String color, float value
    ) {
        cv.put(ApiColorChartEntry.COLUMN_CATEGORY, category);
        cv.put(ApiColorChartEntry.COLUMN_COLOR, color);
        cv.put(ApiColorChartEntry.COLUMN_VALUE, value);
        db.insert(ApiColorChartEntry.TABLE_NAME, null, cv);
    }

    private static void logDatabaseUpgrade(int oldVer, int newVer) {
        Log.i(DatabaseHelper.class.getSimpleName(),
                "Upgrading database from v." + oldVer + " to v." + newVer + "...");
    }

}
