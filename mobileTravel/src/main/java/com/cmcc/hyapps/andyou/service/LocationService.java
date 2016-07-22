
package com.cmcc.hyapps.andyou.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.cmcc.hyapps.andyou.adapter.AMapLocationListenerAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.TravelApp;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocationService extends Service {
    public static final String ACTION_UPDATE_LOCATION = "com.cmcc.hyapps.andyou.action.UPDATE_LOCATION";
    private Context mContext;
    private TravelApp mApp;
    private LocationManagerProxy mLocationManager;
    private AMapLocationListenerAdapter mLocationCb;
    private final List<LocationListener> mListeners = new CopyOnWriteArrayList<LocationListener>();

    //更新位置信息，发送广播
    private void updateLocation(Location location) {
        mApp.setCurrentLocation(location);
        Intent intent = new Intent(Const.ACTION_LOCATION_UPDATE);
        intent.putExtra(Const.EXTRA_COORDINATES, location);
        mContext.sendBroadcast(intent);
        notifyListners(location);
    }

    public interface LocationListener {
        void onReceivedLocation(Location loc);
        void onLocationError();
    }

    @Override
    public void onCreate() {
        mApp = (TravelApp) getApplication();
        mContext = getApplicationContext();
        Log.d("LocationService onCreate");
        super.onCreate();
        //监听者，定义位置发生变化后执行的动作
        mLocationCb = new AMapLocationListenerAdapter() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation == null) return;
                Location loc = new Location(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                loc.city = aMapLocation.getCity();
                loc.accuracy = aMapLocation.getAccuracy();
                loc.speed = aMapLocation.getSpeed();
                loc.bearing = aMapLocation.getBearing();
                updateLocation(loc);
            }
        };
        mLocationManager = LocationManagerProxy.getInstance(mContext);
        // API定位采用GPS定位方式，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
        mLocationManager.requestLocationData(LocationManagerProxy.GPS_PROVIDER, ConstTools.AUTO_GUIDE_INTERVAL, 10, mLocationCb);

    }

    @Override
    public void onDestroy() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationCb);
            mLocationManager.destroy();
            mLocationManager = null;
        }
        Log.d("Stopping location serivce");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        if (ACTION_UPDATE_LOCATION.equals(action)) {
            Location location = (Location) intent.getParcelableExtra(Const.EXTRA_COORDINATES);
            if (location != null) {
                updateLocation(location);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }

    }

    public void registerListener(LocationListener listener) {
        mListeners.add(listener);
    }

    public void unregisterListener(LocationListener listener) {
        mListeners.remove(listener);
    }

    //调用其他类实现的接口方法，locationListener的接收位置信息后 和 位置信息错误 的方法
    private void notifyListners(Location loc) {
        for (LocationListener locationListener : mListeners) {
            if (loc != null) {
                locationListener.onReceivedLocation(loc);
            } else {
                locationListener.onLocationError();
            }
        }

    }
}
