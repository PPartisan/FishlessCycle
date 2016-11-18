package com.github.ppartisan.fishlesscycle;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class FishlessCycleApplication extends Application {

    private Tracker mTracker;

    public synchronized Tracker getDefaultTracker() {
        if(mTracker == null) {
            final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

}
