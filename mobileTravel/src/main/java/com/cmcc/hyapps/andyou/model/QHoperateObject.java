package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/11/24.
 */
public class QHoperateObject implements Parcelable {
    private int objectId;
    private String text;

    public int getObjectId() {
        return objectId;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.objectId);
        dest.writeString(this.text);
    }

    public QHoperateObject() {
    }

    protected QHoperateObject(Parcel in) {
        this.objectId = in.readInt();
        this.text = in.readString();
    }

    public static final Parcelable.Creator<QHoperateObject> CREATOR = new Parcelable.Creator<QHoperateObject>() {
        public QHoperateObject createFromParcel(Parcel source) {
            return new QHoperateObject(source);
        }

        public QHoperateObject[] newArray(int size) {
            return new QHoperateObject[size];
        }
    };
}
