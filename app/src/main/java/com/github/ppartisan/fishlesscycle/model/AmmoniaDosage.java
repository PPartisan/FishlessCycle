package com.github.ppartisan.fishlesscycle.model;

public final class AmmoniaDosage {

    public final float dosage, targetConcentration;

    public AmmoniaDosage(AmmoniaDosage ammoniaDosage) {
        this(ammoniaDosage.dosage, ammoniaDosage.targetConcentration);
    }

    public AmmoniaDosage(float dosage, float targetConcentration) {
        this.dosage = dosage;
        this.targetConcentration = targetConcentration;
    }
}

