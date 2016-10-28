package com.github.ppartisan.fishlesscycle.setup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ppartisan.fishlesscycle.model.Tank;


public class BaseSetUpWizardPagerFragment extends Fragment implements TankBuilderObserver, SharedPreferences.OnSharedPreferenceChangeListener {

    protected TankBuilderSupplier mTankBuilderSupplier;
    protected ColorPackSupplier mColorPackSupplier;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mTankBuilderSupplier = (TankBuilderSupplier) context;
            mColorPackSupplier = (ColorPackSupplier) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement "
                    + TankBuilderSupplier.class.getCanonicalName() + " and "
                    + ColorPackSupplier.class.getCanonicalName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTankBuilderSupplier = null;
        mColorPackSupplier = null;
    }

    @Override
    public void onTankModified(Tank.Builder builder) {}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
