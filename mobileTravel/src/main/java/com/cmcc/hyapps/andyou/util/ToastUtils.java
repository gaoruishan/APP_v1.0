
package com.cmcc.hyapps.andyou.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * ToastUtils
 * 
 * @author kuloud
 */
public class ToastUtils {
    private static Toast mToast;
    private static Handler handler = new Handler();
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mToast != null)
                mToast.cancel();
            mToast = null;
        }
    };
    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, CharSequence text, int duration) {
        Toast.makeText(context, text, duration).show();
    }

    public static void show(Context context, int resId, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args),
                Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), duration);
    }

    public static void show(Context context, String format, int duration, Object... args) {
        show(context, String.format(format, args), duration);
    }

    public static void AvoidRepeatToastShow(Context context,int resId,int duration){
        if (context == null)
            return;
        handler.removeCallbacks(runnable);
        if (mToast == null)
            mToast = Toast.makeText(context,context.getResources().getString(resId),duration);
        handler.postDelayed(runnable,1000);
        mToast.show();
    }
}
