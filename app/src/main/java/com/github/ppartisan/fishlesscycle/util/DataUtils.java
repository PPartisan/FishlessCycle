package com.github.ppartisan.fishlesscycle.util;

import android.content.ContentProviderOperation;

import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.Tank;

import java.util.ArrayList;

public final class DataUtils {

    private DataUtils() { throw new AssertionError(); }

    public static ArrayList<ContentProviderOperation> tankBuilderToBatchList(Tank.Builder builder) {

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>(3);

        ContentProviderOperation op;

        final long id = builder.getIdentifier();
        final String where = Contract.TankEntry._ID + " =?";
        final String[] whereArgs = new String[] { String.valueOf(id) };

        op = ContentProviderOperation.newUpdate(Contract.TankEntry.CONTENT_URI)
                .withSelection(where, whereArgs)
                .withValues(TankUtils.toContentValues(builder))
                .build();

        ops.add(op);

        if (builder.getControlReading() != null) {
            op = ContentProviderOperation.newInsert(Contract.ReadingEntry.CONTENT_URI)
                    .withValues(ReadingUtils.toContentValues(builder.getControlReading()))
                    .build();

            ops.add(op);
        }

        if (builder.getLastReading() != null) {
            op = ContentProviderOperation.newInsert(Contract.ReadingEntry.CONTENT_URI)
                    .withValues(ReadingUtils.toContentValues(builder.getLastReading()))
                    .build();

            ops.add(op);
        }

        return ops;

    }

}
