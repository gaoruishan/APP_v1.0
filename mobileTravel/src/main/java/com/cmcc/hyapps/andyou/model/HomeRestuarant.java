/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public  class HomeRestuarant extends HomeBase {
   /* photo_url	string	大图地址
    s_photo_url	string
    小图地址*/
    @SerializedName("avg_rating")
    public String avg_rating;

    @SerializedName("business_id")
    public String business_id;

    @SerializedName("address")
    public String address;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("photo_url")
    public String photo_url;

    @SerializedName("s_photo_url")
    public String s_photo_url;
    @SerializedName("price")
    public String price;
    @SerializedName("distance")
    public String distance;
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(avg_rating);
        dest.writeString(business_id);
        dest.writeString(address);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(photo_url);
        dest.writeString(s_photo_url);
        dest.writeString(price);
        dest.writeString(distance);
    }

    public HomeRestuarant() {
    }

    public HomeRestuarant(Parcel in) {
        name = in.readString();
        imageUrl = in.readString();
        avg_rating = in.readString();
        business_id = in.readString();
        address = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        photo_url = in.readString();
        s_photo_url = in.readString();
        price = in.readString();
        distance = in.readString();
    }

    public static final Parcelable.Creator<HomeRestuarant> CREATOR =
            new Parcelable.Creator<HomeRestuarant>() {
                @Override
                public HomeRestuarant createFromParcel(Parcel in) {
                    return new HomeRestuarant(in);
                }

                @Override
                public HomeRestuarant[] newArray(int size) {
                    return new HomeRestuarant[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "HomeRestuarant [name=" + name + ", imageUrl=" + imageUrl + ", avg_rating="
                + avg_rating + ", business_id=" + business_id + ", address=" + address + ", longitude=" + longitude+
                ", latitude=" + latitude  +", photo_url=" + photo_url  +", s_photo_url=" + s_photo_url  +", price=" + price  +", distance=" + distance  + "]";
    }

    public static class HomeRestuarantList extends ResultList<HomeRestuarant> {
        @SerializedName("display_title")
        public String displayTitle;

        @Override
        public String toString() {
            return "HomeRestuarantList [list=" + list + ", pagination=" + pagination + ", displayTitle="
                    + displayTitle + "]";
        }

    }

}
