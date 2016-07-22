
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
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

import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.SpecialListAdapter;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StrategyListFragment extends BaseFragment implements
        DataLoaderCallback<QHStrategy.QHStrategyList>, OnClickListener {
    private PullToRefreshRecyclerView mPullToRefreshView;
    private Context mContext;
    private ArrayList<QHStrategy> mVideoList = new ArrayList<QHStrategy>();
    private UrlListLoader<QHStrategy.QHStrategyList> mLoader;
    private AppendableAdapter<QHStrategy> mAdapter;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mRootView;
    private View mReloadView;
    private LayoutManager mLayoutManager;
    private static final String ARGS_KEY_LAYOUT_MANAGER = "args_key_layout_manager";
    private static final String ARGS_KEY_ADAPTER = "args_key_adapter";
    private Location myLocation;

    private boolean isReturnRefresh = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mAdapter = new SpecialListAdapter(getActivity());

        super.onCreate(savedInstanceState);
    }
    protected void loadList(){
        mLoader = new UrlListLoader<QHStrategy.QHStrategyList>(mRequestTag,QHStrategy.QHStrategyList.class/*, page*/ );
//        mLoader.setUseCache(true);
        String url = ServerAPI.StrategyList.buildStrategyListUrl();
        mLoader.setUrl(url);
    }
    public void loadList(String url){
        mVideoList.clear();
        //加载攻略信息
        mLoader = new UrlListLoader<QHStrategy.QHStrategyList>(  mRequestTag,QHStrategy.QHStrategyList.class/*, page*/ );
        mLoader.setUseCache(false);

        mLoader.setUrl(url);
        mLoader.loadMoreQHData(this, DataLoader.MODE_REFRESH);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        reload();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
      //  if (!isReturnRefresh)
      //   reload();
    //    else
         //   mLoader.loadMoreQHData(this, DataLoader.MODE_REFRESH);
    }

    public void reload() {
        mPullToRefreshView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);
        isReturnRefresh = true;
        if (mLoader == null){
            loadList();
        }
        mLoader.loadMoreQHData(this, DataLoader.MODE_REFRESH);
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity.getApplicationContext();
        super.onAttach(activity);
    }
    String city;
    String city_en;
    public void initLoacation(String city, String city_en){
        this.city = city;
        this.city_en = city_en;
        loadList();
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
        mPullToRefreshView.setMode(Mode.BOTH);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
//                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
//                    reload();
//                    //下拉加载
//                } else {
//                    mLoader.loadMoreQHData(StrategyListFragment.this, DataLoader.MODE_LOAD_MORE);
//                }
                int mode = refreshView.getCurrentMode() ==
                        Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreQHData(StrategyListFragment.this, mode);
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

    @Override
    public void onLoadFinished(QHStrategy.QHStrategyList videoList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();

        List<QHStrategy> list = null;
        if(videoList!=null && videoList.results != null && videoList.results.size() != 0) {
            list = videoList.results;
        }
        if (list == null || list.isEmpty()) {
            if (mVideoList.isEmpty()) {
                ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter())
                        .setDataItems(mVideoList);
                ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter()).notifyDataSetChanged();
                mEmptyHintView.setVisibility(View.VISIBLE);
            }else {
                ToastUtils.AvoidRepeatToastShow(getActivity(), R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        } else {
            if (mode == DataLoader.MODE_REFRESH) {
                mVideoList.clear();
                ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter())
                        .setDataItems(mVideoList);
            }

            mPullToRefreshView.setVisibility(View.VISIBLE);
            for (QHStrategy item : list) {
//                QHStrategy rest = new QHStrategy();
//                rest.name = liveVideo.name;
//                rest.description = liveVideo.description;
//                rest.city = liveVideo.city;
//                rest.imageUrl = liveVideo.imageUrl;
//                rest.url = liveVideo.url;

                mVideoList.add(item);
            }
            ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter()).appendDataItems(/*mVideoList*/videoList.results);
        }
    }

    @Override
    public void onLoadError(int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
//        Toast.makeText(mContext, R.string.loading_failed, Toast.LENGTH_SHORT).show();
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
    public void insertHead(List<QHStrategy> lists ){
        mAdapter.instertDataItemsAhead(lists);
    }
    public void attachLoadingProgress(View loadingProgress) {
        mLoadingProgress = loadingProgress;
    }
}
