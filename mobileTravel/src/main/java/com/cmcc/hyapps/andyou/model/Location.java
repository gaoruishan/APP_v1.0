
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.location.core.GeoPoint;
import com.amap.api.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location implements Parcelable {
    @SerializedName("longitude")
    public double longitude;

    @SerializedName("latitude")
    public double latitude;

    // TODO: temp solution, should not use transient here
    @Expose(serialize = false)
    public transient String city;
    @Expose(serialize = false)
    public transient String city_en;
    @Expose(serialize = false)
    public transient float direction;
    @Expose(serialize = false)
    public transient float radius;
    @Expose(serialize = false)
    public transient float accuracy;
    @Expose(serialize = false)
    public transient float speed;
    @Expose(serialize = false)
    public transient float bearing;

    public Location(double latitude, double longitude) {
        super();
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isValid() {
        return longitude != 0 && latitude != 0;
    }

    public GeoPoint toGeoPoint() {
        return new GeoPoint((int) (latitude * 3.6E6),
                (int) (longitude * 3.6E6));
    }

    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }

    public static Location fromLatLng(LatLng latLng) {
        return new Location(latLng.latitude, latLng.longitude);
    }

    @Override
    public String toString() {
        return "Location [longitude=" + longitude + ", latitude=" + latitude + ", city=" + city+ ", city_en=" + city_en
                + ", direction=" + direction + ", radius=" + radius + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeFloat(this.direction);
        dest.writeFloat(this.radius);
        dest.writeFloat(accuracy);
        dest.writeString(this.city);
        dest.writeString(this.city_en);
    }

    private Location(Parcel in) {
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.direction = in.readFloat();
        this.radius = in.readFloat();
        this.accuracy = in.readFloat();
        this.city = in.readString();
        this.city_en = in.readString();
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
