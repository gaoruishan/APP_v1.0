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
public class ItineraryInfo implements Parcelable {
    public static final Parcelable.Creator<ItineraryInfo> CREATOR = new Parcelable.Creator<ItineraryInfo>() {
        public ItineraryInfo createFromParcel(Parcel in) {
            return new ItineraryInfo(in);
        }

        public ItineraryInfo[] newArray(int size) {
            return new ItineraryInfo[size];
        }
    };
    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("create_time")
    public String createTime;

    @SerializedName("end_time")
    public String endTime;

    @SerializedName("start_time")
    public String startTime;

    @SerializedName("days")
    public List<ItineraryDay> days = new ArrayList<ItineraryDay>();

    public ItineraryInfo(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.createTime = in.readString();
        this.endTime = in.readString();
        this.startTime = in.readString();
        in.readTypedList(this.days, ItineraryDay.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.createTime);
        dest.writeString(this.endTime);
        dest.writeString(this.startTime);
        dest.writeTypedList(this.days);
    }

    public static class ItineraryInfoList extends ResultList<ItineraryInfo> {
        @Override
        public String toString() {
            return "ItineraryInfoList [list=" + list + ", pagination=" + pagination + "]";
        }

    }
}
