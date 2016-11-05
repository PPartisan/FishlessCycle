package com.github.ppartisan.fishlesscycle;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;

import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, MainFragment.newInstance())
                    .commit();
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.TankEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final MainFragment fragment =
                (MainFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        fragment.updateTankList(TankUtils.getTankList(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
