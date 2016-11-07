package com.github.ppartisan.fishlesscycle.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Reading;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils.DosageUnit;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils.VolumeUnit;
import com.github.ppartisan.fishlesscycle.util.TankUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;
import com.github.ppartisan.fishlesscycle.view.ShadowOverflowDrawable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public final class TanksAdapter extends RecyclerView.Adapter<TanksAdapter.ViewHolder> {

    private final SimpleDateFormat mUpdateFormat =
            new SimpleDateFormat("d MMMM", Locale.getDefault());

    private final TankCardCallbacks mCallbacks;
    private List<Tank> mTanks;
    private @DosageUnit
    int mDosageUnit;
    private @VolumeUnit int mVolumeUnit;

    public TanksAdapter(
            @NonNull TankCardCallbacks callbacks, List<Tank> tanks,
            @DosageUnit int dosageUnit,
            @VolumeUnit int volumeUnit
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

        Glide.with(ctx).load(tank.image)
                .listener(new ImageLoadCallbacks(holder.image, tank.image))
                .placeholder(R.drawable.tank_white)
                .into(holder.image);

        holder.title.setText(getTankNameString(ctx, tank));
        final CharSequence tankOptionsText = TankUtils.getTankOptionsText(ctx, tank);
        if (TextUtils.isEmpty(tankOptionsText)) {
            holder.options.setVisibility(View.GONE);
        } else {
            holder.options.setVisibility(View.VISIBLE);
            holder.options.setText(tankOptionsText);
        }

        final String tankStatus = res.getString(
                R.string.fm_cycle_stage_template, TankUtils.getStageString(res, tank.tankStatus)
        );
        holder.stage.setText(tankStatus);

        final Reading lastReading = tank.getLastReading();
//        final String lastReadingString = (lastReading == null)
//                ? "No Readings"
//                : mUpdateFormat.format(lastReading.date);

        String lastReadingString;
        int ammonia, nitrite, nitrate;

        if (lastReading == null) {
            lastReadingString = "No Readings";
            ammonia = nitrite = nitrate = 0;
        } else {
            lastReadingString = res.getString(
                    R.string.fm_last_updated_template,
                    mUpdateFormat.format(lastReading.date)
            );
            ammonia = (int)lastReading.ammonia;
            nitrite = (int)lastReading.nitrite;
            nitrate = (int)lastReading.nitrate;
        }

        holder.lastUpdated.setText(lastReadingString);
        holder.ammonia.setText(ConversionUtils.getUnitFormattedString(res, ammonia, mDosageUnit));
        holder.nitrite.setText(ConversionUtils.getUnitFormattedString(res, nitrite, mDosageUnit));
        holder.nitrate.setText(ConversionUtils.getUnitFormattedString(res, nitrate, mDosageUnit));
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

    public void setDosageUnitType(@DosageUnit int unitType) {
        mDosageUnit = unitType;
    }

    public void setVolumeUnit(@VolumeUnit int unitType) {
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

        final ImageButton overflow, infoStages;
        final ImageView image;
        final TextView title, options, lastUpdated, stage, ammonia, nitrite, nitrate, nextUpdate;

        final PopupMenu menu;

        ViewHolder(TankCardCallbacks callbacks, View itemView) {
            super(itemView);

            mCallbacks = callbacks;

            itemView.setOnClickListener(this);

            overflow = (ImageButton) itemView.findViewById(R.id.tcv_overflow);
            infoStages = (ImageButton) itemView.findViewById(R.id.tcv_stage_info);

            overflow.setImageDrawable(new ShadowOverflowDrawable(itemView.getResources()));

            menu = ViewUtils.buildPopUpMenu(overflow, R.menu.tank_card_menu);
            menu.setOnMenuItemClickListener(this);

            overflow.setOnClickListener(this);
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
                case R.id.mct_action_change_photo_camera:
                    mCallbacks.onChangePhotoCameraClick(getAdapterPosition());
                    return true;
                case R.id.mct_action_change_photo_gallery:
                    mCallbacks.onChangePhotoGalleryClick(getAdapterPosition());
                    return true;
                case R.id.mct_action_delete_tank:
                    mCallbacks.onDeleteTankClick(getAdapterPosition());
                    return true;
            }

            return false;
        }
    }

    private static final class ImageLoadCallbacks implements RequestListener<String, GlideDrawable> {

        private final ImageView mImage;
        private final String mPath;

        private ImageLoadCallbacks(ImageView image, String path) {
            mImage = image;
            mPath = path;
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            Glide.clear(mImage);
            mImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            final Context context = mImage.getContext();
            mImage.setBackgroundColor(ContextCompat.getColor(context, R.color.red_500));
            mImage.setColorFilter(ContextCompat.getColor(context, R.color.red_700));
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImage.clearColorFilter();
            if (isFirstResource) {
                //When loading for the first time images will not correctly fit imageview dimens.
                Glide.with(mImage.getContext()).load(mPath).into(mImage);
            }
            return false;
        }
    }

}
