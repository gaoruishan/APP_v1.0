package com.cmcc.hyapps.andyou.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.FriendsCircleAdapter;
import com.cmcc.hyapps.andyou.adapter.FriendsCircleDescriptionAdapter;
import com.cmcc.hyapps.andyou.adapter.ImageAvatorGalleryAdapter;
import com.cmcc.hyapps.andyou.adapter.ImageGalleryAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.CompoundImage;
import com.cmcc.hyapps.andyou.model.FriendsCircleVote;
import com.cmcc.hyapps.andyou.model.QHDelete;
import com.cmcc.hyapps.andyou.model.QHFriendInfo;
import com.cmcc.hyapps.andyou.model.QHFriendsComment;
import com.cmcc.hyapps.andyou.model.QHFriendsUser;
import com.cmcc.hyapps.andyou.model.QHPublicInfoDetail;
import com.cmcc.hyapps.andyou.support.ExEditText;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.AESEncrpt;
import com.cmcc.hyapps.andyou.util.AnimUtils;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ImageHelper;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bingbing on 2015/10/23.
 */
public class FriendsCircleDescriptionActivity extends BaseActivity implements View.OnClickListener, DataLoader.DataLoaderCallback<QHFriendsComment.QHFriendsCommentList>, UploadTask.UploadCallBack, FriendsCircleDescriptionAdapter.OnItemSubLongClickListener {
    private Button sendButton;
    private ExEditText mEditText;
    private View sendView;
    private PullToRefreshRecyclerView mPullToRefreshRecyclerView;
    private View mainView;
    private UrlListLoader<QHFriendsComment.QHFriendsCommentList> mListLoader;
    private FriendsCircleDescriptionAdapter mFriendsCircleDescriptionAdapter;
    private RecyclerView recyclerView;
    private View reload_view, empty_view;
    private View mLoadingProgress;
    //用来判断返回上个界面的时候，是否去刷新点赞数量
    private boolean isVote = false;
    //用来判断返回上个界面的时候，是否去刷新评论数量
    private boolean isComment = false;
    private int id;
    private int position;
    private String user_id;

    private QHFriendsUser currentReplyUser;
    private QHFriendsComment currentComment;
    private QHPublicInfoDetail mQHPublicInfoDetail;
    private int comment_count = 0;
    private boolean isHasHead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainView = LayoutInflater.from(this).inflate(R.layout.activity_friends_circle_description, null);
        setContentView(mainView);
        id = getIntent().getIntExtra("info_ID", 0);
        position = getIntent().getIntExtra("position", 0);
        isHasHead = getIntent().getBooleanExtra("isHasHead", false);
        initActionbar();
        initPullToRefresh(mainView);
        initRecycleView(mainView);
        initView();
        loadTrendsInformation();
    }

    private void reload() {
        mLoadingProgress.setVisibility(View.VISIBLE);
        mPullToRefreshRecyclerView.setVisibility(View.INVISIBLE);
        empty_view.setVisibility(View.GONE);
        reload_view.setVisibility(View.GONE);
        mFriendsCircleDescriptionAdapter.setHeader(null);
        mFriendsCircleDescriptionAdapter.setDataItems(null);
        loadTrendsInformation();
    }

    private String mText;

    private void initView() {
        initPullToRefresh(mainView);
        sendButton = (Button) mainView.findViewById(R.id.friends_circle_description_send_argument);
        sendButton.setOnClickListener(this);
        sendView = mainView.findViewById(R.id.friends_circle_description_argument_layout);
        mEditText = (ExEditText) mainView.findViewById(R.id.friends_circle_description_argument_content);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mText = s.toString().trim();
                if (mText.length() >= 150) {
                    ToastUtils.show(FriendsCircleDescriptionActivity.this, R.string.count_limited);
                    mEditText.setText(mText.substring(0, 149));
                    mEditText.setSelection(mEditText.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        empty_view = this.findViewById(R.id.empty_hint_view);
        mLoadingProgress = this.findViewById(R.id.loading_progress);
        reload_view = this.findViewById(R.id.reload_view);
        reload_view.setOnClickListener(this);

    }

    private void initPullToRefresh(View view) {
        mPullToRefreshRecyclerView = (PullToRefreshRecyclerView) view.findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPullToRefreshRecyclerView.setVisibility(View.INVISIBLE);
        mPullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
//                if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
//                    reload(DataLoader.MODE_REFRESH);
//                } else {
                mListLoader.loadMoreQHDataAES(FriendsCircleDescriptionActivity.this, DataLoader.MODE_LOAD_MORE);
//                }
            }
        });
    }

    private void initRecycleView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplication());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerView);
        itemClickSupport.setOnItemSubViewClickListener(new ItemClickSupport.OnItemSubViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    onHeaderClicked(view);
                } else {
                    onItemClicked(view);
                }
            }
        });

