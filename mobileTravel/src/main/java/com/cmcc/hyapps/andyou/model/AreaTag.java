package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/10/8.
 */
public class AreaTag implements Parcelable {
    private int id;

    public int getId() {
        return id;
    }

    private String name;
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.type);
    }

    public AreaTag() {
    }

    public AreaTag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public class AraeTagList extends ResultList<AreaTag> {
    }

    protected AreaTag(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<AreaTag> CREATOR = new Parcelable.Creator<AreaTag>() {
        public AreaTag createFromParcel(Parcel source) {
            return new AreaTag(source);
        }

        public AreaTag[] newArray(int size) {
            return new AreaTag[size];
        }
    };
}
