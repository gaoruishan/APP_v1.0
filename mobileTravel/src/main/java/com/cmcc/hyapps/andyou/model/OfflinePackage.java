/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuloud
 */
public class OfflinePackage implements Parcelable {
    @SerializedName("url")
    public String url;

    @SerializedName("size")
    public int size;

    @SerializedName("md5")
    public String md5;

    @SerializedName("scenic_id")
    public int scenicId;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeInt(this.size);
        dest.writeString(this.md5);
        dest.writeInt(this.scenicId);
    }

    public OfflinePackage() {
    }

    private OfflinePackage(Parcel in) {
        this.url = in.readString();
        this.size = in.readInt();
        this.md5 = in.readString();
        this.scenicId = in.readInt();
    }

    public static final Creator<OfflinePackage> CREATOR = new Creator<OfflinePackage>() {
        public OfflinePackage createFromParcel(Parcel source) {
            return new OfflinePackage(source);
        }

        public OfflinePackage[] newArray(int size) {
            return new OfflinePackage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public static class OfflinePackageList implements Parcelable {
        @SerializedName("list")
        public List<OfflinePackage> list;

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(list);
        }

        private OfflinePackageList(Parcel in) {
            list = new ArrayList<OfflinePackage>();
            in.readTypedList(list, OfflinePackage.CREATOR);
        }

        public static final Creator<OfflinePackageList> CREATOR = new Creator<OfflinePackageList>() {
            public OfflinePackageList createFromParcel(Parcel source) {
                return new OfflinePackageList(source);
            }

            public OfflinePackageList[] newArray(int size) {
                return new OfflinePackageList[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }
        public SparseArray<String> toSparseArray() {
            SparseArray<String> sparseArray = new SparseArray<String>();
            for (OfflinePackage item : list) {
                sparseArray.put(item.scenicId, item.url);
            }
            return sparseArray;
        }
    }
}
