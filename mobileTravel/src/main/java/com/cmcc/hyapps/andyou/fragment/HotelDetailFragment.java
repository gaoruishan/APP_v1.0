
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
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
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.HomeRestuarant;
import com.cmcc.hyapps.andyou.model.Pagination;
import com.cmcc.hyapps.andyou.model.Paginator;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HotelDetailFragment extends BaseFragment implements
        DataLoaderCallback<HomeRestuarant.HomeRestuarantList>, OnClickListener {
    private PullToRefreshRecyclerView mPullToRefreshView;
    private Context mContext;
    private ArrayList<HomeRestuarant> mVideoList = new ArrayList<HomeRestuarant>();
    private UrlListLoader<HomeRestuarant.HomeRestuarantList> mLoader;

    private AppendableAdapter<HomeRestuarant> mAdapter;

    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mRootView;
    private View mReloadView;

    private LayoutManager mLayoutManager;

    private static final String ARGS_KEY_LAYOUT_MANAGER = "args_key_layout_manager";
    private static final String ARGS_KEY_ADAPTER = "args_key_adapter";

    /*public static RestaurantListFragment newInstance(
            Class<? extends LayoutManager> layoutMgrClass,
            Class<? extends AppendableAdapter<Video>> adapterClass) {
        RestaurantListFragment fragment = new RestaurantListFragment();

        Bundle args = new Bundle();
        args.putString(ARGS_KEY_LAYOUT_MANAGER, layoutMgrClass.getName());
        args.putString(ARGS_KEY_ADAPTER, adapterClass.getName());
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        String layoutMgrClass = getArguments().getString(ARGS_KEY_LAYOUT_MANAGER);
//        if (GridLayoutManager.class.getName().equals(layoutMgrClass)) {
//            mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
//        } else {
            mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
//        }

//        String adapterClass = getArguments().getString(ARGS_KEY_ADAPTER);
//        if (VideoListAdapter.class.getName().equals(adapterClass)) {
//            mAdapter = new VideoListAdapter(getActivity());
//        } else {
//            mAdapter = new RestaurantListAdapter(getActivity());
//        }

        Parcel parcel =Parcel.obtain() ;

        Pagination pag =  new Pagination(parcel);
        pag.limit = 2;
        pag.offset = 2;
        pag.total = 10;
        Paginator page =  new Paginator();
        page.addPage(pag);

        mLoader = new UrlListLoader<HomeRestuarant.HomeRestuarantList>(
                mRequestTag,HomeRestuarant.HomeRestuarantList.class, page );


        mLoader.setUseCache(true);
       String url = ServerAPI.Home.buildRestuarantUrl("北京");
        mLoader.setUrl(url);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        reload();
        super.onActivityCreated(savedInstanceState);
    }

    private void reload() {
        mPullToRefreshView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);

//        int scenicId = getArguments().getInt(Const.ARGS_SCENIC_ID);
        // TODO
        mLoader.loadMoreData(this, DataLoader.MODE_LOAD_MORE);
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity.getApplicationContext();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_rest_recycler_list, container, false);
//        initActionBar(mRootView);
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
                        Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH
                                : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreData(HotelDetailFragment.this, mode);
            }
        });

        RecyclerView recyclerView = mPullToRefreshView.getRefreshableView();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int scap = ScreenUtils.dpToPxInt(getActivity(), 4);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(recyclerView);
        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(mAdapter);
        return mRootView;
    }
    /*private void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.home_nearby_restaurant);
        actionBar.getLeftView() .setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(this);
        // actionBar.getRight2View().setImageResource(R.drawable.ic_action_bar_search_selecter);
        // actionBar.getRight2View().setOnClickListener(this);
    }*/
    @Override
    public void onLoadFinished(HomeRestuarant.HomeRestuarantList videoList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();

        List<HomeRestuarant> list = null;
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
                ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter())
                        .setDataItems(mVideoList);
            }

            mPullToRefreshView.setVisibility(View.VISIBLE);
            for (HomeRestuarant liveVideo : list) {
//                if (liveVideo.id <= 0) {
//                    continue;
//                }
                HomeRestuarant rest = new HomeRestuarant();
                rest.name = liveVideo.name;
                rest.address = liveVideo.address;
                rest.avg_rating = liveVideo.avg_rating;
                rest.s_photo_url = liveVideo.s_photo_url;
                rest.business_id = liveVideo.business_id;
                rest.latitude = liveVideo.latitude;
                rest.longitude = liveVideo.longitude;
                rest.photo_url = liveVideo.photo_url;
                mVideoList.add(rest);
            }
            ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter())
                    .appendDataItems(mVideoList);
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
