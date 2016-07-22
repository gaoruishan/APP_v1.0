package com.cmcc.hyapps.andyou.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.QHSearchAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.QHRoute;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.model.QHSearch;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.StringUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;
import com.cmcc.hyapps.andyou.widget.circularprogressbar.CircularProgressBar;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Edward on 2015/5/16.
 */
public class SearchStrategyListFragment extends BaseFragment implements /*DataLoader.DataLoaderCallback<QHScenic.QHScenicList>, */View.OnClickListener {

    private UrlListLoader<QHScenic.QHScenicList> mScenicListLoader;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private QHSearchAdapter mAdapter;
    private List<QHSearch> mSearch = new ArrayList<QHSearch>();
    private Gson mGson = new Gson();

    @InjectView(R.id.loading_progress)
    CircularProgressBar mLoadingProgress;
    @InjectView(R.id.search_content)
    EditText search_content;
    @InjectView(R.id.pulltorefresh_twowayview)
    PullToRefreshRecyclerView mPullToRefreshView;
    @InjectView(R.id.search_tv)
    TextView search_tv;
    @InjectView(R.id.empty_hint_view)
    View empty_hint_view;
    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = View.inflate(getActivity(), R.layout.activity_search_scenic, null);
        ButterKnife.inject(this, mRootView);
        initSearch();
        initPullToRefresh();
        initListView();
        return mRootView;
    }

    private void initSearch() {
        search_tv.setOnClickListener(this);
        search_content.setOnKeyListener(new View.OnKeyListener() {//输入完后按键盘上的搜索键【回车键改为了搜索键】

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {//修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(
                                    getActivity().getCurrentFocus()
                                            .getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    UploadRecyclerView();

                }
                return false;
            }
        });
    }

    private void UploadRecyclerView() {
        String condition = search_content.getText().toString();
        if (StringUtils.isEmpty(condition)){
            ToastUtils.show(getActivity(),R.string.reload_search_input);
        }else {
            if (StringUtils.isHasSpecialChar(condition)) {
                ToastUtils.AvoidRepeatToastShow(getActivity(),R.string.is_has_special_char,Toast.LENGTH_SHORT);
                return;
            }
            String url = ServerAPI.QHSearchList.buildSearchCommentUrl(condition);
            loadNearScenics(url);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoadingProgress.setVisibility(View.INVISIBLE);
        search_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    //pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        search_content.setFocusable(true);
        search_content.setFocusableInTouchMode(true);
        search_content.requestFocus();
//        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_tv:
                getActivity().finish();
                break;
        }
    }

    private void initPullToRefresh() {

        mPullToRefreshView = (PullToRefreshRecyclerView) mRootView.findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(Mode.DISABLED);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    reload();
                } else {
                    String url = ServerAPI.QHSearchList.URL;
                    loadNearScenics(url);
                }
            }
        });
    }

    private void reload() {
        mLoadingProgress.setVisibility(View.VISIBLE);
        String url = ServerAPI.QHSearchList.URL;
        loadNearScenics(url);
    }

    private void loadNearScenics(String url) {
        RequestQueue requestQueue = RequestManager.getInstance().getRequestQueue();
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("respond", "respond");
                mPullToRefreshView.onRefreshComplete();
                initResult(response);
                if(mSearch.isEmpty()){
                    empty_hint_view.setVisibility(View.VISIBLE);
                }else {
                    empty_hint_view.setVisibility(View.GONE);
                }
                mAdapter.addItem(mSearch);
                mAdapter.notifyDataSetChanged();
                mLoadingProgress.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("respond", "error");
                Toast.makeText(getActivity(),"加载数据失败",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    private void initResult(JSONArray response) {
        mSearch.clear();
        try {
            for (int i = 0; i < response.length(); i++) {
                String type = response.getJSONObject(i).getString("type");
//                if ("scenic".equals(type)) {
//                    JSONObject jsonObj = response.getJSONObject(i);
//                    QHScenic qhScenic = mGson.fromJson(jsonObj.toString(), QHScenic.class);
//                    QHSearch qhSearch = new QHSearch(type,qhScenic);
//                    mSearch.add(qhSearch);
//                }
                if ("guide".equals(type)) {
                    JSONObject jsonObj = response.getJSONObject(i);
                    QHStrategy qhStrategy = mGson.fromJson(jsonObj.toString(), QHStrategy.class);
                    QHSearch qhSearch = new QHSearch(type,qhStrategy);
                    mSearch.add(qhSearch);
                }
                if ("route".equals(type)) {
                    JSONObject jsonObj = response.getJSONObject(i);
                    QHRoute qhRoute = mGson.fromJson(jsonObj.toString(), QHRoute.class);
                    QHSearch qhSearch = new QHSearch(type,qhRoute);
                    mSearch.add(qhSearch);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initListView() {
        mRecyclerView = mPullToRefreshView.getRefreshableView();
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        int scap = ScreenUtils.dpToPxInt(getActivity(), 13);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        mAdapter = new QHSearchAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    protected void initData() {
        reload();
    }

}
