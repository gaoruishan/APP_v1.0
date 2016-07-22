
package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

public class LiveServerInfo {
    @SerializedName("username")
    public String username;

    @SerializedName("token")
    public String token;

    @SerializedName("timestamp")
    public String timestamp;

    @SerializedName("server")
    public String server;

}
