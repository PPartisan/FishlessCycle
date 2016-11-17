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
import android.widget.RemoteViews;

import com.github.ppartisan.fishlesscycle.MainActivity;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;

public final class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = WidgetProvider.class.getSimpleName();
    private static final String TEXT_KEY = TAG + ".TEXT";
    private static final String IMAGE_KEY = TAG + ".IMAGE";

    private String mContent;
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

            final Bitmap image = ViewUtils.getSizedBitmapFromPath(
                    mImagePath, mBounds.height(), mBounds.width()
            );

            views.setTextViewText(R.id.w_content, mContent);
            views.setOnClickPendingIntent(R.id.w_parent, pIntent);
            views.setImageViewBitmap(R.id.w_image, image);

            pushWidgetUpdate(context, views);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        mContent = intent.getStringExtra(TEXT_KEY);
        mImagePath = intent.getStringExtra(IMAGE_KEY);

        super.onReceive(context, intent);

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

    public static void updateWidget(Context context, String content, String image) {

        final Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        final int[] ids = new int[] { R.xml.widget_provider };
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra(TEXT_KEY, content);
        intent.putExtra(IMAGE_KEY, image);

        context.sendBroadcast(intent);

    }

    private static void pushWidgetUpdate(Context context, RemoteViews views) {
        final ComponentName widget = new ComponentName(context, WidgetProvider.class);
        final AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(widget, views);
    }

}
