
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.CommentDetailsActivity;
import com.cmcc.hyapps.andyou.activity.RouteDetailActivity;
import com.cmcc.hyapps.andyou.activity.StrategyDetailActivity;
import com.cmcc.hyapps.andyou.adapter.GuiderMessageListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.Message;
import com.cmcc.hyapps.andyou.model.MessageReadResponse;
import com.cmcc.hyapps.andyou.model.QHToken;
import com.cmcc.hyapps.andyou.model.Scenic;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemSubViewClickListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiderMessageListFragment extends BaseFragment implements OnClickListener,DataLoaderCallback<Message.List> {
    private final String TAG = getClass().getName();//"GuiderListFragment";
    private final int REQUEST_CODE_SEARCH = 4;
    private ActionBar mActionBar;
    private GuiderMessageListAdapter mAdapter;
    private View mLoadingProgress;
    private View mReloadView;
    private View mEmptyHintView;
    private PullToRefreshRecyclerView mPullToRefreshView;
    private UrlListLoader<Message.List> mScenicListLoader;
//    private Request<HomeBanner.HomeBannerLists> mBannerRequest;
    private ViewGroup mRootView;
    private Scenic mScenic;
    private int HTTP_GET_PARAM_LIMIT = 10;
    private RecyclerView linesListView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareManager.getInstance().onStart(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.guide_list_fragment, container,false);
        mEmptyHintView = mRootView.findViewById(R.id.empty_hint_view);
        initViews();
        reload();
        loadMessage("北京");
        return mRootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        // hookSensorListener(!isHidden());
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageEnd(TAG);
        super.onPause();
        // hookSensorListener(false);
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
        linesListView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);
