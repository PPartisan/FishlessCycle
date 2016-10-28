package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.SettingsActivity;
import com.github.ppartisan.fishlesscycle.model.AmmoniaDosage;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;

import java.text.DecimalFormat;

import static com.github.ppartisan.fishlesscycle.util.ViewUtils.isTextWidgetEmpty;

public final class AmmoniaDosageFragment extends BaseSetUpWizardPagerFragment implements TextWatcher, View.OnClickListener {

    private static final float DEFAULT_AMMONIA_PERCENTAGE = 10f;
    private static final float DEFAULT_TARGET_CONCENTRATION = 3f;

    private final DecimalFormat mFormat = new DecimalFormat("##.##");

    private @PreferenceUtils.VolumeUnit int volumeUnit;
    private boolean isDosageMetric = false;

    private String[] mVolumeUnitOptions, mDoseUnitOptions;

    private AppCompatEditText mAmmoniaPercentEntry, mTargetConcentrationEntry;
    private TextView mTankVolumeLabel, mTargetConcentrationLabel;
    private TextView mSettingsDescription;
    private TextView mTankVolume, mOutput;

    public static AmmoniaDosageFragment newInstance() {

        Bundle args = new Bundle();

        AmmoniaDosageFragment fragment = new AmmoniaDosageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volumeUnit = PreferenceUtils.getVolumeUnit(getContext());
        isDosageMetric = PreferenceUtils.isDosageMetric(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_suw_ammonia_dosage, container, false);

        ImageButton settings = (ImageButton) v.findViewById(R.id.da_suwf_settings);
        settings.setOnClickListener(this);

        mTankVolume = (TextView) v.findViewById(R.id.da_suwf_tank_volume);

        mAmmoniaPercentEntry = (AppCompatEditText) v.findViewById(R.id.da_suwf_per_ammonia);
        mTargetConcentrationEntry = (AppCompatEditText) v.findViewById(R.id.da_suwf_target_dose);

        mOutput = (TextView) v.findViewById(R.id.da_suwf_ouput);

        mTankVolumeLabel = (TextView) v.findViewById(R.id.da_suwf_tank_volume_label);
        mTargetConcentrationLabel = (TextView) v.findViewById(R.id.da_suwf_target_dose_label);

        mSettingsDescription = (TextView) v.findViewById(R.id.da_suwf_settings_description);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTankVolume.setText(getConvertedTankVolumeString());

        final AmmoniaDosage ammoniaDosage = mTankBuilderSupplier.getTankBuilder().getAmmoniaDosage();

        if(ammoniaDosage != null) {
            mTargetConcentrationEntry.setText(mFormat.format(ammoniaDosage.targetConcentration));
            mOutput.setText(mFormat.format(ammoniaDosage.dosage));
        }

        mAmmoniaPercentEntry.addTextChangedListener(this);
        mTargetConcentrationEntry.addTextChangedListener(this);
        mTankVolume.addTextChangedListener(this);

        mSettingsDescription.setText(getSettingsDescriptionText());
        mTankVolumeLabel.setText(getTankVolumeLabel());
        mTargetConcentrationLabel.setText(getTargetConcentrationLabel());

        updateTankVolume(mTankBuilderSupplier.getTankBuilder().getVolumeInLitres());

        mAmmoniaPercentEntry.setText(mFormat.format(DEFAULT_AMMONIA_PERCENTAGE));
        mTargetConcentrationEntry.setText(mFormat.format(DEFAULT_TARGET_CONCENTRATION));

    }

    @Override
    public void onTankModified(Tank.Builder builder) {
        if(builder == null) return;

        if(builder.getVolumeInLitres() != getTankVolumeInLitres()) {
            mTankVolume.setText(getConvertedTankVolumeString());
        }
    }

    private String getSettingsDescriptionText() {

        String volume = null;

        switch (volumeUnit) {
            case PreferenceUtils.METRIC:
                volume = getString(R.string.litres).toLowerCase();
                break;
            case PreferenceUtils.IMPERIAL:
                volume = getString(R.string.imperial_gallons).toLowerCase();
                break;
            case PreferenceUtils.US:
                volume = getString(R.string.us_gallons);
                break;
        }

        final int dosageId = (isDosageMetric) ? R.string.mgl : R.string.ppm;
        final String dosage = getString(dosageId);

        return getString(R.string.units_description_template_double, volume, dosage);

    }

    private String getTargetConcentrationLabel() {
        final int doseUnitIndex = (isDosageMetric) ? 0 : 1;
        return getString(R.string.wus_fda_target_dose_label, getDoseUnitOptions()[doseUnitIndex]);
    }

