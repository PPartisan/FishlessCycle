package com.github.ppartisan.fishlesscycle.setup;

import com.github.ppartisan.fishlesscycle.util.ConversionUtils.UnitType;
import com.github.ppartisan.fishlesscycle.util.PreferenceUtils.VolumeUnit;

public interface TankBuilderModifier {

    float getVolumeAsLitres(float volume, @VolumeUnit int unit);
    float getTankVolumeInLitresAsUserUnitPreference(float volumeInLitres, @VolumeUnit int unit);
    String getUserDosageUnitAsString(@UnitType int unit);
    String getUserVolumeUnitAsString(@VolumeUnit int unit);

}
