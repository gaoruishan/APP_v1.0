/**
 * 
 */

package com.cmcc.hyapps.andyou.task;

import android.os.AsyncTask;

import com.cmcc.hyapps.andyou.util.TaskUtils;

/**
 * @author kuloud
 */
public abstract class BaseTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {
    protected TaskListener<Result> callback;

    @Override
    protected Result doInBackground(Params... params) {
        return null;
    }

    public void exe(Params... params) {
        executeOnExecutor(TaskUtils.DUAL_THREAD_EXECUTOR, params);
    }

    public void exe(TaskListener<Result> listener, Params... params) {
        executeOnExecutor(TaskUtils.DUAL_THREAD_EXECUTOR, params);
        callback = listener;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (callback != null) {
            callback.onResult(result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (callback != null) {
            callback.onCancel(null);
        }
    }

    @Override
    protected void onCancelled(Result result) {
        super.onCancelled(result);
        if (callback != null) {
            callback.onCancel(result);
        }
    }
}
