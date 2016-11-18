package com.github.ppartisan.fishlesscycle;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.util.AppUtils;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.Tracker;

import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 4;

    private Tracker mTracker;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppUtils.checkInternetPermissions(this);
        mTracker = ((FishlessCycleApplication)getApplication()).getDefaultTracker();

        mAdView = (AdView) findViewById(R.id.ma_ad_view);
        final AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("C07A980181A0030AB61A20553A00CD1E")
                .build();
        mAdView.loadAd(request);

        if(getSupportFragmentManager().findFragmentById(R.id.ma_container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.ma_container, MainFragment.newInstance())
                    .commit();
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.sendTrackerHit(mTracker, getClass());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.TankEntry.CONTENT_URI, null, "0", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final MainFragment fragment =
                (MainFragment) getSupportFragmentManager().findFragmentById(R.id.ma_container);
        fragment.updateTankList(TankUtils.getTankList(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
