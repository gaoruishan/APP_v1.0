
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MessageReadResponse implements Parcelable {
    @SerializedName("id")
    public int id;
    @SerializedName("obj_id")
    public int obj_id;
    @SerializedName("obj_type")
    public int obj_type;


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(obj_id);
        dest.writeInt(obj_type);

    }

    public MessageReadResponse() {
    }

    public MessageReadResponse(Parcel in) {
        id = in.readInt();
        obj_id = in.readInt();
        obj_type = in.readInt();
    }

    public static final Creator<MessageReadResponse> CREATOR =
            new Creator<MessageReadResponse>() {
                @Override
                public MessageReadResponse createFromParcel(Parcel in) {
                    return new MessageReadResponse(in);
                }

                @Override
                public MessageReadResponse[] newArray(int size) {
                    return new MessageReadResponse[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Scenic [id=" + id + ", object_id="
                + obj_id + ", obj_type=" + obj_type + "]";
    }
    public static class List extends ResultList<MessageReadResponse> {
    }
}
