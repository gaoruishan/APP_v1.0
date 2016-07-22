package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.activity.FourSActivity;
import com.cmcc.hyapps.andyou.activity.ShopDetailActivity;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.model.QHShopsBanner;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.umeng.analytics.MobclickAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.CityListActivity;
import com.cmcc.hyapps.andyou.activity.FoodActivity;
import com.cmcc.hyapps.andyou.activity.HotelDetailsActivity;
import com.cmcc.hyapps.andyou.activity.IndexActivity;
import com.cmcc.hyapps.andyou.activity.MarketHotelsActivity;
import com.cmcc.hyapps.andyou.activity.RestaurantDetailActivity;
import com.cmcc.hyapps.andyou.activity.SearchListActivity;
import com.cmcc.hyapps.andyou.activity.SecnicActivity;
import com.cmcc.hyapps.andyou.activity.SpecialActivity;
import com.cmcc.hyapps.andyou.activity.SpecialDetailActivity;
import com.cmcc.hyapps.andyou.adapter.BannerPagerAdapter;
import com.cmcc.hyapps.andyou.adapter.MarketAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.GsonRequest;
import com.cmcc.hyapps.andyou.data.LocationDetector;
import com.cmcc.hyapps.andyou.data.OfflinePackageManager;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.City;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHMarketShop;
import com.cmcc.hyapps.andyou.model.Scenic;
import com.cmcc.hyapps.andyou.model.ScenicDetails;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import test.grs.com.ims.message.MessageListActivity;

/**
 * Created by niuzhiguo on 2015/6/23.
 */
public class MarketFragment extends ServiceBaseFragment implements View.OnClickListener, DataLoader.DataLoaderCallback<QHMarketShop.QHMarketShopList> {
    private final String TAG = "MarketFragment";
    private static int MARKET_TO_MESSAGE = 1011;
    private final int REQUEST_CODE_LOGIN_NEW_COMMENT = 1;
    private final int REQUEST_CODE_LOGIN_VOTE = 2;
    private final int REQUEST_CODE_LOGIN_COMMENT_DETAIL = 3;
    private final int REQUEST_CODE_SEARCH = 4;
    private final int REQUEST_CODE_COMMENT_DETAIL = 5;
    private final int REQUEST_CODE_POST_COMMENT = 6;
    private final int REQUEST_CODE_SEARCH_ET = 7;//
    private static final int ACCELERATION_THRESOLD = 22;
    private RecyclerView mRecyclerView;
    private MarketAdapter mAdapter;
    private int mId = -1;
    //    116.397228,39.908815
    private Location mLocation = new Location(39.908815, 116.397228);//我当前loc
    private Location secnicLoc = new Location(39.908815, 116.397228);//当前景区loc
    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mReloadView;
    private PullToRefreshRecyclerView mPullToRefreshView;
    private UrlListLoader<Scenic.ScenicList> mScenicListLoader;
    private UrlListLoader<QHMarketShop.QHMarketShopList> mShopQHListLoader;
    //    private Request<HomeBanner.HomeBannerLists> mBannerRequest;
    private GsonRequest<QHShopsBanner.QHShopsBannerList> mBannerRequest;
    private ViewGroup mRootView;
    private TextView mVoteText;
    private OfflinePackageManager mOfflineManager;
    private Vibrator mVibrator;
    private boolean mRandomLoad;
    private QHMarketShop mShop;
    private LocationDetector mLocationDetector;
    private int HTTP_GET_PARAM_LIMIT = 10;
    private long mSearchTiem;
    private boolean isRefreshing = false;
//    private City dectedCity;

