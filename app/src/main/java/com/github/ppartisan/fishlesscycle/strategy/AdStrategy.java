package com.github.ppartisan.fishlesscycle.strategy;

import android.app.Activity;
import android.content.Context;

interface AdStrategy {

    void loadAdForActivity(Activity activity);
    void initializeAd(Context context);

}
