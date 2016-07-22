/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.ErrorCode;
import com.cmcc.hyapps.andyou.app.TravelApp;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.ResponseError;
import com.cmcc.hyapps.andyou.model.QHToken;
import com.cmcc.hyapps.andyou.model.QHTokenId;
import com.cmcc.hyapps.andyou.model.QHUser;
import com.cmcc.hyapps.andyou.upyun.UpYunException;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.AESEncrpt;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.circularprogressbar.CircularProgressBar;
import com.google.gson.Gson;
import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import test.grs.com.ims.IMApp;
import test.grs.com.ims.message.IMConst;

/**
 * @author kuloud
 */
public class LoginActivity extends BaseActivity implements UploadTask.UploadCallBack {
    private static final String KEY_PHONE = "key_phone";
    private static final String EXTRA_PHONE = "extra_phone";
    private static final String EXTRA_CODE = "extra_code";
    private static final String REGISTER_ID = "register_event_id";
    private static final String KEY_PHONE_REGISTER = "key_phone_register";
    private static final String KEY_USER_REGISTER = "key_user_register";

    @InjectView(R.id.action_bar_progress)
    CircularProgressBar progressBar;

    @InjectView(R.id.et_login_phone)
    EditText phoneEditText;

    @InjectView(R.id.tv_login_valid_code)
    TextView sendValidCode;

    @InjectView(R.id.et_login_pwd)
    EditText confirmCodeEditText;

    @InjectView(R.id.tv_login)
    TextView activiteButton;

    private Context mContext;
    private String mPhone;
    private String mConfirmCode;

