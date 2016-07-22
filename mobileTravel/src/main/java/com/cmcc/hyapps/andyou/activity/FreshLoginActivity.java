package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.autonavi.tbt.IFrameForTBT;
import com.cmcc.hyapps.andyou.BuildConfig;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.app.TravelApp;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHToken;
import com.cmcc.hyapps.andyou.model.QHTokenId;
import com.cmcc.hyapps.andyou.model.QHUser;
import com.cmcc.hyapps.andyou.support.ExEditText;
import com.cmcc.hyapps.andyou.upyun.UpYunException;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.AESEncrpt;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.FormatUtils;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.NetUtils;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.circularprogressbar.CircularProgressBar;
import com.google.gson.Gson;
import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.StringBody;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sso.UMSsoHandler;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.message.IMConst;

/**
 * 三方登录页面
 * Created by bingbing on 2015/12/8.
 */
public class FreshLoginActivity extends BaseActivity implements View.OnClickListener {
    private ExEditText email, password, code;
    private TextView login, message, qq, wechat, sina, forget_password, register;
    private TextView mTextView;
    private ActionBar actionBar;
    private CircularProgressBar mCircularProgressBar;

    private ImageView codeImageView;
    private TextView changeTextView;
    private TextView input_pic_code;
    private View code_layout;
    private String EMAIL_LOGIN_URL = ServerAPI.ADDRESS + "api/login/emailLogin/";
    private String THIRD_LOGIN = ServerAPI.ADDRESS + "api/login/thirdPartLogin/";

    private String TAG = "FreshLoginActivity";
    private final String PASSWORD = "B0CDA560DFB07267DA65855A8D4F4BFA";

    public static final int JUMP_TO_REGISTER = 1001;
    public static final int JUMP_TO_FORGET_PASSWORD = 1002;
    public int LOGIN_REQUEST_CODE = 1111;
    private String email_auto;
    private String password_auto;
    //用来表示当前用什么平台来登录的
    private String current_media;

    private boolean isNeedCode = false;

    private ToggleButton mToggleButton;

    @Override
    protected void onPause() {
        super.onPause();
        TravelApp.checkIntercepted(FreshLoginActivity.this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresh_login);
        initActionBar();
        email_auto = getIntent().getStringExtra("email");
        password_auto = getIntent().getStringExtra("password");
        initView();
        ShareManager.getInstance().onStart(this);
        ShareManager.getInstance().setOnShareManagerGetInfoListener(new ShareManager.OnShareManagerGetInfoListener() {
            @Override
            public void OnGetInfoSuccess(Map<String, Object> infos, SHARE_MEDIA share_media) {
                if (share_media == SHARE_MEDIA.SINA) {
                    current_media = "sina";
                    thirdLogin(setSinaParams(infos));
                }
                if (share_media == SHARE_MEDIA.QQ) {
                    current_media = "qq";
                    thirdLogin(setQQParams(infos));
                }
                if (share_media == SHARE_MEDIA.WEIXIN) {
                    current_media = "wechat";
                    thirdLogin(setWechatParams(infos));
                }
            }

            @Override
            public void OnGetOnfifaild(SHARE_MEDIA share_media) {
                if (mCircularProgressBar != null)
                    mCircularProgressBar.setVisibility(View.GONE);
                sina.setEnabled(true);
            }
        });
        ShareManager.getInstance().setOnShareManagerOauthListener(new ShareManager.OnShareManagerOauthListener() {
            @Override
            public void OnOauthSuccess(SHARE_MEDIA share_media) {
                if (mCircularProgressBar != null)
                    mCircularProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void OnOauthfaild(SHARE_MEDIA share_media) {
                if (mCircularProgressBar != null)
                    mCircularProgressBar.setVisibility(View.GONE);
                sina.setEnabled(true);
            }

            @Override
            public void OnOauthCancle(SHARE_MEDIA share_media) {
                if (mCircularProgressBar != null)
                    mCircularProgressBar.setVisibility(View.GONE);
                sina.setEnabled(true);
            }
        });
    }

    private Map<String, Object> setSinaParams(Map<String, Object> infos) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (infos != null && infos.size() != 0) {
            if (infos.get("screen_name") != null)
                params.put("nickName", infos.get("screen_name"));
            if (infos.get("uid") != null)
                params.put("thirdUid", infos.get("uid"));
            if (infos.get("profile_image_url") != null)
                params.put("headImg", infos.get("profile_image_url"));
//                    if (infos.get("gender") != null)
//                        params.put("gender", infos.get("gender"));
            params.put("securityKey", PASSWORD);

        }
        return params;
    }

