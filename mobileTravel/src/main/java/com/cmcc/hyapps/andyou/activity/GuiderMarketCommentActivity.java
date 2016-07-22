package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.os.Bundle;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.fragment.GuiderMarketCommentListFragment;
import com.cmcc.hyapps.andyou.model.QHMarketShop;

/**
 * Created by Administrator on 2015/6/29 0029.
 */
public class GuiderMarketCommentActivity extends BaseActivity{
    private Context mContext;
    private QHMarketShop mQhMarketShop;
    private GuiderMarketCommentListFragment marketCommentListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special);
        marketCommentListFragment = new GuiderMarketCommentListFragment();
        mQhMarketShop = getIntent().getParcelableExtra(Const.QH_SECNIC);
        mContext = getApplicationContext();
        int id = getIntent().getIntExtra(Const.QH_SECNIC_ID,0);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Const.QH_SECNIC, mQhMarketShop);
        bundle.putInt(Const.QH_SECNIC_ID, id);
        marketCommentListFragment.setArguments(bundle);
        if(null!=findViewById(R.id.fragment_container)){
            getFragmentManager().beginTransaction().add(R.id.fragment_container,marketCommentListFragment).commit();
        }
    }
}
