package com.cmcc.hyapps.andyou.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.cmcc.hyapps.andyou.activity.FriendsCircleDescriptionActivity;
import com.cmcc.hyapps.andyou.activity.FriendsCircleMessageActivity;
import com.cmcc.hyapps.andyou.activity.FriendsCircleSendTrendActivity;
import com.cmcc.hyapps.andyou.activity.UserInformationActivity;
import com.cmcc.hyapps.andyou.adapter.FriendsCircleAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.FriendsCircleVote;
import com.cmcc.hyapps.andyou.model.QHDelete;
import com.cmcc.hyapps.andyou.model.QHFriendInfo;
import com.cmcc.hyapps.andyou.model.QHMessageCount;
import com.cmcc.hyapps.andyou.service.FriendsCircleMessageCountBroadcast;
import com.cmcc.hyapps.andyou.update.UpdateDownLoadManager;
import com.cmcc.hyapps.andyou.util.AESEncrpt;
import com.cmcc.hyapps.andyou.util.AnimUtils;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ChoosePopupWindow;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.grs.com.ims.contact.AddContactActivity;


/**
 * 朋友圈主页
 * Created by bingbing on 2015/10/19.
 */
public class FriendsCircleFragment extends ServiceBaseFragment implements View.OnClickListener, ChoosePopupWindow.OnPopupWindowsClickListener, DataLoader.DataLoaderCallback<QHFriendInfo.QHFriendInfoList>, FriendsCircleMessageCountBroadcast.OnFriendsCircleMessageCountListener {
    private ImageView friends_book, friends_message;
    private TextView title_textview, add_forcus_textview, send_trends_textview;
    private RecyclerView recyclerView;
    private View actionBar;
    private View reload_view, empty_view;
    private View mLoadingProgress;
    private PullToRefreshRecyclerView mPullToRefreshView;

    private ChoosePopupWindow mChoosePopupWindow;

    private List<String> mStringList = new ArrayList<String>();

    private UrlListLoader<QHFriendInfo.QHFriendInfoList> mListLoader;
    private FriendsCircleAdapter mFriendsCircleAdapter;

    private int messageCount = 0;
    private QHMessageCount mQHMessageCount;
    private FriendsCircleMessageCountBroadcast mCountBroadcast;

    public final int TO_FRIENDS_DESCRIPTION = 1000;
    public final int TO_FRIENDS_SEND = 1005;
    public final int TO_FRIENDS_MESSAGE_LIST = 1004;
    public static final String FRIENDS_CIRCLE_MESSAGECOUNT_BROADCAST = "com.FriendsCircleMessageCountBroadcast";

