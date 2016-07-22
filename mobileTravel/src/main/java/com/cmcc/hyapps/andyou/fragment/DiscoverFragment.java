
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.SearchListActivity;
import com.cmcc.hyapps.andyou.activity.SecnicActivity;
import com.cmcc.hyapps.andyou.activity.VideoUploadActivity;
import com.cmcc.hyapps.andyou.adapter.BannerPagerAdapter;
import com.cmcc.hyapps.andyou.adapter.DiscoverAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.GsonRequest;
import com.cmcc.hyapps.andyou.data.LocationDetector;
import com.cmcc.hyapps.andyou.data.OfflinePackageManager;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.model.Scenic;
import com.cmcc.hyapps.andyou.model.ScenicDetails;
import com.cmcc.hyapps.andyou.model.Tag;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.umeng.analytics.MobclickAgent;

import static com.cmcc.hyapps.andyou.data.DataLoader.MODE_REFRESH;

public class DiscoverFragment extends ServiceBaseFragment implements OnClickListener,DataLoader.DataLoaderCallback<QHScenic.QHScenicList> {
    private final String TAG = "DiscoverFragment";
    private UrlListLoader<Scenic.ScenicList> mScenicListLoader;
    private UrlListLoader<QHScenic.QHScenicList> mScenicQHListLoader;
    private View mLoadingProgress;
    private View mReloadView;
    private static View empty_hint_view;
    private PullToRefreshRecyclerView mPullToRefreshView;
    private final int REQUEST_CODE_TAKE_VIDEO = 1;
    private View rootView;
    private int mId = -1;
    private OfflinePackageManager mOfflineManager;
    private LocationDetector mLocationDetector;
    private DiscoverAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getArguments().getInt(Const.EXTRA_ID);
        mOfflineManager = OfflinePackageManager.getInstance();
        mLocationDetector = new LocationDetector(getActivity().getApplicationContext());
//        mAdapter=new DiscoverListAdapter(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discover, container, false);
        initActionBar(rootView);
        initViews();
        loadDiscoverBanner();//tou dianj
        return rootView;
    }
    private  void  initViews(){
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new DiscoverAdapter(getActivity(), new BannerPagerAdapter.IActionCallback<Tag>() {
            @Override
            public void doAction(Tag data) {
//                ToastUtils.show(getActivity(),"hhhh");
            }
        });
        ItemClickSupport clickSupport = ItemClickSupport.addTo(mRecyclerView);
        clickSupport.setOnItemSubViewClickListener(new ItemClickSupport.OnItemSubViewClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    onHeaderClicked(view);
                } else {
                    onItemClicked(view);
                }
            }
        });
        int scap = ScreenUtils.dpToPxInt(getActivity(), 10);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.setAdapter(mAdapter);

        mPullToRefreshView = (PullToRefreshRecyclerView)rootView.findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(PullToRefreshBase.Mode.DISABLED);

        mLoadingProgress = rootView.findViewById(R.id.loading_progress);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView = rootView.findViewById(R.id.reload_view);
        empty_hint_view = rootView.findViewById(R.id.empty_hint_view);

        mReloadView.setOnClickListener(this);

    }
    public  static DiscoverFragment discoverFragment=new DiscoverFragment();
    public static DiscoverFragment getIntstance(){
        return discoverFragment;
    }
    public void setShowEmpty_hint_view(){
        empty_hint_view.setVisibility(View.VISIBLE);
    }
    public void setHideEmpty_hint_view(){
        empty_hint_view.setVisibility(View.GONE);
    }
    private void onItemClicked(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }

        mScenic = (QHScenic) v.getTag();
        if (mScenic == null) {
            Log.e("NULL mScenic");
            return;
        }

        switch (v.getId()) {
            case R.id.rl_discover_item:
                jumpScenicDetail();
                break;
            default:
                break;
        }
    }

    private void jumpScenicDetail() {
        MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_HOME_SECNIC);
        Intent intent = new Intent(getActivity(), SecnicActivity.class);
        int mId = mScenic.id;
        intent.putExtra(Const.QH_SECNIC,mScenic);
        intent.putExtra(Const.QH_SECNIC_ID,mId);
        startActivity(intent);
    }

    private void onHeaderClicked(View v) {
       
    }
    

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageEnd(TAG);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_VIDEO:
                    startUploadActivity();
                    break;
                default:
                    break;
            }
        }
    }
    private void loadNearScenics(int mode) {
        if (mScenicQHListLoader == null) {
            mScenicQHListLoader = new UrlListLoader<QHScenic.QHScenicList>(mRequestTag, QHScenic.QHScenicList.class);
            String url = ServerAPI.BASE_URL + "scenics/?limit=9999&format=json";//limit=9999&
//            mScenicListLoader.setUseCache(true);
            mScenicQHListLoader.setUrl(url);
        }
        mScenicQHListLoader.loadMoreQHData(this, mode);
    }

    @Override
    public void onLoadFinished(QHScenic.QHScenicList list, int mode) {
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setHeader(tags);
        onListLoaded(list, mode);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoadError(int mode) {
        mPullToRefreshView.onRefreshComplete();
        mLoadingProgress.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mReloadView.setVisibility(View.VISIBLE);
    }

    private void onListLoaded(QHScenic.QHScenicList data,int mode) {
        if(mode== MODE_REFRESH){
            mAdapter.setDataItems(data.results);
            mAdapter.setSaveTemp(data.results);
            return;
        }
    }
    private void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.bottom_tab_title_live);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_search_selecter);
        actionBar.getRightView().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_right: {
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_LIVE_SEARCH);
                Intent intent = new Intent(getActivity(), SearchListActivity.class);
                intent.putExtra(Const.EXTRA_TYPE, Const.EXTRA_SCENIC_DATA);
                startActivity(intent);
                break;
            }
            case R.id.reload_view: {
                reload();
                break;
            }
        }
    }
    private void reload() {
        //mPullToRefreshView.setMode(PullToRefreshBase.Mode.DISABLED);
        mRecyclerView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);
        mAdapter.setHeader(null);
        mAdapter.setDataItems(null);
        //  mScenicListLoader = null;
        RequestManager.getInstance().getRequestQueue().cancelAll(mRequestTag);
