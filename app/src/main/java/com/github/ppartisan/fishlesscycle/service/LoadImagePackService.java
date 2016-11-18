package com.github.ppartisan.fishlesscycle.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;

import com.github.ppartisan.fishlesscycle.data.Contract.TankEntry;
import com.github.ppartisan.fishlesscycle.data.Provider;
import com.github.ppartisan.fishlesscycle.model.ImagePack;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;

public final class LoadImagePackService extends IntentService {

    private static final String TAG = LoadImagePackService.class.getSimpleName();
    public static final String ACTION_COMPLETE = TAG + ".ACTION_COMPLETE";
    public static final String IMAGE_PACK_EXTRA = TAG + ".IMAGE_PACK_EXTRA";

    public LoadImagePackService(){
        this(TAG);
    }

    public LoadImagePackService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Cursor c = null;

        try {
            c = getContentResolver().query(
                    TankEntry.CONTENT_URI, Provider.IMAGES_PROJECTION, null, null, null
            );
            final ImagePack pack = PreferenceUtils.buildImagePack(c);
            final Intent i = new Intent(ACTION_COMPLETE);
            i.putExtra(IMAGE_PACK_EXTRA, pack);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        } finally {
            if(c!=null && !c.isClosed()) c.close();
        }

    }

    public static void launchLoadImagePackService(Context context) {
        context.startService(new Intent(context, LoadImagePackService.class));
    }

}
