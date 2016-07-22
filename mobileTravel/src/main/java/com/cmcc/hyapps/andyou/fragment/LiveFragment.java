
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.GsonRequest;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.AreaTag;
import com.cmcc.hyapps.andyou.model.Tag;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ChooseMultiPopupWindow;
import com.cmcc.hyapps.andyou.widget.ChoosePopupWindow;
import com.umeng.analytics.MobclickAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.IndexActivity;
import com.cmcc.hyapps.andyou.activity.SearchActivity;
import com.cmcc.hyapps.andyou.activity.VideoUploadActivity;
import com.cmcc.hyapps.andyou.adapter.LiveVideoListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.data.LocationDetector;
import com.cmcc.hyapps.andyou.data.LocationDetector.LocationListener;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.NetUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.List;

public class LiveFragment extends BaseFragment implements OnClickListener,ChoosePopupWindow.OnPopupWindowsClickListener,ChooseMultiPopupWindow.OnPopupWindowsMultiSelectListener {
    private final String TAG = "LiveFragment";
    private View rootView;
    private View mLoadingProgress;
    private final int REQUEST_CODE_TAKE_VIDEO = 1;

    private GsonRequest<Tag.TagList> scenicGsonRequest;
    private GsonRequest<AreaTag.AraeTagList> araeTagListGsonRequest;

    private TextView mArea, mStyle;

//    private SparseArray areaSparseArray = new SparseArray<String>();
    private SparseArray scenicSparseArray = new SparseArray<String>();

    private List<String> areaLists = new ArrayList<String>();
    private List<String> scenicLists = new ArrayList<String>();

    private List<Integer> area_id_Lists = new ArrayList<Integer>();

    private ChoosePopupWindow chooseAreaPopupWindow;
    private ChooseMultiPopupWindow chooseScenicMultiPopupWindow;

