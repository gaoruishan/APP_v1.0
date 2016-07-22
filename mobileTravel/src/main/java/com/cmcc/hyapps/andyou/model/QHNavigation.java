package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/5/25.
 */
public class QHNavigation implements Parcelable {

    public String name;
    public float longitude;
    public float latitude;

    public QHNavigation(float longitude, float latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeFloat(this.longitude);
        dest.writeFloat(this.latitude);
    }

    public static final Creator<QHNavigation> CREATOR = new Creator<QHNavigation>() {
        public QHNavigation createFromParcel(Parcel source) {
            return new QHNavigation(source);
        }

        public QHNavigation[] newArray(int size) {
            return new QHNavigation[size];
        }
    };

    public QHNavigation(Parcel in) {
        this.name = in.readString();
        this.longitude = in.readFloat();
        this.latitude = in.readFloat();
    }

    public class QHNavigationList extends ResultList<QHNavigation> {

    }

}

