
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.VideoUploadActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.widget.ActionBar;

public class NavigationFragment extends BaseFragment implements OnClickListener {
    private final String TAG = "LiveFragment";

    private View mLoadingProgress;
    private final int REQUEST_CODE_TAKE_VIDEO = 1;
    Location myLocation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hotel, container, false);
        initActionBar(rootView);
        mLoadingProgress = rootView.findViewById(R.id.live_loading_progress);
        mLoadingProgress.setVisibility(View.VISIBLE);
        // showVideoList(LocationUtils.getLastKnownLocation(getActivity()));
        showVideoList();

        return rootView;
    }

    String city;
    String city_en;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        city  = getArguments().getString(Const.CITYNAME);
//        city_en  = getArguments().getString(Const.CITYNAME_EN);
//      myLocation = getArguments().getParcelable(Const.EXTRA_COORDINATES);
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
        NaviListFragment liveFragment = new NaviListFragment();
        liveFragment.attachLoadingProgress(mLoadingProgress);
        getFragmentManager().beginTransaction().add(R.id.live_container, liveFragment).commitAllowingStateLoss();
    }

    private void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.home_nearby_hotel);
        actionBar.getTitleView().setTextColor(Color.WHITE);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView() .setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
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
