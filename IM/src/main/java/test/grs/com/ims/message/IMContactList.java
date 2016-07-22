package test.grs.com.ims.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gaoruishan on 15/9/25.
 */
public class IMContactList implements Parcelable{

    private int id;
    private String userName = "";
    private String nickname = "";
    private String phoneNumber = "";//注：此字段当作
    private String introduce = "";//用户介绍
    private boolean isFriend;//是否关注
    private boolean blackList;//黑名单
    private int msgNum = 0;
    private long time;
    private String message = "";
    private boolean groupChat = false; //群聊
    private String avatarurl = "";//头像
    private long guid;//时间戳
    private boolean top = false;//置顶
    private boolean ignore = false;//屏蔽


    protected IMContactList(Parcel in) {
        id = in.readInt();
        userName = in.readString();
        nickname = in.readString();
        phoneNumber = in.readString();
        msgNum = in.readInt();
        time = in.readLong();
        message = in.readString();
        avatarurl = in.readString();
        guid = in.readLong();
    }

    public static final Creator<IMContactList> CREATOR = new Creator<IMContactList>() {
        @Override
        public IMContactList createFromParcel(Parcel in) {
            return new IMContactList(in);
        }

        @Override
        public IMContactList[] newArray(int size) {
            return new IMContactList[size];
        }
    };

    public IMContactList() {

    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public long getGuid() {
        return guid;
    }

    public void setGuid(long guid) {
        this.guid = guid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(int msgNum) {
        this.msgNum = msgNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatarurl() {
        return avatarurl;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isBlackList() {
        return blackList;
    }

    public void setBlackList(boolean blackList) {
        this.blackList = blackList;
    }


    public boolean isGroupChat() {
        return groupChat;
    }

    public void setGroupChat(boolean groupChat) {
        this.groupChat = groupChat;
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.userName == null ? 0 : this.userName.hashCode());
        return result1;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            IMContactList other = (IMContactList) obj;
            if (this.userName == null) {
                if (other.userName != null) {
                    return false;
                }
            } else if (!this.userName.equals(other.userName)) {
                return false;
            }

            return true;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(userName);
        parcel.writeString(nickname);
        parcel.writeString(phoneNumber);
        parcel.writeInt(msgNum);
        parcel.writeLong(time);
        parcel.writeString(message);
        parcel.writeString(avatarurl);
        parcel.writeLong(guid);
    }

    @Override
    public String toString() {
        return "IMContactList{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", introduce='" + introduce + '\'' +
                ", blackList=" + blackList +
                ", isFriend=" + isFriend +
                ", msgNum=" + msgNum +
                ", time=" + time +
                ", message='" + message + '\'' +
                ", groupChat=" + groupChat +
                ", avatarurl='" + avatarurl + '\'' +
                ", guid=" + guid +
                ", top=" + top +
                ", ignore=" + ignore +
                '}';
    }
}
