package com.github.ppartisan.fishlesscycle.strategy.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public final class SyncReceiver extends BroadcastReceiver {

    private SyncCallbacks mCallbacks;

    @SuppressWarnings("unused")
    public SyncReceiver(){}

    public SyncReceiver(SyncCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final List<SyncManager.DisplayData> data =
                intent.getParcelableArrayListExtra(SyncService.DISPLAY_DATA_LIST_EXTRA);
        mCallbacks.onSyncComplete(data);
    }

}
