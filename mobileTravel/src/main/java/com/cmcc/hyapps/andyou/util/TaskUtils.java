/**
 * 
 */

package com.cmcc.hyapps.andyou.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kuloud
 */
public final class TaskUtils {

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    public static final Executor DUAL_THREAD_EXECUTOR = Executors.newFixedThreadPool(10,
            sThreadFactory);

}
