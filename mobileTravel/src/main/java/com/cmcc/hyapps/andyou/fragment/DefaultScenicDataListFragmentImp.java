
package com.cmcc.hyapps.andyou.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.BasicScenicDataListAdapter;
import com.cmcc.hyapps.andyou.adapter.DefaultScenicDataListAdapterImp;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

public class DefaultScenicDataListFragmentImp extends BasicScenicDataListFragment {

    @Override
    public BasicScenicDataListAdapter initAdapter() {
        return new DefaultScenicDataListAdapterImp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_recycler_list, container, false);
        mPullToRefreshView = (PullToRefreshRecyclerView) rootView.findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(Mode.BOTH);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH
                                : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreData(DefaultScenicDataListFragmentImp.this, mode);
            }
        });
        int padding = ScreenUtils.dpToPxInt(mContext, 6);
        rootView.setPadding(padding, padding, padding, 0);

        mEmptyHintView = rootView.findViewById(R.id.empty_hint_view);
        mLoadingProgress = rootView.findViewById(R.id.loading_progress);
        mSpotsRecyclerView = mPullToRefreshView.getRefreshableView();
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        mSpotsRecyclerView.setLayoutManager(layoutManager);
        mSpotsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int scap = ScreenUtils.dpToPxInt(mContext, 6);
        DividerItemDecoration decor = new DividerItemDecoration(scap, scap);
        decor.initWithRecyclerView(mSpotsRecyclerView);
        mSpotsRecyclerView.addItemDecoration(decor);

        mSpotsRecyclerView.setAdapter(mAdapter);
        return rootView;
    }
}
