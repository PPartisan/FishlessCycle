package com.github.ppartisan.fishlesscycle.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.ppartisan.fishlesscycle.model.ImagePack;

public final class LoadImagePackReceiver extends BroadcastReceiver {

    private OnImagePackReadyListener mListener;

    public LoadImagePackReceiver() {}

    public LoadImagePackReceiver(OnImagePackReadyListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final ImagePack pack = intent.getParcelableExtra(LoadImagePackService.IMAGE_PACK_EXTRA);
        mListener.onImagePackReady(pack);
    }

    public interface OnImagePackReadyListener {
        void onImagePackReady(ImagePack pack);
    }

}
