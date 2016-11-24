package com.github.ppartisan.fishlesscycle.util;

import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry;
import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry.Categories;
import com.github.ppartisan.fishlesscycle.model.ApiColorChartItem;
import com.github.ppartisan.fishlesscycle.model.Tank;

import java.util.ArrayList;
import java.util.List;

public final class ApiColorChatUtils {

    private ApiColorChatUtils() { throw new AssertionError(); }

    public static ArrayList<ApiColorChartItem> buildApiColorChartList(Cursor cursor) {

        if(cursor == null) {
            return new ArrayList<>();
        }

        ArrayList<ApiColorChartItem> items = new ArrayList<>(cursor.getCount());

        if (cursor.moveToFirst()) {

            do {

                final @Categories int category =
                        cursor.getInt(cursor.getColumnIndex(ApiColorChartEntry.COLUMN_CATEGORY));
                final int color = Color.parseColor(
                        cursor.getString(cursor.getColumnIndex(ApiColorChartEntry.COLUMN_COLOR))
                );
                final float value =
                        cursor.getFloat(cursor.getColumnIndex(ApiColorChartEntry.COLUMN_VALUE));

                items.add(new ApiColorChartItem(category, color, value));

            } while (cursor.moveToNext());

        }

        return items;

    }

}
