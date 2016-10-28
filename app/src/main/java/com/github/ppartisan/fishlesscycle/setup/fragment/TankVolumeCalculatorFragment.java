package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.SettingsActivity;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;

import java.text.DecimalFormat;

import static com.github.ppartisan.fishlesscycle.util.ViewUtils.isTextWidgetEmpty;

//ToDo: See Below
/*
This class should pull the unit preference value from SharedPreferences, which is configured in
"Settings" menu, and update SharedPreferences based on the switch value. Revisit once the
"Settings" activity/fragment is configured.
 */

public final class TankVolumeCalculatorFragment extends BaseSetUpWizardPagerFragment
        implements TextWatcher, View.OnClickListener {

    private final DecimalFormat mFormat = new DecimalFormat(".##");

    private EditText mHeight, mWidth, mLength, mOutput;

    private @PreferenceUtils.VolumeUnit int volumeUnit = PreferenceUtils.METRIC;

    private TextView mUnitDescription;
    private ImageButton mSettingsBtn;

    public static TankVolumeCalculatorFragment newInstance() {

        Bundle args = new Bundle();

        TankVolumeCalculatorFragment fragment = new TankVolumeCalculatorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volumeUnit = PreferenceUtils.getVolumeUnit(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_suw_tank_volume, container, false);

        mHeight = (EditText) v.findViewById(R.id.vt_suwf_height);
        mWidth = (EditText) v.findViewById(R.id.vt_suwf_width);
        mLength = (EditText) v.findViewById(R.id.vt_suwf_length);
        mOutput = (EditText) v.findViewById(R.id.vt_swuf_output);

        //Below Index could be passed via Fragment arguments. Optional feature.
        final int editTextBackgroundColor = mColorPackSupplier.getColorPackForIndexId(0).dark;

        mHeight.setHintTextColor(editTextBackgroundColor);
        mWidth.setHintTextColor(editTextBackgroundColor);
        mLength.setHintTextColor(editTextBackgroundColor);
        mOutput.setHintTextColor(editTextBackgroundColor);

        mHeight.setTextColor(Color.DKGRAY);
        mWidth.setTextColor(Color.DKGRAY);
        mLength.setTextColor(Color.DKGRAY);
        mOutput.setTextColor(Color.DKGRAY);

        mUnitDescription = (TextView) v.findViewById(R.id.vt_suwf_unit_description);

        mSettingsBtn = (ImageButton) v.findViewById(R.id.vt_suwf_switch_units);
        mSettingsBtn.setOnClickListener(this);

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        mOutput.setText(getConvertedTankVolumeString());
        mHeight.addTextChangedListener(this);
        mWidth.addTextChangedListener(this);
        mLength.addTextChangedListener(this);
        mOutput.addTextChangedListener(new OutputTextWatcher());
        mUnitDescription.setText(getUnitDescriptionText());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        updateVolume();
    }

    @Override
    public void afterTextChanged(Editable editable) {}


    private String getUnitDescriptionText() {

        String measurement = null, volume = null;

        switch (volumeUnit) {
            case PreferenceUtils.METRIC:
                measurement = getString(R.string.centimetres).toLowerCase();
                volume = getString(R.string.litres).toLowerCase();
                break;
            case PreferenceUtils.IMPERIAL:
                measurement = getString(R.string.inches).toLowerCase();
                volume = getString(R.string.imperial_gallons).toLowerCase();
                break;
            case PreferenceUtils.US:
                measurement = getString(R.string.inches).toLowerCase();
                volume = getString(R.string.us_gallons);
                break;
        }

        return getString(R.string.units_description_template_double, measurement, volume);
    }

    private void updateVolume() {
        if(!isTextWidgetEmpty(mHeight) && !isTextWidgetEmpty(mWidth) && !isTextWidgetEmpty(mLength)) {

            final float height = Float.parseFloat(mHeight.getText().toString());
            final float width = Float.parseFloat(mWidth.getText().toString());
            final float length = Float.parseFloat(mLength.getText().toString());

            float volumeInCmOrCubicInches = (height*width*length);

            float volume = 0;

            switch (volumeUnit) {
                case PreferenceUtils.METRIC:
                    volume = ConversionUtils.getCubicCmAsLitres(volumeInCmOrCubicInches);
                    break;
                case PreferenceUtils.IMPERIAL:
                    volume = ConversionUtils.getCubicInchesAsImperialGallon(volumeInCmOrCubicInches);
                    volume = ConversionUtils.getImperialGallonsAsLitres(volume);
                    break;
                case PreferenceUtils.US:
                    volume = ConversionUtils.getCubicInchesAsUsGallon(volumeInCmOrCubicInches);
                    volume = ConversionUtils.getUsGallonsAsLitres(volume);
                    break;
            }

            updateOutput(volume);

        }
    }

    private void updateOutput(float volumeInLitres) {

        if(mTankBuilderSupplier != null) {
            final Tank.Builder builder = mTankBuilderSupplier.getTankBuilder();
            if(volumeInLitres == builder.getVolumeInLitres()) {
                return;
            }
        } else {
            return;
        }

        float displayVolume = 0;
        switch (volumeUnit) {
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

        final String outputText = mFormat.format(displayVolume);

        if(!outputText.equals(mOutput.getText().toString())) {

            final Tank.Builder builder = mTankBuilderSupplier.getTankBuilder();
            builder.volumeInLitres(volumeInLitres);
            mTankBuilderSupplier.notifyTankBuilderUpdated();
            mOutput.setText(mFormat.format(displayVolume));

        }

    }

    private String getConvertedTankVolumeString() {

        if(mTankBuilderSupplier == null) {
            return null;
        }

        final float volumeInLitres = mTankBuilderSupplier.getTankBuilder().getVolumeInLitres();

        float displayVolume = 0;
        switch (volumeUnit) {
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

        return mFormat.format(displayVolume);

    }

    @Override
    public void onTankModified(Tank.Builder builder) {

        if(builder == null) return;

        final float volumeInLitres = builder.getVolumeInLitres();
        final float outputVolumeInLitres = getOutputTextAsMetricLitres();

        if (volumeInLitres != outputVolumeInLitres) {
            mOutput.setText(getConvertedTankVolumeString());
        }
    }

    private float getOutputTextAsMetricLitres() {

        final String volumeText = mOutput.getText().toString();
        float volumeInLitres;

        try{
            volumeInLitres = Float.parseFloat(volumeText);
        } catch (NumberFormatException ignored) {
            return 0;
        }

        switch (volumeUnit) {
            case PreferenceUtils.METRIC:
                //Already in metric
                break;
            case PreferenceUtils.IMPERIAL:
                volumeInLitres = ConversionUtils.getImperialGallonsAsLitres(volumeInLitres);
                break;
            case PreferenceUtils.US:
                volumeInLitres = ConversionUtils.getUsGallonsAsLitres(volumeInLitres);
                break;
        }

        return volumeInLitres;

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        final String volumeUnitPreferenceKey = getString(R.string.pref_volume_unit_type_key);

        if(volumeUnitPreferenceKey.equals(s)) {

            final float volumeInLitres = getOutputTextAsMetricLitres();

            volumeUnit = PreferenceUtils.getVolumeUnit(getContext());
            mUnitDescription.setText(getUnitDescriptionText());

            updateOutput(volumeInLitres);

        }

}

    @Override
    public void onClick(View view) {
        startActivity(new Intent(getContext(), SettingsActivity.class));
    }

    private final class OutputTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if(mTankBuilderSupplier == null) {
                return;
            }

            if(mOutput.hasFocus()) {
                mHeight.getText().clear();
                mLength.getText().clear();
                mWidth.getText().clear();
            }

            final float outputVolume = getOutputTextAsMetricLitres();
            final Tank.Builder builder = mTankBuilderSupplier.getTankBuilder();
            if(builder.getVolumeInLitres() != outputVolume) {
                builder.volumeInLitres(outputVolume);
                mTankBuilderSupplier.notifyTankBuilderUpdated();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }

}
