package com.cmcc.hyapps.andyou.activity;

import android.os.Bundle;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.fragment.LiveFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2015/7/10 0010.
 */
public class LiveActivity extends BaseActivity {
    private LiveFragment liveFragment;
    private static final String SCENIC_360 = "Scenic_360";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_activity);
        initLiveFragment();
    }

    private void initLiveFragment() {
        liveFragment = new LiveFragment();
        Bundle args = liveFragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putString(Const.ARGS_REQUEST_TAG, requestTag);
        liveFragment.setArguments(args);
        getFragmentManager().beginTransaction().add(R.id.activity_live_container,liveFragment).commitAllowingStateLoss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onEventValue(this, SCENIC_360, null, (int) time);
    }
}
