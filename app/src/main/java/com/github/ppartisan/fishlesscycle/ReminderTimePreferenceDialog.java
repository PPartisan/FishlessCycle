package com.github.ppartisan.fishlesscycle;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.github.ppartisan.fishlesscycle.reminder.ReminderReceiver;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;

import java.util.Calendar;

/*
 * Adapted From: http://stackoverflow.com/a/5533295/1219389
 */
public final class ReminderTimePreferenceDialog extends DialogPreference {

    private int lastHour=0;
    private int lastMinute=0;
    private TimePicker picker=null;

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    public ReminderTimePreferenceDialog(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText(ctxt.getText(R.string.set));
        setNegativeButtonText(ctxt.getText(R.string.cancel));
    }

    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext());
        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour = getCurrentHour();
            lastMinute = getCurrentMinute();

            String time=String.valueOf(lastHour)+":"+String.valueOf(lastMinute);

            if (callChangeListener(time)) {
                persistString(time);
                final Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, lastHour);
                c.set(Calendar.MINUTE, lastMinute);
                ReminderReceiver.updateReminderAlarm(getContext(), c);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        lastHour=getHour(time);
        lastMinute=getMinute(time);
    }

    @SuppressWarnings("deprecation")
    private Integer getCurrentHour() {
        if(picker == null) return 0;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            return picker.getHour();
        } else {
            return picker.getCurrentHour();
        }
    }

    @SuppressWarnings("deprecation")
    private Integer getCurrentMinute() {
        if(picker == null) return 0;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            return picker.getMinute();
        } else {
            return picker.getCurrentMinute();
        }
    }

}
