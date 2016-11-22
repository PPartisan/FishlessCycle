package com.github.ppartisan.fishlesscycle.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.compat.BuildConfig;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.ppartisan.fishlesscycle.FishlessCycleApplication;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.strategy.Strategy;
import com.github.ppartisan.fishlesscycle.util.AppUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;
import com.google.android.gms.analytics.Tracker;

import static com.github.ppartisan.fishlesscycle.data.Contract.CONTENT_AUTHORITY;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 4;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(getClass().getSimpleName(), CONTENT_AUTHORITY);

        AppUtils.checkInternetPermissions(this);
        mTracker = ((FishlessCycleApplication)getApplication()).getDefaultTracker();

        Strategy.get().initializeAd(this);
        Strategy.get().loadAdForActivity(this);

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
