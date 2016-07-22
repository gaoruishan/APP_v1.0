package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.EntertainmentsDetailActivity;
import com.cmcc.hyapps.andyou.adapter.EnjoyListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.QHEnjoy;
import com.cmcc.hyapps.andyou.model.QHMarketShop;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingbing on 2015/8/20.
 */
public class EnjoyListFragment extends BaseFragment implements View.OnClickListener, DataLoader.DataLoaderCallback<QHEnjoy.QHEnjoyList> {
    private View mEmptyHintView;
    private View mLoadingProgress;
    private View mReloadView;
    private PullToRefreshRecyclerView mPullToRefreshView;
    private View mRootView;
    private UrlListLoader<QHEnjoy.QHEnjoyList> mLoader;
    private AppendableAdapter<QHEnjoy> mAdapter;
    private Context mContext;
    private ArrayList<QHEnjoy> mEnjoyList = new ArrayList<QHEnjoy>();

    @Override
    public void onAttach(Activity activity) {
        mContext = activity.getApplicationContext();
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLoader = new UrlListLoader<QHEnjoy.QHEnjoyList>(mRequestTag,QHEnjoy.QHEnjoyList.class);
        mLoader.setUseCache(false);
        mLoader.setUrl(getArguments().getString(Const.ARGS_LOADER_URL));
        mAdapter = new EnjoyListAdapter(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_recycler_list, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        mEmptyHintView = mRootView.findViewById(R.id.empty_hint_view);
        if (mLoadingProgress != null) {
            mLoadingProgress.setVisibility(View.GONE);
        } else {
            mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
        mReloadView = mRootView.findViewById(R.id.reload_view);
        mReloadView.setOnClickListener(this);

        initRecycleView();
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        if (v.getId() == R.id.reload_view) {
            reload();
        }
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
        mLoader.loadMoreQHData(this, DataLoader.MODE_REFRESH);
    }
    public void reload(int mode) {

        if (mEmptyHintView.isShown())
            mEmptyHintView.setVisibility(View.GONE);
        mPullToRefreshView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);

        mAdapter.setDataItems(null);
        mLoader.loadMoreQHData(this, mode);
    }

    private void initRecycleView() {
        mPullToRefreshView = (PullToRefreshRecyclerView) mRootView
                .findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        PullToRefreshBase.Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH
                        : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreQHData(EnjoyListFragment.this, mode);
            }
        });

        RecyclerView recyclerView = mPullToRefreshView.getRefreshableView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        onItemClick(recyclerView);
        int scap = ScreenUtils.dpToPxInt(getActivity(), 4);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(recyclerView);
        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(mAdapter);
    }

    public void attachLoadingProgress(View loadingProgress) {
        mLoadingProgress = loadingProgress;
    }

    @Override
    public void onLoadFinished(QHEnjoy.QHEnjoyList QHEnjoyList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();

        List<QHEnjoy> list = null;
        if (QHEnjoyList != null && QHEnjoyList.results != null && QHEnjoyList.results.size() != 0) {
            list = QHEnjoyList.results;
        }

        if (list == null || list.isEmpty()) {
            if (mEnjoyList.isEmpty()) {
                mEmptyHintView.setVisibility(View.VISIBLE);
            }else {
                ToastUtils.AvoidRepeatToastShow(getActivity(), R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        } else {
            if (mode == DataLoader.MODE_REFRESH/* || videoList.pagination.offset == 0*/) {
                mEnjoyList.clear();
                ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter())
                        .setDataItems(mEnjoyList);
            }

            mPullToRefreshView.setVisibility(View.VISIBLE);
            mEnjoyList = (ArrayList<QHEnjoy>) list;
            ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter())
                    .appendDataItems(list);
        }
    }

    @Override
    public void onLoadError(int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
        Toast.makeText(mContext, R.string.loading_failed, Toast.LENGTH_SHORT).show();
    }

    private void onItemClick(RecyclerView recyclerView){
        ItemClickSupport clickSupport = ItemClickSupport.addTo(recyclerView);
        clickSupport.setOnItemSubViewClickListener(new ItemClickSupport.OnItemSubViewClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                QHEnjoy item = (QHEnjoy) view.getTag();
                if (item != null) {
                    Intent intent = new Intent(EnjoyListFragment.this.getActivity(), EntertainmentsDetailActivity.class);
                    intent.putExtra("entertainment", item.getId());
                    startActivity(intent);
                }
            }
        });
    }

    public void loadList(String url) {
        mLoader = new UrlListLoader<QHEnjoy.QHEnjoyList>(mRequestTag, QHEnjoy.QHEnjoyList.class);
        mLoader.setUseCache(false);

        mLoader.setUrl(url);
}
    public void clearList(){
        if (mEnjoyList != null)
            mEnjoyList.clear();
    }
}
