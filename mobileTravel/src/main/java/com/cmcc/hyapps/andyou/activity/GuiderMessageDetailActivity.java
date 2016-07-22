package com.cmcc.hyapps.andyou.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.HomeRestuarantDetail;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.ActionBar;

public class GuiderMessageDetailActivity extends BaseActivity implements View.OnClickListener {
    private ActionBar mActionBar;
    private String mRequestTag = "GuiderMessageDetailActivity";
    private TextView content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_message_detail);
        initActionBar();
        initView();
        initData();
    }
    private void initView(){
        content = (TextView)findViewById(R.id.content);

    }
    private void initData(){
    }
    private void initActionBar() {
        mActionBar = (ActionBar) findViewById(R.id.action_bar);
        mActionBar.setBackgroundResource(R.drawable.fg_top_shadow);
        mActionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        mActionBar.getLeftView().setOnClickListener(GuiderMessageDetailActivity.this);
        mActionBar.getTitleView().setTextColor(getApplicationContext().getResources().getColor(R.color.black));
        mActionBar.setTitle("信息详情");

    }
    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left:
                finish();
                return;
            default:
                break;
        }
    }
    private void upLoadInfo() {
        final String url;
        url = ServerAPI.Home.buildUrl("beijing"/*mLocation.city*/);
        RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url,
                HomeRestuarantDetail.class, null,
                new Response.Listener<HomeRestuarantDetail>() {
                    @Override
                    public void onResponse(HomeRestuarantDetail response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error loading scenic details from %s", url);
                    }
                }, true, mRequestTag);
    }

}
