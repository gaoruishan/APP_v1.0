package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/5/25.
 */
public class QHCollectionRoute implements Parcelable {

    public int id;
    public int obj_id;
    public int obj_type;
    public String created;
    public int user;
    public String title;
    public String image_url;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.obj_id);
        dest.writeInt(this.obj_type);
        dest.writeString(this.created);
        dest.writeInt(this.user);
        dest.writeString(this.title);
        dest.writeString(this.image_url);
    }
    public QHCollectionRoute(Parcel in) {
        this.id = in.readInt();
        this.obj_id = in.readInt();
        this.obj_type = in.readInt();
        this.created = in.readString();
        this.user = in.readInt();
        this.title = in.readString();
        this.image_url = in.readString();
    }

    public static final Creator<QHCollectionRoute> CREATOR = new Creator<QHCollectionRoute>() {
        public QHCollectionRoute createFromParcel(Parcel source) {
            return new QHCollectionRoute(source);
        }

        public QHCollectionRoute[] newArray(int size) {
            return new QHCollectionRoute[size];
        }
    };

    public class List extends ResultList<QHCollectionRoute> {

    }

}
