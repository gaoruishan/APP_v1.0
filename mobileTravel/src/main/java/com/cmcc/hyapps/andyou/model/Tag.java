package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gaoruishan on 15/7/22.
 */
public class Tag implements Parcelable {

    public int id;
    public String name;

    public Tag() {

    }

    public class TagList extends ResultList<Tag> {

    }

    public Tag(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public String toString() {
        return "Trip [id=" + id + ", name=" + name + "]";
    }
}