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

    public boolean equalsValues(float otherDosage, float otherTargetConcentration) {
        return (this.dosage == otherDosage) &&
                (this.targetConcentration == otherTargetConcentration);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result  = 31 * result + Float.floatToIntBits(dosage);
        result = 31 * result + Float.floatToIntBits(targetConcentration);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof AmmoniaDosage)) return false;
        final AmmoniaDosage other = (AmmoniaDosage) obj;
        return (other.dosage == this.dosage) &&
                (other.targetConcentration == this.targetConcentration);
    }
}

