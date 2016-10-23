package com.github.ppartisan.fishlesscycle.model;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Tank {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE,LIGHT,HEAVY})
    public @interface PlantStatus {}
    public static final int NONE = 0;
    public static final int LIGHT = 5;
    public static final int HEAVY = 6;

    public final String name;
    public final boolean isHeated, isSeeded;
    public final @PlantStatus int plantStatus;
    public final long identifier;

    private Tank(@NonNull String name, boolean isHeated, boolean isSeeded, int plantStatus, long identifier) {

        if (identifier < 0) {
            throw new IllegalArgumentException("identifier' must be non-negative integer");
        }

        this.name = name;
        this.isHeated = isHeated;
        this.isSeeded = isSeeded;
        this.plantStatus = plantStatus;
        this.identifier = identifier;
    }

    public final static class Builder {

        private String name;
        private long identifier;
        private boolean isHeated, isSeeded;
        private @PlantStatus int plantStatus = NONE;

        public Builder(@NonNull String tankName) {
            this.name = tankName;
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

        public Builder identifier(long identifier) {
            this.identifier = identifier;
            return this;
        }

        public Tank build() {
            if(identifier < 0) {
                throw new IllegalArgumentException("'identifier' must be non-negative integer");
            }
            return new Tank(name, isHeated, isSeeded, plantStatus, identifier);
        }

    }

}
