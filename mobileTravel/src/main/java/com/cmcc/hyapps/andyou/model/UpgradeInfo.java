
package com.cmcc.hyapps.andyou.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author kuloud
 */
public class UpgradeInfo implements Parcelable {
    private int versionCode;
    private String versionName;
    private String apkURL;
    private String savePath;
    private String saveFileName;
    private String updateMsg;
    private String fileSize;
    private String[] updateContent;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getApkURL() {
        return apkURL;
    }

    public void setApkURL(String apkURL) {
        this.apkURL = apkURL;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public String getUpdateMsg() {
        return updateMsg;
    }

    public void setUpdateMsg(String updateMsg) {
        this.updateMsg = updateMsg;
    }

    public void setUpdateContent(String[] updateContent) {
        this.updateContent = updateContent;
    }

    public String[] getUpdateContent() {
        return updateContent;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public UpgradeInfo() {
        updateContent = new String[0];
    }

    public UpgradeInfo(Parcel in) {
        this();
        this.versionCode = in.readInt();
        this.versionName = in.readString();
        this.apkURL = in.readString();
        this.savePath = in.readString();
        this.saveFileName = in.readString();
        this.updateMsg = in.readString();
        this.fileSize = in.readString();
        in.readStringArray(this.updateContent);
    }

    public static final Parcelable.Creator<UpgradeInfo> CREATOR = new Parcelable.Creator<UpgradeInfo>() {
        public UpgradeInfo createFromParcel(Parcel in) {
            return new UpgradeInfo(in);
        }

        public UpgradeInfo[] newArray(int size) {
            return new UpgradeInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.versionCode);
        dest.writeString(this.versionName);
        dest.writeString(this.apkURL);
        dest.writeString(this.savePath);
        dest.writeString(this.saveFileName);
        dest.writeString(this.updateMsg);
        dest.writeString(this.fileSize);
        dest.writeStringArray(this.updateContent);
    }
}
