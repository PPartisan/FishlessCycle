package com.github.ppartisan.fishlesscycle.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public final class TanksAdapter extends RecyclerView.Adapter<TanksAdapter.ViewHolder> {

    private final TankCardCallbacks mCallbacks;
    private List<Tank> mTanks;
    private @ConversionUtils.UnitType int mDosageUnit;
    private @PreferenceUtils.VolumeUnit int mVolumeUnit;

    public TanksAdapter(
            @NonNull TankCardCallbacks callbacks, List<Tank> tanks,
            @ConversionUtils.UnitType int dosageUnit,
            @PreferenceUtils.VolumeUnit int volumeUnit
    ) {
        mCallbacks = callbacks;
        mTanks = tanks;
        mDosageUnit = dosageUnit;
        mVolumeUnit = volumeUnit;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tank_card_view, parent, false);
        return new ViewHolder(mCallbacks, v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Tank tank = mTanks.get(position);
        final Context ctx = holder.itemView.getContext();
        final Resources res = ctx.getResources();

        Picasso.with(ctx).load(R.drawable.test_card_image)
                .placeholder(R.drawable.tank_white)
                .into(holder.image, new ImageLoadCallbacks(holder.image));

        holder.title.setText(getTankNameString(ctx, tank));
        holder.options.setText(TankUtils.getTankOptionsText(ctx, tank));

        final String tankStatus = res.getString(
                R.string.fm_cycle_stage_template, TankUtils.getStageString(res, tank.tankStatus)
        );
        holder.stage.setText(tankStatus);

        holder.lastUpdated.setText(res.getString(R.string.fm_last_updated_template, "20th October"));
        holder.ammonia.setText(ConversionUtils.getUnitFormattedString(res, 3, mDosageUnit));
        holder.nitrite.setText(ConversionUtils.getUnitFormattedString(res, 0, mDosageUnit));
        holder.nitrate.setText(ConversionUtils.getUnitFormattedString(res, 15, mDosageUnit));
        holder.nextUpdate.setText(res.getString(R.string.fm_next_update_template, "22nd October"));

    }

    @Override
    public int getItemCount() {
        return (mTanks == null) ? 0 : mTanks.size();
    }

    public void updateTanksList(List<Tank> tanks) {
        mTanks = tanks;
        notifyDataSetChanged();
    }

    public void setDosageUnitType(@ConversionUtils.UnitType int unitType) {
        mDosageUnit = unitType;
    }

    public void setVolumeUnit(@PreferenceUtils.VolumeUnit int unitType) {
        mVolumeUnit = unitType;
    }

    private String getTankNameString(Context ctx, Tank tank) {

        final String volUnitString =
                TankUtils.getAbbreviatedVolumeUnit(ctx.getResources(), mVolumeUnit).toLowerCase();

        final int volume = (int) Math.ceil(
                TankUtils.getTankVolumeInLitresAsUserUnitPreference(
                        tank.volumeInLitres, mVolumeUnit
                )
        );
        return ctx.getString(R.string.fm_tank_name_template, tank.name, volume, volUnitString);
    }

    public Tank getTank(int index) {
        return mTanks.get(index);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            PopupMenu.OnMenuItemClickListener {

        private final TankCardCallbacks mCallbacks;

        final ImageButton overflow, infoOptions, infoStages;
        final ImageView image;
        final TextView title, options, lastUpdated, stage, ammonia, nitrite, nitrate, nextUpdate;

        final PopupMenu menu;

        ViewHolder(TankCardCallbacks callbacks, View itemView) {
            super(itemView);

            mCallbacks = callbacks;

            itemView.setOnClickListener(this);

            overflow = (ImageButton) itemView.findViewById(R.id.tcv_overflow);
            infoOptions = (ImageButton) itemView.findViewById(R.id.tcv_options_info);
            infoStages = (ImageButton) itemView.findViewById(R.id.tcv_stage_info);

            menu = ViewUtils.buildPopUpMenu(overflow, R.menu.tank_card_menu);
            menu.setOnMenuItemClickListener(this);

            overflow.setOnClickListener(this);
            infoOptions.setOnClickListener(this);
            infoStages.setOnClickListener(this);

            image = (ImageView) itemView.findViewById(R.id.tcv_image);

            title = (TextView) itemView.findViewById(R.id.tcv_title);
            options = (TextView) itemView.findViewById(R.id.tcv_options);
            lastUpdated = (TextView) itemView.findViewById(R.id.tcv_last_updated);
            stage = (TextView) itemView.findViewById(R.id.tcv_stage);
            ammonia = (TextView) itemView.findViewById(R.id.tcv_ammonia);
            nitrite = (TextView) itemView.findViewById(R.id.tcv_nitrite);
            nitrate = (TextView) itemView.findViewById(R.id.tcv_nitrate);
            nextUpdate = (TextView) itemView.findViewById(R.id.tcv_next_update);

        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.tcv_parent:
                    mCallbacks.onCardClick(getAdapterPosition());
                    break;
                case R.id.tcv_overflow:
                    menu.show();
                    break;
            }

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.mct_action_edit_tank:
                    mCallbacks.onEditTankClick(getAdapterPosition());
                    return true;
                case R.id.mct_action_add_change_photo:
                    mCallbacks.onChangePhotoClick(getAdapterPosition());
                    return true;
                case R.id.mct_action_delete_tank:
                    mCallbacks.onDeleteTankClick(getAdapterPosition());
                    return true;
            }

            return false;
        }
    }

    private static final class ImageLoadCallbacks implements Callback {

        private final ImageView mImage;

        private ImageLoadCallbacks(ImageView imageView) {
            mImage = imageView;
        }

        @Override
        public void onSuccess() {
            mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImage.clearColorFilter();
        }

        @Override
        public void onError() {
            mImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mImage.setBackgroundColor(ContextCompat.getColor(mImage.getContext(), R.color.red_500));
            mImage.setColorFilter(ContextCompat.getColor(mImage.getContext(), R.color.red_700));
        }

    }

}
