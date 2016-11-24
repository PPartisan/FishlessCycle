package com.github.ppartisan.fishlesscycle.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.ApiColorChartItem;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils.DosageUnit;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.view.BorderedView;

import java.text.DecimalFormat;
import java.util.List;

import static com.github.ppartisan.fishlesscycle.util.ConversionUtils.MGL;
import static com.github.ppartisan.fishlesscycle.util.ConversionUtils.PPM;


public final class ApiColorChartAdapter extends RecyclerView.Adapter<ApiColorChartAdapter.ViewHolder> {

    private final DecimalFormat mFormat = new DecimalFormat("##.##");
    private final String mDoseUnit;
    private List<ApiColorChartItem> mItems;

    public ApiColorChartAdapter(Context context, List<ApiColorChartItem> items) {
        final @DosageUnit int unit = PreferenceUtils.getDosageUnitType(context);
        mDoseUnit = getDoseUnitString(context.getResources(), unit);
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dialog_api_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Resources res = holder.itemView.getResources();
        final ApiColorChartItem item = mItems.get(position);
        holder.view.setColors(item.color);
        holder.label.setText(
                res.getString(
                        R.string.two_string_template,
                        mFormat.format(item.value),
                        mDoseUnit
                )
        );
    }

    @Override
    public int getItemCount() {
        return (mItems == null) ? 0 : mItems.size();
    }

    public void setItems(List<ApiColorChartItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        BorderedView view;
        TextView label;

        public ViewHolder(View itemView) {
            super(itemView);

            view = (BorderedView) itemView.findViewById(R.id.radf_color);
            label = (TextView) itemView.findViewById(R.id.radf_label);

        }

    }

    private String getDoseUnitString(Resources res, @DosageUnit int doseUnit) {

        String unit = null;
        switch (doseUnit) {
            case MGL:
                unit = res.getString(R.string.unit_metric);
                break;
            case PPM:
                unit = res.getString(R.string.unit_imperial);
                break;
        }

        return unit;

    }

}
