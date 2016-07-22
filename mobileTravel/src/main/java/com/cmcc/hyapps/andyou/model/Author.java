
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Author implements Parcelable {
    public static final Parcelable.Creator<Author> CREATOR = new Parcelable.Creator<Author>() {
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
    @SerializedName("uid")
    public long uid;

    @SerializedName("avatar_url")
    public String avatarUrl;

    @SerializedName("name")
    public String name;

    @SerializedName("gender")
    public int gender;

    public Author() {
    }
    public String toString() {
        return "Author [uid=" + uid + ", avatarUrl=" + avatarUrl + ", name=" + name+ ", gender=" + gender
                +"]";
    }
    public static Author fromUser(User u) {
        Author author = new Author();
        author.avatarUrl = u.avatarUrl;
        author.gender = u.gender;
        author.name = u.name;
        author.uid = u.uid;
        return author;
    }

    public Author(Parcel in) {
        this.uid = in.readLong();
        this.avatarUrl = in.readString();
        this.name = in.readString();
        this.gender = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.uid);
        dest.writeString(this.avatarUrl);
        dest.writeString(this.name);
        dest.writeInt(this.gender);
    }
}
