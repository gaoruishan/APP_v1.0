/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.GuideRouteDetailAdapter;
//import com.cmcc.hyapps.andyou.adapter.TripDetailAdapter;
//import com.cmcc.hyapps.andyou.adapter.TripDetailAdapter.DataUpdatedListener;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.Comments.Type;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Comment.VoteResponse;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.Trip;
import com.cmcc.hyapps.andyou.model.TripDay;
import com.cmcc.hyapps.andyou.model.TripDetail;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.Map;

/*路线详情列表*/
public class GuiderRouteDetailActivity extends BaseActivity implements View.OnClickListener{

    private final int REQUEST_CODE_COMMENT_LIST = 1;
    private RecyclerView mRecyclerView;
    private GuideRouteDetailAdapter mAdapter;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private ActionBar mActionBar;
    private int mTripId;
    private String mTripTitle;
    private TextView mVoteTextView;
    private TextView mCommentTextView;
    private Context mContext;
    private int mActionId = R.id.tv_praise;
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onValidClick(View v) {
            switch (v.getId()) {
                case R.id.tv_praise:
                    if (!UserManager.makeSureLogin(activity, Const.REQUEST_CODE_LOGIN)) {
                        voteTrip();
                    } else {
                        mActionId = R.id.tv_praise;
                    }
                    break;
                case R.id.tv_comment:
                    if (!UserManager.makeSureLogin(activity, Const.REQUEST_CODE_LOGIN)) {
                        go2CommentDetail();
                    } else {
                        mActionId = R.id.tv_comment;
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_guider_route_detail_list);
        initView();
        Trip trip = getIntent().getParcelableExtra(Const.EXTRA_TRIP_DATA);
        if (trip == null) {
            mTripId = getIntent().getIntExtra(Const.EXTRA_ID, -1);
        } else {
            mTripId = trip.id;
        }
        if (mTripId != -1) {
            loadTripDetail(mTripId);
            mLoadingProgress.setVisibility(View.VISIBLE);
        } else {
            mLoadingProgress.setVisibility(View.GONE);
            mEmptyHintView.setVisibility(View.VISIBLE);
        }
        ShareManager.getInstance().onStart(activity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Const.REQUEST_CODE_LOGIN) {
            switch (mActionId) {
                case R.id.tv_praise:
                    voteTrip();
                    break;
                case R.id.tv_comment:
                    go2CommentDetail();
                    break;

                default:
                    break;
            }
        } else if (resultCode == CommentListActivity.RESULT_CODE_NEW_COMMENT) {
            int newCommentCount = data.getIntExtra("count", 0);
            newCommentCount += Integer.parseInt(mCommentTextView.getText().toString());
            mCommentTextView.setText(String.valueOf(newCommentCount));
        }
    }

    protected void go2CommentDetail() {
        Intent i = new Intent(mContext, CommentListActivity.class);
        i.putExtra(Const.EXTRA_ID, mTripId);
        i.putExtra(Const.EXTRA_NAME, mTripTitle);
        i.putExtra(Const.EXTRA_TYPE, Type.TRIP);
        startActivityForResult(i, REQUEST_CODE_COMMENT_LIST);
    }

    @Override
    protected void onDestroy() {
        ShareManager.getInstance().onEnd();
        super.onDestroy();
    }

    private void voteTrip() {
        Map<String, String> params = ServerAPI.Comments.buildVoteParams(mContext, mTripId,
                Type.TRIP);
        RequestManager.getInstance().sendGsonRequest(ServerAPI.Comments.VOTE_URL,
                VoteResponse.class,
                new Response.Listener<VoteResponse>() {
                    @Override
                    public void onResponse(VoteResponse response) {
                        mVoteTextView.setText(String.valueOf(response.voteCount));
                        mVoteTextView.setEnabled(false);
                        mAdapter.getTripDetail().voteCount = response.voteCount;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error voteComment: %s", error);
                        mVoteTextView.setEnabled(false);
                        mVoteTextView.setEnabled(false);
                    }
                }, false, params, requestTag);
    }

    private void loadTripDetail(int id) {
        RequestManager.getInstance().sendGsonRequest(Method.GET,
                ServerAPI.Trips.buildGetTripDetailUrl(mContext, id),
                TripDetail.class, null, new Response.Listener<TripDetail>() {
                    @Override
                    public void onResponse(TripDetail tripDetail) {
                        Log.d("onResponse, TripDetail=%s", tripDetail);
                        mTripTitle = tripDetail.title;
                        mAdapter.setTripDetail(tripDetail, new GuideRouteDetailAdapter.DataUpdatedListener() {

                            @Override
                            public void onDataUpdated() {
                                mLoadingProgress.setVisibility(View.GONE);
                                // Remove map mode yet
                                /*
                                 * mActionBar.getRightView().setImageResource(
                                 * R.drawable.ic_action_bar_location_selecter);
                                 * mActionBar
                                 * .getRightView().setOnClickListener(new
                                 * OnClickListener() {
                                 * @Override public void onClick(View v) { //
                                 * TODO } });
                                 */
                            }
                        });
                        mVoteTextView.setText(String.valueOf(tripDetail.voteCount));
                        mVoteTextView.setEnabled(!tripDetail.isVoted);
                        mCommentTextView.setText(String.valueOf(tripDetail.commentCount));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse");
                        mLoadingProgress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), R.string.loading_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                }, true, requestTag);
    }

    private void initView() {
        initActionBar();
        initListView();
        initBottomTab();
    }

    private void initBottomTab() {
        mVoteTextView = (TextView) findViewById(R.id.tv_praise);
        mCommentTextView = (TextView) findViewById(R.id.tv_comment);
        mVoteTextView.setOnClickListener(mOnClickListener);
        mCommentTextView.setOnClickListener(mOnClickListener);
        findViewById(R.id.tv_share).setOnClickListener(mOnClickListener);
    }

    private void initListView() {
        mLoadingProgress = findViewById(R.id.loading_progress);
        mEmptyHintView = findViewById(R.id.empty_hint_view);
        LayoutParams lp = mEmptyHintView.getLayoutParams();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(lp.width, lp.height);
        params.topMargin = ScreenUtils.getDimenPx(activity, R.dimen.action_bar_height); // add
                                                                                        // actionbar
                                                                                        // height
        mEmptyHintView.setLayoutParams(params);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new GuideRouteDetailAdapter();
        ItemClickSupport clickSupport = ItemClickSupport.addTo(mRecyclerView);
        clickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {

            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Object itemObj = view.getTag();
                switch (mAdapter.getItemViewType(position)) {
                    case GuideRouteDetailAdapter.TYPE_HEADER:
                        if (itemObj instanceof TripDetail) {

                        }
                        break;
                    case GuideRouteDetailAdapter.TYPE_TRIP_DAY:
                        if (itemObj instanceof TripDay) {
                            TripDetail tripDetail = mAdapter.getTripDetail();
                            ArrayList<Image> images = new ArrayList<Image>();
                            for (TripDay tripDay : tripDetail.days) {
                                Image image = new Image();
                                image.imagePath = Uri.parse(tripDay.image.largeImage);
                                image.infoTime = tripDay.time;
                                image.infoLocation = tripDay.scenicName;
                                images.add(image);
                            }
                            Intent intent = new Intent(activity, PhotoPreviewActivity.class);
                            intent.putParcelableArrayListExtra(Const.EXTRA_IMAGE_DATA, images);
                            int index = mAdapter.computeImageIndex(position);
                            intent.putExtra(Const.EXTRA_IMAGE_PREVIEW_START_INDEX, index);
                            startActivity(intent);
                        }
                        break;

                    default:
                        Log.e("UNKNOW trip detail type!!!");
                        break;
                }
            }
        });
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        if (!ShareManager.getInstance().hideBorad()) {
            super.onBackPressed();
        }
    }

    private void initActionBar() {
        mActionBar = (ActionBar) findViewById(R.id.action_bar);
        mActionBar.setBackgroundResource(R.drawable.fg_top_shadow);
        mActionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        mActionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
        mActionBar.getRightView().setImageResource(R.drawable.star);
        mActionBar.getRightView().setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.action_bar_right:
                break;
        }

    }
}
