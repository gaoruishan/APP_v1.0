
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.CommentDetailsActivity;
import com.cmcc.hyapps.andyou.activity.CommentEditActivity;
import com.cmcc.hyapps.andyou.activity.FreshLoginActivity;
import com.cmcc.hyapps.andyou.activity.LoginActivity;
import com.cmcc.hyapps.andyou.activity.NavigationDetailActivity;
import com.cmcc.hyapps.andyou.adapter.GuiderRaidersCommentListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.ResponseError;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.QHNavigation;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.model.Scenic;
import com.cmcc.hyapps.andyou.util.AnimUtils;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemSubViewClickListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class GuiderRadiersCommentListFragment extends BaseFragment implements OnClickListener,DataLoaderCallback<Comment.CommentList> {
    private final String TAG = getClass().getName();//"GuiderListFragment";
    private final int REQUEST_CODE_LOGIN_NEW_COMMENT = 1;
    private final int REQUEST_CODE_LOGIN_VOTE = 2;
    private final int REQUEST_CODE_LOGIN_COMMENT_DETAIL = 3;
    private final int REQUEST_CODE_SEARCH = 4;
    private final int REQUEST_CODE_COMMENT_DETAIL = 5;
    private final int REQUEST_CODE_POST_COMMENT = 6;
    private ActionBar mActionBar;
    private GuiderRaidersCommentListAdapter mAdapter;
    private View mLoadingProgress;
    private View mReloadView;
    private PullToRefreshRecyclerView mPullToRefreshView;
    private UrlListLoader<Comment.CommentList> mScenicListLoader;
//    private Request<HomeBanner.HomeBannerLists> mBannerRequest;
    private ViewGroup mRootView;
    private View mEmptyHintView;
    private Scenic mScenic;
    private int HTTP_GET_PARAM_LIMIT = 10;
    private RecyclerView commentListView;
    private Comment mComment;
    private QHScenic scenic;
    private TextView mVoteText;
    private int mId;


    private ArrayList<Comment> raidersListData = new ArrayList<Comment>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareManager.getInstance().onStart(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.guide_list_fragment, container,false);
        scenic = getArguments().getParcelable(Const.QH_SECNIC);
        mId = getArguments().getInt(Const.QH_SECNIC_ID);
        initViews();
//        reload();
//        loadCommentData(DataLoader.MODE_REFRESH);
        initActionBar(mRootView);

        return mRootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        mAdapter.getDataItems().clear();
        reload();
        loadCommentData(DataLoader.MODE_REFRESH);
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageEnd(TAG);
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
    private void reload() {
        mPullToRefreshView.setMode(Mode.PULL_FROM_END);
        commentListView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);
        mScenicListLoader = null;
        RequestManager.getInstance().getRequestQueue().cancelAll(mRequestTag);
    }
    private void initViews() {
        initListView();
        initPullToRefresh();
        mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
        mReloadView = mRootView.findViewById(R.id.reload_view);
        mEmptyHintView = mRootView.findViewById(R.id.empty_hint_view);
        mReloadView.setOnClickListener(this);
    }
    private void initPullToRefresh() {
        mPullToRefreshView = (PullToRefreshRecyclerView) mRootView.findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    reload();
                } else {
                    loadCommentData(DataLoader.MODE_LOAD_MORE);
                }
            }
        });
    }
    private void initListView() {
        commentListView = (RecyclerView) mRootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        commentListView.setLayoutManager(layoutManager);
        mAdapter = new GuiderRaidersCommentListAdapter(getActivity());
        ItemClickSupport clickSupport = ItemClickSupport.addTo(commentListView);
        clickSupport.setOnItemSubViewClickListener(new OnItemSubViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                    onItemClicked(view);
            }
        });
        int scape = ScreenUtils.dpToPxInt(getActivity(), 8);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity().getResources().getDrawable(R.drawable.friends_circle_layer_list_top_bottom),scape,0);
        dividerItemDecoration.initWithRecyclerView(commentListView);
        commentListView.addItemDecoration(dividerItemDecoration);
        commentListView.setItemAnimator(new DefaultItemAnimator());
        commentListView.setAdapter(mAdapter);
    }

    protected void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.getTitleView().setText("评论");
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setOnClickListener(this);

        actionBar.setBackgroundResource(R.drawable.fg_top_shadow);
        actionBar.getRightView().setImageResource(R.drawable.icon_edit);
        actionBar.getRightView().setOnClickListener(this);

        actionBar.setBackgroundResource(R.color.title_bg);
