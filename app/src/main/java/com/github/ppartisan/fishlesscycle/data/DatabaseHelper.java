package com.github.ppartisan.fishlesscycle.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.ppartisan.fishlesscycle.data.Contract.TankEntry;
import com.github.ppartisan.fishlesscycle.data.Contract.ReadingEntry;

final class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fishlesscycle.db";
    private static final int SCHEMA = 1;

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

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //todo
    }

}
