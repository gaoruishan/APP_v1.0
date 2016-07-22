package com.cmcc.hyapps.andyou.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
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
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.Cinema;
import com.amap.api.services.poisearch.Dining;
import com.amap.api.services.poisearch.Hotel;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.amap.api.services.poisearch.Scenic;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.RouteSpot;
import com.cmcc.hyapps.andyou.model.Routes;
import com.cmcc.hyapps.andyou.model.ScenicAudio;
import com.cmcc.hyapps.andyou.model.ScenicDetails;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class NaviMapActivity extends BaseActivity implements
        OnMarkerClickListener, InfoWindowAdapter, OnItemSelectedListener,
        OnPoiSearchListener, OnMapClickListener, OnInfoWindowClickListener,
        android.view.View.OnClickListener, LocationSource, AMapLocationListener {
    @InjectView(R.id.bmap)
    MapView mapView;
    private ProgressDialog progDialog = null;

    private AMap aMap;
    private String deepType = "餐厅";// poi搜索类型
    private int searchType = 0;// 搜索类型
    private int tsearchType = 0;// 当前选择搜索类型
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp = new LatLonPoint(39.908127, 116.375257);// 默认西单广场
    private Marker locationMarker; // 选择的点
    private PoiSearch poiSearch;
    private PoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据
    private Marker detailMarker;// 显示Marker的详情
    private Location mScenicLocation;
    private int mScenicId = -1;
    private UiSettings mUiSettings;
    private View mShowMyLocation;
    private Context mContext;
    private MyPagerAdapter mAdapter;
    private Routes.ScenicRoute mCurrentRoute;
    public static final int FOOD = 0;
    public final static int SHOP = 1;
    public static final int HOTEL = 2;
    public static final int WC = 3;
    public static final int TRAFFIC = 4;
    public static final int ROUTE = 5;
    public int searchState = FOOD;
    protected ScenicDetails mScenicDetailsModel;
    private String myCity = null;
    private ArrayList<Marker> foodMarkerList = new ArrayList();
    private ArrayList<Marker> shopMarkerList = new ArrayList();
    private ArrayList<Marker> hotelMarkerList = new ArrayList();
    private ArrayList<Marker> wcMarkerList = new ArrayList();
    private ArrayList<Marker> tracficMarkerList = new ArrayList();
    private ArrayList<Marker> routeMarkerList = new ArrayList();

    private ArrayList<ScenicAudio> scenicLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.poiaroundsearch_activity);
        ButterKnife.inject(this);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        mShowMyLocation = findViewById(R.id.show_my_location);
        mShowMyLocation.setOnClickListener(this);
        initActionBar();

        mScenicLocation = (Location) getIntent().getParcelableExtra(Const.EXTRA_COORDINATES);
        mScenicDetailsModel = getIntent().getParcelableExtra(Const.EXTRA_SCENIC_DATA);
        myCity = mScenicDetailsModel.cityZh;

        scenicLists = mScenicDetailsModel.audioIntro();


        if (TextUtils.isEmpty(myCity)) myCity = "北京";

        mScenicId = getIntent().getIntExtra(Const.EXTRA_ID, -1);
        if (mScenicId < 0) {
            finish();
            return;
        }

        init();
        initLoaction();

        initBtn();

        getRoutes();
        addSecnicMarkers();
        doSearchQuery("酒店",0,20);

    }

    private void addSecnicMarkers() {
        for (int i = 0; i < scenicLists.size(); i++) {
            ScenicAudio scenicData = scenicLists.get(i);
            if (scenicData == null || scenicData.location == null
                    || scenicData.location.latitude < 1) {
                continue;
            }
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(new LatLng(scenicData.location.latitude, scenicData.location.longitude));
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.scenic_spots_location));
            markerOption.title(scenicData.spotName);

            aMap.addMarker(markerOption);
        }
        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
    }

    private void initBtn() {
        RelativeLayout mapContainer = (RelativeLayout) findViewById(R.id.map_container);
        mapContainer.setGravity(Gravity.RIGHT | Gravity.TOP);
        View mapServices = View.inflate(mContext, R.layout.layout_map_btn, mapContainer);

        View.OnClickListener onCheckedChangeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.show_food:
                        searchState = FOOD;
                        food_box.setSelected(!food_box.isSelected());
                        shop_box.setSelected(false);
                        hotel_box.setSelected(false);
                        wc_box.setSelected(false);
                        traffic_box.setSelected(false);
                        route_box.setSelected(false);
                        if (isHideShowMarkers(food_box.isSelected())) {
                        } else doSearchQuery("餐厅", 20, 1);


                        break;
                    case R.id.show_shop:
                        searchState = SHOP;
                        shop_box.setSelected(!shop_box.isSelected());
                        food_box.setSelected(false);
                        hotel_box.setSelected(false);
                        wc_box.setSelected(false);
                        traffic_box.setSelected(false);
                        route_box.setSelected(false);
                        if (isHideShowMarkers(shop_box.isSelected())) {
                        } else doSearchQuery("商店", 20, 1);

                        break;
                    case R.id.show_hotel:
                        searchState = HOTEL;
                        hotel_box.setSelected(!hotel_box.isSelected());
                        shop_box.setSelected(false);
                        food_box.setSelected(false);
                        wc_box.setSelected(false);
                        traffic_box.setSelected(false);
                        route_box.setSelected(false);
                        if (isHideShowMarkers(hotel_box.isSelected())) {
                        } else doSearchQuery("酒店", 20, 1);

                        break;
                    case R.id.show_wc:
                        searchState = WC;
                        wc_box.setSelected(!wc_box.isSelected());
                        food_box.setSelected(false);
                        hotel_box.setSelected(false);
                        shop_box.setSelected(false);
                        traffic_box.setSelected(false);
                        route_box.setSelected(false);
                        if (isHideShowMarkers(wc_box.isSelected())) {
                        } else doSearchQuery("厕所", 20, 1);

                        break;
                    case R.id.show_traffic:
                        searchState = TRAFFIC;
                        traffic_box.setSelected(!traffic_box.isSelected());
                        food_box.setSelected(false);
                        hotel_box.setSelected(false);
                        wc_box.setSelected(false);
                        shop_box.setSelected(false);
                        route_box.setSelected(false);

                        if (isHideShowMarkers(traffic_box.isSelected())) {
                        } else doSearchQuery("交通", 20, 1);

                        break;
                    case R.id.show_route:
                        searchState = ROUTE;
                        route_box.setSelected(!route_box.isSelected());
                        food_box.setSelected(false);
                        hotel_box.setSelected(false);
                        wc_box.setSelected(false);
                        traffic_box.setSelected(false);
                        shop_box.setSelected(false);

                        if (isHideShowMarkers(route_box.isSelected())) {
                        } else doSearchQuery("路线", 20, 1);
                        break;
                    default:
                        break;
                }
            }
        };
        food_box = ((ImageView) mapServices.findViewById(R.id.show_food));
        food_box.setOnClickListener(onCheckedChangeListener);
        shop_box = ((ImageView) mapServices.findViewById(R.id.show_shop));
        shop_box.setOnClickListener(onCheckedChangeListener);
        hotel_box = ((ImageView) mapServices.findViewById(R.id.show_hotel));
        hotel_box.setOnClickListener(onCheckedChangeListener);
        wc_box = ((ImageView) mapServices.findViewById(R.id.show_wc));
        wc_box.setOnClickListener(onCheckedChangeListener);
        traffic_box = ((ImageView) mapServices.findViewById(R.id.show_traffic));
        traffic_box.setOnClickListener(onCheckedChangeListener);
        route_box = ((ImageView) mapServices.findViewById(R.id.show_route));
        route_box.setOnClickListener(onCheckedChangeListener);
    }

    private ImageView food_box, shop_box, hotel_box, wc_box, traffic_box, route_box;

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
            if (mScenicLocation != null) {
                LatLng marker1 = new LatLng(mScenicLocation.latitude, mScenicLocation.longitude);
                lp.setLatitude(mScenicLocation.latitude);
                lp.setLongitude(mScenicLocation.longitude);
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
                aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            }
            mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(false);
            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
            mUiSettings.setCompassEnabled(true);
        }
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(NaviMapActivity.this);
        aMap.setOnMarkerClickListener(NaviMapActivity.this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(NaviMapActivity.this);
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索中");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String name, int pagesize, int current_page) {
        showProgressDialog();// 显示进度框
        aMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
//		currentPage = 0;
        query = new PoiSearch.Query("", name, myCity);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(50);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页

        searchType = tsearchType;
        //所有poi
        query.setLimitDiscount(false);
        query.setLimitGroupbuy(false);

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new SearchBound(lp, 2000, true));//
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * 点击下一页poi搜索
     */
    public void nextSearch() {
        if (query != null && poiSearch != null && poiResult != null) {
            if (poiResult.getPageCount() - 1 > currentPage) {

                query.setPageNum(currentPage);// 设置查后一页
                poiSearch.searchPOIAsyn();
            } else {

                Toast.makeText(NaviMapActivity.this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 查单个poi详情
     *
     * @param poiId
     */
    public void doSearchPoiDetail(String poiId) {
        if (poiSearch != null && poiId != null) {
            poiSearch.searchPOIDetailAsyn(poiId);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (poiOverlay != null && poiItems != null && poiItems.size() > 0) {
            detailMarker = marker;
            doSearchPoiDetail(poiItems.get(poiOverlay.getPoiIndex(marker))
                    .getPoiId());
        }
        return false;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        Toast.makeText(NaviMapActivity.this, infomation, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * POI详情回调
     */
    @Override
    public void onPoiItemDetailSearched(PoiItemDetail result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 0) {
            if (result != null) {// 搜索poi的结果
                if (detailMarker != null) {
                    StringBuffer sb = new StringBuffer(result.getSnippet());
                    if ((result.getGroupbuys() != null && result.getGroupbuys()
                            .size() > 0)
                            || (result.getDiscounts() != null && result
                            .getDiscounts().size() > 0)) {

                        if (result.getGroupbuys() != null
                                && result.getGroupbuys().size() > 0) {// 取第一条团购信息
                            sb.append("\n团购："
                                    + result.getGroupbuys().get(0).getDetail());
                        }
                        if (result.getDiscounts() != null
                                && result.getDiscounts().size() > 0) {// 取第一条优惠信息
                            sb.append("\n优惠："
                                    + result.getDiscounts().get(0).getDetail());
                        }
                    } else {
                        sb = new StringBuffer("地址：" + result.getSnippet()
                                + "\n电话：" + result.getTel() + "\n类型："
                                + result.getTypeDes());
                    }
                    // 判断poi搜索是否有深度信息
                    if (result.getDeepType() != null) {
                        sb = getDeepInfo(result, sb);
                        detailMarker.setSnippet(sb.toString());
                    } else {
                        Toast.makeText(NaviMapActivity.this,
                                "此Poi点没有深度信息", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                Toast.makeText(NaviMapActivity.this, R.string.no_result, Toast.LENGTH_SHORT).show();
            }
        } else if (rCode == 27) {
            Toast.makeText(NaviMapActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
        } else if (rCode == 32) {
            Toast.makeText(NaviMapActivity.this, R.string.error_key, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(NaviMapActivity.this, getString(R.string.error_other) + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * POI深度信息获取
     */
    private StringBuffer getDeepInfo(PoiItemDetail result,
                                     StringBuffer sbuBuffer) {
        switch (result.getDeepType()) {
            // 餐饮深度信息
            case DINING:
                if (result.getDining() != null) {
                    Dining dining = result.getDining();
                    sbuBuffer
                            .append("\n菜系：" + dining.getTag() + "\n特色："
                                    + dining.getRecommend() + "\n来源："
                                    + dining.getDeepsrc());
                }
                break;
            // 酒店深度信息
            case HOTEL:
                if (result.getHotel() != null) {
                    Hotel hotel = result.getHotel();
                    sbuBuffer.append("\n价位：" + hotel.getLowestPrice() + "\n卫生："
                            + hotel.getHealthRating() + "\n来源："
                            + hotel.getDeepsrc());
                }
                break;
            // 景区深度信息
            case SCENIC:
                if (result.getScenic() != null) {
                    Scenic scenic = result.getScenic();
                    sbuBuffer
                            .append("\n价钱：" + scenic.getPrice() + "\n推荐："
                                    + scenic.getRecommend() + "\n来源："
                                    + scenic.getDeepsrc());
                }
                break;
            // 影院深度信息
            case CINEMA:
                if (result.getCinema() != null) {
                    Cinema cinema = result.getCinema();
                    sbuBuffer.append("\n停车：" + cinema.getParking() + "\n简介："
                            + cinema.getIntro() + "\n来源：" + cinema.getDeepsrc());
                }
                break;
            default:
                break;
        }
        return sbuBuffer;
    }

    /**
     * POI搜索回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        Log.e("---------", "onPoiSearched");

        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始

                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
//						aMap.clear();// 清理之前的图标
                        addMarkers(poiItems);

						/*poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();*/

//						nextButton.setClickable(true);// 设置下一页可点
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(NaviMapActivity.this, R.string.no_result, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(NaviMapActivity.this, R.string.no_result, Toast.LENGTH_SHORT).show();
            }
        } else if (rCode == 27) {
            Toast.makeText(NaviMapActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
        } else if (rCode == 32) {
            Toast.makeText(NaviMapActivity.this, R.string.error_key, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(NaviMapActivity.this, getString(R.string.error_other) + rCode, Toast.LENGTH_SHORT).show();
        }
    }


    private void addMarkers(List<PoiItem> list) {
        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(new LatLng(list.get(i).getLatLonPoint().getLatitude(), list.get(i).getLatLonPoint().getLongitude()));
            switch (searchState) {
                case FOOD:
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));
                    markerOption.title(list.get(i).getTitle()).snippet(list.get(i).getSnippet());
                    Marker marker = aMap.addMarker(markerOption);
                    foodMarkerList.add(marker);
                    break;
                case SHOP:
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.shopping));
                    markerOption.title(list.get(i).getTitle()).snippet(list.get(i).getSnippet());
                    Marker marker1 = aMap.addMarker(markerOption);
                    shopMarkerList.add(marker1);
                    break;
                case HOTEL:
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));
                    markerOption.title(list.get(i).getTitle()).snippet(list.get(i).getSnippet());
                    Marker marker5 = aMap.addMarker(markerOption);
                    hotelMarkerList.add(marker5);
                    break;
                case WC:
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.wc));
                    markerOption.title(list.get(i).getTitle()).snippet(list.get(i).getSnippet());
                    Marker marker2 = aMap.addMarker(markerOption);
                    wcMarkerList.add(marker2);
                    break;
                case TRAFFIC:
//                      markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));
                    markerOption.title(list.get(i).getTitle()).snippet(list.get(i).getSnippet());
                    Marker marker3 = aMap.addMarker(markerOption);
                    tracficMarkerList.add(marker3);
                    break;
                case ROUTE:
//                      markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));
                    markerOption.title(list.get(i).getTitle()).snippet(list.get(i).getSnippet());
                    Marker marker4 = aMap.addMarker(markerOption);
                    routeMarkerList.add(marker4);
                    break;
            }
        }
        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
//    listMarkers.get(0).setIcon(ImagePress(1));
    }
    private boolean isHideShowMarkers(boolean isChecked) {
        switch (searchState) {
            case FOOD:
//               lists = foodMarkerList;
                setMarkerListVisible(foodMarkerList, isChecked);
                setMarkerListVisible(shopMarkerList, false);
                setMarkerListVisible(hotelMarkerList, false);
                setMarkerListVisible(wcMarkerList, false);
                setMarkerListVisible(tracficMarkerList, false);
                setMarkerListVisible(routeMarkerList, false);
                if (foodMarkerList.size() < 1) return false;
                break;
            case SHOP:
//                lists = shopMarkerList;
                setMarkerListVisible(foodMarkerList, false);
                setMarkerListVisible(shopMarkerList, isChecked);
                setMarkerListVisible(hotelMarkerList, false);
                setMarkerListVisible(wcMarkerList, false);
                setMarkerListVisible(tracficMarkerList, false);
                setMarkerListVisible(routeMarkerList, false);
                if (shopMarkerList.size() < 1) return false;
                break;
            case HOTEL:
//                lists = hotelMarkerList;
                setMarkerListVisible(foodMarkerList, false);
                setMarkerListVisible(shopMarkerList, false);
                setMarkerListVisible(hotelMarkerList, isChecked);
                setMarkerListVisible(wcMarkerList, false);
                setMarkerListVisible(tracficMarkerList, false);
                setMarkerListVisible(routeMarkerList, false);
                if (hotelMarkerList.size() < 1) return false;
                break;
            case WC:
//                lists = wcMarkerList;
                setMarkerListVisible(foodMarkerList, false);
                setMarkerListVisible(shopMarkerList, false);
                setMarkerListVisible(hotelMarkerList, false);
                setMarkerListVisible(wcMarkerList, isChecked);
                setMarkerListVisible(tracficMarkerList, false);
                setMarkerListVisible(routeMarkerList, false);
                if (wcMarkerList.size() < 1) return false;
                break;
            case TRAFFIC:
//                lists = tracficMarkerList;
                setMarkerListVisible(foodMarkerList, false);
                setMarkerListVisible(shopMarkerList, false);
                setMarkerListVisible(hotelMarkerList, false);
                setMarkerListVisible(wcMarkerList, false);
                setMarkerListVisible(tracficMarkerList, isChecked);
                setMarkerListVisible(routeMarkerList, false);
                if (tracficMarkerList.size() < 1) return false;
                break;
            case ROUTE:
//              lists =  routeMarkerList;
                setMarkerListVisible(foodMarkerList, false);
                setMarkerListVisible(shopMarkerList, false);
                setMarkerListVisible(hotelMarkerList, false);
                setMarkerListVisible(wcMarkerList, false);
                setMarkerListVisible(tracficMarkerList, false);
                setMarkerListVisible(routeMarkerList, isChecked);
                if (routeMarkerList.size() < 1) return false;
                break;
        }
        return true;
    }
    private void setMarkerListVisible(ArrayList<Marker> lists, boolean isVisible) {
        if (null == lists) return;
        for (int i = 0; i < lists.size(); i++) {
            Marker marker = lists.get(i);
            marker.setVisible(isVisible);
        }
    }
    @Override
    public void onMapClick(LatLng latng) {
        locationMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_coordinate))
                .position(latng).title("点击选择为中心点"));
        locationMarker.showInfoWindow();
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        locationMarker.hideInfoWindow();
        lp = new LatLonPoint(locationMarker.getPosition().latitude,locationMarker.getPosition().longitude);
        locationMarker.destroy();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             */
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
    private LocationSource.OnLocationChangedListener mListener;
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
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        // aMap.setMyLocationType()
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            myLocation = aLocation;
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
    public void activate(LocationSource.OnLocationChangedListener listener) {
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


    class MyPagerAdapter extends PagerAdapter {

        private List<Routes.ScenicRoute> mRoutes;

        public void setRoutes(List<Routes.ScenicRoute> routes) {
            mRoutes = routes;
        }

        public Routes.ScenicRoute getItem(int position) {
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
            Routes.ScenicRoute route = mRoutes.get(position);
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

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

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
        Routes.ScenicRoute route = mAdapter.getItem(position);
        mCurrentRoute = route;
        if (route.route != null && route.route.size() >= 2) {
            ArrayList<RouteSpot> routes = new ArrayList<RouteSpot>();
            routes.addAll(route.route);
            for (RouteSpot s : routes) {
                com.cmcc.hyapps.andyou.util.Log.d("xxxxlat=%d, long=%d:",
                        s.location.toGeoPoint().getLatitudeE6(), s.location
                                .toGeoPoint().getLongitudeE6());
            }

            RouteSpot start = routes.remove(0);
            RouteSpot end = routes.remove(routes.size() - 1);
        }
    }

    private void getRoutes() {
        final String url = ServerAPI.ScenicRoutes.buildUrl(mScenicId);
        com.cmcc.hyapps.andyou.util.Log.d("Loading route from %s", url);
        RequestManager.getInstance().sendGsonRequest(url,
                Routes.class, new Response.Listener<Routes>() {
                    @Override
                    public void onResponse(Routes response) {
                        ArrayList<Routes.ScenicRoute> routes;
                        if (response == null || response.routes == null
                                || response.routes.isEmpty()) {
                            // // TODO
                            // routes = getMockRoutes();
                            Toast.makeText(getApplicationContext(), R.string.no_route_available,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            routes = response.routes;
                        }

                        com.cmcc.hyapps.andyou.util.Log.d("getRoutes, routes=%s", response);
                        initRoutePager(routes);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO
                        com.cmcc.hyapps.andyou.util.Log.e("onErrorResponse, error=%s", error);
                    }
                }, requestTag);
    }

    private void initRoutePager(List<Routes.ScenicRoute> routes) {
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
        loadRoutesForPage(0);
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void getSecnicPots() {
        final String url = ServerAPI.ScenicRoutes.buildUrl(mScenicId);
        com.cmcc.hyapps.andyou.util.Log.d("Loading route from %s", url);
        RequestManager.getInstance().sendGsonRequest(url,
                Routes.class, new Response.Listener<Routes>() {
                    @Override
                    public void onResponse(Routes response) {
                        ArrayList<Routes.ScenicRoute> routes;
                        if (response == null || response.routes == null
                                || response.routes.isEmpty()) {
                            Toast.makeText(getApplicationContext(), R.string.no_route_available,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            routes = response.routes;
                        }

                        com.cmcc.hyapps.andyou.util.Log.d("getRoutes, routes=%s", response);
                        initRoutePager(routes);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO
                        com.cmcc.hyapps.andyou.util.Log.e("onErrorResponse, error=%s", error);
                    }
                }, requestTag);
    }


}
