package com.cmcc.hyapps.andyou.service;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.activity.IndexActivity;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.fragment.FriendsCircleFragment;
import com.cmcc.hyapps.andyou.model.IsHasMessage;
import com.cmcc.hyapps.andyou.model.QHMessageCount;
import com.cmcc.hyapps.andyou.util.AppUtils;

/**
 * 判断是否有新消息或者新动态service
 * Created by bingbing on 2015/11/11.
 */
public class FriendsIsHasMessageServices extends IntentService {

    private boolean isHas = false;
    private Intent isShowRedIntent;
    private Intent messageCountIntent;

    public FriendsIsHasMessageServices() {
        super("FriendsIsHasMessageServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        isHasTrendsOrMessage();
        getTrendsMessageCount();
    }

    /**
     * 判断是否有新的动态或动态消息，来显示圈子的红点
     *
     * @return
     */
    private void isHasTrendsOrMessage() {
        String url = ServerAPI.ADDRESS + "friends/circle/hasNews.do";
        RequestManager.getInstance().sendGsonRequestAESforGET(url, IsHasMessage.class, new Response.Listener<IsHasMessage>() {
            @Override
            public void onResponse(IsHasMessage response) {
                isHas = response.isHad() ? true : false;
                if (isShowRedIntent == null) {
                    isShowRedIntent = new Intent(IndexActivity.FRIENDS_CRICLE_BROADCAST);
                }
                isShowRedIntent.putExtra("isHas", isHas);
                sendBroadcast(isShowRedIntent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isShowRedIntent == null) {
                    isShowRedIntent = new Intent(IndexActivity.FRIENDS_CRICLE_BROADCAST);
                }
                isShowRedIntent.putExtra("isHas", false);
                sendBroadcast(isShowRedIntent);
            }
        }, "isHasMessage", AppUtils.dynamicKey);

    }

    /**
     * 获取消息数量
     */
    private void getTrendsMessageCount() {
        String url = ServerAPI.ADDRESS + "friends/getNewMessageNum.do";
        RequestManager.getInstance().sendGsonRequestAESforGET(url, QHMessageCount.class, new Response.Listener<QHMessageCount>() {
            @Override
            public void onResponse(QHMessageCount response) {
                int count = response.getNewMessageNum();
                if (messageCountIntent == null) {
                    messageCountIntent = new Intent(FriendsCircleFragment.FRIENDS_CIRCLE_MESSAGECOUNT_BROADCAST);
                }
                messageCountIntent.putExtra("messageCount", count);
                sendBroadcast(messageCountIntent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (messageCountIntent == null) {
                    messageCountIntent = new Intent(FriendsCircleFragment.FRIENDS_CIRCLE_MESSAGECOUNT_BROADCAST);
                }
                messageCountIntent.putExtra("messageCount", 0);
                sendBroadcast(messageCountIntent);
            }
        }, "getTrendsMessageCount", AppUtils.dynamicKey);
    }

}
