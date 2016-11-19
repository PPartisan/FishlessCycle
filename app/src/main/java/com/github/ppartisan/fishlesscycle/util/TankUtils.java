package com.github.ppartisan.fishlesscycle.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.data.Contract.ReadingEntry;
import com.github.ppartisan.fishlesscycle.data.Contract.TankEntry;
import com.github.ppartisan.fishlesscycle.model.AmmoniaDosage;
import com.github.ppartisan.fishlesscycle.model.Reading;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.model.Tank.PlantStatus;

import java.util.ArrayList;
import java.util.List;

public final class TankUtils {

    private TankUtils() { throw new AssertionError(); }

    public static List<Tank> getTankList(@NonNull Cursor cursor) {

        final List<Tank> tanks = new ArrayList<>(cursor.getCount());

        if (cursor.moveToFirst()) {

            Tank.Builder builder = new Tank.Builder();

            do {

                final String name =
                        cursor.getString(cursor.getColumnIndex(TankEntry.COLUMN_NAME));
                final String image =
                        cursor.getString(cursor.getColumnIndex(TankEntry.COLUMN_IMAGE));
                final float volumeInLitres =
                        cursor.getFloat(cursor.getColumnIndex(TankEntry.COLUMN_VOLUME));
                final float dosage =
                        cursor.getFloat(cursor.getColumnIndex(TankEntry.COLUMN_DOSAGE));
                final float concentration =
                        cursor.getFloat(cursor.getColumnIndex(TankEntry.COLUMN_CONCENTRATION));
                final boolean isHeated =
                        (cursor.getInt(cursor.getColumnIndex(TankEntry.COLUMN_IS_HEATED)) == 1);
                final boolean isSeeded =
                        (cursor.getInt(cursor.getColumnIndex(TankEntry.COLUMN_IS_SEEDED)) == 1);
                final @PlantStatus int plantStatus =
                        cursor.getInt(cursor.getColumnIndex(TankEntry.COLUMN_PLANT_STATUS));
                final long identifier =
                        cursor.getLong(cursor.getColumnIndex(TankEntry._ID));

                final long date =
                        cursor.getLong(cursor.getColumnIndex(ReadingEntry.COLUMN_DATE));
                final int ammonia =
                        cursor.getInt(cursor.getColumnIndex(ReadingEntry.COLUMN_AMMONIA));
                final int nitrite =
                        cursor.getInt(cursor.getColumnIndex(ReadingEntry.COLUMN_NITRITE));
                final int nitrate =
                        cursor.getInt(cursor.getColumnIndex(ReadingEntry.COLUMN_NITRATE));
                final Reading lastReading =
                        new Reading(identifier, date, ammonia, nitrite, nitrate, false);

                builder.setName(name)
                        .setImage(image)
                        .setVolumeInLitres(volumeInLitres)
                        .setAmmoniaDosage(dosage, concentration)
                        .setLastReading(lastReading)
                        .setIsHeated(isHeated)
                        .setIsSeeded(isSeeded)
                        .setPlantStatus(plantStatus)
                        .setIdentifier(identifier);

                tanks.add(builder.build());

            } while (cursor.moveToNext());

        }

        return tanks;

    }

    public static String getAbbreviatedVolumeUnit(Resources res, @PreferenceUtils.VolumeUnit int unitType) {

        final String[] options = res.getStringArray(R.array.volume_unit_options_abbr);
        String abbrUnit = null;

        switch (unitType) {
            case PreferenceUtils.METRIC:
                abbrUnit = options[0];
                break;
            case PreferenceUtils.IMPERIAL:
            case PreferenceUtils.US:
                abbrUnit = options[1];
                break;
        }

        return abbrUnit;

    }

    public static CharSequence getTankOptionsText(Context context, Tank tank) {

        final String[] textArray = context.getResources().getStringArray(R.array.tank_options);

        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (tank.isHeated) {
            addImageSpan(context, builder, textArray[0], R.drawable.ic_thermometer_white);
            builder.append(", ");
        }

        switch (tank.plantStatus) {
            case Tank.LIGHT:
                addImageSpan(context, builder, textArray[1], R.drawable.ic_planted_light_white);
                builder.append(", ");
                break;
            case Tank.HEAVY:
                addImageSpan(context, builder, textArray[2], R.drawable.ic_planted_heavy_white);
                builder.append(", ");
                break;
            case Tank.NONE:
                //unused
                break;
        }

        if (tank.isSeeded) {
            addImageSpan(context, builder, textArray[3], R.drawable.ic_seeded_white);
        }

        if (builder.toString().endsWith(", ")) {
            builder.delete(builder.length() - 2, builder.length());
        }

        return builder;

    }

