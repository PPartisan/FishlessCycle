package com.github.ppartisan.fishlesscycle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.ppartisan.fishlesscycle.data.Contract.ApiColorChartEntry.Categories;

public final class ApiColorChartItem implements Parcelable {

    public final @Categories int category;
    public final int color;
    public final float value;

    public ApiColorChartItem(@Categories int category, int color, float value) {
        this.category = category;
        this.color = color;
        this.value = value;
    }

    @SuppressWarnings("WrongConstant")
    private ApiColorChartItem(Parcel in) {
        category = in.readInt();
        color = in.readInt();
        value = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(category);
        dest.writeInt(color);
        dest.writeFloat(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApiColorChartItem> CREATOR = new Creator<ApiColorChartItem>() {
        @Override
        public ApiColorChartItem createFromParcel(Parcel in) {
            return new ApiColorChartItem(in);
        }

        @Override
        public ApiColorChartItem[] newArray(int size) {
            return new ApiColorChartItem[size];
        }
    };
}