    private LocationDetector.LocationListener mLocationListener = new LocationDetector.LocationListener() {

        @Override
        public void onReceivedLocation(Location loc) {
            Log.d("onReceivedLocation, location=%s", loc);
            mLocation = loc;
            secnicLoc = loc;
            ConstTools.myCurrentLoacation = new Location(loc.latitude, loc.longitude);
            ConstTools.myCurrentLoacation.city = loc.city;
            ConstTools.myCurrentLoacation.city_en = loc.city_en;
            Log.e("-------", "onReceivedLocation--");
            //  loadScenicDetails();
        }

        @Override
        public void onLocationTimeout() {
            Log.d("onLocationTimeout");
            ((IndexActivity) getActivity()).showLocationSelector();
            mLocation = null;
            mLocation = new Location(39.908815, 116.397228);
            secnicLoc = new Location(39.908815, 116.397228);
            ConstTools.myCurrentLoacation = new Location(39.908815, 116.397228);
            ConstTools.myCurrentLoacation.city = "北京";
            ConstTools.myCurrentLoacation.city_en = "beijing";
            loadHomeBanner();
        }

        @Override
        public void onLocationError() {
            mLocation = null;
            mLocation = new Location(39.908815, 116.397228);
            secnicLoc = new Location(39.908815, 116.397228);
            ConstTools.myCurrentLoacation = new Location(39.908815, 116.397228);
            ConstTools.myCurrentLoacation.city = "北京";
            ConstTools.myCurrentLoacation.city_en = "beijing";
            loadHomeBanner();
            Log.d("onLocationError");
            // TODO
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOfflineManager = OfflinePackageManager.getInstance();
        mLocationDetector = new LocationDetector(getActivity().getApplicationContext());
        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        ShareManager.getInstance().onStart(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_market, container, false);
        initViews();
        reload();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        if (UserManager.isLogin(this.getActivity())){
            isHasXiaoXiMessage();
        }else{
            if (red_point != null && red_point.isShown()){
                red_point.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageEnd(TAG);
        super.onPause();
        // hookSensorListener(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mId = getArguments().getInt(Const.EXTRA_ID);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d("onHiddenChanged, hidden=%s", hidden);
        // hookSensorListener(!hidden);
        super.onHiddenChanged(hidden);
    }

    private void reload() {
        Log.d("reload", "\\\\\\\\");
        isRefreshing = true;
        mPullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshView.setVisibility(View.GONE);
        mEmptyHintView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);
        mAdapter.setHeader(null);
        mAdapter.setDataItems(null);
        mScenicListLoader = null;
        RequestManager.getInstance().getRequestQueue().cancelAll(mRequestTag);
        if (mRandomLoad) {
//            loadHomeBanner();
            return;
        }
        if (mId > 0) {
            ScenicDetails offlineData = mOfflineManager.getOfflineData(mId, ScenicDetails.class);
            if (offlineData != null) {
                Log.d("Using offline package data for scenic %d", mId);
                onScenicDetailsLoaded(offlineData);
            } else {
                loadHomeBanner();
            }
        } else {
//            mLocation = getArguments().getParcelable(Const.EXTRA_COORDINATES);
//            if (mLocation != null && mLocation.isValid()) {
            loadHomeBanner();
//            } else {
//                mLocationDetector.detectLocation(mLocationListener, true, true);
//            }
        }
        mLocationDetector.detectLocation(mLocationListener, true, true);
    }

    private void initViews() {
        initActionBar();
        initListView();
        initPullToRefresh();

        mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
        mEmptyHintView = mRootView.findViewById(R.id.empty_hint_view);
        mReloadView = mRootView.findViewById(R.id.reload_view);
        mReloadView.setOnClickListener(this);
        red_point = (ImageView) mRootView.findViewById(R.id.home_fragment_message_red_point);
    }

    private void initPullToRefresh() {
        mPullToRefreshView = (PullToRefreshRecyclerView) mRootView.findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    reload();
                } else {
                    //下拉加载
                    Log.d(TAG, "loadNearShops--PULL MODE:" + refreshView.getCurrentMode());
                    loadNearShops(DataLoader.MODE_LOAD_MORE);
                }
            }
        });
    }

    private void initListView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isRefreshing) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MarketAdapter(getActivity(),
                new BannerPagerAdapter.IActionCallback<QHShopsBanner>() {
                    @Override
                    public void doAction(QHShopsBanner data) {
                        if (data == null || data.getReferred_shop() == null) {
                            return;
                        }
                        Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
                        int mId = data.getReferred_shop_id();
                        intent.putExtra("shopID", mId + "");
                        startActivity(intent);
                    }
                }
        );
        ItemClickSupport clickSupport = ItemClickSupport.addTo(mRecyclerView);
        clickSupport.setOnItemSubViewClickListener(new ItemClickSupport.OnItemSubViewClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    onHeaderClicked(view);
                } else {
                    onItemClicked(view);
                }
            }
        });
        int scap = ScreenUtils.dpToPxInt(getActivity(), 1);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initActionBar() {
        View searchET = mRootView.findViewById(R.id.action_bar_search_et);
        searchET.setOnClickListener(this);
        mRootView.findViewById(R.id.market_fragment_message).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left:
                getActivity().finish();
                break;
            case R.id.action_bar_search_et:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_BTN_CITY);
                //跳转到搜索页面
                Intent intent = new Intent(getActivity(), SearchListActivity.class);
                intent.putExtra(Const.EXTRA_TYPE, Const.EXTRA_SHOP_DATA);
                startActivity(intent);
                break;
            case R.id.action_bar_left_text: {
                Intent intent1 = new Intent(getActivity(), /*VideoPlayer*//*MediaPlayerDemo_Video*/CityListActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_SEARCH);
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_SCENIC_SEARCH);
                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                String type = "video*//* ";
                Uri uri = Uri.parse("http://forum.ea3w.com/coll_ea3w/attach/2008_10/12237832415.3gp");
                intent.setDataAndType(uri, type);
                startActivity(intent);*/
                break;
            }
            case R.id.reload_view: {
                reload();
                break;
            }
            case R.id.market_fragment_message: {
                //跳到消息列表
                if (!UserManager.makeSureLogin(getActivity(), MARKET_TO_MESSAGE)) {
                    intent = new Intent(getActivity(), MessageListActivity.class);
                    startActivity(intent);
                }
                break;
            }
            default:
                break;
        }
    }

    private void loadHomeBanner() {
        final String url;
        url = ServerAPI.BASE_URL + "shop_banners/?format=json";
        //     url = "http://112.54.207.48/api/shop_banners?format=json";
//        url = ServerAPI.ScenicDetails.buildQinghaiUrl("json");
        Log.d("Loading market banner from %s", url);
        mBannerRequest = RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url,
                QHShopsBanner.QHShopsBannerList.class, null,
                new Response.Listener<QHShopsBanner.QHShopsBannerList>() {
                    @Override
                    public void onResponse(QHShopsBanner.QHShopsBannerList response) {
                        mLoadingProgress.setVisibility(View.GONE);
                        if (mAdapter.getDataItems().size() > 0)
                            return; //TODO niuzhiguo

                        //    mPullToRefreshView.onRefreshComplete();
                        Log.d("loadHomeBanner, ScenicDetails=%s,mAdapter.size=%d", response, mAdapter.getDataItems().size());
                        mRandomLoad = false;
                        mAdapter.setDataItems(null);
                        mAdapter.setHeader(response);
                        mAdapter.setMyLocation(mLocation);
                        //    mPullToRefreshView.setVisibility(View.VISIBLE);
                        mAdapter.notifyDataSetChanged();

                        loadNearShops(DataLoader.MODE_REFRESH);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error loading scenic details from %s", url);
                        showReloadView();
                        /*if (mScenicDetailsModel == null) {
                            showReloadView();
                        }
                        if (mBannerRequest != null) {
                            mBannerRequest.markDelivered();
                        }*/
                        mRandomLoad = false;
                        mPullToRefreshView.onRefreshComplete();
                    }
                }, false, mRequestTag);

    }

    private void showReloadView() {
        mPullToRefreshView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
    }

    private void onScenicDetailsLoaded(ScenicDetails data) {
        Log.e("-------", "onScenicDetailsLoaded--");
        mScenicDetailsModel = data;
        mId = data.id;
        loadHomeBanner();

        // Check if we already have an offline archive
        if (!data.isOfflinePackage) {
            ScenicDetails offlineData = mOfflineManager.getOfflineData(data.id, ScenicDetails.class);
            if (offlineData != null) {
                mScenicDetailsModel = offlineData;
                Log.d("Replacing scenic %s with offline data", data.name);
            }
        }
    }

    private void loadNearScenics(String cityName) {
        Log.e("-------", "loadNearScenics--");
        mLoadingProgress.setVisibility(View.VISIBLE);
        mPullToRefreshView.setVisibility(View.GONE);

        if (mScenicListLoader == null) {
            mScenicListLoader = new UrlListLoader<Scenic.ScenicList>(mRequestTag, Scenic.ScenicList.class);
            String url;
            url = ServerAPI.ScenicList.buildUrl(ServerAPI.ScenicList.Type.NEARBY, cityName, HTTP_GET_PARAM_LIMIT, 0);
            mScenicListLoader.setUrl(url);
        }

//        mScenicListLoader.loadMoreData(this, DataLoader.MODE_LOAD_MORE);
    }

    private void loadNearShops(int mode) {
        if ((System.currentTimeMillis() - mSearchTiem) < 300) {
            return; // Too fast
        }
        mSearchTiem = System.currentTimeMillis();

        Log.d(TAG, "loadNearShops--loadMoreShops");
//        mLoadingProgress.setVisibility(View.VISIBLE);
//        mPullToRefreshView.setVisibility(View.GONE);
        if (mShopQHListLoader == null) {
            mShopQHListLoader = new UrlListLoader<QHMarketShop.QHMarketShopList>(mRequestTag, QHMarketShop.QHMarketShopList.class);
            mShopQHListLoader.setUrl(ServerAPI.MarketShopList.buildRecommendUrl());
        }
        //加载更多
        mShopQHListLoader.loadMoreQHData(this, mode);
    }


    @Override
    public void onLoadFinished(QHMarketShop.QHMarketShopList list, int mode) {
        mPullToRefreshView.onRefreshComplete();
        isRefreshing = false;
        onListLoaded(list);
    }

    @Override
    public void onLoadError(int mode) {
        Log.e("Error loading scenic details comment for %d", mId);
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.setVisibility(View.VISIBLE);
    }


    private void onListLoaded(QHMarketShop.QHMarketShopList data) {
        if (data == null || data.results == null || data.results.size() == 0 || mScenicListLoader != null) {
            if (mAdapter.getItemCount() == 0) {
                mPullToRefreshView.setVisibility(View.GONE);
                mEmptyHintView.setVisibility(View.VISIBLE);
                return;
            }
            // Disable PULL_FROM_END
            //  mPullToRefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            ToastUtils.AvoidRepeatToastShow(getActivity(), R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            return;
        }

        if (data != null) {
            mEmptyHintView.setVisibility(View.GONE);
            mPullToRefreshView.setVisibility(View.VISIBLE);
            if (data.pagination != null && data.pagination.offset == 0) {
                mAdapter.setDataItems(null);
            }
            mAdapter.appendDataItems(data.results);
            mAdapter.notifyDataSetChanged();
        }
        mLoadingProgress.setVisibility(View.GONE);
    }

    private void onItemClicked(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }

        mShop = (QHMarketShop) v.getTag();
        if (mShop == null) {
            Log.e("NULL mShop");
            return;
        }

        switch (mShop.stype) {
            case 1: //Hotel
                Intent intent1 = new Intent(getActivity(), HotelDetailsActivity.class);
                intent1.putExtra(Const.REST_DETAIL, mShop);
                startActivity(intent1);
                break;
            case 2: //Restaurant
                Intent intent2 = new Intent(getActivity(), RestaurantDetailActivity.class);
                intent2.putExtra(Const.REST_DETAIL, mShop);
                startActivity(intent2);
                break;
            case 3: //Special
                Intent intent3 = new Intent(getActivity(), SpecialDetailActivity.class);
                intent3.putExtra(Const.SPECIAL_DETAIL_DATA, mShop);
                startActivity(intent3);
                break;
        }
    }

    private void onHeaderClicked(View v) {
        /*if (mScenicDetailsModel == null) {
            return;
        }*/
        switch (v.getId()) {
            case R.id.scenic_cover_image:
                break;

            case R.id.home_tab_food:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_HOME_BTN_BBQ);
                Intent intent3 = new Intent(getActivity(), FoodActivity.class);
                if (null != mScenicDetailsModel) {
                    intent3.putExtra(Const.CITYNAME_EN, mScenicDetailsModel.city);
                    intent3.putExtra(Const.CITYNAME, mScenicDetailsModel.cityZh);
                } else {
                    intent3.putExtra(Const.CITYNAME_EN, "beijing");
                    intent3.putExtra(Const.CITYNAME, "北京");
                }
//                intent3.putExtra(Const.EXTRA_COORDINATES,mLocation);
                intent3.putExtra(Const.LAT, "" + secnicLoc.latitude);
                intent3.putExtra(Const.LON, "" + secnicLoc.longitude);
                startActivity(intent3);
                break;
            case R.id.home_tab_hotel:

                MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_BTN_HOTEL);
                Intent intent1 = new Intent(getActivity(), MarketHotelsActivity.class);

                startActivity(intent1);
                break;
            case R.id.home_tab_special:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_BTN_SPECIALTY);
                Intent intent2 = new Intent(getActivity(), SpecialActivity.class);

                if (null != mScenicDetailsModel) {
                    intent2.putExtra(Const.CITYNAME_EN, mScenicDetailsModel.city);
                    intent2.putExtra(Const.CITYNAME, mScenicDetailsModel.cityZh);
                } else {
                    intent2.putExtra(Const.CITYNAME_EN, "beijing");
                    intent2.putExtra(Const.CITYNAME, "北京");
                }
                startActivity(intent2);
                break;
            case R.id.home_tab_4s:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_BTN_4S);
                Intent fourIntent = new Intent(getActivity(), FourSActivity.class);
                startActivity(fourIntent);
                break;
            default:
                break;
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LOGIN_NEW_COMMENT:
                    break;
                case REQUEST_CODE_LOGIN_VOTE:
                    break;
                case REQUEST_CODE_LOGIN_COMMENT_DETAIL:
                    break;
                case REQUEST_CODE_SEARCH:
                    City city = data.getParcelableExtra(Const.CITYMODE);
                    if (null != city) {
                        mId = 1;

                        if (null == mScenicDetailsModel) {
                            mLocation.latitude = city.location.latitude;
                            mLocation.longitude = city.location.longitude;
                            mScenicDetailsModel = new ScenicDetails();
                        } else {
                            mScenicDetailsModel.city = city.code;
                            mScenicDetailsModel.cityZh = city.name;
                            mScenicDetailsModel.location.latitude = city.location.latitude;
                            mScenicDetailsModel.location.longitude = city.location.longitude;
                        }
                        secnicLoc.latitude = city.location.latitude;
                        secnicLoc.longitude = city.location.longitude;
                        secnicLoc.city = city.name;
                        secnicLoc.city_en = city.code;

                        mAdapter.setDataItems(null);
                        mScenicListLoader = null;
                        loadNearScenics(city.name);
                    } else {
                        Log.e("error onActivityResult, id = ", mId);
                    }
                    break;
                case REQUEST_CODE_SEARCH_ET:
                    int id = data.getIntExtra(Const.EXTRA_ID, -1);
                    if (id > -1 && mId != id) {
                        MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_HOME_SECNIC);
                        Intent intent = new Intent(getActivity(), SecnicActivity.class);
                        intent.putExtra(Const.EXTRA_ID, id);
                        intent.putExtra(Const.EXTRA_COORDINATES, mLocation);
                        startActivity(intent);
                    } else {
                        Log.e("error onActivityResult, id = ", id);
                    }
                    break;
                case REQUEST_CODE_COMMENT_DETAIL:
                    break;
                case REQUEST_CODE_POST_COMMENT: {
                    Comment c = (Comment) data.getParcelableExtra(Const.EXTRA_COMMENT_DATA);
                    if (c != null) {
//                        mAdapter.getDataItems().add(0, c);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mLocationDetector != null) {
            mLocationDetector.close();
        }
        ShareManager.getInstance().onEnd();
        super.onDestroy();
    }


}
