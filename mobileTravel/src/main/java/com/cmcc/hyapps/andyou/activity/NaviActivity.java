/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.fragment.MapListenDialogFragment;
import com.cmcc.hyapps.andyou.media.PlaybackService;
import com.cmcc.hyapps.andyou.model.AudioIntro;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.RouteSpot;
import com.cmcc.hyapps.andyou.model.Routes;
import com.cmcc.hyapps.andyou.model.Routes.ScenicRoute;
import com.cmcc.hyapps.andyou.model.ScenicAudio;
import com.cmcc.hyapps.andyou.model.ScenicDetails;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.NetUtils;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.MapPlayDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kuloud
 */
public class NaviActivity extends MyLocationMapActivity implements AMap
        .OnMarkerClickListener ,View.OnClickListener{
    private static final int TOTAL_COUNT = 0;
    private Location mScenicLocation;
    private ScenicDetails mScenicDetails;
    private Location mLocation;
    private Context mContext;
    private PoiSearch mPoiSearch;
    private PoiSearch.Query mPoiQuery;
    private PoiResult mPoiResult;
    private final List<String> KINDS = new ArrayList<String>();
    private final Map<String, ArrayList<MarkerOptions>> SERVICE_OVERLAYS = new  HashMap<String, ArrayList<MarkerOptions>>();
    private final Map<String, Integer> KIND_ICONS = new HashMap<String, Integer>();
    private CompoundButton mLastSwitch;
    private CompoundButton mRouteSwitch;
    private ArrayList<MarkerOptions> mWalkRouteOverlay = new ArrayList<MarkerOptions>();
    private Map<LatLng, RouteSpot> mRouteSpotMap = new HashMap<LatLng, RouteSpot>();
    private Polyline mRoutePolyline = null;
    private boolean bRouteDisplay = true;
    private ScenicRoute mCurrentRoute;
    private MyPagerAdapter mAdapter;
    private MapPlayDialog secnicMediaDialog;
    public ImageView listen_btn;
    public int current_play_audio_list_index;
    public boolean first_palyed = false;
    public ArrayList<Marker> addedSecnicMarkers = new ArrayList<Marker>();
    private String diastance = "999";
    private List<ScenicAudio> mAudioList;
    private int mScenicId = -1;
    private int mSpotId = -1;
    private String mScenicName;
    private boolean mPaused = true;
    private PlaybackService mPlaybackService;

    public Marker  selectedMarker;
    public BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.map_listening);
    public ArrayList<BitmapDescriptor> waveMarkers = new ArrayList();
    public ArrayList<BitmapDescriptor> defaultwaveMarkers = new ArrayList();
    public BitmapDescriptor wave1 = BitmapDescriptorFactory.fromResource(R.drawable.map_wave_coor_1);
    public BitmapDescriptor wave2 = BitmapDescriptorFactory.fromResource(R.drawable.map_wave_coor_2);
    public BitmapDescriptor wave3 = BitmapDescriptorFactory.fromResource(R.drawable.map_wave_coor_3);
    public BitmapDescriptor wave4 = BitmapDescriptorFactory.fromResource(R.drawable.map_wave_coor_4);
    public BitmapDescriptor defaultMarker = BitmapDescriptorFactory.fromResource(R.drawable.scenic_spots_location);
    private boolean isCurrentAudioAvaible = true;
    private boolean locationPlaying = false;/*边走边听是否在播放音频*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mScenicLocation = (Location) getIntent().getParcelableExtra(Const.EXTRA_COORDINATES);
        mScenicDetails = getIntent().getParcelableExtra(Const.EXTRA_SCENIC_DATA);
        mScenicId = getIntent().getIntExtra(Const.EXTRA_ID, -1);
        if (mScenicId < 0) {
            finish();
            return;
        }
        waveMarkers.add(wave1);waveMarkers.add(wave2);waveMarkers.add(wave3); waveMarkers.add(wave4);
        defaultwaveMarkers.add(defaultMarker); defaultwaveMarkers.add(defaultMarker);
        mContext = getApplicationContext();
        super.onCreate(savedInstanceState);
        RelativeLayout mapContainer = (RelativeLayout) findViewById(R.id.map_container);
        mapContainer.setGravity(Gravity.RIGHT | Gravity.TOP);
        KIND_ICONS.put(Const.KIND_FOOD, R.drawable.restaurant);
        KIND_ICONS.put(Const.KIND_SHOPPING, R.drawable.shopping);
        KIND_ICONS.put(Const.KIND_HOTEL, R.drawable.map_hotel);
        KIND_ICONS.put(Const.KIND_WC, R.drawable.wc);
        KIND_ICONS.put(Const.KIND_TRAFFIC, R.drawable.ic_coordinate);
        KIND_ICONS.put(Const.KIND_SECINI, R.drawable.scenic_spots_location);
        listen_btn = (ImageView)findViewById(R.id.show_listen_btn);
        listen_btn.setVisibility(View.VISIBLE);
        listen_btn.setOnClickListener(this);

        View mapServices = View.inflate(mContext, R.layout.layout_map_service,mapContainer);
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new
                CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String kind = null;
                        switch (buttonView.getId()) {
                            case R.id.show_food:
                                kind = Const.KIND_FOOD;
                                break;
                            case R.id.show_shop:
                                kind = Const.KIND_SHOPPING;
                                break;
                            case R.id.show_hotel:
                                kind = Const.KIND_HOTEL;
                                break;
                            case R.id.show_wc:
                                kind = Const.KIND_WC;
                                break;
                            case R.id.show_traffic:
                                kind = Const. KIND_TRAFFIC;
//                                aMap.setTrafficEnabled(isChecked);
                                break;
                            case R.id.show_route:
                                if (!mWalkRouteOverlay.isEmpty()) {
                                    bRouteDisplay = isChecked;
//                                    refreshMarkers();
                                    getRoutes();
                                }
                                break;
                            default:
                                break;
                        }
                        if (kind != null) {
                            if (mLastSwitch != null && !KINDS.contains(kind)) {
                                mLastSwitch.setChecked(false);
                            }
                            mLastSwitch = buttonView;
                            if (isChecked && !KINDS.contains(kind)) {
                                KINDS.clear();
                                KINDS.add(kind);
                                if (SERVICE_OVERLAYS.containsKey(kind)) {
                                    refreshMarkers();
                                } else {
                                    searchByKind(kind);
                                }
                            } else if (!isChecked && KINDS.contains(kind)) {
                                KINDS.remove(kind);
                                refreshMarkers();
                            }
                        }
                    }
                };
        ((CheckBox) mapServices.findViewById(R.id.show_food))
                .setOnCheckedChangeListener(onCheckedChangeListener);
        ((CheckBox) mapServices.findViewById(R.id.show_shop))
                .setOnCheckedChangeListener(onCheckedChangeListener);
        ((CheckBox) mapServices.findViewById(R.id.show_hotel))
                .setOnCheckedChangeListener(onCheckedChangeListener);
        ((CheckBox) mapServices.findViewById(R.id.show_wc))
                .setOnCheckedChangeListener(onCheckedChangeListener);
        ((CheckBox) mapServices.findViewById(R.id.show_traffic))
                .setOnCheckedChangeListener(onCheckedChangeListener);
        mRouteSwitch = (CompoundButton) mapServices.findViewById(R.id.show_route);
        mRouteSwitch.setOnCheckedChangeListener(onCheckedChangeListener);


        initCamaver();
        initSoundMedia();
        addSecnicMarkers();
    }

    /**
     * 初始化镜头对准景区
     */
    private void initCamaver() {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        if (mScenicLocation != null && mScenicLocation.isValid()) {
            LatLng marker1 = new LatLng(mScenicLocation.latitude, mScenicLocation.longitude);
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
//        aMap.setOnMarkerClickListener(this);
    }

    private void refreshMarkers() {
        mMapView.getMap().clear();
        addSecnicMarkers();

//        selectedMarked= null;

        for (String key : SERVICE_OVERLAYS.keySet()) {
            ArrayList<MarkerOptions> markers = SERVICE_OVERLAYS.get(key);
            if (KINDS.contains(key)) {
                mMapView.getMap().addMarkers(markers, true);
            }
        }
        if (bRouteDisplay && !mWalkRouteOverlay.isEmpty()) {
            List<Marker> markers = mMapView.getMap().addMarkers(mWalkRouteOverlay, true);
            List<LatLng> latLngs = new ArrayList<LatLng>();
            for (Marker marker : markers) {
                marker.setObject(mRouteSpotMap.get(marker.getPosition()));
                latLngs.add(marker.getPosition());
            }
            if (!latLngs.isEmpty()) {
                mRoutePolyline = mMapView.getMap().addPolyline(new
                        PolylineOptions().addAll(latLngs));
            }
            mRouteSwitch.setChecked(true);
        } else if (!bRouteDisplay && mRoutePolyline != null) {
            mRoutePolyline.remove();
            mRouteSwitch.setChecked(false);
        }

        mMapView.getMap().setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        int index = checkIsSecnicMarker(marker);
        if(index!=-1){//点击景区 剔除其他poi点
            current_play_audio_list_index = index;
            first_palyed = false;
            showDialog();
//            play(current_play_secnic);
        }
        else{

        }

        Object obj = marker.getObject();
        if (mScenicDetails != null && obj instanceof RouteSpot) {
            ScenicAudio scenicAudio = mScenicDetails.findScenicAudio(((RouteSpot) obj).id);
            showSpotDetail(scenicAudio);
        }
        return false;
    }

    private void showSpotDetail(ScenicAudio scenicAudio) {
        if (scenicAudio == null) {
            ToastUtils.show(mContext, "当前景区详情暂缺");
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        MapListenDialogFragment newFragment = MapListenDialogFragment
                .newInstance(scenicAudio);
        newFragment.show(ft, "dialog");
    }

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
    class MyPagerAdapter extends PagerAdapter {

        private List<ScenicRoute> mRoutes;

        public void setRoutes(List<ScenicRoute> routes) {
            mRoutes = routes;
        }

        public ScenicRoute getItem(int position) {
            return mRoutes.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mRoutes == null ? 0 : mRoutes.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_route_desc, container,
                    false);
            ScenicRoute route = mRoutes.get(position);
            TextView name = (TextView) view.findViewById(R.id.route_name);

            if (route.recommended) {
                name.setText(getString(R.string.recommend_route));
            } else {
                name.setText(R.string.route);
            }

            ((TextView) view.findViewById(R.id.route_feature)).setText(route.intro);
            ((TextView) view.findViewById(R.id.route_desc)).setText(getString(R.string.route_info,
                    String.valueOf(route.route.size())));
            ((ViewPager) container).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            loadRoutesForPage(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void loadRoutesForPage(int position) {
        ScenicRoute route = mAdapter.getItem(position);
        mCurrentRoute = route;
        if (route.route != null && route.route.size() >= 2) {
            for (RouteSpot routeSpot : route.route) {
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(routeSpot.location.latitude,routeSpot.location.longitude));
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.scenic_spots_location));
                Marker marker = mMapView.getMap().addMarker(markerOption);
                mRouteSpotMap.put(marker.getPosition(), routeSpot);
                mWalkRouteOverlay.add(markerOption);
            }
            refreshMarkers();
        }
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

    private void getRoutes() {
        final String url = ServerAPI.ScenicRoutes.buildUrl(mScenicId);
        Log.d("Loading route from %s", url);
        RequestManager.getInstance().sendGsonRequest(url,
                Routes.class, new Listener<Routes>() {
                    @Override
                    public void onResponse(Routes response) {
                        ArrayList<ScenicRoute> routes;
                        if (response == null || response.routes == null
                                || response.routes.isEmpty()) {
                            Toast.makeText(mContext,
                                    R.string.no_route_available,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            routes = response.routes;
                        }

                        Log.d("getRoutes, routes=%s", response);
                        initRoutePager(routes);
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO
                        Log.e("onErrorResponse, error=%s", error);
                    }
                }, requestTag);
    }

    private void initRoutePager(List<ScenicRoute> routes) {
        mAdapter = new MyPagerAdapter();
        mAdapter.setRoutes(routes);

        ViewPager viewPager = (ViewPager) findViewById(R.id.route_view_pager);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageMargin(ScreenUtils.dpToPxInt(this, 10));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0, true);
//        loadRoutesForPage(0);
    }

    @Override
    protected void onLocationUpdated(Location location) {
        super.onLocationUpdated(location);
    }

    private void searchByKind(String kind) {
        mPoiQuery = new PoiSearch.Query(kind, kind, mScenicLocation.city);
        mPoiSearch = new PoiSearch(mContext, mPoiQuery);
        mPoiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mScenicLocation
                .latitude, mScenicLocation.longitude), 2000));
        mPoiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult result, int rCode) {
                if (rCode == 0) {
                    if (result != null) {
                        mPoiResult = result;
                        List<PoiItem> poiItems = mPoiResult.getPois();

                        String kind = mPoiResult.getQuery().getQueryString();
                        if (poiItems != null && poiItems.size() > 0) {
                            ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
                            for (PoiItem item : poiItems) {
                                MarkerOptions markerOption = new MarkerOptions();
                                markerOption.position(new LatLng(item.getLatLonPoint().getLatitude(),item.getLatLonPoint().getLongitude()));
                                markerOption.icon(BitmapDescriptorFactory.fromResource(KIND_ICONS.get(kind)));
                                markerOption.title(item.getTitle());
                                Marker marker = mMapView.getMap().addMarker(markerOption);
                                markers.add(markerOption);
                            }
                            SERVICE_OVERLAYS.put(kind, markers);
                            refreshMarkers();
                            //景区icon一直都显示
//                            addSecnicMarkers();

                        } else {
                            ToastUtils.show(mContext, R.string.no_result);
                        }
                    } else {
                        ToastUtils.show(mContext, R.string.no_result);
                    }
                } else if (rCode == 27) {
                    ToastUtils.show(mContext, R.string.error_network);
                } else if (rCode == 32) {
                    ToastUtils.show(mContext, R.string.error_key);
                } else {
                    ToastUtils.show(mContext, getString(R.string.error_other) + rCode);
                }
            }

            @Override
            public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

            }
        });
        mPoiSearch.searchPOIAsyn();
    }

    public void showDialog(){
        ScenicAudio scenicspot = mAudioList.get(current_play_audio_list_index);
        AudioIntro audio = scenicspot.audio.get(0);
        if(audio.duration>0)isCurrentAudioAvaible = true;
        else isCurrentAudioAvaible = false;


        double end_lat = 0.0,end_lon = 0.0 ;
        double star_lat = scenicspot.location.latitude;
        double star_lon = scenicspot.location.longitude;
        if (null != myLocation) {
          end_lat = myLocation.getLatitude();
          end_lon = myLocation.getLongitude();
        }
        diastance = ConstTools.getDistance(star_lat, star_lon, end_lat, end_lon);

        if(null==secnicMediaDialog) {
            secnicMediaDialog = new MapPlayDialog(NaviActivity.this, 0,
                    new OnClickListener() {
                        public void onValidClick(View v) {
                            if (mPlaybackService != null) {
                                if (mPaused) {
                                  /*  mPaused = false;
                                    play(current_play_secnic);
                                    mediaContinue();*/
                                        if (!first_palyed) {//第一次点击
                                            if(!isCurrentAudioAvaible){
                                                ToastUtils.show(getApplicationContext(),getString(R.string.map_secnic_no_audio));
                                                return;
                                            }
                                            playAudoList(current_play_audio_list_index);
                                            resetSelectedMarkerIcons(0);
                                            selectedMarker = addedSecnicMarkers.get(current_play_audio_list_index);
                                        }else{
                                            mediaContinue();//继续播放
                                        }
                                        mPaused = false;
                                    }
                                 else {
                                    if (!first_palyed) {
                                        if(!isCurrentAudioAvaible){
                                            ToastUtils.show(getApplicationContext(),getString(R.string.map_secnic_no_audio));
                                            return;
                                        }
                                        playAudoList(current_play_audio_list_index);//当前有正在播放audio,点击换另一个景点
                                        resetSelectedMarkerIcons(0);
                                        selectedMarker = addedSecnicMarkers.get(current_play_audio_list_index);
                                        mPaused = false;
                                    }else{
                                        mediaPause();//暂停播放
                                        mPaused = true;
                                    }

                                }
                            }
                        }
                    },
                    new OnClickListener() {
                        public void onValidClick(View v) {
                            secnicMediaDialog.dismiss();
                            resetSelectedMarkerIcons(1);
                        }
                    },scenicspot.spotName);
            secnicMediaDialog.setCanceledOnTouchOutside(false);//点击其他区域dialog不消失
        }
        secnicMediaDialog.show();
        if(mPaused)
            secnicMediaDialog.updateDialog(audio.imageUrl,scenicspot.spotName,diastance,audio.content,0);
        else
        {
            if (!first_palyed) {//点击新mark
                secnicMediaDialog.updateDialog(audio.imageUrl,scenicspot.spotName,diastance,audio.content,0);
            }
            else
                secnicMediaDialog.updateDialog(audio.imageUrl,scenicspot.spotName,diastance,audio.content,1);
        }
    }

    private void resetSelectedMarkerIcons(int type){
        if(type==0){//默认icon
             checkDefaultMarker();
            if(null!=selectedMarker)selectedMarker.setIcon(defaultMarker);
        }else if(type==1){//动态icon
            if(null==selectedMarker)return;
            checkMarkersIsEmpty();
            selectedMarker.setIcons(waveMarkers);
        }

    }
    private void checkDefaultMarker(){
       if(null==defaultMarker) defaultMarker = BitmapDescriptorFactory.fromResource(R.drawable.scenic_spots_location);
    }
    private void checkMarkersIsEmpty(){
        if(wave1==null)wave1 = BitmapDescriptorFactory.fromResource(R.drawable.map_wave_coor_1);
        if(wave2==null)wave2 = BitmapDescriptorFactory.fromResource(R.drawable.map_wave_coor_2);
        if(wave3==null)wave3 = BitmapDescriptorFactory.fromResource(R.drawable.map_wave_coor_3);
        if(wave4==null){
            wave4 = BitmapDescriptorFactory.fromResource(R.drawable.map_wave_coor_4);
            waveMarkers.add(wave1);waveMarkers.add(wave2); waveMarkers.add(wave3);waveMarkers.add(wave4);
        }
    }


   /*
   add景区icon
   * */
     private void addSecnicMarkers(){
         if(null!=mScenicDetails){
             addedSecnicMarkers.clear();
             for(int i = 0;i<mAudioList.size();i++){
                 ScenicAudio audio = mAudioList.get(i);
                if (audio == null || audio.location == null|| !audio.location.isValid()) {
                    continue;
                }
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(audio.location.latitude,audio.location.longitude));
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.scenic_spots_location));
                markerOption.title("");
                markerOption.snippet(""+audio.spotId);
                markerOption.setInfoWindowOffset(10000,10000);
                Marker secnic = aMap.addMarker(markerOption);
                addedSecnicMarkers.add(secnic);
            }
            aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        }
    }

    private int checkIsSecnicMarker(Marker marker){
        for(int i = 0;i<addedSecnicMarkers.size();i++){
            if(marker.getId()==addedSecnicMarkers.get(i).getId()){
                return i;
            }
        }
        return -1;
    }

    private ServiceConnection mMediaConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlaybackService = ((PlaybackService.LocalBinder) service).getService();
            Log.d("Conncted to playback service");
            if (mAudioList != null) {
                if (mScenicId != mPlaybackService.getScenicId()) {
                    Log.d("Add mAudioList to play %s", mAudioList);
                    mPlaybackService.stop();
                    mPlaybackService.addAudioTracks(mAudioList, mScenicId, mScenicName, true);
                } else {
                    Log.d("Already playing scenic %d, bring activity to front", mScenicId);
                    mAudioList = mPlaybackService.getPlaylist();
                }
            } else {
                mAudioList = mPlaybackService.getPlaylist();
            }
            if (!mPaused) {
                if (mSpotId > 0) {
                    mPlaybackService.play(mSpotId);
                } else {
                    mPlaybackService.play();
                }
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("Disconnected to playback service");
            mPlaybackService = null;
        }
    };
    private void initSoundMedia(){
        mScenicId = mScenicDetails.id;
        mSpotId = getIntent().getIntExtra(Const.EXTRA_SPOT_ID, -1);
        mScenicName = getIntent().getStringExtra(Const.EXTRA_NAME);
        mAudioList = getIntent().getParcelableArrayListExtra(Const.EXTRA_AUDIO);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Intent intent = new Intent(this, PlaybackService.class);
        startService(intent);
        bindService(intent, mMediaConnection, Service.BIND_AUTO_CREATE);
    }

    private void playAudoList(int index) {
        ScenicAudio scenicspot = mAudioList.get(index);
        AudioIntro audio = scenicspot.audio.get(0);
        if (mPlaybackService != null) {
            if (mPlaybackService.isAutoPlayOnLocationChangeOn()) {

            } else {
                mPlaybackService.setCurrentPlayType(audio.type);
                mPlaybackService.play(audio.id());
            }
        }
        secnicMediaDialog.updateDialog(audio.imageUrl,scenicspot.spotName,diastance,audio.content,1);
        first_palyed = true;
    }
    private void mediaPause(){
        if (mPlaybackService != null) {
            if (mPlaybackService.isPlaying()) {
                mPlaybackService.pause();
            }
            secnicMediaDialog.updateDialog(1);
        }
    }
    private void mediaContinue(){
        if (mPlaybackService != null) {
            mPlaybackService.play();
            secnicMediaDialog.updateDialog(0);
        }
    }
    @Override
    protected void onStart() {
        if (mPlaybackService != null && !mPaused) {
            mPaused = false;
            mPlaybackService.play();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ShareManager.getInstance().onEnd();
        if (mPlaybackService != null) {
            mPlaybackService.stop();
        }
        unbindService(mMediaConnection);

        Intent intent = new Intent(this, PlaybackService.class);
        stopService(intent);
        super.onDestroy();
    }
    /*是否景区内*/
    private boolean isInSecnicZone(){
        if(ConstTools.getDistanceFromMe(mScenicDetails.location.latitude,mScenicDetails.location.longitude)<ConstTools.MAP_SECNIC_RADIUS){
            return true;
        }
        else return false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_listen_btn:
                if(isInSecnicZone()){
                    switchAutoGuide();

                    if(locationPlaying){
                        stopAni();
                    }
                }
                else{
                    showDialog();
                    secnicMediaDialog.updateDialog(mPaused?0:1);
//                    ToastUtils.show(getApplicationContext(),getString(R.string.map_auto_guide_fail));
                }

                break;
            case R.id.show_my_location:
                if (null != myLocation) {
                    LatLng marker1 = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
                    mListener.onLocationChanged(myLocation);
                }
                break;
            default:
                break;
        }
    }

    public void switchAutoGuide() {
        if (NetUtils.isNetworkAvailable(getApplicationContext())) {
                if (mPlaybackService != null) {
                    boolean autoOn = mPlaybackService.isAutoPlayOnLocationChangeOn();
                    if (autoOn) {
                        mPlaybackService.setAutoPlayOnLocationChange(false);
                        ToastUtils.show(mContext, R.string.auto_guide_off);

                    } else {
                        mPlaybackService.setAutoPlayOnLocationChange(true);
                        ToastUtils.show(mContext, R.string.auto_guide_on);
                        if (mPlaybackService.currentTrack() != null) {
                            ToastUtils.show(mContext, getString(R.string.auto_guide_hint, mPlaybackService.currentTrack().scenicName));
                        }

                        mPlaybackService.setLocationSuccessListener(new MyLocationSuccessListener());
                    }
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.network_unavailable,Toast.LENGTH_SHORT).show();
        }
    }

    /*边走边听模式，找到景点后的回调监听器。*/
    public class MyLocationSuccessListener implements PlaybackService.LocationSuccessListener{
        @Override
        public void onLocationSuccess(int scenicSpotId) {
            updateFindedSecnic(scenicSpotId);
//          mPlaybackService.play(scenicSpotId);
//          更新scenicId 对应的动态图标。回复原来marker图标。
//          按钮状态改成光盘。
        }
    }

    private void startAni(){

        listen_btn.setImageResource(R.drawable.map_listening_rote);
        AnimationSet animationSet = new AnimationSet(true);
        RotateAnimation rotateAnimation = new RotateAnimation(0f,360f,//旋转角度的变化范围
                Animation.RELATIVE_TO_SELF, 0.5f,       //旋转中心X的位置确定
                Animation.RELATIVE_TO_SELF,0.5f);       //旋转中心Y的位置确定
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(-1);
        animationSet.addAnimation(rotateAnimation);
        listen_btn.startAnimation(animationSet);
    }
    private void stopAni(){
        listen_btn.setImageResource(R.drawable.scenery_detail_play);
        listen_btn.clearAnimation();
    }

    private void updateFindedSecnic(int scenicSpotId){
        for(int i = 0;i<addedSecnicMarkers.size();i++){
            if((""+scenicSpotId).equals(addedSecnicMarkers.get(i).getSnippet())){
                current_play_audio_list_index = i;
                ScenicAudio scenicspot = mAudioList.get(current_play_audio_list_index);
                AudioIntro audio = scenicspot.audio.get(0);
                if(audio.duration>0)isCurrentAudioAvaible = true;
                else isCurrentAudioAvaible = false;

                if(isCurrentAudioAvaible){
                    locationPlaying = true;
                    resetSelectedMarkerIcons(0);
                    selectedMarker = addedSecnicMarkers.get(current_play_audio_list_index);
                    resetSelectedMarkerIcons(1);
                    startAni();
                }
                else{
                    ToastUtils.show(getApplicationContext(),R.string.map_secnic_no_audio);
                }
                return;
            }
        }
    }
    private void initPhoneListener(){
        TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new OnPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }
    /**
     * 电话状态监听.
     * @author stephen
     *
     */
    public class OnPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                case TelephonyManager.CALL_STATE_IDLE:
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (mPlaybackService != null && !mPaused) {
                        mPaused = true;
                        mPlaybackService.pause();
                    }
                    if(null!=secnicMediaDialog){
                        secnicMediaDialog.updateDialog(0);
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

}