//        mAdapter.setHeader(null);
//        mAdapter.setDataItems(null);
        mScenicListLoader = null;
        RequestManager.getInstance().getRequestQueue().cancelAll(mRequestTag);
    }
    private void initViews() {
        initActionBar();
        initListView();
        initPullToRefresh();
        mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
        mReloadView = mRootView.findViewById(R.id.reload_view);
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
                    loadMessage("bj");
                }
            }
        });
    }

    private void initListView() {
        linesListView = (RecyclerView) mRootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linesListView.setLayoutManager(layoutManager);
        mAdapter = new GuiderMessageListAdapter(getActivity());
        ItemClickSupport clickSupport = ItemClickSupport.addTo(linesListView);
        clickSupport.setOnItemSubViewClickListener(new OnItemSubViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onItemClicked(view, position);
            }
        });
       /* int scap = ScreenUtils.dpToPxInt(getActivity(), 13);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(linesListView);
        linesListView.addItemDecoration(decor);*/
        linesListView.setItemAnimator(new DefaultItemAnimator());
     //   linesListView.setAdapter(mAdapter);
    }

    private void initActionBar() {
        mActionBar = (ActionBar) mRootView.findViewById(R.id.action_bar);
        mActionBar.getLeftView().setImageResource(R.drawable.return_back);
        mActionBar.getLeftView().setOnClickListener(this);
        mActionBar.getRightTextView().setTextColor(getActivity().getResources().getColor(R.color.title_bg));
        mActionBar.getTitleView().setTextColor(getActivity().getResources().getColor(R.color.white));
        mActionBar.setTitle("我的消息");

    }


    private void showReloadView() {
        linesListView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
    }


    private void loadMessage(String cityName) {
        Log.e("-------", "loadMessage--");
//        mLoadingProgress.setVisibility(View.VISIBLE);
//        linesListView.setVisibility(View.GONE);

        if (mScenicListLoader == null) {
            mScenicListLoader = new UrlListLoader<Message.List>( mRequestTag, Message.List.class);
            String  url;
            url = ServerAPI.User.buildMessageListUrl() ;
            mScenicListLoader.setUrl(url);
        }
        mScenicListLoader.loadMoreQHData(this, DataLoader.MODE_LOAD_MORE);
    }
    @Override
    public void onLoadFinished(Message.List list, int mode) {
        mPullToRefreshView.onRefreshComplete();
        onListLoaded(list);
    }

    @Override
    public void onLoadError(int mode) {
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        linesListView.setVisibility(View.VISIBLE);
    }

    private ArrayList<Message> messageList = new ArrayList();
    private void onListLoaded(Message.List data) {
//        Log.e("TAG",data.results.t);
        mLoadingProgress.setVisibility(View.GONE);
        linesListView.setVisibility(View.VISIBLE);
        if (data == null || data.list == null|| (mScenicListLoader != null && !mScenicListLoader.getPaginator().hasMorePages())) {
            // Disable PULL_FROM_END
//            mPullToRefreshView.setMode(Mode.PULL_FROM_END);
//            return;
        }


        List<Message> list = null;
        if (data != null && data.results != null && data.results.size() != 0) {
            list = data.results;
        }
        if (list == null || list.isEmpty()) {
            if (messageList.isEmpty()) {
                mEmptyHintView.setVisibility(View.VISIBLE);
            }else {
                ToastUtils.AvoidRepeatToastShow(getActivity(), R.string.msg_no_more_data, Toast.LENGTH_SHORT);
            }
        }
        if (data != null) {
            if (data.pagination != null && data.pagination.offset == 0) {
                mAdapter.setDataItems(null);
            }
            mAdapter.appendDataItems(data.results);
            messageList.addAll(data.results);
            linesListView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }
    private void onItemClicked(View v, int position) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            /*case R.id.is_select:
                if(mScenic.getSelect()){
                    mScenic.setSelect(false);
                }else {
                    mScenic.setSelect(true);
                }
                mAdapter.notifyDataSetChanged();
                break;*/
            case R.id.item_message:
                Message tag = (Message) v.getTag();
              //  先请求数据，指定为已读
                String url = ServerAPI.User.buildMessageReadUrl(tag.id + "");
                JSONObject json = new JSONObject();
                try {
                    json.put("read", 1);
                } catch (Exception e)
                {

                }


                RequestManager.getInstance().sendGsonRequest(Request.Method.POST, url, json.toString(),
                        MessageReadResponse.class, new Response.Listener<MessageReadResponse>() {
                            @Override
                            public void onResponse(MessageReadResponse response) {
                                Log.e(TAG, "post succeed");
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "post error");
                            }
                        }, "-----------");
                messageList.get(position).read = 1;
                mAdapter.notifyDataSetChanged();
                Log.e("是否点赞"+ url);
                Intent intent = new Intent();
                switch (tag.ctype){
                    case 2:
                        //攻略
                        Bundle bundle1 = new Bundle();
                        bundle1.putInt("id",tag.object_id);
                        intent.putExtra("guide", bundle1);
                        intent.putExtra("TAG",true);
                        intent.setClass(getActivity(), StrategyDetailActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        //路线
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("id",tag.object_id);
                        intent.putExtra("route", bundle2);
                        intent.putExtra("TAG",true);
                        intent.setClass(getActivity(), RouteDetailActivity.class);
                        startActivity(intent);
                        //路线
                        break;
                    case 4:
                        //评论
                        Comment comment = new Comment();
                        comment.id = tag.object_id;

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Const.EXTRA_COMMENT_DATA, comment);
                        bundle.putInt("id", comment.id);
                        intent.putExtras(bundle);
                        intent.putExtra("TAG", true);
                        intent.setClass(getActivity(), CommentDetailsActivity.class);
                        startActivity(intent);
                        break;
                }
            default:
                break;
        }
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
            case R.id.action_bar_right_text: {

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
    private void onHeaderClicked(View v) {
        switch (v.getId()) {
          }
    }
    @Override
    public void onDestroy() {
        ShareManager.getInstance().onEnd();
        super.onDestroy();
    }



}
