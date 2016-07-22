package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public class QHComment implements Parcelable {

    public int id;
    public QHUser user;
    public String content;
    public int layer;
    public int vote_count;
    public int allow_reply;
    public int allow_vote;
    public int object_id;
    public int ctype;
    public List<CommentImage> comment_images = new ArrayList<CommentImage>();
    public String created;
    public String modified;

    public double longitude;
    public double latitude;
    public String address;

    public QHComment() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.content);
        dest.writeInt(this.layer);
        dest.writeInt(this.vote_count);
        dest.writeInt(this.allow_reply);
        dest.writeInt(this.allow_vote);
        dest.writeInt(this.object_id);
        dest.writeInt(this.ctype);
        dest.writeList(this.comment_images);
        dest.writeString(this.created);
        dest.writeString(this.modified);
    }

    public static final Parcelable.Creator<QHComment> CREATOR = new Parcelable.Creator<QHComment>() {
        public QHComment createFromParcel(Parcel source) {
            return new QHComment(source);
        }

        public QHComment[] newArray(int size) {
            return new QHComment[size];
        }
    };

    public QHComment(Parcel in) {
        this.id = in.readInt();
        this.user = in.readParcelable(QHUser.class.getClassLoader());
        this.content = in.readString();
        this.layer = in.readInt();
        this.vote_count = in.readInt();
        this.allow_reply = in.readInt();
        this.allow_vote = in.readInt();
        this.object_id = in.readInt();
        this.ctype = in.readInt();
        in.readList(comment_images,QHComment.class.getClassLoader());
        this.created = in.readString();
        this.modified = in.readString();
    }

    public static class CommentImage implements Parcelable {
        public int id;
        public int large_w;
        public int large_h;
        public int small_w;
        public int small_h;
        public String large_url;
        public String small_url;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeInt(this.large_w);
            dest.writeInt(this.large_h);
            dest.writeInt(this.small_w);
            dest.writeInt(this.small_h);
            dest.writeString(this.large_url);
            dest.writeString(this.small_url);
        }
        public static final Parcelable.Creator<CommentImage> CREATOR = new Parcelable.Creator<CommentImage>() {
            public CommentImage createFromParcel(Parcel source) {
                return new CommentImage(source);
            }

            public CommentImage[] newArray(int size) {
                return new CommentImage[size];
            }
        };

        public CommentImage(Parcel in) {
            this.id = in.readInt();
            this.large_w = in.readInt();
            this.large_h = in.readInt();
            this.small_w = in.readInt();
            this.small_h = in.readInt();
            this.large_url = in.readString();
            this.small_url = in.readString();
        }

    }
    public class QHCommentList extends ResultList<QHComment> implements Parcelable {
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.count);
            dest.writeString(this.next);
            dest.writeString(this.previous);
            dest.writeList(this.results);
        }
        public QHCommentList(Parcel in) {
            this.count = in.readInt();
            this.next = in.readString();
            this.previous = in.readString();
            in.readList(results,QHComment.class.getClassLoader());
        }

        public final Parcelable.Creator<QHCommentList> CREATOR = new Parcelable.Creator<QHCommentList>() {
            public QHCommentList createFromParcel(Parcel source) {
                return new QHCommentList(source);
            }

            public QHCommentList[] newArray(int size) {
                return new QHCommentList[size];
            }
        };
    }
}
