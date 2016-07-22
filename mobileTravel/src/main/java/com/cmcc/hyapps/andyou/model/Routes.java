
package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Routes {
    @SerializedName("routes")
    public ArrayList<ScenicRoute> routes;

    public static class ScenicRoute {
        @SerializedName("route")
        public ArrayList<RouteSpot> route = new ArrayList<RouteSpot>();

        @SerializedName("recommended")
        public boolean recommended;

        @SerializedName("estimate_time")
        public int estimateTime;

        @SerializedName("distance")
        public int distance;

        @SerializedName("intro")
        public String intro = "";

        @Override
        public String toString() {
            return "ScenicRoute [route=" + route + ", recommended=" + recommended + ", intro="
                    + intro + "]";
        }
    }

    @Override
    public String toString() {
        return "Routes [routes=" + routes + "]";
    }

}
