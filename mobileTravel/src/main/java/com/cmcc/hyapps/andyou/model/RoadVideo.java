package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/10/8.
 */
public class RoadVideo implements Parcelable {
    private int id;
    private String video_name;
    private String image_url;

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    private int update_uid;
    private int area_id;
    private String video_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getUpdate_uid() {
        return update_uid;
    }

    public void setUpdate_uid(int update_uid) {
        this.update_uid = update_uid;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.video_name);
        dest.writeString(this.image_url);
        dest.writeInt(this.update_uid);
        dest.writeInt(this.area_id);
        dest.writeString(this.video_url);
    }

    public RoadVideo() {
    }

    public class RoadVideoList extends ResultList<RoadVideo>{}
    protected RoadVideo(Parcel in) {
        this.id = in.readInt();
        this.video_name = in.readString();
        this.image_url = in.readString();
        this.update_uid = in.readInt();
        this.area_id = in.readInt();
        this.video_url = in.readString();
    }

    public static final Parcelable.Creator<RoadVideo> CREATOR = new Parcelable.Creator<RoadVideo>() {
        public RoadVideo createFromParcel(Parcel source) {
            return new RoadVideo(source);
        }

        public RoadVideo[] newArray(int size) {
            return new RoadVideo[size];
        }
    };
}
