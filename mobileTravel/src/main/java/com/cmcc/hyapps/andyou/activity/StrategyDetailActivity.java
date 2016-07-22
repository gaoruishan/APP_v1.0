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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.GsonRequest;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.ResponseError;
import com.cmcc.hyapps.andyou.model.QHCollectionStrategy;
import com.cmcc.hyapps.andyou.model.QHComment;
import com.cmcc.hyapps.andyou.model.QHEnjoy;
import com.cmcc.hyapps.andyou.model.QHEnjoyInfo;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.util.AnimUtils;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.SharePopupWindows;
import com.google.gson.Gson;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sso.UMSsoHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * @author kuloud
 */
public class StrategyDetailActivity extends BaseActivity implements View.OnClickListener ,DataLoader.DataLoaderCallback<QHComment.QHCommentList>,SharePopupWindows.OnSharePopupWindowsBack {
    private static final int REQUEST_CODE_PRASE = 1;
    private Context mContext;
    private LayoutInflater mInflater;
    private LinearLayoutManager mLayoutManager;
    private final int REQUEST_CODE_COMMENT_LIST = 1;
    private GuideDetailAdapter mAdapter;
    private QHStrategy guide_detail1;

    @InjectView(R.id.guide_recyclerview)
    public RecyclerView mRecyclerView;

    @InjectView(R.id.tv_comment)
    public TextView tv_comment;
    @InjectView(R.id.tv_praise)
    public TextView tv_praise;

    private boolean isPrase = false;


