
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.CollectionRaidersListAdapter;
import com.cmcc.hyapps.andyou.adapter.CollectionRoutelListAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHCollectionRoute;
import com.cmcc.hyapps.andyou.model.QHCollectionStrategy;
import com.cmcc.hyapps.andyou.model.QHUser;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;
//使用FreshCollectionListFragment替换
public class CollectionListFragment extends BaseFragment implements DataLoaderCallback<QHCollectionRoute.List>,
       OnClickListener {
    private PullToRefreshRecyclerView routesRefreshView;
    private RecyclerView routes_listView;
    private PullToRefreshRecyclerView raidersfreshView;
    private RecyclerView raiders_listView;
    private Context mContext;
    private ArrayList<QHCollectionStrategy> raidersListData = new ArrayList<QHCollectionStrategy>();
    private ArrayList<QHCollectionRoute> rountesListData = new ArrayList<QHCollectionRoute>();
    private UrlListLoader<QHCollectionRoute.List> mLoader;
    private UrlListLoader<QHCollectionStrategy.List> raidersmLoader;//推荐
    private AppendableAdapter<QHCollectionRoute> rountesAdapter;
    private AppendableAdapter<QHCollectionStrategy> raidersAdapter;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mRootView;
    private View mReloadView;
    private LayoutManager rountrsLayoutManager;
    private LayoutManager raidersLayoutManager;
    private static final String ARGS_KEY_LAYOUT_MANAGER = "args_key_layout_manager";
    private static final String ARGS_KEY_ADAPTER = "args_key_adapter";
    private Location myLocation;
    private RadioGroup switch_ll;
    private RaiersListener raiersListener = new RaiersListener();

    private int state_type = 0;//0 攻略  1 路线。
    QHUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        rountrsLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        raidersLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rountesAdapter = new CollectionRoutelListAdapter(getActivity());
        raidersAdapter = new CollectionRaidersListAdapter(getActivity());
        user = AppUtils.getQHUser(getActivity());
        initLoadUrl();
        super.onCreate(savedInstanceState);
    }
    private void initLoadUrl(){
        //加载攻略信息
        mLoader = new UrlListLoader<QHCollectionRoute.List>(mRequestTag,QHCollectionRoute.List.class/*, page*/ );
        mLoader.setUseCache(false);
        String  url = ServerAPI.User.buildCollectionInfo(3,user.id);
        mLoader.setUrl(url);

        raidersmLoader = new UrlListLoader<QHCollectionStrategy.List>(mRequestTag,QHCollectionStrategy.List.class);
        raidersmLoader.setUseCache(true);
        String  urlraiders = ServerAPI.User.buildCollectionInfo(2,user.id);
        raidersmLoader.setUrl(urlraiders);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        reload();
        super.onActivityCreated(savedInstanceState);
    }

    private void reload() {
        if(state_type==0){
            raidersfreshView.setVisibility(View.GONE);
            mLoadingProgress.setVisibility(View.VISIBLE);
            mReloadView.setVisibility(View.GONE);
            raidersmLoader.loadMoreQHData(raiersListener, DataLoader.MODE_LOAD_MORE);
        }else if(state_type==1){
            routesRefreshView.setVisibility(View.GONE);
            mLoadingProgress.setVisibility(View.VISIBLE);
            mReloadView.setVisibility(View.GONE);
            mLoader.loadMoreQHData(this, DataLoader.MODE_LOAD_MORE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity.getApplicationContext();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.collection_fragment, container, false);
//        initActionBar(mRootView);
        mEmptyHintView = mRootView.findViewById(R.id.empty_hint_view);
        if (mLoadingProgress != null) {
            mRootView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        } else {
            mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
        }
        mReloadView = mRootView.findViewById(R.id.reload_view);
        mReloadView.setOnClickListener(this);
        initListView();

        switch_ll = (RadioGroup)mRootView.findViewById(R.id.fragment_switch);
        switch_ll.setVisibility(View.VISIBLE);
        switch_ll.check(R.id.me_strategy);
        switch_ll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.me_strategy){
                    state_type = 0;
                    resetStateView(0);
                }else if(checkedId==R.id.me_route){
                    state_type = 1;
                    resetStateView(1);
                }
            }
        });
        initActionBar(mRootView);
        return mRootView;
    }
    private void initListView(){

        routesRefreshView = (PullToRefreshRecyclerView) mRootView.findViewById(R.id.pulltorefresh_twowayview);
        routesRefreshView.setMode(Mode.BOTH);
        routesRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH
                        : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreData(CollectionListFragment.this, mode);
            }
        });
        routes_listView = routesRefreshView.getRefreshableView();
        routes_listView.setLayoutManager(rountrsLayoutManager);
        routes_listView.setItemAnimator(new DefaultItemAnimator());
        int scap = ScreenUtils.dpToPxInt(getActivity(), 4);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(routes_listView);
        routes_listView.addItemDecoration(decor);
        routes_listView.setAdapter(rountesAdapter);


        raidersfreshView = (PullToRefreshRecyclerView) mRootView.findViewById(R.id.pulltorefresh_newview);
        raidersfreshView.setMode(Mode.BOTH);
        raidersfreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH: DataLoader.MODE_LOAD_MORE;
                raidersmLoader.loadMoreData(raiersListener, mode);
            }
        });
        raiders_listView = raidersfreshView.getRefreshableView();
        raiders_listView.setLayoutManager(raidersLayoutManager);
        raiders_listView.setItemAnimator(new DefaultItemAnimator());
        decor.initWithRecyclerView(raiders_listView);
        raiders_listView.addItemDecoration(decor);
        raiders_listView.setAdapter(raidersAdapter);
    }

    public void resetStateView(int type){
        mEmptyHintView.setVisibility(View.GONE);
        if(type==0){
            raidersfreshView.setVisibility(View.VISIBLE);
            raiders_listView.setVisibility(View.VISIBLE);
            routesRefreshView.setVisibility(View.GONE );
            routes_listView.setVisibility(View.GONE);
            if (raidersListData.isEmpty()) {
                reload();
            }
        }
        else if(type==1){
            raidersfreshView.setVisibility(View.GONE);
            raiders_listView.setVisibility(View.GONE);
            routesRefreshView.setVisibility(View.VISIBLE);
            routes_listView.setVisibility(View.VISIBLE);
            if (rountesListData.isEmpty()) {
                reload();
            }
        }
    }
    @Override
    public void onLoadFinished(QHCollectionRoute.List videoList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        routesRefreshView.onRefreshComplete();

        List<QHCollectionRoute> list = null;
        if (videoList != null) {
            list = videoList.results;
        }
        if (list == null || list.isEmpty()) {
            if (rountesListData.isEmpty()) {
                mEmptyHintView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mode == DataLoader.MODE_REFRESH ||null==videoList.pagination|| videoList.pagination.offset == 0) {
                rountesListData.clear();
                rountesAdapter.setDataItems(rountesListData);
            }
            mEmptyHintView.setVisibility(View.GONE);
            routesRefreshView.setVisibility(View.VISIBLE);
            rountesListData.addAll(videoList.results);
            rountesAdapter.appendDataItems(videoList.results);
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
    protected void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.getTitleView().setText("我的收藏");
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setOnClickListener(this);
//        mActionBar.getRightView().setImageResource(R.drawable.);
    }


    public class RaiersListener implements DataLoaderCallback<QHCollectionStrategy.List>{

        @Override
        public void onLoadFinished(QHCollectionStrategy.List videoList, int mode) {
            mLoadingProgress.setVisibility(View.GONE);
            raidersfreshView.onRefreshComplete();
            List<QHCollectionStrategy> list = null;
            if (videoList != null) {
                list = videoList.results;
            }
            if (list == null || list.isEmpty()) {
                if (raidersListData.isEmpty()) {
                    mEmptyHintView.setVisibility(View.VISIBLE);
                }
            } else {
                if (mode == DataLoader.MODE_REFRESH || null == videoList.pagination || videoList.pagination.offset == 0) {
                    raidersListData.clear();
                    ((AppendableAdapter) raidersfreshView.getRefreshableView().getAdapter()).setDataItems(raidersListData);
                }
                raidersfreshView.setVisibility(View.VISIBLE);
                raidersListData.addAll(videoList.results);
                raidersAdapter.appendDataItems(videoList.results);
            }
        }
        @Override
        public void onLoadError(int mode) {
            mLoadingProgress.setVisibility(View.GONE);
            mReloadView.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, R.string.loading_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
