package com.github.ppartisan.fishlesscycle.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Tank;

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

        Tank tank = mTanks.get(position);

        holder.image.setImageResource(R.mipmap.ic_launcher);

        holder.title.setText(tank.name);
        holder.lastUpdated.setText("Last updated on 20th October");
        holder.ammonia.setText("3 mg/L");
        holder.nitrite.setText("0 mg/L");
        holder.nitrate.setText("15 mg/L");
        holder.nextUpdate.setText("Next Upate On 22nd October");

    }

    @Override
    public int getItemCount() {
        return (mTanks == null) ? 0 : mTanks.size();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView image;
        final TextView title, lastUpdated, ammonia, nitrite, nitrate, nextUpdate;

        ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.tcv_image);
            title = (TextView) itemView.findViewById(R.id.tcv_title);
            lastUpdated = (TextView) itemView.findViewById(R.id.tcv_last_updated);
            ammonia = (TextView) itemView.findViewById(R.id.tcv_ammonia);
            nitrite = (TextView) itemView.findViewById(R.id.tcv_nitrite);
            nitrate = (TextView) itemView.findViewById(R.id.tcv_nitrate);
            nextUpdate = (TextView) itemView.findViewById(R.id.tcv_next_update);

        }

    }

}