//        mActionBar.getRightView().setImageResource(R.drawable.);
    }


    private void showReloadView() {
        commentListView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
    }

    public final static int COMMENT_SECNIC = 0;//景区评论
    public final static int COMMENT_RAIDERS = 1;//攻略评论
    public final static int COMMENT_TOUTE = 2;//路线评论
    public final static int COMMENT_COMMENT = 3;//评论的 评论
    public int commentType = 0;

    private void loadCommentData(int mode) {
        Log.e("-------","loadNearScenics--");
        if(null==scenic) scenic = new QHScenic();scenic.id = mId;


        String  url  = ServerAPI.User.buildSecnicCommentUrl(scenic.id);
        switch (commentType){
            case COMMENT_SECNIC:
                 url  = ServerAPI.User.buildSecnicCommentUrl(scenic.id);
                break;
            case COMMENT_RAIDERS:
                url  = ServerAPI.User.buildSecnicCommentUrl(scenic.id);
                break;
            case COMMENT_TOUTE:
                url  = ServerAPI.User.buildSecnicCommentUrl(scenic.id);
                break;
            case COMMENT_COMMENT:
                url  = ServerAPI.User.buildSecnicCommentUrl(scenic.id);
                break;
        }
    //    mLoadingProgress.setVisibility(View.VISIBLE);
//      commentListView.setVisibility(View.GONE);

        if (mScenicListLoader == null)
        {
            mScenicListLoader = new UrlListLoader<Comment.CommentList>( mRequestTag, Comment.CommentList.class);
            mScenicListLoader.setUrl(url);
            mScenicListLoader.setPageLimit(HTTP_GET_PARAM_LIMIT);
        }

        mScenicListLoader.loadMoreQHData(this, mode);
    }
    @Override
    public void onLoadFinished(Comment.CommentList list, int mode) {
        mPullToRefreshView.onRefreshComplete();
        onListLoaded(list,mode);
    }

    @Override
    public void onLoadError(int mode) {
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        commentListView.setVisibility(View.VISIBLE);
    }
    private void onListLoaded(Comment.CommentList data,int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        commentListView.setVisibility(View.VISIBLE);



        if (data == null || data.list == null|| (mScenicListLoader != null && !mScenicListLoader.getPaginator().hasMorePages())) {
            // Disable PULL_FROM_END
//            mPullToRefreshView.setMode(Mode.PULL_FROM_END);
            return;

        }

        if (data.results == null || data.results.isEmpty()) {
            if (mode == DataLoader.MODE_LOAD_MORE){
                ToastUtils.AvoidRepeatToastShow(GuiderRadiersCommentListFragment.this.getActivity(),R.string.msg_no_more_data,Toast.LENGTH_SHORT);
            }else {
                mEmptyHintView.setVisibility(View.VISIBLE);
            }
            return;
        }else {
            mEmptyHintView.setVisibility(View.GONE);
        }

        if (data != null) {
            if (data.pagination != null && data.pagination.offset == 0) {
                mAdapter.setDataItems(null);
            }
            mAdapter.appendDataItems(data.results);
            raidersListData.addAll(data.results);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onItemClicked(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        mComment = (Comment) v.getTag();
        if (mComment == null) {
            Log.e("NULL comment");
            return;
        }

        switch (v.getId()) {
            case R.id.item_comment_root:
            case R.id.comment_cover_image:
            case R.id.comment_count: {
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_SCENIC_COMMENT_DETAIL);
                AnimUtils.doScaleFadeAnim(v);
                jumpCommentDetail();
                break;
            }

            case R.id.comment_vote_count: {
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_SCENIC_COMMENT_VOTE);
                AnimUtils.doScaleFadeAnim(v);
                mVoteText = (TextView) v;
                if(AppUtils.getQHUser(getActivity()) == null){
                    Intent login = new Intent(getActivity(), FreshLoginActivity.class);
                    getActivity().startActivity(login);
                }else {
                    voteComment();
                }
                break;
            }
            case R.id.comment_location:{
                //得到经纬度，打开一个dialog,服务器没有传入地理位置
                if(mComment.latitude==0f&&mComment.longitude==0f){
                    Toast.makeText(getActivity(),"该用户没有分享他的位置",Toast.LENGTH_SHORT).show();
                }else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("开始导航",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getActivity(),NavigationDetailActivity.class);
                            Bundle bundle = new Bundle();
                            //
                            QHNavigation tag = new QHNavigation((float)mComment.longitude,(float)mComment.latitude);
                            bundle.putParcelable("navi_detail",tag);
                            intent.putExtra("navi_bundle",bundle);
                            getActivity().startActivity(intent);
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消导航",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }

                break;
            }
        }


    }
    public static final String PARAM_OBJECT_TYPE = "";
    public class CommentObj  {
        public  String obj_id;
        public  String obj_type;

        CommentObj (String id,String type){
            obj_id = id;
            obj_type = type;
        }
    }
    private void voteComment() {
        if (mComment == null || mVoteText == null) {
            Log.e("[voteComment] NULL comment:" + mComment);
            return;
        }

        CommentObj obj = new CommentObj(""+mComment.id,ServerAPI.Comments.Type.COMMENT.value());
        Gson gson = new Gson();
        String jsonBody = gson.toJson(obj);
        RequestManager.getInstance().sendGsonRequest(Request.Method.POST, ServerAPI.Comments.GUIDER_VOTE_URL,
                jsonBody,
                Comment.VoteResponse.class, new Response.Listener<Comment.VoteResponse>() {
                    @Override
                    public void onResponse(Comment.VoteResponse response) {

                        mVoteText.setText(String.valueOf(response.voteCount));
                        mVoteText.getCompoundDrawables()[0].setLevel(2);
                        mComment.voted = 1;
                        mComment.voteCount = response.voteCount;
                       // mVoteText.setEnabled(false);
                        mVoteText.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToastUtils.AvoidRepeatToastShow(GuiderRadiersCommentListFragment.this.getActivity(),R.string.error_already_voted,Toast.LENGTH_SHORT);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error voteComment: %s", error);
                        if (error instanceof ResponseError
                                && ((ResponseError) error).errCode == ServerAPI.ErrorCode.ERROR_ALREADY_VOTED) {
                            Toast.makeText(getActivity(), R.string.error_already_voted,Toast.LENGTH_SHORT).show();
                        } else {
                            AppUtils.handleResponseError(getActivity(), error);
                        }

                    }
                }, "--------------");
    }


    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left:
                getActivity().finish();
                break;
            case R.id.action_bar_right: {
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_SCENIC_COMMENT_EDIT);
                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_LOGIN_NEW_COMMENT)) {
                    postNewComment();
                }
                break;
            }
            case R.id.reload_view: {
                reload();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        ShareManager.getInstance().onEnd();
        super.onDestroy();
    }
    private void postNewComment() {
        Intent commentEdit = new Intent(getActivity(), CommentEditActivity.class);
        commentEdit.putExtra(Const.EXTRA_ID, scenic.id);
        startActivityForResult(commentEdit, REQUEST_CODE_POST_COMMENT);
    }
    private void jumpCommentDetail() {
        if (mComment == null) {
            Log.e("[jumpCommentDetail] NULL comment");
            return;
        }
        Intent intent = new Intent(getActivity(), CommentDetailsActivity.class);
        intent.putExtra(Const.EXTRA_COMMENT_DATA, mComment);
        startActivityForResult(intent, REQUEST_CODE_COMMENT_DETAIL);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LOGIN_NEW_COMMENT:
//                    postNewComment();
                    break;
                case REQUEST_CODE_LOGIN_VOTE:
//                    voteComment();
                    break;
                case REQUEST_CODE_LOGIN_COMMENT_DETAIL:
                    jumpCommentDetail();
                    break;

                case REQUEST_CODE_COMMENT_DETAIL:
                    Comment comment = (Comment) data.getParcelableExtra(Const.EXTRA_COMMENT_DATA);
                    if (comment != null) {
                        mComment.commentCount = comment.commentCount;
                        mComment.isVoted = comment.isVoted;
                        mComment.voted = comment.voted;
                        mComment.voteCount = comment.voteCount;
                        List<Comment> dataItems = mAdapter.getDataItems();
                        if (dataItems != null) {
                            int position = -1;
                            for (int i = 0; i < dataItems.size(); i++) {
                                if (dataItems.get(i).id == mComment.id) {
//                                    dataItems.get(i).commentCount =comment.commentCount;
                                    position = i;
                                    break;
                                }
                            }
                            if (position >= 0) {
                                // Count the header item
                                position++;
                                mAdapter.notifyItemChanged(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_POST_COMMENT: {
                    Comment c = (Comment) data.getParcelableExtra(Const.EXTRA_COMMENT_DATA);
                    if (c != null) {
                        mEmptyHintView.setVisibility(View.GONE);
                        mAdapter.getDataItems().add(0, c);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

}
