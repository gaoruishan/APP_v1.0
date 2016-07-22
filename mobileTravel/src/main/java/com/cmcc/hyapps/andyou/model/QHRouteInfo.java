package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/7/15 0015.
 */
public class QHRouteInfo implements Parcelable {
    private int id;
    private int route;
    private String content;
    private String image_url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.route);
        dest.writeString(this.content);
        dest.writeString(this.image_url);
    }

    public QHRouteInfo() {
    }

    public int getId() {
        return id;
    }

    public int getRoute() {
        return route;
    }

    public String getContent() {
        return content;
    }

    public String getImage_url() {
        return image_url;
    }

    protected QHRouteInfo(Parcel in) {
        this.id = in.readInt();
        this.route = in.readInt();
        this.content = in.readString();
        this.image_url = in.readString();
    }

    public static final Parcelable.Creator<QHRouteInfo> CREATOR = new Parcelable.Creator<QHRouteInfo>() {
        public QHRouteInfo createFromParcel(Parcel source) {
            return new QHRouteInfo(source);
        }

        public QHRouteInfo[] newArray(int size) {
            return new QHRouteInfo[size];
        }
    };
}
