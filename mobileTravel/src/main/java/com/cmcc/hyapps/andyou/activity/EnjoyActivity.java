package com.cmcc.hyapps.andyou.activity;

import android.os.Bundle;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.fragment.EnjoyFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * enjoy activity
 * Created by bingbing on 2015/8/20.
 */
public class EnjoyActivity extends BaseActivity {
    private EnjoyFragment enjoyFragment;
    private static final String ENJOY = "entertainment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_activity);
        initEnjoyFragment();
    }

    private void initEnjoyFragment(){
        enjoyFragment = new EnjoyFragment();
        getFragmentManager().beginTransaction().add(R.id.activity_live_container,enjoyFragment).commitAllowingStateLoss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onEventValue(this, ENJOY, null, (int) time);
    }
}
