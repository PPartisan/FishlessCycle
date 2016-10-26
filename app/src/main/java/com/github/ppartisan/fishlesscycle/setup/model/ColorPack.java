package com.github.ppartisan.fishlesscycle.setup.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;

public final class ColorPack {

    public final int colorRegular, colorDark;

    private ColorPack(int colorRegular, int colorDark) {
        this.colorRegular = colorRegular;
        this.colorDark = colorDark;
    }

    public static ColorPack build(int colorRegular, int colorDark) {
        return new ColorPack(colorRegular, colorDark);
    }

    public static ColorPack buildFromId(Context context, int colorRegularId, int colorDarkId) {

        final int colorRegular = ContextCompat.getColor(context, colorRegularId);
        final int colorDark = ContextCompat.getColor(context, colorDarkId);
        return new ColorPack(colorRegular, colorDark);

    }

}