    private void registerFriendsCircleCountBroadcast() {
        mCountBroadcast = new FriendsCircleMessageCountBroadcast();
        mCountBroadcast.setMessageCountListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FRIENDS_CIRCLE_MESSAGECOUNT_BROADCAST);
        getActivity().registerReceiver(mCountBroadcast, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friends_circle_title_name:
                //  mChoosePopupWindow.showPopupWindow(v);
                mChoosePopupWindow.showFriendsPopupWindow(actionBar, 0);
                break;
            case R.id.friends_circle_books:
                //获取推荐用户

                //跳转到通讯录
                startActivity(new Intent(getActivity(), AddContactActivity.class));
                break;
            case R.id.friends_circle_message:
                Intent intent = new Intent(getActivity(), FriendsCircleSendTrendActivity.class);
                startActivityForResult(intent, TO_FRIENDS_SEND);
                break;
            case R.id.reload_view:
                reload(DataLoader.MODE_REFRESH);
                break;
            case R.id.friends_circle_empty_add_trends:
                Intent sendIntent = new Intent(getActivity(), FriendsCircleSendTrendActivity.class);
                startActivity(sendIntent);
                break;
            case R.id.friends_circle_empty_add_frocus:
                startActivity(new Intent(getActivity(), AddContactActivity.class));
                break;
            case R.id.friends_circle_actionbar:
                //do nothing  to solve 点击穿透显示了上个fragment的搜索框
                break;

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_friends_circle_layout, container, false);
        initView(view);
        getTrendsMessageCount();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view) {
        initActionbar(view);
        initPopupWindows();
        initPullToRefresh(view);
        initRecycleView(view);
        initOriView(view);
    }

    private void initActionbar(View view) {
        actionBar = view.findViewById(R.id.friends_circle_actionbar);
        actionBar.setOnClickListener(this);
        friends_book = (ImageView) view.findViewById(R.id.friends_circle_books);
        friends_book.setOnClickListener(this);
        friends_message = (ImageView) view.findViewById(R.id.friends_circle_message);
        friends_message.setOnClickListener(this);
        title_textview = (TextView) view.findViewById(R.id.friends_circle_title_name);
        title_textview.setOnClickListener(this);
    }

    private void initRecycleView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplication());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerView);
        itemClickSupport.setOnItemSubViewClickListener(new ItemClickSupport.OnItemSubViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    //这样做判断是为了在切换有没有header时候，保证点击行数的正确性
                    if (messageCount != 0) {
                        onHeaderClicked(view);
                    } else {
                        onItemClicked(view, position);
                    }
                } else {
                    onItemClicked(view, position);
                }
            }
        });

        int scape = ScreenUtils.dpToPxInt(getActivity(), 8);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity().getResources().getColor(R.color.base_grey_bg), scape, 0);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity().getResources().getDrawable(R.drawable.friends_circle_layer_list_top_bottom), scape, 0);
        dividerItemDecoration.initWithRecyclerView(recyclerView);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setDataToRecycleView() {

        mFriendsCircleAdapter = new FriendsCircleAdapter(FriendsCircleFragment.this.getActivity());
        if (messageCount == 0) {
            mFriendsCircleAdapter.setHeaderEnable(false);
        } else {
            mFriendsCircleAdapter.setHeaderEnable(true);
        }

        mFriendsCircleAdapter.setHeader(getQHMessageCount());
        recyclerView.setAdapter(mFriendsCircleAdapter);
    }

    /**
     * 设置消息个数
     *
     * @return
     */
    private QHMessageCount getQHMessageCount() {
        if (mQHMessageCount == null) {
            mQHMessageCount = new QHMessageCount();
        }
        mQHMessageCount.setNewMessageNum(messageCount);
        return mQHMessageCount;
    }

    private void initOriView(View view) {
        mLoadingProgress = view.findViewById(R.id.loading_progress);
        reload_view = view.findViewById(R.id.reload_view);
        reload_view.setOnClickListener(this);

        empty_view = view.findViewById(R.id.empty_hint_view);
        empty_view.setOnClickListener(this);

        add_forcus_textview = (TextView) view.findViewById(R.id.friends_circle_empty_add_frocus);
        add_forcus_textview.setOnClickListener(this);
        send_trends_textview = (TextView) view.findViewById(R.id.friends_circle_empty_add_trends);
        send_trends_textview.setOnClickListener(this);
    }

    private void initPullToRefresh(View view) {
        mPullToRefreshView = (PullToRefreshRecyclerView) view.findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (refreshView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    reload(DataLoader.MODE_REFRESH);
                } else {
                    mListLoader.loadMoreQHDataAES(FriendsCircleFragment.this, DataLoader.MODE_LOAD_MORE);
                }
            }
        });
    }

    @Override
    public void onPopupItemClick() {
        changeFriendsCircleList();
    }

    private void initPopupWindows() {
        mStringList.add(getResources().getString(R.string.friends_all_trends));
        mStringList.add(getResources().getString(R.string.friends_forcus_trends));
        mStringList.add(getResources().getString(R.string.friends_my_trends));

        mChoosePopupWindow = new ChoosePopupWindow(getActivity(), mStringList, 2);
        mChoosePopupWindow.setChoosedView(title_textview);
        mChoosePopupWindow.isSetDrawableRight(false);
        mChoosePopupWindow.setOnPopupWindowsClickListener(this);
    }

    @Override
    public void onLoadFinished(QHFriendInfo.QHFriendInfoList qhFriendInfoList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        reload_view.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();
        List<QHFriendInfo> list = null;
        if (qhFriendInfoList != null && qhFriendInfoList.results != null && qhFriendInfoList.results.size() != 0) {
            list = qhFriendInfoList.results;
        }
        if (list == null || list.isEmpty()) {
            if (mode == DataLoader.MODE_REFRESH) {
                mPullToRefreshView.setVisibility(View.INVISIBLE);
                empty_view.setVisibility(View.VISIBLE);
            } else {
                ToastUtils.AvoidRepeatToastShow(getActivity(), R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        } else {
            empty_view.setVisibility(View.GONE);
            mPullToRefreshView.setVisibility(View.VISIBLE);

            if (mode == DataLoader.MODE_REFRESH) {
                if (mFriendsCircleAdapter != null){
                    mFriendsCircleAdapter.setDataItems(qhFriendInfoList.results);
                }
                return;
            }

            mFriendsCircleAdapter.appendDataItems(qhFriendInfoList.results);
        }
    }

    @Override
    public void onLoadError(int mode) {
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        if (mode == DataLoader.MODE_LOAD_MORE) {
            ToastUtils.show(this.getActivity(), R.string.load_more_faild);
        } else {
            reload_view.setVisibility(View.VISIBLE);
            mPullToRefreshView.setVisibility(View.INVISIBLE);
        }
    }

    private void reload(int mode) {
        if (mFriendsCircleAdapter != null)
            mFriendsCircleAdapter.setDataItems(null);
        mPullToRefreshView.setVisibility(View.INVISIBLE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        reload_view.setVisibility(View.INVISIBLE);
        empty_view.setVisibility(View.INVISIBLE);
        mListLoader = new UrlListLoader<QHFriendInfo.QHFriendInfoList>(mRequestTag, QHFriendInfo.QHFriendInfoList.class);
        mListLoader.setUrl(setUrl());
        mListLoader.loadMoreQHDataAES(this, mode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCountBroadcast != null)
        getActivity().unregisterReceiver(mCountBroadcast);
    }

    private void changeFriendsCircleList() {
        mPullToRefreshView.setVisibility(View.INVISIBLE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        empty_view.setVisibility(View.INVISIBLE);
        reload_view.setVisibility(View.INVISIBLE);
        reload(DataLoader.MODE_REFRESH);
    }

    private String setUrl() {
//        String url = ServerAPI.ADDRESS + "friends/getFriendsInformations.do";
        String url = ServerAPI.ADDRESS + "friends/circle/getFriendsInformations.do";
        Map<String, Object> maps = new HashMap<String, Object>();

        if (mChoosePopupWindow.getCurrentPosition() == 0)
            maps.put("type", "0");
        if (mChoosePopupWindow.getCurrentPosition() == 1)
            maps.put("type", "1");
        if (mChoosePopupWindow.getCurrentPosition() == 2)
            maps.put("type", "2");
        String data = null;
        try {
            data = AESEncrpt.Encrypt(new Gson().toJson(maps), AppUtils.dynamicKey);
            url = url + "?data=" + data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private void onHeaderClicked(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.friends_circle_header_message_layout:
                Intent intent = new Intent(FriendsCircleFragment.this.getActivity(), FriendsCircleMessageActivity.class);
                startActivityForResult(intent, TO_FRIENDS_MESSAGE_LIST);
                break;
        }
    }

    private void onItemClicked(View v, int position) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        QHFriendInfo qhFriendInfo = (QHFriendInfo) v.getTag();
        if (qhFriendInfo == null)
            return;
        Intent intent = null;
        switch (v.getId()) {
            case R.id.friends_circle_recycle_item_avator:
            case R.id.friends_circle_recycle_item_name:
                intent = new Intent(FriendsCircleFragment.this.getActivity(), UserInformationActivity.class);
                intent.putExtra("user_ID", qhFriendInfo.getPublishUser().getUserId());
                break;
            case R.id.friends_circle_recycle_item_main_layout:
                Intent descriptionIntent = new Intent(FriendsCircleFragment.this.getActivity(), FriendsCircleDescriptionActivity.class);
                descriptionIntent.putExtra("info_ID", qhFriendInfo.getInfoId());
                descriptionIntent.putExtra("position", position);
                descriptionIntent.putExtra("isHasHead", mFriendsCircleAdapter.isHeaderEnable());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == FriendsCircleFragment.this.getActivity().RESULT_OK) {
            switch (requestCode) {
                case TO_FRIENDS_DESCRIPTION:
                    if (mFriendsCircleAdapter != null && mFriendsCircleAdapter.getDataItems() != null) {
                        if (data != null) {
                            int position = data.getIntExtra("position", 0);
                            boolean isVote = data.getBooleanExtra("isVote", false);
                            boolean isComment = data.getBooleanExtra("isComment", false);
                            boolean isDelete = data.getBooleanExtra("isDelete", false);
                            boolean isHasHead = data.getBooleanExtra("isHasHead", false);
                            int comment_count = data.getIntExtra("comment_count", 0);
                            //跳转时候主要带头，返回来position要-1
                            if (isHasHead) {
                                --position;
                            }
                            //其他情况position不会发生变化
                            QHFriendInfo item = mFriendsCircleAdapter.getDataItems().get(position);
                            if (mFriendsCircleAdapter.isHeaderEnable()) {
                                ++position;
                            }
                            if (isComment) {
                                item.setCommentNum(comment_count);
                                mFriendsCircleAdapter.notifyItemChanged(position);
                            }
                            if (isVote) {
                                item.setPraiseNum(item.getPraiseNum() + 1);
                                item.setIsPraised(1);
                                mFriendsCircleAdapter.notifyItemChanged(position);
                            }
                            if (isDelete) {
                                mFriendsCircleAdapter.getDataItems().remove(position);
                                mFriendsCircleAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                    break;
                case TO_FRIENDS_SEND:
                    reload(DataLoader.MODE_REFRESH);
                    break;
                case TO_FRIENDS_MESSAGE_LIST:
                    messageCount = 0;
                    setDataToRecycleView();
                    reload(DataLoader.MODE_REFRESH);
//                    if (mFriendsCircleAdapter != null && mFriendsCircleAdapter.isHeaderEnable()) {
//                        //用来解决消失headerhou,第一行不能点击问题
//                        messageCount = 0;
//                        List<QHFriendInfo> list = mFriendsCircleAdapter.getDataItems();
//                        mFriendsCircleAdapter.setHeaderEnable(false);
//                        mFriendsCircleAdapter.setDataItems(list);
//                    }
                    break;
            }
        }
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
                ToastUtils.show(FriendsCircleFragment.this.getActivity(), "删除成功");
                mFriendsCircleAdapter.getDataItems().remove(qhFriendInfo);
                mFriendsCircleAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(FriendsCircleFragment.this.getActivity(), "删除失败");
            }
        }, "deleteTrends", AppUtils.dynamicKey);
    }

    /**
     * 对动态点赞
     */
    private void voteTrends(int id, final View view, final QHFriendInfo qhFriendInfo) {
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("infoId", id);
        String vote_url = ServerAPI.ADDRESS + "friends/circle/praise.do";
        String body = getRequestParams(maps, vote_url);
        RequestManager.getInstance().sendGsonRequestAESforPOST(vote_url, FriendsCircleVote.class, body, new Response.Listener<FriendsCircleVote>() {
            @Override
            public void onResponse(FriendsCircleVote response) {
                if (response.isSuccessful()) {
                    //执行点赞成功后的逻辑
                    TextView vote = (TextView) view;
//                    vote.setText(qhFriendInfo.getPraiseNum() + 1 + "");
                    List<QHFriendInfo> list = mFriendsCircleAdapter.getDataItems();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getInfoId() == qhFriendInfo.getInfoId()) {
                            list.get(i).setPraiseNum(qhFriendInfo.getPraiseNum() + 1);
                            list.get(i).setIsPraised(1);
                            mFriendsCircleAdapter.notifyItemChanged(i);
                        }
                    }
//                    vote.setSelected(true);
                    vote.setEnabled(false);
                } else {
                    ToastUtils.show(FriendsCircleFragment.this.getActivity(), R.string.friends_vote_trends_faild);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(FriendsCircleFragment.this.getActivity(), R.string.friends_vote_trends_faild);
            }
        }, "loadTrendsComment", AppUtils.dynamicKey);
    }

    private QHMessageCount qhMessageCount;

    @Override
    public void onFriendsCircleMessageCount(Intent intent) {
        if (intent != null) {
            messageCount = intent.getIntExtra("messageCount", 0);
            if (mFriendsCircleAdapter != null && mFriendsCircleAdapter.isHeaderEnable() && mFriendsCircleAdapter.getHeader() != null && mFriendsCircleAdapter.getHeader().getMessage_count_textview() != null) {
                mFriendsCircleAdapter.getHeader().getMessage_count_textview().setText(messageCount + "条消息");
            }
            if (mFriendsCircleAdapter != null) {
                List<QHFriendInfo> list = mFriendsCircleAdapter.getDataItems();
                if (messageCount > 0) {
                    mFriendsCircleAdapter.setHeaderEnable(true);
                    if (qhMessageCount == null) {
                        qhMessageCount = new QHMessageCount();
                    }
                    qhMessageCount.setNewMessageNum(messageCount);
                    mFriendsCircleAdapter.setHeader(qhMessageCount);
                    mFriendsCircleAdapter.setDataItems(list);
                } else {
                    mFriendsCircleAdapter.setHeaderEnable(false);
                    mFriendsCircleAdapter.setDataItems(list);
                }
            }
        }
    }

    /**
     * 获取消息数量
     */
    private void getTrendsMessageCount() {
        String url = ServerAPI.ADDRESS + "friends/circle/getNewMessageNum.do";
        RequestManager.getInstance().sendGsonRequestAESforGET(url, QHMessageCount.class, new Response.Listener<QHMessageCount>() {
            @Override
            public void onResponse(QHMessageCount response) {
                messageCount = response.getNewMessageNum();
                registerFriendsCircleCountBroadcast();
                setDataToRecycleView();
                reload(DataLoader.MODE_REFRESH);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                messageCount = 0;
            }
        }, "getTrendsMessageCount", AppUtils.dynamicKey);

    }
}
