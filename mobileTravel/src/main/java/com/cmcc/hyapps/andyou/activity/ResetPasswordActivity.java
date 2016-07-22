package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
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

import java.util.Map;

/**
 * 重置密码界面
 * Created by bingbing on 2015/12/10.
 */
public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private ActionBar actionBar;
    private TextView email, commit, resend_code;
    private ExEditText fresh_code, fresh_password;
    private String emailString;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_left:
                finish();
                break;
            case R.id.reset_password_commit:
                doCommit();
                break;
            case R.id.reset_password_send_code:
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_layout);
        emailString = getIntent().getStringExtra("email");
        initView();
        initActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        email = (TextView) findViewById(R.id.reset_password_email).findViewById(R.id.find_password_has_register_email);
        email.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(emailString))
            email.setText(emailString);
        commit = (TextView) findViewById(R.id.reset_password_commit);
        commit.setOnClickListener(this);
        fresh_password = (ExEditText) findViewById(R.id.reset_password_fresh_password).findViewById(R.id.find_password_emails);
        fresh_password.setVisibility(View.VISIBLE);
        fresh_password.setHint("请输入新密码");

        fresh_code = (ExEditText) findViewById(R.id.reset_password_code_layout).findViewById(R.id.reset_password_code);
        resend_code = (TextView) findViewById(R.id.reset_password_code_layout).findViewById(R.id.reset_password_send_code);
        resend_code.setOnClickListener(this);
    }

    private void initActionBar() {
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("重置密码");
        actionBar.getTitleView().setTextColor(Color.WHITE);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    private void doCommit() {
        final String passwordString = fresh_password.getText().toString().trim();
        if (TextUtils.isEmpty(passwordString)) {
            ToastUtils.show(this, "请输入新密码");
            return;
        }
        String codeString = fresh_code.getText().toString().trim();
        if (TextUtils.isEmpty(codeString)) {
            ToastUtils.show(this, "请输入验证码");
            return;
        }
        if (!FormatUtils.isContainNumberAndChar(passwordString) || !FormatUtils.firstIsChar(passwordString)) {
            ToastUtils.show(this, R.string.hint_login_input_ok_password);
            return;
        }
        commit.setEnabled(false);
        String url = "";
        try {
            url = ServerAPI.ADDRESS + "api/login/resetEmailPassword/?email=" + AESEncrpt.Encrypt(emailString, ServerAPI.AESE_KEY) + "&verifyCode=" + codeString + "&password=" + AESEncrpt.Encrypt(passwordString,ServerAPI.AESE_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url, QHRegister.class, "", new Response.Listener<QHRegister>() {
            @Override
            public void onResponse(QHRegister response) {
                if (response.isSuccessful()) {
                    ToastUtils.show(ResetPasswordActivity.this, "密码重置成功");
                    Intent intent = new Intent(ResetPasswordActivity.this,FreshLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("email", emailString);
                    intent.putExtra("password",passwordString);
                    startActivity(intent);
                } else
                    ToastUtils.show(ResetPasswordActivity.this, "密码重置失败");

                commit.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(ResetPasswordActivity.this, "密码重置失败");
                commit.setEnabled(true);
            }
        }, false, "doCommit");

    }

    private class ReSetPasswordTask extends AsyncTask<Object, String, String> {
        private Context mContext;
        private UploadTask.UploadCallBack callBack;
        private Map<String, Object> maps;

        private ReSetPasswordTask(Context mContext, UploadTask.UploadCallBack callBack, Map<String, Object> params) {
            this.mContext = mContext;
            this.callBack = callBack;
            this.maps = params;
        }

        @Override
        protected String doInBackground(Object... params) {
            String backString = "";
            try {
                backString = upload(mContext, maps);
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

    public String upload(Context mContext, Map<String, Object> params) throws UpYunException {
        String returnStr = null;
        DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
        String urlString = ServerAPI.ADDRESS + "";
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
}
