package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.model.BasicScenicData;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class ScenicSpotsMapAct extends BaseActivity  implements OnMarkerClickListener,  View.OnClickListener ,LocationSource,AMapLocationListener {
    private MapView mapView;
	private AMap aMap;
	private LatLonPoint lp = new LatLonPoint(39.908127, 116.375257);// 默认西单广场
    private UiSettings mUiSettings;
    private View mShowMyLocation;
    private Context mContext;

    private ArrayList<BasicScenicData> mSpotsData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        mContext = getApplicationContext();
		setContentView(R.layout.scenic_spots_map_act);
        mSpotsData = getIntent().getParcelableArrayListExtra(Const.EXTRA_SCENIC_SPOTS_DATA);
        if (mSpotsData == null) {
            finish();
            return;
        }
        mapView = (MapView) findViewById(R.id.bmap);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        mShowMyLocation = findViewById(R.id.show_my_location);
        mShowMyLocation.setOnClickListener(this);
        initActionBar();
		init();
        initLoaction();
        addMarkers();
	}
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
            aMap = mapView.getMap();

            if (null!= mSpotsData &&null!=mSpotsData.get(0)) {
                BasicScenicData first_loc = mSpotsData.get(0);
                LatLng marker1 = new LatLng( first_loc.location().latitude,first_loc.location().longitude);
                lp.setLatitude(first_loc.location().latitude);
                lp.setLongitude(first_loc.location().longitude);
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
                aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            }
            mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(false);
            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
            mUiSettings.setCompassEnabled(true);
		}
	}
    private void addMarkers(){
        for (int i = 0;i< mSpotsData.size();i++) {
            BasicScenicData scenicData  = mSpotsData.get(i);
            if (scenicData == null || scenicData.location() == null
                    || !scenicData.location().isValid()) {
                continue;
            }
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(new LatLng(scenicData.location().latitude,scenicData.location().longitude));
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.scenic_spots_location));
            markerOption.title(""/*scenicData.name()*/);
            markerOption.snippet(""+i);
            markerOption.setInfoWindowOffset(0,1000);
            aMap.addMarker(markerOption);
        }
        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
    }
    protected void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(getIntent().getStringExtra(Const.EXTRA_NAME));
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }
    @Override
	public boolean onMarkerClick(Marker marker) {
        int index = Integer.parseInt(marker.getSnippet());
            BasicScenicData data = mSpotsData.get(index);
            Intent intent = new Intent(getApplicationContext(), ListenActivity.class);
            intent.putParcelableArrayListExtra(Const.EXTRA_AUDIO, data.audioIntro());
            intent.putExtra(Const.EXTRA_ID, data.id());
            intent.putExtra(Const.EXTRA_NAME, data.name());
            startActivity(intent);
		return false;
	}
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 *
		 */
		case R.id.show_my_location:
            if(null!=myLocation){
                LatLng marker1 = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
                mListener.onLocationChanged(myLocation);
            }
			break;
		default:
			break;
		}
	}

    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private AMapLocation myLocation = null ;
    /**
     * 设置一些amap的属性
     */
    private void initLoaction() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.myposition));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
        // aMap.setMyLocationType()
    }
    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            myLocation = aLocation;
//            aLocation.setLatitude(39.94231);
//            aLocation.setLongitude(116.336504);
//            mListener.onLocationChanged(aLocation);// 显示系统小蓝点
        }
    }
    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            //屏蔽自动显示自己的坐标
            mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, ConstTools.MAP_POSITION_INTERVAL, 10, this);
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onLocationChanged(android.location.Location location) {
    }
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
    }
}
