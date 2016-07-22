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
public class User implements Parcelable {
    @SerializedName("uid")
    public int uid;

    @SerializedName("name")
    public String name;

    @SerializedName("avatar_url")
    public String avatarUrl;

    @SerializedName("phone")
    public String phone;

    @SerializedName("gender")
    public int gender;

    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.avatarUrl);
        dest.writeString(this.phone);
        dest.writeInt(this.gender);
    }

    public User() {
    }

    private User(Parcel in) {
        this.uid = in.readInt();
        this.name = in.readString();
        this.avatarUrl = in.readString();
        this.phone = in.readString();
        this.gender = in.readInt();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[User uid: ").append(uid);
        if (name != null) {
            sb.append(", name:").append(name);
        }
        if (avatarUrl != null) {
            sb.append(", avatarUrl:").append(avatarUrl);
        }
        sb.append("]");
        return sb.toString();
    };
}
