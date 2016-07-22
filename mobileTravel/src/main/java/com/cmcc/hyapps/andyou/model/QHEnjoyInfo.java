package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/8/20.
 */
public class QHEnjoyInfo implements Parcelable {
    private int id;
    private int entertainment;
    private String content;
    private String image_url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.entertainment);
        dest.writeString(this.content);
        dest.writeString(this.image_url);
    }

    public QHEnjoyInfo() {
    }

    protected QHEnjoyInfo(Parcel in) {
        this.id = in.readInt();
        this.entertainment = in.readInt();
        this.content = in.readString();
        this.image_url = in.readString();
    }

    public static final Parcelable.Creator<QHEnjoyInfo> CREATOR = new Parcelable.Creator<QHEnjoyInfo>() {
        public QHEnjoyInfo createFromParcel(Parcel source) {
            return new QHEnjoyInfo(source);
        }

        public QHEnjoyInfo[] newArray(int size) {
            return new QHEnjoyInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getEntertainment() {
        return entertainment;
    }

    public String getContent() {
        return content;
    }

    public String getImage_url() {
        return image_url;
    }
}
