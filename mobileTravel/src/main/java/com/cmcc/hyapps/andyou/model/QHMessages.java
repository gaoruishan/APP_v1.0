package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by gaoruishan on 15/11/5.
 */
public class QHMessages implements Parcelable {

    /**
     * messageId : 101
     * messType : 0
     * contentText : 动态评论
     * createTime : 2015-11-2 17:17:51
     * sender : {"nickname":"189****1111","userId":"252","avatarUrl":"http://xxxx/xxxx/xxx.jpg"}
     */

    private int messageId;
    private int messType;
    private String contentText;
    private String createTime;
    private int hasRead;

    public int getInfoId() {
        return infoId;
    }

    private int infoId;

    public QHFriendsUser getFromUser() {
        return fromUser;
    }

    private QHFriendsUser fromUser;

    public QHFriendsUser getToUser() {
        return toUser;
    }

    private QHFriendsUser toUser;

    public QHoperateObject getOperateObject() {
        return operateObject;
    }

    private QHoperateObject operateObject;

    public class QHMessageList extends ResultList<QHMessages> {
    }

    public int getMessageId() {
        return messageId;
    }

    public int getMessType() {
        return messType;
    }

    public String getContentText() {
        return contentText;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getHasRead() {
        return hasRead;
    }


    public QHMessages() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.messageId);
        dest.writeInt(this.messType);
        dest.writeString(this.contentText);
        dest.writeString(this.createTime);
        dest.writeInt(this.hasRead);
        dest.writeInt(this.infoId);
        dest.writeParcelable(this.fromUser, 0);
        dest.writeParcelable(this.toUser, 0);
        dest.writeParcelable(this.operateObject, 0);
    }

    protected QHMessages(Parcel in) {
        this.messageId = in.readInt();
        this.messType = in.readInt();
        this.contentText = in.readString();
        this.createTime = in.readString();
        this.hasRead = in.readInt();
        this.infoId = in.readInt();
        this.fromUser = in.readParcelable(QHFriendsUser.class.getClassLoader());
        this.toUser = in.readParcelable(QHFriendsUser.class.getClassLoader());
        this.operateObject = in.readParcelable(QHoperateObject.class.getClassLoader());
    }

    public static final Creator<QHMessages> CREATOR = new Creator<QHMessages>() {
        public QHMessages createFromParcel(Parcel source) {
            return new QHMessages(source);
        }

        public QHMessages[] newArray(int size) {
            return new QHMessages[size];
        }
    };
}
