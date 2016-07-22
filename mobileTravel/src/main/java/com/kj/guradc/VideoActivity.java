
package com.kj.guradc;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.BaseActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI.LiveVideos;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.SoapRequest;
import com.cmcc.hyapps.andyou.data.SoapRequest.Listener;
import com.cmcc.hyapps.andyou.model.LiveServerInfo;
import com.cmcc.hyapps.andyou.util.ScreenUtils;

import org.apache.http.conn.util.InetAddressUtils;
import org.ksoap2.serialization.SoapObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

public class VideoActivity extends BaseActivity implements SurfaceHolder.Callback {
    private static final String PLAY = "PLAY";
    private static final int MSG_PLAY = 1;
    private static ProgressBar mProgress;
    private SurfaceView mVideoView;
    private Uri mVideoUri;
    private static SurfaceHolder sSurfaceHolder;
    public static Bitmap _bitmap = null;
    private static Rect srcRect = new Rect();
    // Rect of the destination canvas to draw to
    private static Rect dstRect = new Rect();
    private static AudioTrack sAudiotrack = null;
    private int mScreenWidth;
    private int mScreenHeight;
    private Context mContext;
    private int mLiveId;

    private static Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_PLAY:
                    mProgress.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }
        };
    };

    static {
        System.loadLibrary("rtspclient");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mVideoUri = getIntent().getParcelableExtra(Const.EXTRA_URI);
//        mLiveId = getIntent().getIntExtra(Const.EXTRA_ID, -1);
//        mLiveId = 156;
//        if (mLiveId <= 0 && mVideoUri == null) {
//            Log.e("VideoActivity", "Video uri is null");
//            finish();
//            return;
//        }
        String url = getIntent().getStringExtra("url");
        mScreenWidth = ScreenUtils.getScreenWidth(mContext);
        mScreenHeight = ScreenUtils.getScreenHeight(mContext);

        setContentView(R.layout.activity_live_video_playback);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        RTSPClientInit(this);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mVideoView = (SurfaceView) findViewById(R.id.video_view);
        sSurfaceHolder = mVideoView.getHolder();
        sSurfaceHolder.addCallback(this);
        RTSPClientPlay(url, 1);
    }
    private void getLiveServerInfo() {
        RequestManager.getInstance().sendGsonRequest(LiveVideos.LIVE_SERVER_URL,
                LiveServerInfo.class,
                new Response.Listener<LiveServerInfo>() {
                    @Override
                    public void onResponse(LiveServerInfo response) {
//                        Log.d("VIDEO", "onResponse, LiveServerInfo=" + response);
                        if (response == null) {
                            return;
                        }
                        SoapObject rpc = new SoapObject(LiveVideos.NAMESPACE, LiveVideos.METHOD_GET_PLAY_URL);
                        rpc.addProperty("accessKey", response.username);
                        rpc.addProperty("accessToken", response.token);
                        rpc.addProperty("puId", mLiveId);
                        rpc.addProperty("streamType", String.valueOf(0));
                        rpc.addProperty("timestamp", response.timestamp);
                        SoapRequest request = new SoapRequest(response.server, mLiveId, rpc,
                                new Listener<String>() {
                                    @Override
                                    public void onResponse(int liveId, String response) {
//                                        Log.d("SoapRequest", ": liveId = " +liveId+", onResponse = " +response);
                                        long a =  RTSPClientPlay(response, 1);
                                        a+=1;
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });

                        RequestManager.getInstance().addSoapRequest(request, "");

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "Error loading live server info:" + error);
                    }
                }, requestTag);
    }

    @Override
    protected void onDestroy() {
        RTSPClientStop();
        RTSPClientDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("Kuloud", "onConfigurationChanged, newConfig:" + newConfig.orientation);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dstRect.top = 0;
            dstRect.left = 0;
            dstRect.right = mScreenHeight;
            dstRect.bottom = mScreenWidth;
        } else {
            dstRect.top = (mScreenHeight - mScreenWidth * mScreenWidth / mScreenHeight) >> 1;
            dstRect.left = 0;
            dstRect.right = mScreenWidth;
            dstRect.bottom = (mScreenHeight + mScreenWidth * mScreenWidth / mScreenHeight) >> 1;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int ori = getResources().getConfiguration().orientation;
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            dstRect.top = 0;
            dstRect.left = 0;
            dstRect.right = mScreenHeight;
            dstRect.bottom = mScreenWidth;
        } else {
            dstRect.top = (mScreenHeight - mScreenWidth * mScreenWidth / mScreenHeight) >> 1;
            dstRect.left = 0;
            dstRect.right = mScreenWidth;
            dstRect.bottom = (mScreenHeight + mScreenWidth * mScreenWidth / mScreenHeight) >> 1;
        }

        Log.d("Kuloud", dstRect.toString());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public static void RTSPClientNotify(String method, int value) {
        Log.d("Kuloud", "method [" + method + "]:" + value);
        if (mHandler != null) {
            if (PLAY.equalsIgnoreCase(method)) {
                if (value >= 200 && value < 300) {
                    // 连接正常，延时1.5s减少图像加载的延时。
                    mHandler.sendEmptyMessageDelayed(MSG_PLAY, 1500);
                }
            }
        }
        // 、***、
        //
        // Notify("SYSTEM","-1","对方挂断");
        // else if(er == -5)
        // Notify("SYSTEM","-4","连接对方出错");
        // else
        // Notify("SYSTEM","-6","创建链接错误");

        // //
        // if (method == "RECORD") {
        // if (value >= 200) {
        // // g_pMainDlg->Record(this,code);
        // }
        // } else if (method == "DESCRIBE") {
        //
        // } else if (method == "PLAY") {
        // if (value >= 200 && value < 300) {
        // }
        // } else if (method == "PAUSE") {
        // if (value >= 200 && value < 300) {
        // }
        // } else if (method == "PLAYPOS") {
        // // if(m_PlaySlider.GetSafeHwnd())
        // // m_PlaySlider.SetPos(code);
        // } else if (method == "SYSTEM") {
        // }
    }

    public static int CreateBitmap(int width, int height) {
        if (_bitmap == null) {
            try {
                android.os.Process
                        .setThreadPriority(android.os.Process.THREAD_PRIORITY_DISPLAY);
            } catch (Exception e) {
                Log.e("ERROR", "CreateBitmap error");
            }
        }

        try {
            _bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        } catch (Exception ex) {
            _bitmap = null;
            // _byteBuffer = null;
            Log.e("ERROR", "CreateBitmap error 2");
        }
        srcRect.top = 0;
        srcRect.left = 0;
        srcRect.right = width;
        srcRect.bottom = height;
        return 0;
    }

    public static void DestroyBitmap() {
        _bitmap = null;
    }

    public static void DrawBitmap(ByteBuffer _byteBuffer) {
        if (_bitmap == null || _byteBuffer == null) {
            Log.e("ERROR", "bitmap or buffer is NULL");
            return;
        }

        _byteBuffer.rewind();
        _bitmap.copyPixelsFromBuffer(_byteBuffer);

        Canvas canvas = sSurfaceHolder.lockCanvas();
        if (canvas != null) {
            // TODO
            /**
             * 相关提示修改 发送提示小时的信息
             */
            // if (iv.getVisibility() == View.VISIBLE) {
            // h.sendEmptyMessage(0);
            // }
            // Add remove alias will add drawing bitmap times.
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
                    Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.drawBitmap(_bitmap, srcRect, dstRect, null);
            sSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public static int CreateAudio(int channels, int samples, int bitspersample) {
        int bufferSizeInBytes = AudioTrack.getMinBufferSize(samples,
                AudioFormat.CHANNEL_OUT_MONO,
                bitspersample == 8 ? AudioFormat.ENCODING_PCM_8BIT
                        : AudioFormat.ENCODING_PCM_16BIT);

        if (sAudiotrack == null) {

            sAudiotrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                    samples, AudioFormat.CHANNEL_OUT_MONO,
                    bitspersample == 8 ? AudioFormat.ENCODING_PCM_8BIT
                            : AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes, AudioTrack.MODE_STREAM);

            // System.out.println("22222");

            // new AudioTrackThread().start();

        } else {

            if (sAudiotrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {

                // System.out.println("111111111");

            } else {

                // System.out.println("33333");

                sAudiotrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                        samples, AudioFormat.CHANNEL_OUT_MONO,
                        bitspersample == 8 ? AudioFormat.ENCODING_PCM_8BIT
                                : AudioFormat.ENCODING_PCM_16BIT,
                        bufferSizeInBytes, AudioTrack.MODE_STREAM);

                // new AudioTrackThread().start();

            }

        }

        return 0;
    }

    public static int WriteAudio(ByteBuffer data, int len) {

        if (sAudiotrack == null) {
            return -1;
        }
        if (sAudiotrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            sAudiotrack.play();
        }
        byte[] da = new byte[len];
        data.rewind();
        data.get(da);
        sAudiotrack.write(da, 0, len);

        return 0;

    }

    public static void DestroyAudio() {
        if (sAudiotrack != null) {
            sAudiotrack.stop();

            sAudiotrack.release();

            // am.setMode(AudioManager.MODE_NORMAL);
            sAudiotrack = null;
        }
    }

    public String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
                    .getNetworkInterfaces(); mEnumeration.hasMoreElements();) {
                NetworkInterface intf = mEnumeration.nextElement();
                for (Enumeration<InetAddress> enumIPAddr = intf
                        .getInetAddresses(); enumIPAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIPAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(inetAddress
                                    .getHostAddress())) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

    public native long RTSPClientInit(Object jobj);

    public native long RTSPClientPlay(String url, int i);

    public native void RTSPClientStop();

    public native long RTSPClientDestroy();

    public native void RTSPClientSnap(String filename);

    public native void RTSPClientPlayURLs(String localurl, String upnpurl,
            String stunurl, String stunip, int stunport);

    public native void RTSPClientFastPlay(float scale);

    public native void RTSPClientSetRequestTimeout(int t);

    public native void RTSPClientPause();

    public native void RTSPClientReplay();

    public native void RTSPClientStartRecord(String filename);

    public native void RTSPClientStopRecord();

    public native void RTSPClientPlayFile(String filename);

    public native void RTSPClientStopPlayFile();

    private native void RTSPClientSetupAudio(int t);
}
