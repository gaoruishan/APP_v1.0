package com.cmcc.hyapps.andyou.activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.gson.annotations.SerializedName;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import java.util.ArrayList;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class HotelMapActivity extends BaseActivity implements LocationSource, AMapLocationListener {
    @InjectView(R.id.bmap)
    MapView mapView;
    private AMap aMap;
    private int mScenicId = -1;
    private UiSettings mUiSettings;
    private ArrayList<Point> points = new ArrayList();
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.poiaroundsearch_activity);
        ButterKnife.inject(this);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        initActionBar();
        init();
        initLoaction();
        points = getIntent().getParcelableArrayListExtra("points");
        addSecnicMarkers();
    }

    private void addSecnicMarkers() {
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (point == null || point.latitude <0|| point.latitude < 1) {
                continue;
            }
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(new LatLng(point.latitude,point.longitude));
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.scenic_spots_location));
            markerOption.title(point.title);

            aMap.addMarker(markerOption);
        }
//        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
    }
    protected void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_navi);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });



    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();

            mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(false);
            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
            mUiSettings.setCompassEnabled(true);
        }
    }
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private AMapLocation myLocation = null;
    /**
     * 设置一些amap的属性
     */
    private void initLoaction() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.myposition));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细

        aMap.setMyLocationStyle(myLocationStyle);

        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        //设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
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
//            LatLng marker1 = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
//            aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
//            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
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


    public static class Point implements Parcelable {
        public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {
            public Point createFromParcel(Parcel in) {return new Point(in); }
            @Override
            public Point[] newArray(int size) {return new Point[size];}
        };

        @SerializedName("latitude")
        public double latitude;
        @SerializedName("longitude")
        public double longitude;
        @SerializedName("action")
        public String title;

        public Point(double lat,double lon,String name)
        {
            latitude = lat;
            longitude = lon;
            title = name;
        }
        public Point(Parcel in) {
            this.latitude = in.readDouble();
            this.longitude = in.readDouble();
            this.title = in.readString();
        }
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(this.latitude);
            dest.writeDouble(this.longitude);
            dest.writeString(this.title);
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public String toString() {
            return "HomeBanner [latitude=" + latitude +", longitude=" + longitude+ ", title=" + title+ "]";
        }
    }

}