    private LiveVideoListFragment liveFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_live, container, false);
        initView();
        initActionBar(rootView);
        mLoadingProgress = rootView.findViewById(R.id.live_loading_progress);
        mLoadingProgress.setVisibility(View.VISIBLE);
        loadAreaTag();
        loadScenicTag();
        showVideoList();
        return rootView;
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

    private void showVideoList() {
        liveFragment = LiveVideoListFragment.newInstance(LinearLayoutManager.class, LiveVideoListAdapter.class);
        liveFragment.attachLoadingProgress(mLoadingProgress);
        Bundle args = new Bundle();
        args.putString(Const.ARGS_LOADER_URL, ServerAPI.BASE_URL + "videos/?format=json");
        liveFragment.setArguments(args);
        getFragmentManager().beginTransaction().add(R.id.live_container, liveFragment, TAG).commitAllowingStateLoss();
    }

    private void initView() {
        mLoadingProgress = rootView.findViewById(R.id.live_loading_progress);
        mLoadingProgress.setVisibility(View.GONE);

        mArea = (TextView) rootView.findViewById(R.id.filter_scope);
        mArea.setText(R.string.andyou_area);
        mStyle = (TextView) rootView.findViewById(R.id.filter_price);
        mStyle.setText(R.string.andyou_scenic_tag);
        mArea.setOnClickListener(this);
        mStyle.setOnClickListener(this);
        initPopupWindow();
    }

    private void initPopupWindow() {
        chooseAreaPopupWindow = new ChoosePopupWindow(getActivity(), areaLists, 2);
        chooseAreaPopupWindow.setChoosedView(mArea);
        chooseAreaPopupWindow.setOnPopupWindowsClickListener(this);
        chooseScenicMultiPopupWindow = new ChooseMultiPopupWindow(getActivity(), scenicLists);
        chooseScenicMultiPopupWindow.setChoosedView(mStyle);
        chooseScenicMultiPopupWindow.setOnPopupWindowsClickListener(this);
    }

    private void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.home_tab_scenic_360);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_right: {
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_LIVE_SEARCH);
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra(Const.EXTRA_SEARCH_TYPE, SearchActivity.SEARCH_VIDEO);
                getActivity().startActivity(intent);
                break;
            }

            case R.id.action_bar_right2: {
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_LIVE_VIDEO);
                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_TAKE_VIDEO)) {
                    startUploadActivity();
                }
                break;
            }
            case R.id.action_bar_left:
                getActivity().finish();
                break;
            case R.id.filter_scope:
                mArea.setTextColor(getResources().getColor(R.color.market_actionbar));
                chooseAreaPopupWindow.showPopupWindow(v);
                chooseAreaPopupWindow.setDrawableRight(mArea, true);
                break;
            case R.id.filter_price:
                mStyle.setTextColor(getResources().getColor(R.color.market_actionbar));
                chooseScenicMultiPopupWindow.showPopupWindow(v);
                chooseScenicMultiPopupWindow.setDrawableRight(mStyle, true);
                break;
        }
    }

    private void startUploadActivity() {
        Intent intent = new Intent(getActivity(), VideoUploadActivity.class);
        // FIXME
        intent.putExtra(Const.EXTRA_ID, 6);
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPopupItemClick() {
        chooseConditionToRefreshData(chooseAreaPopupWindow.getCurrentPosition(), chooseScenicMultiPopupWindow.getSelectPosition());
    }

    @Override
    public void onPopupMultiSelectClick() {
        chooseConditionToRefreshData(chooseAreaPopupWindow.getCurrentPosition(),chooseScenicMultiPopupWindow.getSelectPosition());
    }

    private void loadAreaTag() {
        String url = ServerAPI.BASE_URL + "area/list/?area_type=0";
        araeTagListGsonRequest = RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url, AreaTag.AraeTagList.class, null, new Response.Listener<AreaTag.AraeTagList>() {
            @Override
            public void onResponse(AreaTag.AraeTagList response) {
                if (response != null && !response.results.isEmpty()) {
                    List<AreaTag> list = response.results;
//                    areaSparseArray.append(-1,"全部");
                    area_id_Lists.add(-1);
                    areaLists.add("全部");
                    for (AreaTag item : list) {
//                        areaSparseArray.append(item.getId(), item.getName());
                        area_id_Lists.add(item.getId());
                        areaLists.add(item.getName());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(LiveFragment.this.getActivity(), R.string.load_road_condition_error);
            }
        }, false, mRequestTag);
    }

    private void loadScenicTag() {
        String url = ServerAPI.BASE_URL + "tags/?limit=30";
        scenicGsonRequest = RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url, Tag.TagList.class, null, new Response.Listener<Tag.TagList>() {
            @Override
            public void onResponse(Tag.TagList response) {
                if (response != null && !response.results.isEmpty()) {
                    scenicSparseArray.append(-1,"全部");
                    scenicLists.add("全部");
                    for (Tag item : response.results) {
                        scenicSparseArray.append(item.id, item.name);
                        scenicLists.add(item.name);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                   ToastUtils.show(LiveFragment.this.getActivity(), R.string.load_road_scenic_tag_error);
            }
        }, false, mRequestTag);
    }

    private void chooseConditionToRefreshData(int areaPosition, SparseBooleanArray scenicSparseBooleanArray) {
        liveFragment = (LiveVideoListFragment) getFragmentManager().findFragmentByTag(TAG);
        liveFragment.loadList(setUrl(areaPosition,scenicSparseBooleanArray));
        liveFragment.clearList();
        liveFragment.reload(DataLoader.MODE_REFRESH);
    }

    private String setUrl(int areaPosition, SparseBooleanArray scenicSparseBooleanArray) {
        String url = "";
        if (areaPosition == 0 && (isAllFalse(scenicSparseBooleanArray) || scenicSparseBooleanArray.size() == 0)) {
            url = ServerAPI.BASE_URL + "videos/?format=json";
        } else if (areaPosition != 0 && (isAllFalse(scenicSparseBooleanArray) || scenicSparseBooleanArray.size() == 0)) {
            url = ServerAPI.BASE_URL + "videos/?area_id=" + area_id_Lists.get(areaPosition);
        } else if (areaPosition != 0 &&  !isAllFalse(scenicSparseBooleanArray) && scenicSparseBooleanArray.size() != 0) {
            url = ServerAPI.BASE_URL + "videos/?area_id=" + area_id_Lists.get(areaPosition) + "&" + setScenicTag(scenicSparseBooleanArray);
        } else if (areaPosition == 0 && !isAllFalse(scenicSparseBooleanArray) && scenicSparseBooleanArray.size() != 0) {
            url = ServerAPI.BASE_URL + "videos/?" + setScenicTag(scenicSparseBooleanArray);
        }
        return url;
    }

    private String setScenicTag(SparseBooleanArray scenicSparseBooleanArray) {
        String url = "tag_id=";

        for (int i = 0; i < scenicSparseBooleanArray.size(); i++) {
            if (scenicSparseBooleanArray.valueAt(i)){
//                if (i == scenicSparseBooleanArray.size() - 1 )
//                    url += scenicSparseArray.keyAt(scenicSparseBooleanArray.keyAt(i));
//                else
                    url += scenicSparseArray.keyAt(scenicSparseBooleanArray.keyAt(i)) + ",";
            }
        }
        return url.substring(0,url.length()-1);
    }

    private boolean isAllFalse(SparseBooleanArray scenicSparseBooleanArray) {
        for (int i = 0; i < scenicSparseBooleanArray.size(); i++) {
            if (i==0 && scenicSparseBooleanArray.valueAt(i))
                return  true;
            if (scenicSparseBooleanArray.valueAt(i)) {
                return false;
            }
        }
        return true;
    }
}
