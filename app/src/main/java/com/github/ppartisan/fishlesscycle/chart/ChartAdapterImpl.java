package com.github.ppartisan.fishlesscycle.chart;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Reading;
import com.github.ppartisan.fishlesscycle.util.ReadingUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartAdapterImpl implements ChartAdapter, OnChartValueSelectedListener {

    private static final String TAG = ChartAdapterImpl.class.getCanonicalName();

    private static final String CHART_MODE_KEY = TAG + ".CHART_MODE_KEY";
    private static final String SELECTED_ITEM_KEY = TAG + ".SELECTED_ITEM_KEY";
    private static final String X_AXIS_KEY = TAG + ".X_AXIS_KEY";

    private static final float LINE_WIDTH = 2f;
    private static final float LINE_DASH_WIDTH = 10f;

    private final String keyAmmonia, keyNitrite, keyNitrate;
    private final int green300, red300, blue300, lineDashColor;

    private float lineMinX = -1, lineMaxX = -1;

    private final CombinedChart mChart;

    private List<Reading> mReading;

    private LineData mLineData;
    private BarData mBarData;

    private final CombinedData mCombinedData;

    private @ChartAdapterImpl.ChartMode int chartMode = LINE;
    private ParcelableHighlightData highlight = null;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LINE, BAR})
    @interface ChartMode {}
    static final int LINE = 5;
    static final int BAR = 50;

    public ChartAdapterImpl(@NonNull CombinedChart chart) {

        mChart = chart;
        initChart(mChart);

        Context context = chart.getContext();

        green300 = ContextCompat.getColor(context, R.color.green_300);
        red300 = ContextCompat.getColor(context, R.color.red_300);
        blue300 = ContextCompat.getColor(context, R.color.blue_300);
        lineDashColor = ContextCompat.getColor(context, R.color.primary_dark);

        final String[] labels = context.getResources().getStringArray(R.array.chart_labels);
        keyAmmonia = labels[0];
        keyNitrite = labels[1];
        keyNitrate = labels[2];

        mCombinedData = new CombinedData();

        mReading = new ArrayList<>();
        highlight = new ParcelableHighlightData();

    }

    @Override
    public void setData(List<Reading> reading) {

        if (reading == null) return;

        mReading = reading;

        switch (chartMode) {
            case LINE:
                mLineData = null;
                mCombinedData.setData(getLineData());
                mCombinedData.notifyDataChanged();
                break;
            case BAR:
                mBarData = null;
                mCombinedData.setData(getBarData());
                mCombinedData.notifyDataChanged();
                break;
        }

        mChart.setData(mCombinedData);
        mChart.invalidate();

        if (highlight != null && highlight.dataSetIndex >= 0) {
            Highlight h = new Highlight(highlight.x, highlight.dataSetIndex, highlight.stackIndex);
            h.setDataIndex(highlight.dataIndex);
            mChart.highlightValue(h);
        }

    }

    @Override
    public void addDataItem(Reading item) {
    }

    @Override
    public void showLineChart() {
        chartMode = LINE;
        final BarData barData = mCombinedData.getBarData();
        if (barData != null) barData.clearValues();
        mCombinedData.setData(getLineData());
        mChart.setData(mCombinedData);
        mChart.invalidate();
    }

    @Override
    public void showBarChat() {
        chartMode = BAR;
        final LineData lineData = mCombinedData.getLineData();
        if (lineData != null) lineData.clearValues();
        mCombinedData.setData(getBarData());
        mChart.setData(mCombinedData);
        mChart.invalidate();
    }

    @Override
    public void switchChartType() {
        switch (chartMode) {
            case LINE:
                showBarChat();
                break;
            case BAR:
                showLineChart();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt(CHART_MODE_KEY, chartMode);
        bundle.putParcelable(SELECTED_ITEM_KEY, highlight);
        bundle.putFloatArray(X_AXIS_KEY, new float[] { lineMinX, lineMaxX });
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        final @ChartMode int key = bundle.getInt(CHART_MODE_KEY, LINE);
        if(key != chartMode) {
            switchChartType();
        }
        highlight = bundle.getParcelable(SELECTED_ITEM_KEY);
        final float[] xAxisVals = bundle.getFloatArray(X_AXIS_KEY);
        if (xAxisVals != null) {
            lineMinX = xAxisVals[0];
            lineMaxX = xAxisVals[1];
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        highlight.x = h.getX();
        highlight.y = h.getY();
        highlight.dataSetIndex = h.getDataSetIndex();
        highlight.dataIndex = h.getDataIndex();
        highlight.stackIndex = h.getStackIndex();
    }

    @Override
    public void onNothingSelected() {
        highlight.x = highlight.y = 0;
        highlight.dataSetIndex = highlight.dataIndex = highlight.stackIndex = -1;
    }

    private void initChart(CombinedChart chart) {

        chart.setDrawGridBackground(false);
        chart.setDescription(null);
        chart.setOnChartValueSelectedListener(this);

        final YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setAxisLineColor(Color.WHITE);
        yAxisLeft.setTextColor(Color.WHITE);

        final YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        final XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new XAxisFormatter());
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextColor(Color.WHITE);

        final Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

    }

    private LineData getLineData() {
        if (mLineData == null || mLineData.getEntryCount() == 0) {
            Map<String, List<Entry>> entryMap = buildEntryMapFromDataList(mReading);
            mLineData = buildLineDataFromEntryMap(entryMap);

            final float min = mChart.getXChartMin();
            final float max = mChart.getXChartMax();

            mChart.getXAxis().setAxisMinimum(0);
            mChart.getXAxis().setAxisMaximum(mReading.size()-1);
        }
        return mLineData;
    }

    private BarData getBarData() {
        if (mBarData == null || mBarData.getEntryCount() == 0) {
            List<BarEntry> barEntries = buildBarEntryListFromDataList(mReading);
            final BarDataSet set = new BarDataSet(barEntries, "");

            final int[] colors = new int[] { green300, blue300, red300 };
            final String[] labels = new String[] { keyAmmonia, keyNitrite, keyNitrate };

            setBarDataSetAttributes(set, colors, labels, lineDashColor);

            mBarData = new BarData(set);
            mBarData.setBarWidth(0.4f);
            mChart.getXAxis().setAxisMinimum(-mBarData.getBarWidth()/2);
            mChart.getXAxis().setAxisMaximum(mBarData.getEntryCount() - mBarData.getBarWidth()/2);
            mBarData.notifyDataChanged();
        }
        return mBarData;
    }

    private Map<String, List<Entry>> buildEntryMapFromDataList(@NonNull List<Reading> reading) {

        final int size = reading.size();

        List<Entry> ammonia = new ArrayList<>(size);
        List<Entry> nitrite = new ArrayList<>(size);
        List<Entry> nitrate = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            final Reading item = reading.get(i);
            ammonia.add(new Entry(i, item.ammonia));
            nitrite.add(new Entry(i, item.nitrite));
            nitrate.add(new Entry(i, item.nitrate));
        }

        Map<String, List<Entry>> entriesMap = new HashMap<>(3);
        entriesMap.put(keyAmmonia, ammonia);
        entriesMap.put(keyNitrite, nitrite);
        entriesMap.put(keyNitrate, nitrate);

        return entriesMap;

    }

    private List<BarEntry> buildBarEntryListFromDataList(@NonNull List<Reading> reading) {

        final int size = reading.size();

        List<BarEntry> stackedBars = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            final Reading item = reading.get(i);
            final float[] values = new float[] { item.ammonia, item.nitrite, item.nitrate };
            stackedBars.add(new BarEntry(i, values));
        }

        return stackedBars;

    }

    private LineData buildLineDataFromEntryMap(Map<String, List<Entry>> map) {

        LineDataSet ammoniaSet = new LineDataSet(map.get(keyAmmonia), keyAmmonia);
        LineDataSet nitriteSet = new LineDataSet(map.get(keyNitrite), keyNitrite);
        LineDataSet nitrateSet = new LineDataSet(map.get(keyNitrate), keyNitrate);

        setLineDataSetAttributes(ammoniaSet, green300, lineDashColor);
        setLineDataSetAttributes(nitriteSet, blue300, lineDashColor);
        setLineDataSetAttributes(nitrateSet, red300, lineDashColor);

        return new LineData(ammoniaSet, nitriteSet, nitrateSet);

    }

    private static void setBarDataSetAttributes(BarDataSet set, int[] colors, String[] labels, int dashColor) {
        set.setColors(colors);
        set.setStackLabels(labels);
        set.setHighLightColor(dashColor);
        set.setDrawValues(false);
    }

    private static void setLineDataSetAttributes(LineDataSet set, int color, int dashColor) {

        set.setColor(color);
        set.setCircleColor(color);
        set.setDrawCircleHole(false);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setLineWidth(LINE_WIDTH);
        set.setDrawHighlightIndicators(true);
        set.setHighLightColor(dashColor);
        set.enableDashedHighlightLine(LINE_DASH_WIDTH, LINE_DASH_WIDTH, 0f);
        set.setDrawValues(false);

    }

    private final class XAxisFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            final int index = (int) value;
            if (mReading == null || mReading.isEmpty() || index < 0 || index > mReading.size()-1) {
                return "";
            }
            return ReadingUtils.getReadableDateString(mReading.get((int) value).date);
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

    private static final class ParcelableHighlightData implements Parcelable{

        float x,y ;
        int dataSetIndex, dataIndex, stackIndex;

        ParcelableHighlightData() {
            x = y = 0;
            dataSetIndex = dataIndex = stackIndex = -1;
        }

        ParcelableHighlightData(Parcel in) {
            x = in.readFloat();
            y = in.readFloat();
            dataSetIndex = in.readInt();
            dataIndex = in.readInt();
            stackIndex = in.readInt();
        }

        public static final Creator<ParcelableHighlightData> CREATOR = new Creator<ParcelableHighlightData>() {
            @Override
            public ParcelableHighlightData createFromParcel(Parcel in) {
                return new ParcelableHighlightData(in);
            }

            @Override
            public ParcelableHighlightData[] newArray(int size) {
                return new ParcelableHighlightData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeFloat(x);
            parcel.writeFloat(y);
            parcel.writeInt(dataSetIndex);
            parcel.writeInt(dataIndex);
            parcel.writeInt(stackIndex);
        }
    }

}
