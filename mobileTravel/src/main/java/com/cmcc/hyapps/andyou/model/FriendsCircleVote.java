package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/11/11.
 */
public class FriendsCircleVote implements Parcelable {
    private boolean successful;
    private String info;

    public boolean isSuccessful() {
        return successful;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(successful ? (byte) 1 : (byte) 0);
        dest.writeString(this.info);
    }

    public FriendsCircleVote() {
    }

    protected FriendsCircleVote(Parcel in) {
        this.successful = in.readByte() != 0;
        this.info = in.readString();
    }

    public static final Parcelable.Creator<FriendsCircleVote> CREATOR = new Parcelable.Creator<FriendsCircleVote>() {
        public FriendsCircleVote createFromParcel(Parcel source) {
            return new FriendsCircleVote(source);
        }

        public FriendsCircleVote[] newArray(int size) {
            return new FriendsCircleVote[size];
        }
    };
}
