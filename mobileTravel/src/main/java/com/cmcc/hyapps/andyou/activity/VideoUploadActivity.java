/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.qiniu.auth.Authorizer;
import com.qiniu.io.IO;
import com.qiniu.rs.CallBack;
import com.qiniu.rs.CallRet;
import com.qiniu.rs.PutExtra;
import com.qiniu.rs.UploadCallRet;
import com.qiniu.rs.UploadTaskExecutor;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.Upload.Bucket;
import com.cmcc.hyapps.andyou.app.ServerAPI.Upload.Token.UploadParams;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.EmptyResponse;
import com.cmcc.hyapps.andyou.model.Video;
import com.cmcc.hyapps.andyou.util.CommonUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ImageHelper;
import com.cmcc.hyapps.andyou.util.LocationUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.TimeUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.CommonDialog;
import com.cmcc.hyapps.andyou.widget.CommonDialog.OnDialogViewClickListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.cmcc.hyapps.andyou.model.QHScenic.QHVideo;

/**
 * @author kuloud
 */
public class VideoUploadActivity extends BaseActivity {
    private static final int REQUEST_CODE_CAPTURE_VIDEO = 1;
    private static final int VIDEO_CAPTURE_LIMIT_SECS = 10;
    private ImageView mVideoPreview;
    private EditText mVideoTitle;
    private File mVideoFile;
    private ImageView mSendButton;
    private UploadTaskExecutor mUploadVideoFileTask;
    private UploadTaskExecutor mUploadThunmnailTask;
    private Bitmap mThumbnail;
    private Video mVideo;

    private CommonDialog mInProgressDialog;
    private int mScenicId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        mScenicId = getIntent().getIntExtra(Const.EXTRA_ID, -1);

        mVideoPreview = (ImageView) findViewById(R.id.video_preview);
        int spacing = ScreenUtils.getDimenPx(getApplicationContext(), R.dimen.common_margin);
        int dimen = ScreenUtils.getScreenWidth(this) - spacing * 2;
        mVideoPreview.setLayoutParams(new FrameLayout.LayoutParams(dimen, dimen));

        mVideoTitle = (EditText) findViewById(R.id.video_title);
        mVideoPreview.setOnClickListener(mOnClickListener);

        mInProgressDialog = new CommonDialog(this);
        mInProgressDialog.getLeftBtn().setVisibility(View.GONE);
        mInProgressDialog.setTitleText(R.string.dialog_title_publishing_video);
        mInProgressDialog.setContentText(getString(R.string.dialog_content_publishing_video, 0));
        mInProgressDialog.getRightBtn().setText(R.string.dialog_cancel_sending);
        mInProgressDialog.getDialog().setCancelable(false);
        mInProgressDialog.getDialog().setCanceledOnTouchOutside(false);

        mInProgressDialog.setOnDialogViewClickListener(new OnDialogViewClickListener() {

            @Override
            public void onRightButtonClick() {
                cancelRequests();
            }

            @Override
            public void onLeftButtonClick() {
            }
        });

