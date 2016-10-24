package com.github.ppartisan.fishlesscycle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.ppartisan.fishlesscycle.adapter.DataAdapter;
import com.github.ppartisan.fishlesscycle.model.Data;
import com.github.ppartisan.fishlesscycle.util.DataUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;

import java.util.ArrayList;
import java.util.List;

public final class DetailFragment extends Fragment implements View.OnClickListener {

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private RecyclerView mRecyclerView;
    private DataAdapter mAdapter;

    private CombinedChart mChart;

    public static DetailFragment newInstance() {

        Bundle args = new Bundle();

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.fd_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mFab = (FloatingActionButton) view.findViewById(R.id.fd_fab);
        mFab.setOnClickListener(this);

        final List<Data> dummyData = buildDummyData();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fd_recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new DataAdapter(dummyData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        List<Entry> ammoniaEntries = new ArrayList<>(dummyData.size());
        List<Entry> nitriteEntries = new ArrayList<>(dummyData.size());
        List<Entry> nitrateEntries = new ArrayList<>(dummyData.size());

        int counter = 0;

        for (Data data : dummyData) {
            ammoniaEntries.add(new Entry(counter, (float)data.ammonia));
            nitriteEntries.add(new Entry(counter, (float)data.nitrite));
            nitrateEntries.add(new Entry(counter, (float)data.nitrate));
            counter++;
        }

        final int green300 = ContextCompat.getColor(getContext(), R.color.green_300);
        final int red300 = ContextCompat.getColor(getContext(), R.color.red_300);
        final int blue300 = ContextCompat.getColor(getContext(), R.color.blue_300);

        final int primaryDark = ContextCompat.getColor(getContext(), R.color.primary_dark);

        LineDataSet ammoniaSet = new LineDataSet(ammoniaEntries, "Ammonia");
        ammoniaSet.setColor(green300);
        ammoniaSet.setCircleColor(green300);
        ammoniaSet.setDrawCircleHole(false);
        ammoniaSet.setMode(LineDataSet.Mode.LINEAR);
        ammoniaSet.setLineWidth(2f);
        ammoniaSet.setDrawHighlightIndicators(true);
        ammoniaSet.setHighLightColor(primaryDark);
        ammoniaSet.enableDashedHighlightLine(10f, 10f, 0f);
        ammoniaSet.setDrawValues(false);

        LineDataSet nitriteSet = new LineDataSet(nitriteEntries, "Nitrite");
        nitriteSet.setColor(blue300);
        nitriteSet.setCircleColor(blue300);
        nitriteSet.setDrawCircleHole(false);
        nitriteSet.setMode(LineDataSet.Mode.LINEAR);
        nitriteSet.setLineWidth(2f);
        nitriteSet.setDrawHighlightIndicators(true);
        nitriteSet.setHighLightColor(primaryDark);
        nitriteSet.enableDashedHighlightLine(10f, 10f, 0f);
        nitriteSet.setDrawValues(false);

        LineDataSet nitrateSet = new LineDataSet(nitrateEntries, "Nitrate");
        nitrateSet.setColor(red300);
        nitrateSet.setCircleColor(red300);
        nitrateSet.setDrawCircleHole(false);
        nitrateSet.setMode(LineDataSet.Mode.LINEAR);
        nitrateSet.setLineWidth(2f);
        nitrateSet.setDrawHighlightIndicators(true);
        nitrateSet.setHighLightColor(primaryDark);
        nitrateSet.enableDashedHighlightLine(10f, 10f, 0f);
        nitrateSet.setDrawValues(false);

        CombinedData combinedData = new CombinedData();

        LineData lineData = new LineData(ammoniaSet, nitriteSet, nitrateSet);

        combinedData.setData(lineData);

        final IAxisValueFormatter axisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                final int index = dummyData.size() - (int) value - 1;
                return DataUtils.getReadableDateString(dummyData.get((index)).date);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        };

        mChart = (CombinedChart) view.findViewById(R.id.fd_chart);
        mChart.setDrawGridBackground(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setValueFormatter(axisValueFormatter);
        mChart.getXAxis().setAxisLineColor(Color.WHITE);
        mChart.setDescription(null);
        mChart.getXAxis().setTextColor(Color.WHITE);
        mChart.getAxisLeft().setTextColor(Color.WHITE);
        mChart.getAxisLeft().setAxisLineColor(Color.WHITE);
        mChart.getLegend().setEnabled(true);
        mChart.getLegend().setTextColor(Color.WHITE);
        //mChart.setGridBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_light));
        mChart.setData(combinedData);

        return view;
    }

    private static List<Data> buildDummyData() {

        List<Data> dataList = new ArrayList<>();

        long date = System.currentTimeMillis();

        dataList.add(new Data(0, date, 2, 0, 0));
        dataList.add(new Data(1, date -= 8.64e+7, 5, 0, 0));
        dataList.add(new Data(2, date -= 8.64e+7, 6, 0, 0));
        dataList.add(new Data(3, date -= 8.64e+7, 1, 2, 12));
        dataList.add(new Data(4, date -= 8.64e+7, 0, 4, 22));
        dataList.add(new Data(5, date -= 8.64e+7, 0, 10, 26));
        dataList.add(new Data(6, date -= 8.64e+7, 3, 5, 40));

        return dataList;

    }

    @Override
    public void onClick(View view) {
        //ToDo: Add New Entry to Data Table for Tank
        Log.d(getClass().getSimpleName(), view.toString() + " clicked");
    }
}
