package com.github.ppartisan.fishlesscycle.setup;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils.UnitType;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils.VolumeUnit;

import static com.github.ppartisan.fishlesscycle.util.ConversionUtils.MGL;
import static com.github.ppartisan.fishlesscycle.util.ConversionUtils.PPM;


public class BaseSetUpWizardPagerFragment extends Fragment implements TankBuilderObserver, TankBuilderModifier, SharedPreferences.OnSharedPreferenceChangeListener {

    private TankBuilderSupplier mTankBuilderSupplier;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mTankBuilderSupplier = (TankBuilderSupplier) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement "
                    + TankBuilderSupplier.class.getCanonicalName());
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
    }

    @Override
    public void onTankModified(Tank.Builder builder) {}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    protected TankBuilderSupplier getTankBuilderSupplier() {
        if (mTankBuilderSupplier == null) {
            mTankBuilderSupplier = (TankBuilderSupplier) getContext();
        }
        return mTankBuilderSupplier;
    }


    @Override
    public float getVolumeAsLitres(float volume, @VolumeUnit int unit) {

        float volumeInLitres = 0;

        switch (unit) {
            case PreferenceUtils.METRIC:
                //Already in litres
                volumeInLitres = volume;
                break;
            case PreferenceUtils.IMPERIAL:
                volumeInLitres = ConversionUtils.getImperialGallonsAsLitres(volume);
                break;
            case PreferenceUtils.US:
                volumeInLitres = ConversionUtils.getUsGallonsAsLitres(volume);
                break;
        }

        return volumeInLitres;
    }

    @Override
    public float getTankVolumeInLitresAsUserUnitPreference(float volumeInLitres, @VolumeUnit int unit) {

        float displayVolume = 0;
        switch (unit) {
            case PreferenceUtils.METRIC:
                displayVolume = volumeInLitres;
                break;
            case PreferenceUtils.IMPERIAL:
                displayVolume = ConversionUtils.getLitresAsImperialGallons(volumeInLitres);
                break;
            case PreferenceUtils.US:
                displayVolume = ConversionUtils.getLitresAsUsGallons(volumeInLitres);
                break;
        }

        return displayVolume;
    }

    @Override
    public String getUserDosageUnitAsString(@UnitType int unit) {

            String unitString;

            switch (unit) {
                case MGL:
                    unitString = getString(R.string.unit_metric);
                    break;
                case PPM:
                    unitString = getString(R.string.unit_imperial);
                    break;
                default:
                    throw new IllegalArgumentException("'type' parameter must be of type "
                            + UnitType.class.getCanonicalName());
            }

            return unitString;

    }

    public String getUserVolumeUnitAsString(@VolumeUnit int unit) {

        String unitString = null;

        switch (unit) {
            case PreferenceUtils.METRIC:
                unitString = getString(R.string.litres);
                break;
            case PreferenceUtils.IMPERIAL:
                unitString = getString(R.string.imperial_gallons);
                break;
            case PreferenceUtils.US:
                unitString = getString(R.string.us_gallons);
                break;
        }

        return unitString;

    }

}
