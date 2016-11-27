package com.github.ppartisan.fishlesscycle.strategy.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

public final class SyncService extends IntentService {

    private static final String TAG = SyncService.class.getSimpleName();
    public static final String DISPLAY_DATA_LIST_EXTRA = TAG + ".LIST_EXTRA";
    public static final String ACTION_COMPLETE = TAG + ".ACTION_COMPLETE";

    private final SyncManager mManager = new SyncManager();

    public SyncService() {
        super(TAG);
    }

    public SyncService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ArrayList<SyncManager.DisplayData> data = mManager.executeCopy(getContentResolver());
        Intent i = new Intent(ACTION_COMPLETE);
        i.putParcelableArrayListExtra(DISPLAY_DATA_LIST_EXTRA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

    }

}
