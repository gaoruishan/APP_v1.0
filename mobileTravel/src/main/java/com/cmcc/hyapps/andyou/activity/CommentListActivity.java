/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.CommentAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.Comments.Type;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.StringConverter;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.Comment.NewCommentResult;
import com.cmcc.hyapps.andyou.model.QHComment;
import com.cmcc.hyapps.andyou.model.QHComment.QHCommentList;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.model.User;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.LocationUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kuloud
 */
public class CommentListActivity extends BaseActivity implements OnClickListener  ,DataLoader.DataLoaderCallback<QHComment.QHCommentList> ,  UploadTask.UploadCallBack{
    private final int REQUEST_CODE_LOGIN_NEW_COMMENT = 1;
    protected static final int RESULT_CODE_NEW_COMMENT = 2;
    private RecyclerView mRecyclerView;
    private View mEmptyHintView;
    private View mLoadingProgress;
    private View mContainerView;
    private View mPostButton;
    private EditText mNewCommentEditText;
    private CommentAdapter mAdapter;
    private int object_id = -1;
    private int ctype = -1;
    private int mNewCommentCount;
    private Type mType = Type.SCENIC;
    private QHStrategy guide_detail1;
    private static final int GET_COMMENTS_PARAM_LIMIT = 10;
    private ArrayList<QHComment> commentArrayList = new ArrayList<QHComment>();
    private UrlListLoader<QHCommentList> commentListLoader;
    private PullToRefreshRecyclerView mPullToRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_details);
        object_id = getIntent().getIntExtra("object_id", 0);
        ctype = getIntent().getIntExtra("ctype", 0);
        guide_detail1 = getIntent().getParcelableExtra("guide");
        initViews();

        initPullToRefresh();
        getCommentList(DataLoader.MODE_REFRESH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LOGIN_NEW_COMMENT:
                    postComment(mNewCommentEditText.getText().toString());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void finish() {
        if (mNewCommentCount > 0) {
            Intent i = new Intent();
            i.putExtra("count", mNewCommentCount);
            setResult(RESULT_CODE_NEW_COMMENT, i);
        }
        super.finish();
    }

    private void initViews() {
        initActionBar();
        initListView();
        mPostButton = findViewById(R.id.comment_post_button);
        mPostButton.setOnClickListener(this);
        mNewCommentEditText = (EditText) findViewById(R.id.new_commnet_text);
    }

    private void initPullToRefresh() {
       mLoadingProgress = findViewById(R.id.loading_progress);
       mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
       mPullToRefreshView = (PullToRefreshRecyclerView) findViewById(R.id.pulltorefresh_recyclerview);
       mPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
           @Override
           public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
               if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                   getCommentList(DataLoader.MODE_REFRESH);
               } else {
                   getCommentList(DataLoader.MODE_LOAD_MORE);
               }
           }
       });
       mPullToRefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

   }
    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("评论");
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(this);
    }

    private void initListView() {
        mEmptyHintView = findViewById(R.id.empty_hint_view);
        mLoadingProgress = findViewById(R.id.loading_progress);
        mContainerView = findViewById(R.id.container);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new CommentAdapter(false);
        mAdapter.setHeader(guide_detail1);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void getCommentList(int mode) {
        if (mode != DataLoader.MODE_LOAD_MORE)
         mLoadingProgress.setVisibility(View.VISIBLE);
        String url = getIntent().getStringExtra("url");
        if (commentListLoader == null)
        {
            commentListLoader = new UrlListLoader<QHComment.QHCommentList>( requestTag, QHComment.QHCommentList.class);
            commentListLoader.setUrl(url);
            commentListLoader.setPageLimit(10);
        }
        commentListLoader.loadMoreQHData(this, DataLoader.MODE_LOAD_MORE);


//        final String url = ServerAPI.Comments.buildUrl(this, mId, mType,GET_COMMENTS_PARAM_LIMIT, 0);
//        Log.d("Loading comment list from %s", url);
       /* final String url = getIntent().getStringExtra("url");
            RequestManager.getInstance().sendGsonRequest(url, QHCommentList.class,
                new Response.Listener<QHCommentList>() {
                    @Override
                    public void onResponse(QHCommentList response) {
                        Log.d("onResponse, CommentList=%s", response);
                        mLoadingProgress.setVisibility(View.GONE);
                        mContainerView.setVisibility(View.VISIBLE);
                        if (response == null || response.results == null || response.results.isEmpty()) {
                            mEmptyHintView.setVisibility(View.VISIBLE);
                            return;
                        }
                        mEmptyHintView.setVisibility(View.GONE);
                        onCommentListLoaded(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error loading scenic details from %s", url);
                        // TODO handle it
                        mLoadingProgress.setVisibility(View.GONE);
                        mEmptyHintView.setVisibility(View.VISIBLE);
                    }
                }, requestTag);*/
    }

    @Override
    public void onLoadFinished(QHComment.QHCommentList response, int mode) {
        Log.d("onResponse, CommentList=%s", response);
        mLoadingProgress.setVisibility(View.GONE);
        mContainerView.setVisibility(View.VISIBLE);
        if (response == null || response.results == null || response.results.isEmpty()) {
            if(commentArrayList.isEmpty()) {
               mEmptyHintView.setVisibility(View.VISIBLE);
           }
            else{
                mEmptyHintView.setVisibility(View.GONE);
                ToastUtils.AvoidRepeatToastShow(CommentListActivity.this,R.string.msg_no_more_data,Toast.LENGTH_SHORT);
            }

            mLoadingProgress.setVisibility(View.GONE);
            mPullToRefreshView.onRefreshComplete();
            return;
        }
        commentArrayList.addAll(response.results);
        mEmptyHintView.setVisibility(View.GONE);
        onCommentListLoaded(response);

        mPullToRefreshView.onRefreshComplete();
    }
    @Override
    public void onLoadError(int mode) {
        // TODO handle it
        mLoadingProgress.setVisibility(View.GONE);
        mEmptyHintView.setVisibility(View.VISIBLE);
        mPullToRefreshView.onRefreshComplete();
    }

    @Override
    public void onSuccess(String result) {

        Gson mGson = new Gson();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(String.class, new StringConverter());
        mGson = gb.create();
        QHComment qhStrategy =  mGson.fromJson(result.toString(), QHComment.class);
        if (qhStrategy.object_id > 0) {
            commentPostSuccess(qhStrategy);
        }

        ToastUtils.show(getApplicationContext(), "发送评论成功！");
    }

    @Override
    public void onFailed() {
        mNewCommentEditText.setEnabled(true);
        ToastUtils.show(getApplicationContext(),"发送评论失败！");
    }

    public class LoginObj  {
        public  String object_id;
        public  String ctype;
        public  String content;
        public  String latitude;
        public  String longitude;
        public  String address;
        public  String[] comment_images = new String[0];

        LoginObj (String p,String code,String content,String latitude,String longitude,String address){
            this.object_id = p;
            this.ctype = code;
            this.content = content;
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
        }

    }
    private void postComment(String commentStr) {
        final QHComment comment = new QHComment();
        comment.content = commentStr;
        comment.object_id = object_id;
        comment.ctype = ctype;
        comment.latitude = LocationUtil.getInstance(getApplicationContext()).getLatitude();
        comment.longitude = LocationUtil.getInstance(getApplicationContext()).getLongitude();
        comment.address = "";
//        comment.allowReply = false;
//        comment.allowVote = false;

        List<File> imgFiles = new ArrayList<File>();
        String url = ServerAPI.User.buildWriteCommentUrl();
        Map<String, String> params = new HashMap<String, String>();
        Map<String, File> fileParams = new HashMap<String, File>();
        params.put("ctype",""+ctype);
        params.put("object_id",""+object_id);
        params.put("content",commentStr);
        params.put("latitude",""+LocationUtil.getInstance(getApplicationContext()).getLatitude());
        params.put("longitude",""+LocationUtil.getInstance(getApplicationContext()).getLongitude());
        params.put("address","");

        new UploadTask(getApplicationContext(), url,2,fileParams , params,this).execute("", "");

       /* LoginObj obj = new LoginObj(""+object_id,""+ctype,commentStr,""+comment.latitude,""+comment.latitude,"");
        Gson gson = new Gson();
        String jsonBody = gson.toJson(obj*//*comment*//*);
        Log.d("posting comment, mComment=%s", jsonBody);
        RequestManager.getInstance().sendGsonRequest(Method.POST,
                ServerAPI.User.buildWriteCommentUrl(),
                jsonBody,
                QHComment.class, new Response.Listener<QHComment>() {
                    @Override
                    public void onResponse(QHComment result) {
                        Log.d("New comment posted, id=: %d", result.object_id);
                        if (result.object_id > 0) {
                            comment.object_id  =  result.object_id;
                            User user = AppUtils.getUser(getApplicationContext());
                            if (user != null) {
//                                comment.setAuthor(user);
                            }
                            // TODO: This time is may different from server time
//                            comment.createTime = TimeUtils.formatTime(System.currentTimeMillis(),
//                                    TimeUtils.DATE_TIME_FORMAT);
                            commentPostSuccess(result);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error post new comment, error=%s", error);
                        mNewCommentEditText.setEnabled(true);
                        AppUtils.handleResponseError(CommentListActivity.this, error);
                    }
                }, requestTag);*/
    }

    protected void commentPostError() {
        mNewCommentEditText.setEnabled(true);
        Toast.makeText(this, R.string.comment_post_failed, Toast.LENGTH_SHORT).show();
    }

    protected void commentPostSuccess(QHComment newComment) {
        mNewCommentEditText.setEnabled(true);
        mNewCommentEditText.setText("");
        List<QHComment> list = mAdapter.getDataItems();
        list.add(newComment);
        Collections.sort(list, new Comparator<QHComment>() {

            @Override
            public int compare(QHComment lhs, QHComment rhs) {
                return (int) 1;
//                        (TimeUtils
//                        .parseTimeToMills(rhs.createTime) - TimeUtils
//                        .parseTimeToMills(lhs.createTime));
            }
        });

        mAdapter.setDataItems(list);
        if (list.size() > 0) {
            mEmptyHintView.setVisibility(View.GONE);
            commentArrayList.addAll(list);
        }
        mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);

        mNewCommentCount++;
    }

    public void onCommentListLoaded(QHCommentList data) {
        mAdapter.appendDataItems(data.results);
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
            case R.id.comment_post_button: {
                if (!TextUtils.isEmpty(mNewCommentEditText.getText().toString().trim())) {
                    if (!UserManager.makeSureLogin(CommentListActivity.this, REQUEST_CODE_LOGIN_NEW_COMMENT)) {
                        mNewCommentEditText.setEnabled(false);
                        postComment(mNewCommentEditText.getText().toString());
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

}
