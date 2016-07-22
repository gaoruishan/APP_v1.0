package com.cmcc.hyapps.andyou.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.QHNavigation;
import com.cmcc.hyapps.andyou.navi.TTSController;
import com.cmcc.hyapps.andyou.util.GPSManager;
import com.cmcc.hyapps.andyou.util.LocationUtil;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

/**
 * Created by bingbing on 2015/10/12.
 */
public class NavigationDetailActivity extends BaseActivity implements AMapNaviViewListener,AMapNaviListener,TTSController.NavigationDetail {
    private String TAG = "NavigationDetailActivity";
    private CircularProgressBar loading_progress;
    private AMapNaviView mAmapAMapNaviView;

    //起点终点列表
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
    //    private MyAMapNaviListener myAMapNaviListener;
    //是否为模拟导航
    private boolean mIsEmulatorNavi = false;
    //记录有哪个页面跳转而来，处理返回键

    private NaviLatLng mNaviStart;
    private NaviLatLng mNaviEnd;
    private  TTSController ttsManager;
    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {
        finish();
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onVictory() {
        loading_progress.setVisibility(View.INVISIBLE);
        mAmapAMapNaviView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFaild(int i) {
        Log.d(TAG, "enter--------->onCalculateRouteFailure");
        handleErrorRoute(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_detail);
        openGPS();
        initTTS();
        initView(savedInstanceState);
//        if (GPSManager.isGpsEnable(this)){
//            initData();
//        }
    }

    private void initView(Bundle savedInstanceState){
        loading_progress = (CircularProgressBar) this.findViewById(R.id.loading_progress);
        mAmapAMapNaviView = (AMapNaviView) this.findViewById(R.id.simplenavimap);
        mAmapAMapNaviView.onCreate(savedInstanceState);
        mAmapAMapNaviView.setAMapNaviViewListener(this);
        loading_progress.setVisibility(View.VISIBLE);
        mAmapAMapNaviView.setVisibility(View.INVISIBLE);
        AMapNavi.getInstance(this).setAMapNaviListener(this);
    }
    private void initData() {
        Bundle bundle = getIntent().getBundleExtra("navi_bundle");
        QHNavigation navi_detail = bundle.getParcelable("navi_detail");

        double mLatitude = LocationUtil.getInstance(this).getLatitude();
        double mLongitude = LocationUtil.getInstance(this).getLongitude();

        mNaviStart = new NaviLatLng(mLatitude, mLongitude);
        mNaviEnd = new NaviLatLng(navi_detail.latitude, navi_detail.longitude);

        mStartPoints.add(mNaviStart);
        mEndPoints.add(mNaviEnd);

        AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints,
                mEndPoints, null, AMapNavi.DrivingDefault);
    }
    private void initTTS(){
        ttsManager =  TTSController.getInstance(this);
        ttsManager.init();
        ttsManager.startSpeaking();
        ttsManager.setNavigationDetail(this);
        //  AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);
    }

    private void initNavigation() {
//        mAmapAMapNaviView.onCreate(savedInstanceState);
//        mAmapAMapNaviView.setAMapNaviViewListener(this);
        if (mIsEmulatorNavi) {
            // 设置模拟速度
            AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
            // 开启模拟导航
            AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);
        } else {
            // 开启实时导航
            //   myAMapNaviListener = new MyAMapNaviListener();
            AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);

        }
    }
    //导航回调
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
        AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);
        initNavigation();
    }

    @Override
    public void onCalculateRouteFailure(int i) {
        Log.d(TAG, "onCalculateRouteFailure");
        handleErrorRoute(i);
    }

    @Override
    public void onReCalculateRouteForYaw() {
        Log.d(TAG, "onReCalculateRouteForYaw");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        Log.d(TAG,"onReCalculateRouteForTrafficJam");
    }

    @Override
    public void onArrivedWayPoint(int i) {
        Log.d(TAG,"onArrivedWayPoint");
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAmapAMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAmapAMapNaviView.onResume();
        if (GPSManager.isGpsEnable(this)){
            initData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAmapAMapNaviView.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAmapAMapNaviView.onDestroy();
        TTSController.getInstance(this).stopSpeaking();
        AMapNavi.getInstance(this).stopNavi();
//        // add it to solve more navigation when enter this activity agian
        AMapNavi.getInstance(this)
                .removeAMapNaviListener(ttsManager);
    }

    private void handleErrorRoute(int errorCode){
        loading_progress.setVisibility(View.INVISIBLE);
        mAmapAMapNaviView.setVisibility(View.VISIBLE);
        switch (errorCode){
            case 6:
                ToastUtils.show(NavigationDetailActivity.this,"终点错误");
                break;
            case 3:
                ToastUtils.show(NavigationDetailActivity.this,"起点错误");
                break;
            case 10:
                ToastUtils.show(NavigationDetailActivity.this,"起点没有找到道路");
                break;
            case 11:
                ToastUtils.show(NavigationDetailActivity.this,"终点没有找到道路");
                break;
        }
    }

    private void openGPS(){
        if (!GPSManager.isGpsEnable(this)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                        }
                    });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    NavigationDetailActivity.this.finish();
                }
            } );
            dialog.show();
        }
    }
}
