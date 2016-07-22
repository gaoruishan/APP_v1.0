
package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RouteSpot implements Serializable {
    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("ticket_price")
    public String ticketPrice;

    @SerializedName("rating")
    public float rating;

    @SerializedName("cover_image")
    public String coverImage;

    @SerializedName("location")
    public Location location;

    @SerializedName("type")
    public int type;
}
