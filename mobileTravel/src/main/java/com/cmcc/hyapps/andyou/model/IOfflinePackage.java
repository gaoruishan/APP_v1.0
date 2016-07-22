
package com.cmcc.hyapps.andyou.model;


public interface IOfflinePackage {

    public static final String AUDIO_PATH = "audio";
    public static final String VIDEO_PATH = "video";
    public static final String IMAGE_PATH = "image";

    void setOfflinePathRoot(String pathRoot);

    String getOfflineFileName();
}
