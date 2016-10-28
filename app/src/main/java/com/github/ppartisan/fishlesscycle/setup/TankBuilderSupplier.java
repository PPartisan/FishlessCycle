package com.github.ppartisan.fishlesscycle.setup;

import com.github.ppartisan.fishlesscycle.model.Tank;
import com.github.ppartisan.fishlesscycle.setup.model.ColorPack;

public interface TankBuilderSupplier {

    Tank.Builder getTankBuilder();
    void notifyTankBuilderUpdated();

}
