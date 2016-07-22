
package com.cmcc.hyapps.andyou.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.util.CommonUtils;

import java.util.ArrayList;
import java.util.Map;

public class ScenicDetails extends Scenic implements IOfflinePackage {

    @SerializedName("intro_text")
    public String introText;

    @SerializedName("intro_title")
    public String introTitle;

    @SerializedName("cover_image")
    public String coverImage;

    @SerializedName("info")
    public ScenicInfo info;

    @SerializedName("intro_audio")
    public ArrayList<ScenicAudio> audioIntroSections = new ArrayList<ScenicAudio>();

    @SerializedName("service")
    public ServiceInfo serviceInfo;

    @SerializedName("favorite")
    public boolean isFavorite;

    @SerializedName("city")
    public String city;

    @SerializedName("city_zh")
    public String cityZh;

    @SerializedName("offline_package")
    public OfflinePackage offlinePackage;

    public Weather weather;

    public NearbyPeopleCount nearbyPeopleCount;

    public boolean isOfflinePackage;

    public boolean hasAudioResources() {
        if (audioIntroSections == null || audioIntroSections.isEmpty()) {
            return false;
        }

        boolean hasAudio = false;
        for (ScenicAudio scenicAudio : audioIntroSections) {
            if (scenicAudio.audio == null) {
                continue;
            }

            for (AudioIntro audioIntro : scenicAudio.audio) {
                if (audioIntro != null && CommonUtils.isValidUrl(audioIntro.url)) {
                    hasAudio = true;
                }
            }
        }

        return hasAudio;
    }

    public ScenicAudio findScenicAudio(int id) {
        if (audioIntroSections != null) {
            for (ScenicAudio scenicAudio : audioIntroSections) {
                if (scenicAudio.spotId == id) {
                    return scenicAudio;
                }
            }
        }
        return null;
    }

    public static class OfflinePackage implements Parcelable {
        @SerializedName("url")
        public String url;
        @SerializedName("size")
        public long size;
        @SerializedName("md5")
        public String md5;

        @Override
        public int describeContents() {
            return 0;
        }

        public boolean isValid() {
            return CommonUtils.isValidUrl(url);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(url);
            dest.writeLong(size);
            dest.writeString(md5);
        }

        public OfflinePackage(Parcel in) {
            url = in.readString();
            size = in.readLong();
            md5 = in.readString();
        }

        public OfflinePackage() {
        }

        public static final Parcelable.Creator<OfflinePackage> CREATOR =
                new Parcelable.Creator<OfflinePackage>() {
                    @Override
                    public OfflinePackage createFromParcel(Parcel in) {
                        return new OfflinePackage(in);
                    }

                    @Override
                    public OfflinePackage[] newArray(int size) {
                        return new OfflinePackage[size];
                    }
                };

        @Override
        public String toString() {
            return "OfflinePackage [url=" + url + ", size=" + size + ", md5=" + md5 + "]";
        }
    }

    public static class ScenicInfo implements Parcelable {
        @SerializedName("address")
        public String address;

        @SerializedName("ticket_price")
        public String ticketPrice;

        @SerializedName("ticket_url")
        public String ticketUrl;

        @SerializedName("open_hours")
        public String openHours;

        @SerializedName("level")
        public String level;

        @SerializedName("misc")
        public Map<String, String> miscData;

        public String getDisplayString(Context context) {
            StringBuilder builder = new StringBuilder();
            if (!TextUtils.isEmpty(address)) {
                builder.append(context.getString(R.string.address, address));
            }
            if (!TextUtils.isEmpty(openHours)) {
                builder.append("\n");
                builder.append(context.getString(R.string.open_hours, openHours));
            }
            if (!TextUtils.isEmpty(ticketPrice)) {
                builder.append("\n");
                builder.append(context.getString(R.string.ticket_price, String.valueOf(ticketPrice)));
            }
            if (!TextUtils.isEmpty(level)) {
                builder.append("\n");
                builder.append(context.getString(R.string.scenic_level, level));
            }

            if (miscData != null) {
                for (Map.Entry<String, String> entry : miscData.entrySet()) {
                    builder.append("\n");
                    builder.append(context.getString(R.string.scenic_info_any, entry.getKey(),
                            entry.getValue()));
                }
            }
            return builder.toString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(address);
            dest.writeString(ticketPrice);
            dest.writeString(ticketUrl);
            dest.writeString(openHours);
            dest.writeString(level);
        }

        public ScenicInfo(Parcel in) {
            address = in.readString();
            ticketPrice = in.readString();
            ticketUrl = in.readString();
            openHours = in.readString();
            level = in.readString();
        }

        public static final Parcelable.Creator<ScenicInfo> CREATOR =
                new Parcelable.Creator<ScenicInfo>() {
                    @Override
                    public ScenicInfo createFromParcel(Parcel in) {
                        return new ScenicInfo(in);
                    }

                    @Override
                    public ScenicInfo[] newArray(int size) {
                        return new ScenicInfo[size];
                    }
                };

