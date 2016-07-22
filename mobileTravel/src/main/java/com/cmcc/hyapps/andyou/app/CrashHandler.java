
package com.cmcc.hyapps.andyou.app;

import android.content.Context;
import android.os.Build;

import com.cmcc.hyapps.andyou.util.AppUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kuloud
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private Context mContext;
    private static CrashHandler INSTANCE = null;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 退出应用
     */
    private void endApplication() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    /*
     * 异常处理
     */
    public final static int CRASH_POP_INTERVAL = 1000 * 5;

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        endApplication();
        return true;
    }

    private String collectDeviceInfo(Context ctx, Throwable ex) {
        Map<String, String> infos = new HashMap<String, String>();
        infos.put("VERSIONNAME", String.valueOf(AppUtils.getVersionName(ctx)));
        infos.put("OSVERSION", String.valueOf(Build.VERSION.SDK_INT));
        Field[] fields = Build.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }

}
