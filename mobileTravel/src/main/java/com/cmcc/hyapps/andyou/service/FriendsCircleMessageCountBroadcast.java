package com.cmcc.hyapps.andyou.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by bingbing on 2015/11/12.
 */
public class FriendsCircleMessageCountBroadcast extends BroadcastReceiver {

    private OnFriendsCircleMessageCountListener mMessageCountListener;

    public void setMessageCountListener(OnFriendsCircleMessageCountListener messageCountListener) {
        mMessageCountListener = messageCountListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && mMessageCountListener != null) {
            mMessageCountListener.onFriendsCircleMessageCount(intent);
        }
    }

    public interface OnFriendsCircleMessageCountListener {
        void onFriendsCircleMessageCount(Intent intent);
    }
}
