package com.example.rtspdemo;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import veg.mediaplayer.sdk.MediaPlayer;
import veg.mediaplayer.sdk.MediaPlayer.MediaPlayerCallback;
import veg.mediaplayer.sdk.MediaPlayer.PlayerNotifyCodes;
import veg.mediaplayer.sdk.MediaPlayer.PlayerState;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class PlayerActivity extends Activity implements MediaPlayerCallback {

	String TAG = this.getClass().getName();
	boolean bReopen = true;
	
	String RTSP_URL;
	Timer time = new Timer();
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			reopen();
		}
	};

	// player
	private enum PlayerStatesError {
		None, Disconnected, Eos
	};

	PlayerStatesError player_state_error = PlayerStatesError.None;

	private MediaPlayer player = null;

	private boolean isFileUrl = false;
    public View progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_player);
		
		SharedSettings.getInstance(this).loadPrefSettings();
		SharedSettings.getInstance().savePrefSettings();

        progressBar =  findViewById(R.id.progress);
		player = (MediaPlayer) findViewById(R.id.playerView);
        String path = getIntent().getStringExtra("url");
        if(path== null||path.equals("")){
			Toast.makeText(this, R.string.url_error, Toast.LENGTH_SHORT).show();
            finish();
		}else{
			RTSP_URL = path;
		}

//		time.schedule(task, 0, 5000);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {
    	super.onConfigurationChanged(newConfig);
    	
		boolean bPortrait = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
		Log.v(TAG, "onConfigurationChanged() bPortrait=" + bPortrait);
		
		if (player == null)
			return;
		

