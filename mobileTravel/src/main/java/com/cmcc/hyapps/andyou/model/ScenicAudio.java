
package com.cmcc.hyapps.andyou.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.cmcc.hyapps.andyou.util.AppUtils;

public class ScenicAudio implements Parcelable {
    @SerializedName("name")
    public String spotName;

    @SerializedName("image")
    public String image;

    @SerializedName("audio")
    public List<AudioIntro> audio;

    @SerializedName("location")
    public Location location;

    @SerializedName("id")
    public int spotId;

    public boolean hasAudioHighlighted() {
        if (audio == null) {
            return false;
        }

        for (AudioIntro item : audio) {
            if (item != null && item.highlight) {
                return true;
            }
        }

        return false;
    }

    public boolean validate() {
        if (audio == null) {
            return false;
        }

        boolean isValid = true;
        // TODO: Hack
        if (spotId <= 0) {
            spotId = AppUtils.randomUUID();
        }
//        Iterator<AudioIntro> it = audio.iterator();
//        while (it.hasNext()) {
//            AudioIntro audio = it.next();
//            if (!CommonUtils.isValidUrl(audio.url)) {
//                it.remove();
//            } else {
//                isValid = true;
//            }
//        }

        return isValid;
    }

    public ScenicAudio() {

    }

    public static Parcelable.Creator<ScenicAudio> CREATOR = new Parcelable.Creator<ScenicAudio>() {
        @Override
        public ScenicAudio createFromParcel(Parcel source) {
            return new ScenicAudio(source);
        }

        @Override
        public ScenicAudio[] newArray(int size) {
            return new ScenicAudio[size];
        }
    };

    private ScenicAudio(Parcel in) {
        this.spotId = in.readInt();
        this.spotName = in.readString();
        this.image = in.readString();
        this.audio = new ArrayList<AudioIntro>();
        in.readTypedList(audio, AudioIntro.CREATOR);
        this.location = in.readParcelable(Location.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(spotId);
        dest.writeString(this.spotName);
        dest.writeString(this.image);
        dest.writeTypedList(audio);
        dest.writeParcelable(this.location, flags);
    }

    @Override
    public String toString() {
        return "ScenicAudio [spotName=" + spotName + ", image=" + image + ", audio=" + audio
                + ", location=" + location + ", spotId=" + spotId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((spotName == null) ? 0 : spotName.hashCode());
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
        ScenicAudio other = (ScenicAudio) obj;
        if (spotName == null) {
            if (other.spotName != null) {
                return false;
            }
        } else if (!spotName.equals(other.spotName)) {
            return false;
        }
        return true;
    }

}
