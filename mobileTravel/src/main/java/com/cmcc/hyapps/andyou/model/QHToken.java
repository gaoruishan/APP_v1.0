package com.cmcc.hyapps.andyou.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;

/**
 * Created by Administrator on 2015/5/29.
 */
public class QHToken implements Parcelable {
    private static final String KEY_TOKEN_INFO = "key_token_info";
    private boolean successful;
    private int errorcode;
    public String token;
    public int userId;
    public String password;
    private String info;
    public static QHToken getTokenInfo(Context context) {
        String json = PreferencesUtils.getString(context, KEY_TOKEN_INFO);
        QHToken info = null;
        try {
            info = new Gson().fromJson(json, QHToken.class);
        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return info;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "QHToken{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public QHToken() {
    }

    public int getErrorcode() {
        return errorcode;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(successful ? (byte) 1 : (byte) 0);
        dest.writeInt(this.errorcode);
        dest.writeString(this.token);
        dest.writeInt(this.userId);
        dest.writeString(this.password);
        dest.writeString(this.info);
    }

    protected QHToken(Parcel in) {
        this.successful = in.readByte() != 0;
        this.errorcode = in.readInt();
        this.token = in.readString();
        this.userId = in.readInt();
        this.password = in.readString();
        this.info = in.readString();
    }

    public static final Creator<QHToken> CREATOR = new Creator<QHToken>() {
        public QHToken createFromParcel(Parcel source) {
            return new QHToken(source);
        }

        public QHToken[] newArray(int size) {
            return new QHToken[size];
        }
    };

    public String getInfo() {
        return info;
    }
}
