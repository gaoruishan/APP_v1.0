
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.CityListActivity;
import com.cmcc.hyapps.andyou.activity.IndexActivity;
import com.cmcc.hyapps.andyou.activity.NightLiveActivity;
import com.cmcc.hyapps.andyou.activity.QHNavigationActivity;
import com.cmcc.hyapps.andyou.activity.RouteActivity;
import com.cmcc.hyapps.andyou.activity.SearchListActivity;
import com.cmcc.hyapps.andyou.activity.SecnicActivity;
import com.cmcc.hyapps.andyou.activity.StrategyActivity;
import com.cmcc.hyapps.andyou.activity.WaitActivity;
import com.cmcc.hyapps.andyou.activity.WebActivity;
import com.cmcc.hyapps.andyou.adapter.BannerPagerAdapter;
import com.cmcc.hyapps.andyou.adapter.HomeAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.GsonRequest;
import com.cmcc.hyapps.andyou.data.LocationDetector;
import com.cmcc.hyapps.andyou.data.LocationDetector.LocationListener;
import com.cmcc.hyapps.andyou.data.OfflinePackageManager;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.City;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHHomeBanner;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.model.Scenic;
import com.cmcc.hyapps.andyou.model.ScenicDetails;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.FormatUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kj.guradc.VideoActivity;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemSubViewClickListener;
import com.umeng.analytics.MobclickAgent;

//import com.cmcc.hyapps.andyou.util.Log;

public class HomeFragment extends ServiceBaseFragment implements OnClickListener,    DataLoaderCallback<QHScenic.QHScenicList> {
    private final String TAG = "HomeFragment";

    private final int REQUEST_CODE_LOGIN_NEW_COMMENT = 1;
    private final int REQUEST_CODE_LOGIN_VOTE = 2;
    private final int REQUEST_CODE_LOGIN_COMMENT_DETAIL = 3;
    private final int REQUEST_CODE_SEARCH = 4;
    private final int REQUEST_CODE_COMMENT_DETAIL = 5;
    private final int REQUEST_CODE_POST_COMMENT = 6;
    private final int REQUEST_CODE_SEARCH_ET = 7;//
    private static final int ACCELERATION_THRESOLD = 22;
    private ActionBar mActionBar;
    private RecyclerView mRecyclerView;
    private HomeAdapter mAdapter;
    private int mId = -1;
//    116.397228,39.908815
    private Location mLocation = new Location(39.908815,116.397228);//我当前loc
    private Location secnicLoc = new Location(39.908815,116.397228);//当前景区loc
    private View mLoadingProgress;
    private View mReloadView;
    private PullToRefreshRecyclerView mPullToRefreshView;
    private UrlListLoader<Scenic.ScenicList> mScenicListLoader;
    private UrlListLoader<QHScenic.QHScenicList> mScenicQHListLoader;
//    private Request<HomeBanner.HomeBannerLists> mBannerRequest;
    private GsonRequest<QHHomeBanner.QHHomeBannerLists> mBannerRequest;
    private ViewGroup mRootView;
    private TextView mVoteText;
    private OfflinePackageManager mOfflineManager;
    private Vibrator mVibrator;
    private boolean mRandomLoad;
    private QHScenic mScenic;
    private LocationDetector mLocationDetector;
    private int HTTP_GET_PARAM_LIMIT = 10;
//    private City dectedCity;
    private boolean isLoading = false;

    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onReceivedLocation(Location loc) {
           Log.d("onReceivedLocation, location=%s", loc);
            mLocation = loc;
            secnicLoc = loc;
            ConstTools.myCurrentLoacation = new  Location(loc.latitude,loc.longitude);
            ConstTools.myCurrentLoacation.city = loc.city;
            ConstTools.myCurrentLoacation.city_en = loc.city_en;
            Log.e("-------","onReceivedLocation--");
            loadScenicDetails();
        }
        @Override
        public void onLocationTimeout() {
            Log.d("onLocationTimeout");
            ((IndexActivity) getActivity()).showLocationSelector();
            mLocation = null;
            mLocation = new Location(39.908815,116.397228);
            secnicLoc = new Location(39.908815,116.397228);
            ConstTools.myCurrentLoacation = new  Location(39.908815,116.397228);
            ConstTools.myCurrentLoacation.city ="北京";
            ConstTools.myCurrentLoacation.city_en = "beijing";
            loadHomeBanner();
        }
        @Override
        public void onLocationError() {
            mLocation = null;
            mLocation = new Location(39.908815,116.397228);
            secnicLoc = new Location(39.908815,116.397228);
            ConstTools.myCurrentLoacation = new  Location(39.908815,116.397228);
            ConstTools.myCurrentLoacation.city ="北京";
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
        mRootView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.home_fragment, container,false);
        initViews();
        reload();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        // hookSensorListener(!isHidden());
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
        mPullToRefreshView.setMode(Mode.PULL_FROM_END);
        mRecyclerView.setVisibility(View.GONE);
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

