package com.github.ppartisan.fishlesscycle.util;

import android.content.ContentProviderOperation;

import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.Reading;
import com.github.ppartisan.fishlesscycle.model.Tank;

import java.util.ArrayList;

public final class DataUtils {

    private DataUtils() { throw new AssertionError(); }

    public static ArrayList<ContentProviderOperation> tankBuilderToBatchList(Tank.Builder builder) {

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>(3);

        ContentProviderOperation op;

        final long id = builder.getIdentifier();
        String where = Contract.TankEntry._ID + " =?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        op = ContentProviderOperation.newUpdate(Contract.TankEntry.CONTENT_URI)
                .withSelection(where, whereArgs)
                .withValues(TankUtils.toContentValues(builder))
                .build();

        ops.add(op);


        Reading control = builder.getControlReading();

        if(control == null) {
            control =
                    new Reading(builder.getIdentifier(), Reading.CONTROL_DATE, 0, 0, 0, true);
            builder.setControlReading(control);

            op = ContentProviderOperation.newInsert(Contract.ReadingEntry.CONTENT_URI)
                    .withValues(ReadingUtils.toContentValues(builder.getControlReading()))
                    .build();
        } else {

            where = Contract.ReadingEntry.COLUMN_IS_CONTROL + "=? AND "
                    + Contract.ReadingEntry.COLUMN_IDENTIFIER + "=?";
            whereArgs = new String[] { String.valueOf(1), String.valueOf(id) };

            op = ContentProviderOperation.newUpdate(Contract.ReadingEntry.CONTENT_URI)
                    .withSelection(where, whereArgs)
                    .withValues(ReadingUtils.toContentValues(builder.getControlReading()))
                    .build();
        }

        ops.add(op);


        if (builder.getLastReading() != null) {
            op = ContentProviderOperation.newInsert(Contract.ReadingEntry.CONTENT_URI)
                    .withValues(ReadingUtils.toContentValues(builder.getLastReading()))
                    .build();

            ops.add(op);
        }

        return ops;

    }

}
