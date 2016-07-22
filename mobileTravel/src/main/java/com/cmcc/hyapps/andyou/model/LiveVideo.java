
package com.cmcc.hyapps.andyou.model;


import android.os.Parcel;
import android.os.Parcelable;

public class LiveVideo implements Parcelable {

    public int id;
    public String video_day;
    public String video_night;
    public String image_url;

    public LiveVideo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.video_day);
        dest.writeString(this.video_night);
        dest.writeString(this.image_url);
    }

    public LiveVideo(Parcel in) {
        this.id = in.readInt();
        this.video_day = in.readString();
        this.video_night = in.readString();
        this.image_url = in.readString();
    }

    public static final Parcelable.Creator<LiveVideo> CREATOR = new Parcelable.Creator<LiveVideo>() {
        public LiveVideo createFromParcel(Parcel source) {
            return new LiveVideo(source);
        }

        public LiveVideo[] newArray(int size) {
            return new LiveVideo[size];
        }
    };

    public static class LiveVideoList extends ResultList<LiveVideo> {

    }
}