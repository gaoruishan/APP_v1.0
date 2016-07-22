package com.cmcc.hyapps.andyou.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.HotelDetailsActivity;
import com.cmcc.hyapps.andyou.activity.RestaurantDetailActivity;
import com.cmcc.hyapps.andyou.activity.ShopDetailActivity;
import com.cmcc.hyapps.andyou.activity.SpecialDetailActivity;
import com.cmcc.hyapps.andyou.adapter.MarketSearchListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.QHMarketShop;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.StringUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.cmcc.hyapps.andyou.widget.circularprogressbar.CircularProgressBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by niuzhiguo on 2015/6/26.
 */
public class MarketSearchListFragment extends BaseFragment implements DataLoader.DataLoaderCallback<QHMarketShop.QHMarketShopList>, View.OnClickListener {
    public static final String TAG = "MarketSearchListFragment";
    private UrlListLoader<QHMarketShop.QHMarketShopList> mShopQHListLoader;
    private MarketSearchListAdapter mAdapter;
    private QHMarketShop mShop;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private long mSearchTiem;

    @InjectView(R.id.loading_progress)
    CircularProgressBar mLoadingProgress;
    @InjectView(R.id.search_content)
    EditText search_content;
    @InjectView(R.id.pulltorefresh_twowayview)
    PullToRefreshRecyclerView mPullToRefreshView;
    @InjectView(R.id.search_tv)
    TextView search_tv;
    @InjectView(R.id.empty_hint_view)
    View empty_hint_view;
    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = View.inflate(getActivity(), R.layout.activity_search_scenic, null);
        ButterKnife.inject(this, mRootView);
        initSearch();
        initPullToRefresh();
        initListView();
        return mRootView;
    }

    private void initSearch() {
        search_tv.setOnClickListener(this);
        search_content.setOnKeyListener(new View.OnKeyListener() {//输入完后按键盘上的搜索键【回车键改为了搜索键】

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {//修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(
                                    getActivity().getCurrentFocus()
                                            .getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    loadSearchShops();

                }
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoadingProgress.setVisibility(View.INVISIBLE);
        search_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    //pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        search_content.setFocusable(true);
        search_content.setFocusableInTouchMode(true);
        search_content.requestFocus();
//        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_tv:
                getActivity().finish();
                break;
        }
    }

    private void initPullToRefresh() {

        mPullToRefreshView = (PullToRefreshRecyclerView) mRootView.findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(Mode.PULL_FROM_END);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                loadMore();
            }
        });
    }

    private void loadSearchShops() {
        if ((System.currentTimeMillis() - mSearchTiem) < 300) {
            return; // Too fast
        }
        mSearchTiem = System.currentTimeMillis();
        String condition = search_content.getText().toString().trim();

        if (TextUtils.isEmpty(condition)){
            ToastUtils.AvoidRepeatToastShow(getActivity(),R.string.reload_search_input,Toast.LENGTH_SHORT);
            return;
        }
        if (StringUtils.isHasSpecialChar(condition)) {
            ToastUtils.AvoidRepeatToastShow(getActivity(),R.string.is_has_special_char,Toast.LENGTH_SHORT);
            return;
        }
        com.cmcc.hyapps.andyou.util.Log.d(TAG, "loadNearShops--loadMoreShops");
        mPullToRefreshView.setVisibility(View.GONE);
        empty_hint_view.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mShopQHListLoader = new UrlListLoader<QHMarketShop.QHMarketShopList>( mRequestTag, QHMarketShop.QHMarketShopList.class);

        String stype = getStype();
        String url = null;
        if (!TextUtils.isEmpty(stype)) {
            if (stype.equalsIgnoreCase(Const.MARKET_HOTEL))
                url = ServerAPI.HotelsList.buildSearchCommentUrl(condition);
            if (stype.equalsIgnoreCase(Const.MARKET_FOOD))
                url = ServerAPI.RestaurantList.buildSearchCommentUrl(condition);
            if (stype.equalsIgnoreCase(Const.MARKET_SPECIAL))
                url = ServerAPI.SpecialList.buildSearchCommentUrl(condition);
            if (stype.equalsIgnoreCase(Const.MARKET_4S))
                url = ServerAPI.FourSlist.buildSearchCommentUrl(condition);
        } else {
            url = ServerAPI.MarketShopList.buildSearchCommentUrl(condition);
        }
        mShopQHListLoader.setUrl(url);

        mAdapter.setDataItems(null);
        mShopQHListLoader.loadMoreQHData(this, DataLoader.MODE_REFRESH);
    }

    private void loadMore() {
        //加载更多
        mShopQHListLoader.loadMoreQHData(this, DataLoader.MODE_LOAD_MORE);
    }

    @Override
    public void onLoadFinished(QHMarketShop.QHMarketShopList list, int mode) {
        mPullToRefreshView.onRefreshComplete();
        onListLoaded(list);
    }

    @Override
    public void onLoadError(int mode) {
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.setVisibility(View.VISIBLE);
    }


    private void onListLoaded(QHMarketShop.QHMarketShopList data) {
        mLoadingProgress.setVisibility(View.GONE);

        if (data == null || data.results == null || data.results.size() == 0) {

            if (mAdapter.getItemCount() == 0) {
                mPullToRefreshView.setVisibility(View.GONE);
                empty_hint_view.setVisibility(View.VISIBLE);
                return;
            }

            ToastUtils.AvoidRepeatToastShow(getActivity(),R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            return;
        }

        if (data != null) {
            empty_hint_view.setVisibility(View.GONE);
            mPullToRefreshView.setVisibility(View.VISIBLE);
            if (data.pagination != null && data.pagination.offset == 0) {
                mAdapter.setDataItems(null);
            }
            mAdapter.appendDataItems(data.results);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onItemClicked(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }

        mShop = (QHMarketShop) v.getTag();
        if (mShop == null) {
            com.cmcc.hyapps.andyou.util.Log.e("NULL mShop");
            return;
        }

        switch (mShop.stype){
            case 1: //Hotel
                Intent intent1 = new Intent(getActivity(), HotelDetailsActivity.class);
                intent1.putExtra(Const.REST_DETAIL, mShop);
                startActivity(intent1);
                break;
            case 2: //Restaurant
                Intent intent2 = new Intent(getActivity(), RestaurantDetailActivity.class);
                intent2.putExtra(Const.REST_DETAIL, mShop);
                startActivity(intent2);
                break;
            case 3: //Special
                Intent intent3 = new Intent(getActivity(), SpecialDetailActivity.class);
                intent3.putExtra(Const.SPECIAL_DETAIL_DATA, mShop);
                startActivity(intent3);
                break;
            case 4: //4s
                Intent intent4 = new Intent(getActivity(), ShopDetailActivity.class);
                intent4.putExtra("shopID", mShop.id + "" );
                startActivity(intent4);
                break;
        }
    }

    private void initListView() {
        mRecyclerView = mPullToRefreshView.getRefreshableView();
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        int scap = ScreenUtils.dpToPxInt(getActivity(), 1);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        mAdapter = new MarketSearchListAdapter(getActivity());
        ItemClickSupport clickSupport = ItemClickSupport.addTo(mRecyclerView);
        clickSupport.setOnItemSubViewClickListener(new ItemClickSupport.OnItemSubViewClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                onItemClicked(view);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * the stype of market
     * 1：酒店
       2：美食
       3：特产
     * @return
     */
    private String getStype() {
        Bundle bundle = getArguments();
        return bundle != null ? bundle.getString(Const.EXTRA_STYPE) : null ;
    }
}
