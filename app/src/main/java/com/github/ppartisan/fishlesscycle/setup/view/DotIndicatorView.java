package com.github.ppartisan.fishlesscycle.setup.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.github.ppartisan.fishlesscycle.util.ViewUtils;

public final class DotIndicatorView extends View {

    private static final float RADIUS = ViewUtils.dpToPx(2);
    private static final float PADDING_INTERNAL = ViewUtils.dpToPx(1);
    private static final float STROKE_WIDTH = 1f;

    private ViewPager mPager;

    private final Paint mCirclePaint = new Paint();

    public DotIndicatorView(Context context) {
        super(context);
        init();
    }

    public DotIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DotIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStrokeWidth(STROKE_WIDTH);
        mCirclePaint.setAntiAlias(true);
    }

    public void attachToViewPager(ViewPager pager) {
        mPager = pager;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if(mPager == null || mPager.getAdapter() == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        final int circleCount = mPager.getAdapter().getCount();
        final int targetWidth = (int)((RADIUS*circleCount) + (PADDING_INTERNAL*(circleCount-2)));
        final int targetHeight = (int)(RADIUS*2);

        setMeasuredDimension(targetWidth, targetHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int numDots = mPager.getAdapter().getCount();
        final int currentPage = mPager.getCurrentItem();

        final int width = canvas.getWidth();
        final int subCanvasWidth = width/numDots;

        final float y = canvas.getHeight()/2;

        int counter = 0;

        while (counter < numDots) {
            final float left = counter*subCanvasWidth;
            final float x = left + (subCanvasWidth/2);

            mCirclePaint.setStyle((counter==currentPage) ? Paint.Style.FILL : Paint.Style.STROKE);
            canvas.drawCircle(x,y,RADIUS,mCirclePaint);

            counter++;
        }

    }

}
