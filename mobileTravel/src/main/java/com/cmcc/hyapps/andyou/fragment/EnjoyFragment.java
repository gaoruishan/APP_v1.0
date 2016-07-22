package com.cmcc.hyapps.andyou.fragment;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.GsonRequest;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.AreaTag;
import com.cmcc.hyapps.andyou.model.Tag;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.ChoosePopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingbing on 2015/8/20.
 */
public class EnjoyFragment extends BaseFragment implements View.OnClickListener,ChoosePopupWindow.OnPopupWindowsClickListener {
    private View rootView;
    private final String TAG = "EnjoyFragment";
    private View mLoadingProgress;
    private EnjoyListFragment mEnjoyListFragment;
    private TextView mArea, mStyle;

    private GsonRequest<Tag.TagList> typeGsonRequest;
    private GsonRequest<AreaTag.AraeTagList> araeTagListGsonRequest;
    private ChoosePopupWindow chooseAreaPopupWindow,chooseStylePopupWindow;

//    private SparseArray areaSparseArray = new SparseArray<String>();
//    private SparseArray typeSparseArray = new SparseArray<String>();

    private List<String> areaLists = new ArrayList<String>();
    private List<Integer> area_id_Lists = new ArrayList<Integer>();
    private List<String> typeLists = new ArrayList<String>();
    private List<Integer> type_id_Lists = new ArrayList<Integer>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.enjoy_fragment_layout, container, false);
        initActionBar(rootView);
        initView();
        loadAreaTag();
        loadTypeTag();
        showEnjoyList();
        return rootView;
    }

    private void initView() {
        mLoadingProgress = rootView.findViewById(R.id.live_loading_progress);
        mLoadingProgress.setVisibility(View.GONE);

        mArea = (TextView) rootView.findViewById(R.id.filter_scope);
        mArea.setText(R.string.andyou_area);
        mStyle = (TextView) rootView.findViewById(R.id.filter_price);
        mStyle.setText(R.string.andyou_style);
        mArea.setOnClickListener(this);
        mStyle.setOnClickListener(this);
        initPopupWindow();
    }

    private void showEnjoyList() {
        EnjoyListFragment enjoyListFragment = new EnjoyListFragment();
     //   if (mLoadingProgress != null)
     //   enjoyListFragment.attachLoadingProgress(mLoadingProgress);
        Bundle bundle = new Bundle();
        bundle.putString(Const.ARGS_LOADER_URL, ServerAPI.BASE_URL + "entertainments/?format=json");
        enjoyListFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.enjoy_container, enjoyListFragment,TAG).commitAllowingStateLoss();
    }

    private void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.home_tab_enjoy);
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_left:
                getActivity().finish();
                break;
            case R.id.filter_scope:
                mArea.setTextColor(getResources().getColor(R.color.market_actionbar));
                chooseAreaPopupWindow.showPopupWindow(v);
                chooseAreaPopupWindow.setDrawableRight(mArea,true);
                break;
            case R.id.filter_price:
                mStyle.setTextColor(getResources().getColor(R.color.market_actionbar));
                chooseStylePopupWindow.showPopupWindow(v);
                chooseStylePopupWindow.setDrawableRight(mStyle, true);
                break;
        }
    }
    private void initPopupWindow(){
        chooseAreaPopupWindow  = new ChoosePopupWindow(getActivity(), areaLists,2);
        chooseAreaPopupWindow.setChoosedView(mArea);
        chooseAreaPopupWindow.setOnPopupWindowsClickListener(this);
        chooseStylePopupWindow = new ChoosePopupWindow(getActivity(),typeLists,2);
        chooseStylePopupWindow.setChoosedView(mStyle);
        chooseStylePopupWindow.setOnPopupWindowsClickListener(this);
    }

    @Override
    public void onPopupItemClick() {
        if (chooseAreaPopupWindow == null || chooseStylePopupWindow == null)
            return;
        chooseConditionToUploadRecyclerView(chooseAreaPopupWindow.getCurrentPosition(), chooseStylePopupWindow.getCurrentPosition());
    }

    private void chooseConditionToUploadRecyclerView(int areaPosition,int stylePosition){
        mEnjoyListFragment = (EnjoyListFragment) getFragmentManager().findFragmentByTag(TAG);
        if (area_id_Lists == null || type_id_Lists == null){
            return;
        }
        mEnjoyListFragment.loadList(setUrl(areaPosition,stylePosition));
        mEnjoyListFragment.clearList();
        mEnjoyListFragment.reload(DataLoader.MODE_REFRESH);
    }

    private String setUrl(int areaPosition,int stylePosition){
        String url = "";
        if (areaPosition == 0 && stylePosition == 0){
            url = ServerAPI.EnjoyList.URL;
        }else if (areaPosition != 0 && stylePosition == 0){
            url = ServerAPI.EnjoyList.buildEnjoyListUrlNoWithType(area_id_Lists.get(areaPosition));
        }else  if (areaPosition == 0 && stylePosition != 0){
            url = ServerAPI.EnjoyList.buildEnjoyListUrlNoWithArea(type_id_Lists.get(stylePosition));
        }else if (areaPosition != 0 && stylePosition !=0){
            url = ServerAPI.EnjoyList.buildEnjoyListUrl(area_id_Lists.get(areaPosition),type_id_Lists.get(stylePosition));
        }
        return  url;
    }

    private void loadAreaTag(){
        String url =ServerAPI.BASE_URL +"area/list/?area_type=0";
        araeTagListGsonRequest = RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url, AreaTag.AraeTagList.class, null, new Response.Listener<AreaTag.AraeTagList>() {
            @Override
            public void onResponse(AreaTag.AraeTagList response) {
                if (response != null && !response.results.isEmpty()){
//                        areaSparseArray.append(-1,"全部");
                        area_id_Lists.add(-1);
                        areaLists.add("全部");
                        for (AreaTag item: response.results ) {
//                            areaSparseArray.append(item.getId(), item.getName());
                            area_id_Lists.add(item.getId());
                            areaLists.add(item.getName());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(EnjoyFragment.this.getActivity(), R.string.load_road_condition_error);
            }
        },false,mRequestTag);
    }

    private void loadTypeTag(){
        String url =ServerAPI.BASE_URL +"/entertainments/types/";
        typeGsonRequest = RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url, Tag.TagList.class, null, new Response.Listener<Tag.TagList>() {
            @Override
            public void onResponse(Tag.TagList response) {
                if (response != null && !response.results.isEmpty()){
//                    typeSparseArray.append(-1, "全部");
                    type_id_Lists.add(-1);
                    typeLists.add("全部");
                    for (Tag item: response.results ) {
//                        typeSparseArray.append(item.id, item.name);
                        type_id_Lists.add(item.id);
                        typeLists.add(item.name);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   ToastUtils.show(EnjoyFragment.this.getActivity(), R.string.load_road_condition_error);
            }
        },false,mRequestTag);
    }
}
