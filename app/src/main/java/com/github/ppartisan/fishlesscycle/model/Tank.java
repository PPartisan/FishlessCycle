package com.github.ppartisan.fishlesscycle.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

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

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NOT_STARTED,CYCLING_AMMONIA,CYCLING_NITRITE,CYCLING_NITRATE,CYCLE_COMPLETE})
    public @interface TankStatus {}
    public static final int NOT_STARTED = 0;
    public static final int CYCLING_AMMONIA = 10;
    public static final int CYCLING_NITRITE = 11;
    public static final int CYCLING_NITRATE = 12;
    public static final int CYCLE_COMPLETE = 50;

    public final String name, image;
    public final float volumeInLitres;
    private final AmmoniaDosage ammoniaDosage;
    public final boolean isHeated, isSeeded;
    public final @PlantStatus int plantStatus;
    public final @TankStatus int tankStatus;
    public final long identifier;

    private Tank(
            @NonNull String name,
            String image,
            float volumeInLitres,
            @NonNull AmmoniaDosage ammoniaDosage,
            boolean isHeated,
            boolean isSeeded,
            @PlantStatus int plantStatus,
            @TankStatus int tankStatus,
            long identifier
    ) {

        this.name = name;
        this.image = image;
        this.volumeInLitres = volumeInLitres;
        this.isHeated = isHeated;
        this.isSeeded = isSeeded;
        this.plantStatus = plantStatus;
        this.identifier = identifier;
        this.tankStatus = tankStatus;
        this.ammoniaDosage = ammoniaDosage;

    }

    @SuppressWarnings("WrongConstant")
    protected Tank(Parcel in) {
        this.name = in.readString();
        this.image = in.readString();
        this.volumeInLitres = in.readFloat();
        this.ammoniaDosage = in.readParcelable(AmmoniaDosage.class.getClassLoader());
        this.isHeated = in.readByte() != 0;
        this.isSeeded = in.readByte() != 0;
        this.plantStatus = in.readInt();
        this.tankStatus = in.readInt();
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
        parcel.writeString(image);
        parcel.writeFloat(volumeInLitres);
        parcel.writeParcelable(ammoniaDosage, i);
        parcel.writeByte((byte) (isHeated ? 1 : 0));
        parcel.writeByte((byte) (isSeeded ? 1 : 0));
        parcel.writeInt(plantStatus);
        parcel.writeInt(tankStatus);
        parcel.writeLong(identifier);
    }

    public AmmoniaDosage getAmmoniaDosage() {

        return ammoniaDosage;
    }

    public final static class Builder implements Parcelable {

        private String name, image;
        private float volumeInLitres;
        private AmmoniaDosage ammoniaDosage;
        private boolean isHeated, isSeeded;
        private long identifier;
        private @PlantStatus int plantStatus = NONE;
        private @TankStatus int tankStatus = NOT_STARTED;

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
            this.tankStatus = tank.tankStatus;
            this.identifier = tank.identifier;
        }

        @SuppressWarnings("WrongConstant")
        protected Builder(Parcel in) {
            name = in.readString();
            image = in.readString();
            volumeInLitres = in.readFloat();
            ammoniaDosage = in.readParcelable(AmmoniaDosage.class.getClassLoader());
            isHeated = in.readByte() != 0;
            isSeeded = in.readByte() != 0;
            identifier = in.readLong();
            plantStatus = in.readInt();
            tankStatus = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(image);
            dest.writeFloat(volumeInLitres);
            dest.writeParcelable(ammoniaDosage, flags);
            dest.writeByte((byte) (isHeated ? 1 : 0));
            dest.writeByte((byte) (isSeeded ? 1 : 0));
            dest.writeLong(identifier);
            dest.writeInt(plantStatus);
            dest.writeInt(tankStatus);
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

        public Builder setName(@NonNull String name) {
            this.name = name;
            return this;
        }

        public Builder setImage(String image) {
            this.image = image;
            return this;
        }

        public Builder setIsHeated(boolean isHeated) {
            this.isHeated = isHeated;
            return this;
        }

        public Builder setIsSeeded(boolean isSeeded) {
            this.isSeeded = isSeeded;
            return this;
        }

        public Builder setPlantStatus(@PlantStatus int plantStatus) {
            this.plantStatus = plantStatus;
            return this;
        }

        public Builder setTankStatus(@TankStatus int tankStatus) {
            this.tankStatus = tankStatus;
            return this;
        }

        public Builder setVolumeInLitres(float volumeInLitres) {
            this.volumeInLitres = volumeInLitres;
            return this;
        }

        public Builder setAmmoniaDosage(AmmoniaDosage ammoniaDosage) {
            this.ammoniaDosage = ammoniaDosage;
            return this;
        }

        public Builder setAmmoniaDosage(float dosage, float targetConcentration) {
            this.ammoniaDosage = new AmmoniaDosage(dosage, targetConcentration);
            return this;
        }

        public Builder setIdentifier(long identifier) {
            this.identifier = identifier;
            return this;
        }

        public Tank build() {
            if(identifier < 0) {
                throw new IllegalArgumentException("'setIdentifier' must be non-negative integer");
            }
            return new Tank(
                    name,
                    image,
                    volumeInLitres,
                    ammoniaDosage,
                    isHeated,
                    isSeeded,
                    plantStatus,
                    tankStatus,
                    identifier
            );
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public float getVolumeInLitres() {
            return volumeInLitres;
        }

        public AmmoniaDosage getAmmoniaDosage() {

            if (ammoniaDosage == null) {
                ammoniaDosage = new AmmoniaDosage(0, DEFAULT_TARGET_CONCENTRATION);
            }

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

        public @PlantStatus int getPlantStatus() {
            return plantStatus;
        }

        public @TankStatus int getTankStatus() {
            return tankStatus;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "name='" + name + '\'' +
                    ", image='" + image + '\'' +
                    ", volumeInLitres=" + volumeInLitres +
                    ", ammoniaDosage=" + ammoniaDosage +
                    ", isHeated=" + isHeated +
                    ", isSeeded=" + isSeeded +
                    ", identifier=" + identifier +
                    ", plantStatus=" + plantStatus +
                    ", tankStatus=" + tankStatus +
                    '}';
        }
    }

}
