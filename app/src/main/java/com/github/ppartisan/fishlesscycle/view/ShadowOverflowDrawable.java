package com.github.ppartisan.fishlesscycle.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.github.ppartisan.fishlesscycle.R;

public final class ShadowOverflowDrawable extends Drawable {

    private static final float SHADOW_RADIUS = 6f;

    private final Bitmap mOverflowIcon, mShadowIcon;
    private final Paint mShadowPaint;
    private final int[] mOffset = new int[2];

    public ShadowOverflowDrawable(Resources res) {

        mOverflowIcon = BitmapFactory.decodeResource(res, R.drawable.ic_more_vert_white_24dp);
        mShadowPaint = new Paint();
        mShadowPaint.setColor(Color.DKGRAY);
        mShadowPaint.setShadowLayer(SHADOW_RADIUS, 0f, 0f, Color.DKGRAY);

        final BlurMaskFilter blurFilter = new BlurMaskFilter(SHADOW_RADIUS, BlurMaskFilter.Blur.OUTER);
        mShadowPaint.setMaskFilter(blurFilter);

        mShadowIcon = mOverflowIcon.extractAlpha(mShadowPaint, mOffset);

    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final float cx = getBounds().centerX() - (mOverflowIcon.getWidth()/2);
        final float cy = getBounds().centerY() - (mOverflowIcon.getHeight()/2);
        canvas.drawBitmap(mShadowIcon, cx, cy, mShadowPaint);
        canvas.drawBitmap(mOverflowIcon, cx - mOffset[0], cy - mOffset[1], mShadowPaint);
    }

    @Override
    public void setAlpha(int i) {}

    @Override
    public void setColorFilter(ColorFilter colorFilter) {}

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
