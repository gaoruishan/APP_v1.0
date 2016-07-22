/**
 *
 */

package com.cmcc.hyapps.andyou.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.cmcc.hyapps.andyou.app.ServerAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaBean introduce scenic spot.
 * 
 * @author kuloud
 */
public class ScenicSpot implements BasicScenicData, IOfflinePackage {
    @SerializedName("spot_id")
    public int id;

    @SerializedName("cover_image")
    public String coverImage;

    @SerializedName("intro_text")
    public String introText;

    @SerializedName("spot_name")
    public String name;

    @SerializedName("rating")
    public float rating;

    @SerializedName("ticket_price")
    public String ticketPrice;

    @SerializedName("location")
    public Location location;

    @SerializedName("intro_audio")
    public List<AudioIntro> audioIntroSections;

    @Override
    public void setOfflinePathRoot(String pathRoot) {
        coverImage = pathRoot + coverImage;

        if (audioIntroSections != null) {
            for (AudioIntro audioIntro : audioIntroSections) {
                audioIntro.imageUrl = pathRoot + audioIntro.imageUrl;
                audioIntro.url = pathRoot + audioIntro.url;
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(coverImage);
        dest.writeString(name);
        dest.writeString(introText);
        dest.writeFloat(rating);
        dest.writeString(ticketPrice);
        dest.writeParcelable(location, flags);
        if (audioIntroSections != null && !audioIntroSections.isEmpty()) {
            dest.writeInt(1);
            dest.writeTypedList(audioIntroSections);
        } else {
            dest.writeInt(0);
        }
    }

    public ScenicSpot() {
    }

    public ScenicSpot(Parcel in) {
        id = in.readInt();
        coverImage = in.readString();
        name = in.readString();
        introText = in.readString();
        rating = in.readFloat();
        ticketPrice = in.readString();
        location = (Location) in.readParcelable(Location.class.getClassLoader());
        if (in.readInt() == 1) {
            audioIntroSections = new ArrayList<AudioIntro>();
            in.readTypedList(audioIntroSections, AudioIntro.CREATOR);
        }
    }

    public static final Parcelable.Creator<ScenicSpot> CREATOR =
            new Parcelable.Creator<ScenicSpot>() {
                @Override
                public ScenicSpot createFromParcel(Parcel in) {
                    return new ScenicSpot(in);
                }

                @Override
                public ScenicSpot[] newArray(int size) {
                    return new ScenicSpot[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "ScenicSpot [id=" + id + ", coverImage=" + coverImage + ", introText=" + introText
                + ", name=" + name + ", rating=" + rating + ", ticketPrice=" + ticketPrice
                + ", location=" + location + ", audioIntroSections=" + audioIntroSections + "]";
    }

    public static class ScenicSpotList extends ResultList<ScenicSpot> implements IOfflinePackage {
        @Override
        public String toString() {
            return "ScenicSpotList [list=" + list + ", pagination=" + pagination + "]";
        }

        @Override
        public void setOfflinePathRoot(String pathRoot) {
            if (list == null) {
                return;
            }

            for (ScenicSpot video : list) {
                video.setOfflinePathRoot(pathRoot);
            }
        }

        @Override
        public String getOfflineFileName() {
            return Uri.parse(ServerAPI.ScenicSpots.URL).getLastPathSegment();
        }
    }

    @Override
    public String coverImage() {
        return coverImage;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public float rating() {
        return rating;
    }

    @Override
    public String ticketPrice() {
        return ticketPrice;
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public ArrayList<ScenicAudio> audioIntro() {
        ScenicAudio scenicAudio = new ScenicAudio();
        scenicAudio.spotId = id();
        scenicAudio.spotName = name();
        scenicAudio.image = coverImage();
        scenicAudio.audio = audioIntroSections;
        scenicAudio.location = location();
        ArrayList<ScenicAudio> list = new ArrayList<ScenicAudio>(1);
        list.add(scenicAudio);
        return list;
    }

    @Override
    public String getOfflineFileName() {
        // TODO Auto-generated method stub
        return null;
    }
}
