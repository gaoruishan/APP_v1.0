
package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbyPeopleCount {
    @SerializedName("locations")
    public List<Location> locations;

    @SerializedName("nearby_counts")
    public int count;

    @Override
    public String toString() {
        return "NearbyPeopleCount [locations=" + locations + ", count=" + count + "]";
    }

}
