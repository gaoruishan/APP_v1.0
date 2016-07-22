
package com.cmcc.hyapps.andyou.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.ImageLoaderManager;
import com.cmcc.hyapps.andyou.data.OfflinePackageManager;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHUser;
import com.cmcc.hyapps.andyou.model.Version;
import com.cmcc.hyapps.andyou.service.LocationService;
import com.cmcc.hyapps.andyou.service.MobileTravelToIMService;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.LocationUtil;
import com.cmcc.hyapps.andyou.util.LocationUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;
import com.cmcc.hyapps.andyou.widget.CommonDialog;
import com.cmcc.hyapps.andyou.widget.CommonDialog.OnDialogViewClickListener;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.push.FeedbackPush;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import test.grs.com.ims.IMApp;

/**
 * Created by kuloud on 14-8-16.
 */
public class TravelApp extends MultiDexApplication {
    public static final String TAG = "Travel";
    public static Context mContext;
    private static TravelApp instance;

    //记录当前所在位置
    private Location mCurrentLocation;

    @Override
    public void onCreate() {
        LogUtils.e("onCreate-start="+System.currentTimeMillis());
        super.onCreate();
        instance = this;
        //防止反编译
        if (getSignature(Const.PACKAGENAME) != Const.SIGNATRUE || android.os.Debug.isDebuggerConnected() || 0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
            Log.e("DEBUG", "程序被修改为可调试状态！！！");
//            android.os.Process.killProcess(android.os.Process.myPid());
        }
        QHCrashHandler crashHandler = QHCrashHandler.getInstance();
        // 注册crashHandler
        crashHandler.init(getApplicationContext());

        mContext = getApplicationContext();
        Intent service = new Intent(getApplicationContext(), LocationService.class);
        startService(service);

        FeedbackPush.getInstance(this).init(false);

        LocationUtil.getInstance(mContext);

        //初始化
        init();
        IMApp.getInstance().initToIM(mContext);//初始化IM
        MobileTravelToIMService.init(mContext);//开启服务
        // Are we using advanced debugging - locale?
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String p = pref.getString("set_locale", "");
        if (p != null && !p.equals("")) {
            Locale locale;
            // workaround due to region code
            if (p.startsWith("zh")) {
                locale = Locale.CHINA;
            } else {
                locale = new Locale(p);
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        LogUtils.e("onCreate-end="+System.currentTimeMillis());
    }

    public static Context getContext() {
        return mContext;
    }

    public static String getDataDir(Context context) {
        String state = Environment.getExternalStorageState();
        String dir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
//
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            // 获取sdcard的路径：外置和内置
            try {
                String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", new Class[0]).invoke(sm, new Object[0]);
                if (null != paths) {
                    for (int i = 0; i < paths.length; i++) {
                        if (TextUtils.isEmpty(paths[i]) == false) {
                            File file = new File(paths[i]);
                            if (file.canWrite()) {
                                return paths[i];
                            }
                        }
                    }
                }
                dir = context.getFilesDir().getAbsolutePath();
            } catch (Exception e) {
                Log.e(TAG, e);
            }
        }

        return dir;
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.recommand_bg)
                .showImageForEmptyUri(R.drawable.recommand_bg)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageOnFail(R.drawable.recommand_bg)
                .considerExifParams(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.MAX_PRIORITY).denyCacheImageMultipleSizesInMemory().defaultDisplayImageOptions(defaultOptions)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
//		.writeDebugLogs() // Remove for release app

                .diskCache(new UnlimitedDiscCache(new File(getDataDir(this) + "/TravelApp")))
                .memoryCache(new WeakMemoryCache())
                .threadPoolSize(4)
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void init() {
        //创建默认的ImageLoader配置参数
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
//                .createDefault(this);
//        //Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(configuration);

        initImageLoader();

        ServerAPI.switchServer(PreferencesUtils.getBoolean(this, ServerAPI.KEY_DEBUG));
        RequestManager.getInstance().init(this);
        ImageLoaderManager.getInstance().init(this);
        OfflinePackageManager.getInstance().init(this);

        // TODO open Umeng log, remove it when release.
        com.umeng.socialize.utils.Log.LOG = Const.UMENG_DEBUG;
        MobclickAgent.setDebugMode(Const.UMENG_DEBUG);
        MobclickAgent.setCatchUncaughtExceptions(true);

        UIStartUpHelper.executeWhenIdle(new Runnable() {


            @Override
            public void run() {
                tryUpdateUserInfoFromNet();
            }
        });

    }

    /**
     * 检查程序安装后classes.dex文件的Hash值, 最后一次打包时 修改R.string.crc
     *
     * @return
     */
    private boolean checkCRC() {
        boolean beModified = false;
        long crc = Long.parseLong(getString(R.string.crc));
        ZipFile zf;
        try {
            zf = new ZipFile(getApplicationContext().getPackageCodePath());
            ZipEntry ze = zf.getEntry("classes.dex");//
            Log.e("=" + crc + ",checkcrc=" + String.valueOf(ze.getCrc()), String.valueOf(ze.getCrc()));
            if (ze.getCrc() == crc) {
                beModified = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            beModified = false;
        }
        return beModified;
    }

    /**
     * 获取签名hash值
     *
     * @param packageName
     * @return
     */
    public int getSignature(String packageName) {
        PackageManager pm = this.getPackageManager();
        PackageInfo pi = null;
        int sig = 0;
        try {
            pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] s = pi.signatures;
            for (int i = 0; i < s.length; i++) {
                android.util.Log.e("=Signature:" + s[i].hashCode(), "TAG");
            }
            sig = s[0].hashCode();
        } catch (Exception e1) {
            sig = 0;
            e1.printStackTrace();
        }
        return sig;
    }

    private void tryUpdateUserInfoFromNet() {
        // if user info exist local, try to update once.
        if (AppUtils.getUser(getBaseContext()) != null) {
            String url = ServerAPI.BASE_URL + "users/current/";
            RequestManager.getInstance().sendGsonRequest(url, QHUser.class,
                    new Response.Listener<QHUser>() {

                        @Override
                        public void onResponse(QHUser user) {
                            Log.e("onResponse, User: " + user);
                            // Delete previous user avatar if need.
                            QHUser oldUserInfo = AppUtils.getQHUser(getBaseContext());

                            if (oldUserInfo != null && null != user.user_info.avatar_url
                                    && !oldUserInfo.user_info.avatar_url.equals(user.user_info.avatar_url)) {
                                AppUtils.setOldAvatarUrl(getBaseContext(), oldUserInfo.user_info.avatar_url);
                            }
                            AppUtils.saveUser(getBaseContext(), user);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(error, "onErrorResponse");
                        }
                    }, TAG);
        }
    }

    private void checkVersion() {
        RequestManager.getInstance().sendGsonRequest(
                ServerAPI.Version.buildUrl(getCurrentVersion()),
                Version.class,
                new Response.Listener<Version>() {

                    @Override
                    public void onResponse(Version version) {
                        Log.d("onResponse, Version: " + version);
                        try {
                            if (version != null
                                    && Integer.parseInt(version.latest) > getCurrentVersion()) {
                                showUpdateDialog(version.message);
                            }
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse when checking new version");
                    }
                }, TAG);
    }

    private int getCurrentVersion() {
        int versionCode = 0;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    private void showUpdateDialog(String contentText) {
        CommonDialog downloadDialog = new CommonDialog(getApplicationContext());
        downloadDialog.setTitleText(R.string.software_update);
        downloadDialog.getDialog().setCancelable(true);
        downloadDialog.getDialog().setCanceledOnTouchOutside(true);
        downloadDialog.setContentText(contentText);
        downloadDialog.setOnDialogViewClickListener(new OnDialogViewClickListener() {

            @Override
            public void onRightButtonClick() {
            }

            @Override
            public void onLeftButtonClick() {

            }
        });
        downloadDialog.showDialog();
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Location location) {
        mCurrentLocation = location;

        if (location != null && location.isValid()) {
            LocationUtils.setLastKnownLocation(getApplicationContext(), location);
        }
    }

    @Override
    public void onTerminate() {
        OfflinePackageManager.getInstance().destroy();
        MobileTravelToIMService.destory();
        super.onTerminate();
    }

    /**
     * @return the main context of the Application
     */
    public static Context getAppContext() {
        return instance;
    }

    /**
     * @return the main resources from the Application
     */
    public static Resources getAppResources() {
        return instance.getResources();
    }

    /**
     * 是否被劫持 5s执行
     */
    public static boolean stop;
    public static boolean isStop = true;

    public  static void checkIntercepted(final Activity activity) {
        if (isStop&&!stop){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (stop) {
                        stop = false;
                        return;
                    }
                    String topActivity = getTopActivity(activity);
                    if (topActivity.contains("com.tencent.open.agent")) return;
                    if (topActivity.contains("com.sina.weibo")) return;
                    if (topActivity.contains(".Launcher")) return;
                    if (topActivity.equals("com.cmcc.hyapps.andyou.activity.PasswordBackActivity"))
                        return;
                    if (topActivity.equals("com.cmcc.hyapps.andyou.activity.RegisterActivity"))
                        return;
                    if (topActivity.equals("com.cmcc.hyapps.andyou.activity.LoginActivity"))
                        return;
                    if (!topActivity.equals(activity.getClass().getName())) {
                        Toast.makeText(mContext, "此页面可能被劫持啦！", Toast.LENGTH_SHORT).show();
                    }
                    isStop=true;
                }
            }, 10 * 1000);
            isStop=false;
        }
    }

    /**
     * 获取栈顶activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Activity context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            android.util.Log.e("=get:" + (runningTaskInfos.get(0).topActivity).getClassName(), "TAG");
            return (runningTaskInfos.get(0).topActivity).getClassName();
        } else
            return null;
    }
}
