package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/6/1.
 */
public class QHUserInfo implements Parcelable{

    public String nickname ="";
    public int gender;
    public String avatar_url="";
    private String location_provnice;
    private String location_area;
    private String introduction;

    public String getLocation_provnice() {
        return location_provnice;
    }

    public String getLocation_area() {
        return location_area;
    }

    public String getIntroduction() {
        return introduction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickname);
        dest.writeInt(this.gender);
        dest.writeString(this.avatar_url);
        dest.writeString(this.location_provnice);
        dest.writeString(this.location_area);
        dest.writeString(this.introduction);
    }

    public QHUserInfo() {
    }

    protected QHUserInfo(Parcel in) {
        this.nickname = in.readString();
        this.gender = in.readInt();
        this.avatar_url = in.readString();
        this.location_provnice = in.readString();
        this.location_area = in.readString();
        this.introduction = in.readString();
    }

    public static final Creator<QHUserInfo> CREATOR = new Creator<QHUserInfo>() {
        public QHUserInfo createFromParcel(Parcel source) {
            return new QHUserInfo(source);
        }

        public QHUserInfo[] newArray(int size) {
            return new QHUserInfo[size];
        }
    };

    public void setLocation_provnice(String location_provnice) {
        this.location_provnice = location_provnice;
    }

    public void setLocation_area(String location_area) {
        this.location_area = location_area;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}