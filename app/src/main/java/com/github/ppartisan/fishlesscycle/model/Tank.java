package com.github.ppartisan.fishlesscycle.model;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Tank {

    //ToDo: Handle Reminders. Reminder class, time only..?

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE,LIGHT,HEAVY})
    public @interface PlantStatus {}
    public static final int NONE = 0;
    public static final int LIGHT = 5;
    public static final int HEAVY = 6;

    public final String name;
    public final float volumeInLitres, softenerDosagePerLitre;
    private final AmmoniaDosage ammoniaDosage;
    public final boolean isHeated, isSeeded;
    public final @PlantStatus int plantStatus;
    public final long identifier;

    private Tank(
            @NonNull String name,
            float volumeInLitres,
            float softenerDosagePerLitre,
            @NonNull AmmoniaDosage ammoniaDosage,
            boolean isHeated,
            boolean isSeeded,
            @PlantStatus int plantStatus,
            long identifier
    ) {

        this.name = name;
        this.volumeInLitres = volumeInLitres;
        this.softenerDosagePerLitre = softenerDosagePerLitre;
        this.ammoniaDosage = ammoniaDosage;
        this.isHeated = isHeated;
        this.isSeeded = isSeeded;
        this.plantStatus = plantStatus;
        this.identifier = identifier;

    }

    public AmmoniaDosage getAmmoniaDosage() {
        return ammoniaDosage;
    }

    public final static class Builder {

        private String name;
        private float volumeInLitres, softenerDosagePerLitre;
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
            this.softenerDosagePerLitre = tank.softenerDosagePerLitre;
            this.ammoniaDosage = new AmmoniaDosage(tank.getAmmoniaDosage());
            this.isHeated = tank.isHeated;
            this.isSeeded = tank.isSeeded;
            this.identifier = tank.identifier;
            this.plantStatus = tank.plantStatus;
        }

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

        public Builder softenerDosagePerLitre(float softenerDosagePerLitre) {
            this.softenerDosagePerLitre = softenerDosagePerLitre;
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
                    softenerDosagePerLitre,
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

        public float getSoftenerDosagePerLitre() {
            return softenerDosagePerLitre;
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
