package com.github.ppartisan.fishlesscycle.strategy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.setup.SetUpWizardActivity;
import com.github.ppartisan.fishlesscycle.ui.DetailActivity;
import com.github.ppartisan.fishlesscycle.ui.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.HashMap;
import java.util.Map;

import static com.github.ppartisan.fishlesscycle.util.Api.OUKITEL_DEVICE_ID;

class StrategyImpl implements AppStrategy {

    private final Map<String, Integer> mAdIdMap = buildAdIdMap();

    @Override
    public void loadAdForActivity(Activity activity) {
        final int adResId = mAdIdMap.get(activity.getClass().getSimpleName());
        final AdView adView = (AdView) activity.findViewById(adResId);
        final AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(OUKITEL_DEVICE_ID)
                .build();
        adView.loadAd(request);

    }

    @Override
    public void initializeAd(Context context) {
        MobileAds.initialize(
                context.getApplicationContext(), context.getString(R.string.ad_unit_id)
        );
    }

    private static Map<String, Integer> buildAdIdMap() {

        final Map<String, Integer> map = new HashMap<>(3);
        map.put(MainActivity.class.getSimpleName(), R.id.ma_ad_view);
        map.put(DetailActivity.class.getSimpleName(), R.id.da_ad_view);
        map.put(SetUpWizardActivity.class.getSimpleName(), R.id.wusa_ad_view);

        return map;

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void addExcludedTarget(Transition transition) {
        transition.excludeTarget(R.id.da_ad_view, true);
    }

    @Override
    public void launchSyncAppFragment(AppCompatActivity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.finish();
    }

    @Override
    public void setSyncNoticeAlreadyDisplayed(Context context, boolean isAlreadyDisplayed) {}

}
