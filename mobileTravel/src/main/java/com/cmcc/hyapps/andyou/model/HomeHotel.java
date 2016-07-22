/**
 * 
 */

package com.cmcc.hyapps.andyou.model;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;


public  class HomeHotel implements Parcelable {

    @SerializedName("star")
    public float star;

    @SerializedName("desc")
    public String desc;

    @SerializedName("id")
    public String id;

    @SerializedName("business_id")
    public String business_id;

    @SerializedName("address")
    public String address;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("latitude")
    public String latitude;
    @SerializedName("mark")
    public String mark;
    @SerializedName("price")
    public String price;
    @SerializedName("telephone")
    public String telephone;
    @SerializedName("services")
    public String services;

    @SerializedName("image")
    public String[] imageUrls;
   /*@SerializedName("image")
   public List<String> imageUrls;*/


    @SerializedName("name")
    public String name;
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(desc);
        dest.writeFloat(star);
        dest.writeString(business_id);
        dest.writeString(address);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(mark);
        dest.writeString(price);
        dest.writeString(telephone);
        dest.writeString(services);
        dest.writeStringArray(this.imageUrls);




    }


    public HomeHotel() {
    }

    public HomeHotel(Parcel in) {
        name = in.readString();
        id = in.readString();
        desc = in.readString();
        star = in.readFloat();
        business_id = in.readString();
        address = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        mark = in.readString();
        price = in.readString();
        telephone = in.readString();
        services = in.readString();
        this.imageUrls = in.createStringArray();
    }

    public static final Creator<HomeHotel> CREATOR =
            new Creator<HomeHotel>() {
                @Override
                public HomeHotel createFromParcel(Parcel in) {
                    return new HomeHotel(in);
                }

                @Override
                public HomeHotel[] newArray(int size) {
                    return new HomeHotel[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "HomeHotel [name=" + name + ", id=" + id + ", desc="
                + desc + ", star=" + star+", business_id=" + business_id   + ", address=" + address + ", longitude=" + longitude
                +", latitude=" + latitude  + ", mark=" + mark +  ", price=" + price+ ", telephone=" + telephone+ ", services=" + services+
                ", imageUrls=" + imageUrls.toString()  + "]";
    }

    public static class HomeHotelList extends ResultList<HomeHotel> {
        @SerializedName("display_title")
        public String displayTitle;

        @Override
        public String toString() {
            return "HomeHotelList [list=" + list + ", pagination=" + pagination + ", displayTitle="
                    + displayTitle + "]";
        }

    }

}
