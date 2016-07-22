
package com.cmcc.hyapps.andyou.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.cmcc.hyapps.andyou.model.AreaTreeTag;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.LandScapeScrollerView;
import com.cmcc.hyapps.andyou.widget.VerticalScapeScrollerView;
import com.umeng.analytics.MobclickAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.LiveVideoListAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RoadConditionFragment extends BaseFragment implements OnClickListener, VerticalScapeScrollerView.OnItemClickListener {
    private final String TAG = "RoadConditionFragment";
    private View mLoadingProgress;
    private VerticalScapeScrollerView landSpaceView;
    private GsonRequest<AreaTag.AraeTagList> araeTagListGsonRequest;
    private RoadConditionListFragment liveFragment;
    private List<AreaTag> areaList;
    private List<AreaTreeTag> areaTreeList;
    private List<String> areaStringList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.road_live_fragment, container, false);
        initActionBar(rootView);
        landSpaceView = (VerticalScapeScrollerView) rootView.findViewById(R.id.road_tab_ver);
        landSpaceView.setFirst(0);
        landSpaceView.setOnItemClick(this);
        mLoadingProgress = rootView.findViewById(R.id.live_loading_progress);
        mLoadingProgress.setVisibility(View.VISIBLE);
//        loadAreaTag();
        loadAreaTreeTag();
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

    private void showVideoList() {
        liveFragment = new RoadConditionListFragment();
        liveFragment.attachLoadingProgress(mLoadingProgress);
        Bundle args = new Bundle();
        args.putString(Const.ARGS_LOADER_URL, ServerAPI.BASE_URL + "traffic/videos/");
        liveFragment.setArguments(args);
        getFragmentManager().beginTransaction().add(R.id.live_container, liveFragment, TAG).commitAllowingStateLoss();
    }

    private void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.home_tab_road_condition);
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
            case R.id.action_bar_left:
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onItemOnclick(View view) {
        int position = (Integer) view.getTag();
        if (position == 0) {
            chooseConditionToRefreshAllData();
        } else {
            if (ids != null && ids.size() != 0) {
                int id = ids.get(position);
                if (id != -1) {
                    chooseConditionToRefreshData(id);
                }
            }
        }
    }

    private void loadAreaTag() {
        String url = ServerAPI.BASE_URL + "area/list/?area_type=1";
        araeTagListGsonRequest = RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url, AreaTag.AraeTagList.class, null, new Response.Listener<AreaTag.AraeTagList>() {
            @Override
            public void onResponse(AreaTag.AraeTagList response) {
                if (response != null && !response.results.isEmpty()) {
                    areaList = response.results;
                    areaStringList = new ArrayList<String>();
                    areaStringList.add("全部");
                    for (AreaTag item : response.results) {
                        areaStringList.add(item.getName());
                    }
                    areaStringList.add(1, "西宁市");
                    areaStringList.add(6, "海东市");
                    AreaTag allAreaTag = new AreaTag(0, "全部");
                    AreaTag areaTag1 = new AreaTag(1, "西宁市");
                    AreaTag areaTag2 = new AreaTag(6, "海东市");
                    areaList.add(0, allAreaTag);
                    areaList.add(1, areaTag1);
                    areaList.add(6, areaTag2);
//                    landSpaceView.addInnerView(areaStringList, RoadConditionFragment.this
//                            .getActivity());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(RoadConditionFragment.this.getActivity(), R.string.load_road_condition_error);
            }
        }, false, mRequestTag);
//          areaStringList = new ArrayList<String>();
//          areaStringList.add("全部");
//          areaStringList.add("西宁市");
//          areaStringList.add("城中区");
//          areaStringList.add("城西区");
//          areaStringList.add("城北区");
//          areaStringList.add("城东区");
//          areaStringList.add("海东市");
//          areaStringList.add("平安区");
//          landSpaceView.addInnerView(areaStringList, RoadConditionFragment.this
//                                .getActivity());
    }

    private LinkedHashMap<String, Boolean> linkedHashMap = new LinkedHashMap<String, Boolean>();
    private List<Integer> ids = new ArrayList<Integer>();

    private void loadAreaTreeTag() {
        String url = ServerAPI.BASE_URL + "area/tree/?clearNoChild=0";
        RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url, AreaTreeTag.AraeTreeTagList.class, null, new Response.Listener<AreaTreeTag.AraeTreeTagList>() {
            @Override
            public void onResponse(AreaTreeTag.AraeTreeTagList response) {
                if (response != null && !response.results.isEmpty()) {
                    areaTreeList = response.results;
                    linkedHashMap.put("全部", false);
                    //表示全部
                    ids.add(-1);
                    for (AreaTreeTag item : response.results) {
                        linkedHashMap.put(item.getName(), true);
                        ids.add(item.getId());
                        if (item.getChildren() != null && item.getChildren().size() != 0) {
                            for (AreaTreeTag child : item.getChildren()) {
                                linkedHashMap.put(child.getName(), false);
                                ids.add(child.getId());
                            }
                        }
                    }
                    landSpaceView.addInnerView(linkedHashMap, RoadConditionFragment.this
                            .getActivity());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(RoadConditionFragment.this.getActivity(), R.string.load_road_condition_error);
            }
        }, false, mRequestTag);
    }

    private void chooseConditionToRefreshData(int areaID) {
        liveFragment = (RoadConditionListFragment) getFragmentManager().findFragmentByTag(TAG);
        liveFragment.loadList(ServerAPI.BASE_URL + "traffic/videos/?area_id=" + areaID);
        liveFragment.reload(DataLoader.MODE_REFRESH);
    }

    private void chooseConditionToRefreshAllData() {
        liveFragment = (RoadConditionListFragment) getFragmentManager().findFragmentByTag(TAG);
        liveFragment.loadList(ServerAPI.BASE_URL + "traffic/videos/");
        liveFragment.reload(DataLoader.MODE_REFRESH);
    }
}
