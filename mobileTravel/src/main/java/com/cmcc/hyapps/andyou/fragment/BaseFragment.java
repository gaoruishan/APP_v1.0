/**
 *
 */

package com.cmcc.hyapps.andyou.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.activity.IndexActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.IsHasMessage;
import com.cmcc.hyapps.andyou.service.MessageToshowRedPointBroadcast;
import com.cmcc.hyapps.andyou.util.AESEncrpt;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.IMContact;
import test.grs.com.ims.message.MessageHandle;
import test.grs.com.ims.message.UNIMContact;

/**
 * Base fragment, all fragment should extends it.
 *
 * @author Kuloud
 */
public class BaseFragment extends Fragment implements MessageToshowRedPointBroadcast.OnShowRedPointListener, MessageToshowRedPointBroadcast.OnCancleLoginListener {
    protected static final boolean DEBUG = Const.DEBUG;
    protected ImageView red_point;
    protected String mRequestTag = BaseFragment.class.getName();

    private MessageToshowRedPointBroadcast mMessageToshowRedPointBroadcast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().containsKey(Const.ARGS_REQUEST_TAG)) {
            mRequestTag = getArguments().getString(Const.ARGS_REQUEST_TAG);
        }
        registerBroadcast();
        super.onCreate(savedInstanceState);
    }

    public BaseFragment() {
    }

    private static final String TO_SHOW_RED_POINT = "show_red_point";

    protected void registerBroadcast() {
        mMessageToshowRedPointBroadcast = new MessageToshowRedPointBroadcast();
        mMessageToshowRedPointBroadcast.setOnShowRedPointListener(this);
        mMessageToshowRedPointBroadcast.setOnCancleLoginListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TO_SHOW_RED_POINT);
        intentFilter.addAction(IndexActivity.CANCLE_LOGIN_BROADCASE);
        getActivity().registerReceiver(mMessageToshowRedPointBroadcast, intentFilter);
    }

    public String getFragmentName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 获取请求加密参数
     *
     * @return
     */
    public String getRequestParams(Map<String, Object> maps, String url) {
        String data = "";
        try {
            data = AESEncrpt.Encrypt(new Gson().toJson(maps), AppUtils.dynamicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("data", data);
        String body = RequestManager.getInstance().appendParameter(url, params);
        return body;
    }

    @Override
    public void toshow() {
        if (red_point != null) {
            red_point.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void tohide() {
        if (red_point != null) {
            red_point.setVisibility(View.GONE);
        }
    }

    boolean isHasMessageCenter = false;
    boolean isHasXiaoXiMessage = false;
    boolean isHasXiaoXiUnMessage = false;

    /**
     * 消息中心是否有未读消息（不包括朋友圈）
     */
    protected void isHasMessageCenter() {
        RequestManager.getInstance().sendGsonRequest(ServerAPI.BASE_URL + "getNewMessageNum/", IsHasMessage.class, new Response.Listener<IsHasMessage>() {
            @Override
            public void onResponse(IsHasMessage response) {
                isHasMessageCenter = response.isHad() ? true : false;
                sendCast(isHasMessageCenter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sendCast(false);
            }
        }, "isHasMessageCenter");
    }

    private void sendCast(boolean isshow) {
        Intent isShowRedIntent = new Intent("show_red_point");
        isShowRedIntent.putExtra("isshow", isshow);
        if (getActivity() != null)
        getActivity().sendBroadcast(isShowRedIntent);
    }

    private List<IMContact> cmContactsList ;
    private List<UNIMContact> scmContactsList ;

    /**
     * 是否有小溪消息,互相关注的消息,包括群消息
     */
    protected void isHasXiaoXiMessage() {
        cmContactsList = MessageHandle.getInstance().getDbIMContact();
        if (cmContactsList == null)
            return;
        for (IMContact item : cmContactsList) {
            if (item.getMsgNum() > 0) {
                isHasXiaoXiMessage = true;
                break;
            }
        }
        if (isHasXiaoXiMessage) {
            sendCast(isHasXiaoXiMessage);
        } else
            isHasXiaoXiUnForcusMessage();

    }

    /**
     * 是否有小溪消息,未关注的人的消息
     */
    protected void isHasXiaoXiUnForcusMessage() {
        scmContactsList = MessageHandle.getInstance().getDbUNIMContact();
        if (scmContactsList == null)
            return;
        for (UNIMContact item : scmContactsList) {
            if (item.getMsgNum() > 0) {
                isHasXiaoXiUnMessage = true;
                break;
            }
        }
        if (isHasXiaoXiUnMessage) {
            sendCast(isHasXiaoXiUnMessage);
        } else
            isHasMessageCenter();
    }

    @Override
    public void cancleLogin() {
        if (red_point != null)
            red_point.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mMessageToshowRedPointBroadcast);
    }
}
