
package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.LocationDetector;
import com.cmcc.hyapps.andyou.data.LocationDetector.LocationListener;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.ConstTools;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class MyLocationMapActivity extends BaseActivity implements
        OnClickListener , LocationSource, AMapLocationListener {
    protected Context mContext;
    @InjectView(R.id.bmap)
    protected MapView mMapView;
    @InjectView(R.id.show_my_location)
    View mShowMyLocation;

    private LocationDetector mLocationDetector;
    protected Location mLocation;
    public AMap aMap;
    public LocationSource.OnLocationChangedListener mListener;
    public LocationManagerProxy mAMapLocationManager;
    public AMapLocation myLocation  ;



    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onReceivedLocation(Location location) {
            mLocation = location;
            onLocationUpdated(mLocation);
        }

        @Override
        public void onLocationTimeout() {

        }

        @Override
        public void onLocationError() {

        }
    };

    protected void onLocationUpdated(Location location) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.scenic_spots_map);
        ButterKnife.inject(this);
        mShowMyLocation.setOnClickListener(this);
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.getMap().getUiSettings().setZoomControlsEnabled(false);
        }
        initActionBar();
        if (aMap == null)  aMap = mMapView.getMap();
        initLoaction();

    }

    @Override
    protected void onStart() {
        mLocationDetector = new LocationDetector(this);
        mLocationDetector.detectLocation(mLocationListener, false, false);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mLocationDetector.close();
        super.onStop();
    }

    protected abstract void initActionBar();

    @Override
    protected void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_my_location: {
                /*if (mLocation != null && mLocation.isValid()) {
                    mMapView.getMap().animateCamera(CameraUpdateFactory.newLatLng(mLocation.toLatLng()));
                    mMapView.getMap().animateCamera(CameraUpdateFactory.zoomTo(12));
                }*/
                if (null != myLocation) {
                    LatLng marker1 = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
                    mListener.onLocationChanged(myLocation);
                }
                break;
            }

            default:
                break;
        }

    }


    /**
     * 设置一些amap的属性
     */
    private void initLoaction() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.myposition));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 180));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);

    }
    /**
     * 定位成功后回调函数
     */

    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            myLocation = aLocation;
            ConstTools.myCurrentLoacation.latitude = aLocation.getLatitude();
            ConstTools.myCurrentLoacation.longitude = aLocation.getLongitude();
        }
    }

    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }


    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            /*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, ConstTools.MAP_POSITION_INTERVAL, 10, this);
        }
    }
}
