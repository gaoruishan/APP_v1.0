package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/11/6.
 */
public class QHFriendsUser implements Parcelable {
    private String nickname;
    private String userId;
    private String avatarUrl;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickname);
        dest.writeString(this.userId);
        dest.writeString(this.avatarUrl);
    }

    public QHFriendsUser() {
    }

    protected QHFriendsUser(Parcel in) {
        this.nickname = in.readString();
        this.userId = in.readString();
        this.avatarUrl = in.readString();
    }

    public static final Parcelable.Creator<QHFriendsUser> CREATOR = new Parcelable.Creator<QHFriendsUser>() {
        public QHFriendsUser createFromParcel(Parcel source) {
            return new QHFriendsUser(source);
        }

        public QHFriendsUser[] newArray(int size) {
            return new QHFriendsUser[size];
        }
    };
}