    private int mCount;
    private Timer mTimer;
    private boolean codeState;
    private static String type;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what > 0) {
                sendValidCode.setText(getString(R.string.register_btn_count_time, msg.what));
            } else {
                sendValidCode.setEnabled(true);
                codeState = true;
                sendValidCode.setText(R.string.register_btn_security_code_resend);
                mTimer.cancel();
            }
        }

        ;
    };

    @Override
    protected void onPause() {
        super.onPause();
        TravelApp.checkIntercepted(LoginActivity.this);
    }

    private String mUserName;
    private String mUserNickName;
    private String mPassWord;

    private String current_media = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_login);
        ButterKnife.inject(activity);
        mPhone = PreferencesUtils.getString(mContext, KEY_PHONE);
        phoneEditText.setText(mPhone);
        if (mPhone != null && TextUtils.getTrimmedLength(mPhone) == 11)
            sendValidCode.setEnabled(true);
        codeState = true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            phoneEditText.setText(savedInstanceState.getString(EXTRA_PHONE));
            confirmCodeEditText.setText(savedInstanceState.getString(EXTRA_CODE));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_PHONE, phoneEditText.getText().toString());
        outState.putString(EXTRA_CODE, confirmCodeEditText.getText().toString());
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);

        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
            mTimer = null;
        }
        TravelApp.stop = true;
        super.onDestroy();
    }

    @OnTextChanged(R.id.et_login_phone)
    void onPhoneChanged(CharSequence phoneNum) {
        CharSequence confirmCode = confirmCodeEditText.getText();
        onTextChanged(phoneNum, confirmCode);
    }

    @OnTextChanged(R.id.et_login_pwd)
    void onConfirmCodeChanged(CharSequence confirmCode) {
        CharSequence phone = phoneEditText.getText();
        onTextChanged(phone, confirmCode);
    }

    private void onTextChanged(CharSequence phone, CharSequence confirmCode) {
        if (TextUtils.getTrimmedLength(phone) == 11) {
            if (codeState)
                sendValidCode.setEnabled(true);

            activiteButton.setEnabled(true);
        } else {
            activiteButton.setEnabled(false);

        }
    }

    @OnClick(R.id.action_bar_left)
    void onBackClick() {
        finish();
    }

    @OnClick(R.id.tv_login_valid_code)
    void sendValidCode() {
        clearErrors();

        // Store values at the time of the login attempt.
        mPhone = phoneEditText.getText().toString();

        // Check for a valid phone number.
        if (TextUtils.isEmpty(mPhone)) {
            phoneEditText.setError(getString(R.string.error_phone_empty));
            return;
        } else if (mPhone.length() != 11) {
            phoneEditText.setError(getString(R.string.error_invalid_phone));
            return;
        } else if (!ConstTools.checkTelPhone(mPhone)) {
            phoneEditText.setError(getString(R.string.error_invalid_phone));
            return;
        }

        {
            sendValidCode.setEnabled(false);
            sendValidCode.setText(R.string.register_btn_security_code_sending);
            progressBar.setVisibility(View.VISIBLE);
            ValidCode obj = null;
            try {
                obj = new ValidCode(AESEncrpt.Encrypt(mPhone, ServerAPI.AESE_KEY), 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            String jsonBody = gson.toJson(obj);
            type = "send_code";
//                new SendCodeTask(mContext, LoginActivity.this, ServerAPI.BASE_URL + "send_code/", bodyparams).execute("", "");
                new SendCodeTask(mContext, LoginActivity.this, ServerAPI.BASE_URL + "send_sms_code/", jsonBody).execute("", "");
//            String url = ServerAPI.BASE_URL + "send_code/";

//            RequestManager.getInstance().sendGsonRequest(Request.Method.POST, url,
//                    jsonBody,
//                    QHActiviteResponse.class, new Response.Listener<QHActiviteResponse>() {
//                        @Override
//                        public void onResponse(QHActiviteResponse response) {
//                            codeState = false;
//                            progressBar.setVisibility(View.GONE);
//                            Log.e("onResponse, response nextSendDelay: " + response.details);
//                            int time = 60000;
//                            mCount = time / 1000;
//                            TimerTask task = new TimerTask() {
//
//                                @Override
//                                public void run() {
//                                    Message msg = new Message();
//                                    msg.what = mCount--;
//                                    mHandler.sendMessage(msg);
//                                }
//                            };
//                            if (mTimer != null) {
//                                mTimer.purge();
//                                mTimer.cancel();
//                            }
//                            mTimer = new Timer();
//                            mTimer.schedule(task, 1000, 1000);
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.e(error, "onErrorResponse");
//                            handleResponseError(error);
//                            sendValidCode.setText(R.string.register_btn_security_code_resend);
//                            sendValidCode.setEnabled(true);
//                        }
//                    }, requestTag);
//            RequestManager.getInstance().sendGsonRequest(/*ServerAPI.User.buildActiviteUrl()*/url,
//                    QHActiviteResponse.class, new Response.Listener<QHActiviteResponse>() {
//
//                        @Override
//                        public void onResponse(QHActiviteResponse response) {
//                            progressBar.setVisibility(View.GONE);
//                            Log.e("onResponse, response nextSendDelay: " + response.details);
//                            int time = 2000;
//                            mCount = time / 1000;
//                            TimerTask task = new TimerTask() {
//
//                                @Override
//                                public void run() {
//                                    Message msg = new Message();
//                                    msg.what = mCount--;
//                                    mHandler.sendMessage(msg);
//                                }
//                            };
//                            if (mTimer != null) {
//                                mTimer.purge();
//                                mTimer.cancel();
//                            }
//                            mTimer = new Timer();
//                            mTimer.schedule(task, 1000, 1000);
//                        }
//                    }, new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.e(error, "onErrorResponse");
//                            handleResponseError(error);
//                            sendValidCode.setText(R.string.register_btn_security_code_resend);
//                            sendValidCode.setEnabled(true);
//                        }
//                    }, false,
//                    ServerAPI.User.buildActiviteParams(mPhone, VerifyCodeType.USER_REGISTER),
//                    requestTag);
        }
    }

    @Override
    public void onSuccess(String result) {
        Log.e("===result", result);
        if (type.equals("login")) {
//            Log.e("onResponse, response: " + response);
            progressBar.setVisibility(View.GONE);
//            if (!isHasRegister(mPhone)) {
//                MobclickAgent.onEvent(LoginActivity.this, REGISTER_ID);
//                savePhone(mPhone);
//            }
//            PreferencesUtils.putString(mContext, KEY_PHONE, mPhone);
            AppUtils.saveQHToken(mContext, result);

            QHToken info = new Gson().fromJson(result, QHToken.class);

            mUserName = info.userId + "";
            mPassWord = info.password;
            Log.e("=mUserName" + mUserName, "mPassWord" + mPassWord);
            //登陆小溪服务器
            if (mUserName != null && mPassWord != null) {
                IMApp.getInstance().doLogin(mUserName, mPassWord);
            }

            //获取friend token 并保存
            getFriendToken();


        } else {
            codeState = false;
            progressBar.setVisibility(View.GONE);
            Log.e("onResponse, response nextSendDelay: " + result);
            int time = 60000;
            mCount = time / 1000;
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = mCount--;
                    mHandler.sendMessage(msg);
                }
            };
            if (mTimer != null) {
                mTimer.purge();
                mTimer.cancel();
            }
            mTimer = new Timer();
            mTimer.schedule(task, 1000, 1000);
        }

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
                        AppUtils.saveFriendToken(LoginActivity.this, new Gson().toJson(user), user.jsessionid);
                        AppUtils.saveDynamicKey(user.getDynamicKey());
                        //默认加载--我关注的人和黑名单
                        LoginActivity.this.sendBroadcast(new Intent(IMConst.ACTION_MYATTENTION));
                        LoginActivity.this.sendBroadcast(new Intent(IMConst.ACTION_BLACKLIST));
                        //获得用户信息
                        getUserInfo();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                        Toast.makeText(getApplicationContext(), "网络验证错误", Toast.LENGTH_SHORT).show();
                        //验证通过Friend和登陆小溪再销毁
//                        setResult(RESULT_OK);
//                        finish();
                    }
                }, "",ServerAPI.AESE_KEY);
    }

    @Override
    public void onFailed() {
        if (type.equals("send_code")) {

            ToastUtils.show(this, "发送验证码失败");
            sendValidCode.setText(R.string.register_btn_security_code_resend);
            sendValidCode.setEnabled(true);
        } else {
            handleResponseError();
        }
    }

    public class ValidCode {
        public String phone;
        public int type;

        ValidCode(String phone, int type) {
            this.phone = phone;
            this.type = type;
        }
    }

    @OnClick(R.id.tv_login)
    void login() {
        clearErrors();

        View focusView = null;

        // Store values at the time of the login attempt.
        mPhone = phoneEditText.getText().toString();
        mConfirmCode = confirmCodeEditText.getText().toString();

        // Check for a valid confirm code.
        if (TextUtils.isEmpty(mConfirmCode)) {
            confirmCodeEditText.setError(getString(R.string.error_field_required));
            return;
        }
//        else if (mConfirmCode.length() != 6 ) {
//            confirmCodeEditText.setError(getString(R.string.error_invalid_confirm_code));
//            return;
//        }

        // Check for a valid phone number.
        if (TextUtils.isEmpty(mPhone)) {
            phoneEditText.setError(getString(R.string.error_phone_empty));
            return;
        } else if (mPhone.length() != 11) {
            phoneEditText.setError(getString(R.string.error_invalid_phone));
            return;
        } else if (!ConstTools.checkTelPhone(mPhone)) {
            phoneEditText.setError(getString(R.string.error_invalid_phone));
            return;
        }

        {
            handleLoginStart();
        }

    }

    private void handleLoginStart() {
        activiteButton.setEnabled(false);
        LoginObj obj = null;
        try {
            obj = new LoginObj(AESEncrpt.Encrypt(mPhone, ServerAPI.AESE_KEY), AESEncrpt.Encrypt(mConfirmCode,ServerAPI.AESE_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String jsonBody = gson.toJson(obj);
        String url = ServerAPI.BASE_URL + "sms_login/";
        type = "login";
        new SendCodeTask(mContext, LoginActivity.this, url, jsonBody).execute();

//        RequestManager.getInstance().sendGsonRequest(Request.Method.POST, url,
//                jsonBody,
//                QHToken.class, new Response.Listener<QHToken>() {
//                    @Override
//                    public void onResponse(QHToken response) {
//                        Log.e("onResponse, response: " + response);
//                        progressBar.setVisibility(View.GONE);
////                        AppUtils.saveTokenInfo(mContext, response);
//                        PreferencesUtils.putString(mContext, KEY_PHONE, mPhone);
//                        AppUtils.saveQHToken(mContext,response);
//                        getUserInfo();
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(error, "onErrorResponse");
//                        progressBar.setVisibility(View.GONE);
//                        handleResponseError(error);
//
//                    }
//                }, requestTag);
    }

    public class LoginObj {
        public String phone;
        public String activation_code;

        LoginObj(String p, String code) {
            phone = p;
            activation_code = code;
        }
    }

    private void handleResponseError(VolleyError error) {
        progressBar.setVisibility(View.GONE);
        activiteButton.setEnabled(true);
        if (error instanceof ResponseError) {
            ResponseError response = (ResponseError) error;
            switch (response.errCode) {
                case ErrorCode.ERROR_INVALID_PHONE:
                    phoneEditText.setError(getString(R.string.error_invalid_phone));
                    phoneEditText.requestFocus();
                    break;
                case ErrorCode.ERROR_INVALID_CODE:
                    confirmCodeEditText.setError(getString(R.string.error_invalid_confirm_code));
                    confirmCodeEditText.requestFocus();
                    break;
                case ErrorCode.ERROR_CODE_EXPIRE:
                    confirmCodeEditText.setError(getString(R.string.error_security_code_expire));
                    confirmCodeEditText.requestFocus();
                    break;
                default:
                    ToastUtils.show(mContext, R.string.error_unknown);
                    break;
            }
        } else {
            if (error instanceof NoConnectionError) {
                ToastUtils.show(mContext, R.string.network_unavailable);
            } else if (error instanceof TimeoutError) {
                ToastUtils.show(mContext, R.string.network_timeout);
            } else if (error instanceof ServerError) {
                ToastUtils.show(mContext, R.string.error_invalid_confirm_code);
            } else ToastUtils.show(mContext, R.string.error_unknown);
        }
    }

    private void handleResponseError() {
        progressBar.setVisibility(View.GONE);
        activiteButton.setEnabled(true);
        ToastUtils.show(mContext, "验证失败");
    }

    private void clearErrors() {
        phoneEditText.setError(null);
        confirmCodeEditText.setError(null);
    }

    private void getUserInfo() {
        final String url = ServerAPI.BASE_URL + "users/current/";
        RequestManager.getInstance().sendGsonRequest(url, QHUser.class,
                new Response.Listener<QHUser>() {

                    @Override
                    public void onResponse(QHUser user) {
                        Log.e("==getUserInfo, User: " + user);
                        if (user.user_info != null && null == user.user_info.avatar_url)
                            user.user_info.avatar_url = "";
                        if (!isHasRegisterByUser(user.id+"")) {
                            MobclickAgent.onEvent(LoginActivity.this, REGISTER_ID);
                            saveUserID(user.id + "");
                        }
                        //保存
                        AppUtils.saveUser(mContext, user);
                        if (!TextUtils.isEmpty(current_media))
                            AppUtils.saveOauth(LoginActivity.this, current_media);
                        mUserName = user.username;
                        //验证通过Friend和登陆小溪，用户信息再销毁
                        setResult(RESULT_OK);
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse");
//                        handleResponseError(error);
                        progressBar.setVisibility(View.GONE);
                        activiteButton.setEnabled(true);
                        ToastUtils.show(mContext, "登录失败");
                    }
                }, requestTag);
    }

    private class SendCodeTask extends AsyncTask<Object, String, String> {
        private Context mContext;
        private UploadTask.UploadCallBack callBack;
        private String mUrl;
        private String body;

        private SendCodeTask(Context mContext, UploadTask.UploadCallBack callBack, String mUrl, String body) {
            this.mContext = mContext;
            this.callBack = callBack;
            this.mUrl = mUrl;
            this.body = body;
        }

        @Override
        protected String doInBackground(Object... params) {
            String backString = "";
            try {
                backString = upload(mContext, mUrl, body);
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

    public String upload(Context mContext, String mUrl, String jsonBody) throws UpYunException {
        String returnStr = null;
        DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
        HttpPost localHttpPost = new HttpPost(mUrl);
        MultipartEntity localMultipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        try {
            QHToken tokenInfo = AppUtils.getQHToken(mContext);
            if (tokenInfo != null && !TextUtils.isEmpty(tokenInfo.token)) {
                // localHttpPost.addHeader("Authorization", "JWT " + tokenInfo.token);
                localHttpPost.addHeader("Authorization", tokenInfo.token);
            }
            localHttpPost.setEntity(localMultipartEntity);
            localHttpPost.setEntity(new StringEntity(jsonBody, "UTF-8"));
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

    private boolean isHasRegister(String mPhone) {
        String phones = PreferencesUtils.getString(mContext, KEY_PHONE_REGISTER);
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

    private boolean isHasRegisterByUser(String mPhone) {
        String phones = PreferencesUtils.getString(mContext, KEY_USER_REGISTER);
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

    private void savePhone(String phoneString) {
        String phones = PreferencesUtils.getString(mContext, KEY_PHONE_REGISTER);
        if (TextUtils.isEmpty(phones)) {
            PreferencesUtils.putString(mContext, KEY_PHONE_REGISTER, phoneString + ",");
        } else {
            StringBuffer sb = new StringBuffer(phones);
            PreferencesUtils.putString(mContext, KEY_PHONE_REGISTER, sb.append(phoneString).append(",").toString());
        }
    }

    private void saveUserID(String phoneString) {
        String phones = PreferencesUtils.getString(mContext, KEY_USER_REGISTER);
        if (TextUtils.isEmpty(phones)) {
            PreferencesUtils.putString(mContext, KEY_USER_REGISTER, phoneString + ",");
        } else {
            StringBuffer sb = new StringBuffer(phones);
            PreferencesUtils.putString(mContext, KEY_USER_REGISTER, sb.append(phoneString).append(",").toString());
        }
    }
}
