package com.cmcc.hyapps.andyou.model;

/**
 * Created by gaoruishan on 15/11/5.
 */
public class QHResultState {

    /**
     * successful : true
     * info : xxxxxxxxxxxxx
     */

    private String successful;
    private String info;

    public void setSuccessful(String successful) {
        this.successful = successful;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSuccessful() {
        return successful;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "QHResultState{" +
                "successful='" + successful + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