        initActionBar();
        captureVideo();
    }

    private void cancelRequests() {
        RequestManager.getInstance().cancelAll(requestTag);
        if (mUploadVideoFileTask != null) {
            mUploadVideoFileTask.cancel();
            mUploadVideoFileTask = null;
        }

        if (mUploadThunmnailTask != null) {
            mUploadThunmnailTask.cancel();
            mUploadThunmnailTask = null;
        }

        if (mInProgressDialog != null && mInProgressDialog.getDialog().isShowing()) {
            mInProgressDialog.dismissDialog();
        }
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_video_upload);
        mSendButton = actionBar.getRightView();
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        mSendButton.setImageResource(R.drawable.ic_action_bar_send_selecter);
        actionBar.getLeftView().setOnClickListener(mOnClickListener);
        mSendButton.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAPTURE_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String filePath = null;
                    Uri uri = data.getData();
                    if (uri != null) {
                        if ("content".equals(uri.getScheme())) {
                            mVideoFile = getVideoFile(uri);
                        } else {
                            filePath = uri.getPath();
                            if (filePath != null) {
                                mVideoFile = new File(filePath);
                            }
                        }
                        Log.e("Pick video, file path: " + filePath);
                    } else {
                        Log.e("Pick video uri NULL");
                    }
                }
                if (mVideoFile == null) {
                    mVideoFile = getVideoFile(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                }
                if (mVideoFile != null && mVideoFile.exists()) {
                    mThumbnail = ThumbnailUtils.createVideoThumbnail(mVideoFile.getAbsolutePath(),
                            MediaStore.Video.Thumbnails.MINI_KIND);
                    mVideoPreview.setImageBitmap(mThumbnail);
                }
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    private File getVideoFile(Uri uri) {
        String[] columns = {
                MediaStore.Video.Media.DATA, MediaStore.MediaColumns.DATE_ADDED
        };

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, columns, null, null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC");

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);

        cursor.close();

        return new File(path);
    }

    private void captureVideo() {
        Intent intent = new Intent(VideoUploadActivity.this, VideoRecorderActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_VIDEO);
//        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        // set the video file name
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri());
//        // set the video quality high
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, VIDEO_CAPTURE_LIMIT_SECS);
//        // start the video capture Intent
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, REQUEST_CODE_CAPTURE_VIDEO);
//        } else {
//            ToastUtils.show(getApplicationContext(), R.string.video_error_not_support);
//        }
    }

    private static Uri getOutputMediaFileUri() {
        // get the mobile Pictures directory
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // get the current time
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE)
                .format(new Date());
        File videoFile = new File(picDir.getPath() + File.separator + "VIDEO_" + timeStamp + ".mp4");
        return Uri.fromFile(videoFile);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (ExcessiveClickBlocker.isExcessiveClick()) {
                return;
            }
            switch (v.getId()) {
                case R.id.action_bar_left:
                    finish();
                    break;
                case R.id.action_bar_right: {
                    if (TextUtils.isEmpty(mVideoTitle.getText())) {
                        // TODO
                        Toast.makeText(getApplicationContext(), R.string.video_error_missing_title,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mInProgressDialog.showDialog();
                    fillVideo();
                    getUploadVideoToken();
                    getUploadThumbnailToken();
                    break;
                }
                case R.id.video_preview: {
                    if (mVideoFile != null && mVideoFile.exists()) {
                        Uri uri = Uri.fromFile(mVideoFile);
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // No player to play video, just play it in
                            // VideoPlaybackActivity
                            QHVideo video = new QHVideo();
                            video.video_day = uri.toString();
                            CommonUtils.playVideo(VideoUploadActivity.this, video);
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void fillVideo() {
        mVideo = new Video();
        mVideo.scenicId = mScenicId;
        mVideo.size = mVideoFile.length();
        mVideo.title = mVideoTitle.getText().toString();
        mVideo.createdTime = TimeUtils.formatTime(System.currentTimeMillis(),
                TimeUtils.DATE_TIME_FORMAT);
        mVideo.location = LocationUtils
                .getLastKnownLocation(getApplicationContext());

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mVideoFile.getAbsolutePath());
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        // String width =
        // retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        // String height =
        // retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String mime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        Bitmap bitmap = retriever.getFrameAtTime();

        mVideo.duration = Long.parseLong(duration);
        mVideo.width = bitmap.getWidth();
        mVideo.height = bitmap.getHeight();
        mVideo.mimeType = mime;
        retriever.release();
    }

    private void uploadVideoJson() {
        if (TextUtils.isEmpty(mVideo.url)
                || TextUtils.isEmpty(mVideo.thumbnail)) {
            return;
        }

        String gson = new Gson().toJson(mVideo);
        Log.d("uploadVideo, video json: %s", gson);
        RequestManager.getInstance().sendGsonRequest(Method.POST,
                ServerAPI.VideoUpload.URL,
                EmptyResponse.class, gson, new Listener<EmptyResponse>() {
                    @Override
                    public void onResponse(EmptyResponse response) {
                        videoPostSuccess();
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("videoPostError, error=%s", error);
                        videoPostError();
                    }
                }, false, requestTag);
    }

    private void getUploadVideoToken() {
        UploadParams params = new UploadParams();
        params.bucket = Bucket.bucketVideo;

        ServerAPI.Upload.Token.getUploadToken(getApplicationContext(), params,
                requestTag,
                new ServerAPI.Upload.Token.UploadTokenCallback() {

                    @Override
                    public void onTokenError() {
                        videoPostError();
                    }

                    @Override
                    public void onGetToken(String token) {
                        uploadVideoFile(token);
                    }
                });
    }

    private void getUploadThumbnailToken() {
        UploadParams params = new UploadParams();
        params.bucket = Bucket.bucketImage;

        ServerAPI.Upload.Token.getUploadToken(getApplicationContext(),
                params,
                requestTag,
                new ServerAPI.Upload.Token.UploadTokenCallback() {

                    @Override
                    public void onTokenError() {
                        videoPostError();
                    }

                    @Override
                    public void onGetToken(String token) {
                        uploadThunmnail(token);
                    }
                });
    }

    private void uploadVideoFile(String token) {
        PutExtra extra = new PutExtra();
        extra.mimeType = mVideo.mimeType;
        Authorizer authorizer = new Authorizer();
        authorizer.setUploadToken(token);

        mUploadVideoFileTask = IO.putFile(getApplicationContext(),
                authorizer, IO.UNDEFINED_KEY,
                Uri.fromFile(mVideoFile), extra,
                new CallBack() {
                    @Override
                    public void onProcess(long current, long total) {
                        int percent = (int) (current * 100 / total);
                        mInProgressDialog.setContentText(getString(
                                R.string.dialog_content_publishing_video, percent));
                        Log.d("Uploading percent %d", percent);
                    }

                    @Override
                    public void onSuccess(UploadCallRet ret) {
                        if (ret.getException() != null) {
                            Log.e(ret.getException(), "Upload error");
                            videoPostError();
                        } else {
                            String key = ret.getKey();
                            String redirect = "http://"
                                    + ServerAPI.Upload.Bucket.bucketVideo.value()
                                    + ".qiniudn.com/" + key;
                            Log.d("Video %s uploaded to %s", mVideoFile.getAbsolutePath(), redirect);

                            mVideo.url = redirect;
                            uploadVideoJson();
                        }

                    }

                    @Override
                    public void onFailure(CallRet ret) {
                        videoPostError();
                    }
                });
    }

    private void uploadThunmnail(String token) {
        PutExtra extra = new PutExtra();
        extra.mimeType = "image/jpeg";
        Authorizer authorizer = new Authorizer();
        authorizer.setUploadToken(token);

        mUploadThunmnailTask = IO.put(authorizer, IO.UNDEFINED_KEY,
                ImageHelper.bitmap2InputStreamAt(mThumbnail), extra, new CallBack() {
                    @Override
                    public void onProcess(long current, long total) {
                        int percent = (int) (current * 100 / total);
                        Log.d("Uploading thunmbnail percent %d", percent);
                    }

                    @Override
                    public void onSuccess(UploadCallRet ret) {
                        if (ret.getException() != null) {
                            Log.e(ret.getException(), "Upload error");
                            videoPostError();
                        } else {
                            String key = ret.getKey();
                            String redirect = "http://"
                                    + ServerAPI.Upload.Bucket.bucketImage.value()
                                    + ".qiniudn.com/" + key;
                            Log.d("Video thunmbnail uploaded to %s", redirect);

                            mVideo.thumbnail = redirect;
                            uploadVideoJson();
                        }

                    }

                    @Override
                    public void onFailure(CallRet ret) {
                        videoPostError();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mVideoFile != null && mVideoFile.exists()) {
            mVideoFile.delete();
        }
        cancelRequests();
        super.onDestroy();
    }

    private void videoPostSuccess() {
        Log.d("videoPostSuccess");
        if (mInProgressDialog.getDialog().isShowing()) {
            mInProgressDialog.dismissDialog();
        }
        setResult(RESULT_OK);
        Toast.makeText(this, R.string.upload_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void videoPostError() {
        Log.d("videoPostError");
        if (mInProgressDialog.getDialog().isShowing()) {
            mInProgressDialog.dismissDialog();
        }
        // TODO
        Toast.makeText(this, R.string.video_post_error, Toast.LENGTH_SHORT).show();
    }
}
