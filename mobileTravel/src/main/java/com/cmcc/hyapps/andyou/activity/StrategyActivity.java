/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.os.Bundle;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.fragment.StrategyFragment;
import com.cmcc.hyapps.andyou.model.Location;
import com.umeng.analytics.MobclickAgent;

/**
 * @author kuloud
 */
public class StrategyActivity extends BaseActivity {
    private Context mContext;
    private /*RestaurantListFragment*/ StrategyFragment strategyFragment;
    private static final String TRAVEL_NOTES = "travel_notes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_special);
        strategyFragment = new StrategyFragment();

        Bundle b = new Bundle();
        Location location = getIntent().getParcelableExtra(Const.EXTRA_COORDINATES);
        b.putString(Const.CITYNAME_EN,getIntent().getStringExtra(Const.CITYNAME_EN));
        b.putString(Const.CITYNAME,getIntent().getStringExtra(Const.CITYNAME));
        strategyFragment.setArguments(b);

        if(null!=findViewById(R.id.fragment_container)){
            getFragmentManager().beginTransaction().add(R.id.fragment_container, strategyFragment).commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onEventValue(this, TRAVEL_NOTES, null, (int) time);
    }
}