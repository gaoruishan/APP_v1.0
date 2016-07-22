
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.SearchListActivity;
import com.cmcc.hyapps.andyou.activity.TripDayEditActivity;
import com.cmcc.hyapps.andyou.activity.VideoUploadActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.umeng.analytics.MobclickAgent;

public class StrategyFragment extends BaseFragment implements OnClickListener {
    private final String TAG = "StrategyFragment";

    private View mLoadingProgress;
    private final int REQUEST_CODE_TAKE_VIDEO = 1;
    private Location myLocation;
    private TextView personal_travle,recommand_travle;
    private StrategyListFragment personalTravelFragment;
    private RouteListFragment recommandTravelFragment;
    private Fragment mCurrentFragment = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_special, container, false);
        initActionBar(rootView);
        mLoadingProgress = rootView.findViewById(R.id.live_loading_progress);
        mLoadingProgress.setVisibility(View.VISIBLE);
        personal_travle =(TextView) rootView.findViewById(R.id.personal_travle);
        recommand_travle =(TextView) rootView.findViewById(R.id.recommand_travle);
        personal_travle.setOnClickListener(this);
        recommand_travle.setOnClickListener(this);
        showList();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        et_search_content.setOnKeyListener(new View.OnKeyListener() {//输入完后按键盘上的搜索键【回车键改为了搜索键】
//
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(keyCode==KeyEvent.KEYCODE_ENTER){//修改回车键功能
//                    // 先隐藏键盘
//                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
//                            .hideSoftInputFromWindow(
//                                    getActivity().getCurrentFocus()
//                                            .getWindowToken(),
//                                    InputMethodManager.HIDE_NOT_ALWAYS);
//                    if(event.getAction()==KeyEvent.ACTION_DOWN){
//                        UploadRecyclerView();
//                    }
//
//                }
//                return false;
//            }
//        });
    }
    StrategyListFragment guideListFragment;
    private void UploadRecyclerView() {
//        String condition = et_search_content.getText().toString();
//        guideListFragment= (StrategyListFragment) getFragmentManager().findFragmentByTag("GuideListFragment");
//        String url = ServerAPI.Guide.buildSearchCommentUrl(condition);
//        guideListFragment.loadList(url);
//        guideListFragment.reload();
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
    QHStrategy newStrategy;
    String city;
    String city_en;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       city  = getArguments().getString(Const.CITYNAME_EN);
       city_en  = getArguments().getString(Const.CITYNAME_EN);
//        myLocation = getArguments().getParcelable(Const.EXTRA_COORDINATES);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
//                case REQUEST_CODE_TAKE_VIDEO:
//                    startUploadActivity();
//                    break;
                case REQUEST_CODE_ADD_TRIP_DAY:
                    if (data == null) {
                        Log.wtf("REQUEST_CODE_ADD_TRIP_DAY data NULL");
                        return;
                    }
//                    guideListFragment= (StrategyListFragment) getFragmentManager().findFragmentByTag("GuideListFragment");
//                    String url = ServerAPI.Guide.BASE_GUIDE_SEARCH_URL;
//                    guideListFragment.loadList(url);
//                    personal_travle.performClick();
                    ToastUtils.show(getActivity(),"发表成功！");
//                    QHStrategy newStrategy = data.getParcelableExtra(Const.EXTRA_QHSTRATEGY_DATA);
//                    if (newStrategy != null) {
//                        List<QHStrategy> trips = new ArrayList<QHStrategy>();
//                        trips.add(newStrategy);
////                        mAdapter.instertDataItemsAhead(trips);
//                        liveFragment.insertHead(trips);
//                    }



//                    mTripLoader = null;
//                    mAdapter.setDataItems(null);
//                    mPullRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
//                    loadTrips();
//                    Trip newTrip = data.getParcelableExtra(Const.EXTRA_TRIP_DAY_DATA);
//                    if (newTrip != null) {
//                        List<Trip> trips = new ArrayList<Trip>();
//                        trips.add(newTrip);
//                        mAdapter.instertDataItemsAhead(trips);
//                    }
//                    // Success add trip day, update item and auto jump to detail
//                    int id = data.getIntExtra(Const.EXTRA_ID, -1);
//                    int index = 0;
//                    for (Trip trip : mAdapter.getDataItems()) {
//                        if (trip.id == id) {
//                            trip = newTrip;
//                            mAdapter.notifyItemChanged(index);
//                            Intent goDetail = new Intent(getActivity(),
//                                    TripDetailEditActivity.class);
//                            goDetail.putExtras(data);
//                            startActivity(goDetail);
//                            break;
//                        }
//                        index++;
//                    }
                default:
                    break;
            }
        }
    }
    private void showList(/*Location location*/) {
        personal_travle.performClick();
//        personal_travle.setSelected(true);
//        recommand_travle.setSelected(false);
//        if (personalTravelFragment == null){
//            personalTravelFragment= new StrategyListFragment();
//            mCurrentFragment = personalTravelFragment;
//        }
//        if (recommandTravelFragment == null)
//            recommandTravelFragment = new RouteListFragment();
//        personalTravelFragment.attachLoadingProgress(mLoadingProgress);
//        recommandTravelFragment.attachLoadingProgress(mLoadingProgress);
//        getFragmentManager().beginTransaction().add(R.id.live_container, personalTravelFragment,"GuideListFragment").commitAllowingStateLoss();
//        personalTravelFragment.initLoacation(city,city_en);
    }

    private void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.special_title);
        actionBar.getTitleView().setTextColor(Color.WHITE);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView() .setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
        actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_search);
        actionBar.getRightView().setOnClickListener(this);
        actionBar.getRight2View().setImageResource(R.drawable.edit);
        actionBar.getRight2View().setOnClickListener(this);
    }
    private final int REQUEST_CODE_ADD_TRIP_DAY_LOGIN = 2;
    private final int REQUEST_CODE_ADD_TRIP_DAY = 1;
    @Override
    public void onClick(View v) {
//        if (ExcessiveClickBlocker.isExcessiveClick()) {
//            return;
//        }
        switch (v.getId()) {
            case R.id.action_bar_left: {
                getActivity().finish();
                break;
            }
            case R.id.action_bar_right:
//                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_DISCOVERY_ADD);
//                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_ADD_TRIP_DAY_LOGIN)) {
//                    Intent addTripDay = new Intent(getActivity(), TripDayEditActivity.class);
//                    startActivityForResult(addTripDay, REQUEST_CODE_ADD_TRIP_DAY);
////                    startActivity(addTripDay);
//                }
                MobclickAgent.onEvent(getActivity(), MobConst.ID_HOME_BTN_CITY);
                //跳转到搜索页面
                Intent intent = new Intent(getActivity(), SearchListActivity.class);
                intent.putExtra(Const.STRATEGY,Const.STRATEGY);
                startActivity(intent);
                break;
            case R.id.action_bar_right2:
//                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_DISCOVERY_ADD);
//                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_ADD_TRIP_DAY_LOGIN)) {
//                    Intent addTripDay = new Intent(getActivity(), TripDayEditActivity.class);
//                    startActivityForResult(addTripDay, REQUEST_CODE_ADD_TRIP_DAY);
////                    startActivity(addTripDay);
//                }
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_DISCOVERY_ADD);
                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_ADD_TRIP_DAY_LOGIN)) {
                    Intent addTripDay = new Intent(getActivity(), TripDayEditActivity.class);
                    startActivityForResult(addTripDay, REQUEST_CODE_ADD_TRIP_DAY);
//                    startActivity(addTripDay);
                }
                break;
            case R.id.personal_travle:
                if (personalTravelFragment == null){
                    personalTravelFragment= new StrategyListFragment();
                    personalTravelFragment.attachLoadingProgress(mLoadingProgress);
                    personalTravelFragment.loadList();
                }
                personal_travle.setSelected(true);
                recommand_travle.setSelected(false);
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (mCurrentFragment == null){
                        getFragmentManager().beginTransaction().add(R.id.live_container, personalTravelFragment,"personalTravelFragment").commitAllowingStateLoss();
                    }else if (personalTravelFragment.isAdded()){
                        getFragmentManager().beginTransaction().hide(mCurrentFragment)
                                .show(personalTravelFragment)
                                .commit();
                    }else {
                        getFragmentManager().beginTransaction().hide(mCurrentFragment)
                                .add(R.id.live_container, personalTravelFragment).commit();
                    }
                }
                mCurrentFragment = personalTravelFragment;
                break;
            case R.id.recommand_travle:
                if (recommandTravelFragment == null){
                    recommandTravelFragment= new RouteListFragment();
                    recommandTravelFragment.attachLoadingProgress(mLoadingProgress);
                    recommandTravelFragment.loadList();
                }
                personal_travle.setSelected(false);
                recommand_travle.setSelected(true);
                if (getActivity() != null && !getActivity().isFinishing()) {
                    if (mCurrentFragment == null){
                        getFragmentManager().beginTransaction().add(R.id.live_container, recommandTravelFragment,"recommandTravelFragment").commitAllowingStateLoss();
                    }else if (recommandTravelFragment.isAdded()){
                        getFragmentManager().beginTransaction().hide(mCurrentFragment)
                                .show(recommandTravelFragment)
                                .commit();
                    }else {
                        getFragmentManager().beginTransaction().hide(mCurrentFragment)
                                .add(R.id.live_container, recommandTravelFragment).commit();
                    }
                }
                mCurrentFragment = recommandTravelFragment;
                break;
        }
    }

    private void startUploadActivity() {
        Intent intent = new Intent(getActivity(), VideoUploadActivity.class);
        // FIXME
        intent.putExtra(Const.EXTRA_ID, 6);
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
