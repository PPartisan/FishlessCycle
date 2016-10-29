package com.github.ppartisan.fishlesscycle.setup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;


public class BaseSetUpWizardPagerFragment extends Fragment implements TankBuilderObserver, TankBuilderModifier, SharedPreferences.OnSharedPreferenceChangeListener {

    private TankBuilderSupplier mTankBuilderSupplier;
    private ColorPackSupplier mColorPackSupplier;

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

    protected TankBuilderSupplier getTankBuilderSupplier() {
        if (mTankBuilderSupplier == null) {
            mTankBuilderSupplier = (TankBuilderSupplier) getContext();
        }
        return mTankBuilderSupplier;
    }

    protected ColorPackSupplier getColorPackSupplier() {
        if (mColorPackSupplier == null) {
            mColorPackSupplier = (ColorPackSupplier) getContext();
        }
        return mColorPackSupplier;
    }


    @Override
    public float getVolumeAsLitres(float volume, @PreferenceUtils.VolumeUnit int unit) {

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
    public float getTankVolumeInLitresAsUserUnitPreference(float volumeInLitres, @PreferenceUtils.VolumeUnit int unit) {

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

}
