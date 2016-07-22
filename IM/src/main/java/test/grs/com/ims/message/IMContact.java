package test.grs.com.ims.message;

/**
 * Created by gaoruishan on 15/9/25.
 */
public class IMContact {

    private int id;
    private String userName = "";
    private String nickname = "";
    private String phoneNumber = "";
    private String groupId = "";
    private boolean blackList;//黑名单
    private int msgNum = 0;
    private long time;
    private String message = "";
    private boolean groupChat = false; //true 群聊
    private String avatarurl = "";//头像
    private long guid;//时间戳
    private boolean top = false;//置顶
    private boolean ignore = false;//屏蔽
    private boolean save = true;//保存

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
            IMContact other = (IMContact) obj;
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
    public String toString() {
        return "IMContact{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", groupId='" + groupId + '\'' +
                ", blackList=" + blackList +
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
