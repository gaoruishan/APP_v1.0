package com.cmcc.hyapps.andyou.activity;

import android.os.Bundle;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.fragment.RoadConditionFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by bingbing on 2015/9/24.
 */
public class RoadConditionActivity extends BaseActivity {
    private RoadConditionFragment liveFragment;
    private static final String LIVE_TRAFFIC = "live_traffic";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_activity);
        initLiveFragment();
    }
    private void initLiveFragment() {
        liveFragment = new RoadConditionFragment();
        Bundle args = liveFragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putString(Const.ARGS_REQUEST_TAG, requestTag);
        liveFragment.setArguments(args);
        getFragmentManager().beginTransaction().add(R.id.activity_live_container,liveFragment).commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onEventValue(this, LIVE_TRAFFIC, null, (int)time);
    }
}
