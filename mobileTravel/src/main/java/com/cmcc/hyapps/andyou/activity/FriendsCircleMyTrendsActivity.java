package com.cmcc.hyapps.andyou.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.FriendsCircleMyTrendsAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.FriendsCircleVote;
import com.cmcc.hyapps.andyou.model.QHDelete;
import com.cmcc.hyapps.andyou.model.QHFriendInfo;
import com.cmcc.hyapps.andyou.model.QHPublicInfoDetail;
import com.cmcc.hyapps.andyou.util.AESEncrpt;
import com.cmcc.hyapps.andyou.util.AnimUtils;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bingbing on 2015/11/11.
 */
public class FriendsCircleMyTrendsActivity extends BaseActivity implements View.OnClickListener, DataLoader.DataLoaderCallback<QHFriendInfo.QHFriendInfoList> {
    private PullToRefreshRecyclerView mPullToRefreshView;
    private UrlListLoader<QHFriendInfo.QHFriendInfoList> mLoader;

    private AppendableAdapter<QHFriendInfo> mAdapter;

    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mReloadView;

    private RecyclerView.LayoutManager mLayoutManager;
    private String user_id;

    public final int TO_FRIENDS_DESCRIPTION = 1000;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_left:
                finish();
                break;
        }
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        if (TextUtils.isEmpty(user_id)){
            actionBar.setTitle("我的动态");
        }else
            actionBar.setTitle("TA的动态");
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    private void setUrl() {
        mLoader = new UrlListLoader<QHFriendInfo.QHFriendInfoList>("FriendsCircleMyTrendsActivity", QHFriendInfo.QHFriendInfoList.class);
        mLoader.setUseCache(false);
        String url = null;
        if (TextUtils.isEmpty(user_id))
            url = ServerAPI.ADDRESS + "friends/circle/getMyInformations.do";
        else{
//            url = ServerAPI.ADDRESS + "friends/circle/getHisInformations.do?userId=" + user_id ;
            url = ServerAPI.ADDRESS + "friends/circle/getHisInformations.do" ;
            Map<String,Object> maps = new HashMap<String, Object>();
            maps.put("userId",user_id);
            String data = "";
            try {
                data = AESEncrpt.Encrypt(new Gson().toJson(maps), AppUtils.dynamicKey);
                url = url + "?data=" + data;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        mLoader.setUrl(url);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_my_trends);
        user_id = getIntent().getStringExtra("user_id");
        mAdapter = new FriendsCircleMyTrendsAdapter(this);
        initActionBar();
        initView();
        reload(DataLoader.MODE_REFRESH);
    }

    private void reload(int mode) {
        if (mAdapter != null)
            mAdapter.setDataItems(null);
        setUrl();
        mLoader.loadMoreQHDataAES(this, mode);
    }

    private void initView() {
        mEmptyHintView = this.findViewById(R.id.empty_hint_view);
        mLoadingProgress = findViewById(R.id.loading_progress);
        mReloadView = findViewById(R.id.reload_view);
        mReloadView.setOnClickListener(this);

        mPullToRefreshView = (PullToRefreshRecyclerView)
                findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        PullToRefreshBase.Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH
                        : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreQHDataAES(FriendsCircleMyTrendsActivity.this, mode);
            }
        });

        RecyclerView recyclerView = mPullToRefreshView.getRefreshableView();
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerView);
        itemClickSupport.setOnItemSubViewClickListener(new ItemClickSupport.OnItemSubViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                  itemClick(view,position);
            }
        });
        int scap = ScreenUtils.dpToPxInt(this, 8);
        DividerItemDecoration decor = new DividerItemDecoration(getResources().getDrawable(R.drawable.friends_circle_layer_list_top_bottom),scap,0);
