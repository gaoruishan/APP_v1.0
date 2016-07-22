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
public class Footprint implements Parcelable {
    public static final Parcelable.Creator<Footprint> CREATOR = new Parcelable.Creator<Footprint>() {
        public Footprint createFromParcel(Parcel in) {
            return new Footprint(in);
        }

        public Footprint[] newArray(int size) {
            return new Footprint[size];
        }
    };

    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("cover_image")
    public String coverImage;

    @SerializedName("create_time")
    public String createdTime;

    @SerializedName("location")
    public Location location;

    public Footprint(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.coverImage = in.readString();
        this.createdTime = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.coverImage);
        dest.writeString(this.createdTime);
        dest.writeParcelable(this.location, flags);
    }

    public static class FootprintList {
        @SerializedName("list")
        public ArrayList<Footprint> list;

        @SerializedName("pagination")
        public Pagination pagination;

        @Override
        public String toString() {
            return "FootprintList [list=" + list + ", pagination=" + pagination + "]";
        }

    }
}
