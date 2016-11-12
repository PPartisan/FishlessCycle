package com.github.ppartisan.fishlesscycle.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.github.ppartisan.fishlesscycle.util.ReadingUtils;
import com.github.ppartisan.fishlesscycle.util.TankUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Tank {

    public static final float DEFAULT_TARGET_CONCENTRATION = 3f;

    //ToDo: Handle Reminders. Reminder class, time only..?

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE,LIGHT,HEAVY})
    public @interface PlantStatus {}
    public static final int NONE = 0;
    public static final int LIGHT = 5;
    public static final int HEAVY = 6;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NOT_STARTED, STARTED, CYCLING_AMMONIA, CYCLING_NITRITE,CYCLE_COMPLETE})
    public @interface TankStatus {}
    public static final int NOT_STARTED = 10;
    public static final int STARTED = 12;
    public static final int CYCLING_AMMONIA = 13;
    public static final int CYCLING_NITRITE = 14;
    public static final int CYCLE_COMPLETE = 15;

    public final String name, image;
    public final float volumeInLitres;
    private final AmmoniaDosage mAmmoniaDosage;
    private final Reading mLastReading;
    public final boolean isHeated, isSeeded;
    public final @PlantStatus int plantStatus;
    public final long identifier;

    private Tank(
            @NonNull String name,
            String image,
            float volumeInLitres,
            @NonNull AmmoniaDosage mAmmoniaDosage,
            boolean isHeated,
            boolean isSeeded,
            @PlantStatus int plantStatus,
            Reading lastReading,
            long identifier
    ) {

        this.name = name;
        this.image = image;
        this.volumeInLitres = volumeInLitres;
        this.isHeated = isHeated;
        this.isSeeded = isSeeded;
        this.plantStatus = plantStatus;
        this.identifier = identifier;
        this.mAmmoniaDosage = mAmmoniaDosage;
        this.mLastReading = lastReading;
    }

    public AmmoniaDosage getAmmoniaDosage() {

        return mAmmoniaDosage;
    }

    public Reading getLastReading() {
        return mLastReading;
    }

    public final static class Builder implements Parcelable {

        private String name, image;
        private float volumeInLitres;
        private AmmoniaDosage ammoniaDosage;
        private Reading lastReading;
        private Reading controlReading;
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
            this.lastReading = tank.getLastReading();
            this.controlReading = null;
            this.isHeated = tank.isHeated;
            this.isSeeded = tank.isSeeded;
            this.identifier = tank.identifier;
            this.plantStatus = tank.plantStatus;
            this.identifier = tank.identifier;
        }

        @SuppressWarnings("WrongConstant")
        protected Builder(Parcel in) {
            name = in.readString();
            image = in.readString();
            volumeInLitres = in.readFloat();
            ammoniaDosage = in.readParcelable(AmmoniaDosage.class.getClassLoader());
            lastReading = in.readParcelable(Reading.class.getClassLoader());
            controlReading = in.readParcelable(Reading.class.getClassLoader());
            isHeated = in.readByte() != 0;
            isSeeded = in.readByte() != 0;
            identifier = in.readLong();
            plantStatus = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(image);
            dest.writeFloat(volumeInLitres);
            dest.writeParcelable(ammoniaDosage, flags);
            dest.writeParcelable(lastReading, flags);
            dest.writeParcelable(controlReading, flags);
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

        public Builder setLastReading(Reading reading) {
            this.lastReading = reading;
            return this;
        }

        public Builder setControlReading(Reading reading) {
            this.controlReading = reading;
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
                    lastReading,
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

        @NonNull
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

        @Nullable
        public Reading getLastReading() {
            return lastReading;
        }

        @Nullable
        public Reading getControlReading() {
            return controlReading;
        }

        @Override
        public int hashCode() {
            int result = 19;
            result = 31 * result + name.hashCode();
            result = 31 * result + image.hashCode();
            result = 31 * result + Float.floatToIntBits(volumeInLitres);
            result = 31 * result + ammoniaDosage.hashCode();
            result = 31 * result + lastReading.hashCode();
            result = 31 * result + controlReading.hashCode();
            result = 31 * result + (isHeated ? 1 : 0);
            result = 31 * result + (isSeeded ? 1 : 0);
            result = 31 * result + (int) (identifier^(identifier>>>32));
            result = 31 * result + plantStatus;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(!(obj instanceof Tank.Builder)) return false;
            final Tank.Builder other = (Tank.Builder) obj;
            return TextUtils.equals(this.image, other.image) &&
                    TextUtils.equals(this.image, other.image) &&
                    this.volumeInLitres == other.volumeInLitres &&
                    TankUtils.equals(this.ammoniaDosage, other.ammoniaDosage) &&
                    ReadingUtils.equals(this.lastReading, other.lastReading) &&
                    ReadingUtils.equals(this.controlReading, other.controlReading) &&
                    this.isHeated == other.isHeated &&
                    this.isSeeded == other.isSeeded &&
                    this.identifier == other.identifier &&
                    this.plantStatus == other.plantStatus;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "name='" + name + '\'' +
                    ", image='" + image + '\'' +
                    ", volumeInLitres=" + volumeInLitres +
                    ", ammoniaDosage=" + ammoniaDosage +
                    ", lastReading=" + lastReading +
                    ", controlReading=" + controlReading +
                    ", isHeated=" + isHeated +
                    ", isSeeded=" + isSeeded +
                    ", identifier=" + identifier +
                    ", plantStatus=" + plantStatus +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Tank{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", volumeInLitres=" + volumeInLitres +
                ", mAmmoniaDosage=" + mAmmoniaDosage +
                ", mLastReading=" + mLastReading +
                ", isHeated=" + isHeated +
                ", isSeeded=" + isSeeded +
                ", plantStatus=" + plantStatus +
                ", identifier=" + identifier +
                '}';
    }
}
