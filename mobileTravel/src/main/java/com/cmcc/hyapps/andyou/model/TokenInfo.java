/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author kuloud
 */
public class TokenInfo implements Parcelable {

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("expire_in")
    public long expireIn;

    @SerializedName("token_type")
    public String tokenType;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accessToken);
        dest.writeString(this.refreshToken);
        dest.writeLong(this.expireIn);
        dest.writeString(this.tokenType);
    }

    public TokenInfo() {
    }

    private TokenInfo(Parcel in) {
        this.accessToken = in.readString();
        this.refreshToken = in.readString();
        this.expireIn = in.readLong();
        this.tokenType = in.readString();
    }

    public static final Parcelable.Creator<TokenInfo> CREATOR = new Parcelable.Creator<TokenInfo>() {
        public TokenInfo createFromParcel(Parcel source) {
            return new TokenInfo(source);
        }

        public TokenInfo[] newArray(int size) {
            return new TokenInfo[size];
        }
    };
}