//        if (mRandomLoad) {
////            loadDiscoverBanner();
//            return;
//        }
        if (mId > 0) {
            ScenicDetails offlineData = mOfflineManager.getOfflineData(mId, ScenicDetails.class);
            if (offlineData != null) {
                Log.d("Using offline package data for scenic %d", mId);
               // onScenicDetailsLoaded(offlineData);
            } else {
                loadDiscoverBanner();
            }
        } else {
            loadDiscoverBanner();
        }
        //mLocationDetector.detectLocation(mLocationListener, true, true);
    }
    private void startUploadActivity() {
        Intent intent = new Intent(getActivity(), VideoUploadActivity.class);
        // FIXME
        intent.putExtra(Const.EXTRA_ID, 6);
        getActivity().startActivity(intent);
    }
    Tag.TagList tags;
    private RecyclerView mRecyclerView;
    private GsonRequest<Tag.TagList> mBannerRequest;


    private void loadDiscoverBanner() {
        final String url= ServerAPI.BASE_URL + "tags/?limit=30&format=json";
        Log.e("Loading scenic details from %s", url);
        mBannerRequest = RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url,
                Tag.TagList.class, null,
                new Response.Listener<Tag.TagList>() {
                    @Override
                    public void onResponse(Tag.TagList response) {
                        mPullToRefreshView.onRefreshComplete();
                      //  mLoadingProgress.setVisibility(View.GONE);
                        Log.e("loadDiscoverBanner, ScenicDetails=%s,mAdapter.size=%d", response, mAdapter.getDataItems().size());
//                        mRandomLoad = false;
                       // mAdapter.setDataItems(null);
                         tags= response;
                        //mAdapter.setHeader(response);
//                        mAdapter.setMyLocation(mLocation);
                        //mRecyclerView.setVisibility(View.VISIBLE);
                        mAdapter.notifyDataSetChanged();
                        loadNearScenics(MODE_REFRESH);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error loading scenic details from %s", url);
                        showReloadView();
                        mPullToRefreshView.onRefreshComplete();
                    }
                }, false, mRequestTag);

    }
    private void showReloadView() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
    }
}
