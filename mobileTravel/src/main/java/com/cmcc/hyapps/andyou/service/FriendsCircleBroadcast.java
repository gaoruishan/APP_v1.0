package com.cmcc.hyapps.andyou.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.activity.LoginActivity;
import com.cmcc.hyapps.andyou.fragment.FreshMeFragment;
import com.cmcc.hyapps.andyou.util.AppUtils;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.message.DialogFactory;
import test.grs.com.ims.message.IMConst;

/**
 * Created by bingbing on 2015/11/12.
 */
public class FriendsCircleBroadcast extends BroadcastReceiver {

    private OnFriendsCircleBroadcastListener mCircleBroadcastListener;

    public void setCircleBroadcastListener(OnFriendsCircleBroadcastListener circleBroadcastListener) {
        mCircleBroadcastListener = circleBroadcastListener;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent != null) {
            Log.e("开始回调","onReceive");
            mCircleBroadcastListener.onFriendsCircleBroadcast(intent);
        }
        String action = intent.getAction();
        if (IMConst.ACTION_RGISTER.equals(action)) {
            View.OnClickListener oKBtnEvent = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //注销
                    IMApp.getInstance().doRegister();
                    AppUtils.clearQHUser(context);
                    AppUtils.clearQHToken(context);
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            };
            DialogFactory.getConfirmDialog(context, "账号登录冲突", "您的账号已在其他终端登录，请重新登陆？", " 取消", "确定", null, oKBtnEvent).show();
        }
    }

    public interface OnFriendsCircleBroadcastListener {
        void onFriendsCircleBroadcast(Intent intent);
    }
}
