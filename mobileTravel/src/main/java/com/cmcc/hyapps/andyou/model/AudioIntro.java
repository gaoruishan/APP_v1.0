
package com.cmcc.hyapps.andyou.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.cmcc.hyapps.andyou.util.AppUtils;

public class AudioIntro extends ScenicIntroSection {
    public static final int TYPE_INTRO = 1;
    public static final int TYPE_ALLUSIONS = 2;

    // TODO:url is the identification of an audio
    @SerializedName("url")
    public String url = "";

    @SerializedName("duration")
    public int duration;

    @SerializedName("type")
    public int type = TYPE_INTRO;

    /**
     * This audio source is preparing or is playing
     */
    public boolean highlight;

    public AudioIntro() {
    }

    // TODO: temp solution
    public int id() {
        if (id <= 0) {
            id = AppUtils.randomUUID();
        }
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AudioIntro other = (AudioIntro) obj;
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        return true;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(scenicName);
        dest.writeString(imageUrl);
        dest.writeString(content);
        dest.writeInt(duration);
        if (highlight) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        if (imageBitmap != null) {
            dest.writeInt(1);
            imageBitmap.writeToParcel(dest, 0);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(type);
        dest.writeString(scenicImage);
        dest.writeString(scenicName);
    }

    public AudioIntro(Parcel in) {
        url = in.readString();
        title = in.readString();
        scenicName = in.readString();
        imageUrl = in.readString();
        content = in.readString();
        duration = in.readInt();
        highlight = in.readInt() == 1;
        if (in.readInt() != 0) {
            imageBitmap = Bitmap.CREATOR.createFromParcel(in);
        } else {
            imageBitmap = null;
        }
        type = in.readInt();
        scenicImage = in.readString();
        scenicName = in.readString();
    }

    public static final Parcelable.Creator<AudioIntro> CREATOR =
            new Parcelable.Creator<AudioIntro>() {
                @Override
                public AudioIntro createFromParcel(Parcel in) {
                    return new AudioIntro(in);
                }

                @Override
                public AudioIntro[] newArray(int size) {
                    return new AudioIntro[size];
                }
            };

    @Override
    public String toString() {
        return "AudioIntro [url=" + url + ", duration=" + duration + ", highlight=" + highlight
                + ", title=" + title + ", imageUrl=" + imageUrl
                + ", scenicName=" + scenicName + "]";
    }

}
