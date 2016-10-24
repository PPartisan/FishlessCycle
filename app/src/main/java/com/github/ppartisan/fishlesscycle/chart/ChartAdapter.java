package com.github.ppartisan.fishlesscycle.chart;

import android.os.Bundle;

import com.github.ppartisan.fishlesscycle.model.Data;

import java.util.List;

public interface ChartAdapter {

    void setData(List<Data> data);
    void addDataItem(Data item);
    void showLineChart();
    void showBarChat();
    void switchChartType();
    void onSaveInstanceState(Bundle bundle);
    void onRestoreInstanceState(Bundle bundle);

}
