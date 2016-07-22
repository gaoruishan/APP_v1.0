
package com.cmcc.hyapps.andyou.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.cmcc.hyapps.andyou.app.ServerAPI;

import java.util.ArrayList;

public class CompoundImage implements Parcelable {
    public CompoundImage() {
    }

    public static final Parcelable.Creator<CompoundImage> CREATOR = new Parcelable.Creator<CompoundImage>() {
        public CompoundImage createFromParcel(Parcel in) {
            return new CompoundImage(in);
        }

        public CompoundImage[] newArray(int size) {
            return new CompoundImage[size];
        }
    };
    @SerializedName("large")
    public String largeImage;

    @SerializedName("small")
    public String smallImage;

    public CompoundImage(Parcel in) {
        this.largeImage = in.readString();
        this.smallImage = in.readString();
    }
    public CompoundImage(String lar,String small) {
        this.largeImage = lar;
        this.smallImage = small;
    }

    @Override
    public String toString() {
        return "CompoundImage [largeImage=" + largeImage + ", smallImage=" + smallImage + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.largeImage);
        dest.writeString(this.smallImage);
    }

    public static class TextImage implements Parcelable {
        public static final Parcelable.Creator<TextImage> CREATOR = new Parcelable.Creator<TextImage>() {
            public TextImage createFromParcel(Parcel in) {
                return new TextImage(in);
            }

            public TextImage[] newArray(int size) {
                return new TextImage[size];
            }
        };
        @SerializedName("image")
        public CompoundImage image;

        @SerializedName("create_time")
        public String createTime;

        @SerializedName("content")
        public String text;

        public TextImage(Parcel in) {
            this.image = in.readParcelable(CompoundImage.class.getClassLoader());
            this.text = in.readString();
            this.createTime = in.readString();
        }

        public TextImage(CompoundImage image, String text, String createTime) {
            super();
            this.image = image;
            this.text = text;
            this.createTime = createTime;
        }

        @Override
        public String toString() {
            return "TextImage [image=" + image + ", text=" + text + "]";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.image, flags);
            dest.writeString(this.text);
            dest.writeString(this.createTime);
        }
    }

    public static class TextImageList extends ResultList<TextImage> implements Parcelable,
            IOfflinePackage {
        public static final Creator<TextImageList> CREATOR = new Creator<TextImageList>() {
            public TextImageList createFromParcel(Parcel in) {
                return new TextImageList(in);
            }

            public TextImageList[] newArray(int size) {
                return new TextImageList[size];
            }
        };

        public TextImageList() {
            this.list = new ArrayList<CompoundImage.TextImage>();
        }

        public TextImageList(Parcel in) {
            this();
            this.pagination = in.readParcelable(Pagination.class.getClassLoader());
            in.readTypedList(this.list, TextImage.CREATOR);
        }

        @Override
        public String toString() {
            return "TextImageList [pagination=" + pagination + ", list=" + list + "]";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.pagination, flags);
            dest.writeTypedList(this.list);
        }

        @Override
        public void setOfflinePathRoot(String pathRoot) {
            if (list == null) {
                return;
            }

            for (TextImage textImage : list) {
                if (textImage.image.largeImage != null) {
                    textImage.image.largeImage = pathRoot + textImage.image.largeImage;
                }

                if (textImage.image.smallImage != null) {
                    textImage.image.smallImage = pathRoot + textImage.image.smallImage;
                }
            }

        }

        @Override
        public String getOfflineFileName() {
            return Uri.parse(ServerAPI.ScenicImages.URL).getLastPathSegment();
        }
    }
}
