
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.SearchListActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.LocationUtil;
import com.cmcc.hyapps.andyou.widget.ChoosePopupWindow;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;

public class SpecialFragment extends BaseFragment implements OnClickListener,ChoosePopupWindow.OnPopupWindowsClickListener {
    private final String TAG = "FoodFragment";

    private View mLoadingProgress;
    private Location myLocation;
    private String city;
    private String city_en;
    private EditText et_search_content;
    private TextView mScopeSel, mPriceSel;
    private SpecialListFragment mSearchSpecialListFragment;
    private SpecialListFragment mFoodListFragment;
    private ChoosePopupWindow chooseDistancePopupWindow,choosePricePopupWindow;
    private String[] prices = new String[]{ "0_100", "100_200", "200_400", "400_inf","不限"};
    private String[] distances = new String[]{"500", "1000", "2000","5000", "不限"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_market_special, container, false);
        initActionBar(rootView);
        mLoadingProgress = rootView.findViewById(R.id.live_loading_progress);
        et_search_content = (EditText) rootView.findViewById(R.id.fragment_search_et);
        mLoadingProgress.setVisibility(View.VISIBLE);

        mScopeSel = (TextView) rootView.findViewById(R.id.filter_scope);
        mPriceSel = (TextView) rootView.findViewById(R.id.filter_price);
        mScopeSel.setOnClickListener(this);
        mPriceSel.setOnClickListener(this);
        et_search_content.setOnClickListener(this);
        showSpecialList();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        et_search_content.setOnKeyListener(new View.OnKeyListener() {//输入完后按键盘上的搜索键【回车键改为了搜索键】

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {//修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(
                                    getActivity().getCurrentFocus()
                                            .getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    UploadRecyclerView();

                }
                return false;
            }
        });
    }


    private void UploadRecyclerView() {
        String condition = et_search_content.getText().toString();
        mSearchSpecialListFragment = (SpecialListFragment) getFragmentManager().findFragmentByTag(TAG);
        String url = ServerAPI.SpecialList.buildSearchCommentUrl(condition);
        mSearchSpecialListFragment.loadList(url);
        mSearchSpecialListFragment.reload(DataLoader.MODE_REFRESH);
    }

    private void chooseConditionToUploadRecyclerView(int distancePosition,int pricePosition){
        mSearchSpecialListFragment = (SpecialListFragment) getFragmentManager().findFragmentByTag(TAG);
        mSearchSpecialListFragment.loadList(setUrl(distancePosition,pricePosition));
        mSearchSpecialListFragment.reload(DataLoader.MODE_REFRESH);
    }
    private String setUrl(int distancePosition,int pricePosition){
        float mLatitude = (float) LocationUtil.getInstance(getActivity()).getLatitude();
        float mLongitude = (float)LocationUtil.getInstance(getActivity()).getLongitude();
        String url = null;
        if (distancePosition == distances.length-1 && pricePosition == prices.length -1)
            url = ServerAPI.SpecialList.buildMutilConditionMarketAllNotLimitUrl(mLatitude,mLongitude,"average");
        if (distancePosition == distances.length-1 && pricePosition != prices.length -1)
            url = ServerAPI.SpecialList.buildMutilConditionMarketNoLimtDistanceUrl(mLatitude, mLongitude, "average", prices[pricePosition]);
        if (distancePosition != distances.length-1 && pricePosition == prices.length -1)
            url = ServerAPI.SpecialList.buildMutilConditionMarketNoLimitpriceUrl(mLatitude, mLongitude, distances[distancePosition], "average");
        if (distancePosition != distances.length-1 && pricePosition != prices.length -1)
            url = ServerAPI.SpecialList.buildMutilConditionMarketUrl(mLatitude, mLongitude, distances[distancePosition], "average", prices[pricePosition]);
        return  url;
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
        city = getArguments().getString(Const.CITYNAME_EN);
        city_en = getArguments().getString(Const.CITYNAME_EN);
    }


    private void showSpecialList() {
        mFoodListFragment = new SpecialListFragment();
//        mFoodListFragment.attachLoadingProgress(mLoadingProgress);
        getFragmentManager().beginTransaction().add(R.id.live_container, mFoodListFragment, TAG).commitAllowingStateLoss();
        float mLatitude = (float)LocationUtil.getInstance(getActivity()).getLatitude();
        float mLongitude = (float)LocationUtil.getInstance(getActivity()).getLongitude();
        String url = ServerAPI.SpecialList.buildMutilConditionMarketNoLimitpriceUrl(mLatitude, mLongitude, "1000", "average");
        mFoodListFragment.initLoacation(city, city_en,url);
    }

    private void initActionBar(View view) {
        View leftView = view.findViewById(R.id.action_bar_left_text);
        leftView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        if (chooseDistancePopupWindow == null) {
            initPopupWindow();
        }
        switch (v.getId()) {
            case R.id.action_bar_left_text: {
                getActivity().finish();
                break;
            }
            case R.id.filter_scope:
                mScopeSel.setTextColor(getResources().getColor(R.color.market_actionbar));
                chooseDistancePopupWindow.showPopupWindow(v);
                chooseDistancePopupWindow.setDrawableRight(mScopeSel, true);
                break;
            case R.id.filter_price:
                mPriceSel.setTextColor(getResources().getColor(R.color.market_actionbar));
                choosePricePopupWindow.showPopupWindow(v);
                choosePricePopupWindow.setDrawableRight(mPriceSel, true);
                break;
            case R.id.fragment_search_et:
                Intent searchIntent = new Intent(getActivity(), SearchListActivity.class);
                searchIntent.putExtra(Const.EXTRA_TYPE,Const.EXTRA_SHOP_DATA);
                searchIntent.putExtra(Const.EXTRA_STYPE, Const.MARKET_SPECIAL);
                startActivity(searchIntent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void initPopupWindow(){
        chooseDistancePopupWindow  = new ChoosePopupWindow(getActivity(), Arrays.asList(distances),0);
        chooseDistancePopupWindow.setChoosedView(mScopeSel);
        chooseDistancePopupWindow.setOnPopupWindowsClickListener(this);
        choosePricePopupWindow = new ChoosePopupWindow(getActivity(),Arrays.asList(prices),1);
        choosePricePopupWindow.setChoosedView(mPriceSel);
        choosePricePopupWindow.setOnPopupWindowsClickListener(this);
    }

    @Override
    public void onPopupItemClick() {
        if (chooseDistancePopupWindow == null || chooseDistancePopupWindow == null)
            return;
        chooseConditionToUploadRecyclerView(chooseDistancePopupWindow.getCurrentPosition(), choosePricePopupWindow.getCurrentPosition());
    }
}
