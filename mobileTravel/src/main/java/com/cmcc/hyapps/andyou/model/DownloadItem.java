/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author kuloud
 */
public class DownloadItem {

    @SerializedName("scenic_id")
    public int scenicId;

    @SerializedName("name")
    public String name;

    @SerializedName("desc")
    public String desc;

    @SerializedName("size")
    public long size;

    @SerializedName("create_time")
    public String createTime;

}
