package com.github.ppartisan.fishlesscycle.setup;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.adapter.SetUpWizardAdapter;
import com.github.ppartisan.fishlesscycle.setup.view.DotIndicatorView;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public final class SetUpWizardActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, TankModifier {

    private ViewGroup mParent;
    private ImageView mImage;
    private List<Pair<Integer, Integer>> mColors;
    private ArgbEvaluator mArgbEvaluator;

    private ViewPager mPager;
    private SetUpWizardAdapter mAdapter;

    private DotIndicatorView mIndicator;
    private Tank.Builder mTankBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_wizard);

        mParent = (ViewGroup) findViewById(R.id.wusa_parent);
        mImage = (ImageView) findViewById(R.id.wusa_image);
        mImage.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        Picasso.with(this).load(R.drawable.tank_white).into(mImage);

        mArgbEvaluator = new ArgbEvaluator();

        mPager = (ViewPager) findViewById(R.id.wusa_pager);
        mPager.addOnPageChangeListener(this);

        final int blueGray300 = ContextCompat.getColor(this, R.color.blue_grey_300);
        final int amber300 = ContextCompat.getColor(this, R.color.amber_300);
        final int green300 = ContextCompat.getColor(this, R.color.green_300);
        final int lightBlue300 = ContextCompat.getColor(this, R.color.light_blue_300);
        final int indigo300 = ContextCompat.getColor(this, R.color.indigo_300);

        final int blueGray500 = ContextCompat.getColor(this, R.color.blue_grey_500);
        final int amber500 = ContextCompat.getColor(this, R.color.amber_500);
        final int green500 = ContextCompat.getColor(this, R.color.green_500);
        final int lightBlue500 = ContextCompat.getColor(this, R.color.light_blue_500);
        final int indigo500 = ContextCompat.getColor(this, R.color.indigo_500);

        mColors = new ArrayList<>();
        mColors.add(new Pair<>(blueGray300, blueGray500));
        mColors.add(new Pair<>(amber300, amber500));
        mColors.add(new Pair<>(green300, green500));
        mColors.add(new Pair<>(lightBlue300, lightBlue500));
        mColors.add(new Pair<>(indigo300, indigo500));
        mColors.add(new Pair<>(blueGray300, blueGray500));
        mColors.add(new Pair<>(amber300, amber500));
        mColors.add(new Pair<>(green300, green500));
        mColors.add(new Pair<>(lightBlue300, lightBlue500));
        mColors.add(new Pair<>(indigo300, indigo500));


        mAdapter = new SetUpWizardAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        mIndicator = (DotIndicatorView) findViewById(R.id.wusa_indicator);
        mIndicator.attachToViewPager(mPager);

        mTankBuilder = new Tank.Builder();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (position < (mAdapter.getCount() -1) && position < (mColors.size()-1)) {

            final Pair<Integer, Integer> currentColorPair = mColors.get(position);
            final Pair<Integer, Integer> nextColorPair = mColors.get(position + 1);

            final int primaryColor = (Integer) mArgbEvaluator.evaluate(
                    positionOffset, currentColorPair.first, nextColorPair.first
            );

            final int primaryDarkColor = (Integer) mArgbEvaluator.evaluate(
                    positionOffset, currentColorPair.second, nextColorPair.second
            );

            ViewUtils.setStatusBarColor(this, primaryDarkColor);

            mImage.setColorFilter(primaryDarkColor);
            mParent.setBackgroundColor(primaryColor);
            mIndicator.setColor(primaryDarkColor);

        } else {

            final Pair<Integer, Integer> finalColorPair = mColors.get(mColors.size()-1);

            mParent.setBackgroundColor(finalColorPair.first);
            mImage.setColorFilter(finalColorPair.second);
            mIndicator.setColor(finalColorPair.second);

            ViewUtils.setStatusBarColor(this, finalColorPair.second);

        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public Tank.Builder getTankBuilder() {
        return mTankBuilder;
    }

}
