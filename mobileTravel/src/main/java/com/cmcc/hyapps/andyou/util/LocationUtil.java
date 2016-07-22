package com.cmcc.hyapps.andyou.util;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;

/**
 * Created by Administrator on 2015/6/9.
 */
public class LocationUtil {

    //经纬度
    private static double currentlatitude;
    private static double currentlongitude;

    public  String getCurrentAddress() {
        return currentAddress;
    }

    private static String currentAddress;
    private LocationManagerProxy mAMapLocationManager;
    private AMap aMap;

    private static LocationUtil mLocation = new LocationUtil(AppUtils.getContext());

    private LocationUtil(Context context) {
        aMap = new AMap();
        mAMapLocationManager = LocationManagerProxy.getInstance(context);
        mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 5000, 10, aMap);
//        mAMapLocationManager.requestLocationUpdates(
//                LocationProviderProxy.AMapNetwork, 5000, 10, aMap);
    }

    public static LocationUtil getInstance(Context context) {
        return mLocation;
    }

    public String getDistance(double latitude, double longitude) {
        LatLng start = new LatLng(latitude, longitude);
        LatLng end = new LatLng(currentlatitude, currentlongitude);
        float my_distance = AMapUtils.calculateLineDistance(start, end);
        String str_distance;
        if (my_distance > 1000) {
            str_distance = (int) (my_distance / 1000) + "Km";
        } else str_distance = my_distance + "m";
        return str_distance;
    }

    public double getLatitude(){
        return currentlatitude;
    }

    public double getLongitude(){
        return currentlongitude;
    }
    public String getAddress(){
        return "";
    }


    class AMap implements AMapLocationListener {

        @Override
        public void onLocationChanged(Location location) {
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
            currentlatitude = location.getLatitude();
            currentlongitude = location.getLongitude();
            currentAddress = location.getAddress();
            Log.i("Location", currentlatitude + "," + currentlongitude);
        }
    }

    public void destroyAMapLocationListener() { //取消经纬度监听
        if (mAMapLocationManager != null ){
            mAMapLocationManager.removeUpdates(aMap);
            mAMapLocationManager.destory();
            mAMapLocationManager = null;
        }
    }
    public void getAddressName(Context context,LatLonPoint latLonPoint,GeocodeSearch.OnGeocodeSearchListener listener){
        GeocodeSearch geocoderSearch = new GeocodeSearch(context);
        geocoderSearch.setOnGeocodeSearchListener(listener);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }
}
