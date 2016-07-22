
package com.cmcc.hyapps.andyou.app;

import static com.cmcc.hyapps.andyou.app.DownloadThread.Configs.EVENT;
import static com.cmcc.hyapps.andyou.app.DownloadThread.Configs.IS_PAUSED;
import static com.cmcc.hyapps.andyou.app.DownloadThread.Configs.PROCESS_PROGRESS;
import static com.cmcc.hyapps.andyou.app.DownloadThread.Configs.PROCESS_SPEED;
import static com.cmcc.hyapps.andyou.app.DownloadThread.Configs.URL;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.download.DownloadService.Event;
import com.cmcc.hyapps.andyou.task.DownloadTask;
import com.cmcc.hyapps.andyou.task.DownloadTask.DownloadTaskListener;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.util.NetUtils;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author kuloud
 */
public class DownloadThread extends Thread {
    public static final String ACTION_DOWNLOAD = "com.cmcc.hyapps.andyou.ACTION_DOWNLOAD";
    public static final String ACTION_EVENT_NOTIFY = "com.cmcc.hyapps.andyou.ACTION_EVENT_NOTIFY";

    private static final int MAX_TASK_COUNT = 100;
    private static final int MAX_DOWNLOAD_THREAD_COUNT = 3;

    private Context mContext;

    private TaskQueue mTaskQueue;
    private List<DownloadTask> mDownloadingTasks;
    private List<DownloadTask> mPausingTasks;

    private Boolean isRunning = false;

    public DownloadThread(Context context) {

        mContext = context;
        mTaskQueue = new TaskQueue();
        mDownloadingTasks = new ArrayList<DownloadTask>();
        mPausingTasks = new ArrayList<DownloadTask>();
    }

    public void startManage() {
        isRunning = true;
        this.start();
        checkUncompleteTasks();
    }

