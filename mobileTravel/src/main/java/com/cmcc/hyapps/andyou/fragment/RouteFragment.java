
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.umeng.analytics.MobclickAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.VideoUploadActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.widget.ActionBar;

public class RouteFragment extends BaseFragment implements OnClickListener {
    private final String TAG = "LiveFragment";

    private View mLoadingProgress;
    private final int REQUEST_CODE_TAKE_VIDEO = 1;
    private Location myLocation;
    RouteListFragment listFragment = new RouteListFragment();
    private EditText et_search_content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant, container, false);
        initActionBar(rootView);
        initBottomTab(rootView);
        mLoadingProgress = rootView.findViewById(R.id.live_loading_progress);
        et_search_content = (EditText) rootView.findViewById(R.id.fragment_search_et);
        mLoadingProgress.setVisibility(View.VISIBLE);
        // showVideoList(LocationUtils.getLastKnownLocation(getActivity()));
        showVideoList();
        return rootView;
    }

    private void initBottomTab(View rootView) {
        RadioGroup fragment_switch = (RadioGroup)rootView.findViewById(R.id.fragment_switch);
        fragment_switch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.my_travel:
                        listFragment.setType(1);
                        break;
                    case R.id.group_travel:
                        listFragment.setType(2);
                        break;
                }
                listFragment.reload();
            }
        });
        fragment_switch.getChildAt(0).setSelected(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        et_search_content.setOnKeyListener(new View.OnKeyListener() {//输入完后按键盘上的搜索键【回车键改为了搜索键】

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER){//修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(
                                    getActivity().getCurrentFocus()
                                            .getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    if(event.getAction()==KeyEvent.ACTION_DOWN){
                        UploadRecyclerView();
                    }
                }
                return false;
            }
        });
    }

    private void UploadRecyclerView() {
        String condition = et_search_content.getText().toString();
        RouteListFragment routeListFragment = (RouteListFragment) getFragmentManager().findFragmentByTag("RouteListFragment");
        String url = ServerAPI.Route.buildSearchCommentUrl(condition);
        routeListFragment.loadList(url);
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

    String city;
    String city_en;
    String lat;
    String lon;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        city  = getArguments().getString(Const.CITYNAME);
        city_en  = getArguments().getString(Const.CITYNAME_EN);
        lat  = getArguments().getString(Const.LAT);
        lon  = getArguments().getString(Const.LON);
//        myLocation = getArguments().getParcelable(Const.EXTRA_COORDINATES);
//        listFragment.setLoaction(myLocation);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_VIDEO:
                    startUploadActivity();
                    break;
                default:
                    break;
            }
        }
    }

    private void showVideoList(/*Location location*/) {

        listFragment.attachLoadingProgress(mLoadingProgress);
        getFragmentManager().beginTransaction().add(R.id.live_container, listFragment,"RouteListFragment").commitAllowingStateLoss();
        listFragment.initLoacation(city,city_en,lat,lon);
    }

    private void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.home_nearby_restaurant);
        actionBar.getTitleView().setTextColor(Color.WHITE);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView() .setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);

        /*actionBar.getRightView().setImageResource(R.drawable.icon_audio);
        actionBar.getRightView().setOnClickListener(new com.cmcc.hyapps.andyou.support.OnClickListener() {
            @Override
            public void onValidClick(View v) {
                listFragment.gotoMapView();
            }
        });*/
    }
    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left: {
                getActivity().finish();
                break;
            }
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
