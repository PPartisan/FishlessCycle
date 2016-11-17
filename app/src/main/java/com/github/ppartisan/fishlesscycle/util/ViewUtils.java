package com.github.ppartisan.fishlesscycle.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;

import java.lang.reflect.Field;

public final class ViewUtils {

    private static final int SWITCH_STATE_LIST_ALPHA = (int)(0.3f*255);

    private ViewUtils() { throw new AssertionError(); }

    public static PopupMenu buildPopUpMenu(View target, int menuId) {

        PopupMenu menu = new PopupMenu(target.getContext(), target, GravityCompat.END);
        menu.getMenuInflater().inflate(menuId, menu.getMenu());
        return menu;

    }

    public static int dpToPx(int dp) {
        final DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int)((dp * metrics.density));
    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        }
    }

    public static ColorStateList buildSwitchCompatColorStateListFromResId(
            Context context, int activatedColorResId) {
        return buildSwitchCompatColorStateList(
                context, ContextCompat.getColor(context, activatedColorResId)
        );
    }

    public static ColorStateList buildSwitchCompatColorStateList(Context context, int activatedColor) {

        final int disabledColor = ContextCompat.getColor(context, R.color.grey_300);
        final int disabledColorAlpha = Color.argb(
                SWITCH_STATE_LIST_ALPHA,
                Color.red(disabledColor),
                Color.green(disabledColor),
                Color.blue(disabledColor)
        );

        final int[][] states = new int[][] {
                new int[] { -android.R.attr.state_enabled },
                new int[] { android.R.attr.state_checked },
                new int[0]
        };

        final int[] colors = new int[] { disabledColorAlpha, activatedColor, disabledColor };

        return new ColorStateList(states, colors);

    }

    public static boolean isTextWidgetEmpty(TextView textWidget) {
        return TextUtils.isEmpty(textWidget.getText());
    }

    public static float getParsedFloatFromTextWidget(TextView textView) {

        float value;

        try {
            value = Float.parseFloat(textView.getText().toString());
        } catch (NumberFormatException e) {
            value = 0;
        }

        return value;

    }

    public static void collapseSoftInput(Activity activity) {
        final View v = activity.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Nullable
    public static TextView getToolbarTitleTextView(Toolbar toolbar) {
        try {
            Class<?> toolbarClass = Toolbar.class;
            Field titleTextViewField = toolbarClass.getDeclaredField("mTitleTextView");
            titleTextViewField.setAccessible(true);

            return (TextView) titleTextViewField.get(toolbar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static DisplayMetrics getScreenMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }

    public static ActivityOptionsCompat buildCircleRevealActivityTransition(int cx, int cy, View view, @Nullable View root) {
        final DisplayMetrics metrics = ViewUtils.getScreenMetrics();
        int tHeight, tWidth;
        if(root == null) {
            tHeight = metrics.heightPixels;
            tWidth = metrics.widthPixels;
        } else {
            tHeight = root.getHeight();
            tWidth = root.getWidth();
        }

        return ActivityOptionsCompat.makeClipRevealAnimation(view, cx, cy, tWidth, tHeight);
    }

    public static ActivityOptionsCompat buildCircleRevealActivityTransition(View view, @Nullable View root){
        final int cx = view.getWidth()/2;
        final int cy = view.getHeight()/2;
        return buildCircleRevealActivityTransition(cx, cy, view, root);
    }

    @Nullable
    public static Bitmap getSizedBitmapFromPath(String filePath, int newHeight, int newWidth) {

        if(filePath == null) return null;

        filePath = removeFilePrefixFromPath(filePath);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/newWidth, photoH/newHeight);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(filePath, bmOptions);

    }

    private static String removeFilePrefixFromPath(String path) {
        if(path.startsWith("file:")) {
            return path.substring(5);
        }
        return path;
    }

}
