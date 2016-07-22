package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHRegister;
import com.cmcc.hyapps.andyou.model.QHState;
import com.cmcc.hyapps.andyou.model.QHToken;
import com.cmcc.hyapps.andyou.support.ExEditText;
import com.cmcc.hyapps.andyou.upyun.UpYunException;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.FormatUtils;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.google.gson.Gson;
import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.StringBody;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Map;

/**
 * 密码找回页面
 * Created by bingbing on 2015/12/10.
 */
public class PasswordBackActivity extends BaseActivity implements View.OnClickListener {
    private ImageView codeImageView;
    private TextView changeTextView, commitTextView;
    private ExEditText emailExEditText, codeExEditText;
    private ActionBar actionBar;
    private String GET_CODE_URL = ServerAPI.ADDRESS + "api/login/getLoginVerifyCode/";
    private TextView input_pic_code;
    private String emailString;
    private final int TO_JUMP_RESET = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_back_layout);
        initActionBar();
        initView();
    }

    private void doLoadPic() {
        emailString = emailExEditText.getText().toString().trim();
        if (!FormatUtils.isEmail(emailString)) {
            ToastUtils.show(PasswordBackActivity.this, R.string.hint_login_input_ok_email);
            return;
        }
        String isEmailExist_url = ServerAPI.ADDRESS + "api/login/getSendEmailVerifyCode/" + "?email=" + emailString;
        getCode(codeImageView, isEmailExist_url);
        // new loadPicInputStreamTask(PasswordBackActivity.this,isEmailExist_url,codeImageView,input_pic_code).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        codeImageView = (ImageView) findViewById(R.id.find_password_fresh_password).findViewById(R.id.find_password_code_image);
        changeTextView = (TextView) findViewById(R.id.find_password_fresh_password).findViewById(R.id.find_password_change_code);
        input_pic_code = (TextView) findViewById(R.id.find_password_fresh_password).findViewById(R.id.input_pic_code);
        commitTextView = (TextView) findViewById(R.id.find_password_commit);
        emailExEditText = (ExEditText) findViewById(R.id.find_password_email_layout).findViewById(R.id.find_password_email);
        emailExEditText.setVisibility(View.VISIBLE);
        codeExEditText = (ExEditText) findViewById(R.id.find_password_fresh_password).findViewById(R.id.find_password_code);
        codeExEditText.setHint("请输入验证码");
        changeTextView.setOnClickListener(this);
        commitTextView.setOnClickListener(this);
        emailExEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.toString().contains(".com") && FormatUtils.isEmail(s.toString())) {
                    input_pic_code.setEnabled(true);
                }
            }
        });
        input_pic_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_password_commit:
                doCommit();
                break;
            case R.id.find_password_change_code:
                doLoadPic();
                break;
            case R.id.action_bar_left:
                finish();
                break;
            case R.id.input_pic_code:
                doLoadPic();
                break;
        }
    }

    private void doCommit() {
        emailString = emailExEditText.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)){
            ToastUtils.show(PasswordBackActivity.this, R.string.hint_login_input_ok_email);
            return;
        }
        if (!FormatUtils.isEmail(emailString)) {
            ToastUtils.show(PasswordBackActivity.this, R.string.hint_login_input_ok_email);
            return;
        }
        String codeString = codeExEditText.getText().toString().trim();
        if (TextUtils.isEmpty(codeString)) {
            ToastUtils.show(this, "请输入验证码");
            return;
        }
        commitTextView.setEnabled(false);
        String isEmailExist_url = ServerAPI.ADDRESS + "api/login/sendVerifyEmail/" + "?email=" + emailString + "&verifyCode=" + codeString;
        new ReSetPasswordTask(this, new UploadTask.UploadCallBack() {
            @Override
            public void onSuccess(String result) {
                QHRegister mQhRegister = new Gson().fromJson(result,QHRegister.class);
                if (mQhRegister != null){
                    if (mQhRegister.isSuccessful()){
                        ToastUtils.show(PasswordBackActivity.this, "已经成功发送验证码到您的邮箱");
                        Intent intent = new Intent(PasswordBackActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", emailString);
                        startActivityForResult(intent, TO_JUMP_RESET);
                    }else
                        ToastUtils.show(PasswordBackActivity.this, "发送验证码失败");
                        doLoadPic();
                }
                commitTextView.setEnabled(true);

            }

            @Override
            public void onFailed() {
                ToastUtils.show(PasswordBackActivity.this, "发送验证码失败");
                doLoadPic();
                commitTextView.setEnabled(true);
            }
        }, null).execute(isEmailExist_url);
    }

    private void initActionBar() {
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(getResources().getString(R.string.find_password));
        actionBar.getTitleView().setTextColor(Color.WHITE);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    class loadPicInputStreamTask extends AsyncTask<Object, String, InputStream> {
        private final String url;
        private final Context mContext;
        private final ImageView codeImageView;
        private final TextView input_pic_code;

        public loadPicInputStreamTask(Context mContext, String url, ImageView codeImageView, TextView input_pic_code) {
            this.mContext = mContext;
            this.url = url;
            this.codeImageView = codeImageView;
            this.input_pic_code = input_pic_code;
        }

        @Override
        protected InputStream doInBackground(Object... params) {
            InputStream inputStream = null;
            HttpPost httpRequst = new HttpPost(url);//创建HttpPost对象
            try {
                HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    inputStream = httpResponse.getEntity().getContent();
                    return inputStream;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            if (inputStream == null) {
                ToastUtils.show(mContext, "请检查邮箱是否注册或正确");
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            input_pic_code.setVisibility(View.GONE);
            codeImageView.setVisibility(View.VISIBLE);
            codeImageView.setImageBitmap(bitmap);
        }
    }

    private void getCode(ImageView imageView, String url) {
        ImageUtil.DisplayImage(url, imageView,
                R.drawable.recommand_bg, R.drawable.bg_image_error, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        ToastUtils.show(PasswordBackActivity.this, "请检查邮箱是否注册或正确");
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        if (bitmap == null) {
                            ToastUtils.show(PasswordBackActivity.this, "请检查邮箱是否注册或正确");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TO_JUMP_RESET:
                    if (input_pic_code != null)
                        input_pic_code.setVisibility(View.VISIBLE);
                    if (codeImageView != null)
                        codeImageView.setVisibility(View.INVISIBLE);
                    if (codeExEditText != null)
                        codeExEditText.setText("");
                    break;
            }
        }
    }

    private class ReSetPasswordTask extends AsyncTask<String, String, String> {
        private Context mContext;
        private UploadTask.UploadCallBack callBack;
        private Map<String, Object> maps;

        private ReSetPasswordTask(Context mContext, UploadTask.UploadCallBack callBack, Map<String, Object> params) {
            this.mContext = mContext;
            this.callBack = callBack;
            this.maps = params;
        }

        @Override
        protected String doInBackground(String... params) {
            String backString = "";
            try {
                backString = upload(mContext, maps,params[0]);
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

    public String upload(Context mContext, Map<String, Object> params,String urlString) throws UpYunException {
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
}
