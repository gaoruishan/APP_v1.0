package com.cmcc.hyapps.andyou.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.*;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.download.DownloadManager;
import com.cmcc.hyapps.andyou.model.QHApk;
import com.cmcc.hyapps.andyou.update.UpdateDownLoadManager;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/6/17.
 */
public class CheckUpdateUtil implements UpdateDownLoadManager.OnDownLoadListener {
    private final int UPDATA_NONEED = 0;

    private final int UPDATA_CLIENT = 1;

    private final int GET_UNDATAINFO_ERROR = 2;

    private final int SDCARD_NOMOUNTED = 3;

    private final int DOWN_ERROR = 4;

    private boolean isShow = true;

    public static String mRequestTag = CheckUpdateUtil.class.getName();

    Handler handler = new Handler() {

        @Override

        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub

            super.handleMessage(msg);

            switch (msg.what) {

                case UPDATA_NONEED:
                    if(isShow){
                        Toast.makeText(context, "当前是最新版本，不需要更新",

                                Toast.LENGTH_SHORT).show();
                    }

                    break;

                case UPDATA_CLIENT:

                    //对话框通知用户升级程序

                    showUpdataDialog();

                    break;

                case GET_UNDATAINFO_ERROR:

                    //服务器超时

                    Toast.makeText(context, "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();

                    break;

                case DOWN_ERROR:

                    //下载apk失败

                    Toast.makeText(context, "下载新版本失败", Toast.LENGTH_SHORT).show();

                    break;

            }

        }

    };

    private Context context;
    private static CheckUpdateUtil instance = new CheckUpdateUtil();
    private static Activity mActivity;
    private QHApk apkinfo;
    private ProgressDialog pd;

    private CheckUpdateUtil(){
        context = AppUtils.getContext();
    }

    public static CheckUpdateUtil getInstance(Activity activity){
        mActivity = activity;
        UpdateDownLoadManager.getInstance().setOnDownLoadListener(instance);
        return instance;
    }

    /*
    * 获取当前程序的版本号
    */
    private String getVersionName() throws Exception{
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
    }
    /*
     * 获取服务器端版本号
     */
    public void getUpdataInfo( final boolean b) throws Exception{
        isShow = b;
        if(pd!=null){
            pd.show();
        }else {
            String url = ServerAPI.QHApkList.URL;
            RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url,
                    QHApk.QHApkList.class, null,
                    new Response.Listener<QHApk.QHApkList>() {
                        @Override
                        public void onResponse(QHApk.QHApkList response) {
                            //比较版本号是否相同
                            if (response.results != null && response.results.size() == 0 && b){
                                ToastUtils.show(mActivity,"当前是最新版本");
                                return;
                            }
                            if(response.results.size()!=0){
                                apkinfo = response.results.get(0);
                                PreferencesUtils.putString(context,"APK_URL",apkinfo.apk_file);
                                checkVersion(apkinfo);
                            }
//                        Log.d("onResponse, ScenicDetails=%s,mAdapter.size=%d", response, mAdapter.getDataItems().size());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            android.util.Log.i("law",error.toString());
                        }
                    }, false, mRequestTag);
        }

    }
    public void getUpdataInfo() throws Exception{
        getUpdataInfo(true);

    }

    private void checkVersion(QHApk qhApk) {
        String localVersion = null;
        try {
            localVersion = getVersionName();
            int i1 = localVersion.compareTo(qhApk.version);
            if(i1<0){
                Message msg = new Message();
                msg.what = UPDATA_CLIENT;
                handler.sendMessage(msg);
            }else {
                Message msg = new Message();
                msg.what = UPDATA_NONEED;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showUpdataDialog() {

        AlertDialog.Builder builer = new AlertDialog.Builder(mActivity);

        builer.setTitle("版本升级");

        builer.setMessage("检测到最新版本，请及时更新！");
        if(apkinfo.force == 1) {
            builer.setMessage("版本重要变更，请在网络畅通时，更新后使用");
            builer.setCancelable(false);
        }

        //当点确定按钮时从服务器上下载 新的apk 然后安装

        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                downLoadApk();
            }
        });


            builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    //do sth
                    dialog.dismiss();
                    if (apkinfo.force == 1)
                        System.exit(0);
                }
            });

        AlertDialog dialog = builer.create();


        dialog.show();

        if(apkinfo.force == 1) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

//            final Button nagativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
//            nagativeButton.setEnabled(false);//不可点击
        }

    }

    protected void downLoadApk() {

        UpdateDownLoadManager.getInstance().setIsDownload(true);

        pd = new  ProgressDialog(mActivity);

        pd.setCancelable(false);

        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        if(apkinfo.force == 0) {
            pd.setButton(DialogInterface.BUTTON_POSITIVE, "后台更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        pd.setButton(DialogInterface.BUTTON_NEGATIVE,"取消更新",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UpdateDownLoadManager.getInstance().setIsDownload(false);
                pd = null;
                File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");
                if (file.exists())
                    file.delete();
                if(apkinfo.force == 1)
                    System.exit(0);
            }
        });

        pd.setMessage("正在下载更新");

        pd.show();

//        if (apkinfo.force == 1) {
//            final Button button = pd.getButton(DialogInterface.BUTTON_NEGATIVE);
//            button.setEnabled(false);
////            button.setClickable(false);
//        }
        new Thread(){

            @Override

            public void run() {

                try {

                    File file = UpdateDownLoadManager.getInstance().getFileFromServer(apkinfo.apk_file, pd);

                    sleep(1000);
//                    if (UpdateDownLoadManager.isDownload){
//                        installApk(file);
//                    }
                    if(pd != null && pd.isShowing()) {
                        pd.dismiss(); //结束掉进度条对话框
                    }

                } catch (Exception e) {

                    Message msg = new Message();

                    msg.what = DOWN_ERROR;

                    handler.sendMessage(msg);

                    e.printStackTrace();

                }

            }}.start();

    }
    protected void installApk(File file) {

        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

//        Uri uri = Uri.fromFile(new File(fileName));
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "application/vnd.android.package-archive");
//        startActivity(intent);

    }

    @Override
    public void downLoadCancle() {

    }

    @Override
    public void downLoadSuccess(File file) {
        if (file.exists())
          installApk(file);
    }
}
