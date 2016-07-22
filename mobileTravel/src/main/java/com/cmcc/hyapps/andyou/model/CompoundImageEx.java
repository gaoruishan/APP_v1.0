
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CompoundImageEx extends CompoundImage {
    @SerializedName("large_h")
    public int largeH;
    @SerializedName("large_w")
    public int largeW;

    @SerializedName("small_h")
    public int smallH;
    @SerializedName("small_w")
    public int smallW;

    public CompoundImageEx() {
        super();
    }

    public static final Parcelable.Creator<CompoundImageEx> CREATOR = new Parcelable.Creator<CompoundImageEx>() {
        public CompoundImageEx createFromParcel(Parcel in) {
            return new CompoundImageEx(in);
        }

        public CompoundImageEx[] newArray(int size) {
            return new CompoundImageEx[size];
        }
    };

    public CompoundImageEx(Parcel in) {
        super(in);
        this.largeH = in.readInt();
        this.largeW = in.readInt();
        this.smallH = in.readInt();
        this.smallW = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.largeH);
        dest.writeInt(this.largeW);
        dest.writeInt(this.smallH);
        dest.writeInt(this.smallW);
    }

    public float getLargeRadio() {
        return (largeH == 0) ? 1f : 1f * largeW / largeH;
    }

    public float getSmallRadio() {
        return (smallH == 0) ? 1f : 1f * smallW / smallH;
    }
}
