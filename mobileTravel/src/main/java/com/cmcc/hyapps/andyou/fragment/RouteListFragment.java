
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.HotelMapActivity;
import com.cmcc.hyapps.andyou.adapter.RestaurantListAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHRoute;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class RouteListFragment extends BaseFragment implements
        DataLoaderCallback<QHRoute.QHRouteList>, OnClickListener {
    private PullToRefreshRecyclerView mPullToRefreshView;
    private Context mContext;
    private ArrayList<QHRoute> mVideoList = new ArrayList<QHRoute>();
    private UrlListLoader<QHRoute.QHRouteList> mLoader;

    private AppendableAdapter<QHRoute> mAdapter;

    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mRootView;
    private View mReloadView;
    private LayoutManager mLayoutManager;
    private static final String ARGS_KEY_LAYOUT_MANAGER = "args_key_layout_manager";
    private static final String ARGS_KEY_ADAPTER = "args_key_adapter";
    private Location myLocation;

    private List<QHRoute> myself_travel = new ArrayList<QHRoute>();
    private List<QHRoute> group_travel = new ArrayList<QHRoute>();
    private int type = 1;


    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mAdapter = new RestaurantListAdapter(getActivity());
        super.onCreate(savedInstanceState);
    }
//   public void setLoaction(Location myLocation){
//       this.myLocation = myLocation;
//       initUrl();
//   }

    public void loadList(){

        mLoader = new UrlListLoader<QHRoute.QHRouteList>(  mRequestTag,QHRoute.QHRouteList.class/*, page*/ );
        String url;

        url = ServerAPI.BASE_URL +"routes/?format=json";

        mLoader.setUrl(url);
    }
    public void loadList(String url){

        myself_travel.clear();
        group_travel.clear();

        mLoader = new UrlListLoader<QHRoute.QHRouteList>(  mRequestTag,QHRoute.QHRouteList.class/*, page*/ );
        mLoader.setUseCache(false);

        mLoader.setUrl(url);
        mLoader.loadMoreQHData(this, DataLoader.MODE_REFRESH);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        reload();
        super.onActivityCreated(savedInstanceState);
    }

    public void reload() {
        myself_travel.clear();
        group_travel.clear();
        loadList();
        mPullToRefreshView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);

        mLoader.loadMoreQHData(this, DataLoader.MODE_REFRESH);
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity.getApplicationContext();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_recycler_list, container, false);
        mEmptyHintView = mRootView.findViewById(R.id.empty_hint_view);
        if (mLoadingProgress != null) {
            mRootView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        } else {
            mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
        }
        mReloadView = mRootView.findViewById(R.id.reload_view);
        mReloadView.setOnClickListener(this);

        mPullToRefreshView = (PullToRefreshRecyclerView) mRootView
                .findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(Mode.BOTH);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreQHData(RouteListFragment.this, mode);
            }
        });

        RecyclerView recyclerView = mPullToRefreshView.getRefreshableView();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int scap = ScreenUtils.dpToPxInt(getActivity(), 1);
        DividerItemDecoration decor = new DividerItemDecoration(Color.LTGRAY,scap,0);
        decor.initWithRecyclerView(recyclerView);
        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(mAdapter);
        return mRootView;
    }
    String city;
    String city_en;
    String lat;
    String lon;
    public void initLoacation(String city, String city_en,String lat,String lon){
        this.city = city;
        this.city_en = city_en;
        this.lat = lat;
        this.lon = lon;
        loadList();
    }
    @Override
    public void onLoadFinished(QHRoute.QHRouteList videoList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();

        List<QHRoute> list = null;
        if(videoList != null && videoList.results != null && videoList.results.size() != 0){
            list = videoList.results;
        }

        if (list == null || list.isEmpty()) {
            if (mode==DataLoader.MODE_REFRESH) {
                mPullToRefreshView.setVisibility(View.INVISIBLE);
                mEmptyHintView.setVisibility(View.VISIBLE);
            }else {
                ToastUtils.AvoidRepeatToastShow(getActivity(), R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        } else {
            mPullToRefreshView.setVisibility(View.VISIBLE);
            if (mode == DataLoader.MODE_REFRESH) {
                mVideoList.clear();
                ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter())
                        .setDataItems(mVideoList);
            }

            mPullToRefreshView.setVisibility(View.VISIBLE);
            for (QHRoute liveVideo : list) {
//                if (liveVideo.id <= 0) {
//                    continue;
//                }
//                QHRoute rest = new QHRoute();
//                rest.name = liveVideo.name;
//                rest.address = liveVideo.address;
//                rest.avg_rating = liveVideo.avg_rating;
//                rest.s_photo_url = liveVideo.s_photo_url;
//                rest.business_id = liveVideo.business_id;
//                rest.latitude = liveVideo.latitude;
//                rest.longitude = liveVideo.longitude;
//                rest.photo_url = liveVideo.photo_url;
//                rest.price = liveVideo.price;
//                rest.distance = liveVideo.distance;
                mVideoList.add(liveVideo);
            }
//            for(QHRoute route : list){
//                if(route.ctype == 1){
//                    //自驾游
//                    myself_travel.add(route);
//                }else{
//                    //团队游
//                    group_travel.add(route);
//                }
//            }
//
//            if(type == 1){
//                list = myself_travel;
//            }else{
//                list = group_travel;
//            }
            ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter()).appendDataItems(list);
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
        }else {


        }
    }
    public void gotoMapView(){
        ArrayList<HotelMapActivity.Point> points = new ArrayList<HotelMapActivity.Point>();
        for(int i =0;i<mVideoList.size();i++){
            QHRoute hotel = mVideoList.get(i);
//            HotelMapActivity.Point point = new HotelMapActivity.Point(Double.parseDouble(hotel.latitude),
//                    Double.parseDouble(hotel.longitude),hotel.name+hotel.address);
//            points.add(point);
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(),HotelMapActivity.class);
        intent.putParcelableArrayListExtra("points",points);
        startActivity(intent);
    }
    public void attachLoadingProgress(View loadingProgress) {
        mLoadingProgress = loadingProgress;
    }

}
