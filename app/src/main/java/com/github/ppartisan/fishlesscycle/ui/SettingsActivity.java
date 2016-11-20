package com.github.ppartisan.fishlesscycle.ui;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.ppartisan.fishlesscycle.FishlessCycleApplication;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.ImagePack;
import com.github.ppartisan.fishlesscycle.reminder.ReminderReceiver;
import com.github.ppartisan.fishlesscycle.service.LoadImagePackReceiver;
import com.github.ppartisan.fishlesscycle.service.LoadImagePackReceiver.OnImagePackReadyListener;
import com.github.ppartisan.fishlesscycle.service.LoadImagePackService;
import com.github.ppartisan.fishlesscycle.util.AppUtils;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.widget.WidgetProvider;
import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements
        OnSharedPreferenceChangeListener, OnImagePackReadyListener {

    private Tracker mTracker;

    private LoadImagePackReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.mm_action_settings);

        AppUtils.checkInternetPermissions(this);
        mTracker = ((FishlessCycleApplication)getApplication()).getDefaultTracker();

        mReceiver = new LoadImagePackReceiver(this);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getFragmentManager().findFragmentById(android.R.id.content) == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new Preferences())
                    .commit();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.sendTrackerHit(mTracker, getClass());
        final IntentFilter filter = new IntentFilter(LoadImagePackService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
        LoadImagePackService.launchLoadImagePackService(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

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
    public void onImagePackReady(ImagePack pack) {
        final Preferences frag =
                (Preferences) getFragmentManager().findFragmentById(android.R.id.content);
        if (frag != null) {
            frag.setWidgetImageListEntries(pack.getTitles());
            frag.setWidgetImageListValues(pack.getPaths());
        }
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
