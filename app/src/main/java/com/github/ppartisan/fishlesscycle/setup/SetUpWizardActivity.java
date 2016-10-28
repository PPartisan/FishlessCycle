package com.github.ppartisan.fishlesscycle.setup;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.adapter.SetUpPageChangeListener;
import com.github.ppartisan.fishlesscycle.setup.adapter.SetUpPageChangeListenerCallbacks;
import com.github.ppartisan.fishlesscycle.setup.adapter.SetUpWizardAdapter;
import com.github.ppartisan.fishlesscycle.setup.model.ColorPack;
import com.github.ppartisan.fishlesscycle.setup.view.DotIndicatorView;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public final class SetUpWizardActivity extends AppCompatActivity implements TankBuilderSupplier, ColorPackSupplier, SetUpPageChangeListenerCallbacks {

    private ViewGroup mParent;
    private ImageView mImage;

    private ViewPager mPager;
    private SetUpWizardAdapter mAdapter;

    private DotIndicatorView mIndicator;
    private Tank.Builder mTankBuilder;

    private List<ColorPack> mColors;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_wizard);

        mParent = (ViewGroup) findViewById(R.id.wusa_parent);
        mImage = (ImageView) findViewById(R.id.wusa_image);
        mImage.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        Picasso.with(this).load(R.drawable.tank_white).into(mImage);

        mColors = new ArrayList<>();
        mColors.add(ColorPack.buildFromId(this, R.color.light_blue_300, R.color.light_blue_500));
        mColors.add(ColorPack.buildFromId(this, R.color.blue_grey_300, R.color.blue_grey_500));
        mColors.add(ColorPack.buildFromId(this, R.color.indigo_300, R.color.indigo_500));
        mColors.add(ColorPack.buildFromId(this, R.color.blue_300, R.color.blue_500));

        mPager = (ViewPager) findViewById(R.id.wusa_pager);
        mPager.addOnPageChangeListener(new SetUpPageChangeListener(this, mColors));
        mAdapter = new SetUpWizardAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        mIndicator = (DotIndicatorView) findViewById(R.id.wusa_indicator);
        mIndicator.attachToViewPager(mPager);

        mTankBuilder = new Tank.Builder();

    }

    @Override
    public Tank.Builder getTankBuilder() {
        return mTankBuilder;
    }

    @Override
    public void notifyTankBuilderUpdated() {
        mAdapter.notifyTankBuilderUpdated(mTankBuilder);
    }

    @Override
    public int getCount() {
        return mAdapter == null ? 0 : mAdapter.getCount();
    }

    @Override
    public void onUpdateColors(int colorRegular, int colorDark) {

        mParent.setBackgroundColor(colorRegular);
        mImage.setColorFilter(colorDark);
        mIndicator.setColor(colorDark);
        ViewUtils.setStatusBarColor(this, colorDark);

    }

    @Override
    public ColorPack getColorPackForIndexId(int index) {
        final int maxIndex = mColors.size() - 1;
        if(index > maxIndex) index = maxIndex;
        return mColors.get(index);
    }
}
