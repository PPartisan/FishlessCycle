package com.github.ppartisan.fishlesscycle.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Transition;
import android.view.Window;

import com.github.ppartisan.fishlesscycle.FishlessCycleApplication;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.adapter.TanksAdapter;
import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.strategy.Strategy;
import com.github.ppartisan.fishlesscycle.util.AppUtils;
import com.github.ppartisan.fishlesscycle.util.ReadingUtils;
import com.google.android.gms.analytics.Tracker;

public final class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 5;

    private Tracker mTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            final Transition explode = new Explode();
            explode.excludeTarget(android.R.id.statusBarBackground, true);
            explode.excludeTarget(R.id.fd_fab, true);
            explode.excludeTarget(getAnimTargetName(), true);
            Strategy.get().addExcludedTarget(explode);
            getWindow().setEnterTransition(explode);
        }

        ActivityCompat.postponeEnterTransition(this);

        super.onCreate(savedInstanceState);
        setTitle(getName());
        setContentView(R.layout.activity_detail);

        AppUtils.checkInternetPermissions(this);
        mTracker = ((FishlessCycleApplication)getApplication()).getDefaultTracker();

        Strategy.get().initializeAd(this);
        Strategy.get().loadAdForActivity(this);

        if (getSupportFragmentManager().findFragmentById(R.id.da_container) == null) {

            final DetailFragment f =
                    DetailFragment.newInstance(getName(), getIdentifier(), getDosage());
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.da_container, f);
            ft.commit();

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
        final Uri uri = Contract.ReadingEntry.buildReadingUri(getIdentifier());
        return new CursorLoader(this, uri, null, null, null, Contract.ReadingEntry.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null) return;

        final DetailFragment frag =
                (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.da_container);
        frag.updateReadings(ReadingUtils.getReadingsList(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private long getIdentifier() {
        return getIntent().getLongExtra(DetailFragment.KEY_IDENTIFIER, -1);
    }

    private String getAnimTargetName() {
        return TanksAdapter.TRANSITION_NAME_BASE + getIdentifier();
    }

    private String getName() {
        return getIntent().getStringExtra(DetailFragment.KEY_NAME);
    }

    private float getDosage() {
        return getIntent().getFloatExtra(DetailFragment.KEY_DOSAGE, 0f);
    }

}
