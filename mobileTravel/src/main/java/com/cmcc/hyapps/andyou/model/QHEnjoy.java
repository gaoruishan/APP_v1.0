package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class QHEnjoy implements Parcelable {
    public int id;
    public String title;
    public String address;
    public float longitude;
    public float latitude;
    public String intro_text;
    public String cover_image;
    public int vote_count;
    public int comment_count;
    public int voted;
    public int collected;
    public String shareURL;
    public List<QHEnjoyInfo> entertainment_info = new ArrayList<QHEnjoyInfo>();


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIntro_text() {
        return intro_text;
    }

    public String getCover_image() {
        return cover_image;
    }

    public int getVote_count() {
        return vote_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public int getVoted() {
        return voted;
    }

    public int getCollected() {
        return collected;
    }

    public List<QHEnjoyInfo> getList() {
        return entertainment_info;
    }

    public String getShareURL() {
        return shareURL;
    }

    public List<QHEnjoyInfo> getEntertainment_info() {
        return entertainment_info;
    }

    public class QHEnjoyList extends ResultList<QHEnjoy>{}

    public QHEnjoy() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.address);
        dest.writeFloat(this.longitude);
        dest.writeFloat(this.latitude);
        dest.writeString(this.intro_text);
        dest.writeString(this.cover_image);
        dest.writeInt(this.vote_count);
        dest.writeInt(this.comment_count);
        dest.writeInt(this.voted);
        dest.writeInt(this.collected);
        dest.writeString(this.shareURL);
        dest.writeTypedList(entertainment_info);
    }

    protected QHEnjoy(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.address = in.readString();
        this.longitude = in.readFloat();
        this.latitude = in.readFloat();
        this.intro_text = in.readString();
        this.cover_image = in.readString();
        this.vote_count = in.readInt();
        this.comment_count = in.readInt();
        this.voted = in.readInt();
        this.collected = in.readInt();
        this.shareURL = in.readString();
        this.entertainment_info = in.createTypedArrayList(QHEnjoyInfo.CREATOR);
    }

    public static final Creator<QHEnjoy> CREATOR = new Creator<QHEnjoy>() {
        public QHEnjoy createFromParcel(Parcel source) {
            return new QHEnjoy(source);
        }

        public QHEnjoy[] newArray(int size) {
            return new QHEnjoy[size];
        }
    };
}
