package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by gaoruishan on 15/11/5.
 */
public class QHFriendInfo implements Parcelable {

    /**
     * infoId : 112
     * infoText : 我的动态，动态是我的！！求点赞啊！！
     * longitude : 101.851671359166
     * latitude : 36.5721334460024
     * address : 北京市西城区西便门内大街
     * isPublic : 1
     * createTime : 2015-11-2 15:14:34
     * commentNum : 168
     * praiseNum : 1880
     * isPraised : 1
     */
    private int infoId;
    private String infoText;
    private double longitude;
    private double latitude;
    private String address;
    private int isPublic;
    private String createTime;
    private int commentNum;
    private int praiseNum;
    private List<QHFriendsImage> images;

    public List<QHFriendsImage> getImages() {
        return images;
    }

    public void setPublishUser(QHFriendsUser publishUser) {
        this.publishUser = publishUser;
    }

    public QHFriendsUser getPublishUser() {

        return publishUser;
    }

    private int isPraised;
    private QHFriendsUser publishUser;

    public class QHFriendInfoList extends ResultList<QHFriendInfo>{}

    public void setInfoId(int infoId) {
        this.infoId = infoId;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public void setIsPraised(int isPraised) {
        this.isPraised = isPraised;
    }

    public int getInfoId() {
        return infoId;
    }

    public String getInfoText() {
        return infoText;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public int getIsPraised() {
        return isPraised;
    }

    public QHFriendInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.infoId);
        dest.writeString(this.infoText);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.address);
        dest.writeInt(this.isPublic);
        dest.writeString(this.createTime);
        dest.writeInt(this.commentNum);
        dest.writeInt(this.praiseNum);
        dest.writeTypedList(images);
        dest.writeInt(this.isPraised);
        dest.writeParcelable(this.publishUser, 0);
    }

    protected QHFriendInfo(Parcel in) {
        this.infoId = in.readInt();
        this.infoText = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.address = in.readString();
        this.isPublic = in.readInt();
        this.createTime = in.readString();
        this.commentNum = in.readInt();
        this.praiseNum = in.readInt();
        this.images = in.createTypedArrayList(QHFriendsImage.CREATOR);
        this.isPraised = in.readInt();
        this.publishUser = in.readParcelable(QHFriendsUser.class.getClassLoader());
    }

    public static final Creator<QHFriendInfo> CREATOR = new Creator<QHFriendInfo>() {
        public QHFriendInfo createFromParcel(Parcel source) {
            return new QHFriendInfo(source);
        }

        public QHFriendInfo[] newArray(int size) {
            return new QHFriendInfo[size];
        }
    };
}
