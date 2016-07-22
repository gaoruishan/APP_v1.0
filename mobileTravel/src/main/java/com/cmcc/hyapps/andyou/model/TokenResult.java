
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TokenResult implements Parcelable {
    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
    }

    public TokenResult() {
    }

    protected TokenResult(Parcel in) {
        this.token = in.readString();
    }

    public static final Parcelable.Creator<TokenResult> CREATOR = new Parcelable.Creator<TokenResult>() {
        public TokenResult createFromParcel(Parcel source) {
            return new TokenResult(source);
        }

        public TokenResult[] newArray(int size) {
            return new TokenResult[size];
        }
    };
}
