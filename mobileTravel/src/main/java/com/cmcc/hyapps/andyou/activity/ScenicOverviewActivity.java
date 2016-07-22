/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemSubViewClickListener;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.ScenicOverviewAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.AudioIntro;
import com.cmcc.hyapps.andyou.model.BasicScenicData;
import com.cmcc.hyapps.andyou.model.ScenicAudio;
import com.cmcc.hyapps.andyou.model.ScenicSpot;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuloud
 */
public class ScenicOverviewActivity extends BaseActivity implements OnClickListener {
    private RecyclerView mRecyclerView;

    private BasicScenicData mScenicData;
    private ScenicOverviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mScenicData = (BasicScenicData) getIntent().getParcelableExtra(Const.EXTRA_SCENIC_DATA);
        if (mScenicData == null) {
            finish();
            return;
        }

        initViews();
        loadScenicSpotDetails();
    }

    private void initViews() {
        initActionBar();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        int scap = ScreenUtils.dpToPxInt(activity, 13);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(scap));
        mAdapter = new ScenicOverviewAdapter(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemClickSupport clickSupport = ItemClickSupport.addTo(mRecyclerView);
        clickSupport.setOnItemSubViewClickListener(new OnItemSubViewClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    onHeaderItemClicked(view);
                } else {
                    onCommentItemClicked(view);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void onCommentItemClicked(View view) {
        switch (view.getId()) {
            case R.id.scenic_overview_group_play: {
                // TODO
                AudioIntro item = (AudioIntro) view.getTag();
                item.scenicName = mScenicData.name();
                List<AudioIntro> audioList = new ArrayList<AudioIntro>();
                audioList.add(item);
                ArrayList<ScenicAudio> audioIntro = new ArrayList<ScenicAudio>();
                ScenicAudio scenicAudio = new ScenicAudio();
                scenicAudio.spotId = mScenicData.id();
                scenicAudio.spotName = mScenicData.name();
                scenicAudio.image = item.imageUrl;
                scenicAudio.audio = audioList;
                scenicAudio.location = mScenicData.location();
                audioIntro.add(scenicAudio);

                Intent intent = new Intent(this, ListenActivity.class);
                intent.putParcelableArrayListExtra(Const.EXTRA_AUDIO, audioIntro);
                intent.putExtra(Const.EXTRA_ID, mScenicData.id());
                intent.putExtra(Const.EXTRA_NAME, mScenicData.name());
                startActivity(intent);
                break;
            }
        }
    }

    private void onHeaderItemClicked(View view) {

    }

    private void loadScenicSpotDetails() {
        final String url = ServerAPI.ScenicSpots.buildDetailsUrl(mScenicData.id());
        Log.d("loadScenicSpotDetails: %s", url);

        RequestManager.getInstance().sendGsonRequest(url, ScenicSpot.class,
                new Response.Listener<ScenicSpot>() {
                    @Override
                    public void onResponse(ScenicSpot response) {
                        Log.d("onResponse, ScenicSpot=%s", response);
                        if (response == null || response.audioIntro() == null) {
                            return;
                        }

                        // for (AudioIntro audio : response.audioIntro()) {
                        // if (audio != null) {
                        // audio.scenicName = mScenicData.name();
                        // }
                        // }

                        mAdapter.setHeader(response.coverImage());
                        mAdapter.setDataItems(response.audioIntro().get(0).audio);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error loading weather from %s", url);
                    }
                }, requestTag);
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(mScenicData.name());
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left:
                finish();
                break;

            default:
                break;
        }
    }

}
