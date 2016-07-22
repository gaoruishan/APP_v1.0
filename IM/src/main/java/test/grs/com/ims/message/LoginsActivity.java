package test.grs.com.ims.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ViewInject;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;

public class LoginsActivity extends IMBaseActivity {
    private EditText mUsername_EditText;
    private EditText mPassword_EditText;
    private TextView tv_version;
    private Button mRegister_Button;
    private Button mLogin_Button;
    private TextView mPsd_TextView;

    private BroadcastReceiver receiverService = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IMConst.ACTION_LOGIN_SUCCESS.equals(action)) {
                // 登录成功
                hideDialog();
                Toast.makeText(LoginsActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginsActivity.this, MessageListActivity.class));
                finish();//退出
            } else if (IMConst.ACTION_LOGIN_START.equals(action)) {
                Log.e("Logining", "TAG");
                 showLogining();
            } else if (IMConst.ACTION_LOGIN_FAIL.equals(action)) {
                //登录失败
                String errorMsg = intent.getStringExtra(IMConst.LOGIN_FAIL_MSG);
//                Toast.makeText(LoginsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }
    };
    private Dialog mLoadingDialog;

    private void showLogining() {
        mLoadingDialog = DialogFactory.getLoadingDialog(this, this.getString(R.string.hint_logining), false, null);

        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    private void hideDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销服务
        if (receiverService != null) {
            unregisterReceiver(receiverService);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //开启监听登录的广播
        IntentFilter filter = new IntentFilter(IMConst.ACTION_LOAD_CONTACT_FAIL);
        filter.addAction(IMConst.ACTION_LOAD_CONTACT_SUCCESS);
        filter.addAction(IMConst.ACTION_LOGIN_ACCOUNT_CONFLICT);
        filter.addAction(IMConst.ACTION_LOGIN_FAIL);
        filter.addAction(IMConst.ACTION_LOGIN_START);
        filter.addAction(IMConst.ACTION_LOGIN_SUCCESS);
        filter.addAction(IMConst.NET_DISCONNECT);
        filter.addAction(IMConst.NET_RECONNECT);
        registerReceiver(receiverService, filter);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){      //首次安装打开登录后，点击app图标打开登录界面
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        initView();
        setUpView();
    }
    @Override
    protected void onResume() {
        if(IMApp.isBackGroundServiceRunning == true){
            Intent intentMain = new Intent(LoginsActivity.this, MessageListActivity.class);
            startActivity(intentMain);
            LoginsActivity.this.finish();   //每次打开都有,否则会有多个
        }
        super.onResume();
    }

    private void initView() {
        mUsername_EditText = (EditText)findViewById(R.id.et_username);
        mPassword_EditText = (EditText)findViewById(R.id.et_password);
        tv_version = (TextView) findViewById(R.id.tv_version);
        mRegister_Button = (Button)findViewById(R.id.btn_register);
        mLogin_Button = (Button)findViewById(R.id.btn_login);
        mPsd_TextView = (TextView)findViewById(R.id.btn_find_psd);//忘记密码
        mRegister_Button.setOnClickListener(mOnClickListener);
        mLogin_Button.setOnClickListener(mOnClickListener);
        mPsd_TextView.setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_set).setOnClickListener(mOnClickListener);
        // 用户名变动，清空密码
        mUsername_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPassword_EditText.setText("");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int view_id = view.getId();

            if(R.id.btn_register == view_id) {
                Intent intent = new Intent(LoginsActivity.this, RegisterActivity.class);
                startActivityForResult(intent, IMConst.SETUP_REGISTER);
            }
            else if(R.id.btn_login == view_id) {
                Log.e("登陆","TAG");
                final String userName = ((EditText)findViewById(R.id.et_username)).getText().toString();
                final String passWord = ((EditText)findViewById(R.id.et_password)).getText().toString();

//                doLogin(userName,passWord);

            }
            else if(R.id.btn_find_psd == view_id) {

            }else if (R.id.btn_set == view_id) {
//                onSetServerAndAppKey();
            }
        }
    };

    private void setUpView()  {
        mUsername_EditText.setText(IMSharedPreferences.getString(IMSharedPreferences.ACCOUNT, ""));
        mPassword_EditText.setText(IMSharedPreferences.getString(IMSharedPreferences.PASSWORD, ""));
        tv_version.setText("版本号：" + getAppVersionName());
    }
    private String getAppVersionName() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
        String version;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            version = "获取失败";
            return version;
        }
        version = packInfo.versionName;
        return version;
    }

}
