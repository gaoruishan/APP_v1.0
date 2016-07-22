/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;


public  class HomeRestuarantDetail extends HomeBase {

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


    @SerializedName("telephone")
    public String telephone;
    @SerializedName("online_reservation_url")
    public String online_reservation_url;

    @SerializedName("popular_dishes")
    public String[] popular_dishes;

    @SerializedName("specialties")
    public String[] specialties;

    @SerializedName("categories")
    public String[] categories;


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
        dest.writeString(online_reservation_url);
        dest.writeStringArray(this.popular_dishes);
        dest.writeStringArray(this.specialties);
        dest.writeStringArray(this.categories);
    }

    public HomeRestuarantDetail() {
    }

    public HomeRestuarantDetail(Parcel in) {
        name = in.readString();
        imageUrl = in.readString();
        avg_rating = in.readString();
        business_id = in.readString();
        address = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        photo_url = in.readString();
        online_reservation_url = in.readString();
        in.readStringArray(this.popular_dishes);
        in.readStringArray(this.specialties);
        in.readStringArray(this.categories);
    }

    public static final Creator<HomeRestuarantDetail> CREATOR =
            new Creator<HomeRestuarantDetail>() {
                @Override
                public HomeRestuarantDetail createFromParcel(Parcel in) {
                    return new HomeRestuarantDetail(in);
                }

                @Override
                public HomeRestuarantDetail[] newArray(int size) {
                    return new HomeRestuarantDetail[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

   /* @Override
    public String toString() {
        return "HomeRestuarant [name=" + name + ", imageUrl=" + imageUrl + ", avg_rating="
                + avg_rating + ", business_id=" + business_id + ", address=" + address + ", longitude=" + longitude+
                ", latitude=" + latitude  +", photo_url=" + photo_url  +", s_photo_url=" + s_photo_url  + "]";
    }*/



}
