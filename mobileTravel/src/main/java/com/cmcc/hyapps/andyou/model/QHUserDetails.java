package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gaoruishan on 15/11/5.
 */
public class QHUserDetails implements Parcelable {

    /**
     * nickname : 小飞侠
     * userId : 266
     * avatarUrl : http://xxxx/xxxx/xxx.jpg
     * introduction : 论演员的自我修养
     * gender : 0
     * hasAttentioned : 0
     * payAttentionNum : 324
     * isAttentionedNum : 12
     * isBlackUser : 0
     * locationProvnice : 北京市
     * locationArea : 西城区
     */

    private String nickname;
    private String userId;
    private String avatarUrl;
    private String introduction;
    private int gender;
    private int hasAttentioned;
    private int payAttentionNum;
    private int isAttentionedNum;
    private int isBlackUser;
    private String locationProvnice;
    private String locationArea;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setHasAttentioned(int hasAttentioned) {
        this.hasAttentioned = hasAttentioned;
    }

    public void setPayAttentionNum(int payAttentionNum) {
        this.payAttentionNum = payAttentionNum;
    }

    public void setIsAttentionedNum(int isAttentionedNum) {
        this.isAttentionedNum = isAttentionedNum;
    }

    public void setIsBlackUser(int isBlackUser) {
        this.isBlackUser = isBlackUser;
    }

    public void setLocationProvnice(String locationProvnice) {
        this.locationProvnice = locationProvnice;
    }

    public void setLocationArea(String locationArea) {
        this.locationArea = locationArea;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserId() {
        return userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public int getGender() {
        return gender;
    }

    public int getHasAttentioned() {
        return hasAttentioned;
    }

    public int getPayAttentionNum() {
        return payAttentionNum;
    }

    public int getIsAttentionedNum() {
        return isAttentionedNum;
    }

    public int getIsBlackUser() {
        return isBlackUser;
    }

    public String getLocationProvnice() {
        return locationProvnice;
    }

    public String getLocationArea() {
        return locationArea;
    }

    @Override
    public String toString() {
        return "QHUserDetails{" +
                "nickname='" + nickname + '\'' +
                ", userId='" + userId + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", introduction='" + introduction + '\'' +
                ", gender=" + gender +
                ", hasAttentioned=" + hasAttentioned +
                ", payAttentionNum=" + payAttentionNum +
                ", isAttentionedNum=" + isAttentionedNum +
                ", isBlackUser=" + isBlackUser +
                ", locationProvnice='" + locationProvnice + '\'' +
                ", locationArea='" + locationArea + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickname);
        dest.writeString(this.userId);
        dest.writeString(this.avatarUrl);
        dest.writeString(this.introduction);
        dest.writeInt(this.gender);
        dest.writeInt(this.hasAttentioned);
        dest.writeInt(this.payAttentionNum);
        dest.writeInt(this.isAttentionedNum);
        dest.writeInt(this.isBlackUser);
        dest.writeString(this.locationProvnice);
        dest.writeString(this.locationArea);
    }

    public QHUserDetails() {
    }

    protected QHUserDetails(Parcel in) {
        this.nickname = in.readString();
        this.userId = in.readString();
        this.avatarUrl = in.readString();
        this.introduction = in.readString();
        this.gender = in.readInt();
        this.hasAttentioned = in.readInt();
        this.payAttentionNum = in.readInt();
        this.isAttentionedNum = in.readInt();
        this.isBlackUser = in.readInt();
        this.locationProvnice = in.readString();
        this.locationArea = in.readString();
    }

    public static final Parcelable.Creator<QHUserDetails> CREATOR = new Parcelable.Creator<QHUserDetails>() {
        public QHUserDetails createFromParcel(Parcel source) {
            return new QHUserDetails(source);
        }

        public QHUserDetails[] newArray(int size) {
            return new QHUserDetails[size];
        }
    };
}
