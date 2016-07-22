/**
 *
 */

package com.cmcc.hyapps.andyou.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.data.LocationDetector;
import com.cmcc.hyapps.andyou.data.LocationDetector.LocationListener;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.AudioIntro;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.ScenicAudio;
import com.cmcc.hyapps.andyou.util.CommonUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.NetUtils;

import java.io.IOException;
import java.util.List;

public class PlaybackService extends Service implements MediaPlayer.OnCompletionListener
        , MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener {

    public static final String ACTION_PLAYSTATE_CHANGED = "com.cmcc.hyapps.andyou.action.PLAYSTATE_CHANGED";
    public static final String ACTION_META_CHANGED = "com.cmcc.hyapps.andyou.action.META_CHANGED";
    public static final String ACTION_LAUNCH_PLAYBACK_ACTIVITY = "com.cmcc.hyapps.andyou.action.PLAYBACK_ACTIIVTY";
    public static final String ACTION_TOGGLE_PLAYBACK_NOTIFICATION = "com.cmcc.hyapps.andyou.action.TOGGLE_PLAYBACK_NOTIFICATION";
    public static final String ACTION_CLOSE_NOTIFICATION = "com.cmcc.hyapps.andyou.action.CLOSE_NOTIFICATION";

    private static final int FLAG_PLAY_NEXT = 1;
    private static final int FLAG_PLAY_PREV = 2;
    private static final int FLAG_PLAY_CURRENT = 3;
    private static final int FLAG_PLAY_BY_TRACK_ID = 4;

    // TODO
    private static final int NOTIFICATION_IMAGE_MAX_DIMEN = 100;

    private static final int MSG_PLAY = 1;
    private static final int MSG_PAUSE = 2;
    private static final int MSG_STOP = 3;
    private static final int MSG_PREPARE = 4;

    private static final int PLAYBACKSERVICE_STATUS = 1;
    private static final int DISTANCE_THRESHOLD_METERS = 45;

    private MediaPlayer mMediaPlayer;
    private Playlist mPlaylist;
    private int mScenicId = -1;
    private String mScenicName = "";

    private boolean mInitialized = false;
    private boolean mPreparing = false;
    private boolean mAutoPlayOnLocationChange = false;
    private LocationDetector mLocationDetector;

    private int mBufferedPercent;
    private ImageContainer mInFlightImageReqeust;

    private int mPlayingType = AudioIntro.TYPE_INTRO;
    private Handler mH = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            Log.d("handle message, what=%d", msg.what);
            if (mMediaPlayer == null) {
                Log.e("MediaPlayer NULL, abandon message");
                return;
            }
            switch (msg.what) {
                case MSG_PREPARE:
                    AudioIntro audio = (AudioIntro) msg.obj;
                    mMediaPlayer.reset();
                    try {
                        mMediaPlayer.setDataSource(audio.url);
                        if (isStreamingDataSource(audio.url)) {
                            if (!NetUtils.isNetworkAvailable(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(),
                                        R.string.network_unavailable,
                                        Toast.LENGTH_SHORT).show();
                                stop();
                            } else {
                                mMediaPlayer.prepareAsync();
                                mPreparing = true;
                                mBufferedPercent = 0;
                            }
                        } else {
                            mMediaPlayer.prepare();
                            play();
                            // local audio resource, pretend already fully
                            // buffered
                            mBufferedPercent = 100;
                        }

                        downloadAudioCoverImage(audio);
                    } catch (Exception e) {
                        if (e instanceof IllegalArgumentException || e instanceof SecurityException
                                || e instanceof IOException || e instanceof IllegalStateException) {
                            Log.e(e, "Error playing url %s", audio);
                            handlePlayError();
                        } else {
                            Log.e(e, "Error");
                            // throw new RuntimeException(e);
                        }
                    }

                    mInitialized = true;
                    break;

                case MSG_PLAY:
                    if (!isPlaying()) {
                        mMediaPlayer.start();
                    }
                    // trigger ui refresh
                    notifyChange(ACTION_META_CHANGED);
                    break;

                case MSG_PAUSE:
                    if (isPlaying()) {
                        mMediaPlayer.pause();
                        notifyChange(ACTION_PLAYSTATE_CHANGED);
                        // stopForeground(false);
                    }
                    break;

                case MSG_STOP:
                    if (mInitialized) {
                        gotoIdle();
                    }
                    break;
            }
            updateNotification();
            super.handleMessage(msg);
        }

    };

    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onReceivedLocation(Location loc) {
            playOnLocationChange(loc);
        }

        @Override
        public void onLocationTimeout() {
            Log.d("onLocationTimeout");
        }

        @Override
        public void onLocationError() {
            Log.d("onLocationError");
        }
    };

    private boolean isStreamingDataSource(String path) {
        Uri uri = Uri.parse(path);
        return "http".equals(uri.getScheme()) || "https".equals(uri.getScheme());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    @Override
    public void onCreate() {
        initPlayer();
        mPlaylist = new Playlist();
        mLocationDetector = new LocationDetector(this);

        super.onCreate();
    }

    private void initPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mPreparing = false;
        mBufferedPercent = 0;
        mInitialized = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        String action = intent.getAction();
        Log.d("onStartCommand, action=%s", action);
        if (ACTION_TOGGLE_PLAYBACK_NOTIFICATION.equals(action)) {
            if (isPlaying()) {
                pause();
            } else {
                play();
            }
        } else if (ACTION_CLOSE_NOTIFICATION.equals(action)) {
            // pause();
            stopForeground(true);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void addAudioTracks(List<ScenicAudio> audio, int scenicId, String scenicName,
                               boolean clearOthers) {
        if (clearOthers) {
            mPlaylist.clear();
        }
        mScenicId = scenicId;
        mScenicName = scenicName;
        mPlaylist.addToPlaylist(audio);
    }

    public void play() {
        if (mPlaylist.isEmpty()) {
            Log.d("Empty playlist, stop");
            return;
        }
        if (mPreparing) {
            return;
        }

        if (!mMediaPlayer.isPlaying()) {
            playInternal(FLAG_PLAY_CURRENT, false, -1);
        }
    }

    public void pause() {
        if (mPreparing) {
            return;
        }

        mH.obtainMessage(MSG_PAUSE).sendToTarget();
    }

    public void play(int id) {
        Log.d("FLAG_PLAY_BY_TRACK_ID, id=%d", id);
        playInternal(FLAG_PLAY_BY_TRACK_ID, true, id);
    }

    public void setCurrentPlayType(int type) {
        mPlayingType = type;
    }

    public int getCurrentPlayType() {
        return mPlayingType;
    }

    public void seek(int pos) {
        if (pos < 0) {
            pos = 0;
        }
        if (pos > duration()) {
            pos = duration();
        }
        mMediaPlayer.seekTo(pos);
    }

    public void stop() {
        mH.obtainMessage(MSG_STOP).sendToTarget();
    }

    public void next() {
        playNext(true);
    }

    public void prev() {
        playPrevious();
    }

    public void setAutoPlayOnLocationChange(boolean on) {
        mAutoPlayOnLocationChange = on;
        if (mAutoPlayOnLocationChange) {
            mLocationDetector.detectLocation(mLocationListener, false, false, 10000);
        } else {
            mLocationDetector.close();
        }
    }

    public boolean isAutoPlayOnLocationChangeOn() {
        return mAutoPlayOnLocationChange;
    }

    private void playOnLocationChange(Location newLocation) {
        if (mPlaylist.isEmpty()) {
            return;
        }

        List<ScenicAudio> playlist = mPlaylist.getPlaylist();
        int scenicId = -1;
        double lastDistance = DISTANCE_THRESHOLD_METERS;
        for (ScenicAudio scenicAudio : playlist) {
            if (scenicAudio.location == null || !scenicAudio.location.isValid()) {
                continue;
            }

            double distance = AMapUtils.calculateLineDistance(scenicAudio.location.toLatLng(), newLocation.toLatLng());
            Log.d("playOnLocationChange， distance=%d", (int) distance);
            // if (distance < 1000) {
            // Toast.makeText(
            // getApplicationContext(),
            // "currentPlayingSpot()=" + currentPlayingSpot() +
            // ",spotId=" + scenicAudio.spotId + ", lan="
            // + scenicAudio.location.latitude
            // + ", long=" + scenicAudio.location.longitude + ",distance="
            // + distance, Toast.LENGTH_LONG).show();
            // }
            if (distance < lastDistance) {
                lastDistance = distance;
                scenicId = scenicAudio.spotId;
            }
        }

        if (scenicId > 0 && currentPlayingSpot() != scenicId) {
            Log.d("Into location scope, start to play");
            // Toast.makeText(getApplicationContext(), "Start auto play",
            // Toast.LENGTH_SHORT).show();
            play(scenicId);
            mylocationSuccessListener.onLocationSuccess(scenicId);
        }
    }

    private void playNext(boolean force) {
        playInternal(FLAG_PLAY_NEXT, force, -1);
    }

    private void playPrevious() {
        playInternal(FLAG_PLAY_PREV, false, -1);
    }

    private void playInternal(int flag, boolean force, int args) {
        int action = -1;
        AudioIntro current = mPlaylist.currentTrack();
        AudioIntro audioToPlay = null;

        switch (flag) {
            case FLAG_PLAY_NEXT:
                audioToPlay = mPlaylist.nextTrack(force);
                action = MSG_PREPARE;
                break;

            case FLAG_PLAY_PREV:
                audioToPlay = mPlaylist.previousTrack();
                action = MSG_PREPARE;
                break;

            case FLAG_PLAY_CURRENT:
                audioToPlay = mPlaylist.currentTrack();
                if (audioToPlay != null) {
                    action = MSG_PLAY;
                } else {
                    audioToPlay = mPlaylist.nextTrack(force);
                    action = MSG_PREPARE;
                }
                break;

            case FLAG_PLAY_BY_TRACK_ID:
                if (Const.DEBUG) {
                    CommonUtils.assertTrue(mPlaylist.gotoTrack(args));
                } else {
                    mPlaylist.gotoTrack(args);
                }
                audioToPlay = mPlaylist.currentTrack();
                action = MSG_PREPARE;
                break;

            default:
                break;
        }

        if (audioToPlay != null) {
            if (current != null) {
                current.highlight = false;
            }
            audioToPlay.highlight = true;
            notifyChange(ACTION_META_CHANGED);

            if (flag != FLAG_PLAY_BY_TRACK_ID && flag != FLAG_PLAY_CURRENT
                    && audioToPlay.type != mPlayingType) {
                playInternal(flag, force, args);
                return;
            }

            mH.obtainMessage(action, audioToPlay).sendToTarget();
            Log.d("Playing track:action=%d, track= %s", action, audioToPlay);
        } else {
            Log.d("Nothing to play, goto idle");
            gotoIdle();
        }
    }

    private void gotoIdle() {
        // mMediaPlayer.seekTo(0);
        Log.d("gotoIdle");
        mMediaPlayer.stop();
        notifyChange(ACTION_PLAYSTATE_CHANGED);
        setAutoPlayOnLocationChange(false);
        stopForeground(true);
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public int duration() {
        // TODO: hack
        if (mInitialized && !mPreparing && mMediaPlayer.getDuration() < 60 * 60 * 1000) {
            return mMediaPlayer.getDuration();
        } else {
            AudioIntro audio = mPlaylist.currentTrack();
            if (audio != null) {
                return audio.duration;
            }
        }
        return 0;
    }

    public long position() {
        if (mInitialized) {
            if (mMediaPlayer.getCurrentPosition() <= duration()) {
                return mMediaPlayer.getCurrentPosition();
            }
        }
        return 0;
    }

    public int bufferedPercent() {
        return mBufferedPercent;
    }

    public void setRepeatMode(int mode) {

    }

    public int getRepeatMode() {
        return 0;
    }

    public AudioIntro currentTrack() {
        return mPlaylist.currentTrack();
    }

    public int currentPlayingSpot() {
        return mPlaylist.currentPlayingSpot();
    }

    public List<ScenicAudio> getPlaylist() {
        return mPlaylist.getPlaylist();
    }

    public int getScenicId() {
        return mScenicId;
    }

    private void notifyChange(String action) {
        Log.d("notifyChange, action=%s", action);
        // mPlaylist.dump();
        Intent i = new Intent(action);
        // i.putExtra(Const.EXTRA_CURRENT_PLAYING_TRACK,
        // mPlaylist.currentTrack().url);
        // i.putExtra(Const.EXTRA_CURRENT_PLAYING_SPOT,
        // mPlaylist.currentPlayingSpot());
        // i.putExtra(Const.EXTRA_IS_PLAYING, isPlaying());
        sendStickyBroadcast(i);
    }

    private void updateNotification() {
        AudioIntro audioIntro = mPlaylist.currentTrack();
        if (audioIntro == null) {
            return;
        }

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.playback_notification);
        views.setTextViewText(R.id.track_title,
                mScenicName + getResources().getString(R.string.listen));
        if (audioIntro.imageBitmap != null) {
            views.setImageViewBitmap(R.id.track_image, audioIntro.imageBitmap);
        }
        int playButton = isPlaying() ? R.drawable.notification_pause
                : R.drawable.notification_play;
        views.setImageViewResource(R.id.play_pause, playButton);

        ComponentName service = new ComponentName(this, PlaybackService.class);
        Intent playPause = new Intent(ACTION_TOGGLE_PLAYBACK_NOTIFICATION);
        playPause.setComponent(service);
        views.setOnClickPendingIntent(R.id.play_pause,
                PendingIntent.getService(this, 0, playPause, 0));

        Intent close = new Intent(ACTION_CLOSE_NOTIFICATION);
        close.setComponent(service);
        views.setOnClickPendingIntent(R.id.close, PendingIntent.getService(this, 0, close, 0));

        Notification status = new Notification();
        status.contentView = views;
        status.flags |= Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.ic_launcher;
        status.contentIntent = PendingIntent
                .getActivity(
                        this,
                        0,
                        new Intent(ACTION_LAUNCH_PLAYBACK_ACTIVITY)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        startForeground(PLAYBACKSERVICE_STATUS, status);
    }

    @Override
    public void onDestroy() {
        // Handler should be released before mMediaPlayer.
        mH.removeCallbacksAndMessages(null);

        Log.d("PlaybackService destroyed");
        if (mInFlightImageReqeust != null) {
            mInFlightImageReqeust.cancelRequest();
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("onError, what=%d, extra=%d", what, extra);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                handlePlayError();
                return true;
            default:
                break;
        }

        // TODO: pretend we handle the error
        return true;
    }

    private void handlePlayError() {
        mMediaPlayer.release();
        initPlayer();
        // Do not try to play again
        // playNext(false);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("onCompletion, play next");
        // Don't auto play
        // playNext(false);
        notifyChange(ACTION_PLAYSTATE_CHANGED);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (percent < 100) {
            Log.d("onBufferingUpdate:%d", percent);
        }
        mBufferedPercent = percent;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("onPrepared");
        mPreparing = false;
        play();
        Log.d("mp.getDuration()=%d", mp.getDuration());
    }

    private void downloadAudioCoverImage(final AudioIntro audio) {
        if (TextUtils.isEmpty(audio.imageUrl)) {
            return;
        }

        if (mInFlightImageReqeust != null) {
            mInFlightImageReqeust.cancelRequest();
        }

        Log.d("Downloading audio image from %s", audio.imageUrl);
        mInFlightImageReqeust = RequestManager.getInstance().getImageLoader()
                .get(audio.imageUrl, new ImageListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do nothing
                    }

                    @Override
                    public void onResponse(ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            audio.imageBitmap = response.getBitmap();
                            updateNotification();
                            notifyChange(ACTION_META_CHANGED);
                        }
                    }
                });
    }
    //定位成功，景区内自动定位，找到可以播放的audio
    public interface LocationSuccessListener {
        void onLocationSuccess(int scenicSpotId);
    }
    private LocationSuccessListener mylocationSuccessListener;
    public void setLocationSuccessListener(LocationSuccessListener l){
        mylocationSuccessListener = l;
    }


}
