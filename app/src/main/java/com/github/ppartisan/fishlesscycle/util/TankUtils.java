package com.github.ppartisan.fishlesscycle.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Tank;

public final class TankUtils {

    private TankUtils() { throw new AssertionError(); }

    public static String getStageString(Resources res) {

        final String[] stages = res.getStringArray(R.array.tank_stage);
        final int randomIndex = (int) (Math.random()*stages.length - 1);

        return stages[randomIndex];

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

    private static void addImageSpan(Context context, SpannableStringBuilder builder, String text, int resId) {

        final int startIndex = builder.length();

        final Drawable drawable = ContextCompat.getDrawable(context, resId);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth()/2,drawable.getIntrinsicHeight()/2);
        final ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        builder.append(" ");
        builder.setSpan(span, startIndex, startIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(text);

    }

}
