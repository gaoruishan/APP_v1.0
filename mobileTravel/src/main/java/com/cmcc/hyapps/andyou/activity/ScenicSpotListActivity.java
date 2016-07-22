/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.fragment.BasicScenicDataListFragment;
import com.cmcc.hyapps.andyou.fragment.DefaultScenicDataListFragmentImp;
import com.cmcc.hyapps.andyou.model.BasicScenicData;
import com.cmcc.hyapps.andyou.model.ScenicAudio;
import com.cmcc.hyapps.andyou.model.ScenicSpot.ScenicSpotList;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;

/**
 * @author kuloud
 */
public class ScenicSpotListActivity extends ServiceBaseActivity {
    private int mId = -1;
    private BasicScenicDataListFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mId = getIntent().getIntExtra(Const.EXTRA_ID, -1);
        if (mId < 0) {
            Log.e("Invalid scenic id %s", mId);
            finish();
            return;
        }

        setContentView(R.layout.activity_scenic_spot_list);
        initViews();

        mFragment = new DefaultScenicDataListFragmentImp();
        Bundle args = new Bundle();
        args.putString(Const.ARGS_KEY_MODEL_CLASS, ScenicSpotList.class.getName());
        args.putString(Const.ARGS_LOADER_URL, ServerAPI.ScenicSpots.buildUrl(mId));
        args.putInt(Const.ARGS_SCENIC_ID, mId);
        mFragment.setArguments(args);
        mFragment.setOnItemClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View v) {
                BasicScenicData data = (BasicScenicData) v.getTag();
                if (data != null && data.audioIntro() != null && data.audioIntro().size() > 0) {
                    if (!data.audioIntro().get(0).validate()) {
                        Toast.makeText(activity, R.string.audio_invalid, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // TODO
                    Intent intent = new Intent(ScenicSpotListActivity.this, ListenActivity.class);
                    // TODO
                    ArrayList<ScenicAudio> sas = new ArrayList<ScenicAudio>();
                    for (BasicScenicData sd : mFragment.getScenicDataList()) {
                        sas.addAll(sd.audioIntro());
                    }
                    intent.putParcelableArrayListExtra(Const.EXTRA_AUDIO, sas);
                    intent.putExtra(Const.EXTRA_ID, mId);
                    intent.putExtra(Const.EXTRA_SPOT_ID, data.id());
                    intent.putExtra(Const.EXTRA_NAME, data.name());
                    startActivity(intent);
                }
            }
        });
        getFragmentManager().beginTransaction()
                .add(R.id.container, mFragment).commit();
    }

    private void initViews() {
        initActionBar();
        findViewById(R.id.iv_service).setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                showServicePopup();
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(getIntent().getStringExtra(Const.EXTRA_NAME));
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_map_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
        actionBar.getRightView().setOnClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View v) {
                ArrayList<BasicScenicData> scenicList = mFragment.getScenicDataList();
                if (scenicList != null && scenicList.size() != 0) {
                    Intent intent = new Intent(activity, ScenicSpotsMapAct/*ScenicSpotsMapActivity*/.class);
                    intent.putParcelableArrayListExtra(Const.EXTRA_SCENIC_SPOTS_DATA,
                            scenicList);
                    intent.putExtra(Const.EXTRA_NAME, getIntent().getStringExtra(Const.EXTRA_NAME));
                    startActivity(intent);
                }
            }
        });
    }
}