    private QHComment.QHCommentList mCommentList;
    private String url;
    private int limit = 10;
    private View mLoadingProgress;
    private  ActionBar actionBar;
    private int guide_detail_id;
    private Boolean tag;
    private SharePopupWindows sharePopupWindows;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mInflater = LayoutInflater.from(this);
        setContentView(R.layout.activity_special_detail);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        tag = intent.getBooleanExtra("TAG", false);
        Bundle guide=intent.getBundleExtra("guide");
      //  guide_detail1 = guide.getParcelable("guide");
        guide_detail_id = guide.getInt("id");
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
                    ToastUtils.show(StrategyDetailActivity.this, "分享成功");
            }

            @Override
            public void shareFaild() {
//                ToastUtils.show(StrategyDetailActivity.this, "分享失败");
                if (guide_detail1 != null)
                gotoDirectShare(guide_detail1,"sina");
            }
        });
      //  initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initActionBar() {
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(guide_detail1.title);
        actionBar.getTitleView().setTextColor(Color.WHITE);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView() .setImageResource(R.drawable.return_back);
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

        /*已经收藏过的攻略不能再次收藏*/
        if(AppUtils.getQHUser(activity)!=null){
            if(guide_detail1.collected !=0/*||guide_detail1.user.username.equals(AppUtils.getQHUser(activity).username)*/){
                actionBar.getRightView().setSelected(true);
               // actionBar.getRightView().setClickable(false);
            }
        }

      //  mLoadingProgress = findViewById(R.id.loading_progress);
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
                Intent intent = new Intent(StrategyDetailActivity.this,CommentListActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("object_id",guide_detail_id);
                intent.putExtra("ctype",Integer.parseInt(ServerAPI.Comments.Type.RAIDERS.value()));
                intent.putExtra("guide",guide_detail1);
                startActivityForResult(intent, REQUEST_CODE_COMMENT_LIST);
//                startActivity(intent);
                break;
            case R.id.tv_praise:
                //TODO 点赞登陆
                AnimUtils.doScaleFadeAnim(v);
                if (!UserManager.makeSureLogin(this, REQUEST_CODE_PRASE)) {
                    isPrase = true;
                    if(guide_detail1.voted==1){
                        tv_praise.getCompoundDrawables()[1].setLevel(2);
                        tv_praise.setEnabled(false);
                        ToastUtils.show(getApplicationContext(),"您已经点赞过，不能再次点赞！");
                    }else{
                        voteComment();
                    }
                }
              break;
            case R.id.action_bar_right: {
                if (!UserManager.makeSureLogin(activity, REQUEST_CODE_LOGIN_NEW_COMMENT)) {
                    if (guide_detail1.collected == 0)
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
    private void initView(){
        mLoadingProgress = findViewById(R.id.loading_progress);
    }

    private void initData(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(StrategyDetailActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        //mRecyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        String url = ServerAPI.Guide.buildItemDetailUrl(guide_detail_id + "");
        RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url,
                QHStrategy.class, null,
                new Response.Listener<QHStrategy>() {
                    @Override
                    public void onResponse(QHStrategy response) {

                        guide_detail1 = response;
                        mAdapter = new GuideDetailAdapter(guide_detail1);
                        mRecyclerView.setAdapter(mAdapter);
                        mLoadingProgress.setVisibility(View.INVISIBLE);
                        if (response.guide_info == null || response.guide_info.size() == 0)
                            ToastUtils.show(StrategyDetailActivity.this,"暂无攻略内容");
                        tv_comment.setOnClickListener(StrategyDetailActivity.this);
                        tv_praise.setOnClickListener(StrategyDetailActivity.this);
                        getCommentList();
                        if(guide_detail1.voted==1){
                            tv_praise.getCompoundDrawables()[1].setLevel(2);}
                        tv_praise.setText("" + guide_detail1.vote_count);
                        tv_comment.setText("" + guide_detail1.comment_count);
                        if(guide_detail1.comment_count>10)limit = guide_detail1.comment_count;
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

    public void getCommentList() {
        url =ServerAPI.BASE_URL + "guides/"+guide_detail1.id+"/comments/?format=json";
        GsonRequest gsonRequest=   RequestManager.getInstance().sendGsonRequest(url, QHComment.QHCommentList.class,
                new Response.Listener<QHComment.QHCommentList>() {
                    @Override
                    public void onResponse(QHComment.QHCommentList response) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        Log.i("law respond",response.toString());
                        Log.i("law",response.toString());
                        mCommentList = response;


//                        Log.d("onResponse, CommentList=%s", response);
//                        mLoadingProgress.setVisibility(View.GONE);
//                        mContainerView.setVisibility(View.VISIBLE);
//                        if (response == null || response.list == null || response.list.isEmpty()) {
//                            mEmptyHintView.setVisibility(View.VISIBLE);
//                            return;
//                        }
//                        mEmptyHintView.setVisibility(View.GONE);
//                        onCommentListLoaded(response);
                        updateComment(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("law error",error.toString());
                        Log.i("law",error.toString());
//                        Log.e(error, "Error loading scenic details from %s", url);
//                        // TODO handle it
//                        mLoadingProgress.setVisibility(View.GONE);
//                        mEmptyHintView.setVisibility(View.VISIBLE);
                    }
                }, requestTag);
    }

    @Override
    public void onLoadFinished(QHComment.QHCommentList list, int mode) {
//        mLoadingProgress.setVisibility(View.GONE);
        tv_comment.setText(""+list.count);
        mRecyclerView.setVisibility(View.VISIBLE);
        mCommentList = list;
//        updateComment(list);
    }
    @Override
    public void onLoadError(int mode) {
//        mLoadingProgress.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void updateComment(QHComment.QHCommentList response) {
        tv_comment.setText("" + response.count);
//        tv_comment.setText(""+ response.results.size());
    }
    private void gotoShare(QHStrategy routedetail, String type) {
        if (routedetail != null && routedetail.guide_info != null && routedetail.guide_info.size() > 0) {
            QHStrategy.QHGuideInfo item = routedetail.guide_info.get(0);
            if (item != null) {
                String content = item.content;
                if (TextUtils.isEmpty(content))
                    content = routedetail.getTitle();
                if (type.equals("qq")) {
                    ShareManager.getInstance().shareQQ(routedetail.getTitle(), content, item.image_url, routedetail.getShareURL());
                }
                if (type.equals("sina")) {
                    ShareManager.getInstance().shareSina(routedetail.getTitle(), content, item.image_url, routedetail.getShareURL());

                }
                if (type.equals("wechat")) {
                    ShareManager.getInstance().shareWeChat(routedetail.getTitle(), content, item.image_url, routedetail.getShareURL());

                }
                if (type.equals("wechatcircle")) {
                    ShareManager.getInstance().shareWeChatCircle(routedetail.getTitle(), content, item.image_url, routedetail.getShareURL());
                }
            }
        }
    }

    private void gotoDirectShare(QHStrategy routedetail, String type) {
        if (routedetail != null && routedetail.guide_info != null && routedetail.guide_info.size() > 0) {
            QHStrategy.QHGuideInfo item = routedetail.guide_info.get(0);
            if (item != null) {
                String content = item.content;
                if (TextUtils.isEmpty(content))
                    content = routedetail.getTitle();
                if (type.equals("qq")) {
                    ShareManager.getInstance().shareQQ(routedetail.getTitle(), content, item.image_url, routedetail.getShareURL());
                }
                if (type.equals("sina")) {
                    ShareManager.getInstance().shareDirectSina(routedetail.getTitle(), content, item.image_url, routedetail.getShareURL());

                }
                if (type.equals("wechat")) {
                    ShareManager.getInstance().shareWeChat(routedetail.getTitle(), content, item.image_url, routedetail.getShareURL());

                }
                if (type.equals("wechatcircle")) {
                    ShareManager.getInstance().shareWeChatCircle(routedetail.getTitle(), content, item.image_url, routedetail.getShareURL());
                }
            }
        }
    }
    @Override
    public void onQQShareBack() {
        gotoShare(guide_detail1,"qq");
    }

    @Override
    public void onSinaShareBack() {
        gotoShare(guide_detail1,"sina");
    }

    @Override
    public void onWeChatShareBack() {
        gotoShare(guide_detail1,"wechat");
    }

    @Override
    public void onWeChatCircleShareBack() {
        gotoShare(guide_detail1,"wechatcircle");
    }

    private class GuideDetailAdapter extends RecyclerView.Adapter {

        private QHStrategy strategyDetail;

        public GuideDetailAdapter(QHStrategy strategyDetail) {
            this.strategyDetail = strategyDetail;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(getApplicationContext(),R.layout.item_route_detail1,null);
            return new GuideHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            GuideHolder routeHolder = (GuideHolder) holder;
            String large_url = strategyDetail.guide_info.get(position).image_url;
            String content = strategyDetail.guide_info.get(position).content;

            if (!TextUtils.isEmpty(large_url)) {
//                RequestManager.getInstance().getImageLoader().get(large_url,
//                        ImageLoader.getImageListener(routeHolder.imageView,
//                                R.drawable.recommand_bg, R.drawable.bg_image_error));

                ImageUtil.DisplayImage(large_url, routeHolder.imageView,
                        R.drawable.recommand_bg, R.drawable.bg_image_error);
            }else {
                routeHolder.imageView.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(content))
            routeHolder.textView.setText(content);
            else
                routeHolder.textView.setVisibility(View.GONE);

        }

        @Override
        public int getItemCount() {
            return strategyDetail.guide_info.size();
        }


        private class GuideHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;
            public GuideHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.route_detail_image);
                textView = (TextView) view.findViewById(R.id.route_detail_tv);
            }
        }
    }

    private void voteComment() {
        if (guide_detail1 == null || tv_praise == null) {
            com.cmcc.hyapps.andyou.util.Log.e("[voteComment] NULL comment:" + guide_detail1);
            return;
        }
//        if(isPrase){
//            return;
//        }
        CommentObj obj = new CommentObj(""+guide_detail1.id,ServerAPI.Comments.Type.RAIDERS.value());
        Gson gson = new Gson();
        String jsonBody = gson.toJson(obj);
        RequestManager.getInstance().sendGsonRequest(Request.Method.POST, ServerAPI.Comments.GUIDER_VOTE_URL,
                jsonBody,
                QHCollectionStrategy.class, new Response.Listener<QHCollectionStrategy>() {
                    @Override
                    public void onResponse(QHCollectionStrategy response) {
                        tv_praise.setText(String.valueOf(response.vote_count));
//                        tv_praise.getCompoundDrawables()[1].setLevel(2);
                        isPrase = false;
                        guide_detail1.voted = 1;
                        guide_detail1.vote_count = response.vote_count;
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
                        } else {
                            AppUtils.handleResponseError(StrategyDetailActivity.this, error);
                        }

                    }
                }, "--------------");
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
    /* 收藏路线*/
    private void postCollection() {
        mLoadingProgress.setVisibility(View.VISIBLE);

        if (guide_detail1 == null || tv_praise == null) {
            com.cmcc.hyapps.andyou.util.Log.e("[voteComment] NULL comment:" + guide_detail1);
            return;
        }

        CommentObj obj = new CommentObj(""+guide_detail1.id,ServerAPI.Comments.Type.RAIDERS.value());
        Gson gson = new Gson();
        String jsonBody = gson.toJson(obj);
        RequestManager.getInstance().sendGsonRequest(Request.Method.POST, ServerAPI.Comments.GUIDER_COLLECT_URL,
                jsonBody,
                QHCollectionStrategy.class, new Response.Listener<QHCollectionStrategy>() {
                    @Override
                    public void onResponse(QHCollectionStrategy response) {
                        Log.e("StrategyDetailActivity",response.toString());
                        //   tv_praise.setText(String.valueOf(response.vote_count));
//                        tv_praise.getCompoundDrawables()[1].setLevel(2);
//                        guide_detail1.voted = 1;
//                        guide_detail1.vote_count = response.vote_count;
//                        tv_praise.setEnabled(false);
//                        tv_praise.setClickable(false);

                        guide_detail1.collected = response.id;
                        if (mLoadingProgress != null){
                            mLoadingProgress.setVisibility(View.GONE);
                        }
                        actionBar.getRightView().setSelected(true);
                        //  actionBar.getRightView().setImageResource(R.drawable.icon_collected);
                     //   actionBar.getRightView().setClickable(false);
                        ToastUtils.show(activity.getBaseContext(), R.string.collection_success);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mLoadingProgress != null){
                            mLoadingProgress.setVisibility(View.GONE);
                        }
                            ToastUtils.show(activity.getBaseContext(), R.string.collection_fail);
                    }
                }, "--------------");
    }
    GsonRequest<QHCollectionStrategy> cancleGsonRequest = null;
    /* 取消收藏路线*/
    private void cancleCollection() {
        mLoadingProgress.setVisibility(View.VISIBLE);

        if (guide_detail1 == null || tv_praise == null) {
            com.cmcc.hyapps.andyou.util.Log.e("[voteComment] NULL comment:" + guide_detail1);
            return;
        }

      cancleGsonRequest = RequestManager.getInstance().sendGsonRequest(Request.Method.POST, ServerAPI.Comments.GUIDER_COLLECT_URL + guide_detail1.collected +"/",
                null,
                QHCollectionStrategy.class, new Response.Listener<QHCollectionStrategy>() {
                    @Override
                    public void onResponse(QHCollectionStrategy response) {
                        if (mLoadingProgress != null){
                            mLoadingProgress.setVisibility(View.GONE);
                        }
                        ToastUtils.show(activity.getBaseContext(),R.string.cancle_collection_fail);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       if (cancleGsonRequest != null && cancleGsonRequest.getStateCode() == 204){
                           guide_detail1.collected = 0;
                           if (mLoadingProgress != null){
                               mLoadingProgress.setVisibility(View.GONE);
                           }
                           actionBar.getRightView().setSelected(false);
                           ToastUtils.show(activity.getBaseContext(),R.string.cancle_collection_success);
                       }else {
                           if (mLoadingProgress != null){
                               mLoadingProgress.setVisibility(View.GONE);
                           }
                           ToastUtils.show(activity.getBaseContext(),R.string.cancle_collection_fail);
                       }
                    }
                }, "--------------");
    }


}
