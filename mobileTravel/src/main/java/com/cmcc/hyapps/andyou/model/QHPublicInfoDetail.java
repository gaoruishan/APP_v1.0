package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by gaoruishan on 15/11/5.
 * 朋友圈动态详细信息实体
 */
public class QHPublicInfoDetail implements Parcelable {

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
     * images : [{"imgPath":"http://xxxx:8080/media/friends/12212.jpg"},{"imgPath":"http://xxxx:8080/media/friends/12213.jpg"},{"imgPath":"http://xxxx:8080/media/friends/12214.jpg"}]
     * publishUser : {"nickname":"小飞侠","userId":"266","avatarUrl":"http://xxxx/xxxx/xxx.jpg"}
     * praiseUsers : [{"nickname":"丘比特","userId":"268","avatarUrl":"http://xxxx/xxxx/xxx.jpg"},{"nickname":"bob","userId":"269","avatarUrl":"http://xxxx/xxxx/xxx.jpg"}]
     * comments : [{"commentId":1213,"commentText":"这个不错，我知道是你的动态！","createTime":"2015-11-2 15:14:34","sender":{"nickname":"钢铁侠","userId":"206","avatarUrl":"http://xxxx/xxxx/xxx.jpg"},"reply":{}},{"commentId":1243,"commentText":"废话，不是你的动态是谁的动态？","createTime":"2015-11-2 15:14:34","sender":{"nickname":"雷神","userId":"299","avatarUrl":"http://xxxx/xxxx/xxx.jpg"},"reply":{"commentId":2223,"commentText":"这个？我是为了测试朋友的功能！呵呵！","createTime":"2015-11-2 15:14:34","sender":{"nickname":"小飞侠","userId":"266","avatarUrl":"http://xxxx/xxxx/xxx.jpg"},"reply":{}}}]
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
    private int isPraised;
    /**
     * nickname : 小飞侠
     * userId : 266
     * avatarUrl : http://xxxx/xxxx/xxx.jpg
     */

    private QHFriendsUser publishUser;
    /**
     * imgPath : http://xxxx:8080/media/friends/12212.jpg
     */

    private List<QHFriendsImage> images;
    /**
     * nickname : 丘比特
     * userId : 268
     * avatarUrl : http://xxxx/xxxx/xxx.jpg
     */

    private List<QHFriendsUser> praiseUsers;


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

    public QHFriendsUser getPublishUser() {
        return publishUser;
    }

    public List<QHFriendsImage> getImages() {
        return images;
    }

    public List<QHFriendsUser> getPraiseUsers() {
        return praiseUsers;
    }


    public QHPublicInfoDetail() {
    }

    public int getIsPraised() {
        return isPraised;
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
        dest.writeInt(this.isPraised);
        dest.writeParcelable(this.publishUser, 0);
        dest.writeTypedList(images);
        dest.writeTypedList(praiseUsers);
    }

    protected QHPublicInfoDetail(Parcel in) {
        this.infoId = in.readInt();
        this.infoText = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.address = in.readString();
        this.isPublic = in.readInt();
        this.createTime = in.readString();
        this.commentNum = in.readInt();
        this.praiseNum = in.readInt();
        this.isPraised = in.readInt();
        this.publishUser = in.readParcelable(QHFriendsUser.class.getClassLoader());
        this.images = in.createTypedArrayList(QHFriendsImage.CREATOR);
        this.praiseUsers = in.createTypedArrayList(QHFriendsUser.CREATOR);
    }

    public static final Creator<QHPublicInfoDetail> CREATOR = new Creator<QHPublicInfoDetail>() {
        public QHPublicInfoDetail createFromParcel(Parcel source) {
            return new QHPublicInfoDetail(source);
        }

        public QHPublicInfoDetail[] newArray(int size) {
            return new QHPublicInfoDetail[size];
        }
    };
}
