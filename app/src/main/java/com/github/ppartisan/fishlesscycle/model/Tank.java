package com.github.ppartisan.fishlesscycle.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Tank implements Parcelable {

    public static final float DEFAULT_TARGET_CONCENTRATION = 3f;

    //ToDo: Handle Reminders. Reminder class, time only..?

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE,LIGHT,HEAVY})
    public @interface PlantStatus {}
    public static final int NONE = 0;
    public static final int LIGHT = 5;
    public static final int HEAVY = 6;

    public final String name;
    public final float volumeInLitres;
    private final AmmoniaDosage ammoniaDosage;
    public final boolean isHeated, isSeeded;
    public final @PlantStatus int plantStatus;
    public final long identifier;

    private Tank(
            @NonNull String name,
            float volumeInLitres,
            @NonNull AmmoniaDosage ammoniaDosage,
            boolean isHeated,
            boolean isSeeded,
            @PlantStatus int plantStatus,
            long identifier
    ) {

        this.name = name;
        this.volumeInLitres = volumeInLitres;
        this.ammoniaDosage = ammoniaDosage;
        this.isHeated = isHeated;
        this.isSeeded = isSeeded;
        this.plantStatus = plantStatus;
        this.identifier = identifier;

    }

    protected Tank(Parcel in) {
        this.name = in.readString();
        this.volumeInLitres = in.readFloat();
        this.ammoniaDosage = in.readParcelable(AmmoniaDosage.class.getClassLoader());
        this.isHeated = in.readByte() != 0;
        this.isSeeded = in.readByte() != 0;
        //noinspection WrongConstant
        this.plantStatus = in.readInt();
        this.identifier = in.readLong();
    }

    public static final Creator<Tank> CREATOR = new Creator<Tank>() {
        @Override
        public Tank createFromParcel(Parcel in) {
            return new Tank(in);
        }

        @Override
        public Tank[] newArray(int size) {
            return new Tank[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeFloat(volumeInLitres);
        parcel.writeParcelable(ammoniaDosage, i);
        parcel.writeByte((byte) (isHeated ? 1 : 0));
        parcel.writeByte((byte) (isSeeded ? 1 : 0));
        parcel.writeInt(plantStatus);
        parcel.writeLong(identifier);
    }

    public AmmoniaDosage getAmmoniaDosage() {
        return ammoniaDosage;
    }

    public final static class Builder implements Parcelable {

        private String name;
        private float volumeInLitres;
        private AmmoniaDosage ammoniaDosage;
        private boolean isHeated, isSeeded;
        private long identifier;
        private @PlantStatus int plantStatus = NONE;

        public Builder() {}

        public Builder(@NonNull String tankName) {
            this.name = tankName;
        }

        public Builder(Tank tank) {
            this.name = tank.name;
            this.volumeInLitres = tank.volumeInLitres;
            this.ammoniaDosage = new AmmoniaDosage(tank.getAmmoniaDosage());
            this.isHeated = tank.isHeated;
            this.isSeeded = tank.isSeeded;
            this.identifier = tank.identifier;
            this.plantStatus = tank.plantStatus;
        }

        protected Builder(Parcel in) {
            name = in.readString();
            volumeInLitres = in.readFloat();
            ammoniaDosage = in.readParcelable(AmmoniaDosage.class.getClassLoader());
            isHeated = in.readByte() != 0;
            isSeeded = in.readByte() != 0;
            identifier = in.readLong();
            //noinspection WrongConstant
            plantStatus = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeFloat(volumeInLitres);
            dest.writeParcelable(ammoniaDosage, flags);
            dest.writeByte((byte) (isHeated ? 1 : 0));
            dest.writeByte((byte) (isSeeded ? 1 : 0));
            dest.writeLong(identifier);
            dest.writeInt(plantStatus);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Builder> CREATOR = new Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel in) {
                return new Builder(in);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };

        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        public Builder isHeated(boolean isHeated) {
            this.isHeated = isHeated;
            return this;
        }

        public Builder isSeeded(boolean isSeeded) {
            this.isSeeded = isSeeded;
            return this;
        }

        public Builder plantStatus(@PlantStatus int plantStatus) {
            this.plantStatus = plantStatus;
            return this;
        }

        public Builder volumeInLitres(float volumeInLitres) {
            this.volumeInLitres = volumeInLitres;
            return this;
        }

        public Builder ammoniaDosage(AmmoniaDosage ammoniaDosage) {
            this.ammoniaDosage = ammoniaDosage;
            return this;
        }

        public Builder ammoniaDosage(float dosage, float targetConcentration) {
            this.ammoniaDosage = new AmmoniaDosage(dosage, targetConcentration);
            return this;
        }

        public Builder identifier(long identifier) {
            this.identifier = identifier;
            return this;
        }

        public Tank build() {
            if(identifier < 0) {
                throw new IllegalArgumentException("'identifier' must be non-negative integer");
            }
            return new Tank(
                    name,
                    volumeInLitres,
                    ammoniaDosage,
                    isHeated,
                    isSeeded,
                    plantStatus,
                    identifier
            );
        }

        public String getName() {
            return name;
        }

        public float getVolumeInLitres() {
            return volumeInLitres;
        }

        public AmmoniaDosage getAmmoniaDosage() {
            return ammoniaDosage;
        }

        public boolean isHeated() {
            return isHeated;
        }

        public boolean isSeeded() {
            return isSeeded;
        }

        public long getIdentifier() {
            return identifier;
        }

        public int getPlantStatus() {
            return plantStatus;
        }
    }

}
