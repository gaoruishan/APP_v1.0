package com.cmcc.hyapps.andyou.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.cmcc.hyapps.andyou.util.PreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Administrator on 2015/5/29.
 */
public class QHTokenId implements Parcelable {

    public String token;
    public String jsessionid;
    private String dynamicKey;


    public QHTokenId() {
    }


    public String getDynamicKey() {
        return dynamicKey;
    }

    public void setDynamicKey(String dynamicKey) {
        this.dynamicKey = dynamicKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.jsessionid);
        dest.writeString(this.dynamicKey);
    }

    protected QHTokenId(Parcel in) {
        this.token = in.readString();
        this.jsessionid = in.readString();
        this.dynamicKey = in.readString();
    }

    public static final Parcelable.Creator<QHTokenId> CREATOR = new Parcelable.Creator<QHTokenId>() {
        public QHTokenId createFromParcel(Parcel source) {
            return new QHTokenId(source);
        }

        public QHTokenId[] newArray(int size) {
            return new QHTokenId[size];
        }
    };

    public void setJsessionid(String jsessionid) {
        this.jsessionid = jsessionid;
    }
}
