package com.cmcc.hyapps.andyou.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

/**
 * Created by Administrator on 2015/5/28.
 */
public class MyCollection extends BaseActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private PullToRefreshRecyclerView mPullToRefreshRecyclerView;
    private LinearLayoutManager layoutManager;
    private RadioGroup switch_ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_collection);
        initListView();
        initActionBar();

    }

    protected void initData() {
        switch_ll = (RadioGroup) findViewById(R.id.fragment_switch);
        switch_ll.setVisibility(View.VISIBLE);
        switch_ll.check(R.id.me_strategy);
    }

    public void initListView() {
        initData();
        mPullToRefreshRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        mRecyclerView = mPullToRefreshRecyclerView.getRefreshableView();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);


//        StrategyAdapter strategyAdapter = new StrategyAdapter(activity, imageViews, homeItems);
//        mRecyclerView.setAdapter(strategyAdapter);

    }

    protected void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.getTitleView().setText("我的收藏");
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setOnClickListener(this);
//        mActionBar.getRightView().setImageResource(R.drawable.);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.action_bar_left:
                activity.finish();
                break;
        }
    }

}
