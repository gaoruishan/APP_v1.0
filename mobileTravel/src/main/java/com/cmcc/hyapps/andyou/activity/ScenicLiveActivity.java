package com.cmcc.hyapps.andyou.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.Cache;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.VideoListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.ScenicVideos;
import com.cmcc.hyapps.andyou.app.ServerAPI.ScenicVideos.Type;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.List;
import com.cmcc.hyapps.andyou.model.QHScenic.QHVideo.QHVideoList;
import com.cmcc.hyapps.andyou.model.QHScenic.QHVideo;

public class ScenicLiveActivity extends ServiceBaseActivity implements OnClickListener {
    private static final int REQUEST_CODE_POST_VIDEO = 1;
    private int mId = -1;
    private Type mType;

    private Context mContext;
    //    private TextView mSharedVideoListTitle;
    private RecyclerView mRecyclerView;
    private VideoListAdapter mAdapter;
//    private NetworkImageView mCoverImageView;

    private Cache mCache = RequestManager.getInstance().getRequestQueue().getCache();
    private boolean mForceReload;
    private UrlListLoader<QHVideoList> mSharedVideoLoader;
    //    private UrlListLoader<VideoList> mLiveVideoLoader;
//    private TextView mCameras;
    // TODO: test
    private Location mLocation = new Location(18.296388,
        109.208867);

    private PullToRefreshRecyclerView mPullToRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        mId = getIntent().getIntExtra(Const.EXTRA_ID, -1);
        mType = (Type) getIntent().getSerializableExtra(Const.EXTRA_VIDEO_TYPE);
        if (mType == null) {
            Log.e("Invalid scenic mType %s", mType);
            finish();
            return;
        }

        if (mId < 0) {
            finish();
            return;
        }

        setContentView(R.layout.activity_scenic_video_list);

//        mLiveVideoLoader = new UrlListLoader<Video.VideoList>(
//                requestTag,
//                VideoList.class);
//        mLiveVideoLoader.setUrl(getBannerUrl());

        mSharedVideoLoader = new UrlListLoader<QHVideoList>(
            requestTag,
                QHVideoList.class);
        mSharedVideoLoader.setUrl(getVideoListUrl(ServerAPI.ScenicVideos.Type.SHARED));
        initViews();
    }

    private void initViews() {
        initActionBar();

//        mCoverImageView = (NetworkImageView) findViewById(R.id.scenic_live_video_cover);
//        mCoverImageView.setOnClickListener(this);
//        mCameras = ((TextView) findViewById(R.id.scenic_live_cameras));
//        mCameras.setOnClickListener(this);
//        mSharedVideoListTitle = (TextView) findViewById(R.id.live_share_category)
//                .findViewById(R.id.scenic_category_title);
//        mSharedVideoListTitle.setText(R.string.scenery_detail_func_live_title);
//        findViewById(R.id.live_share_category).setOnClickListener(this);

        initPullToRefresh();
        mRecyclerView = (RecyclerView) mPullToRefreshView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
//        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int scap = ScreenUtils.dpToPxInt(mContext, 4);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        mAdapter = new VideoListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        findViewById(R.id.iv_service).setOnClickListener(this);

        refreshData();
    }

    private void initPullToRefresh() {
        mPullToRefreshView = (PullToRefreshRecyclerView) findViewById(R.id.pulltorefresh_recyclerview);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                    mSharedVideoLoader.loadMoreData(mShareDataLoaderCallback,
                        DataLoader.MODE_LOAD_MORE);
                }
            }
        });
    }

    private void refreshData() {
//        mLiveVideoLoader.loadMoreData(this, DataLoader.MODE_LOAD_MORE);
        mSharedVideoLoader.loadMoreData(mShareDataLoaderCallback, DataLoader.MODE_LOAD_MORE);

        // loadSharedVideoList();
        // getCurrentLocation();
        Log.d("refreshData, mForceReload=%s", mForceReload);
    }

    private DataLoaderCallback<QHVideoList> mShareDataLoaderCallback = new DataLoaderCallback<QHVideoList>() {

        @Override
        public void onLoadFinished(QHVideoList videoList, int mode) {
            mPullToRefreshView.onRefreshComplete();

            List<QHVideo> list = null;
            if (videoList != null) {
                list =  videoList.results;
            }

            if (list == null || list.isEmpty()) {
                // if (mVideoList.isEmpty()) {
                // mEmptyHintView.setVisibility(View.VISIBLE);
                // }
            } else {
                if (mSharedVideoLoader.getPaginator().hasMorePages()) {
                    mPullToRefreshView.setMode(Mode.PULL_FROM_END);
                } else {
                    mPullToRefreshView.setMode(Mode.DISABLED);
                }
                mPullToRefreshView.setVisibility(View.VISIBLE);
                if (mode == DataLoader.MODE_REFRESH) {
                    mAdapter.setDataItems(list);
                } else {
                    mAdapter.appendDataItems(list);
                }
            }
        }

        @Override
        public void onLoadError(int mode) {

        }

    };

    //
    // private void loadSharedVideoList() {
    // if (mSharedListRequest != null) {
    // mSharedListRequest.cancel();
    // }
    //
    // final String url = getVideoListUrl(ServerAPI.ScenicVideos.Type.SHARED);
    // if (mForceReload) {
    // mCache.remove(url);
    // }
    //
    // mSharedListRequest =
    // RequestManager.getInstance().sendGsonRequest(Method.GET, url,
    // VideoList.class, null,
    // new Response.Listener<VideoList>() {
    // @Override
    // public void onResponse(VideoList videoList) {
    // Log.d("onResponse, VideoList=%s", videoList);
    //
    // if (videoList != null) {
    // // mSharedVideoListTitle.setText(videoList.displayTitle);
    // mAdapter.setDataItems(videoList.list);
    // }
    //
    // }
    // }, new Response.ErrorListener() {
    // @Override
    // public void onErrorResponse(VolleyError error) {
    // Log.e(error, "onErrorResponse");
    // if (mSharedListRequest != null) {
    // mSharedListRequest.markDelivered();
    // }
    // }
    // }, !mForceReload, this);
    // }

    private String getVideoListUrl(ServerAPI.ScenicVideos.Type type) {
        return ServerAPI.ScenicVideos.buildUrl(mId, type, null);
    }

    private String getBannerUrl() {
        return ServerAPI.ScenicVideos.buildUrl(-1, ScenicVideos.Type.LIVE, mLocation);
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

        actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_record_selecter);
        actionBar.getRightView().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
