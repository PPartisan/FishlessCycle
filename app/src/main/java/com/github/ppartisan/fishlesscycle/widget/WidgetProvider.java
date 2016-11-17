package com.github.ppartisan.fishlesscycle.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.github.ppartisan.fishlesscycle.MainActivity;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = WidgetProvider.class.getSimpleName();
    private static final String IMAGE_KEY = TAG + ".IMAGE";

    private SimpleDateFormat mFormat = null;

    private String mImagePath;

    private Rect mBounds = new Rect();

    @Override
    public void onUpdate(final Context context, AppWidgetManager manager, int[] appWidgetIds) {

        for(int widgetId : appWidgetIds) {

            final Intent intent = new Intent(context, MainActivity.class);
            final PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            final Bundle options = manager.getAppWidgetOptions(widgetId);

            setImageBounds(context.getResources(), options);

            if (mImagePath == null) {
                setDefaultImage(views);
            } else {
                final Bitmap image = ViewUtils.getSizedBitmapFromPath(
                        mImagePath, mBounds.height(), mBounds.width()
                );
                if (image == null) {
                    setDefaultImage(views);
                } else {
                    views.setImageViewBitmap(R.id.w_image, image);
                }
            }

            views.setTextViewText(R.id.w_content, getContentText(context));
            views.setOnClickPendingIntent(R.id.w_parent, pIntent);

            pushWidgetUpdate(context, views);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mImagePath = intent.getStringExtra(IMAGE_KEY);
        if(mImagePath == null) mImagePath = PreferenceUtils.getWidgetImagePath(context);
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        mImagePath = PreferenceUtils.getWidgetImagePath(context);
    }

    private void setImageBounds(Resources res, Bundle options) {

        int width = ViewUtils.dpToPx(options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH));
        int height = ViewUtils.dpToPx(options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT));

        if(width <= 0) {
            width = res.getDimensionPixelSize(R.dimen.w_min_width);
        }

        if (height <= 0) {
            height = res.getDimensionPixelOffset(R.dimen.w_min_height);
        }

        mBounds.set(0,0,width,height);

    }

    //todo Replace with a default image
    private void setDefaultImage(RemoteViews views) {
        views.setImageViewResource(R.id.w_image, R.drawable.capsule_default);
    }

    private String getContentText(Context context) {
        final long time = PreferenceUtils.getReminderTime(context).getTimeInMillis();
        Log.e(TAG, "Time: " + time);
        return (time == PreferenceUtils.NO_TIME.getTimeInMillis())
                ? context.getString(R.string.w_no_reminder)
                : context.getString(
                R.string.fm_next_update_template, getFormat(context.getResources()).format(time)
        );
    }

    private SimpleDateFormat getFormat(Resources res) {
        if (mFormat == null) {
            mFormat = new SimpleDateFormat(res.getString(R.string.w_date_format), Locale.getDefault());
        }
        return mFormat;
    }

    public static void updateWidget(Context context, String image) {

        final Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        final int[] ids = new int[] { R.xml.widget_provider };
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra(IMAGE_KEY, image);

        context.sendBroadcast(intent);

    }

    public static void updateWidget(Context context) {
        String path = PreferenceUtils.getWidgetImagePath(context);
        updateWidget(context, path);
    }

    private static void pushWidgetUpdate(Context context, RemoteViews views) {
        final ComponentName widget = new ComponentName(context, WidgetProvider.class);
        final AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(widget, views);
    }

}
