package com.github.ppartisan.fishlesscycle.reminder;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.ui.MainActivity;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;
import com.github.ppartisan.fishlesscycle.widget.WidgetProvider;

import java.util.Calendar;

public final class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Intent nIntent = new Intent(context, MainActivity.class);
        nIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final PendingIntent pIntent =
                PendingIntent.getActivity(
                        context, 0, nIntent, PendingIntent.FLAG_UPDATE_CURRENT
                );

        final Bitmap largeIcon =
                ViewUtils.getSizedBitmapForNotification(
                        context.getResources(), R.drawable.f_cycle_color
                );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setLargeIcon(largeIcon);
        builder.setSmallIcon(R.drawable.tank_white);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(context.getString(R.string.r_content));
        builder.setColor(ContextCompat.getColor(context, R.color.primary));
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(pIntent);
        builder.setAutoCancel(true);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        }

        manager.notify(0, builder.build());

        //Reset alarm
        final Calendar calendar = PreferenceUtils.getReminderTime(context);
        final int intervalInDays = PreferenceUtils.getReminderIntervalInDays(context);
        calendar.add(Calendar.DAY_OF_MONTH, intervalInDays);
        updateReminderAlarm(context, calendar);

    }

    public static void updateReminderAlarm(Context ctx, Calendar calendar) {

        if(calendar.getTimeInMillis() == PreferenceUtils.NO_TIME.getTimeInMillis()) {
            return;
        }

        final Intent intent = new Intent(ctx, ReminderReceiver.class);
        final PendingIntent pIntent =
                PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.RTC, calendar.getTimeInMillis(), pIntent);
        } else {
            am.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pIntent);
        }

        WidgetProvider.updateWidget(ctx);

    }

    public static void cancelReminderAlarm(Context ctx) {
        final Intent intent = new Intent(ctx, ReminderReceiver.class);
        final PendingIntent pIntent =
                PendingIntent.getBroadcast(ctx, 0, intent, 0);
        final AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pIntent);

        WidgetProvider.updateWidget(ctx);

    }

}
