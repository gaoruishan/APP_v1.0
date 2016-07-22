/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author kuloud
 */
public class Splash implements Cloneable {
    @SerializedName("start_time")
    public String startTime;

    @SerializedName("image_url")
    public String imageUrl;

    @SerializedName("end_time")
    public String endTime;
}
