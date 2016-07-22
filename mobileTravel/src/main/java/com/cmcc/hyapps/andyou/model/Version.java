
package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

public class Version {
    @SerializedName("url")
    public String url;

    @SerializedName("message")
    public String message;

    @SerializedName("latest")
    public String latest;

    @SerializedName("force_udpate")
    public boolean forceUpdate;

    @Override
    public String toString() {
        return "Version [url=" + url + ", message=" + message + ", latest=" + latest
                + ", forceUpdate=" + forceUpdate + "]";
    }

}
