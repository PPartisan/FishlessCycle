package com.github.ppartisan.fishlesscycle;

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
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;

import com.github.ppartisan.fishlesscycle.adapter.TanksAdapter;
import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.util.ReadingUtils;

public final class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            final Transition slide = new Explode();
            slide.excludeTarget(android.R.id.statusBarBackground, true);
            slide.excludeTarget(R.id.fd_fab, true);
            slide.excludeTarget(getAnimTargetName(), true);
            getWindow().setEnterTransition(slide);
        }

        ActivityCompat.postponeEnterTransition(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportFragmentManager().findFragmentById(R.id.da_container) == null) {

            final DetailFragment f = DetailFragment.newInstance(getName(), getIdentifier());
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.da_container, f);
            ft.commit();

        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

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

}
