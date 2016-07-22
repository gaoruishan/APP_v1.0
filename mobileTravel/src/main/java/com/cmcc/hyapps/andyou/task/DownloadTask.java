
package com.cmcc.hyapps.andyou.task;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.cmcc.hyapps.andyou.app.AndroidHttpClient;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.NetUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author kuloud
 */
public class DownloadTask extends BaseTask<Void, Integer, Long> {

    public final static int TIME_OUT = 30000;
    private final static int BUFFER_SIZE = 1024 * 8;

    private static final String TEMP_SUFFIX = ".download";

    private URL mUrl;
    private File mFile;
    private File mTempFile;
    private String mUrlStr;
    private RandomAccessFile outFile;
    private DownloadTaskListener mListener;
    private Context mContext;

    private long mDownloadSize;
    private long mPreviousFileSize;
    private long mTotalSize;
    private int mDownloadPercent;
    private long mNetworkSpeed;
    private long mPreviousTime;
    private long mTotalTime;
    private Throwable mError = null;
    private boolean mInterrupt = false;

    private AndroidHttpClient mHttpClient;
    private HttpGet mHttpGet;
    private HttpResponse mHttpResponse;

    private final class ProgressReportingRandomAccessFile extends RandomAccessFile {
        private int progress = 0;

        public ProgressReportingRandomAccessFile(File file, String mode)
                throws FileNotFoundException {
            super(file, mode);
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);
            progress += count;
            publishProgress(progress);
        }
    }

    public DownloadTask(Context context, String url, String path) throws MalformedURLException {
        this(context, url, path, null);
    }

    public DownloadTask(Context context, String url, String path, DownloadTaskListener listener)
            throws MalformedURLException {
        super();
        this.mUrlStr = url;
        this.mUrl = new URL(url);
        this.mListener = listener;
        String fileName = new File(mUrl.getFile()).getName();
        this.mFile = new File(path, fileName);
        this.mTempFile = new File(path, fileName + TEMP_SUFFIX);
        this.mContext = context;
    }

    public String getUrl() {
        return mUrlStr;
    }

    public boolean isInterrupt() {
        return mInterrupt;
    }

    public int getDownloadPercent() {
        return mDownloadPercent;
    }

    public long getDownloadSize() {
        return mDownloadSize + mPreviousFileSize;
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public long getDownloadSpeed() {
        return this.mNetworkSpeed;
    }

    public long getTotalTime() {
        return this.mTotalTime;
    }

    public DownloadTaskListener getListener() {
        return this.mListener;
    }

    @Override
    protected void onPreExecute() {
        mPreviousTime = System.currentTimeMillis();
        if (mListener != null)
            mListener.preDownload(this);
    }

    @Override
    protected Long doInBackground(Void... params) {
        long result = -1;
        try {
            result = download();
        } catch (NetworkErrorException e) {
            mError = e;
        } catch (IOException e) {
            mError = e;
        } finally {
            if (mHttpClient != null) {
                mHttpClient.close();
            }
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (progress.length > 1) {
            mTotalSize = progress[1];
            if (mTotalSize < 0) {
                if (mListener != null) {
                    mListener.errorDownload(this, mError);
                }
            }
        } else {
            mTotalTime = System.currentTimeMillis() - mPreviousTime;
            mDownloadSize = progress[0];
            mDownloadPercent = (int) ((mDownloadSize + mPreviousFileSize) * 100 / mTotalSize);
            mNetworkSpeed = mDownloadSize / mTotalTime;
            if (mListener != null) {
                mListener.updateProcess(this);
            }
        }
    }

    @Override
    protected void onPostExecute(Long result) {
        if (result == -1 || mInterrupt || mError != null) {
            if (mError != null) {
                Log.v("Download failed." + mError.getMessage());
            }
            if (mListener != null) {
                mListener.errorDownload(this, mError);
            }
            return;
        }
        // finish download
        mTempFile.renameTo(mFile);
        if (mListener != null) {
            mListener.finishDownload(this);
        }
    }

    @Override
    public void onCancelled() {
        super.onCancelled();
        mInterrupt = true;
    }

    private long download() throws NetworkErrorException, IOException {
        Log.v("totalSize: " + mTotalSize);
        /*
         * check net work
         */
        if (!NetUtils.isNetworkAvailable(mContext)) {
            throw new NetworkErrorException("Network blocked.");
        }

        /*
         * check file length
         */
        mHttpClient = AndroidHttpClient.newInstance("DownloadTask");
        mHttpGet = new HttpGet(mUrlStr);
        mHttpResponse = mHttpClient.execute(mHttpGet);
        mTotalSize = mHttpResponse.getEntity().getContentLength();

        if (mFile.exists() && mTotalSize == mFile.length()) {
            Log.e("Output file already exists. Skipping download.");
            throw new IOException("Output file already exists. Skipping download.");
        } else if (mTempFile.exists()) {
            mHttpGet.addHeader("Range", "bytes=" + mTempFile.length() + "-");
            mPreviousFileSize = mTempFile.length();

            mHttpClient.close();
            mHttpClient = AndroidHttpClient.newInstance("DownloadTask");
            mHttpResponse = mHttpClient.execute(mHttpGet);
            Log.v("File is not complete, download now.");
            Log.v("File length:" + mTempFile.length() + " totalSize:" + mTotalSize);
        }

        /*
         * check memory
         */
        long storage = FileUtils.getAvailableStorage();
        Log.d("storage:" + storage + " totalSize:" + mTotalSize);
        if (mTotalSize - mTempFile.length() > storage) {
            throw new IOException("SD card no memory.");
        }

        /*
         * start download
         */
        outFile = new ProgressReportingRandomAccessFile(mTempFile, "rw");

        publishProgress(0, (int) mTotalSize);

        InputStream input = mHttpResponse.getEntity().getContent();
        int bytesCopied = copy(input, outFile);

        if ((mPreviousFileSize + bytesCopied) != mTotalSize && mTotalSize != -1 && !mInterrupt) {
            throw new IOException("Download incomplete: " + bytesCopied + " != " + mTotalSize);
        }

        Log.v("Download completed successfully.");

        return bytesCopied;

    }

    public int copy(InputStream input, RandomAccessFile out) throws IOException,
            NetworkErrorException {

        if (input == null || out == null) {
            return -1;
        }

        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);

        Log.v("length" + out.length());

        int count = 0, n = 0;
        long errorBlockTimePreviousTime = -1, expireTime = 0;

        try {
            out.seek(out.length());

            while (!mInterrupt) {
                n = in.read(buffer, 0, BUFFER_SIZE);
                if (n == -1) {
                    break;
                }
                out.write(buffer, 0, n);
                count += n;

                /*
                 * check network
                 */
                if (!NetUtils.isNetworkAvailable(mContext)) {
                    throw new NetworkErrorException("Network blocked.");
                }

                if (mNetworkSpeed == 0) {
                    if (errorBlockTimePreviousTime > 0) {
                        expireTime = System.currentTimeMillis() - errorBlockTimePreviousTime;
                        if (expireTime > TIME_OUT) {
                            throw new ConnectTimeoutException("connection time out.");
                        }
                    } else {
                        errorBlockTimePreviousTime = System.currentTimeMillis();
                    }
                } else {
                    expireTime = 0;
                    errorBlockTimePreviousTime = -1;
                }
            }
        } finally {
            mHttpClient.close(); // must close client first
            mHttpClient = null;
            out.close();
            in.close();
            input.close();
        }
        return count;

    }

    public interface DownloadTaskListener {

        public void updateProcess(DownloadTask task);

        public void finishDownload(DownloadTask task);

        public void preDownload(DownloadTask task);

        public void errorDownload(DownloadTask task, Throwable error);
    }

}