//			player.UpdateView(guardedByOrientationIntValue(SharedSettings.getInstance().rendererEnableAspectRatio) == 1);
			Log.v(TAG, "onConfigurationChanged() rendererAspectRatioMode=" + SharedSettings.getInstance().rendererAspectRatioMode);
			player.getConfig().setAspectRatioMode(SharedSettings.getInstance().rendererAspectRatioMode);
			player.getConfig().setAspectRatioZoomModePercent(SharedSettings.getInstance().rendererAspectRatioZoomModePercent);
			//player.UpdateView();
    }

	public void playerConnect(final String url) {
		if (player == null || url.isEmpty())
			return;

		player.Close();

		SharedSettings.getInstance(this).loadPrefSettings();
		SharedSettings sett = SharedSettings.getInstance();

		isFileUrl = isUrlFile(url);

		int connectionProtocol = sett.connectionProtocol;
		int connectionDetectionTime = sett.connectionDetectionTime;
		int connectionBufferingTime = sett.connectionBufferingTime;

		int decoderType = sett.decoderType;
		int rendererType = sett.rendererType;
		int rendererEnableColorVideo = sett.rendererEnableColorVideo;
		int rendererEnableAspectRatio = guardedByOrientationIntValue(sett.rendererEnableAspectRatio);
		int synchroEnable = sett.synchroEnable;
		int synchroNeedDropVideoFrames = sett.synchroNeedDropVideoFrames;

		// Connect and start playback
		player.backgroundColor(Color.BLACK);

		// player.setKey("27f6bee98ba8962f7a65e9b32542869e");
		// player.setStartLiveStreamPosition(30000);
		// player.getConfig().setStartPreroll(1);

		// SharedSettings.getInstance().rendererAspectRatioZoomModePercent =
		// 100;
		player.getConfig().setDataReceiveTimeout(5000);
		player.Open(url, connectionProtocol, connectionDetectionTime,
				connectionBufferingTime, decoderType, rendererType,
				synchroEnable, synchroNeedDropVideoFrames,
				rendererEnableColorVideo, sett.rendererAspectRatioMode,
				isFileUrl ? 1 : player.getConfig().getDataReceiveTimeout(),
				sett.decoderNumberOfCpuCores, this);
	}

	private boolean isUrlFile(String url) {
		return (url != null && !url.isEmpty() && (!url.contains("://") || url
				.contains("file://")));
	}

	@Override
	public int OnReceiveData(ByteBuffer buffer, int size, long pts) {
		// TODO Auto-generated method stub
		return 0;
	}

    private void reopen()
    {
        if (!isPlayerBusy()) {
            if (!isFileUrl) {
                // player_state = PlayerStates.Busy;
                Log.e(TAG, "CONTENT_PROVIDER_ERROR_DISCONNECTED Close.");
//						player.Close();
				playerConnect(RTSP_URL);

				if(player != null && player.getConfig() != null) {
					Log.e(TAG, "Reconnecting: "
							+ player.getConfig().getDataReceiveTimeout());
				}
				else {
					Log.e(TAG, "null pointer");
				}
            }
        }
    }

	@Override
	public int Status(int arg0) {
		// TODO Auto-generated method stub
		PlayerNotifyCodes status = PlayerNotifyCodes.forValue(arg0);
		if (handler == null || status == null)
			return 0;

		if (player != null)
			Log.i(TAG, "Current state:" + player.getState());
		Log.i(TAG, "status: " + status);

		switch (status) {
		// for synchronus process
		// case PLAY_SUCCESSFUL:
		// case VRP_NEED_SURFACE:
		// synchronized (waitOnMe)
		// {
		// Message msg = new Message();
		// msg.obj = status;
		// handler.sendMessage(msg);
		// try
		// {
		// waitOnMe.wait();
		// }
		// catch (InterruptedException e) {}
		// }
		// break;

		case CP_CONNECT_FAILED:
		case PLP_BUILD_FAILED:
		case PLP_PLAY_FAILED:
		case PLP_ERROR:
			// case CP_STOPPED:
			// case VDP_STOPPED:
			// case VRP_STOPPED:
			// case ADP_STOPPED:
			// case ARP_STOPPED:
		case CP_ERROR_DISCONNECTED: {
			player_state_error = PlayerStatesError.Disconnected;
			Message msg = new Message();
			msg.obj = status;
			msg.what = 1;
			handler.removeMessages(mOldMsg);
			mOldMsg = msg.what;
			handler.sendMessage(msg);
			break;
		}

		// for asynchronus process
		default: {
			Message msg = new Message();
			msg.obj = status;
			msg.what = 1;
			handler.removeMessages(mOldMsg);
			mOldMsg = msg.what;
			handler.sendMessage(msg);
		}
		}

		return 0;
	}

	private int mOldMsg = 0;
	private Handler handler = new Handler() {
		String strText = "Status:";

		@Override
		public void handleMessage(Message msg) {
			PlayerNotifyCodes status = (PlayerNotifyCodes) msg.obj;
			Log.e(TAG, "Notify: " + status);

			switch (status) {
			case CP_CONNECT_STARTING:
				// player_state = PlayerStates.Busy;
				player_state_error = PlayerStatesError.None;
				showProgressView();
				break;

			case VRP_NEED_SURFACE:
				// player_state = PlayerStates.Busy;
				// showVideoView();
				// synchronized (waitOnMe) { waitOnMe.notifyAll(); }
				break;

			case PLP_PLAY_SUCCESSFUL:
				// player_state = PlayerStates.ReadyForUse;
				player_state_error = PlayerStatesError.None;
				hideProgressView();
				// updatePlayerPanelControlButtons(isLocked, true,
				// SharedSettings.getInstance().rendererAspectRatioMode);
				break;

			case PLP_CLOSE_STARTING:
				// player_state = PlayerStates.Busy;
				break;

			case PLP_CLOSE_SUCCESSFUL:
				// player_state = PlayerStates.ReadyForUse;
				// hideProgressView();
				// updatePlayerPanelControlButtons(isLocked, false,
				// SharedSettings.getInstance().rendererAspectRatioMode);
				System.gc();
				if (bReopen) {
					new Handler().postDelayed(new Runnable() {
						public void run() {
							reopen();
						}
					}, 5000);
				}
				break;

			case PLP_CLOSE_FAILED:
				// player_state = PlayerStates.ReadyForUse;
				// hideProgressView();
				break;

                case CP_INIT_FAILED:
			case CP_CONNECT_FAILED:
				// player_state = PlayerStates.ReadyForUse;
				player_state_error = PlayerStatesError.Disconnected;

//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        if(player == null)
//                            Log.e(TAG, "player is null");
//
//                        reopen();
//                    }
//                }, 5000);


				// hideProgressView();
				break;

			case PLP_BUILD_FAILED:
				// player_state = PlayerStates.ReadyForUse;
				player_state_error = PlayerStatesError.Disconnected;
				// hideProgressView();
				break;

			case PLP_PLAY_FAILED:
				// player_state = PlayerStates.ReadyForUse;
				player_state_error = PlayerStatesError.Disconnected;
				// hideProgressView();
				break;

			case PLP_ERROR:
				// player_state = PlayerStates.ReadyForUse;
				player_state_error = PlayerStatesError.Disconnected;
				// hideProgressView();
				break;

			case CP_INTERRUPTED:
				// player_state = PlayerStates.ReadyForUse;
				// player_state_error = PlayerStatesError.Disconnected;
				// hideProgressView();
				break;

			// case CONTENT_PROVIDER_ERROR_DISCONNECTED:
			case CP_STOPPED:
			case VDP_STOPPED:
			case VRP_STOPPED:
			case ADP_STOPPED:
			case ARP_STOPPED:
				if (!isPlayerBusy()) {
					// stopProgressTask();
					// player_state = PlayerStates.Busy;
					Log.e(TAG, "AUDIO_RENDERER_PROVIDER_STOPPED_THREAD Close.");
//					player.Close();
				}
				break;

			case PLP_EOS:
				Log.e(TAG, "PLP_EOS: " + isFileUrl + ", " + player.getState());
				// if ((isFileUrl || isModeFile()) && !isPlayerBusy() &&
				// player_state_error != PlayerStatesError.Eos)
				// {
				// player_state_error = PlayerStatesError.Eos;
				// if (isStartedByIntent)
				// {
				// player.Close();
				// onBackPressed();
				// return;
				// }
				//
				// if (!mPanelIsVisible)
				// {
				// if
				// (SharedSettings.getInstance().AllowPlayStreamsSequentially)
				// playNextChannelOrBack();
				// //playNextChannelOrAgain();
				// else
				// {
				// player.Close();
				// onBackPressed();
				// }
				//
				// return;
				// }
				//
				// Log.e(TAG, "CONTENT_PROVIDER_ERROR_DISCONNECTED Close.");
				// player.Close();
				// }

				player.Close();
//                Toast.makeText(PlayerActivity.this, R.string.play_error, Toast.LENGTH_SHORT).show();
//                finish();
				new Handler().postDelayed(new Runnable() {
					public void run() {
						reopen();
					}
				}, 5000);
				break;


			case CP_ERROR_DISCONNECTED:

						new Handler().postDelayed(new Runnable() {
							public void run() {
								reopen();
							}
						}, 5000);

				break;

			default:
//				Toast.makeText(PlayerActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
//				finish();
				break;
			}

			strText += " " + status;
		}
	};

	public boolean isPlayerBusy() {
		if (player != null
				&& (player.getState() == PlayerState.Closing || player
						.getState() == PlayerState.Opening || player.getState() == PlayerState.Opened)) {
			return true;
		}
		return false;
	}

    @Override
    protected void onStart() {
        super.onStart();
		bReopen = true;
        reopen();
    }

    @Override
    protected void onStop() {
        super.onStop();
		bReopen = false;
        player.Close();
    }

    private int guardedByOrientationIntValue(int value)
    {
		boolean bPort = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    	Log.i(TAG, "aspect ratio: " + (bPort ? 1 : value));
    	return (bPort ? 1 : value);
	}  

	@Override
	protected void onDestroy() {
		bReopen = false;
		if (player != null) {
			player.Close();
			player.onDestroy();
		}
		player = null;

		super.onDestroy();
	}

    private void showProgressView(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressView(){
        progressBar.setVisibility(View.GONE);
    }

}
