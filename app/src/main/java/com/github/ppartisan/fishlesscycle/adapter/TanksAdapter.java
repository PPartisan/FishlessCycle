package com.github.ppartisan.fishlesscycle.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;

import java.util.List;

public final class TanksAdapter extends RecyclerView.Adapter<TanksAdapter.ViewHolder> {

    private List<Tank> mTanks;

    public TanksAdapter(List<Tank> tanks) {
        mTanks = tanks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tank_card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Tank tank = mTanks.get(position);
        final Resources res = holder.itemView.getResources();
        final @ConversionUtils.UnitType int type = ConversionUtils.METRIC;

        holder.image.setImageResource(R.mipmap.ic_launcher);

        holder.title.setText(tank.name);
        holder.lastUpdated.setText(res.getString(R.string.fm_last_updated_template, "20th October"));
        holder.ammonia.setText(ConversionUtils.getUnitFormattedString(res, 3, type));
        holder.nitrite.setText(ConversionUtils.getUnitFormattedString(res, 0, type));
        holder.nitrate.setText(ConversionUtils.getUnitFormattedString(res, 15, type));
        holder.nextUpdate.setText(res.getString(R.string.fm_next_update_template, "22nd October"));

    }

    @Override
    public int getItemCount() {
        return (mTanks == null) ? 0 : mTanks.size();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView image;
        final TextView title, lastUpdated, ammonia, nitrite, nitrate, nextUpdate;

        ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            image = (ImageView) itemView.findViewById(R.id.tcv_image);
            title = (TextView) itemView.findViewById(R.id.tcv_title);
            lastUpdated = (TextView) itemView.findViewById(R.id.tcv_last_updated);
            ammonia = (TextView) itemView.findViewById(R.id.tcv_ammonia);
            nitrite = (TextView) itemView.findViewById(R.id.tcv_nitrite);
            nitrate = (TextView) itemView.findViewById(R.id.tcv_nitrate);
            nextUpdate = (TextView) itemView.findViewById(R.id.tcv_next_update);

        }

        @Override
        public void onClick(View view) {
            Log.d(getClass().getSimpleName(), view.toString() + ": onClick");
        }
    }

}
