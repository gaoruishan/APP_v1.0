package com.cmcc.hyapps.andyou.model;

/**
 * Created by gaoruishan on 15/12/15.
 */
public class QHState {

    /**
     * successful : true
     * info : xxxxxxxxxxxxx
     */

    private boolean successful;
    private String info;

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "QHState{" +
                "successful=" + successful +
                ", info='" + info + '\'' +
                '}';
    }
}
