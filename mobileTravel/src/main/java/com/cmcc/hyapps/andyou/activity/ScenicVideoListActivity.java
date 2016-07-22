
package com.cmcc.hyapps.andyou.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.VideoListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.ScenicVideos.Type;
import com.cmcc.hyapps.andyou.fragment.VideoListFragment;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.CommonUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.model.QHScenic.QHVideo;

public class ScenicVideoListActivity extends BaseActivity implements OnClickListener {
    private int mId = -1;
    private Type mType;
    private VideoListFragment mFragment;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mId = getIntent().getIntExtra(Const.EXTRA_ID, -1);
        mLocation = (Location) getIntent().getParcelableExtra(Const.EXTRA_COORDINATES);
        mType = (Type) getIntent().getSerializableExtra(Const.EXTRA_VIDEO_TYPE);
        if (mType == null) {
            Log.e("Invalid scenic mType %s", mType);
            finish();
            return;
        }
        setContentView(R.layout.activity_scenic_spot_list);
        initViews();

        mFragment = VideoListFragment.newInstance(GridLayoutManager.class,
                VideoListAdapter.class);
        Bundle args = new Bundle();
        args.putString(Const.ARGS_LOADER_URL, ServerAPI.ScenicVideos
                .buildUrl(mId, mType, mLocation));
        args.putInt(Const.ARGS_SCENIC_ID, mId);
        mFragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .add(R.id.container, mFragment).commit();
    }

    private void initViews() {
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.scenic_video_title);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ExcessiveClickBlocker.isExcessiveClick()) {
                    return;
                }
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        QHVideo data = (QHVideo) v.getTag();
        CommonUtils.playVideo(this, data);
    }
}
