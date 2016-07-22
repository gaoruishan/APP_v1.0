
package com.cmcc.hyapps.andyou.model;

import android.graphics.Bitmap;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public abstract class ScenicIntroSection implements Parcelable {

    @SerializedName("title")
    public String title;

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("text_content")
    public String content;

    @SerializedName("id")
    public int id;

    // TODO
    public String scenicName = "";
    public String scenicImage = "";

    public Bitmap imageBitmap;
}
