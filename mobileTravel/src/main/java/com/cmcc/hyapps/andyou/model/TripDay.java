/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author kuloud
 */
public class TripDay implements Parcelable {
    public static final Parcelable.Creator<TripDay> CREATOR = new Parcelable.Creator<TripDay>() {
        public TripDay createFromParcel(Parcel in) {
            return new TripDay(in);
        }

        public TripDay[] newArray(int size) {
            return new TripDay[size];
        }
    };
    @SerializedName("content")
    public String content;

    @SerializedName("image")
    public CompoundImageEx image;

    @SerializedName("time")
    public String time;

    @SerializedName("scenic_id")
    public int scenicId = -1;

    @SerializedName("scenic_name")
    public String scenicName;

    @SerializedName("location")
    public Location location;

    @SerializedName("city")
    public String city;

    public int itemType = 0;//0 item,1 header ,2 date
    public String date  ="";//日期
    public int  day = 0;//第几日
    public int  index = -1;//第几个item

    public TripDay(Parcel in) {
        this.content = in.readString();
        this.image = in.readParcelable(Image.class.getClassLoader());
        this.time = in.readString();
        this.scenicId = in.readInt();
        this.scenicName = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.city = in.readString();
    }

    public TripDay() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.time);
        dest.writeInt(this.scenicId);
        dest.writeString(this.scenicName);
        dest.writeParcelable(this.location, flags);
        dest.writeString(this.city);
    }

    public static class TripDayList {
        @SerializedName("days")
        public ArrayList<TripDay> days;

        @SerializedName("trip_id")
        public int tripId;

        @Override
        public String toString() {
            return "TripDayList [days=" + days + ", tripId=" + tripId + "]";
        }

    }

    public enum Status {
        DRAFT, PUSHING, PUSH_FAILED, PUSHED
    }
}
