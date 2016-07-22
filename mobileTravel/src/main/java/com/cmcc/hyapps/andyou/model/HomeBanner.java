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
public class HomeBanner  implements Parcelable {
    public static final Parcelable.Creator<HomeBanner> CREATOR = new Parcelable.Creator<HomeBanner>() {
        public HomeBanner createFromParcel(Parcel in) {
            return new HomeBanner(in);
        }
        public HomeBanner[] newArray(int size) {
            return new HomeBanner[size];
        }
    };

    @SerializedName("type")
    public int type;

    @SerializedName("action")
    public String action;

    @SerializedName("meta_data")
    public MetaData meta_data;

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("title")
    public String title;






    public HomeBanner() {
    }
    public HomeBanner(Parcel in) {
        this.type = in.readInt();
        this.meta_data = in.readParcelable(MetaData.class.getClassLoader());
        this.imageUrl = in.readString();
        this.title = in.readString();
        this.action = in.readString();


    }

    @Override
    public String toString() {
        return "HomeBanner [type=" + type +", meta_data=" + meta_data+ ", imageUrl=" + imageUrl+ ", action=" + action +", title=" + title  + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeParcelable(this.meta_data, flags);

        dest.writeString(this.imageUrl);
        dest.writeString(this.title);
        dest.writeString(this.action);

    }

    public static class HomeBannerLists extends ResultList<HomeBanner> {
        @Override
        public String toString() {
            return "TripList [list=" + list + ", pagination=" + pagination + "]";
        }
    }

    public enum Status {
        UNPUBLISH, UPDATED, IDLE
    }
}
