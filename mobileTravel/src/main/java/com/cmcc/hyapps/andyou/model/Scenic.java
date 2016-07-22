
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Scenic implements BasicScenicData {

    @SerializedName("id")
    public int id;
    @SerializedName("have_video")
    public int have_video;

    @SerializedName("name")
    public String name;

    @SerializedName("intro")
    public String intro;

    @SerializedName("rating")
    public float rating;

    @SerializedName("location")
    public Location location;

    @SerializedName("image_url")
    public String imageUrl;

    @SerializedName("ticket_price")
    public String ticketPrice = "";

    private boolean isSelected = false;
    public void setSelect(boolean b){
        isSelected = b;
    }
    public boolean getSelect(){
        return isSelected;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(have_video);
        dest.writeString(name);
        dest.writeString(intro);
        dest.writeFloat(rating);
        dest.writeString(ticketPrice);
        dest.writeString(imageUrl);
        dest.writeParcelable(location, flags);
    }

    public Scenic() {
    }

    public Scenic(Parcel in) {
        id = in.readInt();
        have_video = in.readInt();
        name = in.readString();
        intro = in.readString();
        rating = in.readFloat();
        ticketPrice = in.readString();
        imageUrl = in.readString();
        location = (Location) in.readParcelable(Location.class.getClassLoader());
    }

    public static final Parcelable.Creator<Scenic> CREATOR =
            new Parcelable.Creator<Scenic>() {
                @Override
                public Scenic createFromParcel(Parcel in) {
                    return new Scenic(in);
                }

                @Override
                public Scenic[] newArray(int size) {
                    return new Scenic[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Scenic [id=" + id + ", have_video=" + have_video + ", name=" + name + ", location="
                + location + ", image_url=" + imageUrl + ", rating=" + rating + "]";
    }

    @Override
    public String coverImage() {
        return imageUrl;
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

    public static class ScenicList extends ResultList<Scenic> {
        @SerializedName("display_title")
        public String displayTitle;

        @Override
        public String toString() {
            return "ScenicList [list=" + list + ", pagination=" + pagination + ", displayTitle="
                    + displayTitle + "]";
        }

    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public ArrayList<ScenicAudio> audioIntro() {
        return null;
    }
}
