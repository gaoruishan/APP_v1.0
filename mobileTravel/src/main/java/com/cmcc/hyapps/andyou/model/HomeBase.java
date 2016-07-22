/**
 * 
 */

package com.cmcc.hyapps.andyou.model;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author kuloud
 */
public abstract class HomeBase implements Parcelable {

    @SerializedName("name")
    public String name;

    @SerializedName("image")
    public String imageUrl;


}
