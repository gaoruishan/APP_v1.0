package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.RoadConditionListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.RoadVideo;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;

import java.util.List;

/**
 * Created by bingbing on 2015/10/8.
 */
public class RoadConditionListFragment extends BaseFragment implements DataLoader.DataLoaderCallback<RoadVideo.RoadVideoList>, View.OnClickListener {

    private PullToRefreshRecyclerView mPullToRefreshView;
    private Context mContext;
    private UrlListLoader<RoadVideo.RoadVideoList> mLoader;
    private AppendableAdapter<RoadVideo> mAdapter;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mRootView;
    private View mReloadView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mRequestTag = HotelsListFragment.class.getName();
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mAdapter = new RoadConditionListAdapter(getActivity());
        loadList(getArguments().getString(Const.ARGS_LOADER_URL));
        super.onCreate(savedInstanceState);
    }

    public void loadList(String url) {

        mLoader = new UrlListLoader<RoadVideo.RoadVideoList>(mRequestTag, RoadVideo.RoadVideoList.class);
        mLoader.setUseCache(false);

        mLoader.setUrl(url);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        reload();
        super.onActivityCreated(savedInstanceState);
    }

    public void reload() {
        reload(DataLoader.MODE_REFRESH);
    }

    public void reload(int mode) {

        if (mEmptyHintView.isShown())
            mEmptyHintView.setVisibility(View.GONE);
        mPullToRefreshView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);

        mAdapter.setDataItems(null);
        // TODO
        mLoader.loadMoreQHData(this, mode);
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

        mPullToRefreshView = (PullToRefreshRecyclerView) mRootView
                .findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    reload();
                } else {
                    //下拉加载
                    mLoader.loadMoreQHData(RoadConditionListFragment.this, DataLoader.MODE_LOAD_MORE);
                }
            }
        });

        RecyclerView recyclerView = mPullToRefreshView.getRefreshableView();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int scap = ScreenUtils.dpToPxInt(getActivity(), 1);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(recyclerView);
        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(mAdapter);
        return mRootView;
    }

    @Override
    public void onLoadFinished(RoadVideo.RoadVideoList shopList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();

        List<RoadVideo> list = null;
        if (shopList != null && shopList.results != null && shopList.results.size() != 0) {
            list = shopList.results;
        }

        if (list == null || list.isEmpty()) {
            if (mode == DataLoader.MODE_REFRESH || mAdapter.getItemCount() == 0) {
                mEmptyHintView.setVisibility(View.VISIBLE);
                mPullToRefreshView.setVisibility(View.GONE);

            } else {
                mPullToRefreshView.setVisibility(View.VISIBLE);
                ToastUtils.AvoidRepeatToastShow(getActivity(), R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        } else {
            mEmptyHintView.setVisibility(View.GONE);
            mPullToRefreshView.setVisibility(View.VISIBLE);

            if (mode == DataLoader.MODE_REFRESH) {
                mAdapter.setDataItems(shopList.results);
                return;
            }

            mAdapter.appendDataItems(shopList.results);
        }
    }

    @Override
    public void onLoadError(int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mEmptyHintView.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        } else if (v.getId() == R.id.reload_view) {
            reload();
        } else if (v.getId() == R.id.action_bar_left) {
            getActivity().finish();
        }
    }

    public void attachLoadingProgress(View loadingProgress) {
        mLoadingProgress = loadingProgress;
    }
}