    public static float getTankVolumeInLitresAsUserUnitPreference(float volumeInLitres, @PreferenceUtils.VolumeUnit int unit) {

        float displayVolume = 0;
        switch (unit) {
            case PreferenceUtils.METRIC:
                displayVolume = volumeInLitres;
                break;
            case PreferenceUtils.IMPERIAL:
                displayVolume = ConversionUtils.getLitresAsImperialGallons(volumeInLitres);
                break;
            case PreferenceUtils.US:
                displayVolume = ConversionUtils.getLitresAsUsGallons(volumeInLitres);
                break;
        }

        return displayVolume;
    }

    public static float getVolumeAsLitres(float volume, @PreferenceUtils.VolumeUnit int unit) {

        float volumeInLitres = 0;

        switch (unit) {
            case PreferenceUtils.METRIC:
                //Already in litres
                volumeInLitres = volume;
                break;
            case PreferenceUtils.IMPERIAL:
                volumeInLitres = ConversionUtils.getImperialGallonsAsLitres(volume);
                break;
            case PreferenceUtils.US:
                volumeInLitres = ConversionUtils.getUsGallonsAsLitres(volume);
                break;
        }

        return volumeInLitres;
    }

    private static void addImageSpan(Context context, SpannableStringBuilder builder, String text, int resId) {

        final int startIndex = builder.length();

        final Drawable drawable = ContextCompat.getDrawable(context, resId);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth()/2,drawable.getIntrinsicHeight()/2);
        final ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        builder.append(" ");
        builder.setSpan(span, startIndex, startIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(text);

    }

    public static ContentValues toContentValues(Tank.Builder builder) {

        ContentValues cv = new ContentValues();
        cv.put(TankEntry.COLUMN_NAME, builder.getName());
        cv.put(TankEntry.COLUMN_VOLUME, builder.getVolumeInLitres());
        cv.put(TankEntry.COLUMN_CONCENTRATION, builder.getAmmoniaDosage().targetConcentration);
        cv.put(TankEntry.COLUMN_DOSAGE, builder.getAmmoniaDosage().dosage);
        cv.put(TankEntry.COLUMN_IS_HEATED, (builder.isHeated()) ? 1 : 0);
        cv.put(TankEntry.COLUMN_IS_SEEDED, (builder.isSeeded()) ? 1 : 0);
        cv.put(TankEntry.COLUMN_PLANT_STATUS, builder.getPlantStatus());
        cv.put(TankEntry._ID, builder.getIdentifier());

        return cv;

    }

    public static String getUserVolumeUnitAsString(Resources res, @PreferenceUtils.VolumeUnit int unit) {

        String unitString = null;

        switch (unit) {
            case PreferenceUtils.METRIC:
                unitString = res.getString(R.string.litres);
                break;
            case PreferenceUtils.IMPERIAL:
                unitString = res.getString(R.string.imperial_gallons);
                break;
            case PreferenceUtils.US:
                unitString = res.getString(R.string.us_gallons);
                break;
        }

        return unitString;

    }

    public static ContentValues toContentValues(Tank tank) {

        ContentValues cv = new ContentValues(9);
        cv.put(TankEntry.COLUMN_NAME, tank.name);
        cv.put(TankEntry.COLUMN_IMAGE, tank.image);
        cv.put(TankEntry.COLUMN_VOLUME, tank.volumeInLitres);
        cv.put(TankEntry.COLUMN_CONCENTRATION, tank.getAmmoniaDosage().targetConcentration);
        cv.put(TankEntry.COLUMN_DOSAGE, tank.getAmmoniaDosage().dosage);
        cv.put(TankEntry.COLUMN_IS_HEATED, (tank.isHeated) ? 1 : 0);
        cv.put(TankEntry.COLUMN_IS_SEEDED, (tank.isSeeded) ? 1 : 0);
        cv.put(TankEntry.COLUMN_PLANT_STATUS, tank.plantStatus);
        cv.put(TankEntry._ID, tank.identifier);

        return cv;

    }

    public static boolean equals(AmmoniaDosage a1, AmmoniaDosage a2) {
        return a1 == a2 || a1 != null && a2 != null && a1.equals(a2);
    }

}
