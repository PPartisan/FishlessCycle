package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.ui.SettingsActivity;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;

import java.text.DecimalFormat;

import static com.github.ppartisan.fishlesscycle.util.TankUtils.getTankVolumeInLitresAsUserUnitPreference;
import static com.github.ppartisan.fishlesscycle.util.TankUtils.getVolumeAsLitres;
import static com.github.ppartisan.fishlesscycle.util.ViewUtils.getParsedFloatFromTextWidget;
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
        final int editTextBackgroundColor = ContextCompat.getColor(getContext(), R.color.grey_500);

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

        mUnitDescription.setText(getUnitDescriptionText());
        mOutput.setText(getConvertedTankVolumeString());

        mHeight.addTextChangedListener(this);
        mWidth.addTextChangedListener(this);
        mLength.addTextChangedListener(this);
        mOutput.addTextChangedListener(new OutputTextWatcher());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if(!isTextWidgetEmpty(mHeight) && !isTextWidgetEmpty(mWidth) && !isTextWidgetEmpty(mLength)) {
            float volumeInLitres;
            try {
                volumeInLitres = getVolumeFromUserInputFieldsInLitres();
            } catch (NumberFormatException e) {
                return;
            }
            updateOutput(volumeInLitres);
        }

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

    private float getVolumeFromUserInputFieldsInLitres() throws NumberFormatException {

        final float height = Float.parseFloat(mHeight.getText().toString());
        final float width = Float.parseFloat(mWidth.getText().toString());
        final float length = Float.parseFloat(mLength.getText().toString());

        float volumeInCmOrCubicInches = (height*width*length);

        float volume = 0;

        switch (volumeUnit) {
            case PreferenceUtils.METRIC:
                volume = ConversionUtils.getMlAsLitres(volumeInCmOrCubicInches);
                break;
            case PreferenceUtils.IMPERIAL:
            case PreferenceUtils.US:
                volume = ConversionUtils.getCubicInchesAsLitres(volumeInCmOrCubicInches);
                break;
        }

        return volume;

    }

    private void updateOutput(float volumeInLitres) {

        if(getTankBuilderSupplier() != null) {
            final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();
            if(volumeInLitres == builder.getVolumeInLitres()) {
                return;
            }
        } else {
            return;
        }

        float displayVolume = getTankVolumeInLitresAsUserUnitPreference(volumeInLitres, volumeUnit);

        final String outputText = mFormat.format(displayVolume);

        if(!outputText.equals(mOutput.getText().toString())) {

            mOutput.setText(mFormat.format(displayVolume));

            final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();
            if (builder.getVolumeInLitres() != volumeInLitres) {
                builder.setVolumeInLitres(volumeInLitres);
                getTankBuilderSupplier().notifyTankBuilderUpdated();
            }

        }

    }

    private String getConvertedTankVolumeString() {

        if(getTankBuilderSupplier() == null) {
            return null;
        }

        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();
        final float volumeInLitres = builder.getVolumeInLitres();
        final float displayVolume = getTankVolumeInLitresAsUserUnitPreference(volumeInLitres, volumeUnit);

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

        float volumeInLitres = getParsedFloatFromTextWidget(mOutput);
        return getVolumeAsLitres(volumeInLitres, volumeUnit);

    }

    private void clearInputFields() {
        mHeight.getText().clear();
        mWidth.getText().clear();
        mLength.getText().clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        final String volumeUnitPreferenceKey = getString(R.string.pref_volume_unit_type_key);

        if(volumeUnitPreferenceKey.equals(s)) {

            clearInputFields();
            mOutput.requestFocus();

            final float volumeInLitres = getOutputTextAsMetricLitres();

            volumeUnit = PreferenceUtils.getVolumeUnit(getContext());

            final float convertedVolume =
                    getTankVolumeInLitresAsUserUnitPreference(volumeInLitres, volumeUnit);
            mOutput.setText(mFormat.format(convertedVolume));

            mUnitDescription.setText(getUnitDescriptionText());

        }

    }

    @Override
    public void onClick(View view) {
        final Intent settings = new Intent(getContext(), SettingsActivity.class);
        settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final ActivityOptionsCompat options =
                ViewUtils.buildCircleRevealActivityTransition(view, getView());
        ActivityCompat.startActivity(getContext(), settings, options.toBundle());
    }

    private final class OutputTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if(getTankBuilderSupplier() == null) {
                return;
            }

            if(mOutput.hasFocus()) {
                clearInputFields();
            }

            final float outputVolume = getOutputTextAsMetricLitres();
            final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();
            if(builder.getVolumeInLitres() != outputVolume) {
                builder.setVolumeInLitres(outputVolume);
                getTankBuilderSupplier().notifyTankBuilderUpdated();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }

}