    private Map<String, Object> setQQParams(Map<String, Object> infos) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (infos != null && infos.size() != 0) {
            if (infos.get("screen_name") != null)
                params.put("nickName", infos.get("screen_name"));
            if (infos.get("uid") != null)
                params.put("thirdUid", infos.get("uid"));
            if (infos.get("profile_image_url") != null)
                params.put("headImg", infos.get("profile_image_url"));
//                    if (infos.get("gender") != null)
//                        params.put("gender", infos.get("gender"));
            params.put("securityKey", PASSWORD);

        }
        return params;
    }

    private Map<String, Object> setWechatParams(Map<String, Object> infos) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (infos != null && infos.size() != 0) {
            if (infos.get("screen_name") != null)
                params.put("nickName", infos.get("screen_name"));
            if (infos.get("uid") != null)
                params.put("thirdUid", infos.get("uid"));
            if (infos.get("profile_image_url") != null)
                params.put("headImg", infos.get("profile_image_url"));
//                    if (infos.get("gender") != null)
//                        params.put("gender", infos.get("gender"));
            params.put("securityKey", PASSWORD);

        }
        return params;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fresh_login:
                loginByEmail();
                break;
            case R.id.fresh_login_forget_password:
                Intent passwordIntent = new Intent(this, PasswordBackActivity.class);
                startActivity(passwordIntent);
                break;
            case R.id.fresh_login_register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivityForResult(registerIntent, JUMP_TO_REGISTER);
                break;
            case R.id.refresh_login_qq:
                ShareManager.getInstance().doOauthVerify(SHARE_MEDIA.QQ);
                break;
            case R.id.refresh_login_sina:
                sina.setEnabled(false);
                ShareManager.getInstance().doOauthVerify(SHARE_MEDIA.SINA);
                break;
            case R.id.refresh_login_wechat_circle:
                ShareManager.getInstance().doOauthVerify(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.refresh_login_message:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
                break;
            case R.id.action_bar_left:
                finish();
                break;
            case R.id.find_password_change_code:
                doLoadPic();
                break;
        }
    }

    private String emailString;

    private void loginByEmail() {
        if (!NetUtils.isNetworkAvailable(this)){
            Toast.makeText(this, R.string.network_unavailable,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        emailString = email.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)) {
            ToastUtils.show(FreshLoginActivity.this, R.string.hint_login_input_email);
            return;
        }
        if (!FormatUtils.isEmail(emailString)) {
            ToastUtils.show(FreshLoginActivity.this, R.string.hint_login_input_ok_email);
            return;
        }
        String passwordString = password.getText().toString().trim();
        if (TextUtils.isEmpty(passwordString)) {
            ToastUtils.show(FreshLoginActivity.this, R.string.hint_login_input_password);
            return;
        }

//        if (!FormatUtils.isContainNumberAndChar(passwordString) || !FormatUtils.firstIsChar(passwordString)) {
//            ToastUtils.show(FreshLoginActivity.this, R.string.hint_login_input_ok_password);
//            return;
//        }
        String codeString = code.getText().toString().trim();
        if (isNeedCode && TextUtils.isEmpty(codeString)) {
            ToastUtils.show(FreshLoginActivity.this, R.string.hint_login_input_pwd);
            return;
        }
        current_media = "email";
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            String email_encrypt = AESEncrpt.Encrypt(emailString, ServerAPI.AESE_KEY);
            String password_encrypt = AESEncrpt.Encrypt(passwordString, ServerAPI.AESE_KEY);
            params.put("email", email_encrypt);
            params.put("password", password_encrypt);
            if (BuildConfig.DEBUG) {
                android.util.Log.e("AESEncrpt---->email", email_encrypt);
                android.util.Log.e("AESEncrpt---->password", password_encrypt);
                android.util.Log.e("AESEncrpt1111---->email", AESEncrpt.Decrypt(email_encrypt, ServerAPI.AESE_KEY));
                android.util.Log.e("AESEncrpt1111---->passw", AESEncrpt.Decrypt(password_encrypt, ServerAPI.AESE_KEY));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isNeedCode) {
            try {
//                params.put("verifyCode", AESEncrpt.Encrypt(codeString, ServerAPI.AESE_KEY));
                params.put("verifyCode", codeString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        login.setEnabled(false);
        new LoginByEmail(this, new UploadTask.UploadCallBack() {
            @Override
            public void onSuccess(String result) {
                //                /**
//                 *   errorCode 错误代码：
//                 101 邮箱不存在（输入有误；未进行注册）
//                 102 密码输入错误
//                 103 验证码输入错误
//                 104 未输入验证码（申请验证码后，用户未输入验证码就登录，会报此错误）
//                 */
                login.setEnabled(true);
                QHToken info = new Gson().fromJson(result, QHToken.class);
                if (info != null && !info.isSuccessful() && info.getErrorcode() != 0) {
                    if (info.getErrorcode() == 101)
                        ToastUtils.show(FreshLoginActivity.this, R.string.register_no_email);
                    if (info.getErrorcode() == 102) {
                        ToastUtils.show(FreshLoginActivity.this, R.string.login_password_error);
                        isNeedCode = true;
                        if (isNeedCode && code_layout != null) {
                            code_layout.setVisibility(View.VISIBLE);
                            doLoadPic();
                        }
                    }
                    if (info.getErrorcode() == 103) {
                        ToastUtils.show(FreshLoginActivity.this, R.string.login_code_error);
                        doLoadPic();
                        code.setText("");
                        return;
                    }
                    if (info.getErrorcode() == 104)
                        ToastUtils.show(FreshLoginActivity.this, R.string.login_code_no);

                    password.setText("");
                    return;
                }
                AppUtils.saveQHToken(FreshLoginActivity.this, result);

                mUserName = info.userId + "";
                mPassWord = info.password;
                Log.e("=mUserName" + mUserName, "mPassWord" + mPassWord);
                //登陆小溪服务器
                if (mUserName != null && mPassWord != null) {
                    IMApp.getInstance().doLogin(mUserName, mPassWord);
                }

                //获取friend token 并保存
                getFriendToken();
            }

            @Override
            public void onFailed() {
                ToastUtils.show(FreshLoginActivity.this, "登录失败");
                login.setEnabled(true);
            }
        }, params).execute();

    }

    /**
     * 退出故意验证失败,以至于下次登录如果在5分钟内，不需要再次输入验证码
     */
    private void loginByEmailVerifyFaild() {
        if (TextUtils.isEmpty(emailString)) {
            return;
        }
        current_media = "email";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", emailString);
        params.put("password", "adcdefg");
        params.put("verifyCode", "adcdefg");
        login.setEnabled(false);
        new LoginByEmail(getApplicationContext(), new UploadTask.UploadCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onFailed() {
            }
        }, params).execute();
    }

    private void initView() {
        email = (ExEditText) findViewById(R.id.fresh_login_email).findViewById(R.id.et_login_email);
        if (!TextUtils.isEmpty(email_auto)) {
            email.setText(email_auto);
        }
        password = (ExEditText) findViewById(R.id.fresh_login_email_password).findViewById(R.id.et_login_email);
        if (!TextUtils.isEmpty(password_auto)) {
            password.setText(password_auto);
        }
        mTextView = (TextView) findViewById(R.id.fresh_login_email_password).findViewById(R.id.et_login_name);
        mTextView.setText(getResources().getString(R.string.prompt_password));
        password.setHint(getResources().getString(R.string.hint_login_input_email_password));
        login = (TextView) this.findViewById(R.id.fresh_login);
        forget_password = (TextView) this.findViewById(R.id.fresh_login_forget_password);
        register = (TextView) this.findViewById(R.id.fresh_login_register);
        qq = (TextView) this.findViewById(R.id.refresh_login_qq);
        message = (TextView) this.findViewById(R.id.refresh_login_message);
        wechat = (TextView) this.findViewById(R.id.refresh_login_wechat_circle);
        sina = (TextView) this.findViewById(R.id.refresh_login_sina);

        code = (ExEditText) findViewById(R.id.fresh_login_code).findViewById(R.id.find_password_code);
        codeImageView = (ImageView) findViewById(R.id.fresh_login_code).findViewById(R.id.find_password_code_image);
        codeImageView.setVisibility(View.VISIBLE);
        changeTextView = (TextView) findViewById(R.id.fresh_login_code).findViewById(R.id.find_password_change_code);
        code_layout = findViewById(R.id.fresh_login_code);
        code_layout.setVisibility(View.GONE);
        input_pic_code = (TextView) findViewById(R.id.fresh_login_code).findViewById(R.id.input_pic_code);
        input_pic_code.setVisibility(View.INVISIBLE);

        mToggleButton = (ToggleButton) this.findViewById(R.id.fresh_login_email_password).findViewById(R.id.et_login_email_show_password);
        mToggleButton.setVisibility(View.VISIBLE);
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        mCircularProgressBar = (CircularProgressBar) this.findViewById(R.id.loading_progress);
        login.setOnClickListener(this);
        forget_password.setOnClickListener(this);
        register.setOnClickListener(this);
        qq.setOnClickListener(this);
        message.setOnClickListener(this);
        wechat.setOnClickListener(this);
        sina.setOnClickListener(this);

        changeTextView.setOnClickListener(this);
    }

    private void initActionBar() {
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(getResources().getString(R.string.login_btn_login));
        actionBar.getTitleView().setTextColor(Color.WHITE);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        TravelApp.stop = true;
        super.onDestroy();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = ShareManager.getInstance().getController().getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) ssoHandler.authorizeCallBack(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == JUMP_TO_REGISTER) {
                if (!TextUtils.isEmpty(data.getStringExtra("email")) && !TextUtils.isEmpty(data.getStringExtra("password"))) {
                    email.setText(data.getStringExtra("email"));
                    password.setText(data.getStringExtra("password"));
                    loginByEmail();
                }
            }
            if (requestCode == LOGIN_REQUEST_CODE) {
                finish();
            }
        }
    }

    private String mUserName;
    private String mPassWord;

    /**
     * 第三方登录
     */
    private void thirdLogin(Map<String, Object> params) {
        new ThirdLoginTask(this, new UploadTask.UploadCallBack() {
            @Override
            public void onSuccess(String result) {
                AppUtils.saveQHToken(FreshLoginActivity.this, result);

                QHToken info = new Gson().fromJson(result, QHToken.class);

                mUserName = info.userId + "";
                mPassWord = info.password;
                Log.e("=mUserName" + mUserName, "mPassWord" + mPassWord);
                //登陆小溪服务器
                if (mUserName != null && mPassWord != null) {
                    IMApp.getInstance().doLogin(mUserName, mPassWord);
                }
                sina.setEnabled(true);
                //获取friend token 并保存
                getFriendToken();
            }

            @Override
            public void onFailed() {
                sina.setEnabled(true);
                ToastUtils.show(FreshLoginActivity.this, "登录失败");
                if (mCircularProgressBar != null)
                    mCircularProgressBar.setVisibility(View.GONE);
            }
        }, params).execute();
    }

    /**
     * 获取圈子验证后 加载默认
     */
    private void getFriendToken() {
        RequestManager.getInstance().sendGsonRequestAESforGET(ServerAPI.getFriendToken.buildAuthToken(), QHTokenId.class,
                new Response.Listener<QHTokenId>() {

                    @Override
                    public void onResponse(QHTokenId user) {
//                        Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_LONG).show();
//                        Log.d("QHTokenId, User: " + user.jsessionid);
                        //保存验证
                        AppUtils.saveFriendToken(FreshLoginActivity.this, new Gson().toJson(user), user.jsessionid);
                        AppUtils.saveDynamicKey(user.getDynamicKey());
                        //默认加载--我关注的人和黑名单
                        FreshLoginActivity.this.sendBroadcast(new Intent(IMConst.ACTION_MYATTENTION));
                        FreshLoginActivity.this.sendBroadcast(new Intent(IMConst.ACTION_BLACKLIST));
                        //获得用户信息
                        getUserInfo();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                        Toast.makeText(getApplicationContext(), "登录失败，请重试", Toast.LENGTH_SHORT).show();
                        //验证通过Friend和登陆小溪再销毁
//                        setResult(RESULT_OK);
//                        finish();
                        if (mCircularProgressBar != null)
                            mCircularProgressBar.setVisibility(View.GONE);

                        login.setEnabled(true);
                    }
                }, "", ServerAPI.AESE_KEY);
    }

    private void getUserInfo() {
        final String url = ServerAPI.BASE_URL + "users/current/";
        RequestManager.getInstance().sendGsonRequest(url, QHUser.class,
                new Response.Listener<QHUser>() {

                    @Override
                    public void onResponse(QHUser user) {
                        Log.e("==getUserInfo, User: " + user);
                        if (mCircularProgressBar != null)
                            mCircularProgressBar.setVisibility(View.GONE);
                        if (user.user_info != null && null == user.user_info.avatar_url)
                            user.user_info.avatar_url = "";
                        if (!isHasRegisterByUser(user.id+"")) {
                            MobclickAgent.onEvent(FreshLoginActivity.this, REGISTER_ID);
                            saveUserID(user.id + "");
                        }
                        //保存
                        AppUtils.saveUser(FreshLoginActivity.this, user);
                        if (!TextUtils.isEmpty(current_media))
                            AppUtils.saveOauth(FreshLoginActivity.this, current_media);
                        mUserName = user.username;
                        //验证通过Friend和登陆小溪，用户信息再销毁
                        setResult(RESULT_OK);
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse");
                        ToastUtils.show(FreshLoginActivity.this, "登录失败");
                        if (mCircularProgressBar != null)
                            mCircularProgressBar.setVisibility(View.GONE);

                        login.setEnabled(true);
                    }
                }, requestTag);
    }

    private class ThirdLoginTask extends AsyncTask<Object, String, String> {
        private Context mContext;
        private UploadTask.UploadCallBack callBack;
        private Map<String, Object> maps;

        private ThirdLoginTask(Context mContext, UploadTask.UploadCallBack callBack, Map<String, Object> params) {
            this.mContext = mContext;
            this.callBack = callBack;
            this.maps = params;
        }

        @Override
        protected String doInBackground(Object... params) {
            String backString = "";
            try {
                backString = upload(mContext, maps, THIRD_LOGIN);
            } catch (UpYunException e) {
                e.printStackTrace();
            }
            return backString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!TextUtils.isEmpty(s))
                callBack.onSuccess(s);
            else
                callBack.onFailed();
        }
    }

    private class LoginByEmail extends AsyncTask<Object, String, String> {
        private Context mContext;
        private UploadTask.UploadCallBack callBack;
        private Map<String, Object> maps;

        private LoginByEmail(Context mContext, UploadTask.UploadCallBack callBack, Map<String, Object> params) {
            this.mContext = mContext;
            this.callBack = callBack;
            this.maps = params;
        }

        @Override
        protected String doInBackground(Object... params) {
            String backString = "";
            try {
                backString = upload(mContext, maps, EMAIL_LOGIN_URL);
            } catch (UpYunException e) {
                e.printStackTrace();
            }
            return backString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!TextUtils.isEmpty(s))
                callBack.onSuccess(s);
            else
                callBack.onFailed();
        }
    }

    public String upload(Context mContext, Map<String, Object> params, String urlString) throws UpYunException {
        String returnStr = null;
        DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
        HttpPost localHttpPost = new HttpPost(urlString);
        MultipartEntity localMultipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        try {
            QHToken tokenInfo = AppUtils.getQHToken(mContext);
            if (tokenInfo != null && !TextUtils.isEmpty(tokenInfo.token)) {
                // localHttpPost.addHeader("Authorization", "JWT " + tokenInfo.token);
                localHttpPost.addHeader("Authorization", tokenInfo.token);
            }
            if (params != null && params.size() != 0) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    if (!TextUtils.isEmpty(entry.getKey()) && entry.getValue() != null){
                        if (entry.getKey().equals("thirdUid")){
                            String thirdUid = AESEncrpt.Encrypt(String.valueOf(entry.getValue()),ServerAPI.AESE_KEY);
                            localMultipartEntity.addPart(entry.getKey(), new StringBody(thirdUid));
                            continue;
                        }
                        if (entry.getKey().equals("securityKey")){
                            String securityKey = AESEncrpt.Encrypt(String.valueOf(entry.getValue()),ServerAPI.AESE_KEY);
                            localMultipartEntity.addPart(entry.getKey(), new StringBody(securityKey));
                            continue;
                        }
                        localMultipartEntity.addPart(entry.getKey(), new StringBody(String.valueOf(entry.getValue())));

                    }
                }
            }
            localHttpPost.setEntity(localMultipartEntity);
            HttpResponse localHttpResponse = localDefaultHttpClient.execute(localHttpPost);
            String str = EntityUtils.toString(localHttpResponse.getEntity());
            int code = localHttpResponse.getStatusLine().getStatusCode();
            if (code != HttpStatus.SC_OK && code != HttpStatus.SC_CREATED && code != HttpStatus.SC_ACCEPTED) {
                android.util.Log.e("Uploader 发布信息失败", "失败原因：" + str);
                JSONObject obj = new JSONObject(str);
                String msg = obj.getString("message");
                msg = new String(msg.getBytes("UTF-8"), "UTF-8");
                String url = obj.getString("url");
                long time = obj.getLong("time");
                boolean isSigned = false;
                String signString = "";
                if (!obj.isNull("sign")) {
                    signString = obj.getString("sign");
                    isSigned = true;
                } else if (!obj.isNull("non-sign")) {
                    signString = obj.getString("non-sign");
                    isSigned = false;
                }
                UpYunException exception = new UpYunException(code, msg);
                exception.isSigned = isSigned;
                exception.url = url;
                exception.time = time;
                exception.signString = signString;
                throw exception;
            } else {
                JSONObject obj = new JSONObject(str);
                returnStr = str;
            }
        } catch (Exception e) {
            android.util.Log.d("exception", e.toString());
        }
        return returnStr;
    }

    private void doLoadPic() {
        String emailString = email.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)) {
            ToastUtils.show(FreshLoginActivity.this, R.string.hint_login_input_email);
            return;
        }
        if (!FormatUtils.isEmail(emailString)) {
            ToastUtils.show(FreshLoginActivity.this, R.string.hint_login_input_ok_email);
            return;
        }
        String isEmailExist_url = ServerAPI.ADDRESS + "api/login/getLoginVerifyCode/" + "?email=" + emailString;
        getCode(codeImageView, isEmailExist_url);
    }

    private void getCode(ImageView imageView, String url) {
        ImageUtil.DisplayImage(url, imageView,
                R.drawable.recommand_bg, R.drawable.bg_image_error, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        ToastUtils.show(FreshLoginActivity.this, "请检查邮箱是否注册或正确");
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        if (bitmap == null) {
                            ToastUtils.show(FreshLoginActivity.this, "请检查邮箱是否注册或正确");
                            return;
                        }
                        input_pic_code.setVisibility(View.GONE);
                        codeImageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK && isNeedCode) {
            loginByEmailVerifyFaild();
        }
        return super.onKeyDown(keyCode, event);
    }

    private static final String REGISTER_ID = "register_event_id";
    private static final String KEY_USER_REGISTER = "key_user_register";

    private boolean isHasRegisterByUser(String mPhone) {
        String phones = PreferencesUtils.getString(this, KEY_USER_REGISTER);
        if (TextUtils.isEmpty(phones)) {
            return false;
        } else {
            if (phones.contains(",")) {
                String[] phoneArray = phones.split(",");
                for (String item : phoneArray) {
                    if (item.equals(mPhone)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private void saveUserID(String phoneString) {
        String phones = PreferencesUtils.getString(this, KEY_USER_REGISTER);
        if (TextUtils.isEmpty(phones)) {
            PreferencesUtils.putString(this, KEY_USER_REGISTER, phoneString + ",");
        } else {
            StringBuffer sb = new StringBuffer(phones);
            PreferencesUtils.putString(this, KEY_USER_REGISTER, sb.append(phoneString).append(",").toString());
        }
    }
}
