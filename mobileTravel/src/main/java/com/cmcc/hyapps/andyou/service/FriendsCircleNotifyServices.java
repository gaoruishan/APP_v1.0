package com.cmcc.hyapps.andyou.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by bingbing on 2015/10/22.
 */
public class FriendsCircleNotifyServices extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FriendsCircleNotifyServices(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //请求朋友圈消息个数接口
    }


}
