package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/12/14.
 */
public class QHRegister implements Parcelable {

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

    public QHRegister() {
    }

    protected QHRegister(Parcel in) {
        this.successful = in.readByte() != 0;
        this.info = in.readString();
    }

    public static final Parcelable.Creator<QHRegister> CREATOR = new Parcelable.Creator<QHRegister>() {
        public QHRegister createFromParcel(Parcel source) {
            return new QHRegister(source);
        }

        public QHRegister[] newArray(int size) {
            return new QHRegister[size];
        }
    };
}
