package com.cmcc.hyapps.andyou.model;

import com.cmcc.hyapps.andyou.adapter.row.QHRowEnjoy;

/**
 * Created by Administrator on 2015/6/16.
 */
public class QHSearch {
    public String type;

    public QHSearch() {
    }

    public QHSearch(String type, QHScenic qhScenic) {
        this.type = type;
        this.qhScenic = qhScenic;
    }

    public QHSearch(String type, QHRoute qhRoute) {
        this.type = type;
        this.qhRoute = qhRoute;
    }

    public QHSearch(String type, QHStrategy qhStrategy) {
        this.type = type;
        this.qhStrategy = qhStrategy;
    }

    public QHSearch(String type, QHEnjoy qhEnjoy) {
        this.type = type;
        this.qhEnjoy = qhEnjoy;
    }

    public QHScenic qhScenic;
    public QHRoute qhRoute;
    public QHStrategy qhStrategy;
    public QHEnjoy qhEnjoy;
}
