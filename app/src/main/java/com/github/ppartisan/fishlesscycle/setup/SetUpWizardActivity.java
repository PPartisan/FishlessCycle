package com.github.ppartisan.fishlesscycle.setup;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.ppartisan.fishlesscycle.FishlessCycleApplication;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.Reading;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.adapter.SetUpPageChangeListener;
import com.github.ppartisan.fishlesscycle.setup.adapter.SetUpPageChangeListenerCallbacks;
import com.github.ppartisan.fishlesscycle.setup.adapter.SetUpWizardAdapter;
import com.github.ppartisan.fishlesscycle.setup.model.ColorPack;
import com.github.ppartisan.fishlesscycle.setup.view.DotIndicatorView;
import com.github.ppartisan.fishlesscycle.strategy.Strategy;
import com.github.ppartisan.fishlesscycle.util.AppUtils;
import com.github.ppartisan.fishlesscycle.util.DataUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

public final class SetUpWizardActivity extends AppCompatActivity implements TankBuilderSupplier, SetUpPageChangeListenerCallbacks, AdapterSupplier {

    private static final String TAG = SetUpWizardActivity.class.getSimpleName();
    public static final String TANK_BUILDER_KEY = TAG + ".TANK_BUILDER_KEY";

    private Tracker mTracker;

    private ViewGroup mParent;
    private ImageView mImage;

    private ViewPager mPager;
    private SetUpWizardAdapter mAdapter;

    private DotIndicatorView mIndicator;
    private Tank.Builder mTankBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.wus_title);
        setContentView(R.layout.activity_set_up_wizard);

        mTracker = ((FishlessCycleApplication)getApplication()).getDefaultTracker();

        Strategy.get().initializeAd(this);
        Strategy.get().loadAdForActivity(this);

        mParent = (ViewGroup) findViewById(R.id.wusa_parent);
        mImage = (ImageView) findViewById(R.id.wusa_image);
        mImage.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        Glide.with(this).load(R.drawable.tank_white).into(mImage);

        final List<ColorPack> colors = new ArrayList<>(SetUpWizardAdapter.NUM_PAGES);
        colors.add(ColorPack.buildFromId(this, R.color.cyan_700, R.color.cyan_900));
        colors.add(ColorPack.buildFromId(this, R.color.grey_500, R.color.grey_700));
        colors.add(ColorPack.buildFromId(this, R.color.indigo_300, R.color.indigo_500));
        colors.add(ColorPack.buildFromId(this, R.color.blue_grey_300, R.color.blue_grey_500));
        colors.add(ColorPack.buildFromId(this, R.color.cyan_500, R.color.cyan_700));
        colors.add(ColorPack.buildFromId(this, R.color.grey_500, R.color.grey_700));
        colors.add(ColorPack.buildFromId(this, R.color.blue_500, R.color.blue_700));

        mPager = (ViewPager) findViewById(R.id.wusa_pager);
        mPager.addOnPageChangeListener(new SetUpPageChangeListener(this, colors));
        mAdapter = new SetUpWizardAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        mIndicator = (DotIndicatorView) findViewById(R.id.wusa_indicator);
        mIndicator.attachToViewPager(mPager);

        if (savedInstanceState != null) {
            mTankBuilder = savedInstanceState.getParcelable(TANK_BUILDER_KEY);
        }

    }

    private Tank.Builder buildTankBuilder() {

        final ContentValues cv = new ContentValues();
        cv.put(Contract.TankEntry.COLUMN_NAME, getString(R.string.app_name));
        final Uri uri = getContentResolver().insert(Contract.TankEntry.CONTENT_URI, cv);

        final long identifier = Contract.TankEntry.getTankId(uri);
        Tank.Builder builder = new Tank.Builder();
        builder.setIdentifier(identifier);
        builder.setName(getString(R.string.app_name));
        builder.setControlReading(new Reading(identifier,System.currentTimeMillis(),0,0,0,true));

        return builder;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mTankBuilder == null) {
            return;
        }

        final ArrayList<ContentProviderOperation> ops =
                DataUtils.tankBuilderToBatchList(mTankBuilder);

        try {
            getContentResolver().applyBatch(Contract.CONTENT_AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TANK_BUILDER_KEY, mTankBuilder);
    }

    @Override
    public Tank.Builder getTankBuilder() {
        if (mTankBuilder == null) {
            mTankBuilder = buildTankBuilder();
        }
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
    public void onPageSelected(int page) {
        final Class<?> c = mAdapter.getItem(page).getClass();
        AppUtils.sendTrackerHit(mTracker, c);
    }

    @Override
    public void setPagerToLastPage() {
        mPager.setCurrentItem(mAdapter.getCount()-1, false);
    }

}
