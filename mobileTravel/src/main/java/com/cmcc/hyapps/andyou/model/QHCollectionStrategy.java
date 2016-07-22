package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/5/25.
 */
public class QHCollectionStrategy implements Parcelable {

    public int id;
    public int obj_id;
    public int obj_type;
    public String created;
   // public QHCollectionUser guide_created_user;
    public QHUser guide_created_user;
    public String title;
    public String image_url;
    public int vote_count;


    public class List extends ResultList<QHCollectionStrategy> {

    }


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
        dest.writeParcelable(this.guide_created_user, 0);
        dest.writeString(this.title);
        dest.writeString(this.image_url);
        dest.writeInt(this.vote_count);
    }

    public QHCollectionStrategy() {
    }

    protected QHCollectionStrategy(Parcel in) {
        this.id = in.readInt();
        this.obj_id = in.readInt();
        this.obj_type = in.readInt();
        this.created = in.readString();
        this.guide_created_user = in.readParcelable(QHUser.class.getClassLoader());
        this.title = in.readString();
        this.image_url = in.readString();
        this.vote_count = in.readInt();
    }

    public static final Parcelable.Creator<QHCollectionStrategy> CREATOR = new Parcelable.Creator<QHCollectionStrategy>() {
        public QHCollectionStrategy createFromParcel(Parcel source) {
            return new QHCollectionStrategy(source);
        }

        public QHCollectionStrategy[] newArray(int size) {
            return new QHCollectionStrategy[size];
        }
    };
}
