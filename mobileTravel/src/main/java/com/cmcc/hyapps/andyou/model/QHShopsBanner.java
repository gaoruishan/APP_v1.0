package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bingbing on 2015/10/16.
 */
public class QHShopsBanner implements Parcelable {
    private int id;
    private String image_url;
    private QHMarketShop referred_shop;
    private int referred_shop_id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.image_url);
        dest.writeParcelable(this.referred_shop, 0);
        dest.writeInt(this.referred_shop_id);
    }

    public QHShopsBanner() {
    }

    public int getReferred_shop_id() {
        return referred_shop_id;
    }

    public void setReferred_shop_id(int referred_shop_id) {
        this.referred_shop_id = referred_shop_id;
    }

    public QHMarketShop getReferred_shop() {
        return referred_shop;
    }

    public void setReferred_shop(QHMarketShop referred_shop) {
        this.referred_shop = referred_shop;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public class QHShopsBannerList extends ResultList<QHShopsBanner>{

    }
    protected QHShopsBanner(Parcel in) {
        this.id = in.readInt();
        this.image_url = in.readString();
        this.referred_shop = in.readParcelable(QHMarketShop.class.getClassLoader());
        this.referred_shop_id = in.readInt();
    }

    public static final Parcelable.Creator<QHShopsBanner> CREATOR = new Parcelable.Creator<QHShopsBanner>() {
        public QHShopsBanner createFromParcel(Parcel source) {
            return new QHShopsBanner(source);
        }

        public QHShopsBanner[] newArray(int size) {
            return new QHShopsBanner[size];
        }
    };
}
