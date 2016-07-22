
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.HotelListAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.HomeHotel;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HotelListFragment extends BaseFragment implements  DataLoaderCallback<HomeHotel.HomeHotelList>, OnClickListener {
    private PullToRefreshRecyclerView mPullToRefreshView;
    private Context mContext;
    private ArrayList<HomeHotel> mVideoList = new ArrayList<HomeHotel>();
    private UrlListLoader<HomeHotel.HomeHotelList> mLoader;
    private HotelListAdapter mAdapter;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mRootView;
    private View mReloadView;
    private Location myLocation;
    private LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
       mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mAdapter = new HotelListAdapter(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initUrl();
        reload();
        super.onActivityCreated(savedInstanceState);
    }

    private void reload() {
        mPullToRefreshView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);

//      int scenicId = getArguments().getInt(Const.ARGS_SCENIC_ID);
        // TODOst
        mLoader.loadMoreData(this, DataLoader.MODE_LOAD_MORE);
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity.getApplicationContext();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_recycler_list, container, false);
//        initActionBar(mRootView);
        mEmptyHintView = mRootView.findViewById(R.id.empty_hint_view);
        if (mLoadingProgress != null) {
            mRootView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        } else {
            mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
        }
        mReloadView = mRootView.findViewById(R.id.reload_view);
        mReloadView.setOnClickListener(this);

        mPullToRefreshView = (PullToRefreshRecyclerView) mRootView .findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(Mode.BOTH);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH
                                : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreData(HotelListFragment.this, mode);
            }
        });

        RecyclerView recyclerView = mPullToRefreshView.getRefreshableView();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        int scap = ScreenUtils.dpToPxInt(getActivity(), 1);
//        DividerItemDecoration decor = new DividerItemDecoration(ConstTools.LINECOLOR ,scap,0);
//        decor.initWithRecyclerView(recyclerView);
//        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(mAdapter);
        mAdapter.initPosition(this.myLocation);
        return mRootView;
    }

    String city;
    String city_en;
    public void initLoacation(String city, String city_en){
        this.city = city;
        this.city_en = city_en;
        initUrl();
    }
    public void setMyLocation(Location myLocation){
        this.myLocation = myLocation;

    }

    private void initUrl(){
       /* Parcel parcel =Parcel.obtain() ;
        Pagination pag =  new Pagination(parcel);
        pag.limit = 2;
        pag.offset = 2;
        pag.total = 10;
        Paginator page =  new Paginator();
        page.addPage(pag);*/
        mLoader = new UrlListLoader<HomeHotel.HomeHotelList>( mRequestTag,HomeHotel.HomeHotelList.class/*, page*/ );
        mLoader.setUseCache(true);
        String  url;
        if(null!=city)
            url = ServerAPI.Home.buildHotelUrl(city);
        else
            url = ServerAPI.Home.buildHotelUrl("北京");

        mLoader.setUrl(url);
//        mAdapter.initPosition(myLocation);
    }
    @Override
    public void onLoadFinished(HomeHotel.HomeHotelList videoList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();

        List<HomeHotel> list = null;
        if (videoList != null) {
            list = videoList.list;
        }

        if (list == null || list.isEmpty()) {
            if (mVideoList.isEmpty()) {
                mEmptyHintView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mode == DataLoader.MODE_REFRESH || videoList.pagination.offset == 0) {
                mVideoList.clear();
                ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter()).setDataItems(mVideoList);
            }
            mPullToRefreshView.setVisibility(View.VISIBLE);
            for (HomeHotel liveVideo : list) {
//                if (liveVideo.id <= 0) {
//                    continue;
//                }
                HomeHotel rest = new HomeHotel();
                rest.name = liveVideo.name;
                rest.address = liveVideo.address;
                rest.star = liveVideo.star;
                rest.imageUrls = liveVideo.imageUrls;
                rest.business_id = liveVideo.business_id;
                rest.latitude = liveVideo.latitude;
                rest.longitude = liveVideo.longitude;
                rest.id = liveVideo.id;
                rest.desc = liveVideo.desc;
                rest.price = liveVideo.price;
                rest.telephone = liveVideo.telephone;
                rest.star = liveVideo.star;
                rest.mark = liveVideo.mark;
                rest.services = liveVideo.services;
                mVideoList.add(rest);
            }
            ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter()).appendDataItems(videoList.list);
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

}
