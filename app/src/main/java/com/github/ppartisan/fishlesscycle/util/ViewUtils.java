package com.github.ppartisan.fishlesscycle.util;

import android.support.v4.view.GravityCompat;
import android.support.v7.widget.PopupMenu;
import android.view.View;

public final class ViewUtils {

    private ViewUtils() { throw new AssertionError(); }

    public static PopupMenu buildPopUpMenu(View target, int menuId) {

        PopupMenu menu = new PopupMenu(target.getContext(), target, GravityCompat.END);
        menu.getMenuInflater().inflate(menuId, menu.getMenu());
        return menu;

    }

}
