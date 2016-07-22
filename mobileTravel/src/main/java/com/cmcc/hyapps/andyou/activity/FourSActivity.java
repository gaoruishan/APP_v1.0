package com.cmcc.hyapps.andyou.activity;

import android.os.Bundle;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.fragment.FourSFragment;

/**
 * 4s
 * Created by bingbing on 2016/1/11.
 */
public class FourSActivity extends BaseActivity {
    private FourSFragment mFourSFragment = new FourSFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_hotels_layout);
        if(null!=findViewById(R.id.fragment_container)){
            getFragmentManager().beginTransaction().add(R.id.fragment_container, mFourSFragment).commit();
        }
    }
}
