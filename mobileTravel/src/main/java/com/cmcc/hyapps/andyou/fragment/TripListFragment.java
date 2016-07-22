
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemLongClickListener;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.TripDetailEditActivity;
import com.cmcc.hyapps.andyou.adapter.TripListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Trip;
import com.cmcc.hyapps.andyou.model.Trip.TripList;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

public class TripListFragment extends BaseFragment implements DataLoaderCallback<TripList> {
    private PullToRefreshRecyclerView mPullRefreshScrollView;
    private TripListAdapter mAdapter;

    private View mLoadingProgress;
    private View mEmptyHintView;
    private UrlListLoader<TripList> mTripLoader;
    private boolean mEditable;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mPullRefreshScrollView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        loadTrips();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_recycler_list, container, false);
        int scap = ScreenUtils.dpToPxInt(getActivity(), 6);
        rootView.setPadding(scap, scap, scap, scap);
        mEmptyHintView = rootView.findViewById(R.id.empty_hint_view);
        mLoadingProgress = rootView.findViewById(R.id.loading_progress);
        mPullRefreshScrollView = (PullToRefreshRecyclerView) rootView.findViewById(R.id.pulltorefresh_twowayview);

        mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    mTripLoader = null;
                    mAdapter.setDataItems(null);
                    mPullRefreshScrollView.setMode(Mode.BOTH);
                    loadTrips();
                } else {
                    loadTrips();
                }

            }

        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()
                .getApplicationContext());
        mPullRefreshScrollView.getRefreshableView().setLayoutManager(layoutManager);
        mPullRefreshScrollView.getRefreshableView().setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration decor = new DividerItemDecoration(scap * 2 / 3);
        decor.initWithRecyclerView(mPullRefreshScrollView.getRefreshableView());
        mPullRefreshScrollView.getRefreshableView().addItemDecoration(decor);

        mEditable = getArguments().getBoolean("editable", false);
        ItemClickSupport clickSupport = ItemClickSupport.addTo(mPullRefreshScrollView.getRefreshableView());
        if (mEditable) {
            clickSupport.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(RecyclerView parent, View view, int position, long id) {
                    if (mAdapter != null) {
                        mAdapter.setEditMode(!mAdapter.isEditMode());
                    }
                    return true;
                }
            });
        } else {
            clickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {

                @Override
                public void onItemClick(RecyclerView parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), TripDetailEditActivity.class);
                    intent.putExtra(Const.EXTRA_ID, (int) mAdapter.getItemId(position));
                    startActivity(intent);
                }
            });
        }
        mAdapter = new TripListAdapter(mEditable);
        mPullRefreshScrollView.getRefreshableView().setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void loadTrips() {
        if (mTripLoader == null) {
            mTripLoader = new UrlListLoader<TripList>(
                    mRequestTag, TripList.class);
            mTripLoader.setUrl(getArguments().getString(Const.ARGS_LOADER_URL));
        }

        mTripLoader.loadMoreData(this, DataLoader.MODE_LOAD_MORE);
    }

    @Override
    public void onLoadFinished(TripList tripList, int mode) {
        mPullRefreshScrollView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);

        if (tripList == null || tripList.list == null
                || (mTripLoader != null && !mTripLoader.getPaginator().hasMorePages())) {
            // Disable PULL_FROM_END
            mPullRefreshScrollView.setMode(Mode.PULL_FROM_START);
        }
        if (tripList == null || tripList.list == null || tripList.list.isEmpty()) {
            if (mAdapter.getItemCount() == 0) {
                mEmptyHintView.setVisibility(View.VISIBLE);
            }
        } else {
            if (ServerAPI.User.URL_USER_TRIPS.equals(mTripLoader.getUrl())) {
                for (Trip trip : tripList.list) {
                    trip.author = AppUtils.getUser(getActivity());
                }
            }

            mPullRefreshScrollView.setVisibility(View.VISIBLE);
            if (mAdapter != null) {
                mAdapter.appendDataItems(tripList.list);
            }
        }
    }

    @Override
    public void onLoadError(int mode) {
        mPullRefreshScrollView.onRefreshComplete();
    }

}
