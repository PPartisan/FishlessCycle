package com.github.ppartisan.fishlesscycle;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.data.Provider;
import com.github.ppartisan.fishlesscycle.model.ImagePack;
import com.github.ppartisan.fishlesscycle.reminder.ReminderReceiver;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.widget.WidgetProvider;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int IMAGES_LOADER_ID = 50;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getFragmentManager().findFragmentById(android.R.id.content) == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new Preferences())
                    .commit();
        }

        getSupportLoaderManager().initLoader(IMAGES_LOADER_ID, null, this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /*
    The "interval" value is only used when
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String s) {

        final String prefIncludeReminderKey = getString(R.string.pref_include_reminder_key);
        final String reminderTimeKey = getString(R.string.pref_reminder_time_key);
        final String widgetImageKey = getString(R.string.pref_widget_image_path_key);

        if(prefIncludeReminderKey.equals(s)) {

            final boolean isReminderEnabled = prefs.getBoolean(prefIncludeReminderKey, false);
            if(isReminderEnabled) {
                final Calendar c = PreferenceUtils.getReminderTime(this);
                ReminderReceiver.updateReminderAlarm(this, c);
            } else {
                PreferenceUtils.clearReminderTime(this);
                ReminderReceiver.cancelReminderAlarm(this);
            }

        } else if (reminderTimeKey.equals(s)) {

            //Can only be updated if reminders are enabled, so send new alarm
            final Calendar c = PreferenceUtils.getReminderTime(this);
            ReminderReceiver.updateReminderAlarm(this, c);
        } else if (widgetImageKey.equals(s)) {
            final String value = prefs.getString(widgetImageKey, null);
            PreferenceUtils.setWidgetImagePath(this, value);
            WidgetProvider.updateWidget(this);
        }

        /*
        No need to update anything when intervals are changed as this is used when the alarm fires.
        */

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this, Contract.TankEntry.CONTENT_URI, Provider.IMAGES_PROJECTION, null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final Preferences frag = 
                (Preferences) getFragmentManager().findFragmentById(android.R.id.content);
        if (frag != null) {
            final ImagePack pack = PreferenceUtils.buildImagePack(data);
            frag.setWidgetImageListEntries(pack.getTitles());
            frag.setWidgetImageListValues(pack.getPaths());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static class Preferences extends PreferenceFragment {

        private ListPreference mWidgetImageList;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            mWidgetImageList = (ListPreference)
                    findPreference(getString(R.string.pref_widget_image_path_key));
        }


        private void setWidgetImageListEntries(CharSequence[] entries) {
            mWidgetImageList.setEntries(entries);
        }

        private void setWidgetImageListValues(CharSequence[] values) {
            mWidgetImageList.setEntryValues(values);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
