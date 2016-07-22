package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/11/9.
 */
public class QHFriendsImage implements Parcelable {
    private String imgPath;

    public String getImgPath() {
        return imgPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgPath);
    }

    public QHFriendsImage() {
    }

    protected QHFriendsImage(Parcel in) {
        this.imgPath = in.readString();
    }

    public static final Parcelable.Creator<QHFriendsImage> CREATOR = new Parcelable.Creator<QHFriendsImage>() {
        public QHFriendsImage createFromParcel(Parcel source) {
            return new QHFriendsImage(source);
        }

        public QHFriendsImage[] newArray(int size) {
            return new QHFriendsImage[size];
        }
    };
}
