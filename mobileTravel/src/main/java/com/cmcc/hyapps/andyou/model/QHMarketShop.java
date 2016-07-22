package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by niuzhiguo on 2015/5/21.
 */
public class QHMarketShop implements Parcelable {
    public String type;

    public int id;
    public int stype;
    public String name;
    public String image_url;
    public float longitude;
    public float latitude;
    public String introduction;
    public String telephone;
    public int comment_count;
    public String address;
    public String promotion;
    public int average;
    public int recommend;

    @Override
    public int describeContents() {
        return 0;
    }

    public QHMarketShop(){

     }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.stype);
        dest.writeString(this.name);
        dest.writeString(this.image_url);
        dest.writeFloat(this.longitude);
        dest.writeFloat(this.latitude);
        dest.writeString(this.introduction);
        dest.writeString(this.telephone);
        dest.writeInt(this.comment_count);
        dest.writeString(this.address);
        dest.writeString(this.promotion);
        dest.writeInt(this.average);
        dest.writeInt(this.recommend);
    }
    public QHMarketShop(Parcel in) {
        this.id = in.readInt();
        this.stype = in.readInt();
        this.name = in.readString();
        this.image_url = in.readString();
        this.longitude = in.readFloat();
        this.latitude = in.readFloat();
        this.introduction = in.readString();
        this.telephone = in.readString();
        this.comment_count = in.readInt();
        this.address = in.readString();
        this.promotion = in.readString();
        this.average = in.readInt();
        this.recommend = in.readInt();
    }


    public class QHMarketShopList extends ResultList<QHMarketShop> {

    }

    @Override
    public String toString() {
        return "Trip [id=" + id + "stype="+stype+", name=" + name + ", image_url=" + image_url
                + ", longitude=" + longitude + ", latitude=" + latitude + ", introduction="
                + introduction + ", telephone=" + telephone + ", address=" + address + ", promotion=" + promotion+ ", average=" + average+",recommend="+recommend+"]";
    }

    public static final Creator<QHMarketShop> CREATOR = new Creator<QHMarketShop>() {
        public QHMarketShop createFromParcel(Parcel source) {
            return new QHMarketShop(source);
        }

        public QHMarketShop[] newArray(int size) {
            return new QHMarketShop[size];
        }
    };

}
