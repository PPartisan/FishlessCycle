package com.github.ppartisan.fishlesscycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.ppartisan.fishlesscycle.adapter.TankCardCallbacks;
import com.github.ppartisan.fishlesscycle.adapter.TanksAdapter;
import com.github.ppartisan.fishlesscycle.data.Contract;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.SetUpWizardActivity;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;
import com.github.ppartisan.fishlesscycle.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

public final class MainFragment extends Fragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener, TankCardCallbacks, SharedPreferences.OnSharedPreferenceChangeListener {

    private EmptyRecyclerView mRecyclerView;
    private TanksAdapter mAdapter;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);

        mToolbar = (Toolbar) view.findViewById(R.id.fm_toolbar);
        mToolbar.inflateMenu(R.menu.main_menu);
        mToolbar.setOnMenuItemClickListener(this);

        mFab = (FloatingActionButton) view.findViewById(R.id.fm_fab);
        mFab.setOnClickListener(this);

        mRecyclerView = (EmptyRecyclerView) view.findViewById(R.id.fm_recycler);
        mRecyclerView.setEmptyView(view.findViewById(R.id.fm_empty_view));

        final @ConversionUtils.UnitType int dosUnitType =
                PreferenceUtils.getDosageUnitType(getContext());

        final @PreferenceUtils.VolumeUnit int volUnitType =
                PreferenceUtils.getVolumeUnit(getContext());

        mAdapter = new TanksAdapter(this, null, dosUnitType, volUnitType);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void updateTankList(List<Tank> tanks) {
        mAdapter.updateTanksList(tanks);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(), SetUpWizardActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mm_action_settings:
                Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }

        return true;
    }

    @Override
    public void onCardClick(int position) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(DetailFragment.KEY_IDENTIFIER, mAdapter.getTank(position).identifier);
        startActivity(intent);
    }

    @Override
    public void onEditTankClick(int position) {
        // TODO: 03/11/16 Edit tank details
        final Tank.Builder builder = new Tank.Builder(mAdapter.getTank(position));
        Intent intent = new Intent(getContext(), EditTankActivity.class);
        intent.putExtra(EditTankActivity.TANK_BUILDER_KEY, builder);
        startActivity(intent);
    }

    @Override
    public void onDeleteTankClick(int position) {
        final Tank tank = mAdapter.getTank(position);
        deleteTank(tank);
        buildTankDeletedSnackBar(tank).show();
    }

    @Override
    public void onChangePhotoClick(int position) {
        // TODO: 03/11/16 Change Photo
    }

    private void deleteTank(Tank tank) {
        final String where = Contract.TankEntry._ID + "=?";
        final String[] whereArgs =
                new String[] { String.valueOf(tank.identifier) };
        getContext().getContentResolver().delete(Contract.TankEntry.CONTENT_URI, where, whereArgs);
    }

    private Snackbar buildTankDeletedSnackBar(final Tank tank) {
        Snackbar snackbar = Snackbar.make(
                getView(),
                getString(R.string.deleted_template, tank.name),
                Snackbar.LENGTH_LONG
        );

        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().getContentResolver().insert(
                        Contract.TankEntry.CONTENT_URI, TankUtils.toContentValues(tank)
                );
            }
        });

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                //Action click means "Undo" was fired
                if (event == DISMISS_EVENT_ACTION) {
                    return;
                }
                final String where = Contract.ReadingEntry.COLUMN_IDENTIFIER+"=?";
                final String[] whereArgs = new String[] { String.valueOf(tank.identifier) };
                getContext().getContentResolver().delete(
                        Contract.ReadingEntry.CONTENT_URI, where, whereArgs
                );
            }
        });

        return snackbar;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        final String doseUnitPrefsKey = getString(R.string.pref_dosage_unit_type_key);
        final String volUnitPrefsKey = getString(R.string.pref_volume_unit_type_key);

        if(doseUnitPrefsKey.equals(s)) {
            final @ConversionUtils.UnitType int unitType =
                    PreferenceUtils.getDosageUnitType(getContext());
            mAdapter.setDosageUnitType(unitType);
        }

        if(volUnitPrefsKey.equals(s)) {
            final @PreferenceUtils.VolumeUnit int unitType =
                    PreferenceUtils.getVolumeUnit(getContext());
            mAdapter.setVolumeUnit(unitType);
        }

        mAdapter.notifyDataSetChanged();

    }


}
