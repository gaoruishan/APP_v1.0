
package com.cmcc.hyapps.andyou.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.FileObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.cmcc.hyapps.andyou.download.DownloadManager;
import com.cmcc.hyapps.andyou.download.DownloadManager.Request;
import com.cmcc.hyapps.andyou.download.DownloadService;
import com.cmcc.hyapps.andyou.download.Downloads;
import com.cmcc.hyapps.andyou.model.IOfflinePackage;
import com.cmcc.hyapps.andyou.model.ScenicDetails;
import com.cmcc.hyapps.andyou.util.CommonUtils;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OfflinePackageManager {
    private static OfflinePackageManager sInstance = new OfflinePackageManager();
    private Context mContext;
    private DownloadManager mDownloadManager;
    private Handler mWorkerHandler;
    private FileObserver mFileObserver;
    private static final int DELETE_EVENTS = FileObserver.DELETE_SELF | FileObserver.MOVE_SELF
            | FileObserver.DELETE | FileObserver.MOVED_FROM;

    private final class OfflinePackageObserver extends FileObserver {
        public OfflinePackageObserver(String path, int mask) {
            super(path, mask);
        }

        @Override
        public void onEvent(int event, String path) {
            Log.d("Offline package directories deleted:%s", path);
            if ((event & DELETE_EVENTS) != 0) {
                mWorkerHandler.obtainMessage().sendToTarget();
            }
        }
    }

    private OfflinePackageManager() {
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mDownloadManager = new DownloadManager(context);

        HandlerThread t = new HandlerThread("");
        t.start();
        mWorkerHandler = new Handler(t.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                trimDownloadDB();
                super.handleMessage(msg);
            }
        };

        mWorkerHandler.obtainMessage().sendToTarget();

        mFileObserver = new OfflinePackageObserver(getOfflinePackageRoot(), DELETE_EVENTS);
        mFileObserver.startWatching();
    }

    public void destroy() {
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
        }

        mWorkerHandler.getLooper().quit();

        Intent intent = new Intent(mContext, DownloadService.class);
        mContext.stopService(intent);
    }

    private static String getOfflinePackageRoot() {
        File root = new File(FileUtils.getExternalRootDir(), "offline");
        if (!root.exists()) {
            root.mkdirs();
        }

        return root.getAbsolutePath();
    }

    public static OfflinePackageManager getInstance() {
        if (sInstance == null) {
            sInstance = new OfflinePackageManager();
        }
        return sInstance;
    }

    public static String getPackageDirFromUri(Uri uri) {
        String root = getOfflinePackageRoot();
        String fileName = uri.getLastPathSegment();
        int pos = fileName.lastIndexOf(".");
        fileName = pos > 0 ? fileName.substring(0, pos) : fileName;
        return new File(root, fileName).getAbsolutePath();
    }

    // TODO improve this
    public <T extends IOfflinePackage> T getOfflineData(int scenicId, Class<T> clazz) {
        String pathUri = getOfflineDataUriPath(scenicId);
        if (pathUri == null) {
            return null;
        }

        try {
            String json = FileUtils.readFromFile(new File(Uri.parse(pathUri).getPath(), clazz
                    .newInstance()
                    .getOfflineFileName()).getAbsolutePath());
            T t = new Gson().fromJson(json, clazz);
            Log.d("offline path root: %s", pathUri);
            t.setOfflinePathRoot(pathUri + File.separator);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean downloadPackage(ScenicDetails scenic) {
        if (scenic == null || scenic.offlinePackage == null
                || !scenic.offlinePackage.isValid()) {
            return false;
        }

        DownloadManager.Request request = new DownloadManager.Request(scenic);
        String fileName = Uri.parse(scenic.offlinePackage.url).getLastPathSegment();
        Uri destUri = Uri.fromFile(new File(FileUtils.getExternalRootDir(),
                fileName));

        Log.d("Downloading %s to %s", scenic.offlinePackage.url, destUri);
        request.setDestinationUri(destUri);
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
        request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
        // request.setAllowedOverRoaming(false);
        mDownloadManager.enqueue(request);
        return true;
    }

    private String getOfflineDataUriPath(int scenicId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                    new String[] {
                        Downloads.Impl.COLUMN_CONTENT_PATH
                    }, Downloads.Impl.COLUMN_SCENIC_ID + "=?", new String[] {
                        String.valueOf(scenicId)
                    }, null);

            if (c.moveToFirst()) {
                return c.getString(c.getColumnIndex(Downloads.Impl.COLUMN_CONTENT_PATH));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return null;
    }

    public int getDownloadStatus(int scenicId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                    new String[] {
                        Downloads.Impl.COLUMN_STATUS
                    }, Downloads.Impl.COLUMN_SCENIC_ID + "=?", new String[] {
                        String.valueOf(scenicId)
                    }, null);

            if (c.moveToFirst()) {
                return c.getInt(c.getColumnIndex(Downloads.Impl.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return -1;
    }

    public boolean needDownload(int scenicId) {
        return !isPackageExists(scenicId);
    }

    public void delete(int scenicId) {
        String path = getOfflineDataUriPath(scenicId);
        if (path == null || !new File(path).exists()) {
            Log.d("offline data %s to delete does not exists", path);
        } else if (!new File(path).delete()) {
            Log.e("Failed to delete offline data from file system:%s", path);
        }

        mContext.getContentResolver().delete(
                Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                Downloads.Impl.COLUMN_SCENIC_ID + "=?", new String[] {
                    String.valueOf(scenicId)
                });
    }

    private boolean isPackageExists(int scenicId) {
        String path = getOfflineDataUriPath(scenicId);
        return path != null && new File(Uri.parse(path).getPath()).exists();
    }

    private void trimDownloadDB() {
        List<Integer> tobeRemoved = new ArrayList<Integer>();
        Cursor c = mContext.getContentResolver().query(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                new String[] {
                        Downloads.Impl.COLUMN_SCENIC_ID, Downloads.Impl.COLUMN_CONTENT_PATH
                }, Downloads.Impl.COLUMN_STATUS + "=?", new String[] {
                    String.valueOf(Downloads.Impl.STATUS_SUCCESS)
                }, null);
        if (c != null) {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex(Downloads.Impl.COLUMN_SCENIC_ID));
                String packagePath = c.getString(c
                        .getColumnIndex(Downloads.Impl.COLUMN_CONTENT_PATH));
                if (TextUtils.isEmpty(packagePath)
                        || !new File(Uri.parse(packagePath).getPath()).exists()) {
                    Log.d("trimDownloadDB, to be removed:%s", packagePath);
                    tobeRemoved.add(id);
                }
            }
        }

        CommonUtils.closeCursor(c);

        for (int id : tobeRemoved) {
            // TODO: more efficient!
            mContext.getContentResolver().delete(
                    Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                    Downloads.Impl.COLUMN_SCENIC_ID + "=?", new String[] {
                        String.valueOf(id)
                    });
        }
    }
}
