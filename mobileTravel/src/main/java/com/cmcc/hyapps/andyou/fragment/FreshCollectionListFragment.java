package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.row.QHRowEnjoyCollection;
import com.cmcc.hyapps.andyou.adapter.row.QHRowRouteCollection;
import com.cmcc.hyapps.andyou.adapter.row.QHRowStraCollection;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.QHCollectionStrategy;
import com.cmcc.hyapps.andyou.model.QHUser;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;

import java.util.List;

/**
 * Created by Administrator on 2015/7/16 0016.
 */
public class FreshCollectionListFragment extends BaseFragment implements DataLoader.DataLoaderCallback<QHCollectionStrategy.List>,View.OnClickListener{
    private PullToRefreshRecyclerView mPullToRefreshView;
    private Context mContext;
    private UrlListLoader<QHCollectionStrategy.List> mLoader;

    private AppendableAdapter<QHCollectionStrategy> mAdapter;

    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mRootView;
    private View mReloadView;
    private RecyclerView.LayoutManager mLayoutManager;

    private String user_id;
    QHUser user;
    @Override
    public void onLoadFinished(QHCollectionStrategy.List videoList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();

        List<QHCollectionStrategy> list = null;
        if (videoList != null && videoList.results != null && videoList.results.size() != 0) {
            list = videoList.results;
        }

        if (list == null || list.isEmpty()) {
            if (mode == DataLoader.MODE_REFRESH) {
                mPullToRefreshView.setVisibility(View.INVISIBLE);
                mEmptyHintView.setVisibility(View.VISIBLE);
            } else {
                mPullToRefreshView.setVisibility(View.VISIBLE);
                ToastUtils.AvoidRepeatToastShow(getActivity(), R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        } else {
            mPullToRefreshView.setVisibility(View.VISIBLE);
            mEmptyHintView.setVisibility(View.GONE);
            mPullToRefreshView.setVisibility(View.VISIBLE);

            if (mode == DataLoader.MODE_REFRESH) {
                mAdapter.setDataItems(videoList.results);
                return;
            }

            mAdapter.appendDataItems(videoList.results);
        }
    }

    @Override
    public void onLoadError(int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mEmptyHintView.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        if (getArguments() != null){
            user_id = getArguments().getString("user_id");
        }else {
            user = AppUtils.getQHUser(getActivity());
        }
        mAdapter = new FreshCollectionAdapter(getActivity());
    }
    public void loadList(){

        mLoader = new UrlListLoader<QHCollectionStrategy.List>(  mRequestTag,QHCollectionStrategy.List.class/*, page*/ );
        String url;
        if (TextUtils.isEmpty(user_id))
        url = ServerAPI.User.buildCollectionAllInfo(user.id);
        else
        url = ServerAPI.User.buildCollectionAllInfo(Integer.parseInt(user_id));
        mLoader.setUrl(url);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        reload();
        super.onActivityCreated(savedInstanceState);
    }
    public void reload() {
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

    @Nullable
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
        mPullToRefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        PullToRefreshBase.Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreQHData(FreshCollectionListFragment.this, mode);
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
    public void onClick(View view) {

    }
    public void attachLoadingProgress(View loadingProgress) {
        mLoadingProgress = loadingProgress;
    }

    private class FreshCollectionAdapter extends AppendableAdapter<QHCollectionStrategy>{
        public static final int COLLECTION_STRA = 2;//攻略
        public static final int COLLECTION_ROUTE = 3;//路线
        public static final int COLLECTION_ENJOY = 6;//路线
      //  private List<QHCollectionStrategy> collection = new ArrayList<QHCollectionStrategy>();
        private Context context;
        public FreshCollectionAdapter(Context context){
            this.context = context;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            switch (viewType){
                case COLLECTION_STRA:
                    viewHolder = QHRowStraCollection.onCreateViewHolder(parent);
                    break;
                case COLLECTION_ROUTE:
                    viewHolder = QHRowRouteCollection.onCreateViewHolder(parent);
                    break;
                case COLLECTION_ENJOY:
                    viewHolder = QHRowEnjoyCollection.onCreateViewHolder(parent);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int stype = mDataItems.get(position).obj_type;
            switch (stype){
                case COLLECTION_STRA:
                    QHRowStraCollection.onBindViewHolder(context, holder, position, mDataItems.get(position));
                    break;
                case COLLECTION_ROUTE:
                    QHRowRouteCollection.onBindViewHolder(context,holder,position,mDataItems.get(position));
                    break;
                case COLLECTION_ENJOY:
                    QHRowEnjoyCollection.onBindViewHolder(context, holder, position, mDataItems.get(position));
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            int type = mDataItems.get(position).obj_type;
            switch (type){
                case COLLECTION_STRA:
                    return COLLECTION_STRA;
                case COLLECTION_ROUTE:
                    return COLLECTION_ROUTE;
                case COLLECTION_ENJOY:
                    return COLLECTION_ENJOY;
                default:
                    return 100;
            }

        }
    }
}
