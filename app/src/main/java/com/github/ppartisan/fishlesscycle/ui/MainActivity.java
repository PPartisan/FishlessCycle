package com.github.ppartisan.fishlesscycle.ui;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.ppartisan.fishlesscycle.FishlessCycleApplication;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.strategy.Strategy;
import com.github.ppartisan.fishlesscycle.util.AppUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String UNINSTALL_MESSAGE_EXTRA = TAG + ".UNINSTALL_MESSAGE_EXTRA";

    private static final int LOADER_ID = 4;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportFragmentManager().findFragmentById(R.id.ma_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.ma_container, MainFragment.newInstance(getMessage()))
                    .commit();
        }

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
        final List<Tank> tanks = TankUtils.getTankList(data);
        for(Tank tank : tanks) {
            if (tank.image != null) {
                AppUtils.checkStoragePermissions(this);
                break;
            }
        }
        final MainFragment fragment =
                (MainFragment) getSupportFragmentManager().findFragmentById(R.id.ma_container);
        fragment.updateTankList(tanks);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppUtils.REQUEST_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final MainFragment fragment =
                            (MainFragment) getSupportFragmentManager().findFragmentById(R.id.ma_container);
                    fragment.refreshAdapter();
                }
                break;
        }
    }

    private String getMessage() {
        return getIntent().getStringExtra(UNINSTALL_MESSAGE_EXTRA);
    }

}
