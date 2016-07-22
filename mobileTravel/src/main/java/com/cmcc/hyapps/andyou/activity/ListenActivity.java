
package com.cmcc.hyapps.andyou.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.app.TravelApp;
import com.cmcc.hyapps.andyou.fragment.AudioSpotListFragment;
import com.cmcc.hyapps.andyou.media.PlaybackService;
import com.cmcc.hyapps.andyou.model.AudioIntro;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.ScenicAudio;
import com.cmcc.hyapps.andyou.task.BlurTask;
import com.cmcc.hyapps.andyou.task.TaskListener;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.FormatUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.NetUtils;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.CommonDialog;
import com.cmcc.hyapps.andyou.widget.CommonDialog.OnDialogViewClickListener;

import java.util.Iterator;
import java.util.List;

/**
 * @author kuloud
 */
public class ListenActivity extends BaseActivity implements OnClickListener, OnGestureListener {

    private static final int UPDATE_UI_INTERVAL = 1000;
    private static final int MSG_UPDATE_UI = 1;
    private static final int PROGRESS_SEEKBAR_MAX = 1000;
    private ActionBar mActionBar;
    private Context mContext;
    private View mBg;
    private DrawerLayout mDrawerLayout;
    private SeekBar mSeekBar;
    private TextView mCurrentTime;
    private TextView mTotalTime;
    private ImageView mPlayPauseButton;
    private ImageView nextButton;
    private ImageView prevButton;
    private AudioSpotListFragment mIntroDrawer;
    private AudioSpotListFragment mAllusionDrawer;
    private View mMenuAutoGuide;
    private TextView mAutoGuideHint;
    private TextView mContent;
    private ImageView mCoverImage;
    private long mDuration;
    private long mPosOverride = -1;
    private long mLastSeekEventTime;
    private List<ScenicAudio> mAudioList;
    private int mScenicId = -1;
    private int mSpotId = -1;
    private String mScenicName;
    private int mSwipeThreshold;
    private int mSwipVelocityThreshold;
    private boolean mPaused;
    private boolean mInitialized;
    private boolean mAudioValid;
    private PlaybackService mPlaybackService;
    private BlurTask mBlurTask;
    private GestureDetector mDetector;
    public ActionDelegate actionDelegate = new ActionDelegate() {
        @Override
        public void play(AudioIntro audio) {
            ListenActivity.this.play(audio);
        }
        @Override
        public void switchAutoGuide() {
            if (NetUtils.isNetworkAvailable(getApplicationContext())) {
                ListenActivity.this.switchAutoGuide();
            } else {
                Toast.makeText(getApplicationContext(), R.string.network_unavailable,Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void play(AudioIntro audio) {
        if (mPlaybackService != null) {
            if (mPlaybackService.isAutoPlayOnLocationChangeOn()) {
                showDownloadDialog(audio);
            } else {
                mPlaybackService.setCurrentPlayType(audio.type);
                mPlaybackService.play(audio.id());
                mDrawerLayout.closeDrawers();
            }
        }
    }

    private void switchAutoGuide() {
        if (mPlaybackService != null) {
            boolean autoOn = mPlaybackService.isAutoPlayOnLocationChangeOn();
            mPlayPauseButton.setEnabled(autoOn);
            nextButton.setEnabled(autoOn);
            prevButton.setEnabled(autoOn);

            if (autoOn) {
                mPlaybackService.setAutoPlayOnLocationChange(false);
                ToastUtils.show(mContext, R.string.auto_guide_off);
                mMenuAutoGuide.setVisibility(View.GONE);
                mAutoGuideHint.setText(null);
            } else {
                mPlaybackService.setAutoPlayOnLocationChange(true);
                ToastUtils.show(mContext, R.string.auto_guide_on);
                mMenuAutoGuide.setVisibility(View.VISIBLE);
                if (mPlaybackService.currentTrack() != null) {
                    mAutoGuideHint.setText(getString(R.string.auto_guide_hint,
                            mPlaybackService.currentTrack().scenicName));
                }
            }
        }
    }

    private void showDownloadDialog(final AudioIntro audio) {
        CommonDialog downloadDialog = new CommonDialog(this);
        downloadDialog.setTitleText(R.string.auto_guide);
        downloadDialog.getDialog().setCancelable(true);
        downloadDialog.getDialog().setCanceledOnTouchOutside(true);
        downloadDialog.setContentText(R.string.auto_guide_prompt);
        downloadDialog.setOnDialogViewClickListener(new OnDialogViewClickListener() {

            @Override
            public void onRightButtonClick() {

            }

            @Override
            public void onLeftButtonClick() {
                switchAutoGuide();
                play(audio);
            }
        });
        downloadDialog.showDialog();
    }

    private ServiceConnection mMediaConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlaybackService = ((PlaybackService.LocalBinder) service).getService();
            Log.d("Conncted to playback service");
            if (mAudioList != null) {
                if (mScenicId != mPlaybackService.getScenicId()) {
                    Log.d("Add mAudioList to play %s", mAudioList);
                    mPlaybackService.stop();
                    mPlaybackService.addAudioTracks(mAudioList, mScenicId, mScenicName, true);
                } else {
                    Log.d("Already playing scenic %d, bring activity to front", mScenicId);
                    mAudioList = mPlaybackService.getPlaylist();
                }
            } else {
                mAudioList = mPlaybackService.getPlaylist();
            }

            if (!mPaused) {
                if (mSpotId > 0) {
                    mPlaybackService.play(mSpotId);
                } else {
                    mPlaybackService.play();
                }
            }

            updateTrackInfo();
            long next = refreshNow();
            queueNextRefresh(next);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("Disconnected to playback service");
            mPlaybackService = null;
        }

    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("action=%s", action);
            if (PlaybackService.ACTION_PLAYSTATE_CHANGED.equals(action)) {
                setPlayPauseButton();
            } else if (PlaybackService.ACTION_META_CHANGED.equals(action)) {
                if (mPlaybackService != null) {
                    mAudioList = mPlaybackService.getPlaylist();
                }

                setPlayPauseButton();
                updateTrackInfo();
                queueNextRefresh(1);
            }
        }

    };

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_UI: {
                    long next = refreshNow();
                    queueNextRefresh(next);
                    break;
                }

                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_listen);

