/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author kuloud
 */
public class TripDetail extends Trip {
    @SerializedName("is_voted")
    public boolean isVoted;

    @SerializedName("days")
    public List<TripDay> days;

    public TripDetail(Parcel in) {
        super(in);
        boolean[] temp = new boolean[1];
        in.readBooleanArray(temp);
        isVoted = temp[0];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeBooleanArray(new boolean[] {
            isVoted
        });
    }

    @Override
    public String toString() {
        return "TripDetail [id=" + id + ", createTime=" + createTime + ", coverImage=" + coverImage
                + ", title=" + title + ", viewsCount=" + viewsCount + ", commentCount="
                + commentCount + ", voteCount=" + voteCount + ", isVoted=" + isVoted + "]";
    }
}
