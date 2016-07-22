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
public class FootprintDetail implements Parcelable {
    public static final Parcelable.Creator<FootprintDetail> CREATOR = new Parcelable.Creator<FootprintDetail>() {
        public FootprintDetail createFromParcel(Parcel in) {
            return new FootprintDetail(in);
        }

        public FootprintDetail[] newArray(int size) {
            return new FootprintDetail[size];
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

    @SerializedName("scenic")
    public Scenic scenic;

    public FootprintDetail(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.coverImage = in.readString();
        this.createdTime = in.readString();
        this.scenic = in.readParcelable(Scenic.class.getClassLoader());
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
        dest.writeParcelable(this.scenic, flags);
    }

}
