package com.github.ppartisan.fishlesscycle.setup.adapter;

public interface SetUpPageChangeListenerCallbacks {

    int getCount();
    void onUpdateColors(int colorRegular, int colorDark);
    void onPageSelected(int page);

}
