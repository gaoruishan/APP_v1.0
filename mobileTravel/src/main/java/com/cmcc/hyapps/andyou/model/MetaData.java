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
public class MetaData implements Parcelable {
    @SerializedName("scenic_id")
    public int scenic_id;

    @SerializedName("city")
    public String city;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.scenic_id);
        dest.writeString(this.city);

    }

    public MetaData() {
    }

    private MetaData(Parcel in) {
        this.scenic_id = in.readInt();
        this.city = in.readString();
    }

    public static final Creator<MetaData> CREATOR = new Creator<MetaData>() {
        public MetaData createFromParcel(Parcel source) {
            return new MetaData(source);
        }

        public MetaData[] newArray(int size) {
            return new MetaData[size];
        }
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[MetaData scenic_id: ").append(scenic_id);
        if (city != null) {
            sb.append(", city:").append(city);
        }
        sb.append("]");
        return sb.toString();
    };
}
