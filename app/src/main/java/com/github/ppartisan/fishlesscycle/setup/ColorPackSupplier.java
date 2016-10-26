package com.github.ppartisan.fishlesscycle.setup;

import com.github.ppartisan.fishlesscycle.setup.model.ColorPack;

public interface ColorPackSupplier {

    ColorPack getColorPackForIndexId(int index);

}
