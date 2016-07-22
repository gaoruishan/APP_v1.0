package com.cmcc.hyapps.andyou.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/19.
 */
public class QHResultList<T> {
    public int count;
    public String next;
    public String previous;
    public List<T> results = new ArrayList<T>();
}
