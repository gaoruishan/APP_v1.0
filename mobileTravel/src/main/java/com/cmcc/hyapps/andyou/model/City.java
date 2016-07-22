
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class City implements Parcelable {
    @SerializedName("city_code")
    public String code;

    @SerializedName("city_name")
    public String name;

    @SerializedName("location")
    public Location location;

    @SerializedName("is_hot")
    public boolean isHot;

    @Override
    public String toString() {
        return "City [code=" + code + ", name=" + name + ", location=" + location + ", isHot="
                + isHot + "]";
    }

    public class CityList {
        @SerializedName("list")
        public List<City> list;

        @Override
        public String toString() {
            return "CityList [list=" + list + "]";
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeParcelable(this.location, 0);
        dest.writeByte(isHot ? (byte) 1 : (byte) 0);
    }

    public City() {
    }

    private City(Parcel in) {
        this.code = in.readString();
        this.name = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.isHot = in.readByte() != 0;
    }

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
