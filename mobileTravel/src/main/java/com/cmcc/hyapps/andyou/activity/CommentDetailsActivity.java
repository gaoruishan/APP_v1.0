
package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.CommentDetailsAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.Comments.Type;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.StringConverter;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.Comment.CommentList;
import com.cmcc.hyapps.andyou.model.QHCollectionStrategy;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.LocationUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentDetailsActivity extends BaseActivity implements OnClickListener,
        DataLoaderCallback<CommentList> ,UploadTask.UploadCallBack{

    private Comment mComment;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mContainerView;

    private RecyclerView mRecyclerView;
    private CommentDetailsAdapter mAdapter;

    private PullToRefreshRecyclerView mPullToRefreshView;

    private UrlListLoader<CommentList> mCommentLoader;
    private UrlListLoader<CommentList> mNowCommentLoader;
    private View mPostButton;
    private EditText mNewCommentEditText;
    private Boolean tag;

    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        tag = intent.getBooleanExtra("TAG", false);
        mComment = (Comment) intent.getParcelableExtra(Const.EXTRA_COMMENT_DATA);
        int id = intent.getIntExtra("id", -1);
        if(id != -1)
        {
            loadOwnComment(id);
        }
        if (mComment == null) {
            Log.e("Invalid comment obj %s", mComment);
            finish();
            return;
        }
        setContentView(R.layout.activity_comment_details);
        initViews();
        loadComments(DataLoader.MODE_REFRESH);
    }

    private void initViews() {
        initActionBar();

        mLoadingProgress = findViewById(R.id.loading_progress);
        mEmptyHintView = findViewById(R.id.empty_hint_view);
        mContainerView = findViewById(R.id.container);

        initPullToRefresh();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new CommentDetailsAdapter(this);
        mAdapter.setHeader(mComment);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mPostButton = findViewById(R.id.comment_post_button);
        mPostButton.setOnClickListener(this);
        mNewCommentEditText = (EditText) findViewById(R.id.new_commnet_text);
    }

    private void initPullToRefresh() {
        mPullToRefreshView = (PullToRefreshRecyclerView) findViewById(R.id.pulltorefresh_recyclerview);
        mPullToRefreshView.setMode(Mode.PULL_FROM_END);
        mRecyclerView = mPullToRefreshView.getRefreshableView();
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                loadComments(DataLoader.MODE_LOAD_MORE);
            }
        });
    }

    private void initActionBar() {
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        if (mComment.author != null && !TextUtils.isEmpty(mComment.author.name)) {
            actionBar.setTitle(mComment.author.name);
        }
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(this);
        if (tag){
            actionBar.getRightView().setVisibility(View.GONE);
        }else {
            if (mComment.voted==1) {
                actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_praise_selected);
            } else {
                actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_praise);
            }

            actionBar.getRightView().setOnClickListener(this);
        }

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
            case R.id.action_bar_right:
                if (mComment == null) {
                    return;
                }
                if (mComment.isVoted){
                    ToastUtils.AvoidRepeatToastShow(CommentDetailsActivity.this,R.string.error_already_voted,Toast.LENGTH_SHORT);
                    return;
                }
                if (!UserManager.makeSureLogin(CommentDetailsActivity.this, REQUEST_CODE_LOGIN_NEW_COMMENT)) {
                    voteComment(mComment, v, mAdapter.getVoteCountView());
                }

                break;
            case R.id.comment_post_button: {
                if (!TextUtils.isEmpty(mNewCommentEditText.getText().toString().trim())) {
                    if (!UserManager.makeSureLogin(CommentDetailsActivity.this, REQUEST_CODE_LOGIN_NEW_COMMENT)) {

                        mNewCommentEditText.setEnabled(false);
                        postComment(mNewCommentEditText.getText().toString());
                        mNewCommentEditText.setText("");

                    }

                } else {
                    Toast.makeText(this, R.string.error_no_comment, Toast.LENGTH_SHORT).show();
                }
                break;
            }

            default:
                break;
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
    private void voteComment(final Comment comment, final View voteButton, final TextView voteText) {
        if (voteText == null) {
            Log.e("voteComment view NULL");
        }
        voteButton.setEnabled(false);
//        Map<String, String> params = ServerAPI.Comments.buildVoteParams(getApplicationContext(), comment.id, Type.COMMENT);
        CommentObj obj = new CommentObj(""+comment.id, Type.COMMENT.value());
        Gson gson = new Gson();
        String jsonBody = gson.toJson(obj);
        RequestManager.getInstance().sendGsonRequest(Request.Method.POST, ServerAPI.Comments.GUIDER_VOTE_URL,
                jsonBody,
                QHCollectionStrategy.class, new Response.Listener<QHCollectionStrategy>() {
                    @Override
                    public void onResponse(QHCollectionStrategy response) {

                        voteText.setText(String.valueOf(response.vote_count));
                        voteButton.setEnabled(false);
                        comment.isVoted = true;
                        comment.voted =1;
                        comment.voteCount = response.vote_count;

                    //    ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
                        actionBar.getRightView().setImageResource( R.drawable.ic_action_bar_praise_selected);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e(error, "Error voteComment: %s", error);
                        voteButton.setEnabled(true);
                    }
                }, "--------------");



       /* RequestManager.getInstance().sendGsonRequest(ServerAPI.Comments.VOTE_URL,
                VoteResponse.class,
                new Response.Listener<VoteResponse>() {
                    @Override
                    public void onResponse(VoteResponse response) {
                        voteText.setText(String.valueOf(response.voteCount));
                        voteButton.setEnabled(false);
                        comment.isVoted = true;
                        comment.voteCount = response.voteCount;

                        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
                        actionBar.getRightView().setImageResource(
                                R.drawable.ic_action_bar_praise_selected);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error voteComment: %s", error);
                        voteButton.setEnabled(true);
                    }
                }, false, params, requestTag);*/
    }

   /* private void postComment(String commentStr) {
        final QHComment comment = new QHComment();
        comment.content = commentStr;
        comment.object_id = mComment.id;
        comment.ctype = Integer.parseInt(Type.COMMENT.value());
        comment.latitude = LocationUtil.getInstance(getApplicationContext()).getLatitude();
        comment.longitude = LocationUtil.getInstance(getApplicationContext()).getLongitude();
        comment.address = "";
//        comment.allowReply = false;
//        comment.allowVote = false;

        Gson gson = new Gson();
        String jsonBody = gson.toJson(comment);
        Log.d("posting comment, mComment=%s", jsonBody);
        RequestManager.getInstance().sendGsonRequest(Request.Method.POST,
                ServerAPI.User.buildWriteCommentUrl(),
                jsonBody,
                Comment.class, new Response.Listener<Comment>() {
                    @Override
                    public void onResponse(Comment result) {

//ddd
//                        Gson mGson = new Gson();
//                        GsonBuilder gb = new GsonBuilder();
//                        gb.registerTypeAdapter(String.class, new StringConverter());
//                        mGson = gb.create();
//                        mComment =  mGson.fromJson(result.toString(), Comment.class);

                        commentPostSuccess(result);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }, requestTag);
    }*/
    private void postComment(String commentStr){
        final Comment comment = new Comment();
        comment.content = commentStr;
        comment.objectId = mComment.id;
        comment.type = Type.COMMENT.value();
        comment.ctype = Integer.parseInt(Type.COMMENT.value());
        comment.allowReply = false;
        comment.allowVote = false;
        comment.anonymous = AppUtils.getVistorId(activity);
         List<File> imgFiles = new ArrayList<File>();
        String url = ServerAPI.User.buildWriteCommentUrl();


        Map<String, String> params = new HashMap<String, String>();
        Map<String, File> fileParams = new HashMap<String, File>();
        params.put("content",commentStr);
        params.put("object_id",""+mComment.id);
        params.put("ctype",""+Integer.parseInt(Type.COMMENT.value()));
        params.put("latitude",""+LocationUtil.getInstance(getApplicationContext()).getLatitude());
        params.put("longitude",""+LocationUtil.getInstance(getApplicationContext()).getLongitude());
        params.put("address",""+LocationUtil.getInstance(getApplicationContext()).getAddress());

//        new UploadTask(getApplicationContext(), url,2,fileParams , params,this).execute("", "");

        new UploadTask(CommentDetailsActivity.this, imgFiles , commentStr, mComment.id, Integer.parseInt(Type.COMMENT.value()) ,this).execute("", "");
    }
    /*private void postComment(String commentStr) {
        final Comment comment = new Comment();
        comment.content = commentStr;
        comment.objectId = mComment.id;
        comment.type = Type.COMMENT.value();
        comment.ctype = Integer.parseInt(Type.COMMENT.value());
        comment.allowReply = false;
        comment.allowVote = false;
        comment.anonymous = AppUtils.getVistorId(activity);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(comment);
        Log.d("posting comment, mComment=%s", jsonBody);
        RequestManager.getInstance().sendGsonRequest(Method.POST,
                ServerAPI.User.buildWriteCommentUrl(),
                jsonBody,
                NewCommentResult.class, new Response.Listener<NewCommentResult>() {
                    @Override
                    public void onResponse(NewCommentResult result) {
                        Log.d("New comment posted, id=: %d", result.commentId);
                        if (result.commentId > 0) {
                            comment.id = result.commentId;
                            comment.setAuthor(AppUtils.getUser(getApplicationContext()));
                            // TODO: This time is may different from server time
                            comment.createTime = TimeUtils.formatTime(System.currentTimeMillis(), TimeUtils.DATE_TIME_FORMAT);
                            commentPostSuccess(comment);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error post new comment, error=%s", error);
                        commentPostError();
                    }
                }, requestTag);
    }*/

    protected void commentPostError() {
        mNewCommentEditText.setEnabled(true);
        Toast.makeText(this, R.string.comment_post_failed, Toast.LENGTH_SHORT).show();
    }

    protected void commentPostSuccess(Comment newComment) {
        mNewCommentEditText.setEnabled(true);
        List<Comment> list = mAdapter.getDataItems();
//        list.add(newComment);
        list.add(0, newComment);
        /*Collections.sort(list, new Comparator<Comment>() {

            @Override
            public int compare(Comment lhs, Comment rhs) {
                return (int) (TimeUtils
                        .parseTimeToMills(rhs.createTime) - TimeUtils
                        .parseTimeToMills(lhs.createTime));
            }
        });*/

        mAdapter.setDataItems(list);

        Comment header = mAdapter.getHeader();
        if (header != null) {
            header.commentCount++;
            mAdapter.notifyItemChanged(0);

            mComment.commentCount = header.commentCount;
        }
        if (mAdapter.getItemCount() > 1) {
            mRecyclerView.scrollToPosition(1);
        }
    }

    private void loadOwnComment(int id)
    {
        String url = ServerAPI.User.buildCommentInfo(id);
        if(mNowCommentLoader == null)
        {
            mNowCommentLoader = new UrlListLoader<CommentList>(requestTag, CommentList.class);
            mNowCommentLoader.setUrl(url);
        }

        mNowCommentLoader.loadMoreQHData(new OwnCommentListener(), DataLoader.MODE_LOAD_MORE);
    }

    private class OwnCommentListener implements DataLoaderCallback<CommentList>
    {
        @Override
        public void onLoadFinished(CommentList data, int mode) {
            Log.e("haha", data.results.size());
            if(data != null && data.results != null && data.results.size() > 0)
            {
                mComment = data.results.get(0);
            }
        }

        @Override
        public void onLoadError(int mode) {

        }
    }
    private void loadComments(int mode) {
        String  url  = ServerAPI.User.buildCommentCommentUrl(mComment.id);


        if (mCommentLoader == null) {
            mCommentLoader = new UrlListLoader<CommentList>(requestTag, CommentList.class);
            mCommentLoader.setUrl(url);
//            mCommentLoader.setUrl(ServerAPI.Comments.buildUrl(this, mComment.id,ServerAPI.Comments.Type.COMMENT));
        }

        mCommentLoader.loadMoreQHData(this, mode);
    }

    @Override
    public void finish() {
        Intent data = new Intent();


        data.putExtra(Const.EXTRA_COMMENT_DATA, mComment);
        setResult(RESULT_OK, data);

        super.finish();
    }
    private ArrayList<Comment> messageList = new ArrayList();
    @Override
    public void onLoadFinished(CommentList data, int mode) {
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        mContainerView.setVisibility(View.VISIBLE);
        if (data == null || data.list == null
                || (mCommentLoader != null && !mCommentLoader.getPaginator().hasMorePages())) {
            // Disable PULL_FROM_END
            ToastUtils.show(this,"已经没有数据了");
//            mPullToRefreshView.setMode(Mode.DISABLED);
        }
        List<Comment> list = null;
        if (data != null && data.results != null && data.results.size() != 0){
            list = data.results;
        }
        if (list == null || list.isEmpty()) {
            if (messageList.isEmpty()) {
                if (mode == DataLoader.MODE_LOAD_MORE){
                    ToastUtils.AvoidRepeatToastShow(CommentDetailsActivity.this, R.string.msg_no_more_data, Toast.LENGTH_SHORT);
                }
               // mEmptyHintView.setVisibility(View.VISIBLE);
            }else {
                ToastUtils.AvoidRepeatToastShow(CommentDetailsActivity.this, R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        }else {
            mEmptyHintView.setVisibility(View.GONE);
            mPullToRefreshView.setVisibility(View.VISIBLE);
            messageList = (ArrayList<Comment>) list;
            if (mode == DataLoader.MODE_REFRESH) {
                mAdapter.setDataItems(data.results);
                return;
            }

            mAdapter.appendDataItems(data.results);
        }
//        if (data != null) {
//            mAdapter.appendDataItems(data.results);
//        }

    }

    @Override
    public void onLoadError(int mode) {
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        mEmptyHintView.setVisibility(View.VISIBLE);
        Log.d("Error loadComments");
    }


    @Override
    public void onSuccess(String result) {
        Gson mGson = new Gson();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(String.class, new StringConverter());
        mGson = gb.create();
        Comment comment =  mGson.fromJson(result.toString(), Comment.class);

        commentPostSuccess(comment);
    }
    @Override
    public void onFailed() {
        ToastUtils.show(getApplicationContext(),"发送评论失败");
        mPostButton.setEnabled(true);
    }

}
