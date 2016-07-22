
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemSubViewClickListener;
import com.umeng.analytics.MobclickAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.GuiderRaidersCommentListAdapter;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.Scenic;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.ArrayList;

public class GuiderDemoListFragment extends BaseFragment implements OnClickListener,DataLoaderCallback<Comment.CommentList> {
    private final String TAG = getClass().getName();//"GuiderListFragment";
    private final int REQUEST_CODE_SEARCH = 4;
    private ActionBar mActionBar;
    private GuiderRaidersCommentListAdapter mAdapter;
    private View mLoadingProgress;
    private View mReloadView;
    private PullToRefreshRecyclerView mPullToRefreshView;
    private UrlListLoader<Comment.CommentList> mScenicListLoader;
//    private Request<HomeBanner.HomeBannerLists> mBannerRequest;
    private ViewGroup mRootView;
    private View mEmptyHintView;
    private Scenic mScenic;
    private int HTTP_GET_PARAM_LIMIT = 10;
    private RecyclerView commentListView;

    private ArrayList<Comment> raidersListData = new ArrayList<Comment>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareManager.getInstance().onStart(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.guide_list_fragment, container,false);
        initViews();
        reload();
        loadCommentData("北京");
        initActionBar(mRootView);
        return mRootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageEnd(TAG);
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
    private void reload() {
        mPullToRefreshView.setMode(Mode.PULL_FROM_END);
        commentListView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);
        mScenicListLoader = null;
        RequestManager.getInstance().getRequestQueue().cancelAll(mRequestTag);
    }
    private void initViews() {
        initListView();
        initPullToRefresh();
        mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
        mReloadView = mRootView.findViewById(R.id.reload_view);
        mEmptyHintView = mRootView.findViewById(R.id.empty_hint_view);
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
//                    loadNearScenics(mScenicDetailsModel.cityZh);
                }
            }
        });
    }
    private void initListView() {
        commentListView = (RecyclerView) mRootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        commentListView.setLayoutManager(layoutManager);
        mAdapter = new GuiderRaidersCommentListAdapter(getActivity());
        ItemClickSupport clickSupport = ItemClickSupport.addTo(commentListView);
        clickSupport.setOnItemSubViewClickListener(new OnItemSubViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                    onItemClicked(view);
            }
        });

        commentListView.setItemAnimator(new DefaultItemAnimator());
        commentListView.setAdapter(mAdapter);
    }

    protected void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.getTitleView().setText("评论");
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setOnClickListener(this);

        actionBar.setBackgroundResource(R.drawable.fg_top_shadow);
        actionBar.getRightView().setImageResource(R.drawable.icon_edit);
        actionBar.getRightView().setOnClickListener(this);

//        mActionBar.getRightView().setImageResource(R.drawable.);
    }


    private void showReloadView() {
        commentListView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
    }


    private void loadCommentData(String cityName) {
        Log.e("-------","loadNearScenics--");
        mLoadingProgress.setVisibility(View.VISIBLE);
        commentListView.setVisibility(View.GONE);

        if (mScenicListLoader == null) {
            mScenicListLoader = new UrlListLoader<Comment.CommentList>( mRequestTag, Comment.CommentList.class);
            String  url  = ServerAPI.User.buildCommentUrl(2);
            mScenicListLoader.setUrl(url);
            mScenicListLoader.setPageLimit(HTTP_GET_PARAM_LIMIT);
        }
        mScenicListLoader.loadMoreData(this, DataLoader.MODE_LOAD_MORE);
    }
    @Override
    public void onLoadFinished(Comment.CommentList list, int mode) {
        mPullToRefreshView.onRefreshComplete();
        onListLoaded(list);
    }

    @Override
    public void onLoadError(int mode) {
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        commentListView.setVisibility(View.VISIBLE);
    }
    private void onListLoaded(Comment.CommentList data) {
        mLoadingProgress.setVisibility(View.GONE);
        commentListView.setVisibility(View.VISIBLE);

        if (data == null || data.list == null|| (mScenicListLoader != null && !mScenicListLoader.getPaginator().hasMorePages())) {
            // Disable PULL_FROM_END
//            mPullToRefreshView.setMode(Mode.PULL_FROM_END);
//            return;
            if (raidersListData.isEmpty()) {
                mEmptyHintView.setVisibility(View.VISIBLE);
            }
        }
        if (data != null) {
            if (data.pagination != null && data.pagination.offset == 0) {
                mAdapter.setDataItems(null);
            }
            mAdapter.appendDataItems(data.results);
            raidersListData.addAll(data.results);
            mAdapter.notifyDataSetChanged();
        }
    }
    private void onItemClicked(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        mScenic = (Scenic) v.getTag();
        if (mScenic == null) {
            Log.e("NULL mScenic");
            return;
        }
        switch (v.getId()) {
            case R.id.iv_cover_image:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_HOME_SECNIC);
//                Intent intent = new Intent(getActivity(), SecnicActivityGuide.class);
//                intent.putExtra(Const.EXTRA_NAME,mScenic.name);
//                intent.putExtra(Const.EXTRA_ID,mScenic.id);
//                startActivity(intent);
                break;
            default:
                break;
        }
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
            case R.id.action_bar_right_text: {
//                Intent intent = new Intent(getActivity(), CityListActivityGuide.class);
//                startActivityForResult(intent, REQUEST_CODE_SEARCH);
//                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_SCENIC_SEARCH);
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

    @Override
    public void onDestroy() {
        ShareManager.getInstance().onEnd();
        super.onDestroy();
    }



}
