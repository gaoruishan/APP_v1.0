package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/6/25 0025.
 */
public class Hotel implements Parcelable {
   @SerializedName("id")
   public int id;
   @SerializedName("name")
   public String name;
   @SerializedName("stype")
   public int stype ;
   @SerializedName("longitude")
   public double longitude;
   @SerializedName("latitude")
   public double latitude;
   @SerializedName("image_url")
   public String image_url;
   @SerializedName("introduction")
   public String introduction;
   @SerializedName("address")
   public String address;
   @SerializedName("telephone")
   public String telephone;
   @SerializedName("average")
   public int average;
   @SerializedName("promotion")
   public String promotion;
   @SerializedName("comment_count")
   public int comment_count;
   @SerializedName("recommend")
   public int recommend;

   @Override
   public String toString() {
      return "Hotel{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", stype=" + stype +
              ", longitude=" + longitude +
              ", latitude=" + latitude +
              ", image_url='" + image_url + '\'' +
              ", introduction='" + introduction + '\'' +
              ", address='" + address + '\'' +
              ", telephone='" + telephone + '\'' +
              ", average=" + average +
              ", promotion='" + promotion + '\'' +
              ", comment_count=" + comment_count +
              ", recommend=" + recommend +
              '}';
   }

   public static class HotelList extends ResultList<Hotel>{

   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.id);
      dest.writeString(this.name);
      dest.writeInt(this.stype);
      dest.writeDouble(this.longitude);
      dest.writeDouble(this.latitude);
      dest.writeString(this.image_url);
      dest.writeString(this.introduction);
      dest.writeString(this.address);
      dest.writeString(this.telephone);
      dest.writeInt(this.average);
      dest.writeString(this.promotion);
      dest.writeInt(this.comment_count);
      dest.writeInt(this.recommend);
   }

   public Hotel() {
   }

   protected Hotel(Parcel in) {
      this.id = in.readInt();
      this.name = in.readString();
      this.stype = in.readInt();
      this.longitude = in.readDouble();
      this.latitude = in.readDouble();
      this.image_url = in.readString();
      this.introduction = in.readString();
      this.address = in.readString();
      this.telephone = in.readString();
      this.average = in.readInt();
      this.promotion = in.readString();
      this.comment_count = in.readInt();
      this.recommend = in.readInt();
   }

   public static final Parcelable.Creator<Hotel> CREATOR = new Parcelable.Creator<Hotel>() {
      public Hotel createFromParcel(Parcel source) {
         return new Hotel(source);
      }

      public Hotel[] newArray(int size) {
         return new Hotel[size];
      }
   };
}
