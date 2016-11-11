package com.github.ppartisan.fishlesscycle.adapter;

public interface TankCardCallbacks {

    void onCardClick(TanksAdapter.ViewHolder vh, int position);
    void onEditTankClick(int position);
    void onDeleteTankClick(int position);
    void onChangePhotoCameraClick(int position);
    void onChangePhotoGalleryClick(int position);

}