        mScenicId = getIntent().getIntExtra(Const.EXTRA_ID, -1);
        mSpotId = getIntent().getIntExtra(Const.EXTRA_SPOT_ID, -1);
        mScenicName = getIntent().getStringExtra(Const.EXTRA_NAME);
        mAudioList = getIntent().getParcelableArrayListExtra(Const.EXTRA_AUDIO);
        mAudioValid = validatePlaylist();
        if (mAudioValid) {
        } else {
            Toast.makeText(this, R.string.audio_invalid, Toast.LENGTH_SHORT).show();
        }

        initViews();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Intent intent = new Intent(this, PlaybackService.class);
        startService(intent);
        bindService(intent, mMediaConnection, Service.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(PlaybackService.ACTION_META_CHANGED);
        filter.addAction(PlaybackService.ACTION_PLAYSTATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mInitialized = true;

        if (!PreferencesUtils.getBoolean(getApplicationContext(),
                PreferencesUtils.KEY_AUDIO_FIRST_START)) {
            PreferencesUtils.putBoolean(getApplicationContext(),
                    PreferencesUtils.KEY_AUDIO_FIRST_START, true);
            Intent mask = new Intent(activity, ListenMaskActivity.class);
            startActivity(mask);
        }

        ShareManager.getInstance().onStart(activity);
        initPhoneListener();
    }

    private boolean validatePlaylist() {
        if (mAudioList == null) {
            return false;
        }
        boolean isValid = true;
        Iterator<ScenicAudio> it = mAudioList.iterator();
        while (it.hasNext()) {
            ScenicAudio audio = it.next();
            if (audio == null || audio.audio == null || audio.audio.isEmpty()) {
                it.remove();
            } else {
                audio.validate();
            }
        }

        return isValid;
    }

    private void initViews() {
        initActionBar();
        mDetector = new GestureDetector(activity, this);
        mBg = findViewById(R.id.root);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setLongClickable(true);
        mDetector.setIsLongpressEnabled(true);
        ViewConfiguration configuration = ViewConfiguration.get(this);
        mSwipVelocityThreshold = configuration.getScaledMinimumFlingVelocity();
        mSwipeThreshold = configuration.getScaledTouchSlop();
        mIntroDrawer = (AudioSpotListFragment) getFragmentManager().findFragmentById(
                R.id.navigation_drawer_left);
        mIntroDrawer.setType(AudioIntro.TYPE_INTRO);
        mAllusionDrawer = (AudioSpotListFragment) getFragmentManager().findFragmentById(
                R.id.navigation_drawer_right);
        mAllusionDrawer.setType(AudioIntro.TYPE_ALLUSIONS);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mCurrentTime = (TextView) findViewById(R.id.tv_seekbar_left);
        mTotalTime = (TextView) findViewById(R.id.tv_seekbar_right);
        mCoverImage = (ImageView) findViewById(R.id.audio_cover_image);
        mContent = (TextView) findViewById(R.id.content);

        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mPlaybackService == null || mDuration <= 0) {
                    return;
                }

                long bufferPos = mPlaybackService.bufferedPercent() * mDuration / 100;
                mPosOverride = mDuration * seekBar.getProgress() / PROGRESS_SEEKBAR_MAX;
                mPosOverride = mPosOverride < bufferPos ? mPosOverride : bufferPos;
                mPlaybackService.seek((int) mPosOverride);
                seekBar.setProgress((int) (mPosOverride * PROGRESS_SEEKBAR_MAX / mDuration));
                refreshNow();
                mPosOverride = -1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mLastSeekEventTime = 0;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser || mPlaybackService == null || mDuration <= 0) {
                    return;
                }

