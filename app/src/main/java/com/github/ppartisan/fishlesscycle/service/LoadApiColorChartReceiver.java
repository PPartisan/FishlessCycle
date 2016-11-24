package com.github.ppartisan.fishlesscycle.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.ppartisan.fishlesscycle.model.ApiColorChartItem;

import java.util.List;

import static com.github.ppartisan.fishlesscycle.service.LoadApiColorChartService.API_COLOR_CHART_ITEMS_EXTRA;

public final class LoadApiColorChartReceiver extends BroadcastReceiver {

    private OnApiColorChartReadyListener mListener;

    public LoadApiColorChartReceiver() {}

    public LoadApiColorChartReceiver(OnApiColorChartReadyListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final List<ApiColorChartItem> items =
                intent.getParcelableArrayListExtra(API_COLOR_CHART_ITEMS_EXTRA);
        mListener.onApiColorChartReady(items);
    }

    public interface OnApiColorChartReadyListener {
        void onApiColorChartReady(List<ApiColorChartItem> items);
    }

}
