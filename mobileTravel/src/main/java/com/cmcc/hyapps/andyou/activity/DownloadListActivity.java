/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.DownloadListAdapter;
import com.cmcc.hyapps.andyou.app.DownloadThread;
import com.cmcc.hyapps.andyou.app.DownloadThread.Configs;
import com.cmcc.hyapps.andyou.app.ServerAPI.OfflinePackages;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.download.DownloadManager;
import com.cmcc.hyapps.andyou.download.DownloadService.Event;
import com.cmcc.hyapps.andyou.download.Downloads;
import com.cmcc.hyapps.andyou.model.OfflinePackage.OfflinePackageList;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.ActionBar;

/**
 * @author kuloud
 */
public class DownloadListActivity extends BaseActivity implements LoaderCallbacks<Cursor> {
    private RecyclerView mRecyclerView;
    private View mEmptyHintView;
    private View mLoadingProgress;
    private DownloadListAdapter mAdapter;
    private static final int LOADER_DOWNLOAD_LIST = 1;
    private Handler mActionHandler;
    private DownloadManager mDownloadManager;
    private ContentObserver mContentObserver;
    // TODO
    private Callback mActionCallback = new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            long downloadId = (Long) msg.obj;
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor c = mDownloadManager.query(query);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        initActionBar();
        mDownloadManager = new DownloadManager(this);
        //
        // if (!FileUtils.isSDCardPresent()) {
        // ToastUtils.show(activity, R.string.error_no_sdcard);
        // return;
        // }
        //
        // if (!FileUtils.isSdCardWrittenable()) {
        // ToastUtils.show(activity, R.string.error_sdcard_not_rw);
        // return;
        // }
        //
        // try {
        // FileUtils.prepareRootDir();
        // } catch (Exception e) {
        // ToastUtils.show(activity, R.string.error_unknown);
        // return;
        // }

        initListView();
        mContentObserver = new DownloadChangeObserver();
        getLoaderManager().initLoader(LOADER_DOWNLOAD_LIST, null, this);
    }

    @Override
    protected void onStop() {
        getContentResolver().unregisterContentObserver(mContentObserver);
        super.onStop();
    }

    @Override
    protected void onStart() {
        getContentResolver().registerContentObserver(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                true, mContentObserver);
        super.onStart();
    }

    private class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Loader loader = getLoaderManager().getLoader(LOADER_DOWNLOAD_LIST);
            if (loader != null) {
                loader.forceLoad();
            }
        }
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_download);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    private void initListView() {
        mEmptyHintView = findViewById(R.id.empty_hint_view);
        mLoadingProgress = findViewById(R.id.loading_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new DownloadListAdapter(this, mActionHandler, null, 0);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    public class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleIntent(intent);
        }

        private void handleIntent(Intent intent) {

            if (intent != null && DownloadThread.ACTION_EVENT_NOTIFY.equals(intent.getAction())) {
                int type = intent.getIntExtra(Configs.EVENT, -1);
                String url;

                switch (type) {
                    case Event.ADD:
                        url = intent.getStringExtra(Configs.URL);
                        boolean isPaused = intent.getBooleanExtra(Configs.IS_PAUSED,
                                false);
                        if (!TextUtils.isEmpty(url)) {
                            // mAdapter.addItem(url, isPaused);
                        }
                        break;
                    case Event.COMPLETE:
                        url = intent.getStringExtra(Configs.URL);
                        break;
                    case Event.PROCESS:
                        url = intent.getStringExtra(Configs.URL);
                        View taskListItem = mRecyclerView.findViewWithTag(url);
                        DownloadListAdapter.ViewHolder viewHolder = new DownloadListAdapter.ViewHolder(
                                taskListItem);
                        viewHolder.bindDownloadStatus(Event.PROCESS,
                                intent.getIntExtra(Configs.PROCESS_PROGRESS, 0));
                        break;
                    case Event.ERROR:
                        url = intent.getStringExtra(Configs.URL);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static class DownloadCursorLoader extends CursorLoader {

        public DownloadCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            try {
                DownloadManager.Query query = new DownloadManager.Query();
                query.orderBy(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP,
                        DownloadManager.Query.ORDER_DESCENDING);
                Cursor cursor = new DownloadManager(getContext()).query(query);
                if (cursor != null) {
                    try {
                        // Ensure the cursor window is filled.
                        cursor.getCount();
                        // cursor.registerContentObserver(mObserver);
                    } catch (RuntimeException ex) {
                        cursor.close();
                        throw ex;
                    }
                }
                return cursor;
            } finally {

            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(), Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                null, Downloads.Impl.COLUMN_SCENIC_ID + ">?",
                new String[] {
                    String.valueOf(0)
                },
                Downloads.Impl.COLUMN_LAST_MODIFICATION + " DESC");
    }

    static class DownloadInfo {
        long id;
        String url;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
        Log.d("onLoadFinished, count=%d", data.getCount());
        StringBuffer sb = new StringBuffer();
        final SparseArray<DownloadInfo> sa = new SparseArray<DownloadInfo>();
        while (data.moveToNext()) {
            long id = data.getInt(data.getColumnIndex(Downloads.Impl._ID));
            int scenicId = data.getInt(data.getColumnIndex(Downloads.Impl.COLUMN_SCENIC_ID));
            String url = data.getString(data.getColumnIndex(Downloads.Impl.COLUMN_URI));
            DownloadInfo info = new DownloadInfo();
            info.id = id;
            info.url = url;
            sa.put(scenicId, info);
            sb.append(scenicId);
            if (!data.isLast()) {
                sb.append(",");
            }
        }
        if (sb.length() > 0 && mAdapter.getOfflinePackageList() == null) {
            RequestManager.getInstance().sendGsonRequest(OfflinePackages.buildUrl(sb.toString()),
                    OfflinePackageList.class, new Response.Listener<OfflinePackageList>() {
                        @Override
                        public void onResponse(OfflinePackageList response) {
                            LongSparseArray<String> list = new LongSparseArray<String>();
                            response.list.get(0).md5 = "2484a679eb8241b3f92362c965846b12";
                            response.list.get(0).url = "http://selftravel-offline.qiniudn.com/2484a679eb8241b3f92362c965846b12.zip";
                            Log.d("Kuloud %s", "OfflinePackages update url: " + response.list.get(0).url);
                            SparseArray<String> packageList = response.toSparseArray();
                            for (int i = 0; i < packageList.size(); i++) {
                                DownloadInfo info = sa.get(packageList.keyAt(i));
                                if (info != null) {
                                    if (!TextUtils.isEmpty(info.url) && !info.url.equalsIgnoreCase(packageList.valueAt(i))) {
                                        Log.d("Kuloud %s", "OfflinePackages update id: " + info.id
                                                + ", url: " + packageList.valueAt(i));
                                        list.put(info.id, packageList.valueAt(i));
                                    }
                                }
                            }
                            mDownloadManager.updateDownload(list);
                            mAdapter.setOfflinePackageList(packageList);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error:" + error.getMessage());
                        }
            }, requestTag);
        }
        data.moveToFirst();
        mAdapter.swapCursor(data);
        mLoadingProgress.setVisibility(View.GONE);
        if (data.getCount() == 0) {
            mEmptyHintView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
        mLoadingProgress.setVisibility(View.GONE);
        mEmptyHintView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
