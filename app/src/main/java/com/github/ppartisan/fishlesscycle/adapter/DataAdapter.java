package com.github.ppartisan.fishlesscycle.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Data;
import com.github.ppartisan.fishlesscycle.util.DataUtils;

import java.util.List;


public final class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private List<Data> mData;

    public DataAdapter(List<Data> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Data item = mData.get(position);

        holder.date.setText(DataUtils.getReadableDateString(item.date));
        holder.ammonia.setText(DataUtils.toOneDecimalPlace(item.ammonia));
        holder.nitrite.setText(DataUtils.toOneDecimalPlace(item.nitrite));
        holder.nitrate.setText(DataUtils.toOneDecimalPlace(item.nitrate));

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setDataItems(List<Data> items) {
        mData = items;
    }

    public void addDataItem(Data item) {
        mData.add(item);
        notifyItemInserted(mData.size());
    }

    public Data getDataItem(int index) {
        return mData.get(index);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView date, ammonia, nitrite, nitrate;
        ImageButton overflow;

        public ViewHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.rd_date);
            ammonia = (TextView) itemView.findViewById(R.id.rd_ammonia);
            nitrite = (TextView) itemView.findViewById(R.id.rd_nitrite);
            nitrate = (TextView) itemView.findViewById(R.id.rd_nitrate);

            overflow = (ImageButton) itemView.findViewById(R.id.rd_overflow);
            overflow.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //ToDo; Show Pop-Up menu to view notes, edit or delete row
            Log.d(getClass().getSimpleName(), view.toString() + " clicked");
        }

    }

}