        initListView();
        initPullToRefresh();

        mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
        mReloadView = mRootView.findViewById(R.id.reload_view);
        mReloadView.setOnClickListener(this);
    }

    private void initPullToRefresh() {
        mPullToRefreshView = (PullToRefreshRecyclerView) mRootView.findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    reload();
                } else {
                    //下拉加载
                    if(isLoading){
                        ToastUtils.show(getActivity(),"亲，正在加载……");
                    }else {
                        loadNearScenics(DataLoader.MODE_LOAD_MORE);
                    }
                }
            }
        });
    }

    private void initListView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new HomeAdapter(getActivity(),
            new BannerPagerAdapter.IActionCallback<QHHomeBanner>() {
                @Override
                public void doAction(QHHomeBanner data) {
                    if (data == null) {
                        return;
                    }
                    Intent intent = new Intent();
                    if(QHHomeBanner.SCENIC==data.stype){//景区
                        MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_PPT1);
//                        intent.setClass(getActivity(), SecnicActivity.class);
////                        intent.putExtra(Const.EXTRA_NAME,data.title);
//                        intent.putExtra(Const.EXTRA_ID,Integer.parseInt(data.action));
                        intent = new Intent(getActivity(), SecnicActivity.class);
                        int mId = Integer.parseInt(data.action);
                        intent.putExtra(Const.QH_SECNIC,mScenic);
                        intent.putExtra(Const.QH_SECNIC_ID,mId);
                        startActivity(intent);
                    }
                    else if(QHHomeBanner.VIDEO==data.stype){//视频
                        MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_HOME_ACT);
//                        intent.setClass(getActivity(), WebActivity.class);
////                        intent.putExtra(Const.EXTRA_NAME,data.title);
//                        intent.putExtra(Const.EXTRA_URI,data.action);
                        String url = ServerAPI.VideoList.buildItemDetailUrl(data.action);
                        RequestManager.getInstance().sendGsonRequest(Method.GET, url,
                                QHScenic.QHVideo.class, null,
                                new Response.Listener<QHScenic.QHVideo>() {
                                    @Override
                                    public void onResponse(final QHScenic.QHVideo item) {
                                        // 0：使用白天视频 1：使用夜间视频
                                        Intent intent = null;
                                        switch (item.day_or_night) {
                                            case 0:
                                                intent = new Intent(getActivity(), VideoActivity.class);
                                                intent.putExtra("url", item.video_day);
                                                getActivity().startActivity(intent);
                                                break;
                                            case 1:
                                                intent = new Intent(getActivity(), NightLiveActivity.class);
                                                intent.putExtra("url", item.video_night);
                                                getActivity().startActivity(intent);
                                                break;
                                            default:
                                                intent = new Intent(getActivity(), VideoActivity.class);
                                                intent.putExtra("url", item.video_day);
                                                getActivity().startActivity(intent);
                                                break;
                                        }

                                            // 以下是测试白天和晚上的代码
//                                        final Context mContext = getActivity();
//                                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                                        builder.setPositiveButton("白天",new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                Intent intent = new Intent(mContext,VideoActivity.class);
//                                                intent.putExtra("url",item.video_day);
//                                                mContext.startActivity(intent);
//                                                dialogInterface.dismiss();
//                                            }
//                                        });
//                                        builder.setNegativeButton("夜间",new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                Intent intent = new Intent(mContext,NightLiveActivity.class);
//                                                intent.putExtra("url",item.video_night);
//                                                mContext.startActivity(intent);
//                                                dialogInterface.dismiss();
//                                            }
//                                        });
//                                        builder.show();


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getActivity(),"获取视频资源失败，请检查网络",Toast.LENGTH_SHORT).show();
                                    }
                                }, false, mRequestTag);
                        return;
                    }else if (QHHomeBanner.H5 == data.stype){//h5
                        MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_HOME_ACT);
                        intent.setClass(getActivity(), WebActivity.class);
//                        intent.putExtra(Const.EXTRA_NAME,data.title);
                        intent.putExtra(Const.EXTRA_URI,data.action);
                    }
