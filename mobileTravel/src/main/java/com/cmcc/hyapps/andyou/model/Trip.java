/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author kuloud
 */
public class Trip implements Parcelable {
    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
    @SerializedName("id")
    public int id;

    @SerializedName("create_time")
    public String createTime;

    @SerializedName("cover_image")
    public String coverImage;

    @SerializedName("title")
    public String title;

    @SerializedName("views_count")
    public int viewsCount;

    @SerializedName("comment_count")
    public int commentCount;

    @SerializedName("vote_count")
    public int voteCount;

    @SerializedName("author")
    public User author;

    /**
     * Local status flag {@link @Status}
     */
    public Status status = Status.IDLE;

    public Trip() {
    }

    public Trip(Parcel in) {
        this.id = in.readInt();
        this.createTime = in.readString();
        this.coverImage = in.readString();
        this.title = in.readString();
        this.viewsCount = in.readInt();
        this.commentCount = in.readInt();
        this.voteCount = in.readInt();
        this.author = in.readParcelable(User.class.getClassLoader());
        this.status = (Status) in.readSerializable();
    }

    @Override
    public String toString() {
        return "Trip [id=" + id + ", createTime=" + createTime + ", coverImage=" + coverImage
                + ", title=" + title + ", viewsCount=" + viewsCount + ", commentCount="
                + commentCount + ", voteCount=" + voteCount + ", author=" + author + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.createTime);
        dest.writeString(this.coverImage);
        dest.writeString(this.title);
        dest.writeInt(this.viewsCount);
        dest.writeInt(this.commentCount);
        dest.writeInt(this.voteCount);
        dest.writeParcelable(this.author, flags);
        dest.writeSerializable(this.status);
    }

    public static class TripList extends ResultList<Trip> {
        @Override
        public String toString() {
            return "TripList [list=" + list + ", pagination=" + pagination + "]";
        }

    }

    public enum Status {
        UNPUBLISH, UPDATED, IDLE
    }
}
