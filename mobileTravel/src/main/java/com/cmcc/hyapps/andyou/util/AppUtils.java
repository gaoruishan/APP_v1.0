/**
 *
 */

package com.cmcc.hyapps.andyou.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.LoginActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.ErrorCode;
import com.cmcc.hyapps.andyou.app.TravelApp;
import com.cmcc.hyapps.andyou.data.ResponseError;
import com.cmcc.hyapps.andyou.model.QHFriendToken;
import com.cmcc.hyapps.andyou.model.QHToken;
import com.cmcc.hyapps.andyou.model.QHUser;
import com.cmcc.hyapps.andyou.model.TokenInfo;
import com.cmcc.hyapps.andyou.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.littlec.sdk.entity.CMGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import test.grs.com.ims.util.model.QHBlackList;

/**
 * @author kuloud
 */
public final class AppUtils {
    public static final String PATH_DIR_CAHCE_IMAGE = Environment.getDataDirectory()
            + File.separator + "image_cache";
    private static final String KEY_FIRST_LAUNCH = "key_first_launch";
    private static final String KEY_TOKEN_INFO = "key_token_info";
    private static final String KEY_OAUTH_INFO = "key_oauth_info";
    private static final String KEY_FRIEND_TOKEN_INFO = "key_friend_token_info";
    private static final String KEY_UID = "key_uid";
    private static final String KEY_USER = "key_user";
    private static final String KEY_QHUSER = "key_qhuser";
    private static final String KEY_OLD_AVATAR = "key_old_avatar";

    private static String sDeviceId = null;
    private static String sUUID = null;
    private static AppUtils mInstance = null;
    private static final String INSTALLATION = "INSTALLATION";
    private List<CMGroup> mGroups;

    /**
     *
     */
    public AppUtils() {
    }

    public static AppUtils getInstance() {
        if (mInstance == null) {
            mInstance = new AppUtils();
        }
        return mInstance;
    }

