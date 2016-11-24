package com.github.ppartisan.fishlesscycle.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.github.ppartisan.fishlesscycle.util.ViewUtils;

public final class BorderedView extends View {

    private static final float BORDER_WIDTH = ViewUtils.dpToPx(1);

    private final Paint mPaint = new Paint();
    private int mFillColor, mBorderColor;

    public BorderedView(Context context) {
        super(context);
        init();
    }

    public BorderedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BorderedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BorderedView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint.setStrokeWidth(BORDER_WIDTH);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mBorderColor);
        mPaint.setAntiAlias(true);
    }

    public void setColors(int fillColor) {
        mFillColor = fillColor;
        mBorderColor = ViewUtils.buildDarkColor(fillColor);
        mPaint.setColor(mBorderColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mFillColor);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
    }


}
