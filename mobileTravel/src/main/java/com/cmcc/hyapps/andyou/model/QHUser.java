package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/26.
 */
public class QHUser implements Parcelable{
    public int id;
    public String url;
    public String username;
    public String email;
    public List<String> groups = new ArrayList();
    public QHUserInfo user_info;
    private int result;
    private String date_joined;


    public String toString() {
        return "QHUser [id=" + id + ", url=" + url + ", username=" + username
                + ", email=" + email + ", user_info=" + user_info +"]";
    }


    public int getResult() {
        return result;
    }

    public QHUser() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.url);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeStringList(this.groups);
        dest.writeParcelable(this.user_info, 0);
        dest.writeInt(this.result);
        dest.writeString(this.date_joined);
    }

    protected QHUser(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.groups = in.createStringArrayList();
        this.user_info = in.readParcelable(QHUserInfo.class.getClassLoader());
        this.result = in.readInt();
        this.date_joined = in.readString();
    }

    public static final Creator<QHUser> CREATOR = new Creator<QHUser>() {
        public QHUser createFromParcel(Parcel source) {
            return new QHUser(source);
        }

        public QHUser[] newArray(int size) {
            return new QHUser[size];
        }
    };

    public String getDate_joined() {
        return date_joined;
    }
}
