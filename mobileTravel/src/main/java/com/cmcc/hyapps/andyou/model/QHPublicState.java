package com.cmcc.hyapps.andyou.model;

/**
 * Created by gaoruishan on 15/11/5.
 */
public class QHPublicState {

    /**
     * successful : true
     * infoId : 22121
     * info : xxxxxxxxxxxxx
     */

    private String successful;
    private int infoId;
    private String info;

    public void setSuccessful(String successful) {
        this.successful = successful;
    }

    public void setInfoId(int infoId) {
        this.infoId = infoId;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSuccessful() {
        return successful;
    }

    public int getInfoId() {
        return infoId;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "QHPublicState{" +
                "successful='" + successful + '\'' +
                ", infoId=" + infoId +
                ", info='" + info + '\'' +
                '}';
    }
}
