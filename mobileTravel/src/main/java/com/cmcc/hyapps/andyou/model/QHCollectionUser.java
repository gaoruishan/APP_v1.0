package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/7/16 0016.
 */
public class QHCollectionUser implements Parcelable {
    public String nickname;
    public int id;
    public String avatar_url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickname);
        dest.writeInt(this.id);
        dest.writeString(this.avatar_url);
    }

    public QHCollectionUser() {
    }

    protected QHCollectionUser(Parcel in) {
        this.nickname = in.readString();
        this.id = in.readInt();
        this.avatar_url = in.readString();
    }

    public static final Parcelable.Creator<QHCollectionUser> CREATOR = new Parcelable.Creator<QHCollectionUser>() {
        public QHCollectionUser createFromParcel(Parcel source) {
            return new QHCollectionUser(source);
        }

        public QHCollectionUser[] newArray(int size) {
            return new QHCollectionUser[size];
        }
    };
}
