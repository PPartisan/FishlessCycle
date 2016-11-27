package com.github.ppartisan.fishlesscycle.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.ppartisan.fishlesscycle.strategy.Strategy;

public final class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Strategy.get().launchSyncAppFragment(this);
    }

}
