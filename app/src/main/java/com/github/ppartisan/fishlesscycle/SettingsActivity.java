package com.github.ppartisan.fishlesscycle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.ppartisan.fishlesscycle.reminder.ReminderReceiver;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

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
        }

        /*
        No need to update anything when intervals are changed as this is used when the alarm fires.
        */

    }

    public static class Preferences extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
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
