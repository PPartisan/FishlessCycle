package com.github.ppartisan.fishlesscycle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.TankBuilderSupplier;
import com.github.ppartisan.fishlesscycle.setup.fragment.ConfirmationFragment;
import com.github.ppartisan.fishlesscycle.util.AppUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;
import com.google.android.gms.analytics.Tracker;

public final class EditTankActivity extends AppCompatActivity implements TankBuilderSupplier {

    private static final String TAG = EditTankActivity.class.getSimpleName();
    public static final String TANK_BUILDER_KEY = TAG + ".TANK_BUILDER_KEY";

    private Tracker mTracker;
    private Tank.Builder mTankBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tank);

        AppUtils.checkInternetPermissions(this);
        mTracker = ((FishlessCycleApplication)getApplication()).getDefaultTracker();

        if (savedInstanceState == null) {
            mTankBuilder = getIntent().getParcelableExtra(TANK_BUILDER_KEY);
        } else {
            mTankBuilder = savedInstanceState.getParcelable(TANK_BUILDER_KEY);
        }

        final ImageView image = (ImageView) findViewById(R.id.tea_image);
        image.setColorFilter(ContextCompat.getColor(this, R.color.primary_dark));
        Glide.with(this).load(R.drawable.tank_white).into(image);

        if (getSupportFragmentManager().findFragmentById(R.id.tea_parent) == null) {
            ConfirmationFragment f = ConfirmationFragment.newInstance(true);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tea_parent, f)
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.sendTrackerHit(mTracker, getClass());
    }

    @Override
    protected void onPause() {
        super.onPause();
        final String where = Contract.TankEntry._ID + " =?";
        final String[] whereArgs = new String[] { String.valueOf(mTankBuilder.getIdentifier()) };
        getContentResolver().update(
                Contract.TankEntry.CONTENT_URI, TankUtils.toContentValues(mTankBuilder), where, whereArgs
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TANK_BUILDER_KEY, mTankBuilder);
    }

    @Override
    public Tank.Builder getTankBuilder() {
        return mTankBuilder;
    }

    @Override
    public void notifyTankBuilderUpdated() {
        //unused
    }

}
