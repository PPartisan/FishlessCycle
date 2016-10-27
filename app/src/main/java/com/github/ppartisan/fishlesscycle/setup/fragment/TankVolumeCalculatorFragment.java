package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;

import java.text.DecimalFormat;

//ToDo: See Below
/*
This class should pull the unit preference value from SharedPreferences, which is configured in
"Settings" menu, and update SharedPreferences based on the switch value. Revisit once the
"Settings" activity/fragment is configured.
 */

public final class TankVolumeCalculatorFragment extends BaseSetUpWizardPagerFragment
        implements TextWatcher, CompoundButton.OnCheckedChangeListener {

    private final DecimalFormat mFormat = new DecimalFormat(".##");

    private EditText mHeight, mWidth, mLength, mOutput;

    private TextView mUnitDescription;
    private SwitchCompat mUnitSwitch;
    private boolean isImperial;

    public static TankVolumeCalculatorFragment newInstance() {

        Bundle args = new Bundle();

        TankVolumeCalculatorFragment fragment = new TankVolumeCalculatorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_suw_tank_volume, container, false);

        mHeight = (EditText) v.findViewById(R.id.vt_suwf_height);
        mWidth = (EditText) v.findViewById(R.id.vt_suwf_width);
        mLength = (EditText) v.findViewById(R.id.vt_suwf_length);
        mOutput = (EditText) v.findViewById(R.id.vt_swuf_output);

        final int editTextBackgroundColor = mColorPackSupplier.getColorPackForIndexId(0).dark;

        mHeight.setHintTextColor(editTextBackgroundColor);
        mWidth.setHintTextColor(editTextBackgroundColor);
        mLength.setHintTextColor(editTextBackgroundColor);
        mOutput.setHintTextColor(editTextBackgroundColor);

        mHeight.setTextColor(Color.DKGRAY);
        mWidth.setTextColor(Color.DKGRAY);
        mLength.setTextColor(Color.DKGRAY);
        mOutput.setTextColor(Color.DKGRAY);

        mHeight.addTextChangedListener(this);
        mWidth.addTextChangedListener(this);
        mLength.addTextChangedListener(this);

        mUnitDescription = (TextView) v.findViewById(R.id.vt_suwf_unit_description);

        mUnitSwitch = (SwitchCompat) v.findViewById(R.id.vt_suwf_switch_units);
        mUnitSwitch.setOnCheckedChangeListener(this);
        mUnitSwitch.setChecked(true);

        mUnitDescription.setText(getUnitDescriptionText(mUnitSwitch.isChecked()));

        return v;

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        updateVolume();
    }

    @Override
    public void afterTextChanged(Editable editable) {}

    private static boolean isEditTextEmpty(EditText editText) {
        return TextUtils.isEmpty(editText.getText());
    }

    private String getUnitDescriptionText(boolean isImperial) {
        final int resId = (isImperial)
                ? R.string.wus_fcvt_decription_imperial
                : R.string.wus_fcvt_decription_metric;
        return getString(resId);
    }

    private void updateVolume() {
        if(!isEditTextEmpty(mHeight) && !isEditTextEmpty(mWidth) && !isEditTextEmpty(mLength)) {

            final float height = Float.parseFloat(mHeight.getText().toString());
            final float width = Float.parseFloat(mWidth.getText().toString());
            final float length = Float.parseFloat(mLength.getText().toString());

            float volumeInMlOrCubicInches = (height*width*length);

            final float volumeInLitres = ConversionUtils.getMlAsLitres(volumeInMlOrCubicInches);
            final float volumeInUkGallons =
                    ConversionUtils.getCubicInchesAsImperialGallon(volumeInMlOrCubicInches);

            mTankModifier.getTankBuilder().volumeInLitres(volumeInLitres);

            final float displayVolume = (isImperial) ? volumeInUkGallons : volumeInLitres;
            mOutput.setText(mFormat.format(displayVolume));

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isImperial) {
        //Unchecked is metric
        this.isImperial = isImperial;
        mUnitDescription.setText(getUnitDescriptionText(isImperial));
        updateVolume();
    }

}
