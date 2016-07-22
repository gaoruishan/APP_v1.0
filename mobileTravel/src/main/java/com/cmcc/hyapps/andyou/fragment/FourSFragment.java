package com.cmcc.hyapps.andyou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.SearchListActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.LocationUtil;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.LandScapeScrollerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingbing on 2016/1/11.
 */
public class FourSFragment extends BaseFragment implements LandScapeScrollerView.OnItemClickListener, View.OnClickListener {
    private final String TAG = "FourSFragment";
    private View mLoadingProgress;
    private LandScapeScrollerView landSpaceView;
    private FourSListFragment liveFragment;
    private EditText et_search_content;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_4s_layout, container, false);
        initActionBar(rootView);
        landSpaceView = (LandScapeScrollerView) rootView.findViewById(R.id.fours_tab);
        landSpaceView.setFirst(0);
        landSpaceView.setOnItemClick(this);
        mLoadingProgress = rootView.findViewById(R.id.live_loading_progress);
        mLoadingProgress.setVisibility(View.VISIBLE);
        loadAreaTag();
        showVideoList();
        return rootView;
    }

    private void initActionBar(View view) {
        View leftView = view.findViewById(R.id.action_bar_left_text);
        et_search_content = (EditText) view.findViewById(R.id.fragment_search_et);
        et_search_content.setOnClickListener(this);
        leftView.setOnClickListener(this);
    }

    @Override
    public void onItemOnclick(View view) {
        int position = (Integer) view.getTag();
        if (position == 0) {
            chooseConditionToRefreshAllData();
        } else {
            //-1是因为集合最前面加了一个"不限"
//            AreaTag tag = areaList.get(position -1);

            chooseConditionToRefreshData(sparseArray.keyAt(position) + "");
        }
    }

    SparseArray<String> sparseArray = null;

    private void loadAreaTag() {
        sparseArray = new SparseArray();
        sparseArray.put(-1, "不限");
        sparseArray.put(500, "500米");
        sparseArray.put(1000, "1000米");
        sparseArray.put(2000, "2000米");
        sparseArray.put(5000, "5000米");
        landSpaceView.addInnerView(sparseArray, FourSFragment.this
                .getActivity());
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left_text: {
                getActivity().finish();
                break;
            }
            case R.id.fragment_search_et:
                Intent searchIntent = new Intent(getActivity(), SearchListActivity.class);
                searchIntent.putExtra(Const.EXTRA_TYPE,Const.EXTRA_SHOP_DATA);
                searchIntent.putExtra(Const.EXTRA_STYPE, Const.MARKET_4S);
                startActivity(searchIntent);
        }
    }

    private void showVideoList() {
        liveFragment = new FourSListFragment();
        liveFragment.attachLoadingProgress(mLoadingProgress);
        Bundle args = new Bundle();
        args.putString(Const.ARGS_LOADER_URL, ServerAPI.BASE_URL + "shops/?stype=4");
        liveFragment.setArguments(args);
        getFragmentManager().beginTransaction().add(R.id.live_container, liveFragment, TAG).commitAllowingStateLoss();
    }

    private void chooseConditionToRefreshData(String distance) {
        liveFragment = (FourSListFragment) getFragmentManager().findFragmentByTag(TAG);
        float mLatitude = (float) LocationUtil.getInstance(getActivity()).getLatitude();
        float mLongitude = (float)LocationUtil.getInstance(getActivity()).getLongitude();
        liveFragment.loadList(ServerAPI.BASE_URL + "shops/?stype=4&distance=" + distance + "&latitude=" + String.valueOf(mLatitude) + "&longitude=" + String.valueOf(mLongitude));
        liveFragment.reload(DataLoader.MODE_REFRESH);
    }

    private void chooseConditionToRefreshAllData() {
        liveFragment = (FourSListFragment) getFragmentManager().findFragmentByTag(TAG);
        liveFragment.loadList(ServerAPI.BASE_URL + "shops/?stype=4");
        liveFragment.reload(DataLoader.MODE_REFRESH);
    }
}