//        DividerItemDecoration decor = new DividerItemDecoration(getResources().getColor(R.color.base_grey_bg),scap,0);
        decor.initWithRecyclerView(recyclerView);
        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(mAdapter);
    }
        private void itemClick(View v,int position){
            if (ExcessiveClickBlocker.isExcessiveClick()) {
                return;
            }
            QHFriendInfo qhFriendInfo = (QHFriendInfo) v.getTag();
            if (qhFriendInfo == null)
                return;
            Intent intent = null;
            switch (v.getId()) {
//                case R.id.friends_circle_recycle_item_avator:
//                    intent = new Intent(FriendsCircleFragment.this.getActivity(), UserInformationActivity.class);
//                    intent.putExtra("user_ID", qhFriendInfo.getPublishUser().getUserId());
//                    break;
                case R.id.friends_circle_recycle_item_main_layout:
                    Intent descriptionIntent = new Intent(this, FriendsCircleDescriptionActivity.class);
                    descriptionIntent.putExtra("info_ID", qhFriendInfo.getInfoId());
                    descriptionIntent.putExtra("position", position);
                    startActivityForResult(descriptionIntent, TO_FRIENDS_DESCRIPTION);
                    break;
                case R.id.friends_circle_recycle_item_vote:
                    if (v.isSelected()) {
                        return;
                    }
                    AnimUtils.doScaleFadeAnim(v);
                    voteTrends(qhFriendInfo.getInfoId(), v, qhFriendInfo);
                    break;
                case R.id.friends_circle_recycle_item_delete:
                    showDialog(qhFriendInfo);
                    break;
            }
            if (intent != null)
                startActivity(intent);
        }
    private void showDialog(final QHFriendInfo qhFriendInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_item);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                deleteTrends(qhFriendInfo);
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
    /**
     * 删除动态
     *
     * @param qhFriendInfo
     */
    private void deleteTrends(final QHFriendInfo qhFriendInfo) {
//        String delete_url = ServerAPI.ADDRESS + "friends/deleteInformation.do?infoId=" + qhFriendInfo.getInfoId();
        String delete_url = ServerAPI.ADDRESS + "friends/circle/deleteInformation.do";
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("infoId", qhFriendInfo.getInfoId());
        String body = getRequestParams(maps,delete_url);
        RequestManager.getInstance().sendGsonRequestAESforPOST(delete_url, QHDelete.class, body, new Response.Listener<QHDelete>() {
            @Override
            public void onResponse(QHDelete response) {
                ToastUtils.show(FriendsCircleMyTrendsActivity.this, "删除成功");
                mAdapter.getDataItems().remove(qhFriendInfo);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(FriendsCircleMyTrendsActivity.this, "删除失败");
            }
        }, "deleteTrends", AppUtils.dynamicKey);
    }
    /**
     * 对动态点赞
     */
    private void voteTrends(int id, final View view, final QHFriendInfo qhFriendInfo) {
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("infoId",id);
        String vote_url = ServerAPI.ADDRESS + "friends/circle/praise.do";
        String body = getRequestParams(maps,vote_url);
        RequestManager.getInstance().sendGsonRequestAESforPOST( vote_url, FriendsCircleVote.class, body, new Response.Listener<FriendsCircleVote>() {
            @Override
            public void onResponse(FriendsCircleVote response) {
                if (response.isSuccessful()) {
                    //执行点赞成功后的逻辑
                    TextView vote = (TextView) view;
                    List<QHFriendInfo> list = mAdapter.getDataItems();
                    for (int i =0; i < list.size();i ++){
                        if (list.get(i).getInfoId() == qhFriendInfo.getInfoId()){
                            list.get(i).setPraiseNum(qhFriendInfo.getPraiseNum() + 1);
                            list.get(i).setIsPraised(1);
                            mAdapter.notifyItemChanged(i);
                        }
                    }
//                    vote.setSelected(true);
                    vote.setEnabled(false);
                } else {
                    ToastUtils.show(FriendsCircleMyTrendsActivity.this, "点赞失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(FriendsCircleMyTrendsActivity.this, "点赞失败");
            }
        }, "loadTrendsComment",AppUtils.dynamicKey);
    }
    @Override
    public void onLoadFinished(QHFriendInfo.QHFriendInfoList qhFriendInfoList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();
        List<QHFriendInfo> list = null;
        if (qhFriendInfoList != null && qhFriendInfoList.results != null && qhFriendInfoList.results.size() != 0) {
            list = qhFriendInfoList.results;
        }
        if (list == null || list.isEmpty()) {
            if (mode==DataLoader.MODE_REFRESH) {
                mPullToRefreshView.setVisibility(View.INVISIBLE);
                mEmptyHintView.setVisibility(View.VISIBLE);
            }else {
                ToastUtils.AvoidRepeatToastShow(this, R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        }else {
            mEmptyHintView.setVisibility(View.GONE);
            mPullToRefreshView.setVisibility(View.VISIBLE);

            if (mode == DataLoader.MODE_REFRESH) {
                mAdapter.setDataItems(qhFriendInfoList.results);
                return;
            }

            mAdapter.appendDataItems(qhFriendInfoList.results);
        }
    }

    @Override
    public void onLoadError(int mode) {
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TO_FRIENDS_DESCRIPTION:
                    if (mAdapter != null && mAdapter.getDataItems() != null) {
                        if (data != null) {
                            int position = data.getIntExtra("position", 0);
                            boolean isVote = data.getBooleanExtra("isVote", false);
                            boolean isComment = data.getBooleanExtra("isComment", false);
                            boolean isDelete = data.getBooleanExtra("isDelete", false);
                            int comment_count = data.getIntExtra("comment_count", 0);
                            QHFriendInfo item = mAdapter.getDataItems().get(position);
                            if (isComment) {
                                item.setCommentNum(comment_count);
                                mAdapter.notifyItemChanged(position);
                            }
                            if (isVote) {
                                item.setPraiseNum(item.getPraiseNum() + 1);
                                item.setIsPraised(1);
                                mAdapter.notifyItemChanged(position);
                            }
                            if (isDelete){
                                mAdapter.getDataItems().remove(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
            }
        }
    }
}
