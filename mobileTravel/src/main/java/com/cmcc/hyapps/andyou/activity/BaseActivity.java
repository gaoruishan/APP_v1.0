
package com.cmcc.hyapps.andyou.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


import com.android.volley.Request;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.util.AESEncrpt;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * The base activity, all activity should extends it.
 * 
 * @author kuloud
 */
public abstract class BaseActivity extends FragmentActivity {
    protected Activity activity;
    protected String requestTag;
    public final int REQUEST_CODE_LOGIN_NEW_COMMENT = 1;

    protected long startTime;
    protected long endTime;
    protected long time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进出和退出动画
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        requestTag = getClass().getName();
        activity = this;
    }


    @Override
    public void finish() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.finish();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //页面不可见时，取消所有的requestTag，RequestManager是什么呢？
        RequestManager.getInstance().cancelAll(requestTag);
    }

    protected void executeRequest(Request<?> request) {
        RequestManager.getInstance().addRequest(request, requestTag);
    }
    /**
     * app字体不随系统字体变化
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
    /**
     * 获取请求加密参数
     * @return
     */
    public String getRequestParams(Map<String,Object> maps,String url){
        String data = "";
        try {
            data = AESEncrpt.Encrypt(new Gson().toJson(maps), AppUtils.dynamicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String,String> params = new HashMap<String, String>();
        params.put("data", data);
        String body = RequestManager.getInstance().appendParameter(url,params);
        return  body;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        endTime = System.currentTimeMillis();
        time = (endTime-startTime)/1000;
    }
}
