package com.github.ppartisan.fishlesscycle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.ppartisan.fishlesscycle.adapter.DataAdapter;
import com.github.ppartisan.fishlesscycle.chart.ChartAdapter;
import com.github.ppartisan.fishlesscycle.chart.ChartAdapterImpl;
import com.github.ppartisan.fishlesscycle.model.Reading;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class DetailFragment extends Fragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private RecyclerView mRecyclerView;
    private DataAdapter mAdapter;

    private CombinedChart mChart;
    private ChartAdapter mChartAdapter;

    private AppBarLayout mAppBar;

    public static DetailFragment newInstance() {

        Bundle args = new Bundle();

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mAppBar = (AppBarLayout) view.findViewById(R.id.fd_app_bar);

        mToolbar = (Toolbar) view.findViewById(R.id.fd_toolbar);
        mToolbar.inflateMenu(R.menu.detail_menu);
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mFab = (FloatingActionButton) view.findViewById(R.id.fd_fab);
        mFab.setOnClickListener(this);

        final List<Reading> dummyReading = buildDummyData();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fd_recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new DataAdapter(dummyReading);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mChart = (CombinedChart) view.findViewById(R.id.fd_chart);
        mChartAdapter = new ChartAdapterImpl(mChart);
        mChartAdapter.setData(dummyReading);

        if (savedInstanceState != null) {
            mChartAdapter.onRestoreInstanceState(savedInstanceState);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mChartAdapter.onSaveInstanceState(outState);
    }

    private static List<Reading> buildDummyData() {

        List<Reading> readingList = new ArrayList<>();

        long date = System.currentTimeMillis();
        final int count = 31;
        final Random rnd = new Random();

        for (int i = 0; i < count; i++) {
            final long adjDate = (long)(date - ((count-i)*8.64e+7));
            final int ammonia = rnd.nextInt(8);
            final int nitrite = rnd.nextInt(15);
            final int nitrate = rnd.nextInt(50);
            readingList.add(new Reading(i, adjDate, ammonia, nitrite, nitrate));
        }

        return readingList;

    }

    @Override
    public void onClick(View view) {
        //ToDo: Add New Entry to Reading Table for Tank
        Log.d(getClass().getSimpleName(), view.toString() + " clicked");
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.md_action_switch_graph) {
            mChartAdapter.switchChartType();
            return true;
        }
        return false;
    }
}
