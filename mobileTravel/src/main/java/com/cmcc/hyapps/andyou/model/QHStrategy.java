package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/25.
 */
public class QHStrategy implements Parcelable {
    public String type;

    public int id;
    public int scenic;
    public QHUser user;
    public String title;
    public String cover_image;
    public int vote_count;
    public int show_status;
    public List<QHGuideInfo> guide_info = new ArrayList<QHGuideInfo>();
    public String created;
    public String modified;
    public String shareURL;

    public int voted;//�����
    public int collected;//���
    public int comment_count;//���
    public String start_date;

    public QHStrategy() {

    }

    public String getTitle() {
        return title;
    }

    public String getShareURL() {
        return shareURL;
    }

    public String getCover_image() {
        return cover_image;
    }

    public class QHStrategyList extends ResultList<QHStrategy> {

    }

    public static class QHGuideInfo implements Parcelable {
        public int id;
        public int guide;
        public String content;
//        public int large_w;
//        public int large_h;
//        public int small_w;
//        public int small_h;
//        public String large_url;
//        public String small_url;
        public String image_url;
        public int show_status;

        @Override
        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeInt(this.guide);
            dest.writeString(this.content);
//            dest.writeInt(this.large_w);
//            dest.writeInt(this.large_h);
//            dest.writeInt(this.small_w);
//            dest.writeInt(this.small_h);
//            dest.writeString(this.large_url);
//            dest.writeString(this.small_url);
            dest.writeString(this.image_url);
            dest.writeInt(this.show_status);
        }

        public QHGuideInfo(Parcel in) {
            this.id = in.readInt();
            this.guide = in.readInt();
            this.content = in.readString();
//            this.large_w = in.readInt();
//            this.large_h = in.readInt();
//            this.small_w = in.readInt();
//            this.small_h = in.readInt();
//            this.large_url = in.readString();
//            this.small_url = in.readString();
            this.image_url = in.readString();
            this.show_status = in.readInt();
        }
        public static final Parcelable.Creator<QHGuideInfo> CREATOR = new Parcelable.Creator<QHGuideInfo>() {
            public QHGuideInfo createFromParcel(Parcel source) {
                return new QHGuideInfo(source);
            }

            public QHGuideInfo[] newArray(int size) {
                return new QHGuideInfo[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeInt(this.id);
        dest.writeInt(this.scenic);
        dest.writeParcelable(this.user, 0);
        dest.writeString(this.title);
        dest.writeString(this.cover_image);
        dest.writeInt(this.vote_count);
        dest.writeInt(this.show_status);
        dest.writeTypedList(guide_info);
        dest.writeString(this.created);
        dest.writeString(this.modified);
        dest.writeString(this.shareURL);
        dest.writeInt(this.voted);
        dest.writeInt(this.collected);
        dest.writeInt(this.comment_count);
        dest.writeString(this.start_date);
    }

    protected QHStrategy(Parcel in) {
        this.type = in.readString();
        this.id = in.readInt();
        this.scenic = in.readInt();
        this.user = in.readParcelable(QHUser.class.getClassLoader());
        this.title = in.readString();
        this.cover_image = in.readString();
        this.vote_count = in.readInt();
        this.show_status = in.readInt();
        this.guide_info = in.createTypedArrayList(QHGuideInfo.CREATOR);
        this.created = in.readString();
        this.modified = in.readString();
        this.shareURL = in.readString();
        this.voted = in.readInt();
        this.collected = in.readInt();
        this.comment_count = in.readInt();
        this.start_date = in.readString();
    }

    public static final Creator<QHStrategy> CREATOR = new Creator<QHStrategy>() {
        public QHStrategy createFromParcel(Parcel source) {
            return new QHStrategy(source);
        }

        public QHStrategy[] newArray(int size) {
            return new QHStrategy[size];
        }
    };
}
