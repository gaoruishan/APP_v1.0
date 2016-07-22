/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author kuloud
 */
public class ItineraryScenic implements Parcelable {
    public static final Parcelable.Creator<ItineraryScenic> CREATOR = new Parcelable.Creator<ItineraryScenic>() {
        public ItineraryScenic createFromParcel(Parcel in) {
            return new ItineraryScenic(in);
        }

        public ItineraryScenic[] newArray(int size) {
            return new ItineraryScenic[size];
        }
    };

    @SerializedName("scenic_id")
    public int scenicId;

    @SerializedName("scenic_name")
    public String scenicName;

    public Location location;

    public ItineraryScenic(Parcel in) {
        this.scenicId = in.readInt();
        this.scenicName = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
    }

    public ItineraryScenic() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.scenicId);
        dest.writeString(this.scenicName);
        dest.writeParcelable(this.location, flags);
    }
}
