
package com.cmcc.hyapps.andyou.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.BasicScenicDataListAdapter;
import com.cmcc.hyapps.andyou.adapter.ItineraryScenicDataListAdapterImp;
import com.cmcc.hyapps.andyou.model.BasicScenicData;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.Map;

public class ItineraryScenicDataListFragmentImp extends BasicScenicDataListFragment {

    @Override
    public BasicScenicDataListAdapter initAdapter() {
        return new ItineraryScenicDataListAdapterImp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_recycler_list, container, false);
        mEmptyHintView = rootView.findViewById(R.id.empty_hint_view);
        mLoadingProgress = rootView.findViewById(R.id.loading_progress);
        mPullToRefreshView = (PullToRefreshRecyclerView) rootView.findViewById(R.id.pulltorefresh_twowayview);
        mSpotsRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mSpotsRecyclerView.setLayoutManager(layoutManager);
        mSpotsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSpotsRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    public Map<String, BasicScenicData> getSelected() {
        return ((ItineraryScenicDataListAdapterImp) mAdapter).getSelected();
    }

    public void setThreshold(int threshold) {
        if (mAdapter != null) {
            mAdapter.setThreshold(threshold);
        }
    }
}
