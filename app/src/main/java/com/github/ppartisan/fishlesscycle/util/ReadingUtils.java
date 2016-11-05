package com.github.ppartisan.fishlesscycle.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.ppartisan.fishlesscycle.data.Contract.ReadingEntry;
import com.github.ppartisan.fishlesscycle.model.Reading;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ReadingUtils {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM", Locale.getDefault());

    private ReadingUtils() { throw new AssertionError(); }

    public static String getReadableDateString(long date) {
        return DATE_FORMAT.format(date);
    }

    public static String toOneDecimalPlace(double input) {
        return String.valueOf(String.format(Locale.getDefault(), "%.1f", input));
    }

    public static List<Reading> getReadingsList(@NonNull Cursor cursor) {

        final List<Reading> readings = new ArrayList<>(cursor.getCount());

        if (cursor.moveToFirst()) {

            do {

                final long id =
                        cursor.getLong(cursor.getColumnIndex(ReadingEntry.COLUMN_IDENTIFIER));

                final long date =
                        cursor.getLong(cursor.getColumnIndex(ReadingEntry.COLUMN_DATE));
                final float ammonia =
                        cursor.getFloat(cursor.getColumnIndex(ReadingEntry.COLUMN_AMMONIA));
                final float nitrite =
                        cursor.getFloat(cursor.getColumnIndex(ReadingEntry.COLUMN_NITRITE));
                final float nitrate =
                        cursor.getFloat(cursor.getColumnIndex(ReadingEntry.COLUMN_NITRATE));
                final String note =
                        cursor.getString(cursor.getColumnIndex(ReadingEntry.COLUMN_NOTES));
                final boolean isControl =
                        (cursor.getInt(cursor.getColumnIndex(ReadingEntry.COLUMN_IS_CONTROL)) == 1);

                final Reading reading = new Reading(
                        id, date, ammonia, nitrite, nitrate, isControl
                );
                reading.setNote(note);
                readings.add(reading);

            } while (cursor.moveToNext());

        }

        return readings;

    }

    public static ContentValues toContentValues(Reading reading) {

        ContentValues cv = new ContentValues();
        cv.put(ReadingEntry.COLUMN_IDENTIFIER, reading.id);
        cv.put(ReadingEntry.COLUMN_DATE, reading.date);
        cv.put(ReadingEntry.COLUMN_AMMONIA, reading.ammonia);
        cv.put(ReadingEntry.COLUMN_NITRITE, reading.nitrite);
        cv.put(ReadingEntry.COLUMN_NITRATE, reading.nitrate);
        cv.put(ReadingEntry.COLUMN_IS_CONTROL, (reading.isControl) ? 1 : 0);
        cv.put(ReadingEntry.COLUMN_NOTES, reading.getNote());

        return cv;

    }

}
