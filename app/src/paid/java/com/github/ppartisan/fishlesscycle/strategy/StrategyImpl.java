package com.github.ppartisan.fishlesscycle.strategy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;

import com.github.ppartisan.fishlesscycle.strategy.sync.SyncUiFragment;
import com.github.ppartisan.fishlesscycle.strategy.sync.SyncUtil;
import com.github.ppartisan.fishlesscycle.ui.MainActivity;

class StrategyImpl implements AppStrategy {

    @Override
    public void loadAdForActivity(Activity activity) {}

    @Override
    public void initializeAd(Context context) {}

    @Override
    public void addExcludedTarget(Transition transition) {}

    @Override
    public void launchSyncAppFragment(AppCompatActivity activity) {
        final PackageManager pm = activity.getPackageManager();
        final FragmentManager fm = activity.getSupportFragmentManager();

        final SyncUiFragment frag = (SyncUiFragment) fm.findFragmentByTag(SyncUiFragment.TAG);

        if(frag == null) {
            if((SyncUtil.isFreeVersionInstalled(pm) &&
                    !SyncUtil.isSyncNoticeAlreadyDisplayed(activity))) {
                fm.beginTransaction()
                        .add(SyncUiFragment.newInstance(), SyncUiFragment.TAG)
                        .commit();
            } else {
                final Intent mainActivityIntent = new Intent(activity,MainActivity.class);
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(mainActivityIntent);
                activity.finish();
            }
        }

    }

    @Override
    public void setSyncNoticeAlreadyDisplayed(Context context, boolean isAlreadyDisplayed) {
        SyncUtil.setSyncNoticeAlreadyDisplayed(context, isAlreadyDisplayed);
    }


}
