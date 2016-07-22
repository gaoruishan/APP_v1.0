
package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.UIStartUpHelper;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHToken;
import com.cmcc.hyapps.andyou.model.QHTokenId;
import com.cmcc.hyapps.andyou.model.Splash;
import com.cmcc.hyapps.andyou.service.LocationService;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.message.IMConst;

/**
 * Splash page.
 *
 * @author kuloud
 */
public class SplashActivity extends BaseActivity {
    /**
     * Duration for show Splash
     */
    private final long DURATION = 3000;

    private final int CODE_SHOW_INTRO = 1;

    private final String KEY_SPLASHS = "key_splashs";
    private boolean isOk = false;
    private int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresh_splash);

//        setupBackground();

        // Start reqeust current location
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);

        autheToken();

        UIStartUpHelper.executeOnSplashScreen();
        if (AppUtils.firstLaunch(activity)) {
            startActivityForResult(new Intent(activity, IntroActivity.class), CODE_SHOW_INTRO);
        } else {
            delayedInto();
        }
    }

    private void autheToken() {
        QHToken qhToken = AppUtils.getQHToken(this);
        String jsonBody = new Gson().toJson(qhToken);
        String url = ServerAPI.QHToken.buildAuthToken();
        RequestManager.getInstance().sendGsonRequest(Request.Method.POST, url,
                jsonBody,
                QHToken.class, new Response.Listener<QHToken>() {
                    @Override
                    public void onResponse(QHToken response) {
                        userId = response.userId;
                        String password = response.password;
                        if (password == null) {
                            QHToken token = AppUtils.getQHToken(SplashActivity.this);
                            userId = token.getUserId();
                            password = token.getPassword();
                        }
                        Log.e("mUserName===" + response.getUserId() + "mPassWord===" + response.getPassword() + "==" + response.getToken());
                        //登陆小溪
                        if (userId != 0 && password != null) {
                            IMApp.getInstance().doLogin(userId + "", password);
                        }

                        //什么都不做
                        Log.e("response", response.toString());

//                        AppUtils.clearQHToken(SplashActivity.this);
//                        AppUtils.saveQHToken(SplashActivity.this, new Gson().toJson(response));
                        //获取friend token 并保存
                        getFriendToken();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //把当前的token和用户信息删掉
                        Log.e("law", "error=" + error);
                        AppUtils.clearQHToken(SplashActivity.this);
                        AppUtils.clearQHUser(SplashActivity.this);

                        //需登陆
                        isOk = true;
                    }
                }, requestTag);
    }

    /**
     * 获取圈子验证后 加载默认
     */
    private void getFriendToken() {

        RequestManager.getInstance().sendGsonRequestAESforGET(ServerAPI.getFriendToken.buildAuthToken(), QHTokenId.class,
                new Response.Listener<QHTokenId>() {

                    @Override
                    public void onResponse(QHTokenId user) {
//                        Toast.makeText(SplashActivity.this,user.toString(),Toast.LENGTH_LONG).show();
//                        Log.e("==QHTokenId, User: " + user.jsessionid);
                        //保存验证
                        AppUtils.saveFriendToken(SplashActivity.this, new Gson().toJson(user), user.jsessionid);
                        AppUtils.saveDynamicKey(user.getDynamicKey());
                        sendBroadcast(new Intent(IMConst.ACTION_MYATTENTION));
                        sendBroadcast(new Intent(IMConst.ACTION_BLACKLIST));
                        isOk = true;
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "", ServerAPI.AESE_KEY);
    }

    private void setupBackground() {
        // load local splash
        ArrayList<Splash> splashs = loadSplashs();
        if (splashs != null) {
            List<Splash> refreshData = (List<Splash>) splashs.clone();
            long now = System.currentTimeMillis();
            for (Splash splash : splashs) {
                long endTime = TimeUtils.parseTimeToMills(splash.endTime);
                // Remove outdated data
                if (endTime < now) {
                    if (refreshData != null && refreshData.contains(splash)) {
                        FileUtils.cleanCacheBitmap(activity, splash.endTime);
                        refreshData.remove(splash);
                    }
                    continue;
                }
                // Load hit data
                long startTime = TimeUtils.parseTimeToMills(splash.startTime);
                if (startTime < now) {
                    ImageView bg = (ImageView) findViewById(R.id.root);
                    String url = FileUtils.getCachePath(activity, splash.endTime);
                    Bitmap bm = FileUtils.getLocalBitmap(url);
                    if (bm != null) {
                        bg.setImageBitmap(bm);
                    }
                    break;
                }
            }
            if (!refreshData.isEmpty()) {
                PreferencesUtils.putString(activity, KEY_SPLASHS, new Gson().toJson(refreshData));
            }
        }

        RequestManager.getInstance().sendGsonRequest(ServerAPI.Splash.buildUrl("beijing"),
                Splash.class,
                new Response.Listener<Splash>() {
                    @Override
                    public void onResponse(Splash splash) {
                        Log.d("onResponse, Splash=%s", splash);
                        onSplashLoaded(splash);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse");
                    }
                }, "splash");
    }

    private ArrayList<Splash> loadSplashs() {
        // read local
        String splashsJson = PreferencesUtils.getString(activity, KEY_SPLASHS);
        if (!TextUtils.isEmpty(splashsJson)) {
            Type type = new TypeToken<ArrayList<Splash>>() {
            }.getType();
            ArrayList<Splash> splashs = null;
            try {
                splashs = new Gson().fromJson(splashsJson, type);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            return splashs;
        }
        return null;
    }

    protected void onSplashLoaded(final Splash splash) {
        ArrayList<Splash> tempSplashs = loadSplashs();
        if (tempSplashs == null) {
            tempSplashs = new ArrayList<Splash>();
        }
        // Get new data, update local records.
        if (tempSplashs != null && !tempSplashs.contains(splash)) {
            final ArrayList<Splash> splashs = tempSplashs;
            splashs.add(splash);
            int width = ScreenUtils.getScreenWidth(getBaseContext());
            int height = ScreenUtils.getScreenHeight(getBaseContext());
            RequestManager.getInstance().requestImage(splash.imageUrl, new Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    FileUtils.cacheBitmap(getBaseContext(), bitmap, splash.endTime);
                    PreferencesUtils.putString(activity, KEY_SPLASHS, new Gson().toJson(splashs));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(error, "onErrorResponse");
                }
            }, width, height, "splash");
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (CODE_SHOW_INTRO == arg0) {
            AppUtils.setFirstLaunch(activity);
            delayedInto();
        }
    }

    private void delayedInto() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, IndexActivity.class);
                startActivity(intent);
                finish();
            }
        }, DURATION);
    }

    @Override
    protected void onDestroy() {
        if (!isOk) {//后台请求friend token
            sendBroadcast(new Intent(IMConst.ACTION_FRIEND_TOKEN));
        }else {
            //默认加载--我关注的人和群组
            if (userId != 0) {
                SplashActivity.this.sendBroadcast(new Intent(IMConst.ACTION_MYATTENTION));
            }
        }
        super.onDestroy();
    }
}
