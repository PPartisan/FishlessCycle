package com.github.ppartisan.fishlesscycle.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class Reading implements Parcelable {

    public static final int NO_ID = -1;

    public final long id, date;
    public final float ammonia, nitrite, nitrate;
    private String note;
    public boolean isControl;

    public Reading(long id, long date, float ammonia, float nitrite, float nitrate, boolean isControl) {
        this.id = id;
        this.date = date;
        this.ammonia = ammonia;
        this.nitrite = nitrite;
        this.nitrate = nitrate;
        this.isControl = isControl;
    }

    protected Reading(Parcel in) {
        id = in.readLong();
        date = in.readLong();
        ammonia = in.readFloat();
        nitrite = in.readFloat();
        nitrate = in.readFloat();
        note = in.readString();
        isControl = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(date);
        dest.writeFloat(ammonia);
        dest.writeFloat(nitrite);
        dest.writeFloat(nitrate);
        dest.writeString(note);
        dest.writeByte((byte) (isControl ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Reading> CREATOR = new Creator<Reading>() {
        @Override
        public Reading createFromParcel(Parcel in) {
            return new Reading(in);
        }

        @Override
        public Reading[] newArray(int size) {
            return new Reading[size];
        }
    };

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

}