    public static String getAppSdRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + Const.SD_ROOT_PATH + File.separator;
    }

    public static String getCacheRootDir(Context context) {
        if (context != null) {
            return context.getCacheDir() + File.separator + "image_cache";
        } else {
            return PATH_DIR_CAHCE_IMAGE;
        }
    }

    public static boolean firstLaunch(Context context) {
        return PreferencesUtils.getInt(context, KEY_FIRST_LAUNCH) != getVersionCode(context);
    }

    public static void setFirstLaunch(Context context) {
        PreferencesUtils.putInt(context, KEY_FIRST_LAUNCH, getVersionCode(context));
    }

    public static void saveTokenInfo(Context context, TokenInfo tokenInfo) {
        PreferencesUtils.putEncryptString(context, KEY_TOKEN_INFO, new Gson().toJson(tokenInfo));
    }

    public static TokenInfo getTokenInfo(Context context) {
        String json = PreferencesUtils.getEncryptString(context, KEY_TOKEN_INFO);
        TokenInfo info = null;
        try {
            info = new Gson().fromJson(json, TokenInfo.class);
        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return info;
    }

    public static void saveQHToken(Context context, QHToken tokenInfo) {
        PreferencesUtils.putEncryptString(context, KEY_TOKEN_INFO, new Gson().toJson(tokenInfo));
    }

    public static void saveQHToken(Context context, String tokenInfo) {
        PreferencesUtils.putEncryptString(context, KEY_TOKEN_INFO, tokenInfo);
    }

    public static void saveOauth(Context context, String oauth){
        PreferencesUtils.putString(context, KEY_OAUTH_INFO, oauth);
    }

    public static String getOauth(Context context) {
         return PreferencesUtils.getString(context, KEY_OAUTH_INFO);
    }

    public static void clearOauth(Context context) {
         PreferencesUtils.clear(context,KEY_OAUTH_INFO);
    }

    public static QHToken getQHToken(Context context) {
        String json = PreferencesUtils.getEncryptString(context, KEY_TOKEN_INFO);
        QHToken info = null;
        try {
            info = new Gson().fromJson(json, QHToken.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static String mjsessionid;
    public static String dynamicKey;
    public static void saveFriendToken(Context context, String tokenInfo, String jsessionid) {
        mjsessionid = jsessionid;
//        PreferencesUtils.putEncryptString(context, KEY_FRIEND_TOKEN_INFO, tokenInfo);
    }
    public static void saveDynamicKey(String dynamicKey) {
        AppUtils.dynamicKey = dynamicKey;
    }


    public static QHFriendToken getFriendToken(Context context) {
        String json = PreferencesUtils.getEncryptString(context, KEY_FRIEND_TOKEN_INFO);
        QHFriendToken info = null;
        try {
            info = new Gson().fromJson(json, QHFriendToken.class);
        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (info == null) {
            info = new QHFriendToken();
        }
        return info;
    }

    public OnSetBackSuceessListener mListener;

    public interface OnSetBackSuceessListener {
        void onBlackListSuceess(QHBlackList list);
        void onGroupListSuceess(List<CMGroup> groups);
    }

    //用于回调
    public void setOnRecivedMessageListener(OnSetBackSuceessListener mListener) {
        this.mListener = mListener;
        if (mGroups!=null&&mGroups.size()>0){
            mListener.onGroupListSuceess(mGroups);
        }
    }

    public  void setQHBlackLists(QHBlackList lists) {
        if (lists == null) {
            lists = new QHBlackList();
        }
        if (mListener!=null){
            mListener.onBlackListSuceess(lists);
        }
    }
    public  void setGroupLists(List<CMGroup> groups) {
        if (groups == null) {
            groups = new ArrayList<CMGroup>();
        }
        mGroups  = groups;
        if (mListener!=null){
            mListener.onGroupListSuceess(groups);
        }
    }

    public static String getUid(Context context) {
        return PreferencesUtils.getString(context, KEY_UID);
    }


    public static int getVersionCode(Context context) {
        PackageInfo pinfo = null;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            Log.e(e.getMessage());
        }
        int versionNumber = (pinfo != null) ? pinfo.versionCode : 0;
        return versionNumber;
    }

    public static String getVersionName(Context context) {
        PackageInfo pinfo = null;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            Log.e(e.getMessage());
        }
        String versionName = pinfo != null ? pinfo.versionName : null;
        return versionName;
    }

    public static String getBuild() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = TravelApp.getAppContext().getPackageManager()
                    .getApplicationInfo(TravelApp.getAppContext().getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String buildVersion_ip = appInfo.metaData.getString("BuildVersion");
        if (buildVersion_ip == null) {
            buildVersion_ip = "1000";
        }
        return buildVersion_ip;
    }

    public static String getBuildVersion() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = TravelApp.getAppContext().getPackageManager()
                    .getApplicationInfo(TravelApp.getAppContext().getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = appInfo.metaData.getInt("BuildVersion", 1000);
        return version + "";
    }

    public static Context getContext() {
        return TravelApp.getContext();
    }

    // public static int getUid(Context context) {
    // ApplicationInfo apppInfo = null;
    // try {
    // apppInfo =
    // context.getPackageManager().getApplicationInfo(context.getPackageName(),
    // PackageManager.GET_META_DATA);
    // return apppInfo.uid;
    // } catch (NameNotFoundException e) {
    // Log.e(e.getMessage());
    // }
    // return 0;
    // }

    /**
     * Algorithm for device ID
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        if (sDeviceId != null) {
            return sDeviceId;
        }
        if (context == null) {
            return null;
        }
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        String androidId = android.provider.Settings.System.getString(context.getContentResolver(),
                "android_id");
        String serialNo = getDeviceSerial();

        sDeviceId = MD5.getMD5(imei + androidId + serialNo);
        return sDeviceId;
    }

    private static String getDeviceSerial() {
        String serical = "";
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Method get = cls.getMethod("get", String.class);
            serical = (String) get.invoke(cls, "ro.serialno");
        } catch (Exception e) {
        }
        return serical;
    }

    public static String buildClientInfo(Context context) {
        // TODO collect client info
        return "test";
    }

    public synchronized static String uuid(Context context) {
        if (sUUID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sUUID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sUUID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    public static void saveUser(Context context, QHUser user) {
        PreferencesUtils.putEncryptString(context, KEY_QHUSER, new Gson().toJson(user));
//        PreferencesUtils.putString(context, KEY_UID, String.valueOf(user.uid));

    }

    public static User getUser(Context context) {
        String userJson = PreferencesUtils.getEncryptString(context, KEY_USER);
        if (!TextUtils.isEmpty(userJson)) {
            try {
                return new Gson().fromJson(userJson, User.class);
            } catch (JsonSyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static QHUser getQHUser(Context context) {
        String userJson = PreferencesUtils.getEncryptString(context, KEY_QHUSER);
        if (!TextUtils.isEmpty(userJson)) {
            try {
                return new Gson().fromJson(userJson, QHUser.class);
            } catch (JsonSyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void clearUser(Context context) {
        PreferencesUtils.clear(context, KEY_USER);
    }

    public static void clearQHUser(Context context) {
        PreferencesUtils.clear(context, KEY_QHUSER);
    }

    public static void clearToken(Context context) {
        PreferencesUtils.clear(context, KEY_TOKEN_INFO);
    }

    public static void clearQHToken(Context context) {
        PreferencesUtils.clear(context, KEY_TOKEN_INFO);
    }

    public static void handleResponseError(Activity activity, VolleyError error) {
        if (error instanceof ResponseError) {
            ResponseError responseError = (ResponseError) error;
            if (responseError.errCode != ServerAPI.ErrorCode.ERROR_NOT_LOGIN) {
                switch (responseError.errCode) {
                    case ErrorCode.ERROR_PARAMS:
                        if (!TextUtils.isEmpty(responseError.message)) {
                            ToastUtils.show(activity.getBaseContext(), responseError.message);
                            return;
                        }
                        break;

                    default:
                        break;
                }
            } else {
                Intent login = new Intent(activity, LoginActivity.class);
                activity.startActivityForResult(login, Const.REQUEST_CODE_LOGIN);
                return;
            }
        } else {
            if (error instanceof NoConnectionError) {
                ToastUtils.show(activity, R.string.network_unavailable);
            } else if (error instanceof TimeoutError) {
                ToastUtils.show(activity, R.string.network_timeout);
            } else ToastUtils.show(activity, R.string.error_unknown);
        }
        //    ToastUtils.show(activity.getBaseContext(), R.string.error_unknown);
    }

    public static int randomUUID() {
        int uuid = UUID.randomUUID().hashCode();
        if (uuid == Integer.MIN_VALUE) {
            uuid++;
        }
        return Math.abs(uuid);
    }

    public static String getVistorId(Context context) {
        String mac = getLocalMacAddress(context);
        if (TextUtils.isEmpty(mac)) {
            mac = "000000";
        } else {
            mac = mac.replaceAll(":", "");
            if (mac.length() < 6) {
                mac = "000000";
            } else {
                mac = mac.substring(mac.length() - 6);
            }
        }
        String id = "访客_" + mac;
        return id;
    }

    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * @param avatarUrl
     */
    public static void setOldAvatarUrl(Context context, String avatarUrl) {
        PreferencesUtils.putString(context, KEY_OLD_AVATAR, avatarUrl);
    }

    public static String getOldAvatarUrl(Context context) {
        return PreferencesUtils.getString(context, KEY_OLD_AVATAR);
    }

    /**
     * 个别字体设置颜色
     * @param textView
     * @param string
     * @param context
     */
    public static void setAreaTextColor(TextView textView,SparseIntArray sparseIntArray,String string,Context context){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
        for (int i = 0; i< sparseIntArray.size(); i++){
            int start = sparseIntArray.keyAt(i);
            int end = sparseIntArray.valueAt(i);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.market_actionbar)),start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
            textView.setText(spannableStringBuilder);
    }
}
