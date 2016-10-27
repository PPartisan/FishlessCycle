package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.AmmoniaDosage;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.util.ConversionUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;

import java.text.DecimalFormat;

import static com.github.ppartisan.fishlesscycle.util.ViewUtils.isEditTextEmpty;

public final class AmmoniaDosageFragment extends BaseSetUpWizardPagerFragment implements TextWatcher {

    private static final float DEFAULT_AMMONIA_PERCENTAGE = 10f;
    private static final float DEFAULT_TARGET_CONCENTRATION = 3f;

    private final DecimalFormat mFormat = new DecimalFormat("##.##");

    private SwitchCompat mDoseUnitsSwitch, mUnitsSwitch;
    private AppCompatEditText mTankVolumeEntry, mAmmoniaPercentEntry, mTargetConcentrationEntry, mOutput;
    private TextView mTankVolumeLabel, mAmmoniaPercentageLabel, mTargetConcentrationLabel;
    public static AmmoniaDosageFragment newInstance() {

        Bundle args = new Bundle();

        AmmoniaDosageFragment fragment = new AmmoniaDosageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_suw_ammonia_dosage, container, false);

        mDoseUnitsSwitch = (SwitchCompat) v.findViewById(R.id.da_suwf_dose_units_switch);
        mUnitsSwitch = (SwitchCompat) v.findViewById(R.id.da_suwf_units_switch);

        mTankVolumeEntry = (AppCompatEditText) v.findViewById(R.id.da_suwf_tank_volume);
        mAmmoniaPercentEntry = (AppCompatEditText) v.findViewById(R.id.da_suwf_per_ammonia);
        mTargetConcentrationEntry = (AppCompatEditText) v.findViewById(R.id.da_suwf_target_dose);
        mOutput = (AppCompatEditText) v.findViewById(R.id.da_suwf_ouput);

        mTankVolumeEntry.addTextChangedListener(this);
        mAmmoniaPercentEntry.addTextChangedListener(this);
        mTargetConcentrationEntry.addTextChangedListener(this);

        mTankVolumeLabel = (TextView) v.findViewById(R.id.da_suwf_tank_volume_label);
        mAmmoniaPercentageLabel = (TextView) v.findViewById(R.id.da_suwf_per_ammonia_label);
        mTargetConcentrationLabel = (TextView) v.findViewById(R.id.da_suwf_target_dose_label);

        final String[] volumeUnits = getResources().getStringArray(R.array.volume_unit_options);
        final String[] doseUnitOptions = getResources().getStringArray(R.array.dose_unit_options);

        //ToDo Most of these should be accessed from preferences. Revisit when Settings up and running
        final String tankVolumeLabelTemplate = getString(R.string.wus_fda_tank_volume_label, volumeUnits[0]);
        final String targetConcentrationLabel = getString(R.string.wus_fda_target_dose_label, doseUnitOptions[0]);

        mTankVolumeLabel.setText(tankVolumeLabelTemplate);
        mTargetConcentrationLabel.setText(targetConcentrationLabel);

        mTankVolumeEntry.setText(getTankVolumeFromBuilder(mTankModifier.getTankBuilder()));
        mAmmoniaPercentEntry.setText(mFormat.format(DEFAULT_AMMONIA_PERCENTAGE));
        mTargetConcentrationEntry.setText(mFormat.format(DEFAULT_TARGET_CONCENTRATION));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onTankModified(Tank.Builder builder) {
        mTankVolumeEntry.setText(getTankVolumeFromBuilder(builder));
    }

    private String getTankVolumeFromBuilder(Tank.Builder builder) {
        return mFormat.format(builder.getVolumeInLitres());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        updateOutput();
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    private void updateOutput() {
        final boolean inputFieldsNotEmpty =
                !isEditTextEmpty(mTankVolumeEntry) &&
                !isEditTextEmpty(mAmmoniaPercentEntry) &&
                !isEditTextEmpty(mTargetConcentrationEntry);

        if (inputFieldsNotEmpty && mTankModifier != null) {

            final float tankVolumeInLitres = mTankModifier.getTankBuilder().getVolumeInLitres();
            final float ammoniaPercent = Float.parseFloat(mAmmoniaPercentEntry.getText().toString());
            final float targetConcentration = Float.parseFloat(mTargetConcentrationEntry.getText().toString());

            final float ammoniaDosage =
                    ConversionUtils.getAmmoniaDosage(
                            tankVolumeInLitres, targetConcentration, ammoniaPercent
                    );

            mTankModifier.getTankBuilder().ammoniaDosage(
                    new AmmoniaDosage(ammoniaDosage, targetConcentration)
            );

            mOutput.setText(String.valueOf(Math.round(ammoniaDosage)));

            //ToDo: Prevent recursive calls. Check value stored in Builder before notifying
            //mTankModifier.notifyTankBuilderUpdated();

        }
    }

}
