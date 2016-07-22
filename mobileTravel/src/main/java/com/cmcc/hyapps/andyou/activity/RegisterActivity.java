package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHHasRegister;
import com.cmcc.hyapps.andyou.model.QHRegister;
import com.cmcc.hyapps.andyou.model.QHToken;
import com.cmcc.hyapps.andyou.support.ExEditText;
import com.cmcc.hyapps.andyou.upyun.UpYunException;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.AESEncrpt;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.FormatUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.StringBody;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册页面
 * Created by bingbing on 2015/12/10.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private ExEditText email, password;
    private TextView registerTextView;
    private ToggleButton mToggleButton;
    private String register_url = ServerAPI.ADDRESS + "api/login/emailRegist/";
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_left:
                finish();
                break;
            case R.id.register:
                isHasRegister();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);
        initActionBar();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(getResources().getString(R.string.login_btn_register));
        actionBar.getTitleView().setTextColor(Color.WHITE);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    private void initView() {
        email = (ExEditText) findViewById(R.id.register_email).findViewById(R.id.find_password_email);
        email.setVisibility(View.VISIBLE);
        email.setHint(getResources().getString(R.string.hint_login_input_register_email));
        password = (ExEditText) findViewById(R.id.register_email_password).findViewById(R.id.find_password_emails);
        password.setVisibility(View.VISIBLE);
        registerTextView = (TextView) findViewById(R.id.register);
        registerTextView.setOnClickListener(this);
        mToggleButton = (ToggleButton) findViewById(R.id.register_email_password).findViewById(R.id.find_password_emails_show_password);
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
    }

    private void regisiter() {
        if (TextUtils.isEmpty(emailString) && TextUtils.isEmpty(passwordString)) {
            ToastUtils.show(RegisterActivity.this, R.string.register_faild);
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            params.put("email", AESEncrpt.Encrypt(emailString,ServerAPI.AESE_KEY));
            params.put("password", AESEncrpt.Encrypt(passwordString,ServerAPI.AESE_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        new RegisterTask(this, new UploadTask.UploadCallBack() {
            @Override
            public void onSuccess(String result) {
                ToastUtils.show(RegisterActivity.this, R.string.register_success);
                Intent back = new Intent();
                back.putExtra("email", emailString);
                back.putExtra("password", passwordString);
                setResult(RESULT_OK, back);
                RegisterActivity.this.finish();
            }

            @Override
            public void onFailed() {
                ToastUtils.show(RegisterActivity.this, R.string.register_faild);
                registerTextView.setEnabled(true);
            }
        }, params).execute();
//        RequestManager.getInstance().sendGsonRequest(Request.Method.GET, register_url, QHRegister.class, "", new Response.Listener<QHRegister>() {
//            @Override
//            public void onResponse(QHRegister response) {
//                Intent back = new Intent();
//                back.putExtra("email", emailString);
//                back.putExtra("password", passwordString);
//                setResult(RESULT_OK, back);
//                RegisterActivity.this.finish();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                ToastUtils.show(RegisterActivity.this, R.string.register_faild);
//            }
//        }, false, "regisiter");
    }

    private class RegisterTask extends AsyncTask<Object, String, String> {
        private Context mContext;
        private UploadTask.UploadCallBack callBack;
        private Map<String, Object> maps;

        private RegisterTask(Context mContext, UploadTask.UploadCallBack callBack, Map<String, Object> params) {
            this.mContext = mContext;
            this.callBack = callBack;
            this.maps = params;
        }

        @Override
        protected String doInBackground(Object... params) {
            String backString = "";
            try {
                backString = upload(mContext, maps, register_url);
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
                    if (!TextUtils.isEmpty(entry.getKey()) && entry.getValue() != null)
                        localMultipartEntity.addPart(entry.getKey(), new StringBody(String.valueOf(entry.getValue())));
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

    /**
     * 邮箱是否已经注册过
     *
     * @return
     */
    private String emailString;
    private String passwordString;

    private void isHasRegister() {
        emailString = email.getText().toString().trim();
        passwordString = password.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)) {
            ToastUtils.show(RegisterActivity.this, R.string.hint_login_input_email);
            return;
        }
        if (TextUtils.isEmpty(passwordString)) {
            ToastUtils.show(RegisterActivity.this, R.string.hint_login_input_password);
            return;
        }
        if (!FormatUtils.isEmail(emailString)) {
            ToastUtils.show(RegisterActivity.this, R.string.hint_login_input_ok_email);
            return;
        }
        if (!FormatUtils.isContainNumberAndChar(passwordString) || !FormatUtils.firstIsChar(passwordString)) {
            ToastUtils.show(RegisterActivity.this, R.string.hint_login_input_ok_password);
            return;
        }
        registerTextView.setEnabled(false);
        String isEmailExist_url = ServerAPI.ADDRESS + "api/login/isEmailExised/" + "?email=" + emailString;
        RequestManager.getInstance().sendGsonRequest(Request.Method.GET, isEmailExist_url, QHHasRegister.class, "", new Response.Listener<QHHasRegister>() {
            @Override
            public void onResponse(QHHasRegister response) {
                if (!response.isEmailExisted())
                    regisiter();
                else {
                    registerTextView.setEnabled(true);
                    ToastUtils.show(RegisterActivity.this, R.string.register_emai_has_register);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(RegisterActivity.this, R.string.register_faild);
                registerTextView.setEnabled(true);
            }
        }, false, "isHasRegister");
    }
}