        @Override
        public String toString() {
            return "ScenicInfo [address=" + address + ", ticketPrice=" + ticketPrice
                    + ", ticketUrl=" + ticketUrl + ", openHours=" + openHours + ", level=" + level
                    + "]";
        }
    }

    public static class ServiceInfo implements Parcelable {
        @SerializedName("complaint_num")
        public String complaintNumber;

        @SerializedName("help_num")
        public String helpNumber;

        @SerializedName("transport")
        public ArrayList<Transport> tranportInfo;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(complaintNumber);
            dest.writeString(helpNumber);
            dest.writeTypedList(tranportInfo);
        }

        public ServiceInfo(Parcel in) {
            complaintNumber = in.readString();
            helpNumber = in.readString();
            tranportInfo = new ArrayList<Transport>();
            in.readTypedList(tranportInfo, Transport.CREATOR);
        }

        public static final Parcelable.Creator<ServiceInfo> CREATOR =
                new Parcelable.Creator<ServiceInfo>() {
                    @Override
                    public ServiceInfo createFromParcel(Parcel in) {
                        return new ServiceInfo(in);
                    }

                    @Override
                    public ServiceInfo[] newArray(int size) {
                        return new ServiceInfo[size];
                    }
                };

        @Override
        public String toString() {
            return "ServiceInfo [complaintNumber=" + complaintNumber + ", helpNumber=" + helpNumber
                    + ", tranportInfo=" + tranportInfo + "]";
        }

    }

    public static class Transport implements Parcelable {
        @SerializedName("title")
        public String title;

        @SerializedName("desc")
        public String desc;

        @Override
        public String toString() {
            return "Transport [title=" + title + ", desc=" + desc + "]";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(desc);
        }

        public Transport(Parcel in) {
            title = in.readString();
            desc = in.readString();
        }

        public static final Parcelable.Creator<Transport> CREATOR =
                new Parcelable.Creator<Transport>() {
                    @Override
                    public Transport createFromParcel(Parcel in) {
                        return new Transport(in);
                    }

                    @Override
                    public Transport[] newArray(int size) {
                        return new Transport[size];
                    }
                };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(introText);
        dest.writeString(introTitle);
        dest.writeString(coverImage);
        dest.writeParcelable(info, flags);
        dest.writeTypedList(audioIntroSections);
        dest.writeParcelable(serviceInfo, flags);
        if (isFavorite) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        dest.writeString(city);
        dest.writeString(cityZh);
        dest.writeParcelable(offlinePackage, flags);
    }

    public ScenicDetails(Parcel in) {
        super(in);
        introText = in.readString();
        introTitle = in.readString();
        coverImage = in.readString();
        info = in.readParcelable(ScenicInfo.class.getClassLoader());
        audioIntroSections = new ArrayList<ScenicAudio>();
        in.readTypedList(audioIntroSections, ScenicAudio.CREATOR);
        serviceInfo = in.readParcelable(ServiceInfo.class.getClassLoader());
        if (in.readInt() == 1) {
            isFavorite = true;
        } else {
            isFavorite = false;
        }

        city = in.readString();
        cityZh = in.readString();
        offlinePackage = in.readParcelable(OfflinePackage.class.getClassLoader());
    }

    public ScenicDetails() {
    }

    public static final Parcelable.Creator<ScenicDetails> CREATOR =
            new Parcelable.Creator<ScenicDetails>() {
                @Override
                public ScenicDetails createFromParcel(Parcel in) {
                    return new ScenicDetails(in);
                }

                @Override
                public ScenicDetails[] newArray(int size) {
                    return new ScenicDetails[size];
                }
            };

    @Override
    public ArrayList<ScenicAudio> audioIntro() {
        return audioIntroSections;
    }

    @Override
    public String coverImage() {
        return coverImage;
    }

    @Override
    public void setOfflinePathRoot(String pathRoot) {
        isOfflinePackage = true;
        coverImage = pathRoot + coverImage;

        if (audioIntroSections != null) {
            for (ScenicAudio scenicAudio : audioIntroSections) {
                if (scenicAudio != null && scenicAudio.audio != null) {
                    scenicAudio.image = pathRoot + scenicAudio.image;
                    for (AudioIntro audio : scenicAudio.audio) {
                        audio.imageUrl = pathRoot + audio.imageUrl;
                        audio.url = pathRoot + audio.url;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ScenicDetails [introTitle=" + introTitle
                + ", coverImage=" + coverImage + ", info=" + info + ", audioIntroSections="
                + audioIntroSections + ", serviceInfo=" + serviceInfo + ", isFavorite="
                + isFavorite + ", city=" + city + ", offlinePackage=" + offlinePackage
                + ", weather=" + weather + "]";
    }

    @Override
    public String getOfflineFileName() {
        return Uri.parse(ServerAPI.ScenicDetails.URL).getLastPathSegment();
    }

}
