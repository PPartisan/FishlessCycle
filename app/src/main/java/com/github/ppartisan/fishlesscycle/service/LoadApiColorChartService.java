package com.github.ppartisan.fishlesscycle.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry.Categories;
import com.github.ppartisan.fishlesscycle.model.ApiColorChartItem;
import com.github.ppartisan.fishlesscycle.ui.ApiColorChartDialogFragment;
import com.github.ppartisan.fishlesscycle.util.ApiColorChatUtils;

import java.util.ArrayList;

public final class LoadApiColorChartService extends IntentService {

    private static final String TAG = LoadApiColorChartService.class.getSimpleName();
    public static final String ACTION_COMPLETE = TAG + ".ACTION_COMPLETE";
    public static final String API_COLOR_CHART_ITEMS_EXTRA = TAG  +".API_COLOR_CHART_EXTRA";

    public LoadApiColorChartService() {
        super(TAG);
    }

    public LoadApiColorChartService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final @Categories int category =
                intent.getIntExtra(
                        ApiColorChartDialogFragment.CATEGORY_KEY,
                        Contract.ApiColorChartEntry.AMMONIA
                );
        final Uri uri  = Contract.ApiColorChartEntry.buildApiColorChartUri(category);

        Cursor c = null;

        try {
            c = getContentResolver().query(uri, null, null, null, null);
            final ArrayList<ApiColorChartItem> items = ApiColorChatUtils.buildApiColorChartList(c);
            final Intent i = new Intent(ACTION_COMPLETE);
            i.putParcelableArrayListExtra(API_COLOR_CHART_ITEMS_EXTRA, items);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }

    }

    public static void launchLoadApiColorChartService(Context c, @Categories int category) {
        final Intent i = new Intent(c, LoadApiColorChartService.class);
        i.putExtra(ApiColorChartDialogFragment.CATEGORY_KEY, category);
        c.startService(i);
    }

}
