/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuloud
 */
public class ItineraryDay implements Parcelable {

    public static final Parcelable.Creator<ItineraryDay> CREATOR = new Parcelable.Creator<ItineraryDay>() {
        public ItineraryDay createFromParcel(Parcel in) {
            return new ItineraryDay(in);
        }

        public ItineraryDay[] newArray(int size) {
            return new ItineraryDay[size];
        }
    };
    @SerializedName("day")
    public String day;

    @SerializedName("plan_date")
    public String planDate;

    @SerializedName("scenic")
    public List<ItineraryScenic> scenics = new ArrayList<ItineraryScenic>();

    @SerializedName("scenic_name")
    public String scenicName;

    @SerializedName("city")
    public String city;

    public ItineraryDay(Parcel in) {
        this.day = in.readString();
        this.planDate = in.readString();
        in.readTypedList(this.scenics, ItineraryScenic.CREATOR);
        this.scenicName = in.readString();
        this.city = in.readString();
    }

    public ItineraryDay() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.day);
        dest.writeString(this.planDate);
        dest.writeTypedList(this.scenics);
        dest.writeString(this.scenicName);
        dest.writeString(this.city);
    }

    public String formatScenicsToString() {
        StringBuffer sb = new StringBuffer();
        for (ItineraryScenic itineraryScenic : scenics) {
            sb.append(itineraryScenic.scenicName).append(" ");
        }
        return sb.toString();
    }
}
