package com.github.ppartisan.fishlesscycle;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.util.ReadingUtils;

public final class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, DetailFragment.newInstance(getIdentifier()))
                    .commit();

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
                (DetailFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        frag.updateReadings(ReadingUtils.getReadingsList(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private long getIdentifier() {
        return getIntent().getLongExtra(DetailFragment.KEY_IDENTIFIER, -1);
    }

}
