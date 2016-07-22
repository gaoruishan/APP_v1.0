
package com.cmcc.hyapps.andyou.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.cmcc.hyapps.andyou.app.ServerAPI;

public class Video implements Parcelable {
    @SerializedName("file_size")
    public long size;

    @SerializedName("file_url")
    public String url;

    @SerializedName("created_location")
    public Location location;

    @SerializedName("created_time")
    public String createdTime;

    @SerializedName("duration")
    public long duration;

    @SerializedName("title")
    public String title;

    @SerializedName("thumbnail")
    public String thumbnail;

    @SerializedName("width")
    public int width;

    @SerializedName("height")
    public int height;

    @SerializedName("play_times")
    public int playTimes;

    @SerializedName("mime_type")
    public String mimeType;

    @SerializedName("scenic_id")
    public int scenicId;

    public int liveId;

    public Video() {
    }

    public static Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdTime == null) ? 0 : createdTime.hashCode());
        result = prime * result + (int) (duration ^ (duration >>> 32));
        result = prime * result + height;
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + playTimes;
        result = prime * result + scenicId;
        result = prime * result + (int) (size ^ (size >>> 32));
        result = prime * result + ((thumbnail == null) ? 0 : thumbnail.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + width;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Video other = (Video) obj;
        if (createdTime == null) {
            if (other.createdTime != null)
                return false;
        } else if (!createdTime.equals(other.createdTime))
            return false;
        if (duration != other.duration)
            return false;
        if (height != other.height)
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (mimeType == null) {
            if (other.mimeType != null)
                return false;
        } else if (!mimeType.equals(other.mimeType))
            return false;
        if (playTimes != other.playTimes)
            return false;
        if (scenicId != other.scenicId)
            return false;
        if (size != other.size)
            return false;
        if (thumbnail == null) {
            if (other.thumbnail != null)
                return false;
        } else if (!thumbnail.equals(other.thumbnail))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (width != other.width)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Video [size=" + size + ", url=" + url + ", location=" + location + ", createdTime="
                + createdTime + ", duration=" + duration + ", title=" + title + ", thumbnail="
                + thumbnail + ", width=" + width + ", height=" + height + ", playTimes="
                + playTimes + ", mimeType=" + mimeType + ", scenicId=" + scenicId + "]";
    }

    public static class VideoList extends ResultList<Video> implements IOfflinePackage {

        @SerializedName("display_title")
        public String displayTitle;

        @Override
        public String toString() {
            return "VideoList [list=" + list + ", pagination=" + pagination + ", displayTitle="
                    + displayTitle + "]";
        }

        @Override
        public void setOfflinePathRoot(String pathRoot) {
            if (list == null) {
                return;
            }

            for (Video video : list) {
                if (video.url != null) {
                    video.url = pathRoot + video.url;
                }

                if (video.thumbnail != null) {
                    video.thumbnail = pathRoot + video.thumbnail;
                }
            }

        }

        @Override
        public String getOfflineFileName() {
            return Uri.parse(ServerAPI.ScenicVideos.URL).getLastPathSegment();
        }
    }

    private Video(Parcel in) {
        this.size = in.readLong();
        this.url = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.createdTime = in.readString();
        this.duration = in.readLong();
        this.title = in.readString();
        this.thumbnail = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.playTimes = in.readInt();
        this.mimeType = in.readString();
        this.scenicId = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.size);
        dest.writeString(this.url);
        dest.writeParcelable(this.location, flags);
        dest.writeString(this.createdTime);
        dest.writeLong(this.duration);
        dest.writeString(this.title);
        dest.writeString(this.thumbnail);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.playTimes);
        dest.writeString(this.mimeType);
        dest.writeInt(this.scenicId);
    }
}
