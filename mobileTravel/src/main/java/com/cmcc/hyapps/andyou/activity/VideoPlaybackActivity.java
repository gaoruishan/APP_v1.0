
package com.cmcc.hyapps.andyou.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.model.Video;
import com.cmcc.hyapps.andyou.util.Log;

public class VideoPlaybackActivity extends BaseActivity implements OnErrorListener,
        OnPreparedListener, OnInfoListener, OnCompletionListener {
    private VideoView mVideoView;
    private Video mVideo;
    private ProgressBar mProgress;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideo = getIntent().getParcelableExtra(Const.EXTRA_VIDEO);
        if (mVideo == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_video_playback);
        // setOrientation();

        mProgress = (ProgressBar) findViewById(R.id.progress);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mVideoView.setOnInfoListener(this);
        }
        mVideoView.setVideoURI(Uri.parse(mVideo.url));
        mVideoView.start();
        mVideoView.requestFocus();

        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(mVideoView);
        mVideoView.setMediaController(vidControl);
    }

    private void setOrientation() {
        if (mVideo.height > mVideo.width) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, R.string.video_play_error, Toast.LENGTH_SHORT).show();
        finish();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("onPrepared");
        mProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.d("onInfo, what=%d, extra=%d", what, extra);
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                mProgress.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mProgress.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }
}
