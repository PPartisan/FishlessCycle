package com.github.ppartisan.fishlesscycle.strategy.sync;

import java.util.List;

interface SyncCallbacks {

    void onSyncComplete(List<SyncManager.DisplayData> data);

}
