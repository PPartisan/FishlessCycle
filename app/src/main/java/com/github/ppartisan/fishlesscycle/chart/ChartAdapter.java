package com.github.ppartisan.fishlesscycle.chart;

import android.os.Bundle;

import com.github.ppartisan.fishlesscycle.model.Reading;

import java.util.List;

public interface ChartAdapter {

    void setData(List<Reading> reading);
    void addDataItem(Reading item);
    void showLineChart();
    void showBarChat();
    void switchChartType();
    void onSaveInstanceState(Bundle bundle);
    void onRestoreInstanceState(Bundle bundle);

}
