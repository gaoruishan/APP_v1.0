/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author kuloud
 */
public class FavoriteItem {
    @SerializedName("id")
    public int id;

    @SerializedName("type")
    public int type;

    @SerializedName("name")
    public String name;

    @SerializedName("image")
    public String image;

    @SerializedName("content")
    public String content;

    @SerializedName("create_time")
    public String createTime;

    public static class FavoriteItemList extends ResultList<FavoriteItem> {
        @Override
        public String toString() {
            return "FavoriteItemList [list=" + list + ", pagination=" + pagination + "]";
        }

    }
}
