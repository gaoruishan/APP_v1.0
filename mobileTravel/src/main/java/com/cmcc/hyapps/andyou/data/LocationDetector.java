
package com.cmcc.hyapps.andyou.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.service.LocationService;
import com.cmcc.hyapps.andyou.util.LocationUtils;
import com.cmcc.hyapps.andyou.util.Log;

public class LocationDetector {
    private static final int MSG_ON_LOCATION_REQUIRED = 1;
    private static final int MSG_LOCATION_TIMEOUT = 2;
    private static final long GET_LOCATION_TIMEOUT = 8000;

    private Context mContext;

    private boolean mReceiverRegistered;
    private LocationListener mListener;
    private Handler mHandler;
    private boolean mOneshot;

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = (Location) intent.getParcelableExtra(Const.EXTRA_COORDINATES);

            // Remove the timeout message first
            mHandler.removeMessages(MSG_LOCATION_TIMEOUT);

            Message msg = mHandler.obtainMessage(MSG_ON_LOCATION_REQUIRED);
            msg.obj = location;
            msg.sendToTarget();

            if (mOneshot && mReceiverRegistered) {
                mContext.unregisterReceiver(mLocationReceiver);
                mReceiverRegistered = false;
            }
        }
    };

    public LocationDetector(Context mContext) {
        super();
        this.mContext = mContext.getApplicationContext();
        mHandler = new Handler(mContext.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_ON_LOCATION_REQUIRED: {
                        Location location = (Location) msg.obj;
                        mListener.onReceivedLocation(location);
                        break;
                    }

                    case MSG_LOCATION_TIMEOUT: {
                        mListener.onLocationTimeout();
                        break;
                    }

                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    public void detectLocation(LocationListener listener, boolean useCache, boolean oneshot) {
        detectLocation(listener, useCache, oneshot, 1000);
    }

    public void detectLocation(LocationListener listener, boolean useCache, boolean oneshot,
            int scanInterval) {
        mListener = listener;
        mOneshot = oneshot;

        Location location = null;
        if (useCache) {
            location = LocationUtils.getLastKnownLocation(mContext);
        }

        if (location != null && location.isValid()) {
            Message msg = mHandler.obtainMessage(MSG_ON_LOCATION_REQUIRED);
            msg.obj = location;
            msg.sendToTarget();
            Log.d("Using cached location: %s", location);
        } else {
            Intent intent = new Intent(mContext, LocationService.class);
            intent.putExtra(Const.EXTRA_ONESHOT_LOCATION, oneshot);
            intent.putExtra(Const.EXTRA_LOCATION_SCAN_SPAN, scanInterval);
            mContext.startService(intent);

            // Fire a timeout message
            mHandler.removeMessages(MSG_LOCATION_TIMEOUT);
            Message msg = mHandler.obtainMessage(MSG_LOCATION_TIMEOUT);
            mHandler.sendMessageDelayed(msg, GET_LOCATION_TIMEOUT);

            // LocationService is still obtaining location
            if (!mReceiverRegistered) {
                mContext.registerReceiver(mLocationReceiver, new IntentFilter(Const.ACTION_LOCATION_UPDATE));
                mReceiverRegistered = true;
            }
        }

    }

    public void close() {
        if (mReceiverRegistered) {
            mContext.unregisterReceiver(mLocationReceiver);
            mReceiverRegistered = false;
        }

        // TODO: don't stop always, stop if needed
        mContext.stopService(new Intent(mContext, LocationService.class));
        mHandler.removeCallbacksAndMessages(null);
    }

    public interface LocationListener {
        void onReceivedLocation(Location loc);

        void onLocationError();

        void onLocationTimeout();
    }

}
