
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Message implements Parcelable {
    @SerializedName("id")
    public int id;
    @SerializedName("from_user")
    public QHUser from_user;
    @SerializedName("to_user")
    public QHUser to_user;
    @SerializedName("object_id")
    public int object_id;
    @SerializedName("ctype")
    public int ctype;
    @SerializedName("title")
    public String title;
    @SerializedName("read")
    public int read;


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(this.from_user, flags);
        dest.writeParcelable(this.to_user, flags);
        dest.writeInt(object_id);
        dest.writeInt(ctype);
        dest.writeString(title);
        dest.writeInt(read);

    }

    public Message() {
    }

    public Message(Parcel in) {
        id = in.readInt();
        ClassLoader loader=  QHUser.class.getClassLoader();
        this.from_user = in.readParcelable(loader);
        ClassLoader loader2=  QHUser.class.getClassLoader();
        this.to_user = in.readParcelable(loader2);
        object_id = in.readInt();
        ctype = in.readInt();
        title = in.readString();
        read = in.readInt();
    }

    public static final Creator<Message> CREATOR =
            new Creator<Message>() {
                @Override
                public Message createFromParcel(Parcel in) {
                    return new Message(in);
                }

                @Override
                public Message[] newArray(int size) {
                    return new Message[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Scenic [id=" + id + ", from_user=" + from_user + ", to_user=" + to_user + ", object_id="
                + object_id + ", ctype=" + ctype + ", title=" + title + ", read=" + read+ "]";
    }
    public static class List extends ResultList<Message> {
    }
}
