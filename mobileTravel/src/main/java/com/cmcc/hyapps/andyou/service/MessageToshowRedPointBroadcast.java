package com.cmcc.hyapps.andyou.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cmcc.hyapps.andyou.activity.IndexActivity;

/**
 * 当有聊天消息和评论回复时候显示红点（不包括朋友圈中的回复）
 */
public class MessageToshowRedPointBroadcast extends BroadcastReceiver {
    private OnShowRedPointListener mOnShowRedPointListener;
    private OnCancleLoginListener mOnCancleLoginListener;
    private static final String TO_SHOW_RED_POINT = "show_red_point";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction()) && intent.getAction().equals(TO_SHOW_RED_POINT) && mOnShowRedPointListener != null) {

            if (intent.getBooleanExtra("isshow", false)) {
                mOnShowRedPointListener.toshow();
            } else {
                mOnShowRedPointListener.tohide();
            }
        }
        if (intent != null && !TextUtils.isEmpty(intent.getAction()) && intent.getAction().equals(IndexActivity.CANCLE_LOGIN_BROADCASE) && mOnCancleLoginListener != null) {
            mOnCancleLoginListener.cancleLogin();
        }
    }

    public void setOnShowRedPointListener(OnShowRedPointListener onShowRedPointListener) {
        mOnShowRedPointListener = onShowRedPointListener;
    }

    public void setOnCancleLoginListener(OnCancleLoginListener onCancleLoginListener) {
        mOnCancleLoginListener = onCancleLoginListener;
    }

    public interface OnShowRedPointListener {
        void toshow();

        void tohide();
    }

    public interface OnCancleLoginListener {
        void cancleLogin();
    }
}
