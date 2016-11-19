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

public final class ReadingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;

    private DataRowCallbacks mCallbacks;
    private List<Reading> mReadings;

    public ReadingsAdapter(DataRowCallbacks callbacks, List<Reading> readings) {
        mCallbacks = callbacks;
        mReadings = readings;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        int layoutId;
        View v;
        RecyclerView.ViewHolder vh = null;

        switch (viewType) {
            case VIEW_TYPE_HEADER:
                layoutId = R.layout.data_row_heading;
                v = inflater.inflate(layoutId, parent, false);
                vh = new HeadingViewHolder(v);
                break;
            case VIEW_TYPE_CONTENT:
                layoutId = R.layout.data_row;
                v = inflater.inflate(layoutId, parent, false);
                vh = new ViewHolder(mCallbacks, v);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {

        if (h.getAdapterPosition() == 0) {
            return;
        }

        final ReadingsAdapter.ViewHolder holder = (ViewHolder) h;

        final Reading item = mReadings.get(--position);

        String dateText;
        if(item.isControl) {
            dateText = holder.itemView.getResources().getString(R.string.df_control);
        } else {
            dateText = ReadingUtils.getReadableDateString(item.date);
        }

        holder.date.setText(dateText);
        holder.ammonia.setText(String.valueOf((int)item.ammonia));
        holder.nitrite.setText(String.valueOf((int)item.nitrite));
        holder.nitrate.setText(String.valueOf((int)item.nitrate));

    }

    @Override
    public int getItemCount() {
        return mReadings == null ? 0 : mReadings.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    public void setDataItems(List<Reading> items) {
        mReadings = items;
    }

    public Reading getDataItem(int index) {
        return mReadings.get(index);
    }

    private static final class HeadingViewHolder extends RecyclerView.ViewHolder {

        HeadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private DataRowCallbacks mCallbacks;

        TextView date, ammonia, nitrite, nitrate;
        ImageButton overflow;

        ViewHolder(DataRowCallbacks callbacks, View itemView) {
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
            final PopupMenu menu = buildPopUpMenu(view);
            if(date.getText().equals(view.getResources().getString(R.string.df_control))) {
                menu.getMenu().removeItem(R.id.drm_action_delete);
                menu.getMenu().removeItem(R.id.drm_action_notes);
            }
            menu.show();
        }

        private PopupMenu buildPopUpMenu(View target) {

            PopupMenu menu = ViewUtils.buildPopUpMenu(target, R.menu.data_row_menu);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.drm_action_notes:
                            mCallbacks.onNotesClicked(getAdapterPosition()-1);
                            break;
                        case R.id.drm_action_edit:
                            mCallbacks.onEditClicked(getAdapterPosition()-1);
                            break;
                        case R.id.drm_action_delete:
                            mCallbacks.onDeleteClicked(getAdapterPosition()-1);
                            break;
                    }
                    return true;
                }
            });

            return menu;

        }



    }




}
