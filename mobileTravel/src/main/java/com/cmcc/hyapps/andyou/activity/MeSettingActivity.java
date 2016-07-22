package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.model.QHUser;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.CheckUpdateUtil;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.lang.reflect.Method;

import test.grs.com.ims.IMApp;

/**
 * 我的设置页面
 * Created by bingbing on 2015/10/28.
 */
public class MeSettingActivity extends BaseActivity implements View.OnClickListener {
    private View clearCache;
    private Button mButton;
    private long cachesize;
    private TextView cache_textview;
    private Double size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_setting_layout);
        initView();
        initActionBar();
        initListItems();
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        Intent intent = null;
        switch (v.getId()) {
            case R.id.item_my_blacklist:
                //黑名单
                QHUser user = AppUtils.getQHUser(this);
                if (user == null) {
                    intent = new Intent(this, FreshLoginActivity.class);
                } else {
                    startActivity(new Intent(this, BlackListActivity.class));
                }
                break;
            case R.id.item_my_suggestion:
                intent = new Intent(this, FeedbackActivity.class);
                break;
            case R.id.item_update:
                try {
                    CheckUpdateUtil.getInstance(this).getUpdataInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.item_clear_cache:
//                CacheClearHelper.clearCache(this);
//                updateCacheSize();
                if (FileUtils.isEnableSDCard() && FileUtils.delete(new File(Environment.getExternalStorageDirectory().getPath() + "/TravelApp"))) {
                    cache_textview.setText("0M");
                }
                break;
            case R.id.item_about:
                intent = new Intent(this, QHAboutActivity.class);
                break;
            case R.id.me_cancle_login:
                //注销小溪登陆
                IMApp.getInstance().doRegister();
                AppUtils.clearQHUser(this);
                AppUtils.clearQHToken(this);
                Intent cancle_login = new Intent(IndexActivity.CANCLE_LOGIN_BROADCASE);
                sendBroadcast(cancle_login);
                String share_media = AppUtils.getOauth(this);
                if (!TextUtils.isEmpty(share_media)) {
                    if (share_media.equals("sina"))
                        ShareManager.getInstance().deleteOauth(this, SHARE_MEDIA.SINA);
                    if (share_media.equals("qq"))
                        ShareManager.getInstance().deleteOauth(this, SHARE_MEDIA.QQ);
                    if (share_media.equals("wechat"))
                        ShareManager.getInstance().deleteOauth(this, SHARE_MEDIA.WEIXIN);
                }
                finish();
                break;
            case R.id.action_bar_left:
                finish();
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void initView() {
        mButton = (Button) this.findViewById(R.id.me_cancle_login);
        mButton.setOnClickListener(this);
        QHUser user = AppUtils.getQHUser(this);
        if (user == null) {
            mButton.setVisibility(View.INVISIBLE);
        } else
            mButton.setVisibility(View.VISIBLE);
        clearCache = this.findViewById(R.id.item_clear_cache);
        clearCache.setOnClickListener(this);
        cache_textview = (TextView) this.findViewById(R.id.item_text_cache_size);
        if (FileUtils.isEnableSDCard()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    size = FileUtils.getFileOrFilesSize(Environment.getExternalStorageDirectory().getPath() + "/xmpp", 3);
                    mHandler.sendEmptyMessage(1);
                }
            }).start();
//            mThread.start();
        }
    }

    @Override
    protected void onDestroy() {
//        mThread.stop();
//        mThread.destroy();
        super.onDestroy();
    }

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                cache_textview.setText(size + "M");
            }
        }
    };

    private void initListItems() {
        setItem(R.id.item_my_blacklist, R.string.me_item_blacklist);
        setItem(R.id.item_my_suggestion, R.string.settings_item_feedback);
        setItem(R.id.item_update, R.string.me_item_renovate);
//        setItem( R.id.item_clear_cache,  R.string.me_item_clear);
        setItem(R.id.item_about, R.string.settings_item_about);
    }

    private void setItem(int id, int textId) {
        View item = this.findViewById(id);
        item.setOnClickListener(this);
        ImageView icon = (ImageView) item.findViewById(R.id.item_icon);
        icon.setVisibility(View.GONE);
        TextView text = (TextView) item.findViewById(R.id.item_text);
        text.setText(textId);
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) this.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.me_item_settings);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    private void updateCacheSize() {
        try {
            queryPacakgeSize(this.getPackageName());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void queryPacakgeSize(String pkgName) throws Exception {
        if (pkgName != null) {
            //使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
            PackageManager pm = this.getPackageManager();  //得到pm对象
            try {
                //通过反射机制获得该隐藏函数
                Method getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                //调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
                getPackageSizeInfo.invoke(pm, pkgName, new PkgSizeObserver());
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;  // 抛出异常
            }
        }
    }

    public class PkgSizeObserver extends IPackageStatsObserver.Stub {
        /***
         * 回调函数，
         *
         * @param pStats    ,返回数据封装在PackageStats对象中
         * @param succeeded 代表回调成功
         */
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            // TODO Auto-generated method stub
            cachesize = pStats.cacheSize; //缓存大小
            MeSettingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //  desc.setText(formateFileSize(cachesize));
                }
            });

        }

    }
}
