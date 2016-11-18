package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.ui.SettingsActivity;
import com.github.ppartisan.fishlesscycle.model.AmmoniaDosage;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;

import java.text.DecimalFormat;

import static com.github.ppartisan.fishlesscycle.model.Tank.DEFAULT_TARGET_CONCENTRATION;
import static com.github.ppartisan.fishlesscycle.util.TankUtils.getTankVolumeInLitresAsUserUnitPreference;
import static com.github.ppartisan.fishlesscycle.util.TankUtils.getVolumeAsLitres;
import static com.github.ppartisan.fishlesscycle.util.ViewUtils.getParsedFloatFromTextWidget;
import static com.github.ppartisan.fishlesscycle.util.ViewUtils.isTextWidgetEmpty;

public final class AmmoniaDosageFragment extends BaseSetUpWizardPagerFragment implements TextWatcher, View.OnClickListener {

    private static final float DEFAULT_AMMONIA_PERCENTAGE = 10f;

    private final DecimalFormat mFormat = new DecimalFormat("##.##");

    private @PreferenceUtils.VolumeUnit int volumeUnit;
    private boolean isDosageMetric = false;

    private String[] mVolumeUnitOptions, mDoseUnitOptions;

    private AppCompatEditText mAmmoniaPercentEntry, mTargetConcentrationEntry;
    private TextView mTankVolumeLabel, mTargetConcentrationLabel;
    private TextView mSettingsDescription;
    private TextView mTankVolume, mOutput;

    private final AmmoniaDosage nullAmmoniaDosage =
            new AmmoniaDosage(0, DEFAULT_TARGET_CONCENTRATION);

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

        mAmmoniaPercentEntry = (AppCompatEditText) v.findViewById(R.id.da_suwf_per_ammonia);
        mTargetConcentrationEntry = (AppCompatEditText) v.findViewById(R.id.da_suwf_target_dose);

        mOutput = (TextView) v.findViewById(R.id.da_suwf_ouput);
        mTankVolume = (TextView) v.findViewById(R.id.da_suwf_tank_volume);
        mTankVolumeLabel = (TextView) v.findViewById(R.id.da_suwf_tank_volume_label);
        mTargetConcentrationLabel = (TextView) v.findViewById(R.id.da_suwf_target_dose_label);
        mSettingsDescription = (TextView) v.findViewById(R.id.da_suwf_settings_description);

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();

        //todo This will be written to and retrieved from a ContentProvider later
        mAmmoniaPercentEntry.setText(mFormat.format(DEFAULT_AMMONIA_PERCENTAGE));
        mTargetConcentrationEntry.setText(mFormat.format(getAmmoniaDosage().targetConcentration));
        mOutput.setText(mFormat.format(getAmmoniaDosage().dosage));
        if (getTankBuilderSupplier() != null) {
            final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();
            mTankVolume.setText(getConvertedTankVolumeStringFromBuilder(builder));
        }

        mSettingsDescription.setText(getSettingsDescriptionText());
        mTankVolumeLabel.setText(getTankVolumeLabel());
        mTargetConcentrationLabel.setText(getTargetConcentrationLabel());

        mAmmoniaPercentEntry.addTextChangedListener(this);
        mTargetConcentrationEntry.addTextChangedListener(this);
        mTankVolume.addTextChangedListener(this);
    }

    @Override
    public void onTankModified(Tank.Builder builder) {
        if(builder == null) return;

        mTankVolume.setText(getConvertedTankVolumeStringFromBuilder(builder));
        updateOutput();

    }

    private AmmoniaDosage getAmmoniaDosage() {

        if (getTankBuilderSupplier() == null) {
            return nullAmmoniaDosage;
        }

        return getTankBuilderSupplier().getTankBuilder().getAmmoniaDosage();

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

    private String getConvertedTankVolumeStringFromBuilder(Tank.Builder builder) {
        final float volumeInLitres = builder.getVolumeInLitres();
        final float displayVolume = getTankVolumeInLitresAsUserUnitPreference(volumeInLitres, volumeUnit);
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
            final float convertedVolume =
                    getTankVolumeInLitresAsUserUnitPreference(tankVolumeInLitres, volumeUnit);
            mTankVolume.setText(mFormat.format(convertedVolume));
            mTankVolumeLabel.setText(getTankVolumeLabel());
            mSettingsDescription.setText(getSettingsDescriptionText());
            updateOutput();
        }

        if (dosageUnitPreferenceKey.equals(s)) {

            isDosageMetric = PreferenceUtils.isDosageMetric(getContext());
            mSettingsDescription.setText(getSettingsDescriptionText());
            mTargetConcentrationLabel.setText(getTargetConcentrationLabel());

        }

    }

    private float getTankVolumeInLitres() {

        float volumeInLitres = getParsedFloatFromTextWidget(mTankVolume);
        return getVolumeAsLitres(volumeInLitres, volumeUnit);

    }



    private void updateOutput() {

        if (isTextWidgetEmpty(mTankVolume) ||
                isTextWidgetEmpty(mAmmoniaPercentEntry) ||
                isTextWidgetEmpty(mTargetConcentrationEntry) ||
                getTankBuilderSupplier() == null) {
            return;
        }

        final float tankVolumeInLitres = getTankVolumeInLitres();
        final float ammoniaPercentage = getParsedFloatFromTextWidget(mAmmoniaPercentEntry);
        final float targetConcentration = getParsedFloatFromTextWidget(mTargetConcentrationEntry);

        final float ammoniaDosage = ConversionUtils.getAmmoniaDosage(
                tankVolumeInLitres, targetConcentration, ammoniaPercentage
        );

        final String formattedDosageString = mFormat.format(ammoniaDosage);

        if (!formattedDosageString.equals(mOutput.getText().toString())) {

            mOutput.setText(formattedDosageString);

            final AmmoniaDosage currentDosage = getTankBuilderSupplier().getTankBuilder().getAmmoniaDosage();

            if (!currentDosage.equalsValues(ammoniaDosage, targetConcentration)) {
                getTankBuilderSupplier().getTankBuilder().setAmmoniaDosage(ammoniaDosage, targetConcentration);
                getTankBuilderSupplier().notifyTankBuilderUpdated();
            }
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

}
