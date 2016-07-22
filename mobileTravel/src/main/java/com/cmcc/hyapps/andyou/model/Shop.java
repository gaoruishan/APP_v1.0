
package com.cmcc.hyapps.andyou.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Shop implements Parcelable {
    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("scenic_id")
    public int scenicId;

    @SerializedName("scenic_name")
    public String scenicName;

    @SerializedName("image")
    public String image;

    @SerializedName("telphone")
    public String telphone;

    @SerializedName("address")
    public String address;

    @SerializedName("location")
    public Location location;

    @SerializedName("special_goods")
    public List<Goods> specialGoods;

    @SerializedName("price")
    public float price;

    @SerializedName("rating")
    public float rating;

    public double distance;

    public Shop() {
    }

    public static Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
        public Shop createFromParcel(Parcel source) {
            return new Shop(source);
        }

        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };

    public class Goods {
        @SerializedName("name")
        public String name;

        @SerializedName("image")
        public String image;

        @SerializedName("price")
        public float price;
    }

    public static class ShopList extends ResultList<Shop> {

    }

    private Shop(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.scenicId = in.readInt();
        this.scenicName = in.readString();
        this.image = in.readString();
        this.telphone = in.readString();
        this.address = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.price = in.readFloat();
        this.rating = in.readFloat();
        this.distance = in.readDouble();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.scenicId);
        dest.writeString(this.scenicName);
        dest.writeString(this.image);
        dest.writeString(this.telphone);
        dest.writeString(this.address);
        dest.writeParcelable(this.location, flags);
        dest.writeFloat(this.price);
        dest.writeFloat(this.rating);
        dest.writeDouble(this.distance);
    }
}
