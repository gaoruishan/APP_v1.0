
package com.cmcc.hyapps.andyou.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResultList<T> {
    public int count;
    public String next;
    public String previous;
    public List<T> results = new ArrayList<T>();

    @SerializedName("pagination")
    public Pagination pagination;

    @SerializedName("list")
    public List<T> list = new ArrayList<T>();

    @Override
    public String toString() {
        return "ResultList{" +
                "count=" + count +
                ", next='" + next + '\'' +
                ", previous='" + previous + '\'' +
                ", results=" + results +
                ", pagination=" + pagination +
                ", list=" + list +
                '}';
    }
}
