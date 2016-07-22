package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2016/1/5.
 */
public class AESResponse implements Parcelable {
    public String getData() {
        return data;
    }

    private String data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.data);
    }

    public AESResponse() {
    }

    protected AESResponse(Parcel in) {
        this.data = in.readString();
    }

    public static final Parcelable.Creator<AESResponse> CREATOR = new Parcelable.Creator<AESResponse>() {
        public AESResponse createFromParcel(Parcel source) {
            return new AESResponse(source);
        }

        public AESResponse[] newArray(int size) {
            return new AESResponse[size];
        }
    };
}