//        int scape = ScreenUtils.dpToPxInt(this, 1);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(scape);
//        dividerItemDecoration.initWithRecyclerView(recyclerView);
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mFriendsCircleDescriptionAdapter = new FriendsCircleDescriptionAdapter(this);
        mFriendsCircleDescriptionAdapter.setOnItemSubLongClickListener(this);
        recyclerView.setAdapter(mFriendsCircleDescriptionAdapter);
    }

    private void onHeaderClicked(View view) {
        switch (view.getId()) {
            case R.id.friends_circle_recycle_item_avator:
                Intent intent = new Intent(FriendsCircleDescriptionActivity.this, UserInformationActivity.class);
                intent.putExtra("user_ID", user_id);
                startActivity(intent);
                break;
            case R.id.friends_circle_description_header_vote_textview:
                if (!view.isSelected()) {
                    AnimUtils.doScaleFadeAnim(view);
                    voteTrends();
                } else
                    ToastUtils.show(this, "已经点过赞了");
                break;
            case R.id.friends_circle_recycle_item_delete:
                if (mQHPublicInfoDetail != null)
                    showDialog(mQHPublicInfoDetail);
                break;
        }
    }

    private void showDialog(final QHPublicInfoDetail qhPublicInfoDetail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_item);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                deleteTrends(qhPublicInfoDetail);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void deleteTrends(final QHPublicInfoDetail qhPublicInfoDetail) {
        String delete_url = ServerAPI.ADDRESS + "friends/circle/deleteInformation.do";
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("infoId", qhPublicInfoDetail.getInfoId());
        String body = getRequestParams(maps,delete_url);
        RequestManager.getInstance().sendGsonRequestAESforPOST(delete_url, QHDelete.class, body, new Response.Listener<QHDelete>() {
            @Override
            public void onResponse(QHDelete response) {
                ToastUtils.show(FriendsCircleDescriptionActivity.this, "删除成功");
                Intent result = new Intent();
                //用来判断返回上个界面的时候，是否去删除本动态
                result.putExtra("isDelete", true);
                result.putExtra("infoId", id);
                result.putExtra("isHasHead", isHasHead);
                result.putExtra("position", position);
                setResult(RESULT_OK, result);
                FriendsCircleDescriptionActivity.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(FriendsCircleDescriptionActivity.this, "删除失败");
            }
        }, "deleteTrends", AppUtils.dynamicKey);
    }

    private void onItemClicked(View view) {
        QHFriendsComment qhFriendsComment = (QHFriendsComment) view.getTag();
        switch (view.getId()) {
            case R.id.friends_circle_recycle_item_avator:
                if (qhFriendsComment != null) {
                    Intent intent = new Intent(FriendsCircleDescriptionActivity.this, UserInformationActivity.class);
                    intent.putExtra("user_ID", qhFriendsComment.getFromUser().getUserId());
                    startActivity(intent);
                }
                break;
            case R.id.friends_circle_recycle_item_main_layout:
                if (qhFriendsComment != null) {
                    currentComment = null;
                    currentReplyUser = qhFriendsComment.getFromUser();
                }
                if (Integer.parseInt(currentReplyUser.getUserId()) != AppUtils.getQHUser(this).id) {
                    if (currentReplyUser != null) {
                        currentComment = qhFriendsComment;
                        mEditText.setHint("回复:" + qhFriendsComment.getFromUser().getNickname());
                    }
                }
                break;
        }
    }

    private void initActionbar() {
        ActionBar actionBar = (ActionBar) mainView.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.me_item_trends_information);
        actionBar.setBackgroundResource(R.color.title_bg);
        //actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_left:
                Intent result = new Intent();
                result.putExtra("isVote", isVote);
                result.putExtra("position", position);
                result.putExtra("isHasHead", isHasHead);
                result.putExtra("comment_count", comment_count);
                result.putExtra("isComment", isComment);
                setResult(RESULT_OK, result);
                finish();
                break;
            case R.id.reload_view:
                reload();
                break;
            case R.id.friends_circle_description_send_argument:
                if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
                    ToastUtils.show(FriendsCircleDescriptionActivity.this, "请输入评论内容");
                    return;
                }
                if (mEditText.getText().toString().trim().length() < 5) {
                    ToastUtils.show(FriendsCircleDescriptionActivity.this, "不得小于5个字");
                    return;
                }
                // sendTrendArgument();
                hideInput();
                postTrends();
                break;
        }
    }


    @Override
    public void onLoadFinished(QHFriendsComment.QHFriendsCommentList qhFriendsCommentList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        reload_view.setVisibility(View.GONE);
        mPullToRefreshRecyclerView.onRefreshComplete();
        List<QHFriendsComment> list = null;
        if (qhFriendsCommentList != null && qhFriendsCommentList.results != null && qhFriendsCommentList.results.size() != 0) {
            list = qhFriendsCommentList.results;
        }
        if (list == null || list.isEmpty()) {
            if (mode == DataLoader.MODE_REFRESH) {
                mPullToRefreshRecyclerView.setVisibility(View.VISIBLE);
                //   empty_view.setVisibility(View.VISIBLE);
            } else {
                ToastUtils.AvoidRepeatToastShow(this, R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        } else {
            empty_view.setVisibility(View.GONE);
            mPullToRefreshRecyclerView.setVisibility(View.VISIBLE);

            if (mode == DataLoader.MODE_REFRESH) {
                mFriendsCircleDescriptionAdapter.setDataItems(qhFriendsCommentList.results);
                return;
            }

            mFriendsCircleDescriptionAdapter.appendDataItems(qhFriendsCommentList.results);
        }
    }

    @Override
    public void onLoadError(int mode) {
        mPullToRefreshRecyclerView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshRecyclerView.setVisibility(View.INVISIBLE);
        reload_view.setVisibility(View.VISIBLE);
        sendView.setVisibility(View.GONE);
    }

    /**
     * 获取动态详情
     */
    private void loadTrendsInformation() {
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("infoId",id);
//        String url = ServerAPI.ADDRESS + "friends/getInfoDetails.do?infoId=" + id;
        String url = ServerAPI.ADDRESS + "friends/circle/getInfoDetails.do";
        String body  = getRequestParams(maps,url);
        RequestManager.getInstance().sendGsonRequestAESforPOST(url, QHPublicInfoDetail.class, body, new Response.Listener<QHPublicInfoDetail>() {
            @Override
            public void onResponse(QHPublicInfoDetail response) {
                user_id = response.getPublishUser().getUserId();
                comment_count = response.getCommentNum();
                mQHPublicInfoDetail = response;
                mFriendsCircleDescriptionAdapter.setDataItems(null);
                mFriendsCircleDescriptionAdapter.setHeader(response);
                mFriendsCircleDescriptionAdapter.notifyDataSetChanged();
                sendView.setVisibility(View.VISIBLE);
                loadTrendsComment();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLoadingProgress.setVisibility(View.GONE);
                reload_view.setVisibility(View.VISIBLE);
                mPullToRefreshRecyclerView.onRefreshComplete();
                sendView.setVisibility(View.GONE);
            }
        }, "LoadTrendsInformation",AppUtils.dynamicKey);
    }

    /**
     *
     */
    private void refreshCommentCount() {
        String url = ServerAPI.ADDRESS + "friends/circle/getInfoDetails.do";
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("infoId", id);
        String body = getRequestParams(maps,url);
        RequestManager.getInstance().sendGsonRequestAESforPOST(url, QHPublicInfoDetail.class, body, new Response.Listener<QHPublicInfoDetail>() {
            @Override
            public void onResponse(QHPublicInfoDetail response) {
                user_id = response.getPublishUser().getUserId();
                comment_count = response.getCommentNum();
                mQHPublicInfoDetail = response;
                mFriendsCircleDescriptionAdapter.setHeader(mQHPublicInfoDetail);
                mFriendsCircleDescriptionAdapter.notifyDataSetChanged();
                sendView.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mFriendsCircleDescriptionAdapter.notifyDataSetChanged();
            }
        }, "LoadTrendsInformation", AppUtils.dynamicKey);
    }

    /**
     * 获取动态详情页的评论
     */
    private void loadTrendsComment() {
        mListLoader = new UrlListLoader<QHFriendsComment.QHFriendsCommentList>("FriendsCircleDescriptionActivity", QHFriendsComment.QHFriendsCommentList.class);
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("infoId",id);
        String data = "";
        try {
            data = AESEncrpt.Encrypt(new Gson().toJson(maps),AppUtils.dynamicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mListLoader.setUrl(ServerAPI.ADDRESS + "friends/circle/getInfoComments.do?data=" + data);
        mListLoader.loadMoreQHDataAES(this, DataLoader.MODE_REFRESH);
    }

    /**
     * 对动态点赞
     */
    private void voteTrends() {
//        String vote_url = ServerAPI.ADDRESS + "friends/praise.do?infoId=" + id;
        String vote_url = ServerAPI.ADDRESS + "friends/circle/praise.do";
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("infoId", id);
        String body = getRequestParams(maps,vote_url);
        RequestManager.getInstance().sendGsonRequestAESforPOST(vote_url, FriendsCircleVote.class, body, new Response.Listener<FriendsCircleVote>() {
            @Override
            public void onResponse(FriendsCircleVote response) {
                if (response.isSuccessful()) {
                    //执行点赞成功后的逻辑
//                    handleVoteSuccess();
                    ToastUtils.show(activity.getBaseContext(), "点赞成功");
                    isVote = true;
                    refreshVoteState();
                } else {
                    ToastUtils.show(activity.getBaseContext(), "点赞失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(activity.getBaseContext(), "点赞失败");
            }
        }, "loadTrendsComment",AppUtils.dynamicKey);
    }

    /**
     * 对动态发表评论
     */
    private void sendTrendArgument() {
        //   String url = ServerAPI.ADDRESS + "friends/comment.do?infoId=" + id +"&commentId=0&" + "commentText=ggggg";
        String url = ServerAPI.ADDRESS + "friends/comment.do";
        Map<String, String> params = new HashMap<String, String>();
        params.put("infoId", id + "");
        params.put("commentId", 0 + "");
        params.put("commentText", mEditText.getText().toString().trim());
        RequestManager.getInstance().sendGsonRequest(Request.Method.POST, url, FriendsCircleVote.class, null, new Response.Listener<FriendsCircleVote>() {
            @Override
            public void onResponse(FriendsCircleVote response) {
                ToastUtils.show(activity.getBaseContext(), "评论成功");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(activity.getBaseContext(), "评论失败");
            }
        }, false, params, "sendTrendArgument");
    }

    private void postTrends() {
        int commentId = 0;
        if (currentComment != null) {
            commentId = currentComment.getCommentId();
        }
        new UploadTask(this, id, mEditText.getText().toString(), commentId, 5, this).execute("", "");
    }

    @Override
    public void onSuccess(String result) {

        ToastUtils.show(this, "评论成功");
        mEditText.setText("");
        mEditText.setHint("");
        currentComment = null;
        isComment = true;
        reloadComment();
        refreshCommentCount();
    }

    @Override
    public void onFailed() {
        ToastUtils.show(this, "评论失败");
    }

    private void reloadComment() {
        if (mFriendsCircleDescriptionAdapter != null)
            mFriendsCircleDescriptionAdapter.setDataItems(null);
        reload_view.setVisibility(View.INVISIBLE);
        empty_view.setVisibility(View.INVISIBLE);
        loadTrendsComment();
    }

    /**
     * 删除评论
     */
    private void deleteComment(final int position, int id) {
        String delete_url = ServerAPI.ADDRESS + "friends/circle/deleteComment.do";
//        String delete_url = ServerAPI.ADDRESS + "friends/deleteComment.do?commentId=" + id;
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("commentId", id);
        String body = getRequestParams(maps,delete_url);
        RequestManager.getInstance().sendGsonRequestAESforPOST(delete_url, QHDelete.class, body, new Response.Listener<QHDelete>() {
            @Override
            public void onResponse(QHDelete response) {
                ToastUtils.show(FriendsCircleDescriptionActivity.this, "删除成功");
                //  mFriendsCircleDescriptionAdapter.getDataItems().remove(position);
                isComment = true;
                reloadComment();
                refreshCommentCount();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(FriendsCircleDescriptionActivity.this, "删除失败");
            }
        },"deleteTrends", AppUtils.dynamicKey);
    }


    private void refreshVoteState() {
        if (mFriendsCircleDescriptionAdapter != null) {
            FriendsCircleDescriptionAdapter.FriendsCircleDescriptionHeader header = mFriendsCircleDescriptionAdapter.getHeader();
            header.getVote().setSelected(true);
            header.getVote().setText(mQHPublicInfoDetail.getPraiseNum() + 1 + "");
            header.getVote().setEnabled(false);
            List<CompoundImage.TextImage> imageList = new ArrayList<CompoundImage.TextImage>();
            CompoundImage my_comImg = new CompoundImage(AppUtils.getQHUser(this).user_info.avatar_url, AppUtils.getQHUser(this).user_info.avatar_url);
            CompoundImage.TextImage my_textImage = new CompoundImage.TextImage(my_comImg, null, null);
            imageList.add(my_textImage);
            if (mQHPublicInfoDetail.getPraiseUsers() != null) {
                if (mQHPublicInfoDetail.getPraiseUsers().size() < 5) {
                    for (int i = 0; i < mQHPublicInfoDetail.getPraiseUsers().size(); i++) {
                        addImageList(i, imageList);
                    }
                } else {
                    for (int i = 0; i < mQHPublicInfoDetail.getPraiseUsers().size() - 1; i++) {
                        addImageList(i, imageList);
                    }
                }
                ImageAvatorGalleryAdapter adapter = (ImageAvatorGalleryAdapter) header.getAvatorRecycleView().getAdapter();
                adapter.setDataItems(imageList);
                QHFriendsUser me = new QHFriendsUser();
                me.setUserId(AppUtils.getQHUser(this).id +"");
                mFriendsCircleDescriptionAdapter.getBaseHeader().getPraiseUsers().add(0,me);
//                mFriendsCircleDescriptionAdapter.notifyItemChanged(0);
            }
        }
    }

    private void addImageList(int position, List<CompoundImage.TextImage> imageList) {
        QHFriendsUser qhFriendsUser = mQHPublicInfoDetail.getPraiseUsers().get(position);
        CompoundImage comImg = new CompoundImage(qhFriendsUser.getAvatarUrl(), qhFriendsUser.getAvatarUrl());
        CompoundImage.TextImage textImage = new CompoundImage.TextImage(comImg, null, null);
        imageList.add(textImage);
    }

    /**
     * 隐藏键盘
     */
    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent result = new Intent();
            result.putExtra("isVote", isVote);
            result.putExtra("position", position);
            result.putExtra("isHasHead", isHasHead);
            result.putExtra("comment_count", comment_count);
            result.putExtra("isComment", isComment);
            setResult(RESULT_OK, result);
            finish();
        }
        return false;
    }


    @Override
    public void onItemLongClick(int position, int id) {
        deleteComment(position, id);
    }
}
