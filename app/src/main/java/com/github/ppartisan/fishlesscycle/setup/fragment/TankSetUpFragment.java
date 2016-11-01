package com.github.ppartisan.fishlesscycle.setup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.ppartisan.fishlesscycle.R;
import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.model.Tank.PlantStatus;
import com.github.ppartisan.fishlesscycle.setup.BaseSetUpWizardPagerFragment;
import com.github.ppartisan.fishlesscycle.setup.TankBuilderSupplier;

public final class TankSetUpFragment extends BaseSetUpWizardPagerFragment implements
        CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener{

    private boolean isVisible = false;

    private CheckBox mHeater, mSeedMaterial;
    private RadioButton mNoPlants, mLightPlants, mHeavyPlants;

    public static TankSetUpFragment newInstance() {

        Bundle args = new Bundle();

        TankSetUpFragment fragment = new TankSetUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_suw_tank_set_up, container, false);

        mHeater = (CheckBox) v.findViewById(R.id.ust_suwf_heated);
        mSeedMaterial = (CheckBox) v.findViewById(R.id.ust_suwf_seeded);

        RadioGroup plantedGroup = (RadioGroup) v.findViewById(R.id.ust_suwf_planted);

        mNoPlants = (RadioButton) v.findViewById(R.id.ust_suwf_radio_no_plants);
        mLightPlants = (RadioButton) v.findViewById(R.id.ust_suwf_radio_light_plants);
        mHeavyPlants = (RadioButton) v.findViewById(R.id.ust_suwf_radio_heavy_plants);

        mHeater.setOnCheckedChangeListener(this);
        mSeedMaterial.setOnCheckedChangeListener(this);
        plantedGroup.setOnCheckedChangeListener(this);

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();

        final Tank.Builder builder = getTankBuilderSupplier().getTankBuilder();

        mHeater.setChecked(builder.isHeated());
        mSeedMaterial.setChecked(builder.isSeeded());

        final @PlantStatus int plantStatus = builder.getPlantStatus();
        setPlantedButtonChecked(plantStatus);

    }

    private void setPlantedButtonChecked(@PlantStatus int plantStatus) {

        switch (plantStatus) {
            case Tank.NONE:
                mNoPlants.setChecked(true);
                break;
            case Tank.LIGHT:
                mLightPlants.setChecked(true);
                break;
            case Tank.HEAVY:
                mHeavyPlants.setChecked(true);
                break;
        }

    }

    private @PlantStatus int getPlantedStatusFromCheckedButton() {

        if (mNoPlants.isChecked()) return Tank.NONE;
        if (mLightPlants.isChecked()) return Tank.LIGHT;
        if (mHeavyPlants.isChecked()) return Tank.HEAVY;

        throw new RuntimeException("Included solely to satisfy compiler. " +
                PlantStatus.class.getSimpleName() + " is IntDef so this should never be thrown");

    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {

        final TankBuilderSupplier supplier = getTankBuilderSupplier();
        if (supplier == null) return;

        if(!isVisible) return;

        final Tank.Builder builder = supplier.getTankBuilder();

        switch (button.getId()) {
            case R.id.ust_suwf_heated:
                builder.setIsHeated(isChecked);
                break;
            case R.id.ust_suwf_seeded:
                builder.setIsSeeded(isChecked);
                break;
        }

        supplier.notifyTankBuilderUpdated();

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        final TankBuilderSupplier supplier = getTankBuilderSupplier();
        if (supplier == null) return;

        if(!isVisible) return;

        final Tank.Builder builder = supplier.getTankBuilder();

        switch (i) {
            case R.id.ust_suwf_radio_no_plants:
                builder.setPlantStatus(Tank.NONE);
                supplier.notifyTankBuilderUpdated();
                break;
            case R.id.ust_suwf_radio_light_plants:
                builder.setPlantStatus(Tank.LIGHT);
                supplier.notifyTankBuilderUpdated();
                break;
            case R.id.ust_suwf_radio_heavy_plants:
                builder.setPlantStatus(Tank.HEAVY);
                supplier.notifyTankBuilderUpdated();
                break;
        }


    }

    @Override
    public void onTankModified(Tank.Builder builder) {

        if (builder == null) return;

        final boolean isHeated = builder.isHeated();
        if (isHeated != mHeater.isChecked()) {
            mHeater.setChecked(builder.isHeated());
        }

        final boolean isSeeded = builder.isSeeded();
        if (isSeeded != mSeedMaterial.isChecked()) {
            mSeedMaterial.setChecked(builder.isSeeded());
        }

        final @PlantStatus int plantStatus = builder.getPlantStatus();
        final @PlantStatus int currentPlantStatus = getPlantedStatusFromCheckedButton();
        if (plantStatus != currentPlantStatus) {
            setPlantedButtonChecked(plantStatus);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Without this, Radio Group auto-checks first button in group on page swipe.
        this.isVisible = isVisibleToUser;
    }

}
