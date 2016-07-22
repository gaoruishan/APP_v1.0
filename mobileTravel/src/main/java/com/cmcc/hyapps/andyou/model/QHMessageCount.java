package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 消息条数目
 * Created by bingbing on 2015/11/12.
 */
public class QHMessageCount implements Parcelable {
    public void setNewMessageNum(int newMessageNum) {
        this.newMessageNum = newMessageNum;
    }

    private int newMessageNum;

    public int getNewMessageNum() {
        return newMessageNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.newMessageNum);
    }

    public QHMessageCount() {
    }

    protected QHMessageCount(Parcel in) {
        this.newMessageNum = in.readInt();
    }

    public static final Parcelable.Creator<QHMessageCount> CREATOR = new Parcelable.Creator<QHMessageCount>() {
        public QHMessageCount createFromParcel(Parcel source) {
            return new QHMessageCount(source);
        }

        public QHMessageCount[] newArray(int size) {
            return new QHMessageCount[size];
        }
    };
}
