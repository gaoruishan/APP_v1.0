/**
 *
 */

package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author kuloud
 */
public class Audio {
    // url is the identification of an audio
    @SerializedName("url")
    public String url;

    @SerializedName("title")
    public String title;

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("text_content")
    public String content;

    @SerializedName("duration")
    public long duration;
}
