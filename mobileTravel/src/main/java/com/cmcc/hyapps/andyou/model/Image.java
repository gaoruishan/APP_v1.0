
package com.cmcc.hyapps.andyou.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author kuloud
 */
public class Image implements Parcelable {
    public String imageId;
    public Uri thumbnailPath;
    public Uri imagePath;
    public Bitmap bitmap;
    public boolean isSelected = false;

    public String infoTime;
    public String infoLocation;

    public Image() {
    }

    public Image(Parcel in) {
        imageId = in.readString();
        thumbnailPath = in.readParcelable(Uri.class.getClassLoader());
        imagePath = in.readParcelable(Uri.class.getClassLoader());
        if (in.readInt() != 0) {
            bitmap = Bitmap.CREATOR.createFromParcel(in);
        } else {
            bitmap = null;
        }
        infoTime = in.readString();
        infoLocation = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR =
            new Parcelable.Creator<Image>() {
                @Override
                public Image createFromParcel(Parcel in) {
                    return new Image(in);
                }

                @Override
                public Image[] newArray(int size) {
                    return new Image[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageId);
        dest.writeParcelable(thumbnailPath, 0);
        dest.writeParcelable(imagePath, 0);
        if (bitmap != null) {
            dest.writeInt(1);
            bitmap.writeToParcel(dest, 0);
        } else {
            dest.writeInt(0);
        }
        dest.writeString(infoTime);
        dest.writeString(infoLocation);
    }

}
