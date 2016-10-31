package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.MainActivity;
import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.AmmoniaDosage;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;

import java.text.DecimalFormat;

public final class ConfirmationFragment extends BaseSetUpWizardPagerFragment implements TextWatcher {

    private final DecimalFormat mFormat = new DecimalFormat("##.#");

    private @PreferenceUtils.VolumeUnit int volumeUnit;

    private TextView mVolumeLabel, mAmmoniaLabel;
    private EditText mTitle, mVolume, mAmmonia;
    private CheckBox mHeater, mSeedMaterial;
    private RadioButton mNoPlants, mLightlyPlanted, mHeavilyPlanted;

    public static ConfirmationFragment newInstance() {

        Bundle args = new Bundle();

        ConfirmationFragment fragment = new ConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volumeUnit = PreferenceUtils.getVolumeUnit(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v= inflater.inflate(R.layout.fragment_suw_confirmation, container, false);

        mVolumeLabel = (TextView) v.findViewById(R.id.c_suwf_volume_label);
        mAmmoniaLabel = (TextView) v.findViewById(R.id.c_suwf_dosage_label);

        mVolumeLabel.setText(getVolumeLabelText());
        mAmmoniaLabel.setText(getAmmoniaLabelText());

        mTitle = (EditText) v.findViewById(R.id.c_suwf_title_entry);
        mVolume = (EditText) v.findViewById(R.id.c_suwf_volume_entry);
        mAmmonia = (EditText) v.findViewById(R.id.c_suwf_dosage_entry);

        //// TODO: 31/10/16 Generate a title based on tank's number

        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

        mTitle.setText(builder.getName());
        mVolume.setText(getVolumeText());
        mAmmonia.setText(getAmmoniaText());

        mTitle.addTextChangedListener(this);
        mVolume.addTextChangedListener(this);
        mAmmonia.addTextChangedListener(this);

        mHeater = (CheckBox) v.findViewById(R.id.c_suwf_heated);
        mSeedMaterial = (CheckBox) v.findViewById(R.id.c_suwf_seeded);

        mHeater.setChecked(builder.isHeated());
        mSeedMaterial.setChecked(builder.isSeeded());

        mNoPlants = (RadioButton) v.findViewById(R.id.c_suwf_radio_no_plants);
        mLightlyPlanted = (RadioButton) v.findViewById(R.id.c_suwf_radio_light_plants);
        mHeavilyPlanted = (RadioButton) v.findViewById(R.id.c_suwf_radio_heavy_plants);

        final @Tank.PlantStatus int plantStatus = builder.getPlantStatus();
        setPlantedButtonChecked(plantStatus);

        return v;
    }

    private String getVolumeLabelText() {
        return getString(R.string.wus_fda_tank_volume_label, getUserVolumeUnitAsString(volumeUnit));
    }

    private String getAmmoniaLabelText() {
        return getString(R.string.wus_fda_percent_ammonia_label);
    }

    private String getVolumeText() {

        if (getTankBuilderSupplier() == null) return null;

        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();
        final float volume =
                getTankVolumeInLitresAsUserUnitPreference(builder.getVolumeInLitres(), volumeUnit);

        return (volume <=0) ? "" : String.valueOf((int)Math.ceil(volume));
    }

    private String getAmmoniaText() {

        if (getTankBuilderSupplier() == null) return null;

        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

        if (builder.getAmmoniaDosage() == null) {
            return mFormat.format(0);
        }

        return mFormat.format(builder.getAmmoniaDosage().dosage);

    }

    @Override
    public void onTankModified(Tank.Builder builder) {

        if (!mTitle.getText().toString().equals(builder.getName())) {
            mTitle.setText(builder.getName());
        }

        final String volume = getVolumeText();
        if (!mVolume.getText().toString().equals(volume)) {
            mVolume.setText(volume);
        }

        final String ammonia = getAmmoniaText();
        if (!mAmmonia.getText().toString().equals(ammonia)) {
            mAmmonia.setText(ammonia);
        }

        if (mHeater.isChecked() != builder.isHeated()) {
            mHeater.setChecked(builder.isHeated());
        }

        if (mSeedMaterial.isChecked() != builder.isSeeded()) {
            mSeedMaterial.setChecked(builder.isSeeded());
        }

        final @Tank.PlantStatus int plantStatus = builder.getPlantStatus();

        if (plantStatus != getPlantedStatusFromCheckedButton()) {
            setPlantedButtonChecked(plantStatus);
        }

    }

    private void setPlantedButtonChecked(@Tank.PlantStatus int plantStatus) {

        switch (plantStatus) {
            case Tank.NONE:
                mNoPlants.setChecked(true);
                break;
            case Tank.LIGHT:
                mLightlyPlanted.setChecked(true);
                break;
            case Tank.HEAVY:
                mHeavilyPlanted.setChecked(true);
                break;
        }

    }

    private @Tank.PlantStatus
    int getPlantedStatusFromCheckedButton() {

        if (mNoPlants.isChecked()) return Tank.NONE;
        if (mLightlyPlanted.isChecked()) return Tank.LIGHT;
        if (mHeavilyPlanted.isChecked()) return Tank.HEAVY;

        throw new RuntimeException("Included solely to satisfy compiler. " +
                Tank.PlantStatus.class.getSimpleName() + " is IntDef so this should never be thrown");

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if (getTankBuilderSupplier() == null) return;

        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

        final String name = mTitle.getText().toString();
        if (!name.equals(builder.getName())) {
            Log.d(getClass().getSimpleName(), "Updating name");
            builder.name(name);
            getTankBuilderSupplier().notifyTankBuilderUpdated();
        }

        final float parsedVolume = ViewUtils.getParsedFloatFromTextWidget(mVolume);
        Log.d(getClass().getSimpleName(), "Parsed Vol: " + parsedVolume);
        final float volumeInLitres = getVolumeAsLitres(parsedVolume, volumeUnit);
        if (volumeInLitres != builder.getVolumeInLitres()) {
            Log.d(getClass().getSimpleName(), "Updating volume. Parsed Volume: " + parsedVolume + ", builderVolume: " + builder.getVolumeInLitres());
            builder.volumeInLitres(volumeInLitres);
            getTankBuilderSupplier().notifyTankBuilderUpdated();
        }

        final float parsedAmmonia = ViewUtils.getParsedFloatFromTextWidget(mAmmonia);
        final AmmoniaDosage dosage = builder.getAmmoniaDosage();
        if (dosage != null && dosage.dosage != parsedAmmonia) {
            Log.d(getClass().getSimpleName(), "Updating ammonia");
            builder.ammoniaDosage(parsedAmmonia, dosage.targetConcentration);
            getTankBuilderSupplier().notifyTankBuilderUpdated();
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {}

}
