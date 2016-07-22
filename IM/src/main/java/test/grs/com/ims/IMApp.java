package test.grs.com.ims;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.littlec.sdk.CMChatConfig;
import com.littlec.sdk.manager.CMIMHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

import test.grs.com.ims.message.BackgroundService;
import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.IMSharedPreferences;

/**
 * Created by gaoruishan on 15/9/25.
 */

public class IMApp extends Application {
    private static IMApp mAppInstance;
    public static Context mContext;
    private static DbUtils dbUtils;
    public static boolean isBackGroundServiceRunning = false;
    public static String currentUserName = "";
    public static String currentUserPsw = "";
    public static String currentUserAvataUrl="";
    public static String currentUserNick="";

    public synchronized static IMApp getInstance() {
        if (mAppInstance == null) {
            mAppInstance = new IMApp();
        }
        return mAppInstance;
    }


    public static void initToIM(Context context) {
        mContext = context;
//        mAppInstance = (IMApp) context;
        //初始化  888877VV   139948en
        CMIMHelper.getCmAccountManager().init(context, IMConst.APPKEY);
        initImageLoader();
//        NetworkStateReceiver.registerNetworkStateReceiver(this);
      String string=  CMChatConfig.ServerConfig.getIM_HOST()+","+CMChatConfig.ServerConfig.getServerConfigAddress()+","+CMChatConfig.ServerConfig.xmppServiceName+","+CMChatConfig.ServerConfig.getAdapterHost();
        Log.e("==initToIM",string+",");
    }

    public static void initImageLoader() {
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(mContext);
        ImageLoader.getInstance().init(configuration);

    }

    public synchronized static DbUtils geDbUtils() {
        //创建数据库
        if (dbUtils == null) {
            File file;
            if (currentUserName != null) {

                file = new File(IMConst.GLOBALSTORAGE_DB_PATH);
            } else {
                file = new File(IMConst.GLOBALSTORAGE_DB_DEFAULT_PATH);
            }
            try {
                if(!file.exists()){//如果不存在,创建一个;
                    file.mkdirs();
                    //这里不注释掉以前，三星机器会android java.io.ioexceptionopen failed: ENOTDIR
//                    file.createNewFile();
                }
                DbUtils.DaoConfig config = new DbUtils.DaoConfig(mContext);
                config.setDbDir(file.getPath());
                config.setDbName(currentUserName + "_" + IMConst.APPKEY); //db名
                config.setDbVersion(1);  //db版本
                dbUtils = DbUtils.create(config);//db还有其他的一些构造方法，比如含有更新表版本的监听器的
//                dbUtils = DbUtils.create(mContext, file.getPath(), currentUserName + "_" + IMConst.APPKEY);
            } catch (Exception e) {
                Log.e("==geDbUtils", ":" + e.toString());
            }
        }
        return dbUtils;
    }

    public void setCurrentUserName(String userName) {
        this.currentUserName = userName;
    }

    public String getCurrentUserName() {
        return this.currentUserName;
    }

    public static void setCurrentAvataUrl(String avataUrl) {
       currentUserAvataUrl = avataUrl;
        if (avataUrl!=null&&!avataUrl.isEmpty()){
            IMSharedPreferences.putString(
                    IMSharedPreferences.AVATARURL, avataUrl);
        }
    }

    public static String getCurrentAvataUrl() {
        if (currentUserAvataUrl==null||currentUserAvataUrl.isEmpty()||currentUserAvataUrl.equals(" ")){
            currentUserAvataUrl = IMSharedPreferences.getString(
                    IMSharedPreferences.AVATARURL, "");
        }
        return currentUserAvataUrl;
    }

    public static void setCurrentUserNick(String avataUrl) {
        currentUserNick = avataUrl;
        Log.e("==setCurrentUserNick",currentUserNick+"");
        if (avataUrl!=null&&!avataUrl.isEmpty()){
            IMSharedPreferences.putString(
                    IMSharedPreferences.NICKNAME, avataUrl);
        }
    }

    public static String getCurrentUserNick() {
        if (currentUserNick==null||currentUserNick.isEmpty()||currentUserNick.equals(" ")){
            currentUserNick = IMSharedPreferences.getString(
                    IMSharedPreferences.NICKNAME, "");
        }
        Log.e("==getCurrentUserNick",currentUserNick+"");
        return currentUserNick;
    }

    public void doLogin(String userName, String passWord) {
        //取保存的数据
        if (passWord == null || userName == null) {
            userName = IMSharedPreferences.getString(
                    IMSharedPreferences.ACCOUNT, null);
            passWord = IMSharedPreferences.getString(
                    IMSharedPreferences.PASSWORD, null);
        }
        Intent serviceIntent = new Intent(mContext, BackgroundService.class);
        serviceIntent.putExtra("userName", userName.toLowerCase());
        serviceIntent.putExtra("passWord", passWord);
        Log.e("doLogin", userName + "==" + passWord);
        if (userName != null) {
            IMApp.currentUserName = userName;
            IMApp.currentUserPsw = passWord;
            dbUtils = geDbUtils();
            mContext.startService(serviceIntent);
        } else {
            Toast.makeText(mContext, "请检查输入的内容", Toast.LENGTH_SHORT).show();
        }
    }

    public void RedoLogin(){
        //和服务器断开连接,重新登录,
//        BackgroundService.mIsLoginSuccess = false;
     if (IMApp.currentUserName!=null&&!IMApp.currentUserName.equals("")&&IMApp.currentUserPsw!=null&&!IMApp.currentUserPsw.equals("")){
         doLogin(IMApp.currentUserName,IMApp.currentUserPsw);
     }else {
         String userName = IMSharedPreferences.getString(
                 IMSharedPreferences.ACCOUNT, null);
         String passWord = IMSharedPreferences.getString(
                 IMSharedPreferences.PASSWORD, null);
         if (userName!=null&&passWord!=null){
             doLogin(userName,passWord);
         }
     }

    }
    public void doRegister(){
        //退出登录,和服务器断开连接
        CMIMHelper.getCmAccountManager().doLogOut();
        IMSharedPreferences.deleteString(IMSharedPreferences.ACCOUNT);
        mContext.stopService(new Intent(mContext, BackgroundService.class));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("==IM onTerminate","stopService");
        doRegister();//注销
    }
}
