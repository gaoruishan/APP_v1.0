
package com.cmcc.hyapps.andyou.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.ServiceListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI.ScenicShops;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.Shop;
import com.cmcc.hyapps.andyou.model.Shop.ShopList;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ServiceListActivity extends BaseActivity implements
        DataLoaderCallback<ShopList> {
    private static final int SEARCH_RADIUS = 2000;
    private Location mLoc;
    private String mKeyword;
    private Context mContext;

    private RecyclerView mRecyclerView;
    private ServiceListAdapter mAdapter;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private PullToRefreshRecyclerView mPullToRefreshView;

    private UrlListLoader<ShopList> mLoader;
    private ScenicShops.Type mType;
    private int mScenicId;
    private ArrayList<Shop> mShopList = new ArrayList<Shop>();
    private ProgressDialog progDialog = null;
    private PoiSearch mPoiSearch;
    private PoiSearch.Query mPoiQuery;
    private PoiResult mPoiResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = getApplicationContext();
        mKeyword = getIntent().getStringExtra(Const.EXTRA_SEARCH_KEYWORD);
        mLoc = (Location) getIntent().getParcelableExtra(Const.EXTRA_COORDINATES);
        mType = (ScenicShops.Type) getIntent().getSerializableExtra(Const.EXTRA_SHOP_TYPE);
        mScenicId = getIntent().getIntExtra(Const.EXTRA_ID, -1);
        if (mLoc == null || mType == null || mScenicId <= 0) {
            finish();
            return;
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        initActionBar();
        initViews();

        mPoiQuery = new PoiSearch.Query(mKeyword, mKeyword, mLoc.city);
        mPoiSearch = new PoiSearch(mContext, mPoiQuery);
        mPoiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mLoc
                .latitude, mLoc.longitude), SEARCH_RADIUS));
        mPoiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult result, int rCode) {
                mLoadingProgress.setVisibility(View.GONE);
                if (rCode == 0) {
                    if (result != null) {
                        mPoiResult = result;
                        List<PoiItem> poiItems = mPoiResult.getPois();

                        if (poiItems != null) {
                            ArrayList<Shop> shops = new ArrayList<Shop>();
                            for (PoiItem info : poiItems) {
                                Shop shop = new Shop();
                                shop.name = info.getTitle();
                                shop.address = info.getAdName();
                                shop.distance = info.getDistance();
                                shop.telphone = info.getTel();
                                shops.add(shop);
                            }
                            mShopList = shops;
                            mAdapter.setDataItems(shops);
                        } else {
                            mEmptyHintView.setVisibility(View.VISIBLE);
                        }

                    } else {
                        ToastUtils.show(mContext, R.string.no_result);
                    }
                } else if (rCode == 27) {
                    ToastUtils.show(mContext, R.string.error_network);
                } else if (rCode == 32) {
                    ToastUtils.show(mContext, R.string.error_key);
                } else {
                    ToastUtils.show(mContext, getString(R.string.error_other) + rCode);
                }
            }

            @Override
            public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

            }
        });
        mPoiSearch.searchPOIAsyn();
    }

    private void initViews() {
        mPullToRefreshView = (PullToRefreshRecyclerView) findViewById(R.id.pulltorefresh_twowayview);
        mRecyclerView = mPullToRefreshView.getRefreshableView();
        mLoadingProgress = findViewById(R.id.loading_progress);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mEmptyHintView = findViewById(R.id.empty_hint_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        int scap = 0;
        DividerItemDecoration decor = null;
//        if (mType != ScenicShops.Type.HOTEL) {
//            scap = ScreenUtils.dpToPxInt(activity, 6);
//            mRecyclerView.setPadding(scap, scap, scap, scap);
//            decor = new DividerItemDecoration(scap);
//        } else {
            scap = ScreenUtils.dpToPxInt(activity, 1);
            decor = new DividerItemDecoration(getResources().getColor(R.color.base_grey_line), scap, scap);
//        }
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);

        mAdapter = new ServiceListAdapter(mType);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    protected void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(getIntent().getStringExtra(Const.EXTRA_ACTION_BAR_TITLE));
        actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_map_selecter);
        actionBar.getRightView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ServiceMapActivity.class);
                intent.putExtra(Const.EXTRA_SHOP_TYPE, mType);
                intent.putExtra(Const.EXTRA_SEARCH_KEYWORD, mKeyword);
                intent.putExtra(Const.EXTRA_COORDINATES, mLoc);
                intent.putExtra(Const.EXTRA_SEARCH_KIND,
                        getIntent().getStringExtra(Const.EXTRA_SEARCH_KIND));
                intent.putExtra(Const.EXTRA_ACTION_BAR_TITLE, getIntent()
                        .getStringExtra(Const.EXTRA_ACTION_BAR_TITLE));
                intent.putParcelableArrayListExtra(Const.EXTRA_SHOP_DATA, mShopList);
                startActivity(intent);
            }

        });
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onLoadFinished(ShopList shopList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();

        List<Shop> list = null;
        if (shopList != null) {
            list = shopList.list;
        }

        if (list == null || list.isEmpty()) {
            mEmptyHintView.setVisibility(View.VISIBLE);
        } else {
            mPullToRefreshView.setVisibility(View.VISIBLE);
            ((AppendableAdapter) mPullToRefreshView.getRefreshableView().getAdapter())
                    .setDataItems(list);
            mShopList.addAll(list);
        }

    }

    @Override
    public void onLoadError(int mode) {
        // TODO Auto-generated method stub

    }
}
