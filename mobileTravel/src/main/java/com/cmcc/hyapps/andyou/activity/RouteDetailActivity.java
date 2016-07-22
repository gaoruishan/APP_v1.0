/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.QHRouteDetailAdapter;
import com.cmcc.hyapps.andyou.adapter.RouteDetailAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.data.GsonRequest;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.ResponseError;
import com.cmcc.hyapps.andyou.model.QHCollectionStrategy;
import com.cmcc.hyapps.andyou.model.QHComment;
import com.cmcc.hyapps.andyou.model.QHEnjoy;
import com.cmcc.hyapps.andyou.model.QHEnjoyInfo;
import com.cmcc.hyapps.andyou.model.QHRoute;
import com.cmcc.hyapps.andyou.model.QHRouteInfo;
import com.cmcc.hyapps.andyou.util.AnimUtils;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.SharePopupWindows;
import com.google.gson.Gson;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sso.UMSsoHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author kuloud
 */
public class RouteDetailActivity extends BaseActivity implements View.OnClickListener,SharePopupWindows.OnSharePopupWindowsBack {
    private Context mContext;
    protected String mRequestTag = RouteDetailActivity.class.getName();
    private QHRoute routedetail;
    @InjectView(R.id.route_recyclerview)
    public RecyclerView mRecyclerView;
    private final int REQUEST_CODE_COMMENT_LIST = 1;
    @InjectView(R.id.tv_comment)
    public TextView tv_comment;
    @InjectView(R.id.tv_praise)
    public TextView tv_praise;
    ActionBar actionBar;

