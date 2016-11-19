package com.github.ppartisan.fishlesscycle.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class AmmoniaDosage implements Parcelable {

    public final float dosage, targetConcentration;

    AmmoniaDosage(AmmoniaDosage ammoniaDosage) {
        this(ammoniaDosage.dosage, ammoniaDosage.targetConcentration);
    }

    public AmmoniaDosage(float dosage, float targetConcentration) {
        this.dosage = dosage;
        this.targetConcentration = targetConcentration;
    }

    private AmmoniaDosage(Parcel in) {
        dosage = in.readFloat();
        targetConcentration = in.readFloat();
    }

    public static final Creator<AmmoniaDosage> CREATOR = new Creator<AmmoniaDosage>() {
        @Override
        public AmmoniaDosage createFromParcel(Parcel in) {
            return new AmmoniaDosage(in);
        }

        @Override
        public AmmoniaDosage[] newArray(int size) {
            return new AmmoniaDosage[size];
        }
    };

    public boolean equalsValues(float otherDosage, float otherTargetConcentration) {
        return (this.dosage == otherDosage) &&
                (this.targetConcentration == otherTargetConcentration);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Float.floatToIntBits(dosage);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(dosage);
        parcel.writeFloat(targetConcentration);
    }

}

