/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.ScenicList.Type;
import com.cmcc.hyapps.andyou.fragment.BasicScenicDataListFragment;
import com.cmcc.hyapps.andyou.fragment.DefaultScenicDataListFragmentImp;
import com.cmcc.hyapps.andyou.model.BasicScenicData;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.Scenic.ScenicList;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;

/**
 * @author kuloud
 */
public class ScenicListActivity extends BaseActivity implements OnClickListener {
    private ServerAPI.ScenicList.Type mType = null;
    private Location mLocation;
    private BasicScenicDataListFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mType = (Type) getIntent().getSerializableExtra(Const.EXTRA_SCENIC_TYPE);
        if (mType == null) {
            Log.e("Invalid scenic type %s", mType);
            finish();
            return;
        }

        mLocation = (Location) getIntent().getParcelableExtra(Const.EXTRA_COORDINATES);
        setContentView(R.layout.activity_scenic_spot_list);
        initViews();

        mFragment = new DefaultScenicDataListFragmentImp();
        Bundle args = new Bundle();
        args.putString(Const.ARGS_KEY_MODEL_CLASS, ScenicList.class.getName());
        args.putString(Const.ARGS_LOADER_URL, ServerAPI.ScenicList.buildUrl(mType, mLocation));
        mFragment.setArguments(args);
        mFragment.setOnItemClickListener(this);
        getFragmentManager().beginTransaction()
                .add(R.id.container, mFragment).commit();
    }

    private void initViews() {
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(getIntent().getStringExtra(Const.EXTRA_NAME));
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_map_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ExcessiveClickBlocker.isExcessiveClick()) {
                    return;
                }
                finish();
            }
        });
        actionBar.getRightView().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ExcessiveClickBlocker.isExcessiveClick()) {
                    return;
                }
                ArrayList<BasicScenicData> scenicList = mFragment.getScenicDataList();
                if (scenicList != null && scenicList.size() != 0) {
                    Intent intent = new Intent(activity, ScenicSpotsMapActivity.class);
                    intent.putParcelableArrayListExtra(Const.EXTRA_SCENIC_SPOTS_DATA,
                            scenicList);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        BasicScenicData data = (BasicScenicData) v.getTag();
        Intent intent = new Intent();
        intent = new Intent(this, ScenicDetailActivity.class);
        intent.putExtra(Const.EXTRA_ID, data.id());
        intent.putExtra(Const.EXTRA_NAME, data.name());
        startActivity(intent);
    }
}
