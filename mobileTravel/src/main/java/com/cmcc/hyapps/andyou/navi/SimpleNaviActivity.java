package com.cmcc.hyapps.andyou.navi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.LocationDetector;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.data.LocationDetector.LocationListener;

import java.util.ArrayList;

/**
 * 
 *导航界面
 * 
 * */
public class SimpleNaviActivity extends Activity implements
        AMapNaviViewListener {

//    private LocationManagerProxy locationProxy = LocationManagerProxy.getInstance(this);
    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onReceivedLocation(Location loc) {

            ConstTools.myCurrentLoacation = new Location(loc.latitude, loc.longitude);
            ConstTools.myCurrentLoacation.city = loc.city;
            ConstTools.myCurrentLoacation.city_en = loc.city_en;

        }

        @Override
        public void onLocationError() {

        }

        @Override
        public void onLocationTimeout() {

        }
    };

    private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态
    //起点终点
    private NaviLatLng mNaviStart = new NaviLatLng(39.989614, 116.481763);
    private NaviLatLng mNaviEnd = new NaviLatLng(39.983456, 116.3154950);
    //起点终点列表
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

	//导航View
	private AMapNaviView mAmapAMapNaviView;
    //是否为模拟导航
	private boolean mIsEmulatorNavi = true;
	//记录有哪个页面跳转而来，处理返回键
	private int mCode=-1;
    private LocationDetector mLocationDetector;

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simplenavi);

        aMap = new AMap();
        mAMapLocationManager = LocationManagerProxy.getInstance(this);
        mAMapLocationManager.requestLocationUpdates(
                LocationProviderProxy.AMapNetwork, 5000, 10, aMap );

        mLocationDetector = new LocationDetector(getApplicationContext());
        mLocationDetector.detectLocation(mLocationListener, true, true);
		Bundle bundle = getIntent().getExtras();
		processBundle(bundle);
		init(savedInstanceState);
	
	}

	private void processBundle(Bundle bundle) {
		if (bundle != null) {
			mIsEmulatorNavi = bundle.getBoolean(Utils.ISEMULATOR, true);
			mCode=bundle.getInt(Utils.ACTIVITYINDEX);
		}
	}

	/**
	 * 初始化
	 * 
	 * @param savedInstanceState
	 */
	private void init(Bundle savedInstanceState) {
		mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.simplenavimap);
        mAmapAMapNaviView.onCreate(savedInstanceState);
		mAmapAMapNaviView.setAMapNaviViewListener(this);
		if (mIsEmulatorNavi) {
			// 设置模拟速度
			AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
			// 开启模拟导航
			AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);

		} else {
			// 开启实时导航
            AMapNavi.getInstance(this).setAMapNaviListener(new MyAMapNaviListener());
			AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);

		}
	}

    class MyAMapNaviListener implements AMapNaviListener{

        @Override
        public void onInitNaviFailure() {

        }

        @Override
        public void onInitNaviSuccess() {

        }

        @Override
        public void onStartNavi(int i) {

        }

        @Override
        public void onTrafficStatusUpdate() {

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
            float speed = aMapNaviLocation.getSpeed();
        }

        @Override
        public void onGetNavigationText(int i, String s) {

        }

        @Override
        public void onEndEmulatorNavi() {

        }

        @Override
        public void onArriveDestination() {

        }

        @Override
        public void onCalculateRouteSuccess() {

        }

        @Override
        public void onCalculateRouteFailure(int i) {

        }

        @Override
        public void onReCalculateRouteForYaw() {

        }

        @Override
        public void onReCalculateRouteForTrafficJam() {

        }

        @Override
        public void onArrivedWayPoint(int i) {

        }

        @Override
        public void onGpsOpenStatus(boolean b) {

        }

        @Override
        public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

        }

        @Override
        public void onNaviInfoUpdate(NaviInfo naviInfo) {

        }
    }

//-----------------------------导航界面回调事件------------------------
	/**
	 * 导航界面返回按钮监听
	 * */
	@Override
	public void onNaviCancel() {
//		Intent intent = new Intent(SimpleNaviActivity.this,
//				SimpleGPSNaviActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(intent);
		finish();
	}
   
	@Override
	public void onNaviSetting() {

	}

	@Override
	public void onNaviMapMode(int arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onNaviTurnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNextRoadClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScanViewButtonClick() {
		// TODO Auto-generated method stub

	}
	/**
	 * 
	 * 返回键监听事件
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(mCode==Utils.SIMPLEROUTENAVI){
//				Intent intent = new Intent(SimpleNaviActivity.this,
//                        SimpleNaviRouteActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivity(intent);
				finish();
				
			}
			else if(mCode==Utils.SIMPLEGPSNAVI){
//				Intent intent = new Intent(SimpleNaviActivity.this,
//                        SimpleGPSNaviActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivity(intent);
				finish();
			}
			else{
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// ------------------------------生命周期方法---------------------------
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mAmapAMapNaviView.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAmapAMapNaviView.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
		mAmapAMapNaviView.onPause();
		AMapNavi.getInstance(this).stopNavi();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mAmapAMapNaviView.onDestroy();

		
	}

	@Override
	public void onLockMap(boolean arg0) {
		  
		// TODO Auto-generated method stub  
		
	}



        //经纬度
        private  double latitude;
        private  double longitude;

        private LocationManagerProxy mAMapLocationManager;
        private AMap aMap;

        class AMap implements AMapLocationListener {

            @Override
            public void onLocationChanged(android.location.Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
            //获取经纬度
            @Override
            public void onLocationChanged(AMapLocation location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                mNaviStart = new NaviLatLng(latitude, longitude);

            }
        }
        public void destroyAMapLocationListener() { //取消经纬度监听
            mAMapLocationManager.removeUpdates(aMap);
            mAMapLocationManager.destory();
            mAMapLocationManager = null;

        }


	

}
