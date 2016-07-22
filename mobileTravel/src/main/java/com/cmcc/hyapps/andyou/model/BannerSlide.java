/**
 *
 */

package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * @author kuloud
 */
public class BannerSlide {
    @SerializedName("type")
    public int type;

    @SerializedName("title")
    public String title;

    @SerializedName("meta_data")
    public Map<String, String> metaData;

    @SerializedName("image")
    public String image;

    @Override
    public String toString() {
        return "BannerSlide [type=" + type + ", title=" + title + ", metaData=" + metaData
                + ", image=" + image + "]";
    }

    public static class BannerSlideList {

        @SerializedName("list")
        public List<BannerSlide> bannerSlideList;

        @SerializedName("pagination")
        public Pagination pagination;

        @Override
        public String toString() {
            return "BannerSlideList [bannerSlideList=" + bannerSlideList + ", pagination="
                    + pagination + "]";
        }

    }
}