//                    else if("2".equals(data.action)){//游记
//                        /*MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_HOME_ACT);
//                        intent.setClass(getActivity(), WebActivity.class);
//                        intent.putExtra(Const.EXTRA_NAME,data.title);
//                        intent.putExtra(Const.EXTRA_URI,data.action);*/
//                    }
                    startActivity(intent);
                }


            }
        );
        ItemClickSupport clickSupport = ItemClickSupport.addTo(mRecyclerView);
        clickSupport.setOnItemSubViewClickListener(new OnItemSubViewClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    onHeaderClicked(view);
                } else {
                    onItemClicked(view);
                }
            }
        });
        int scap = ScreenUtils.dpToPxInt(getActivity(), 13);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
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
            case R.id.action_bar_left_text: {
                Intent intent = new Intent(getActivity(), /*VideoPlayer*//*MediaPlayerDemo_Video*/CityListActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEARCH);
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
            default:
                break;
        }
    }

    private void loadHomeBanner() {
        isLoading = true;
        final String url;
        url = ServerAPI.BASE_URL + "banners/?format=json";
//        url = ServerAPI.ScenicDetails.buildQinghaiUrl("json");
        Log.e("Loading scenic details from %s", url);
        mBannerRequest = RequestManager.getInstance().sendGsonRequest(Method.GET, url,
                QHHomeBanner.QHHomeBannerLists.class, null,
                new Response.Listener<QHHomeBanner.QHHomeBannerLists>() {
                    @Override
                    public void onResponse(QHHomeBanner.QHHomeBannerLists response) {
                        mPullToRefreshView.onRefreshComplete();
                        mLoadingProgress.setVisibility(View.GONE);
                        Log.e("loadHomeBanner, ScenicDetails=%s,mAdapter.size=%d", response, mAdapter.getDataItems().size());
                        mRandomLoad = false;
                        mAdapter.setDataItems(null);
                        mAdapter.setHeader(response);
                        mAdapter.setMyLocation(mLocation);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mAdapter.notifyDataSetChanged();
                        loadNearScenics(DataLoader.MODE_REFRESH);
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
                }, true, mRequestTag);

    }

    private void showReloadView() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
    }

    private void onScenicDetailsLoaded(ScenicDetails data) {
        Log.e("-------","onScenicDetailsLoaded--");
        mScenicDetailsModel = data;
        mId = data.id;
        mActionBar.getLeftTextView().setText( FormatUtils.cutStringStartBy(mScenicDetailsModel.cityZh, 3));
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
        Log.e("-------","loadNearScenics--");
        mLoadingProgress.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        if (mScenicListLoader == null) {
            mScenicListLoader = new UrlListLoader<Scenic.ScenicList>( mRequestTag, Scenic.ScenicList.class);
            String  url;
            url = ServerAPI.ScenicList.buildUrl(ServerAPI.ScenicList.Type.NEARBY,  cityName, HTTP_GET_PARAM_LIMIT,0) ;
            mScenicListLoader.setUrl(url);
        }

//        mScenicListLoader.loadMoreData(this, DataLoader.MODE_LOAD_MORE);
    }

    private void loadNearScenics(int mode){
//        mLoadingProgress.setVisibility(View.VISIBLE);
//        mRecyclerView.setVisibility(View.GONE);
        if (mScenicQHListLoader == null) {
            mScenicQHListLoader = new UrlListLoader<QHScenic.QHScenicList>( mRequestTag, QHScenic.QHScenicList.class);
            String  url;
            url = ServerAPI.BASE_URL +"scenics/?format=json";
            mScenicQHListLoader.setUrl(url);
        }
        //加载更多
        mScenicQHListLoader.loadMoreQHData(this, mode);
    }



    @Override
    public void onLoadFinished(QHScenic.QHScenicList list, int mode) {
        isLoading = false;
        mPullToRefreshView.onRefreshComplete();
        onListLoaded(list,mode);
    }

    @Override
    public void onLoadError(int mode) {
        Log.e("Error loading scenic details comment for %d", mId);
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void onListLoaded(QHScenic.QHScenicList data,int mode) {
        if(mode==DataLoader.MODE_REFRESH){
            mAdapter.setDataItems(data.results);
            return;
        }
        mLoadingProgress.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        if (data == null || data.results == null|| mScenicListLoader != null) {
            // Disable PULL_FROM_END
            mPullToRefreshView.setMode(Mode.PULL_FROM_END);
            Toast.makeText(getActivity(),"没有更多数据",Toast.LENGTH_SHORT).show();
            return;
        }

        if (data != null) {
            if (data.pagination != null && data.pagination.offset == 0) {
                mAdapter.setDataItems(null);
            }
            mAdapter.appendDataItems(data.results);
            mAdapter.notifyDataSetChanged();
        }
    }
    private void onItemClicked(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }

        mScenic = (QHScenic) v.getTag();
        if (mScenic == null) {
            Log.e("NULL mScenic");
            return;
        }

        switch (v.getId()) {
            case R.id.iv_cover_image:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_HOME_SECNIC);
                Intent intent = new Intent(getActivity(), SecnicActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putExtra("scenic",mScenic);
                int mId = mScenic.id;
                intent.putExtra(Const.QH_SECNIC,mScenic);
                intent.putExtra(Const.QH_SECNIC_ID,mId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    private void onHeaderClicked(View v) {
        /*if (mScenicDetailsModel == null) {
            return;
        }*/
        switch (v.getId()) {
            case R.id.tags_selfDrivingTravel:
//                setTagColorChange(isTrue,R.id.tags_selfDrivingTravel);
                break;
            case R.id.home_search_et:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_BTN_CITY);
                //跳转到搜索页面
                Intent intent = new Intent(getActivity(), SearchListActivity.class);
                startActivity(intent);
//                Intent intent = new Intent(getActivity(), SearchScenicActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_SEARCH_ET);
                break;
            case R.id.home_tab_food:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_HOME_BTN_BBQ);
                Intent intent3 = new Intent(getActivity(), RouteActivity.class);
                if(null!=mScenicDetailsModel){
                    intent3.putExtra(Const.CITYNAME_EN,mScenicDetailsModel.city);
                    intent3.putExtra(Const.CITYNAME,mScenicDetailsModel.cityZh);
                }else {
                    intent3.putExtra(Const.CITYNAME_EN, "beijing");
                    intent3.putExtra(Const.CITYNAME,"北京");
                }
//                intent3.putExtra(Const.EXTRA_COORDINATES,mLocation);
                intent3.putExtra(Const.LAT,""+secnicLoc.latitude);
                intent3.putExtra(Const.LON,""+secnicLoc.longitude);
                startActivity(intent3);
                break;
           case R.id.home_tab_hotel:

                MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_BTN_HOTEL);
                Intent intent1 = new Intent(getActivity(), QHNavigationActivity.class);

                startActivity(intent1);
                break;
           case R.id.home_tab_special:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_BTN_SPECIALTY);
                Intent intent2 = new Intent(getActivity(), StrategyActivity.class);

               if(null!=mScenicDetailsModel){
                   intent2.putExtra(Const.CITYNAME_EN,mScenicDetailsModel.city);
                   intent2.putExtra(Const.CITYNAME,mScenicDetailsModel.cityZh);
               }else{
                   intent2.putExtra(Const.CITYNAME_EN,"beijing");
                   intent2.putExtra(Const.CITYNAME,"北京");
               }
                startActivity(intent2);
                break;
            case R.id.home_tab_guide:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_HOME_GUIDE);
                Intent intentWati = new Intent();
                intentWati.setClass(getActivity(), WaitActivity.class);
                startActivity(intentWati);
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
                    if (null!=city) {
                        mId = 1;

                        if(null==mScenicDetailsModel){
                            mLocation.latitude = city.location.latitude;
                            mLocation.longitude = city.location.longitude;
                            mScenicDetailsModel = new ScenicDetails();
                        }
                        else {
                               mScenicDetailsModel.city = city.code;
                               mScenicDetailsModel.cityZh = city.name;
                               mScenicDetailsModel.location.latitude = city.location.latitude;
                               mScenicDetailsModel.location.longitude = city.location.longitude;
                           }
                        mActionBar.getLeftTextView().setText(FormatUtils.cutStringStartBy(city.name, 3));
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
                        intent.putExtra(Const.EXTRA_ID,id);
                        intent.putExtra(Const.EXTRA_COORDINATES,mLocation);
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

    private void loadScenicDetails() {
        final String url;
        url = ServerAPI.BASE_URL + "scenics/?format=json";
//        url = ServerAPI.ScenicDetails.buildQinghaiUrl("json");
        RequestManager.getInstance().sendGsonRequest(Method.GET, url,
                QHScenic.QHScenicList.class, null,
                new Response.Listener<QHScenic.QHScenicList>() {
                    @Override
                    public void onResponse(QHScenic.QHScenicList response) {
                        Log.d("onResponse, ScenicDetails=%s,mAdapter.size=%d", response, mAdapter.getDataItems().size());
                        mRandomLoad = false;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mLoadingProgress.setVisibility(View.GONE);
                        mReloadView.setVisibility(View.VISIBLE);
                    }
                }, true, mRequestTag);
    }


}
