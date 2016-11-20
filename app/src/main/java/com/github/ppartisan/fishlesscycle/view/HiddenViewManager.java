package com.github.ppartisan.fishlesscycle.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Interpolator;

public final class HiddenViewManager {

    private static final String TAG = HiddenViewManager.class.getSimpleName();
    private static final String STATE_KEY = TAG + ".STATE_KEY";
    private static final int ANIMATION_DURATION = 500;

    private AnimationCallbacks mCallbacks;

    private boolean isExpanded = false;
    private final View mView;

    private final Interpolator fastOutSlowInInterpolator = new FastOutSlowInInterpolator();

    public HiddenViewManager(View view) {
        mView = view;
        mView.setVisibility(View.GONE);
    }

    public void animateIn() {

        if (isExpanded) return;

        ViewCompat.setTranslationY(mView, mView.getHeight());
        ViewCompat.animate(mView)
                .translationY(0f)
                .setInterpolator(fastOutSlowInInterpolator)
                .setDuration(ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        mView.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(View view) {
                        isExpanded = true;
                        if (mCallbacks != null) {
                            mCallbacks.onExpanded();
                        }
                    }
                })
                .setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(View view) {
                        final float transY = ViewCompat.getTranslationY(mView);
                        final float targetY = mView.getHeight();
                        if (mCallbacks != null) {
                            mCallbacks.onAnimationUpdate(transY/targetY);
                        }
                    }
                }).start();
    }

    public void animateOut() {

        if(!isExpanded) return;

        ViewCompat.animate(mView)
                .translationY(mView.getHeight())
                .setInterpolator(fastOutSlowInInterpolator)
                .setDuration(ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        mView.setVisibility(View.GONE);
                        isExpanded = false;
                        if (mCallbacks != null) {
                            mCallbacks.onCollapsed();
                        }
                    }
                })
                .setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(View view) {
                        final float transY = ViewCompat.getTranslationY(mView);
                        final float targetY = mView.getHeight();
                        if (mCallbacks != null) {
                            mCallbacks.onAnimationUpdate(transY/targetY);
                        }
                    }
                }).start();

    }

    public void setAnimationCallbacks(AnimationCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void animate() {
        if (isExpanded) {
            animateOut();
        } else {
            animateIn();
        }
    }

    public View getHiddenView() {
        return mView;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    private void updateState() {
        if (isExpanded) {
            mView.setVisibility(View.VISIBLE);
            ViewCompat.setTranslationY(mView, mView.getHeight());
        } else {
            mView.setVisibility(View.GONE);
            ViewCompat.setTranslationY(mView, 0f);
        }
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        bundle.putBoolean(STATE_KEY, isExpanded);
    }

    public void onRestoreInstanceState(@NonNull Bundle bundle) {
        isExpanded = bundle.getBoolean(STATE_KEY);
        updateState();
    }

    public interface AnimationCallbacks {

        void onAnimationUpdate(float per);
        void onExpanded();
        void onCollapsed();

    }

}
