package com.cmcc.hyapps.andyou.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.recorder.RecordUtils;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.CameraHelper;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.CommonDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnTouch;

/**
 * @author Kuloud
 */
public class VideoRecorderActivity extends BaseActivity implements SurfaceHolder.Callback {

    private final static String TAG = VideoRecorderActivity.class.getSimpleName();
    private final static String FORMAT_RECORED_TIME = "00:%02d";
    private Context mContext;

    private static final int MAX_RECORD_SECONDS = 10;
    private static final int VALID_RECORD_SECONDS = 8;
    private int mRecordSeconds = MAX_RECORD_SECONDS;
    private Camera mCamera;
    private Parameters mCameraParams;
    private SurfaceHolder mHolder;
    private String mVideoPath;
    private Uri mUriVideoPath;
    private MediaRecorder mVideoRecorder;
    private int mDegree = 0;
    private int mScreenWidth;// 竖屏为准

    private SurfaceView mSurfaceView;
    private ImageView mRecordBtn;
    private TextView mRecordTime;

    private final Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mRecordSeconds--;
            if (mRecordSeconds >= 0) {
                if (mRecordSeconds < VALID_RECORD_SECONDS) {
                    mRecordBtn.setEnabled(true);
                }
                sendEmptyMessageDelayed(0, 1000);
                mRecordTime.setText(String.format(FORMAT_RECORED_TIME, mRecordSeconds));
            } else {
                mRecordBtn.setEnabled(false);
                mRecordBtn.getDrawable().setLevel(0);
                stopRecord();
            }
        }

        ;
    };

    private void touch2Focus(int x, int y) {
        if (mCameraParams == null) {
            return;
        }
        Rect focusRect = CameraHelper.getFocusArea(x, y, mScreenWidth,
                mScreenWidth, 300);
        List<Camera.Area> areas = new ArrayList<Camera.Area>();
        areas.add(new Camera.Area(focusRect, 1000));
        if (mCameraParams.getMaxNumFocusAreas() > 0) {
            mCameraParams.setFocusAreas(areas);// 设置对焦区域
        }
        if (mCameraParams.getMaxNumMeteringAreas() > 0) {
            mCameraParams.setMeteringAreas(areas);// 设置测光区域
        }
        mCamera.cancelAutoFocus();
        mCameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(mCameraParams);
        mCamera.autoFocus(null);
    }

    // 对焦框
    class FocusView extends View {
        int left, top;
        Bitmap bitmap;

        public FocusView(Context context, int left, int top, Bitmap bitmap) {
            super(context);
            this.left = left;
            this.top = top;
            this.bitmap = bitmap;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(bitmap, left, top, null);
            super.onDraw(canvas);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mScreenWidth = ScreenUtils.getScreenWidth(mContext);

        if (RecordUtils.checkCameraHardware(mContext)) {
            mCamera = RecordUtils.getCameraInstance(mContext);
        } else {
            ToastUtils.show(mContext, "相机不可用");
            finish();
        }

        setContentView(R.layout.activity_recorder);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            showWainningDialog();
        }
    }

    @Override
    protected void onStop() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mhandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_record_video);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    @OnTouch(R.id.recorder_surface)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int fx = (int) event.getX();
                int fy = (int) event.getY();
                if (fy > mScreenWidth)
                    return true;
                touch2Focus(fx, fy);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    private void initViews() {
        initActionBar();

        mSurfaceView = (SurfaceView) findViewById(R.id.recorder_surface);
        mRecordTime = (TextView) findViewById(R.id.record_time);
        mRecordTime.setText(String.format(FORMAT_RECORED_TIME, MAX_RECORD_SECONDS));
        mRecordBtn = (ImageView) findViewById(R.id.record);
        mRecordBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                mRecordBtn.setEnabled(false);
                if (mRecordBtn.getDrawable().getLevel() > 0) {
                    mRecordBtn.getDrawable().setLevel(0);
                    if (mRecordSeconds >= 8) {
                        finish();
                        return;
                    }
                    stopRecord();
                } else {
                    mhandler.sendEmptyMessage(0);
                    mRecordBtn.getDrawable().setLevel(1);
                    startRecord();
                }
            }
        });

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mCameraParams = mCamera != null ? mCamera.getParameters() : null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera == null) return;
        try {
            mCamera.setPreviewDisplay(mHolder);
            mDegree = RecordUtils.setCameraDisplayOrientation(getWindowManager(),
                    Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null || mCamera == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }

        Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        if (sizes != null) {

            Camera.Size size = RecordUtils.getOptimalPreviewSize(activity, sizes, 1);
            params.setPreviewSize(size.width, size.height);
            mCamera.setParameters(params);
        }

        for (Camera.Size s : sizes) {
            Log.d("camera_size: ", "support " + s.width + "x" + s.height);
        }
        Log.d("camera_size: ", params.getPreviewSize().width + "X"
                + params.getPreviewSize().height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mHolder = null;
        }
    }

    public void startRecord() {
        if (mVideoRecorder == null) {
            mVideoRecorder = new MediaRecorder();
        }

        initRecorder();
    }

    /**
     * Initialize video recorder to record video
     */
    private void initRecorder() {
        try {
            mCamera.stopPreview();
            mCamera.unlock();
            mVideoPath = new File(FileUtils.getExternalRootDir(), "record.mp4").getAbsolutePath();
            mVideoRecorder.setCamera(mCamera);

            // Step 2: Set sources
            mVideoRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mVideoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
            mVideoRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

            // Step 4: Set output file
            mVideoRecorder.setOutputFile(mVideoPath);
            // Step 5: Set the preview output
            mVideoRecorder.setPreviewDisplay(mHolder.getSurface());

            // Step 6: Prepare configured MediaRecorder
            mVideoRecorder.setOrientationHint(mDegree);
            mVideoRecorder.setMaxDuration(10 * 1000); // max 10s
            mVideoRecorder.setMaxFileSize(1024 * 1024); // max 1mb
            mVideoRecorder.setAudioSamplingRate(44100);
            mVideoRecorder.setVideoFrameRate(15);
            mVideoRecorder.setVideoEncodingBitRate(500 * 1000);
            mVideoRecorder.setAudioChannels(1);
            mVideoRecorder.setOnInfoListener(new OnInfoListener() {

                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    switch (what) {
                        case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                            mRecordBtn.getDrawable().setLevel(0);
                            mCamera.stopPreview();
                            releaseMediaRecorder();
                            break;

                        default:
                            break;
                    }
                }
            });
            mVideoRecorder.prepare();
            mVideoRecorder.start();
        } catch (Exception e) {
            Log.e("Error Stating CuXtom Camera", "" + e.getMessage());
        }
    }

    public void releaseMediaRecorder() {
        if (mVideoRecorder != null) {
            mVideoRecorder.reset(); // clear recorder configuration
            mVideoRecorder.release(); // release the recorder object
            mVideoRecorder = null;
        }
    }

    public void stopRecord() {
        if (mVideoRecorder != null) {
            releaseMediaRecorder();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                registerVideo();
                setActivityResult(true);
                finish();
            }
        }).start();
    }

    private void setActivityResult(boolean valid) {
        Intent resultIntent = new Intent();
        int resultCode;
        if (valid) {
            resultCode = RESULT_OK;
            resultIntent.setData(mUriVideoPath);
        } else
            resultCode = RESULT_CANCELED;

        setResult(resultCode, resultIntent);
    }

    private void registerVideo() {
        Uri videoTable = Uri.parse(RecordUtils.VIDEO_CONTENT_URI);

        ContentValues values = new ContentValues(7);
        values.put(Video.Media.TITLE, "ST_record_video");
        values.put(Video.Media.DISPLAY_NAME, "record_video");
        values.put(Video.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(Video.Media.MIME_TYPE, "video/3gpp");
        values.put(Video.Media.DATA, mVideoPath);
        values.put(Video.Media.SIZE, new File(mVideoPath).length());
        try {
            mUriVideoPath = getContentResolver().insert(videoTable, values);
        } catch (Throwable e) {
            mUriVideoPath = null;
        } finally {
        }
    }

    private void showWainningDialog() {
        CommonDialog logoutDialog = new CommonDialog
                (VideoRecorderActivity.this);
        logoutDialog.setTitleText(R.string.dialog_title_camera_disabled);
        logoutDialog.getDialog().setCancelable(false);
        logoutDialog.getDialog().setCanceledOnTouchOutside(false);
        logoutDialog.setContentText(R.string.dialog_content_camera_disabled);
        logoutDialog.getRightBtn().setVisibility(View.GONE);
        logoutDialog.setOnDialogViewClickListener(new CommonDialog.OnDialogViewClickListener() {

            @Override
            public void onRightButtonClick() {
            }

            @Override
            public void onLeftButtonClick() {
                finish();
//                SystemUtils.go2AppDetailSettings(mContext);
            }
        });
        logoutDialog.showDialog();
    }
}
