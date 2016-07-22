
package com.cmcc.hyapps.andyou.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.Shop;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.List;

public class ServiceMapActivity extends MyLocationMapActivity {
    // TODO:
    private static final int SEARCH_RADIUS = 2000;
    private Location mLoc;
    private String mKeyword;

    private ServerAPI.ScenicShops.Type mType;
    private ArrayList<Shop> mShops;

    private ProgressDialog progDialog = null;
    private PoiSearch mPoiSearch;
    private PoiSearch.Query mPoiQuery;
    private PoiResult mPoiResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mKeyword = getIntent().getStringExtra(Const.EXTRA_SEARCH_KEYWORD);
        mLoc = (Location) getIntent().getParcelableExtra(Const.EXTRA_COORDINATES);
        mType = (ServerAPI.ScenicShops.Type) getIntent().getSerializableExtra(Const.EXTRA_SHOP_TYPE);
        mShops = getIntent().getParcelableArrayListExtra(Const.EXTRA_SHOP_DATA);

        super.onCreate(savedInstanceState);

        if (mType == ServerAPI.ScenicShops.Type.REST_ROOM && mLoc == null) {
            finish();
            return;
        }

        String area = "POINT(" + mLoc.longitude + " " + mLoc.latitude + ")";
        mPoiQuery = new PoiSearch.Query(mKeyword, mKeyword, mLoc.city);
        mPoiSearch = new PoiSearch(mContext, mPoiQuery);
        mPoiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mLoc
                .latitude, mLoc.longitude), SEARCH_RADIUS));
        mPoiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult result, int rCode) {
                dissmissProgressDialog();
                if (rCode == 0) {
                    if (result != null) {
                        mPoiResult = result;
                        List<PoiItem> poiItems = mPoiResult.getPois();

                        if (poiItems != null && poiItems.size() > 0) {
                            mMapView.getMap().clear();
                            PoiOverlay poiOverlay = new PoiOverlay
                                    (mMapView.getMap(), poiItems);
                            poiOverlay.removeFromMap();
                            poiOverlay.addToMap();
                            poiOverlay.zoomToSpan();
                        } else {
                            ToastUtils.show(mContext, R.string.no_result);
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
        showProgressDialog();
        mPoiSearch.searchPOIAsyn();
    }

    @Override
    protected void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(getIntent().getStringExtra(Const.EXTRA_ACTION_BAR_TITLE));
        actionBar.setRightMode(false);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + mKeyword);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