//            case R.id.live_share_category: {
//                Intent intent = new Intent(this, ScenicVideoListActivity.class);
//                intent.putExtra(Const.EXTRA_VIDEO_TYPE, ServerAPI.ScenicVideos.Type.SHARED);
//                intent.putExtra(Const.EXTRA_ID, mId);
//                startActivity(intent);
//            }
//                break;
//            case R.id.scenic_live_video_cover: {
//                if (v.getTag() != null) {
//                    CommonUtils.playVideo(this, (Video) v.getTag());
//                }
//            }
//                break;
//            case R.id.scenic_live_cameras: {
//                Intent intent = new Intent(this, ScenicVideoListActivity.class);
//                intent.putExtra(Const.EXTRA_VIDEO_TYPE, ScenicVideos.Type.LIVE);
//                intent.putExtra(Const.EXTRA_COORDINATES, mLocation);
//                // intent.putExtra(Const.EXTRA_ID, mId);
//                startActivity(intent);
//            }
//                break;
            case R.id.action_bar_right: {
                // if (!UserManager.makeSureLogin(this,
                // REQUEST_CODE_LOGIN_POST_VIDEO)) {
                postVideo();
                // }
            }
            break;
            case R.id.iv_service: {
                showServicePopup();
            }
            break;
            default:
                break;
        }
    }

    private void postVideo() {
        Intent intent = new Intent(this, VideoUploadActivity.class);
        intent.putExtra(Const.EXTRA_ID, mId);
        startActivityForResult(intent, REQUEST_CODE_POST_VIDEO);
    }

//    @Override
//    public void onLoadFinished(VideoList videoList, int mode) {
//        List<Video> list = null;
//        if (videoList != null) {
//            list = videoList.list;
//        }
//
//        if (list != null && !list.isEmpty()) {
//            Video video = list.get(0);
//            mCoverImageView.setImageUrl(video.thumbnail, RequestManager.getInstance()
//                    .getImageLoader());
//            mCoverImageView.setTag(video);
//            if (videoList.pagination != null && videoList.pagination.total > 0) {
//                mCameras.setVisibility(View.VISIBLE);
//                mCameras.setText(String
//                        .valueOf(videoList.pagination.total));
//            }
//
//        }
//    }
//
//    @Override
//    public void onLoadError(int mode) {
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_POST_VIDEO:
                    mSharedVideoLoader.loadMoreData(mShareDataLoaderCallback, DataLoader.MODE_REFRESH);
                    break;
                default:
                    break;
            }
        }
    }
}
