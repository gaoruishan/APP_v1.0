package test.grs.com.ims.message;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.littlec.sdk.utils.MyLogger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import test.grs.com.ims.IMApp;

public class VoiceRecorder {
	
	private static final MyLogger logger = MyLogger.getLogger("VoiceRecorder");
	MediaRecorder recorder;
	AudioManager mAudioMgr;
	public static final String PREFIX = "voice";
	public static final String EXTENSION = ".amr";
	private boolean isRecording = false;
	private long startTime;
	public int voice_duration;
	private String voiceFilePath = null;
	private String fileName = null;
	private File file;
	public static final int MAX_DURATION = 180;// 最大录音时长
	public static final int TIME_TO_COUNT_DOWN = 10;// 倒计时开始
	private Handler handler;
	private static MediaPlayer mediaPlayer = null;
	private static SimpleDateFormat mFormat = new SimpleDateFormat("yyyMMddHHmmssSSS");
	private static String playSource = null;
	private static String vmsg_uuid = null;
	public static boolean isPlaying = false;
	public static VoiceRecorder currentPlayListener = null;
	private Context context;
	private MediaPlayerCallback mMediaPlayerCallback;
	private static AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener=null;
	
	static {
		
	}
	public VoiceRecorder(Context context, Handler paramHandler) {
		this.handler = paramHandler;
		this.context = context;
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1){
			mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
				@Override
				public void onAudioFocusChange(int focusChange) {
					if(focusChange == AudioManager.AUDIOFOCUS_LOSS){
						//失去焦点之后的操作
						logger.e( "AUDIOFOCUS_LOSS");
						if(isPlaying){
							stopPlayVoice();
						}
					}else if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
						//获得焦点之后的操作
						logger.e( "AUDIOFOCUS_GAIN");
					}
				}
			};
		}
	}
	
	public String startRecording(String paramString1, Context paramContext) {
		this.file = null;
		try {
			//复位
			discardRecording();
			this.recorder = new MediaRecorder();
			this.recorder.setAudioSource(AudioSource.MIC);
			this.recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			this.recorder.setAudioEncoder(1);
			this.recorder.setAudioChannels(1);
			this.recorder.setAudioSamplingRate(8000);
			this.recorder.setAudioEncodingBitRate(64);
			this.voiceFilePath = getVoiceFilePath();
			this.file = new File(this.voiceFilePath);
			this.recorder.setOutputFile(this.file.getAbsolutePath());
			this.recorder.prepare();
			this.isRecording = true;
			this.recorder.start();
		}
		catch(IOException localIOException) {
			logger.e("voice prepare() failed");
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					while(VoiceRecorder.this.isRecording) {
						Message localMessage = new Message();
						double ratio = (double)recorder.getMaxAmplitude();
						double db = 0;// 分贝
//						if(ratio > 1)
//							db = 20 * Math.log10(ratio);
//						localMessage.arg1 = (int)(db / 10);
						// localMessage.arg1 =
						// (VoiceRecorder.this.recorder.getMaxAmplitude() * 13 /
						// 32767);
						localMessage.arg1 = (int)((14*ratio)/32768);
//						 logger.e("ratio:"+ratio+",db:"+db+",fixed:"+(20*ratio)/32768);
						localMessage.what = MessageActivity.VOICE_REFRESH;
						VoiceRecorder.this.handler.sendMessage(localMessage);
//						Thread.sleep(100L);
						  SystemClock.sleep(100L);
					}
				}
				catch(Exception localException) {
					logger.e("voice " + localException.toString());
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(isRecording) {
					try {
						voice_duration++;
						Log.d("IM", voice_duration + "");
						if(MAX_DURATION - voice_duration < TIME_TO_COUNT_DOWN) {
							Message msg = handler.obtainMessage();
							if(MAX_DURATION - voice_duration < 0) {
								msg.arg1 = stopRecoding();
								msg.what = MessageActivity.VOICE_LONG;
								voice_duration = 0;
								handler.sendMessage(msg);
							}
							else {
								msg.arg1 = MAX_DURATION - voice_duration;
								msg.what = MessageActivity.VOICE_TIP;
								handler.sendMessage(msg);
							}
						}
						Thread.sleep(1000);
					}
					catch(Exception e) {
						
					}
				}
			}
		}).start();
		this.startTime = new Date().getTime();
		logger.d("voice" + " start voice recording to file:" + this.file.getAbsolutePath());
		return this.file == null ? null : this.file.getAbsolutePath();
	}
	
	public interface MediaPlayerCallback {
		void onStart();
		
		void onStop();
	}
	
	public void discardRecording() {
		if(this.recorder != null) {
			try {
				this.recorder.stop();
				this.recorder.release();
				this.recorder = null;
				if((this.file != null) && (this.file.exists()) && (!this.file.isDirectory())) {
					this.file.delete();
				}
			}
			catch(IllegalStateException localIllegalStateException) {
			}
			this.isRecording = false;
		}
	}
	
	public int stopRecoding() {
		if(this.recorder != null) {
			this.isRecording = false;
			this.voice_duration = 0;
			this.recorder.stop();
			this.recorder.release();
			this.recorder = null;
			int i = (int)(new Date().getTime() - this.startTime) / 1000;
			logger.e("voice" + " voice recording finished. seconds:" + i + " file length:" + new File(this.voiceFilePath).length());
			return i;
		}
		return 0;
	}
	
	protected void finalize() throws Throwable {
		super.finalize();
		if(this.recorder != null) {
			this.recorder.release();
		}
	}
	
	public int getAudioTime(String audioPath) {
		int duration = 0;
		File f = new File(audioPath);
		String string = f.getName();
		if(string.contains("_")) {
			try {
				// duration =
				// Integer.parseInt(f.getName().split("_")[1].split(EXTENSION)[0]);
				duration = Integer.parseInt(string.substring(string.lastIndexOf("_") + 1, string.lastIndexOf(".")));
			}
			catch(Exception e) {
				e.printStackTrace();
				duration = 0;
			}
			
		}
		else {
			duration = Math.round(f.length() / (33 * 1000));
			
		}
		return duration;
	}
	
	public String getVoiceFilePath(int length) {
		fileName = file.getAbsolutePath().split(EXTENSION)[0] + "_" + length + EXTENSION;
		
		file.renameTo(new File(fileName));
		return fileName;
	}
	
	public boolean isRecording() {
		return this.isRecording;
	}
	
	public String getVoiceFilePath() {
		fileName = IMConst.GLOBALSTORAGE_DOWNLOAD_PATH + mFormat.format(new Date()) + EXTENSION;
		return fileName;
	}
	
	public void playVoice(String filePath, MediaPlayerCallback callback) {
		Log.e("playVoice",filePath);
		if(!(new File(filePath).exists())) {
			Log.d("IM", "not exits");
			return;
		}
		if(isPlaying) {
			stopPlayVoice();
			if(playSource.equals(filePath)) {
				return;
			}
			else {
				doPlay(filePath, callback);
			}
		}
		else {
			doPlay(filePath, callback);
		}
	}
	
	private void doPlay(String filePath, MediaPlayerCallback callback) {
		mMediaPlayerCallback = callback;
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		
//		if(SettingEntity.getInstance().getWeatherUseSpeakerPlayVoice()) {
			audioManager.setSpeakerphoneOn(true);
			audioManager.setMode(AudioManager.MODE_NORMAL);
//		}
//		else {
//			audioManager.setSpeakerphoneOn(false);
//			audioManager.setMode(AudioManager.MODE_IN_CALL);
//		}
//
		((Activity)context).setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
//		pauseMusic();
		requestAudioFocus();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mMediaPlayerCallback.onStop();
					stopPlayVoice(); // stop animation
				}
				
			});
			isPlaying = true;
			playSource = filePath;
			currentPlayListener = this;
			mediaPlayer.start();
			mMediaPlayerCallback.onStart();
		}
		catch(Exception e) {
			Log.d("IM", e.toString());
		}
	}
	
	public void stopPlayVoice() {
		// stop play voice
		if(mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			mMediaPlayerCallback.onStop();
		}
//		pauseMusic();
		abandonAudioFocus();
		isPlaying = false;
//		PhoneUtils.recoveryAudioManager();
	}
//	private void pauseMusic() {
//		logger.e("关闭系统音乐播放");
//		Intent freshIntent = new Intent();
//		freshIntent.setAction("com.android.music.musicservicecommand.togglepause");
//		freshIntent.putExtra("command", "togglepause");
//		CMChatApplication.getInstance().sendBroadcast(freshIntent);
//	}
	private void requestAudioFocus() {
		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1){
			return;
		}
		
		if (mAudioMgr == null)
			mAudioMgr = (AudioManager) IMApp.mContext
					.getSystemService(Context.AUDIO_SERVICE);
		if (mAudioMgr != null) {
			logger.e( "Request audio focus");
			int ret = mAudioMgr.requestAudioFocus(mAudioFocusChangeListener,
					AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
			if (ret != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				logger.e( "request audio focus fail. " + ret);
			}
		}

	}
	private void abandonAudioFocus() {
		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1){
			return;
		}
		if (mAudioMgr != null) {

			logger.e( "Abandon audio focus");

			mAudioMgr.abandonAudioFocus(mAudioFocusChangeListener);

			mAudioMgr = null;
		}
	}

}
