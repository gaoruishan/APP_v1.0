package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by bingbing on 2016/2/17.
 */
public class AreaTreeTag implements Parcelable {
    private int id;
    private String name;
    private List<AreaTreeTag> children;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(children);
    }

    public AreaTreeTag() {
    }

    public String getName() {
        return name;
    }

    public List<AreaTreeTag> getChildren() {
        return children;
    }

    public int getId() {
        return id;
    }

    public class AraeTreeTagList extends ResultList<AreaTreeTag> {
    }


    protected AreaTreeTag(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.children = in.createTypedArrayList(AreaTreeTag.CREATOR);
    }

    public static final Parcelable.Creator<AreaTreeTag> CREATOR = new Parcelable.Creator<AreaTreeTag>() {
        public AreaTreeTag createFromParcel(Parcel source) {
            return new AreaTreeTag(source);
        }

        public AreaTreeTag[] newArray(int size) {
            return new AreaTreeTag[size];
        }
    };
}
