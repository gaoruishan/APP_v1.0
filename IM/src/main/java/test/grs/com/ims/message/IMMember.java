package test.grs.com.ims.message;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by gaoruishan on 15/9/16.
 */

public class IMMember implements Serializable, Parcelable {
    public static final int TYPE_COMMON = 1;
    public static final int TYPE_ADD = 2;
    public static final int TYPE_REMOVE = 3;

    private String avatarUri;
    private String name;
    private String userName;// 用户名，唯一标示
    private int type;
    private String groupId="";
    public IMMember() {

    }

    public IMMember(String avatarUri, String name, String userName, int type) {
        this.avatarUri = avatarUri;
        this.name = name;
        this.userName = userName;
        this.type = type;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setAvatarUri(String uri) {
        this.avatarUri = uri;
    }

    public String getAvatarUri() {
        return this.avatarUri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatarUri);
        dest.writeString(this.name);
        dest.writeString(this.userName);
        dest.writeInt(this.type);
        dest.writeString(this.groupId);
    }

    protected IMMember(Parcel in) {
        this.avatarUri = in.readString();
        this.name = in.readString();
        this.userName = in.readString();
        this.type = in.readInt();
        this.groupId = in.readString();
    }

    public static final Parcelable.Creator<IMMember> CREATOR = new Parcelable.Creator<IMMember>() {
        public IMMember createFromParcel(Parcel source) {
            return new IMMember(source);
        }

        public IMMember[] newArray(int size) {
            return new IMMember[size];
        }
    };
}