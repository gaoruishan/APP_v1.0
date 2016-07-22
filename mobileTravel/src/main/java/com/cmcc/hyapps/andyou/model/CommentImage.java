
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;
public class CommentImage implements Parcelable {
    @SerializedName("image_url")
    public String image_url;

//    public String large_url;
//    public String small_url;
    @SerializedName("large_url")
    public String large_url;

    @SerializedName("small_url")
    public String small_url;

    @SerializedName("id")
    public int id;

    @Override
    public String toString() {
        return "CommentImage [image_url=" + image_url +", small_url=" + small_url+", large_url=" + large_url+", id=" + id +  "]";
    }

    public class CommentImageList {
        @SerializedName("list")
        public List<CommentImage> list;

        @Override
        public String toString() {
            return "CommentImageList [list=" + list + "]";
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image_url);

        this.large_url = this.image_url;
        this. small_url = this.image_url+"?imageView2/2/w/500/h/300";
        dest.writeString(this.small_url);
        dest.writeString(this.large_url);
        dest.writeInt(this.id);
    }

    public CommentImage() {
    }

    private CommentImage(Parcel in) {
        this.image_url = in.readString();
        this.small_url = in.readString();
        this.large_url = in.readString();
        this.id = in.readInt();

        this.large_url = this.image_url;
        this. small_url = this.image_url+"?imageView2/2/w/500/h/300";
    }

    public static final Creator<CommentImage> CREATOR = new Creator<CommentImage>() {
        public CommentImage createFromParcel(Parcel source) {
            return new CommentImage(source);
        }

        public CommentImage[] newArray(int size) {
            return new CommentImage[size];
        }
    };
}