                long now = SystemClock.elapsedRealtime();
                if ((now - mLastSeekEventTime) > 250) {
                    mLastSeekEventTime = now;
                    mPosOverride = mDuration * seekBar.getProgress() / PROGRESS_SEEKBAR_MAX;
                    refreshNow();
                }
            }
        });

        mSeekBar.setMax(PROGRESS_SEEKBAR_MAX);

        mPlayPauseButton = (ImageView) findViewById(R.id.playpause);
        mPlayPauseButton.setOnClickListener(this);
        prevButton = (ImageView) findViewById(R.id.prev);
        prevButton.setOnClickListener(this);
        nextButton = (ImageView) findViewById(R.id.next);
        nextButton.setOnClickListener(this);
        ImageView shareButton = (ImageView) findViewById(R.id.share);
        shareButton.setOnClickListener(this);

        setPlayPauseButton();
        mMenuAutoGuide = findViewById(R.id.menu_auto_guide);
        mAutoGuideHint = (TextView) findViewById(R.id.tv_auto_guide_listening);
        findViewById(R.id.button_quit_auto_guide).setOnClickListener(this);
    }

    private void initActionBar() {
        mActionBar = (ActionBar) findViewById(R.id.action_bar);
        mActionBar.setBackgroundResource(R.drawable.fg_top_shadow);
        mActionBar.getLeftView()
                .setImageResource(R.drawable.ic_action_bar_back_selecter);
        mActionBar.getLeftView().setOnClickListener(this);
    }

    private void setPlayPauseButton() {
        if (mPlaybackService != null && mPlaybackService.isPlaying()) {
            mPlayPauseButton.setImageResource(R.drawable.listen_stop);
        } else {
            mPlayPauseButton.setImageResource(R.drawable.listen_play);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return mDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left: {
                finish();
                break;
            }

            case R.id.playpause: {
                if (mPlaybackService != null) {
                    if (mPlaybackService.isPlaying()) {
                        mPlaybackService.pause();
                        mPaused = true;
                    } else {
                        mPlaybackService.play();
                        mPaused = false;
                    }
                    refreshNow();
                    // setPlayPauseButton();
                }

                break;
            }
            case R.id.prev: {
                if (mPlaybackService != null) {
                    mPlaybackService.prev();
                }
                break;
            }
            case R.id.next: {
                if (mPlaybackService != null) {
                    mPlaybackService.next();
                }
                break;
            }
            case R.id.share: {
                if (mPlaybackService != null) {
                    final AudioIntro currentAudio = mPlaybackService.currentTrack();
                    if (currentAudio == null) {
                        return;
                    }

                    Location loc = ((TravelApp) getApplication()).getCurrentLocation();
                    if (loc == null) {
                        loc = new Location(0, 0);
                    }

                    ShareManager.getInstance().shareAudio(mScenicId, mSpotId, currentAudio, loc);
                }

                break;
            }

            case R.id.button_quit_auto_guide: {
                switchAutoGuide();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!ShareManager.getInstance().hideBorad()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        if (mPlaybackService != null && !mPaused) {
            mPaused = false;
            mPlaybackService.play();
        }

        updateTrackInfo();
        long next = refreshNow();
        queueNextRefresh(next);
        super.onStart();
    }

    @Override
    protected void onStop() {
        // if (mPlaybackService != null) {
        // mPlaybackService.pause();
        // }
        //
        if (mBlurTask != null) {
            mBlurTask.cancel(true);
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ShareManager.getInstance().onEnd();

        if (mInitialized) {
            unregisterReceiver(mReceiver);
            unbindService(mMediaConnection);
        }
        if (mPlaybackService != null) {
            mPlaybackService.stop();
        }

        Intent intent = new Intent(this, PlaybackService.class);
        stopService(intent);
        super.onDestroy();
    }

    private long refreshNow() {
        if (mPlaybackService == null) {
            return UPDATE_UI_INTERVAL;
        }

        long pos = mPosOverride < 0 ? mPlaybackService.position() : mPosOverride;
        if (pos >= 0 && mDuration > 0) {
            mCurrentTime.setText(FormatUtils.makeTimeString(this, pos / 1000));
            int progress = (int) (PROGRESS_SEEKBAR_MAX * pos / mDuration);

            mSeekBar.setProgress(progress);
            mSeekBar.setSecondaryProgress(mPlaybackService.bufferedPercent() * 10);

            if (mPlaybackService.isPlaying()) {
                mCurrentTime.setVisibility(View.VISIBLE);
            } else {
                // blink the counter
                // int vis = mCurrentTime.getVisibility();
                // mCurrentTime.setVisibility(vis == View.INVISIBLE ?
                // View.VISIBLE
                // : View.INVISIBLE);
                // Do not need to refresh UI
                return -1;
            }
        } else {
            mCurrentTime.setText("0:00");
            mSeekBar.setProgress(PROGRESS_SEEKBAR_MAX);
        }
        return UPDATE_UI_INTERVAL;
    }

    private void queueNextRefresh(long delay) {
        if (delay >= 0) {
            Message msg = mHandler.obtainMessage(MSG_UPDATE_UI);
            mHandler.removeMessages(MSG_UPDATE_UI);
            mHandler.sendMessageDelayed(msg, delay);
        }
    }

    private void updateTrackInfo() {
        if (mPlaybackService == null) {
            return;
        }

        mIntroDrawer.invalidViews(mAudioList);
        mAllusionDrawer.invalidViews(mAudioList);

        final AudioIntro currentAudio = mPlaybackService.currentTrack();
        if (currentAudio == null) {
            return;
        }

        Log.d("updateTrackInfo");
        mDuration = mPlaybackService.duration();
        mTotalTime.setText(FormatUtils.makeTimeString(this, mDuration / 1000));

        ScenicAudio scenicAudio = getCurrentPlayingSpot();
        if (scenicAudio != null) {
            if (mPlaybackService.getCurrentPlayType() == AudioIntro.TYPE_ALLUSIONS) {
                mActionBar.setTitle(currentAudio.title);
            } else {
                if (TextUtils.isEmpty(currentAudio.scenicName)) {
                    if (TextUtils.isEmpty(currentAudio.title)) {
                         mActionBar.setTitle(mScenicName);
                    }
                    else{
                        mActionBar.setTitle(currentAudio.title);
                    }
                } else {
                    mActionBar.setTitle(currentAudio.scenicName);
                }
            }

            List<ScenicAudio> audioList = mPlaybackService.getPlaylist();
            if (audioList != null && audioList.contains(scenicAudio)) {
                int currentPos = audioList.indexOf(scenicAudio);
                boolean hasNext = false;
                for (int i = currentPos + 1; i < audioList.size(); i++) {
                    if (audioList.get(i).validate()) {
                        hasNext = true;
                        break;
                    }
                }
                nextButton.setEnabled(hasNext);
                boolean hasPrev = false;
                for (int i = 0; i < currentPos; i++) {
                    if (audioList.get(i).validate()) {
                        hasPrev = true;
                        break;
                    }
                }
                prevButton.setEnabled(hasPrev);
            }
        } else {
            nextButton.setEnabled(false);
            prevButton.setEnabled(false);
        }

        mContent.setText(currentAudio.content);
        mContent.scrollTo(0, 0);
        if (currentAudio.imageBitmap != null) {
            blurBackground(currentAudio.imageBitmap);
            mCoverImage.setImageBitmap(currentAudio.imageBitmap);
        }

        if (mPlaybackService.isAutoPlayOnLocationChangeOn()) {
            mAutoGuideHint.setText(getString(R.string.auto_guide_hint, currentAudio.scenicName));
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void blurBackground(Bitmap bitmap) {
        if (mBlurTask != null) {
            mBlurTask.cancel(true);
        }
        mBlurTask = new BlurTask(mContext);
        mBlurTask.exe(new TaskListener<Bitmap>() {
            @Override
            public void onResult(final Bitmap result) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mBg.setBackground(new BitmapDrawable(getResources(), result));
                        } else {
                            mBg.setBackgroundDrawable(new BitmapDrawable(getResources(), result));
                        }
                    }
                });
            }

            @Override
            public void onCancel(Bitmap result) {
                // TODO Auto-generated method stub
            }
        }, bitmap);
    }

    private ScenicAudio getCurrentPlayingSpot() {
        int currentPlayingSpotId = mPlaybackService.currentPlayingSpot();
        for (ScenicAudio sceniAudio : mAudioList) {
            if (sceniAudio.spotId == currentPlayingSpotId) {
                return sceniAudio;
            }
        }

        return null;
    }

    public interface ActionDelegate {
        public void play(AudioIntro audio);

        public void switchAutoGuide();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getRawY() > mDrawerLayout.getBottom() || e1.getRawY() < mDrawerLayout.getTop()) {
            // Just handle with area in DrawerLayout
            return false;
        }
        float deltaX = e2.getRawX() - e1.getRawX();
        float deltaY = e2.getRawY() - e1.getRawY();

        if (Math.abs(deltaX) > 2 * Math.abs(deltaY)) {
            if (Math.abs(deltaX) > mSwipeThreshold && Math.abs(velocityX) > mSwipVelocityThreshold) {
                if (deltaX > 0) {
                    onSwipeRight();
                    return true;
                } else {
                    onSwipeLeft();
                    return true;
                }
            }
        }
        return false;
    }

    public void onSwipeRight() {
        if (mDrawerLayout.isDrawerVisible(Gravity.END)) {
            mDrawerLayout.closeDrawer(Gravity.END);
        } else if (!mDrawerLayout.isDrawerVisible(Gravity.START)) {
            mDrawerLayout.openDrawer(Gravity.START);
        }
    }

    public void onSwipeLeft() {
        if (mDrawerLayout.isDrawerVisible(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else if (!mDrawerLayout.isDrawerVisible(Gravity.END)) {
            mDrawerLayout.openDrawer(Gravity.END);
        }
    }


    private void initPhoneListener(){
        TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new OnPhoneStateListener(),PhoneStateListener.LISTEN_CALL_STATE);
    }
    /**
     * 电话状态监听.
     * @author stephen
     *
     */
    public class OnPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                case TelephonyManager.CALL_STATE_IDLE:
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (mPlaybackService != null && !mPaused) {
                        mPaused = true;
                        mPlaybackService.pause();
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }
}
