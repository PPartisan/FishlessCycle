package com.github.ppartisan.fishlesscycle.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class Reading implements Parcelable {

    public static final long CONTROL_DATE = Long.MIN_VALUE;

    public final long id, date;
    public final float ammonia, nitrite, nitrate;
    private String note;
    public final boolean isControl;

    public Reading(long id, long date, float ammonia, float nitrite, float nitrate, boolean isControl) {
        this.id = id;
        this.date = (isControl) ? CONTROL_DATE : date;
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

    @Override
    public int hashCode() {
        int result = 5;
        result = 31 * result + (int) (id^(id>>>32));
        result = 31 * result + (int) (date^(date>>>32));
        result = 31 * result + Float.floatToIntBits(ammonia);
        result = 31 * result + Float.floatToIntBits(nitrite);
        result = 31 * result + Float.floatToIntBits(nitrate);
        result = 31 * result + note.hashCode();
        result = 31 * result + (isControl ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==this)return true;
        if(!(obj instanceof Reading))return false;
        final Reading other = (Reading) obj;
        return this.id == other.id &&
                this.date == other.date &&
                this.ammonia == other.ammonia &&
                this.nitrite == other.nitrite &&
                this.nitrate == other.nitrate &&
                this.note.equals(other.note) &&
                this.isControl == other.isControl;
    }
}
