
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Pagination implements Parcelable {
    public static final Parcelable.Creator<Pagination> CREATOR = new Parcelable.Creator<Pagination>() {
        public Pagination createFromParcel(Parcel in) {
            return new Pagination(in);
        }

        public Pagination[] newArray(int size) {
            return new Pagination[size];
        }
    };
    @SerializedName("limit")
    public int limit;

    @SerializedName("offset")
    public int offset;

    @SerializedName("total")
    public int total;

    public Pagination(Parcel in) {
        this.limit = in.readInt();
        this.offset = in.readInt();
        this.total = in.readInt();
    }

    @Override
    public String toString() {
        return "Pagination [limit=" + limit + ", offset=" + offset + ", total=" + total + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.limit);
        dest.writeInt(this.offset);
        dest.writeInt(this.total);
    }
}
