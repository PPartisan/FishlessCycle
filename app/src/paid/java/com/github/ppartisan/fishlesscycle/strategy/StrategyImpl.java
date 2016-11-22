package com.github.ppartisan.fishlesscycle.strategy;

import android.app.Activity;
import android.content.Context;
import android.transition.Transition;

public class StrategyImpl implements AdStrategy, AnimStrategy {

    @Override
    public void loadAdForActivity(Activity activity) {}

    @Override
    public void initializeAd(Context context) {}

    @Override
    public void addExcludedTarget(Transition transition) {}

}
