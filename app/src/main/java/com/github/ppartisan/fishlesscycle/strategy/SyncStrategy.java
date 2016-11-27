package com.github.ppartisan.fishlesscycle.strategy;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

interface SyncStrategy {

    void launchSyncAppFragment(AppCompatActivity activity);
    void setSyncNoticeAlreadyDisplayed(Context context, boolean isAlreadyDisplayed);

}
