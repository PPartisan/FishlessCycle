package com.github.ppartisan.fishlesscycle.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.adapter.ApiColorChartAdapter;
import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry;
import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry.Categories;
import com.github.ppartisan.fishlesscycle.model.ApiColorChartItem;
import com.github.ppartisan.fishlesscycle.service.LoadApiColorChartReceiver;
import com.github.ppartisan.fishlesscycle.service.LoadApiColorChartService;

import java.util.List;

public final class ApiColorChartDialogFragment extends DialogFragment implements LoadApiColorChartReceiver.OnApiColorChartReadyListener {

    private static final String TAG = ApiColorChartDialogFragment.class.getSimpleName();
    public static final String CATEGORY_KEY = TAG + ".CATEGORY_KEY";

    private ApiColorChartAdapter mAdapter;
    private LoadApiColorChartReceiver mBroadcastReceiver;

    public static ApiColorChartDialogFragment newInstance(@Categories int category) {

        Bundle args = new Bundle();
        args.putInt(CATEGORY_KEY, category);

        ApiColorChartDialogFragment fragment = new ApiColorChartDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_dialog_api_color, container, false);

        final TextView title = (TextView) v.findViewById(R.id.fdac_title);
        title.setText(getTitle());

        final ImageButton dismiss = (ImageButton) v.findViewById(R.id.fdac_dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        final RecyclerView recycler = (RecyclerView) v.findViewById(R.id.fdac_recycler);
        mAdapter = new ApiColorChartAdapter(getContext(), null);
        recycler.setAdapter(mAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mBroadcastReceiver = new LoadApiColorChartReceiver(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter filter = new IntentFilter(LoadApiColorChartService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, filter);
        LoadApiColorChartService.launchLoadApiColorChartService(getContext(), getCategory());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    private @Categories int getCategory() {
        final @Categories int category = getArguments().getInt(CATEGORY_KEY);
        return category;
    }

    private String getTitle() {

        final String[] titles = getResources().getStringArray(R.array.chart_labels);
        final @Categories int cat = getCategory();
        int index = 0;

        switch (cat) {
            case ApiColorChartEntry.AMMONIA:
                index = 0;
                break;
            case ApiColorChartEntry.NITRITE:
                index = 1;
                break;
            case ApiColorChartEntry.NITRATE:
                index = 2;
                break;
        }

        return titles[index];

    }

    @Override
    public void onApiColorChartReady(List<ApiColorChartItem> items) {
        mAdapter.setItems(items);
    }

}
