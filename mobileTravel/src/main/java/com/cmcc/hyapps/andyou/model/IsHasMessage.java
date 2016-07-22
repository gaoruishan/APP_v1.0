package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/11/10.
 */
public class IsHasMessage implements Parcelable {
    private boolean had;

    public boolean isHad() {
        return had;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(had ? (byte) 1 : (byte) 0);
    }

    public IsHasMessage() {
    }

    protected IsHasMessage(Parcel in) {
        this.had = in.readByte() != 0;
    }

    public static final Parcelable.Creator<IsHasMessage> CREATOR = new Parcelable.Creator<IsHasMessage>() {
        public IsHasMessage createFromParcel(Parcel source) {
            return new IsHasMessage(source);
        }

        public IsHasMessage[] newArray(int size) {
            return new IsHasMessage[size];
        }
    };
}
