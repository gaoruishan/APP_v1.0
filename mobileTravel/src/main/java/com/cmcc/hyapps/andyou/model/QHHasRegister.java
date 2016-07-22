package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/12/14.
 */
public class QHHasRegister implements Parcelable {
    private boolean emailExisted;

    public boolean isEmailExisted() {
        return emailExisted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(emailExisted ? (byte) 1 : (byte) 0);
    }

    public QHHasRegister() {
    }

    protected QHHasRegister(Parcel in) {
        this.emailExisted = in.readByte() != 0;
    }

    public static final Parcelable.Creator<QHHasRegister> CREATOR = new Parcelable.Creator<QHHasRegister>() {
        public QHHasRegister createFromParcel(Parcel source) {
            return new QHHasRegister(source);
        }

        public QHHasRegister[] newArray(int size) {
            return new QHHasRegister[size];
        }
    };
}
