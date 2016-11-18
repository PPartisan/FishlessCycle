package com.github.ppartisan.fishlesscycle.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class ImagePack implements Parcelable {

    private final String[] titles, paths;

    public ImagePack(String[] titles, String[] paths) {
        this.titles = titles;
        this.paths = paths;
    }

    protected ImagePack(Parcel in) {
        titles = in.createStringArray();
        paths = in.createStringArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(titles);
        dest.writeStringArray(paths);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImagePack> CREATOR = new Creator<ImagePack>() {
        @Override
        public ImagePack createFromParcel(Parcel in) {
            return new ImagePack(in);
        }

        @Override
        public ImagePack[] newArray(int size) {
            return new ImagePack[size];
        }
    };

    public String[] getTitles() {
        return titles;
    }

    public String[] getPaths() {
        return paths;
    }

}
