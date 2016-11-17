package com.github.ppartisan.fishlesscycle.adapter;

import android.view.View;

public interface TankCardCallbacks {

    void onCardClick(TanksAdapter.ViewHolder vh, int position);
    void onEditTankClick(View v, int position);
    void onDeleteTankClick(int position);
    void onChangePhotoCameraClick(int position);
    void onChangePhotoGalleryClick(int position);

}
