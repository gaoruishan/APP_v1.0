package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/11/9.
 * 朋友圈动态评论实体
 */
public class QHFriendsComment implements Parcelable {
    private int commentId;
    private String commentText;
    private String createTime;
    /**
     * nickname : 钢铁侠
     * userId : 206
     * avatarUrl : http://xxxx/xxxx/xxx.jpg
     */

    private QHFriendsUser fromUser;
    private QHFriendsUser toUser;
    private int type;

    public int getType() {
        return type;
    }

    public QHFriendsUser getToUser() {
        return toUser;
    }

    public QHFriendsUser getFromUser() {
        return fromUser;
    }

    public class QHFriendsCommentList extends ResultList<QHFriendsComment> {
    }

    public int getCommentId() {
        return commentId;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCreateTime() {
        return createTime;
    }


    public QHFriendsComment() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.commentId);
        dest.writeString(this.commentText);
        dest.writeString(this.createTime);
        dest.writeParcelable(this.fromUser, 0);
        dest.writeParcelable(this.toUser, 0);
        dest.writeInt(this.type);
    }

    protected QHFriendsComment(Parcel in) {
        this.commentId = in.readInt();
        this.commentText = in.readString();
        this.createTime = in.readString();
        this.fromUser = in.readParcelable(QHFriendsUser.class.getClassLoader());
        this.toUser = in.readParcelable(QHFriendsUser.class.getClassLoader());
        this.type = in.readInt();
    }

    public static final Creator<QHFriendsComment> CREATOR = new Creator<QHFriendsComment>() {
        public QHFriendsComment createFromParcel(Parcel source) {
            return new QHFriendsComment(source);
        }

        public QHFriendsComment[] newArray(int size) {
            return new QHFriendsComment[size];
        }
    };
}
