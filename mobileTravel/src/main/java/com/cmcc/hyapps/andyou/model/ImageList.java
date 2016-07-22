
package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageList {
    @SerializedName("images")
    public List<CompoundImage> images;

    @SerializedName("title")
    public String title;

    @SerializedName("scenic_id")
    public int scenicId;
}
