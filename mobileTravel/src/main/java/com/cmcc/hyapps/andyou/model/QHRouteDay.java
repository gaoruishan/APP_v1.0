package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Edward on 2015/6/9.
 */
public class QHRouteDay implements Parcelable {
    public int id;
    public int route;
    public QHRouteScenic scenic;
    public int day;

    public int itemType = 0;//0 item,1 header ,2 date
    public String date  ="";//日期
    public int  index = -1;//第几个item

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.route);
        dest.writeParcelable(this.scenic,flags);
        dest.writeInt(this.day);
    }
    public QHRouteDay(){}

    public QHRouteDay(Parcel in) {
        this.id = in.readInt();
        this.route = in.readInt();
        this.scenic = in.readParcelable(QHRouteScenic.class.getClassLoader());
        this.day = in.readInt();
    }

    public static final Parcelable.Creator<QHRouteDay> CREATOR = new Parcelable.Creator<QHRouteDay>() {
        public QHRouteDay createFromParcel(Parcel source) {
            return new QHRouteDay(source);
        }

        public QHRouteDay[] newArray(int size) {
            return new QHRouteDay[size];
        }
    };
}
