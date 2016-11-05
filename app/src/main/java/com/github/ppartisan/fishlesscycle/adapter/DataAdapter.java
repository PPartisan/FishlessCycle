package com.github.ppartisan.fishlesscycle.adapter;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Reading;
import com.github.ppartisan.fishlesscycle.util.ReadingUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;

import java.util.List;

public final class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private DataRowCallbacks mCallbacks;
    private List<Reading> mReading;

    public DataAdapter(DataRowCallbacks callbacks, List<Reading> reading) {
        mCallbacks = callbacks;
        mReading = reading;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_row, parent, false);
        return new ViewHolder(mCallbacks, v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Reading item = mReading.get(position);

        holder.date.setText(ReadingUtils.getReadableDateString(item.date));
        holder.ammonia.setText(ReadingUtils.toOneDecimalPlace(item.ammonia));
        holder.nitrite.setText(ReadingUtils.toOneDecimalPlace(item.nitrite));
        holder.nitrate.setText(ReadingUtils.toOneDecimalPlace(item.nitrate));

    }

    @Override
    public int getItemCount() {
        return mReading == null ? 0 : mReading.size();
    }

    public void setDataItems(List<Reading> items) {
        mReading = items;
    }

    public void addDataItem(Reading item) {
        mReading.add(item);
        notifyItemInserted(mReading.size());
    }

    public Reading getDataItem(int index) {
        return mReading.get(index);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private DataRowCallbacks mCallbacks;

        TextView date, ammonia, nitrite, nitrate;
        ImageButton overflow;

        public ViewHolder(DataRowCallbacks callbacks, View itemView) {
            super(itemView);

            mCallbacks = callbacks;

            date = (TextView) itemView.findViewById(R.id.rd_date);
            ammonia = (TextView) itemView.findViewById(R.id.rd_ammonia);
            nitrite = (TextView) itemView.findViewById(R.id.rd_nitrite);
            nitrate = (TextView) itemView.findViewById(R.id.rd_nitrate);

            overflow = (ImageButton) itemView.findViewById(R.id.rd_overflow);
            overflow.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            buildPopUpMenu(view).show();
        }

        private PopupMenu buildPopUpMenu(View target) {

            PopupMenu menu = ViewUtils.buildPopUpMenu(target, R.menu.data_row_menu);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.drm_action_notes:
                            mCallbacks.onNotesClicked(getAdapterPosition());
                            break;
                        case R.id.drm_action_edit:
                            mCallbacks.onEditClicked(getAdapterPosition());
                            break;
                        case R.id.drm_action_delete:
                            mCallbacks.onDeleteClicked(getAdapterPosition());
                            break;
                    }
                    return true;
                }
            });

            return menu;

        }

    }


}
