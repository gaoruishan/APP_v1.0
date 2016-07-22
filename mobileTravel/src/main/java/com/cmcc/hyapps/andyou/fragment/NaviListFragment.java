
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.NavigationDetailActivity;
import com.cmcc.hyapps.andyou.adapter.NaviListAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.HomeHotel;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHNavigation;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NaviListFragment extends BaseFragment implements  DataLoaderCallback<QHNavigation.QHNavigationList>, OnClickListener, AMapNaviListener {
    private PullToRefreshRecyclerView mPullToRefreshView;
    private Context mContext;
    private ArrayList<HomeHotel> mVideoList = new ArrayList<HomeHotel>();
    private UrlListLoader<QHNavigation.QHNavigationList> mLoader;
    private NaviListAdapter mAdapter;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mRootView;
    private View mReloadView;
    private Location myLocation;
    private LayoutManager mLayoutManager;

    private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态
    //起点终点
    private NaviLatLng mNaviStart = new NaviLatLng(39.989614, 116.481763);
    private NaviLatLng mNaviEnd = new NaviLatLng(39.983456, 116.3154950);
    //起点终点列表
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mAdapter = new NaviListAdapter(getActivity(),new NaviListAdapter.OnClickedListener() {
            @Override
            public void onItemClicked(View view) {
                final QHNavigation tag = (QHNavigation) view.getTag();
                //得到经纬度，打开一个dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setPositiveButton("开始导航",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getActivity(),NavigationDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("navi_detail",tag);
                        intent.putExtra("navi_bundle",bundle);
                        getActivity().startActivity(intent);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("取消导航",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();

                //navigation
//                Location location = ConstTools.myCurrentLoacation;
//                mNaviStart = new NaviLatLng(location.latitude,location.longitude);
//
//                QHScenic tag = (QHScenic) v.getTag();
//                mNaviEnd = new NaviLatLng(tag.latitude,tag.longitude);
//
//                mStartPoints.add(mNaviStart);
//                mEndPoints.add(mNaviEnd);
//
//                AMapNavi.getInstance(getActivity()).calculateDriveRoute(mStartPoints,
//                        mEndPoints, null, AMapNavi.DrivingDefault);
//                mRouteCalculatorProgressDialog.show();
            }
        });
        initView();
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        mRouteCalculatorProgressDialog=new ProgressDialog(getActivity());
        mRouteCalculatorProgressDialog.setCancelable(true);

        AMapNavi.getInstance(getActivity()).setAMapNaviListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initUrl();
        reload();
        super.onActivityCreated(savedInstanceState);
    }

    private void reload() {
        mLoader.loadMoreQHData(this, DataLoader.MODE_LOAD_MORE);
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity.getApplicationContext();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_recycler_list, container, false);

        mPullToRefreshView = (PullToRefreshRecyclerView) mRootView .findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(Mode.PULL_FROM_END);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH
                        : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreQHData(NaviListFragment.this, mode);
            }
        });

        mEmptyHintView = mRootView.findViewById(R.id.empty_hint_view);
        if (mLoadingProgress != null) {
            mRootView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        } else {
            mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
        }
        mReloadView = mRootView.findViewById(R.id.reload_view);
        mReloadView.setOnClickListener(this);

        RecyclerView recyclerView = mPullToRefreshView.getRefreshableView();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return mRootView;
    }

    private void initUrl(){
        mLoader = new UrlListLoader<QHNavigation.QHNavigationList>( mRequestTag,QHNavigation.QHNavigationList.class/*, page*/ );
        mLoader.setUseCache(false);
        String  url = ServerAPI.NavigationList.URL;

        mLoader.setUrl(url);
        mAdapter.initPosition(myLocation);
    }
    @Override
    public void onLoadFinished(QHNavigation.QHNavigationList scenicList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();

        if(scenicList==null){
            ToastUtils.show(mContext,"亲，没有更多数据了");
            return;
        }
        List<QHNavigation> list = null;
        if (scenicList != null) {
            list = scenicList.results;
        }

        if (list == null || list.isEmpty()) {
            if (mVideoList.isEmpty()) {
                mEmptyHintView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mode == DataLoader.MODE_REFRESH || scenicList.count == 0) {
                mVideoList.clear();
                ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter()).setDataItems(mVideoList);
            }
            mPullToRefreshView.setVisibility(View.VISIBLE);
//            for (HomeHotel liveVideo : list) {
////                if (liveVideo.id <= 0) {
////                    continue;
////                }
//                HomeHotel rest = new HomeHotel();
//                rest.name = liveVideo.name;
//                rest.address = liveVideo.address;
//                rest.star = liveVideo.star;
//                rest.imageUrls = liveVideo.imageUrls;
//                rest.business_id = liveVideo.business_id;
//                rest.latitude = liveVideo.latitude;
//                rest.longitude = liveVideo.longitude;
//                rest.id = liveVideo.id;
//                rest.desc = liveVideo.desc;
//                rest.price = liveVideo.price;
//                rest.telephone = liveVideo.telephone;
//                rest.star = liveVideo.star;
//                rest.mark = liveVideo.mark;
//                rest.services = liveVideo.services;
//                mVideoList.add(rest);
//            }
            ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter()).appendDataItems(scenicList.results);
        }
    }

    @Override
    public void onLoadError(int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
        Toast.makeText(mContext, R.string.loading_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
       else if (v.getId() == R.id.reload_view) {
            reload();
        }
        else if (v.getId() ==  R.id.action_bar_left){
            getActivity().finish();
        }
    }
    public void attachLoadingProgress(View loadingProgress) {
        mLoadingProgress = loadingProgress;
    }

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
//        mRouteCalculatorProgressDialog.dismiss();
//        Intent intent = new Intent(getActivity(),
//                SimpleNaviActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        Bundle bundle=new Bundle();
//        bundle.putInt(Utils.ACTIVITYINDEX, Utils.SIMPLEGPSNAVI);
//        bundle.putBoolean(Utils.ISEMULATOR, false);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    @Override
    public void onCalculateRouteFailure(int i) {
        mRouteCalculatorProgressDialog.dismiss();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 删除导航监听
        AMapNavi.getInstance(getActivity()).removeAMapNaviListener(this);
    }
}
