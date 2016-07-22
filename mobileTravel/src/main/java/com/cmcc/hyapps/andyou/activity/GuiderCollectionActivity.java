/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.fragment.FreshCollectionListFragment;
import com.cmcc.hyapps.andyou.widget.ActionBar;

/**
 * @author kuloud
 */
public class GuiderCollectionActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
   // private  CollectionListFragment  specialFragment;
    private FreshCollectionListFragment specialFragment;
    private String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        View view = LayoutInflater.from(this).inflate(R.layout.fresh_collcetion,null);
        setContentView(view);
        user_id = getIntent().getStringExtra("user_id");
        initActionBar(view);
        specialFragment = new FreshCollectionListFragment();
        if (!TextUtils.isEmpty(user_id)){
            Bundle bundle = new Bundle();
            bundle.putString("user_id", user_id);
            specialFragment.setArguments(bundle);
        }

        if(null!=findViewById(R.id.fragment_container)){
            getFragmentManager().beginTransaction().add(R.id.fragment_container,specialFragment).commit();
        }
    }
    protected void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        if (TextUtils.isEmpty(user_id))
        actionBar.getTitleView().setText("我的收藏");
        else
            actionBar.getTitleView().setText("TA的收藏");
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setOnClickListener(this);
//        mActionBar.getRightView().setImageResource(R.drawable.);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.action_bar_left:
                finish();
                break;
        }
    }
}
