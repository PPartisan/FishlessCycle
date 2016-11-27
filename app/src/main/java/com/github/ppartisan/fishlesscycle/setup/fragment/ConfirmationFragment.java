package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.AmmoniaDosage;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.setup.TankBuilderSupplier;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils;
import com.github.ppartisan.fishlesscycle.util.ViewUtils;

import java.text.DecimalFormat;

import static com.github.ppartisan.fishlesscycle.util.TankUtils.getTankVolumeInLitresAsUserUnitPreference;
import static com.github.ppartisan.fishlesscycle.util.TankUtils.getUserVolumeUnitAsString;
import static com.github.ppartisan.fishlesscycle.util.TankUtils.getVolumeAsLitres;

public final class ConfirmationFragment extends BaseSetUpWizardPagerFragment implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = ConfirmationFragment.class.getSimpleName();
    public static final String IS_VISIBLE_DEFAULT_KEY = TAG + ".IS_VISIBLE_BY_DEFAULT_KEY";

    private final DecimalFormat mFormat = new DecimalFormat("##.#");

    private @PreferenceUtils.VolumeUnit int volumeUnit;

    private boolean isVisible = true;

    private TextView mVolumeLabel;
    private EditText mTitle, mVolume, mAmmonia;
    private CheckBox mHeater, mSeedMaterial;
    private RadioGroup mPlantGroup;
    private RadioButton mNoPlants, mLightlyPlanted, mHeavilyPlanted;

    public static ConfirmationFragment newInstance(boolean isVisibleByDefault) {

        Bundle args = new Bundle();
        args.putBoolean(IS_VISIBLE_DEFAULT_KEY, isVisibleByDefault);

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

        mTitle = (EditText) v.findViewById(R.id.c_suwf_title_entry);
        mVolume = (EditText) v.findViewById(R.id.c_suwf_volume_entry);
        mAmmonia = (EditText) v.findViewById(R.id.c_suwf_dosage_entry);

        mHeater = (CheckBox) v.findViewById(R.id.c_suwf_heated);
        mSeedMaterial = (CheckBox) v.findViewById(R.id.c_suwf_seeded);

        mNoPlants = (RadioButton) v.findViewById(R.id.c_suwf_radio_no_plants);
        mLightlyPlanted = (RadioButton) v.findViewById(R.id.c_suwf_radio_light_plants);
        mHeavilyPlanted = (RadioButton) v.findViewById(R.id.c_suwf_radio_heavy_plants);

        Button confirm = (Button) v.findViewById(R.id.c_suwf_confirm);
        confirm.setOnClickListener(this);

        mPlantGroup = (RadioGroup) v.findViewById(R.id.c_suwf_planted);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

        final String title = getTitleText();
        if (title != null) {
            mTitle.setText(title);
            builder.setName(title);
        }
        mVolumeLabel.setText(getVolumeLabelText());

        mVolume.setText(getVolumeText());
        mAmmonia.setText(getAmmoniaText());

        mHeater.setChecked(builder.isHeated());
        mSeedMaterial.setChecked(builder.isSeeded());

        final @Tank.PlantStatus int plantStatus = builder.getPlantStatus();

        setPlantedButtonChecked(plantStatus);

        mTitle.addTextChangedListener(new TitleTextWatcher());
        mVolume.addTextChangedListener(new TankVolumeTextWatcher());
        mAmmonia.addTextChangedListener(new AmmoniaTextWatcher());

        mHeater.setOnCheckedChangeListener(this);
        mSeedMaterial.setOnCheckedChangeListener(this);

        mPlantGroup.setOnCheckedChangeListener(this);

    }

    private boolean isVisibleByDefault() {
        return getArguments().getBoolean(IS_VISIBLE_DEFAULT_KEY);
    }

    private String getTitleText() {

        if (getTankBuilderSupplier() == null) return null;

        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

        String title;

        if (!TextUtils.isEmpty(builder.getName())) {
            title = builder.getName();
        } else {
            title = getString(R.string.wus_cf_name_template, builder.getIdentifier() + 1);
        }

        return title;

    }

    private String getVolumeLabelText() {
        return getString(
                R.string.wus_fda_tank_volume_label,
                getUserVolumeUnitAsString(getResources(), volumeUnit)
        );
    }

    private String getVolumeText() {

        if (getTankBuilderSupplier() == null) return null;

        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();
        final float volume =
                getTankVolumeInLitresAsUserUnitPreference(builder.getVolumeInLitres(), volumeUnit);

        return (volume <=0) ? "0" : String.valueOf((int)Math.ceil(volume));
    }

    private String getAmmoniaText() {

        if (getTankBuilderSupplier() == null) return null;
        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();
        return mFormat.format(builder.getAmmoniaDosage().dosage);

    }

    @Override
    public void onTankModified(Tank.Builder builder) {

        if (builder.getName() != null && mTitle!= null && !mTitle.getText().toString().equals(builder.getName())) {
            mTitle.setText(getTitleText());
        }

        final String volume = getVolumeText();
        if (mVolume != null && !mVolume.getText().toString().equals(volume)) {
            mVolume.setText(volume);
        }

        final String ammonia = getAmmoniaText();
        if (mAmmonia != null && !mAmmonia.getText().toString().equals(ammonia)) {
            mAmmonia.setText(ammonia);
        }

        if (mHeater != null && mHeater.isChecked() != builder.isHeated()) {
            mHeater.setChecked(builder.isHeated());
        }

        if (mSeedMaterial != null && mSeedMaterial.isChecked() != builder.isSeeded()) {
            mSeedMaterial.setChecked(builder.isSeeded());
        }

        if(mNoPlants == null || mLightlyPlanted == null || mHeavilyPlanted == null){
            return;
        }

        final @Tank.PlantStatus int plantStatus = builder.getPlantStatus();
        final @Tank.PlantStatus int currentPlantStatus = getPlantedStatusFromCheckedButton();
        if (plantStatus != currentPlantStatus) {
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
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        final TankBuilderSupplier supplier = getTankBuilderSupplier();
        if (supplier == null) return;

        if(!isVisible) return;

        final Tank.Builder builder = supplier.getTankBuilder();

        switch (i) {
            case R.id.c_suwf_radio_no_plants:
                builder.setPlantStatus(Tank.NONE);
                supplier.notifyTankBuilderUpdated();
                break;
            case R.id.c_suwf_radio_light_plants:
                builder.setPlantStatus(Tank.LIGHT);
                supplier.notifyTankBuilderUpdated();
                break;
            case R.id.c_suwf_radio_heavy_plants:
                builder.setPlantStatus(Tank.HEAVY);
                supplier.notifyTankBuilderUpdated();
                break;
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Without this, Radio Group auto-checks first button in group on page swipe.
        if(!isVisibleByDefault()) {
            this.isVisible = isVisibleToUser;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {

        final TankBuilderSupplier supplier = getTankBuilderSupplier();
        if (supplier == null) return;

        if(!isVisible) return;

        final Tank.Builder builder = supplier.getTankBuilder();

        switch (button.getId()) {
            case R.id.c_suwf_heated:
                builder.setIsHeated(isChecked);
                break;
            case R.id.c_suwf_seeded:
                builder.setIsSeeded(isChecked);
                break;
        }

        supplier.notifyTankBuilderUpdated();
    }

    @Override
    public void onClick(View view) {
        getActivity().finish();
    }

    private final class TitleTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (getTankBuilderSupplier() == null) return;

            final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

            final String name = mTitle.getText().toString();
            if (!name.equals(builder.getName())) {
                builder.setName(name);
                getTankBuilderSupplier().notifyTankBuilderUpdated();
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}

    }

    private final class TankVolumeTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (getTankBuilderSupplier() == null) return;

            final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

            final float parsedVolume = ViewUtils.getParsedFloatFromTextWidget(mVolume);
            final float volumeInLitres = getVolumeAsLitres(parsedVolume, volumeUnit);
            if (volumeInLitres != builder.getVolumeInLitres()) {
                builder.setVolumeInLitres(volumeInLitres);
                getTankBuilderSupplier().notifyTankBuilderUpdated();
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}

    }

    private final class AmmoniaTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (getTankBuilderSupplier() == null) return;

            final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

            final float parsedAmmonia = ViewUtils.getParsedFloatFromTextWidget(mAmmonia);
            final AmmoniaDosage dosage = builder.getAmmoniaDosage();

            final float targetConcentration = dosage.targetConcentration;

            if (dosage.dosage != parsedAmmonia) {
                builder.setAmmoniaDosage(parsedAmmonia, targetConcentration);
                getTankBuilderSupplier().notifyTankBuilderUpdated();
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}

    }

}