    private String[] getDoseUnitOptions() {
        if (mDoseUnitOptions == null) {
            mDoseUnitOptions = getResources().getStringArray(R.array.dose_unit_options);
        }
        return mDoseUnitOptions;
    }

    private String[] getVolumeUnitOptions() {
        if (mVolumeUnitOptions == null) {
            mVolumeUnitOptions = getResources().getStringArray(R.array.volume_unit_options);
        }
        return mVolumeUnitOptions;
    }

    private String getTankVolumeLabel() {

        final String[] volumeOptions = getVolumeUnitOptions();
        String tankVolumeLabel = null;

        switch (volumeUnit) {
            case PreferenceUtils.METRIC:
                tankVolumeLabel = getString(R.string.wus_fda_tank_volume_label, volumeOptions[0]);
                break;
            case PreferenceUtils.IMPERIAL:
                tankVolumeLabel = getString(R.string.wus_fda_tank_volume_label, volumeOptions[1]);
                break;
            case PreferenceUtils.US:
                tankVolumeLabel = getString(R.string.wus_fda_tank_volume_label, volumeOptions[2]);
                break;
        }

        return tankVolumeLabel;

    }

    private void updateTankVolume(float volumeInLitres) {

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

        if(!outputText.equals(mTankVolume.getText().toString())) {

            final Tank.Builder builder = mTankBuilderSupplier.getTankBuilder();

            if (builder.getVolumeInLitres() != volumeInLitres) {
                builder.volumeInLitres(volumeInLitres);
                mTankBuilderSupplier.notifyTankBuilderUpdated();
            }

            mTankVolume.setText(mFormat.format(displayVolume));

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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        updateOutput();
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        final String volumeUnitPreferenceKey = getString(R.string.pref_volume_unit_type_key);
        final String dosageUnitPreferenceKey = getString(R.string.pref_dosage_unit_type_key);

        if (volumeUnitPreferenceKey.equals(s)) {
            final float tankVolumeInLitres = getTankVolumeInLitres();
            volumeUnit = PreferenceUtils.getVolumeUnit(getContext());
            updateTankVolume(tankVolumeInLitres);
            mTankVolumeLabel.setText(getTankVolumeLabel());
            mSettingsDescription.setText(getSettingsDescriptionText());
        }

        if (dosageUnitPreferenceKey.equals(s)) {
            isDosageMetric = PreferenceUtils.isDosageMetric(getContext());
            mTargetConcentrationLabel.setText(getTargetConcentrationLabel());
            mSettingsDescription.setText(getSettingsDescriptionText());
        }

    }

    private void updateOutput() {
        final boolean inputFieldsNotEmpty =
                !isTextWidgetEmpty(mTankVolume) &&
                !isTextWidgetEmpty(mAmmoniaPercentEntry) &&
                !isTextWidgetEmpty(mTargetConcentrationEntry);

        if (inputFieldsNotEmpty && mTankBuilderSupplier != null) {

            final Tank.Builder builder = mTankBuilderSupplier.getTankBuilder();

            float tankVolumeInLitres;
            float ammoniaPercent;
            float targetConcentration;

            try {
                tankVolumeInLitres = getTankVolumeInLitres();
                ammoniaPercent = Float.parseFloat(mAmmoniaPercentEntry.getText().toString());
                targetConcentration = Float.parseFloat(mTargetConcentrationEntry.getText().toString());
            } catch (NumberFormatException e) {
                return;
            }

            if (tankVolumeInLitres != builder.getVolumeInLitres()) {
                builder.volumeInLitres(tankVolumeInLitres);
                mTankBuilderSupplier.notifyTankBuilderUpdated();
            }

            final float ammoniaDosage =
                    ConversionUtils.getAmmoniaDosage(
                            tankVolumeInLitres, targetConcentration, ammoniaPercent
                    );

            final String formattedAmmoniaDosage = mFormat.format(ammoniaDosage);

            if (!mOutput.getText().toString().equals(formattedAmmoniaDosage)) {
                mOutput.setText(formattedAmmoniaDosage);
            }

            final AmmoniaDosage currentDosage = builder.getAmmoniaDosage();

            if (currentDosage == null || !currentDosage.equalsValues(ammoniaDosage, targetConcentration)) {
                final AmmoniaDosage newDosage =
                        new AmmoniaDosage(ammoniaDosage, targetConcentration);
                mTankBuilderSupplier.getTankBuilder().ammoniaDosage(newDosage);
                mTankBuilderSupplier.notifyTankBuilderUpdated();
            }

        }
    }

    private float getTankVolumeInLitres() {

        final String volumeText = mTankVolume.getText().toString();

        float volumeInLitres;
        try {
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
    public void onClick(View view) {
        startActivity(new Intent(getContext(), SettingsActivity.class));
    }

}
