/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.fragment.RouteFragment;

/**
 * @author kuloud
 */
public class RouteActivity extends BaseActivity {
    private Context mContext;
    private RouteFragment routeFragment = new RouteFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_restaurant);
        Intent intent =  getIntent();
        Bundle b = new Bundle();
        b.putString(Const.CITYNAME_EN,getIntent().getStringExtra(Const.CITYNAME_EN));
        b.putString(Const.CITYNAME,getIntent().getStringExtra(Const.CITYNAME));
        b.putString(Const.LAT,getIntent().getStringExtra(Const.LAT));
        b.putString(Const.LON,getIntent().getStringExtra(Const.LON));
        routeFragment.setArguments(b);
        if(null!=findViewById(R.id.fragment_container)){
            getFragmentManager().beginTransaction().add(R.id.fragment_container, routeFragment).commit();
        }
    }
}
