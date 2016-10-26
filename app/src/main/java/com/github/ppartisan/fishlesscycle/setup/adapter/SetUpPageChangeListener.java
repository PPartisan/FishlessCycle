package com.github.ppartisan.fishlesscycle.setup.adapter;

import android.animation.ArgbEvaluator;
import android.support.v4.view.ViewPager;

import com.github.ppartisan.fishlesscycle.setup.model.ColorPack;

import java.util.List;

public final class SetUpPageChangeListener implements ViewPager.OnPageChangeListener {

    private final SetUpPageChangeListenerCallbacks mCallback;
    private final List<ColorPack> mColors;
    private final ArgbEvaluator mEvaluator;

    public SetUpPageChangeListener(SetUpPageChangeListenerCallbacks callbacks, List<ColorPack> colors) {
        mCallback = callbacks;
        mColors = colors;
        mEvaluator = new ArgbEvaluator();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (position < (mCallback.getCount() -1) && position < (mColors.size()-1)) {

            final ColorPack currentColor = mColors.get(position);
            final ColorPack nextColor = mColors.get(position + 1);

            final int primaryColor =
                    getEvaluatedRegularColor(positionOffset, currentColor, nextColor);

            final int primaryDarkColor =
                    getEvaluatedDarkColor(positionOffset, currentColor, nextColor);

            mCallback.onUpdateColors(primaryColor, primaryDarkColor);

        } else {

            final ColorPack finalColor = mColors.get(mColors.size()-1);

            mCallback.onUpdateColors(finalColor.colorRegular, finalColor.colorDark);

        }
    }

    @Override
    public void onPageSelected(int position) { }

    @Override
    public void onPageScrollStateChanged(int state) { }

    private int getEvaluatedRegularColor(float offset, ColorPack current, ColorPack next) {
        return (Integer) mEvaluator.evaluate(offset, current.colorRegular, next.colorRegular);
    }

    private int getEvaluatedDarkColor(float offset, ColorPack current, ColorPack next) {
        return (Integer) mEvaluator.evaluate(offset, current.colorDark, next.colorDark);
    }

}
