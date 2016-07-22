/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.fragment.PublishListFragment;

public class GuiderPublishActivity extends BaseActivity {
    private  Context mContext;
    private PublishListFragment specialFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_special);
        specialFragment = new PublishListFragment();
        String user_id = getIntent().getStringExtra("user_id");
        if (!TextUtils.isEmpty(user_id)){
            Bundle bundle = new Bundle();
            bundle.putString("user_id",user_id);
            specialFragment.setArguments(bundle);
        }

        if(null!=findViewById(R.id.fragment_container)){
            getFragmentManager().beginTransaction().add(R.id.fragment_container,specialFragment).commit();
        }
    }
}
