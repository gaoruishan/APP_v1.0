package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/11/19.
 */
public class QHDelete implements Parcelable {
    private boolean successful;

    public boolean isSuccessful() {
        return successful;
    }

    public String getInfo() {
        return info;
    }

    private String info;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(successful ? (byte) 1 : (byte) 0);
        dest.writeString(this.info);
    }

    public QHDelete() {
    }

    protected QHDelete(Parcel in) {
        this.successful = in.readByte() != 0;
        this.info = in.readString();
    }

    public static final Parcelable.Creator<QHDelete> CREATOR = new Parcelable.Creator<QHDelete>() {
        public QHDelete createFromParcel(Parcel source) {
            return new QHDelete(source);
        }

        public QHDelete[] newArray(int size) {
            return new QHDelete[size];
        }
    };
}