    private LinearLayoutManager mLayoutManager;
    private RouteDetailAdapter mAdapter;
    private QHRouteDetailAdapter qhRouteDetailAdapter;
    private int limit = 10;
    private String url;
    private QHComment.QHCommentList mCommentList;
    private View mLoadingProgress;
    private int route_detail_id;
    private Boolean tag;
    private SharePopupWindows sharePopupWindows;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_restaurant_detail);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        tag = intent.getBooleanExtra("TAG", false);
        Bundle route =  intent.getBundleExtra("route");
        routedetail = route.getParcelable("route");
        route_detail_id = route.getInt("id");

        initView();
        sharePopupWindows = new SharePopupWindows(this);
        sharePopupWindows.setOnSharePopupWindowsBack(this);
        ShareManager.getInstance().onStart(this);
        ShareManager.getInstance().setOnShareManagerBackListener(new ShareManager.OnShareManagerBackListener() {
            @Override
            public void shareSuccess(SHARE_MEDIA share_media) {
                if (sharePopupWindows != null && sharePopupWindows.isShowing())
                    sharePopupWindows.dismiss();
//                if (share_media == SHARE_MEDIA.SINA || share_media == SHARE_MEDIA.WEIXIN || share_media == SHARE_MEDIA.WEIXIN_CIRCLE)
                    ToastUtils.show(RouteDetailActivity.this, "分享成功");
            }

            @Override
            public void shareFaild() {
//                ToastUtils.show(RouteDetailActivity.this,"分享失败");
            }
        });
       // initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(RouteDetailActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        //mRecyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        String url = ServerAPI.Route.buildItemDetailUrl(route_detail_id + "");
        RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url,
                QHRoute.class, null,
                new Response.Listener<QHRoute>() {
                    @Override
                    public void onResponse(QHRoute response) {

                        routedetail = response;
                        qhRouteDetailAdapter = new QHRouteDetailAdapter(RouteDetailActivity.this, routedetail.route_info);
                        if (routedetail.route_info != null && routedetail.route_info.size() != 0) {
                            mRecyclerView.setAdapter(qhRouteDetailAdapter);
                        }
                        if (!TextUtils.isEmpty(routedetail.intro_text)) {
                            qhRouteDetailAdapter.setHeader(routedetail);
                        }
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLoadingProgress.setVisibility(View.INVISIBLE);
                        tv_comment.setOnClickListener(RouteDetailActivity.this);
                        tv_praise.setOnClickListener(RouteDetailActivity.this);
                        ItemClickSupport clickSupport = ItemClickSupport.addTo(mRecyclerView);
                        clickSupport.setOnItemSubViewClickListener(new ItemClickSupport.OnItemSubViewClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {
                                if (position == 0) {
                                    onHeaderClicked(view);
                                } else {
                                    //   onItemClicked(view);
                                }
                            }
                        });
                        getCommentList();
                        if (routedetail.voted == 1) {
                            tv_praise.getCompoundDrawables()[1].setLevel(2);
                        }
                        if (routedetail.comment_count > 10)
                            limit = routedetail.comment_count;

                        tv_praise.setText("" + routedetail.vote_count);
                        tv_comment.setText("" + routedetail.comment_count);
                        if (routedetail.comment_count > 10) limit = routedetail.comment_count;
                        initActionBar();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        /*if (mScenicDetailsModel == null) {
                            showReloadView();
                        }
                        if (mBannerRequest != null) {
                            mBannerRequest.markDelivered();
                        }*/
                    }
                }, false, requestTag);
    }

    private void initActionBar() {
        actionBar= (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(routedetail.title);
        actionBar.getTitleView().setTextColor(Color.WHITE);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
        if (tag){
            actionBar.getRightView().setVisibility(View.GONE);
        }else {
            actionBar.getRightView().setImageResource(R.drawable.collection_seclector);
            actionBar.getRightView().setSelected(false);
            actionBar.getRightView().setOnClickListener(this);
            actionBar.getRight2View().setImageResource(R.drawable.fresh_share);
            actionBar.getRight2View().setOnClickListener(this);
        }

        if(routedetail.collected != 0){
            actionBar.getRightView().setSelected(true);
        }
    }


    public void getCommentList() {
        url = ServerAPI.BASE_URL + "routes/"+routedetail.id+"/comments/?limit="+limit+"/";
        RequestManager.getInstance().sendGsonRequest(url, QHComment.QHCommentList.class,
                new Response.Listener<QHComment.QHCommentList>() {
                    @Override
                    public void onResponse(QHComment.QHCommentList response) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        android.util.Log.i("law respond", response.toString());
                        android.util.Log.i("law", response.toString());
                        mCommentList = response;
                        updateComment(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        android.util.Log.i("law error", error.toString());
                        android.util.Log.i("law", error.toString());
                    }
                }, requestTag);
    }

    private void updateComment(QHComment.QHCommentList response) {
        tv_comment.setText("" + response.count);
//        tv_comment.setText(""+response.results.size());

    }
    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left: {
                finish();
                break;
            }
            case R.id.tv_comment:
                Intent intent = new Intent(RouteDetailActivity.this,CommentListActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("object_id",routedetail.id);
                intent.putExtra("ctype", Integer.parseInt(ServerAPI.Comments.Type.ROUTE.value()));
                startActivityForResult(intent, REQUEST_CODE_COMMENT_LIST);
                break;
            case R.id.tv_praise:
                AnimUtils.doScaleFadeAnim(v);
                if (!UserManager.makeSureLogin(this, 1)) {
                    if (routedetail.voted == 1) {
                        tv_praise.getCompoundDrawables()[1].setLevel(2);
                        tv_praise.setEnabled(false);
                        ToastUtils.show(getApplicationContext(), "您已经点赞过，不能再次点赞！");
                    }
                    {
                        voteComment();
                    }
                }
                break;
            case R.id.action_bar_right: {
                if (!UserManager.makeSureLogin(RouteDetailActivity.this, REQUEST_CODE_LOGIN_NEW_COMMENT)) {
                    if (routedetail.collected == 0)
                    postCollection();
                    else
                        cancleCollection();
                }
                break;
            }
            case R.id.action_bar_right2:
                if (sharePopupWindows != null && !sharePopupWindows.isShowing())
                    sharePopupWindows.showAtLocation(actionBar, Gravity.CENTER,0,0);
                break;

        }
    }

    private void initView() {
        mLoadingProgress =  findViewById(R.id.loading_progress);
    }


   /* 收藏路线*/
    private void postCollection() {
        mLoadingProgress.setVisibility(View.VISIBLE);

        if (routedetail == null || tv_praise == null) {
            Log.e("[voteComment] NULL comment:" + routedetail);
            return;
        }

        CommentObj obj = new CommentObj(""+routedetail.id,ServerAPI.Comments.Type.ROUTE.value());
        Gson gson = new Gson();
        String jsonBody = gson.toJson(obj);
        RequestManager.getInstance().sendGsonRequest(Request.Method.POST, ServerAPI.Comments.GUIDER_COLLECT_URL,
                jsonBody,
                QHCollectionStrategy.class, new Response.Listener<QHCollectionStrategy>() {
                    @Override
                    public void onResponse(QHCollectionStrategy response) {
                        routedetail.collected = response.id;
                        mLoadingProgress.setVisibility(View.GONE);
                        actionBar.getRightView().setSelected(true);
                     //   actionBar.getRightView().setClickable(false);
                        ToastUtils.show(activity.getBaseContext(),R.string.collection_success);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        mLoadingProgress.setVisibility(View.GONE);

                        if (error instanceof ResponseError
                                && ((ResponseError) error).errCode == ServerAPI.ErrorCode.ERROR_ALREADY_VOTED) {
                            ToastUtils.show(activity.getBaseContext(), R.string.collection_is_success);
                        }
//                        else if(error instanceof ServerError) {
//                          //  Toast.makeText(getApplicationContext(), R.string.error_already_voted,  Toast.LENGTH_SHORT).show();
//                        }
                        else{
                            AppUtils.handleResponseError(RouteDetailActivity.this, error);
                        }


                    }
                }, "--------------");
    }
    private void voteComment() {
        if (routedetail == null || tv_praise == null) {
            Log.e("[voteComment] NULL comment:" + routedetail);
            return;
        }

        CommentObj obj = new CommentObj(""+routedetail.id,ServerAPI.Comments.Type.ROUTE.value());
        Gson gson = new Gson();
        String jsonBody = gson.toJson(obj);
        RequestManager.getInstance().sendGsonRequest(Request.Method.POST, ServerAPI.Comments.GUIDER_VOTE_URL,
                jsonBody,
                QHCollectionStrategy.class, new Response.Listener<QHCollectionStrategy>() {
                    @Override
                    public void onResponse(QHCollectionStrategy response) {
                        tv_praise.setText(String.valueOf(response.vote_count));
//                        tv_praise.getCompoundDrawables()[1].setLevel(2);
                        routedetail.voted = 1;
                        routedetail.vote_count = response.vote_count;
                        tv_praise.getCompoundDrawables()[1].setLevel(2);
                        tv_praise.setEnabled(false);
                        tv_praise.setClickable(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof ResponseError
                                && ((ResponseError) error).errCode == ServerAPI.ErrorCode.ERROR_ALREADY_VOTED) {
                            Toast.makeText(getApplicationContext(), R.string.error_already_voted,  Toast.LENGTH_SHORT).show();
                        }
//                        else {
//                            AppUtils.handleResponseError(RouteDetailActivity.this, error);
//                        }

                    }
                }, "--------------");
    }
    private void gotoShare(QHRoute routedetail, String type) {
        if (routedetail != null) {
                String content = routedetail.intro_text;
                if (TextUtils.isEmpty(content))
                    content = routedetail.getTitle();
                if (type.equals("qq")) {
                    ShareManager.getInstance().shareQQ(routedetail.getTitle(), content, routedetail.getCover_image(), routedetail.getShareURL());
                }
                if (type.equals("sina")) {
                    ShareManager.getInstance().shareSina(routedetail.getTitle(), content, routedetail.getCover_image(), routedetail.getShareURL());

                }
                if (type.equals("wechat")) {
                    ShareManager.getInstance().shareWeChat(routedetail.getTitle(), content, routedetail.getCover_image(), routedetail.getShareURL());

                }
                if (type.equals("wechatcircle")) {
                    ShareManager.getInstance().shareWeChatCircle(routedetail.getTitle(), content, routedetail.getCover_image(), routedetail.getShareURL());
                }
        }
    }
    @Override
    public void onQQShareBack() {
        gotoShare(routedetail,"qq");
    }

    @Override
    public void onSinaShareBack() {
        gotoShare(routedetail,"sina");
    }

    @Override
    public void onWeChatShareBack() {
        gotoShare(routedetail,"wechat");
    }

    @Override
    public void onWeChatCircleShareBack() {
        gotoShare(routedetail,"wechatcircle");
    }

    public class CommentObj  {
        public  String obj_id;
        public  String obj_type;
        CommentObj (String id,String type){
            obj_id = id;
            obj_type = type;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CommentListActivity.RESULT_CODE_NEW_COMMENT) {
            int newCommentCount = data.getIntExtra("count", 0);
            newCommentCount += Integer.parseInt(tv_comment.getText().toString());
            tv_comment.setText(String.valueOf(newCommentCount));
        }
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = ShareManager.getInstance().getController().getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
    private void onHeaderClicked(View v) {
     //   ToastUtils.show(RouteDetailActivity.this,"dfgdfgdgdgdfg");
    }
    GsonRequest<QHCollectionStrategy> cancleGsonRequest = null;
    /* 取消收藏路线*/
    private void cancleCollection() {
        mLoadingProgress.setVisibility(View.VISIBLE);

        if (routedetail == null || tv_praise == null) {
            Log.e("[voteComment] NULL comment:" + routedetail);
            return;
        }

        CommentObj obj = new CommentObj(""+routedetail.id,ServerAPI.Comments.Type.ROUTE.value());
        Gson gson = new Gson();
        String jsonBody = gson.toJson(obj);
         cancleGsonRequest = RequestManager.getInstance().sendGsonRequest(Request.Method.POST, ServerAPI.Comments.GUIDER_COLLECT_URL+routedetail.collected +"/",
                jsonBody,
                QHCollectionStrategy.class, new Response.Listener<QHCollectionStrategy>() {
                    @Override
                    public void onResponse(QHCollectionStrategy response) {
                        mLoadingProgress.setVisibility(View.GONE);
                        ToastUtils.show(activity.getBaseContext(), R.string.cancle_collection_fail);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (cancleGsonRequest != null && cancleGsonRequest.getStateCode() == 204){
                            routedetail.collected = 0;
                            mLoadingProgress.setVisibility(View.GONE);
                            actionBar.getRightView().setSelected(false);
                            ToastUtils.show(activity.getBaseContext(),R.string.cancle_collection_success);
                        }else {
                            mLoadingProgress.setVisibility(View.GONE);
                            ToastUtils.show(activity.getBaseContext(), R.string.cancle_collection_fail);
                        }
                    }
                }, "--------------");
    }
}
