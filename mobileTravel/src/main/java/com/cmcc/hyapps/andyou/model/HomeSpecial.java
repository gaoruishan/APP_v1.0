/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;


public  class HomeSpecial extends HomeBase {

    @SerializedName("description")
    public String description;

    @SerializedName("url")
    public String url;

    @SerializedName("url_desc")
    public String url_desc;

    @SerializedName("city")
    public String city;


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(url_desc);
        dest.writeString(city);

    }

    public HomeSpecial() {
    }

    public HomeSpecial(Parcel in) {
        name = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        url = in.readString();
        url_desc = in.readString();
        city = in.readString();

    }

    public static final Creator<HomeSpecial> CREATOR =
            new Creator<HomeSpecial>() {
                @Override
                public HomeSpecial createFromParcel(Parcel in) {
                    return new HomeSpecial(in);
                }

                @Override
                public HomeSpecial[] newArray(int size) {
                    return new HomeSpecial[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "HomeRestuarant [name=" + name + ", imageUrl=" + imageUrl + ", description="
                + description + ", url=" + url + ", url_desc=" + url_desc + ", city=" + city  + "]";
    }

    public static class HomeSpecialList extends ResultList<HomeSpecial> {
        @SerializedName("display_title")
        public String displayTitle;

        @Override
        public String toString() {
            return "HomeRestuarantList [list=" + list + ", pagination=" + pagination + ", displayTitle="
                    + displayTitle + "]";
        }

    }

}
