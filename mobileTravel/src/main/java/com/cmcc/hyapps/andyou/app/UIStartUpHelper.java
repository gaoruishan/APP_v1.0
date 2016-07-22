/**
 * 
 */

package com.cmcc.hyapps.andyou.app;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;

/**
 * Splash 欢迎页帮助类
 * @author Kuloud
 */
public class UIStartUpHelper {
    private static final String TAG = "UIStartUpHelper";

    private static boolean sSplashScreenTasksExecuted = false;

    private static UIStartUpHelper mInstance = null;

    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private UIStartUpHelper() {
    }

    public static UIStartUpHelper getInstance() {
        if (mInstance == null) {
            mInstance = new UIStartUpHelper();
        }
        return mInstance;
    }

    public static void executeOnSplashScreen() {
        if (sSplashScreenTasksExecuted) {
            return;
        }
        sSplashScreenTasksExecuted = true;

        final UIStartUpHelper instance = UIStartUpHelper.getInstance();
        instance.executeSplashScreenMainThreadTasks();
        instance.executeSplashScreenAsyncTasks();
    }

    /**
     * Handle main thread tasks
     */
    private void executeSplashScreenMainThreadTasks() {
        // TODO
    }

    /**
     * Handle async tasks
     */
    private void executeSplashScreenAsyncTasks() {

        new Thread() {
            @Override
            public void run() {
                // TODO
            };
        }.start();
    }

    public final static void executeWhenIdle(final Runnable r) {
        MessageQueue mq = Looper.myQueue();
        mq.addIdleHandler(new IdleHandler() {

            @Override
            public boolean queueIdle() {
                r.run();
                return false;
            }
        });
    }
}
