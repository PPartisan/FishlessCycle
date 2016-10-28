package com.github.ppartisan.fishlesscycle.setup;

import com.github.ppartisan.fishlesscycle.model.Tank;

public interface TankBuilderObserver {

    void onTankModified(Tank.Builder builder);

}
