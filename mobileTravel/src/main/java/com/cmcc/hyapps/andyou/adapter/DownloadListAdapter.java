
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.LevelListDrawable;
import android.os.Handler;
import android.support.v7.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.ScenicDetailActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.data.OfflinePackageManager;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.download.DownloadManager;
import com.cmcc.hyapps.andyou.download.Downloads;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.TimeUtils;
import com.cmcc.hyapps.andyou.widget.CircularSeekBar;
import com.cmcc.hyapps.andyou.widget.CommonDialog;
import com.cmcc.hyapps.andyou.widget.CommonDialog.OnDialogViewClickListener;

/**
 * @author kuloud
 */
public class DownloadListAdapter extends CursorAdapter<DownloadListAdapter.ViewHolder> {
    private Activity mActivity;
    private Handler mActionHandler;
    private DownloadManager mDownloadManager;
    private SparseArray<String> mOfflinePackageList;

    public DownloadListAdapter(Activity context, Handler handler, Cursor c, int flags) {
        super(context, c, flags);
        mActivity = context;
        mActionHandler = handler;
        mDownloadManager = new DownloadManager(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download,
                parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView scenicImageView;
        CircularSeekBar downloadProgress;
        TextView progressText;
        TextView scenicName;
        TextView scenicIntro;
        TextView size;
        TextView date;
        View actionView;
        /**
         * Level 0 null 1 download 2 continue 3 complete
         */
        LevelListDrawable statusIcon;
        View deleteView;

        public ViewHolder(View v) {
            super(v);
            scenicImageView = (NetworkImageView) itemView.findViewById(R.id.download_scenic_image);
            scenicName = (TextView) itemView.findViewById(R.id.scenic_name);
            scenicIntro = (TextView) itemView.findViewById(R.id.scenic_intro);
            size = (TextView) itemView.findViewById(R.id.size);
            date = (TextView) itemView.findViewById(R.id.date);
            actionView = itemView.findViewById(R.id.action);

            downloadProgress = (CircularSeekBar) itemView.findViewById(R.id.download_progress);
            downloadProgress.setMaxProgress(100);
            downloadProgress.setSeekable(false);
            downloadProgress.invalidate();
            progressText = (TextView) itemView.findViewById(R.id.download_icon);
            statusIcon = (LevelListDrawable) progressText.getBackground();

            deleteView = itemView.findViewById(R.id.delete);
        }

        public void bindDownloadStatus(int status, int progress) {
            Log.d("bindDownloadStatus, status=%d, progress=%d", status, progress);
            // TODO temp solution
            if ((progress < downloadProgress.getProgress()
                    && status != Downloads.Impl.STATUS_SUCCESS) || progress > 100) {
                Log.e("weird download progress:%d, current progress:%d", progress,
                        downloadProgress.getProgress());
                return;
            }

            switch (status) {
                case Downloads.Impl.STATUS_RUNNING:
                    statusIcon.setLevel(0);
                    progressText.setText(progress + "%");
                    downloadProgress.setProgress(progress);
                    break;
                case Downloads.Impl.STATUS_SUCCESS:
                    progressText.setText("");
                    statusIcon.setLevel(3);
                    downloadProgress.setProgress(progress);
                    break;
                // paused
                case Downloads.Impl.STATUS_PAUSED_BY_APP:
                case Downloads.Impl.STATUS_WAITING_TO_RETRY:
                case Downloads.Impl.STATUS_WAITING_FOR_NETWORK:
                case Downloads.Impl.STATUS_QUEUED_FOR_WIFI:
                    // pending
                case Downloads.Impl.STATUS_PENDING:
                default:
                    // failed
                    statusIcon.setLevel(2);
                    progressText.setText("");
                    downloadProgress.setProgress(progress);
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        int scenicId = cursor.getInt(cursor.getColumnIndex(Downloads.Impl.COLUMN_SCENIC_ID));
        String url = cursor.getString(cursor.getColumnIndex(Downloads.Impl.COLUMN_URI));
        holder.itemView.setTag(scenicId);
        String scenicName = cursor.getString(cursor.getColumnIndex(Downloads.Impl.COLUMN_SCENIC_NAME));
        holder.scenicName.setText(scenicName);
        if (mOfflinePackageList != null) {
            String packageUrl = mOfflinePackageList.get(scenicId);
            if (!TextUtils.isEmpty(packageUrl) && !packageUrl.equalsIgnoreCase(url)) {
                holder.scenicName.setCompoundDrawables(null, null, mContext.getResources().getDrawable(R.drawable.red_dot), null);
            } else {
                holder.scenicName.setCompoundDrawables(null, null, null, null);
            }
        } else {
            holder.scenicName.setCompoundDrawables(null, null, null, null);
        }
        String scenicIntro = cursor.getString(cursor.getColumnIndex(Downloads.Impl.COLUMN_SCENIC_INTRO));
        holder.scenicIntro.setText(scenicIntro);
        String coverImageUrl = cursor.getString(cursor.getColumnIndex(Downloads.Impl.COLUMN_COVER_IMAGE));
        if (!TextUtils.isEmpty(coverImageUrl)) {
//            holder.scenicImageView.setDefaultImageResId(R.drawable.bg_image_hint)
//                    .setErrorImageResId(R.drawable.bg_image_hint);
//            holder.scenicImageView.setImageUrl(coverImageUrl, RequestManager.getInstance()
//                    .getImageLoader());

            ImageUtil.DisplayImage(coverImageUrl, holder.scenicImageView, R.drawable.bg_image_hint, R.drawable.bg_image_hint);
        }

        long currentSize = cursor.getLong(cursor.getColumnIndex(Downloads.Impl.COLUMN_CURRENT_BYTES));
        long totalSize = cursor.getLong(cursor.getColumnIndex(Downloads.Impl.COLUMN_TOTAL_BYTES));
        int status = cursor.getInt(cursor.getColumnIndex(Downloads.Impl.COLUMN_STATUS));
        int progress = totalSize > 0 ? (int) (currentSize * 100 / totalSize) : 0;
        Log.d("currentSize=%d, totalSize=%d", currentSize, totalSize);
        holder.bindDownloadStatus(status, progress);

        if (totalSize > 0) {
            holder.size.setText(Formatter.formatFileSize(mContext, totalSize));
        }

        long date = cursor.getLong(cursor.getColumnIndex(Downloads.Impl.COLUMN_LAST_MODIFICATION));
        holder.date.setText(TimeUtils.formatTime(date, TimeUtils.DATE_FORMAT));

        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                int scenicId = (Integer) v.getTag();
                Intent intent = new Intent(mActivity, ScenicDetailActivity.class);
                intent.putExtra(Const.EXTRA_ID, scenicId);
                mActivity.startActivity(intent);
            }
        });

        long downloadId = cursor.getLong(cursor.getColumnIndex(Downloads.Impl._ID));
        holder.actionView.setTag(downloadId);
        holder.actionView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                long downloadId = (Long) v.getTag();
                // Message msg = mActionHandler.obtainMessage();
                // msg.obj = downloadId;
                // msg.sendToTarget();
                // TODO: move it to another thread if it's too slow
                mDownloadManager.pauseOrResumeDownload(downloadId);
            }
        });

        holder.itemView.setLongClickable(true);
        holder.itemView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                showDownloadDialog(v);
                return true;
            }
        });
    }

    private void showDownloadDialog(final View v) {
        CommonDialog downloadDialog = new CommonDialog(mActivity);
        downloadDialog.setTitleText(R.string.offline_download);
        downloadDialog.getDialog().setCancelable(true);
        downloadDialog.getDialog().setCanceledOnTouchOutside(true);
        downloadDialog.setContentText(R.string.offline_download_delete_confirm);
        downloadDialog.setOnDialogViewClickListener(new OnDialogViewClickListener() {

            @Override
            public void onRightButtonClick() {
            }

            @Override
            public void onLeftButtonClick() {
                int scenicId = (Integer) v.getTag();
                OfflinePackageManager.getInstance().delete(scenicId);
            }
        });
        downloadDialog.showDialog();
    }

    @Override
    protected void onContentChanged() {

    }

    public SparseArray<String> getOfflinePackageList() {
        return this.mOfflinePackageList;
    }

    public void setOfflinePackageList(SparseArray<String> hashArray) {
        this.mOfflinePackageList = hashArray;
        if (mOfflinePackageList != null) {
            notifyDataSetChanged();
        }
    }
}
