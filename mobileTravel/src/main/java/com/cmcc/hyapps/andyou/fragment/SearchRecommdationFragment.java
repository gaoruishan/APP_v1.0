
package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemClickListener;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.SearchActivity;
import com.cmcc.hyapps.andyou.adapter.SearchAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Scenic;
import com.cmcc.hyapps.andyou.model.Scenic.ScenicList;
import com.cmcc.hyapps.andyou.util.LocationUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;

public class SearchRecommdationFragment extends BaseFragment {

    private static final int HTTP_GET_PARAM_LIMIT = 6;
    private SearchActivity mActivity;
    private RecyclerView mHotScenics;

    @Override
    public void onAttach(Activity activity) {
        mActivity = (SearchActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        loadHotScenics();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_recommdation, container, false);
        mHotScenics = (RecyclerView) rootView.findViewById(R.id.hot_scenics);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity()
                .getApplicationContext(), 3);
        mHotScenics.setLayoutManager(layoutManager);
        mHotScenics.setItemAnimator(new DefaultItemAnimator());
        int scap = ScreenUtils.getDimenPx(getActivity(), R.dimen.scenic_image_spacing);
        DividerItemDecoration decor = new DividerItemDecoration(scap, scap);
        decor.initWithRecyclerView(mHotScenics);
        mHotScenics.addItemDecoration(decor);
        mHotScenics.setAdapter(new SearchAdapter());

        ItemClickSupport clickSupport = ItemClickSupport.addTo(mHotScenics);
        clickSupport.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                if (mActivity != null && !mActivity.isFinishing()) {
                    Scenic scenic = (Scenic) view.getTag();
                    if (scenic == null) {
                        return;
                    }
                    mActivity.onScenicSelected(scenic);
                }
            }
        });
        return rootView;
    }

    private void loadHotScenics() {
        final String url = ServerAPI.ScenicList
                .buildUrl(ServerAPI.ScenicList.Type.HOT,
                        LocationUtils.getLastKnownLocation(getActivity()), HTTP_GET_PARAM_LIMIT,
                        0);
        RequestManager.getInstance().sendGsonRequest(url, ScenicList.class,
                new Response.Listener<ScenicList>() {
                    @Override
                    public void onResponse(ScenicList scenicList) {
                        Log.d("onResponse, ScenicList=%s", scenicList);
                        if (scenicList == null || scenicList.list == null
                                || scenicList.list.isEmpty()) {
                            // TODO

                            mHotScenics.setVisibility(View.GONE);
                            return;
                        }

                        onHotScenicListLoaded(scenicList);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse");
                    }
                }, this/* TODO */);
    }

    private void onHotScenicListLoaded(ScenicList scenicList) {
        ((SearchAdapter) mHotScenics.getAdapter()).setSpotList(scenicList.list);
    }

}
