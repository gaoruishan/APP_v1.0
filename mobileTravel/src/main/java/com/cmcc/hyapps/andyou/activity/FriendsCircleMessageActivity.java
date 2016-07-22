package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.FriendsCircleMessageAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.QHFriendInfo;
import com.cmcc.hyapps.andyou.model.QHMessages;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 圈子消息列表页面
 * Created by bingbing on 2015/10/27.
 */
public class FriendsCircleMessageActivity extends BaseActivity implements View.OnClickListener, DataLoader.DataLoaderCallback<QHMessages.QHMessageList> {
    private ActionBar mActionBar;
    private PullToRefreshRecyclerView mPullToRefreshView;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private View mReloadView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FriendsCircleMessageAdapter mFriendsCircleMessageAdapter;

    private UrlListLoader<QHMessages.QHMessageList> mLoader;
    //来盘点是否返回上个activity要影藏头部
    private boolean isHideHeader = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_circle_message_list);
        mFriendsCircleMessageAdapter = new FriendsCircleMessageAdapter(this);
        initView();
        initActionBar();
        initPullToRefreshView();
        setUrl();
        reload();
    }

    private void setUrl() {
        mLoader = new UrlListLoader<QHMessages.QHMessageList>("FriendsCircleMessageActivity", QHMessages.QHMessageList.class);
        mLoader.setUseCache(false);
        mLoader.setUrl(ServerAPI.ADDRESS + "friends/circle/getMessages.do");
    }

    private void reload() {
        mPullToRefreshView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);
        mLoader.loadMoreQHDataAES(this, DataLoader.MODE_REFRESH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_left:
                Intent intent = new Intent();
                intent.putExtra("isHideHeader",isHideHeader);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.reload_view:
                reload();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.putExtra("isHideHeader",isHideHeader);
            setResult(RESULT_OK, intent);
            finish();
        }
        return false;
    }

    private void initView() {
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mEmptyHintView = this.findViewById(R.id.empty_hint_view);
        mLoadingProgress = this.findViewById(R.id.loading_progress);
        mReloadView = this.findViewById(R.id.reload_view);
        mReloadView.setOnClickListener(this);
    }

    private void initActionBar() {
        mActionBar = (ActionBar) findViewById(R.id.action_bar);
        mActionBar.setTitle("消息");
        mActionBar.getTitleView().setTextColor(Color.WHITE);
        mActionBar.setBackgroundResource(R.color.title_bg);
        mActionBar.getLeftView().setImageResource(R.drawable.return_back);
        mActionBar.getLeftView().setOnClickListener(this);
    }

    private void initPullToRefreshView() {
        mPullToRefreshView = (PullToRefreshRecyclerView) this
                .findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        PullToRefreshBase.Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreQHDataAES(FriendsCircleMessageActivity.this, mode);
            }
        });
        RecyclerView recyclerView = mPullToRefreshView.getRefreshableView();
        recyclerView.setLayoutManager(mLayoutManager);
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerView);
        itemClickSupport.setOnItemSubViewClickListener(new ItemClickSupport.OnItemSubViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onItemClicked(view,position);
            }
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int scap = ScreenUtils.dpToPxInt(this, 1);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Color.LTGRAY, scap, 0);
        dividerItemDecoration.initWithRecyclerView(recyclerView);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mFriendsCircleMessageAdapter);
    }

    public final int MESSAGE_LIST_TO_FRIENDS_DESCRIPTION = 1006;

    private void onItemClicked(View view,int position) {
        QHMessages qhMessages = (QHMessages) view.getTag();
        switch (view.getId()) {
            case R.id.friends_message_item_avator:
                if (qhMessages != null) {
                    Intent intent = new Intent(FriendsCircleMessageActivity.this, UserInformationActivity.class);
                    intent.putExtra("user_ID", qhMessages.getFromUser().getUserId());
                    startActivity(intent);
                }
                break;
            case R.id.friends_message_item_linear:
                if (qhMessages != null && qhMessages.getOperateObject() != null) {
                    Intent descriptionIntent = new Intent(this, FriendsCircleDescriptionActivity.class);
                    descriptionIntent.putExtra("info_ID", qhMessages.getInfoId());
                    startActivityForResult(descriptionIntent, MESSAGE_LIST_TO_FRIENDS_DESCRIPTION);
                }
                break;
        }
    }

    @Override
    public void onLoadFinished(QHMessages.QHMessageList qhMessageList, int mode) {
        isHideHeader = true;
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();
        List<QHMessages> list = null;
        if (qhMessageList != null && qhMessageList.results != null && qhMessageList.results.size() != 0) {
            list = qhMessageList.results;
        }
        if (list == null || list.isEmpty()) {
            if (mode == DataLoader.MODE_REFRESH) {
                mPullToRefreshView.setVisibility(View.INVISIBLE);
                mEmptyHintView.setVisibility(View.VISIBLE);
            } else {
                ToastUtils.AvoidRepeatToastShow(this, R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        } else {
            mEmptyHintView.setVisibility(View.GONE);
            mPullToRefreshView.setVisibility(View.VISIBLE);

            if (mode == DataLoader.MODE_REFRESH) {
                mFriendsCircleMessageAdapter.setDataItems(qhMessageList.results);
                return;
            }

            mFriendsCircleMessageAdapter.appendDataItems(qhMessageList.results);
        }
    }

    @Override
    public void onLoadError(int mode) {
        mLoadingProgress.setVisibility(View.INVISIBLE);
        mReloadView.setVisibility(View.VISIBLE);
        mEmptyHintView.setVisibility(View.GONE);
        mPullToRefreshView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case MESSAGE_LIST_TO_FRIENDS_DESCRIPTION:
                    if (data != null && mFriendsCircleMessageAdapter != null){
                        int infoId = data.getIntExtra("infoId", 0);
                        //这里的isDelete删除是删除整条动态，不是删除动态评论
                        boolean isDelete = data.getBooleanExtra("isDelete", false);
                        List<QHMessages> qhMessagesList = mFriendsCircleMessageAdapter.getDataItems();
                        //用来存放要删除的对象，在遍历迭代的时候操作了增删，会出现java.util.ConcurrentModificationException
                        List<QHMessages> deleteList = new ArrayList<QHMessages>();
                        if (isDelete && qhMessagesList != null) {
                           for (QHMessages item : qhMessagesList){
                               if (item.getInfoId() == infoId){
                                   deleteList.add(item);
                               }
                           }
                            qhMessagesList.removeAll(deleteList);
                            mFriendsCircleMessageAdapter.notifyDataSetChanged();
                            if (mFriendsCircleMessageAdapter.getDataItems().size() == 0 && mEmptyHintView != null && mPullToRefreshView != null){
                                mEmptyHintView.setVisibility(View.VISIBLE);
                                mPullToRefreshView.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    break;
            }
        }
    }
}
