package com.github.ppartisan.fishlesscycle.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;

public final class DosagePopUpWindow {

    private static final String TAG = DosagePopUpWindow.class.getSimpleName();
    private static final String IS_OPEN_KEY = TAG + ".IS_OPEN_KEY";
    //Auto-Close PopUpWindow after 3.5seconds
    private static final long TIME_OUT_DURATION = 3500;

    private final PopupWindow mWindow;
    private final View mAnchor;
    private final Handler mTimeOutHandler;
    private final Runnable mTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if (mWindow != null && mWindow.isShowing()) {
                dismiss();
            }
        }
    };

    public DosagePopUpWindow(View anchor, String tankName, float dosage) {
        final Context context = anchor.getContext();
        mAnchor = anchor;
        mWindow = buildDosagePopUp(context, buildContentString(context,tankName,dosage));
        mTimeOutHandler = new Handler(Looper.getMainLooper());
    }

    public void show() {
        if(!mWindow.isShowing()) {
            PopupWindowCompat.showAsDropDown(mWindow, mAnchor, 0, 0, GravityCompat.END);
            mTimeOutHandler.postDelayed(mTimeOutRunnable, TIME_OUT_DURATION);
        }
    }

    public void dismiss() {
        mWindow.dismiss();
        mTimeOutHandler.removeCallbacks(mTimeOutRunnable);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_OPEN_KEY, mWindow.isShowing());
    }

    public void onRestoreInstanceState(Bundle state) {

        if(mWindow == null || mAnchor == null || state == null) return;

        boolean isShowing = state.getBoolean(IS_OPEN_KEY);

        if(isShowing && !mWindow.isShowing()) {
            mAnchor.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(ViewCompat.isLaidOut(mAnchor)) {
                        show();
                        mAnchor.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        } else
        if (!isShowing && mWindow.isShowing()) {
            mWindow.dismiss();
        }

    }

    private PopupWindow buildDosagePopUp(Context context, String content) {

        final View view = LayoutInflater.from(context)
                .inflate(R.layout.popup_dosage, null);
        final TextView text = (TextView) view.findViewById(R.id.p_dosage_text);
        text.setText(content);

        final PopupWindow window = new PopupWindow(
                view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWindow.dismiss();
            }
        });

        //noinspection deprecation
        window.setBackgroundDrawable(new BitmapDrawable());
        window.setOutsideTouchable(true);

        return window;

    }

    private static String buildContentString(Context context, String tankName, float dosage) {
        return context.getString(R.string.p_dosage_content, tankName, dosage);
    }

}
