
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
import com.cmcc.hyapps.andyou.adapter.GuiderRaidersCommentListAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GuiderRadiersCommentListOldFragment extends BaseFragment implements  OnClickListener {
    private PullToRefreshRecyclerView raidersfreshView;
    private RecyclerView raiders_listView;
    private Context mContext;
    private ArrayList<Comment> raidersListData = new ArrayList<Comment>();
    private UrlListLoader<Comment.CommentList> raidersmLoader;//推荐
    private AppendableAdapter<Comment> raidersAdapter;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mRootView;
    private View mReloadView;
    private LayoutManager raidersLayoutManager;
    private static final String ARGS_KEY_LAYOUT_MANAGER = "args_key_layout_manager";
    private static final String ARGS_KEY_ADAPTER = "args_key_adapter";
    private Location myLocation;
//    private RadioGroup switch_ll;
    private RaiersListener raiersListener;
//    private int state_type = 0;//0 攻略  1 路线。

    @Override
    public void onCreate(Bundle savedInstanceState) {
        raidersLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        raidersAdapter = new GuiderRaidersCommentListAdapter(getActivity());
        initLoadUrl();
        super.onCreate(savedInstanceState);
    }
    private void initLoadUrl(){
        raidersmLoader = new UrlListLoader<Comment.CommentList>(mRequestTag,Comment.CommentList.class);
        raidersmLoader.setUseCache(true);
        String  urlraiders = ServerAPI.User.buildCommentUrl(2);
        raidersmLoader.setUrl(urlraiders);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        reload();
        super.onActivityCreated(savedInstanceState);
    }

    private void reload() {
            raidersfreshView.setVisibility(View.GONE);
            mLoadingProgress.setVisibility(View.VISIBLE);
            mReloadView.setVisibility(View.GONE);
            raidersmLoader.loadMoreQHData(raiersListener, DataLoader.MODE_LOAD_MORE);

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

        initActionBar(mRootView);
        return mRootView;
    }
    private void initListView(){


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
        raiders_listView.setAdapter(raidersAdapter);
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
        actionBar.getTitleView().setText("评论");
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setOnClickListener(this);
//        mActionBar.getRightView().setImageResource(R.drawable.);
    }


    public class RaiersListener implements DataLoaderCallback<Comment.CommentList>{

        @Override
        public void onLoadFinished(Comment.CommentList videoList, int mode) {
            mLoadingProgress.setVisibility(View.GONE);
            raidersfreshView.onRefreshComplete();
            List<Comment> list = null;
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
