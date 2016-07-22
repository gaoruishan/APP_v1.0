package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.fragment.DiscoverSearchListFragment;
import com.cmcc.hyapps.andyou.fragment.MarketSearchListFragment;
import com.cmcc.hyapps.andyou.fragment.SearchListFragment;
import com.cmcc.hyapps.andyou.fragment.SearchStrategyListFragment;


/**
 * Created by Edward on 2015/6/7.
 */
public class SearchListActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        // by niuzhiguo add, For Market Search.
        Intent data = getIntent();
        if (data != null) {
            String type = data.getStringExtra(Const.EXTRA_TYPE);
            String stype =data.getStringExtra(Const.EXTRA_STYPE);
            if (type != null && type.equals(Const.EXTRA_SHOP_DATA)) {//商户
                MarketSearchListFragment searchListFragment = new MarketSearchListFragment();
                if (!TextUtils.isEmpty(stype)){
                    Bundle bundle = new Bundle();
                    bundle.putString(Const.EXTRA_STYPE,stype);
                    searchListFragment.setArguments(bundle);
                }
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,searchListFragment,searchListFragment.getFragmentName()).commit();
                return;
            }else if (type != null && type.equals(Const.EXTRA_SCENIC_DATA)){//景区
                DiscoverSearchListFragment searchListFragment = new DiscoverSearchListFragment();
                if (!TextUtils.isEmpty(stype)){
                    Bundle bundle = new Bundle();
                    bundle.putString(Const.EXTRA_STYPE,stype);
                    searchListFragment.setArguments(bundle);
                }
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,searchListFragment,searchListFragment.getFragmentName()).commit();
                return;
            }

            String strategy = data.getStringExtra(Const.STRATEGY);//游记
            if (!TextUtils.isEmpty(strategy)){
                SearchStrategyListFragment searchListFragment = new SearchStrategyListFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,searchListFragment,searchListFragment.getFragmentName()).commit();
                return;
            }
        }

        SearchListFragment searchListFragment = new SearchListFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,searchListFragment,searchListFragment.getFragmentName()).commit();
    }
}