    public void close() {
        isRunning = false;
        pauseAllTask();
        this.stop();
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {

        super.run();
        while (isRunning) {
            DownloadTask task = mTaskQueue.poll();
            mDownloadingTasks.add(task);
            task.execute();
        }
    }

    public void addTask(String url) {

        if (!FileUtils.isSDCardPresent()) {
            ToastUtils.show(mContext, R.string.error_no_sdcard);
            return;
        }

        if (!FileUtils.isSdCardWrittenable()) {
            ToastUtils.show(mContext, R.string.error_sdcard_not_rw);
            return;
        }

        if (getTotalTaskCount() >= MAX_TASK_COUNT) {
            ToastUtils.show(mContext, R.string.error_download_reach_max);
            return;
        }

        try {
            addTask(newDownloadTask(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void addTask(DownloadTask task) {
        broadcastAddTask(task.getUrl());

        mTaskQueue.offer(task);

        if (!this.isAlive()) {
            this.startManage();
        }
    }

    private void broadcastAddTask(String url) {

        broadcastAddTask(url, false);
    }

    private void broadcastAddTask(String url, boolean isInterrupt) {
        Intent nofityIntent = new Intent(ACTION_DOWNLOAD);
        nofityIntent.putExtra(EVENT, Event.ADD);
        nofityIntent.putExtra(URL, url);
        nofityIntent.putExtra(IS_PAUSED, isInterrupt);
        mContext.sendBroadcast(nofityIntent);
    }

    public void reBroadcastAddAllTask() {
        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            broadcastAddTask(task.getUrl(), task.isInterrupt());
        }
        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            broadcastAddTask(task.getUrl());
        }
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            broadcastAddTask(task.getUrl());
        }
    }

    public boolean hasTask(String url) {
        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task.getUrl().equals(url)) {
                return true;
            }
        }
        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
        }
        return false;
    }

    public DownloadTask getTask(int position) {
        if (position >= mDownloadingTasks.size()) {
            return mTaskQueue.get(position - mDownloadingTasks.size());
        } else {
            return mDownloadingTasks.get(position);
        }
    }

    public int getQueueTaskCount() {

        return mTaskQueue.size();
    }

    public int getDownloadingTaskCount() {
        return mDownloadingTasks.size();
    }

    public int getPausingTaskCount() {
        return mPausingTasks.size();
    }

    public int getTotalTaskCount() {
        return getQueueTaskCount() + getDownloadingTaskCount() + getPausingTaskCount();
    }

    public void checkUncompleteTasks() {
        List<String> urlList = Configs.getURLArray(mContext);
        if (urlList.size() >= 0) {
            for (int i = 0; i < urlList.size(); i++) {
                addTask(urlList.get(i));
            }
        }
    }

    public synchronized void pauseTask(String url) {
        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
                pauseTask(task);
            }
        }
    }

    public synchronized void pauseAllTask() {
        DownloadTask task;

        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            mTaskQueue.remove(task);
            mPausingTasks.add(task);
        }

        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null) {
                pauseTask(task);
            }
        }
    }

    public synchronized void deleteTask(String url) {
        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
                File file = new File(FileUtils.FILE_ROOT
                        + NetUtils.getFileNameFromUrl(task.getUrl()));
                if (file.exists())
                    file.delete();

                task.onCancelled();
                completeTask(task);
                return;
            }
        }
        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            if (task != null && task.getUrl().equals(url)) {
                mTaskQueue.remove(task);
            }
        }
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
                mPausingTasks.remove(task);
            }
        }
    }

    public synchronized void continueTask(String url) {
        DownloadTask task;
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
                continueTask(task);
            }

        }
    }

    public synchronized void pauseTask(DownloadTask task) {
        if (task != null) {
            task.onCancelled();

            // move to pausing list
            String url = task.getUrl();
            try {
                mDownloadingTasks.remove(task);
                task = newDownloadTask(url);
                mPausingTasks.add(task);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void continueTask(DownloadTask task) {

        if (task != null) {
            mPausingTasks.remove(task);
            mTaskQueue.offer(task);
        }
    }

    public synchronized void completeTask(DownloadTask task) {
        if (mDownloadingTasks.contains(task)) {
            Configs.clearURL(mContext, mDownloadingTasks.indexOf(task));
            mDownloadingTasks.remove(task);

            // notify list changed
            Intent nofityIntent = new Intent(ACTION_EVENT_NOTIFY);
            nofityIntent.putExtra(EVENT, Event.COMPLETE);
            nofityIntent.putExtra(URL, task.getUrl());
            mContext.sendBroadcast(nofityIntent);
        }
    }

    /**
     * Create a new download task with default config
     * 
     * @param url
     * @return
     * @throws MalformedURLException
     */
    private DownloadTask newDownloadTask(String url) throws MalformedURLException {

        DownloadTaskListener taskListener = new DownloadTaskListener() {

            @Override
            public void updateProcess(DownloadTask task) {

                Intent updateIntent = new Intent(ACTION_EVENT_NOTIFY);
                updateIntent.putExtra(EVENT, Event.PROCESS);
                updateIntent.putExtra(PROCESS_SPEED, task.getDownloadSpeed() + "kbps | "
                        + task.getDownloadSize() + " / " + task.getTotalSize());
                updateIntent.putExtra(PROCESS_PROGRESS, task.getDownloadPercent());
                updateIntent.putExtra(URL, task.getUrl());
                mContext.sendBroadcast(updateIntent);
            }

            @Override
            public void preDownload(DownloadTask task) {
                Configs.storeURL(mContext, mDownloadingTasks.indexOf(task), task.getUrl());
            }

            @Override
            public void finishDownload(DownloadTask task) {
                completeTask(task);
            }

            @Override
            public void errorDownload(DownloadTask task, Throwable error) {
                if (error != null) {
                    Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }

            }
        };
        return new DownloadTask(mContext, url, FileUtils.FILE_ROOT, taskListener);
    }

    /**
     * A obstructed task queue
     */
    private class TaskQueue {
        private Queue<DownloadTask> taskQueue;

        public TaskQueue() {

            taskQueue = new LinkedList<DownloadTask>();
        }

        public void offer(DownloadTask task) {

            taskQueue.offer(task);
        }

        public DownloadTask poll() {
            DownloadTask task = null;
            while (mDownloadingTasks.size() >= MAX_DOWNLOAD_THREAD_COUNT
                    || (task = taskQueue.poll()) == null) {
                try {
                    Thread.sleep(1000); // sleep
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return task;
        }

        public DownloadTask get(int position) {
            if (position >= size()) {
                return null;
            }
            return ((LinkedList<DownloadTask>) taskQueue).get(position);
        }

        public int size() {
            return taskQueue.size();
        }

        @SuppressWarnings("unused")
        public boolean remove(int position) {
            return taskQueue.remove(get(position));
        }

        public boolean remove(DownloadTask task) {

            return taskQueue.remove(task);
        }
    }

    public static class Configs {

        public static final String EVENT = "event";
        public static final String PROCESS_SPEED = "process_speed";
        public static final String PROCESS_PROGRESS = "process_progress";
        public static final String URL = "url";
        public static final String ERROR_CODE = "error_code";
        public static final String ERROR_INFO = "error_info";
        public static final String IS_PAUSED = "is_paused";

        public static final String PREFERENCE_NAME = "com.cmcc.hyapps.andyou.download";

        public static final int URL_COUNT = 3;
        public static final String KEY_URL = "url";

        public static void storeURL(Context context, int index, String url) {
            PreferencesUtils.putString(context, KEY_URL + index, url);
        }

        public static void clearURL(Context context, int index) {
            PreferencesUtils.putString(context, KEY_URL + index, "");
        }

        public static String getURL(Context context, int index) {
            return PreferencesUtils.getString(context, KEY_URL + index);
        }

        public static List<String> getURLArray(Context context) {
            List<String> urlList = new ArrayList<String>();
            for (int i = 0; i < URL_COUNT; i++) {
                if (!TextUtils.isEmpty(getURL(context, i))) {
                    urlList.add(PreferencesUtils.getString(context, KEY_URL + i));
                }
            }
            return urlList;
        }

    }
}
