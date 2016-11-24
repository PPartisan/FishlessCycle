package com.github.ppartisan.fishlesscycle.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry;
import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry.Categories;
import com.github.ppartisan.fishlesscycle.data.Contract.ReadingEntry;
import com.github.ppartisan.fishlesscycle.data.Contract.TankEntry;

public final class Provider extends ContentProvider {

    public static final String[] IMAGES_PROJECTION =
            { TankEntry.COLUMN_NAME, TankEntry.COLUMN_IMAGE };

    static final int TANKS = 100;
    static final int READINGS = 101;
    static final int API_COLORS = 102;
    static final int TANK = 500;
    static final int READING = 501;

    static final UriMatcher MATCHER = buildUriMatcher();

    private DatabaseHelper db = null;

    @Override
    public boolean onCreate() {
        db = DatabaseHelper.getInstance(getContext());
        return (db!=null);
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String s1) {

        Cursor cursor;

        switch (MATCHER.match(uri)) {
            case TANKS:
                cursor = getTanks(projection);
                break;
            case READING:
                final long identifier = ReadingEntry.getReadingId(uri);
                cursor = getReading(identifier);
                break;
            case API_COLORS:
                final @Categories int category =
                        ApiColorChartEntry.getApiColorChatCategory(uri);
                cursor = getApiColors(category);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;

    }

    @Override
    public String getType(@NonNull Uri uri) {

        Log.e(getClass().getSimpleName(), uri.toString());

        switch (MATCHER.match(uri)) {
            case TANKS:
                return TankEntry.CONTENT_TYPE;
            case TANK:
                return TankEntry.CONTENT_ITEM_TYPE;
            case READINGS:
                return ReadingEntry.CONTENT_TYPE;
            case READING:
                return ReadingEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues cv) {

        Uri result;

        switch (MATCHER.match(uri)) {
            case TANKS:
                result = insertTank(cv);
                break;
            case READINGS:
                result = insertReading(cv);
                if (getContext() != null) {
                    getContext().getContentResolver().notifyChange(TankEntry.CONTENT_URI, null);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (result == null) throw new SQLException("Failed to insert row into " + uri);

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs) {

        String table;

        switch (MATCHER.match(uri)) {
            case TANKS:
                table = TankEntry.TABLE_NAME;
                break;
            case READINGS:
                table = ReadingEntry.TABLE_NAME;
                if (getContext() != null) {
                    getContext().getContentResolver().notifyChange(TankEntry.CONTENT_URI, null);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        final int rowsDeleted = db.getWritableDatabase().delete(table, where, whereArgs);

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues cv, String where, String[] whereArgs) {

        String table;

        switch (MATCHER.match(uri)) {
            case TANKS:
                table = TankEntry.TABLE_NAME;
                break;
            case READINGS:
                table = ReadingEntry.TABLE_NAME;
                if (getContext() != null) {
                    getContext().getContentResolver().notifyChange(TankEntry.CONTENT_URI, null);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        final int rowsUpdated = db.getWritableDatabase().update(table, cv, where, whereArgs);

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_TANKS, TANKS);
        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_READINGS, READINGS);
        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_API_COLOR_CHART+"/#", API_COLORS);
        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_TANKS+"/#", TANK);
        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_READINGS+"/#", READING);

        return matcher;

    }

    private Cursor getTanks() {
        return db.getReadableDatabase().rawQuery(TANK_QUERY, null);
    }

    private Cursor getTanks(String[] projection) {
        if (projection == null) {
            return getTanks();
        }
        return db.getReadableDatabase().query(TankEntry.TABLE_NAME,
                projection, null, null, null, null, null);
    }

    private Cursor getReading(long identifier) {
        final String selection = ReadingEntry.COLUMN_IDENTIFIER + "=?";
        final String[] whereArgs = new String[] { String.valueOf(identifier) };
        final String orderBy = ReadingEntry.COLUMN_DATE;
        return db.getReadableDatabase().query(
                ReadingEntry.TABLE_NAME, null, selection, whereArgs, null, null, orderBy
        );
    }

    private Cursor getApiColors(@Categories int category) {
        final String selection = ApiColorChartEntry.COLUMN_CATEGORY + "=?";
        final String[] whereArgs = new String[] { String.valueOf(category) };
        return db.getReadableDatabase().query(
                ApiColorChartEntry.TABLE_NAME,
                null,
                selection,
                whereArgs,
                null,
                null,
                ApiColorChartEntry._ID);
    }

    private Uri insertTank(ContentValues cv) {
        final long id = db.getWritableDatabase().insertWithOnConflict(
                TankEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE
        );
        return  (id >= 0) ? TankEntry.buildTankUri(id) : null;
    }

    private Uri insertReading(ContentValues cv) {
        final long id = db.getWritableDatabase().insertWithOnConflict(
                ReadingEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE
        );
        return (id >= 0) ? ReadingEntry.buildReadingUri(id) : null;
    }

    private static final String TANK_QUERY = "SELECT " +
            TankEntry.TABLE_NAME + ".*, " +
            ReadingEntry.COLUMN_IDENTIFIER + ", " +
            ReadingEntry.COLUMN_AMMONIA + ", " +
            ReadingEntry.COLUMN_NITRITE + ", " +
            ReadingEntry.COLUMN_NITRATE + ", " +
            ReadingEntry.COLUMN_DATE + ", " +
            ReadingEntry.COLUMN_NOTES + ", " +
            ReadingEntry.COLUMN_IS_CONTROL + " " +
            "FROM " + TankEntry.TABLE_NAME + " LEFT OUTER JOIN " + ReadingEntry.TABLE_NAME + " " +
            "ON " + TankEntry.TABLE_NAME + "." + TankEntry._ID + "=" + ReadingEntry.COLUMN_IDENTIFIER + " " +
            "WHERE (" + ReadingEntry.COLUMN_DATE + " IS NULL) " +
            "OR (" + ReadingEntry.COLUMN_DATE + "=" +
            "(SELECT MAX(r2." + ReadingEntry.COLUMN_DATE + ") FROM " + ReadingEntry.TABLE_NAME + " " +
            "r2 WHERE " + ReadingEntry.TABLE_NAME + "." + ReadingEntry.COLUMN_IDENTIFIER + "=" +
            "r2." + ReadingEntry.COLUMN_IDENTIFIER + ")" +
            ");";

}
