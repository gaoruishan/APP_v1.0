
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View.OnClickListener;

import com.cmcc.hyapps.andyou.model.BasicScenicData;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicScenicDataListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<BasicScenicData> mDataItems = new ArrayList<BasicScenicData>();
    protected OnClickListener mItemClickListener;
    protected int selectedThrehold;

    public BasicScenicDataListAdapter() {
    }

    public BasicScenicDataListAdapter(List<BasicScenicData> items) {
        this.mDataItems = items;
    }

    public void addSpotList(List<BasicScenicData> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        if (mDataItems.isEmpty()) {
            mDataItems = items;
            notifyDataSetChanged();
        } else {
            int positionStart = mDataItems.size();
            mDataItems.addAll(items);
            notifyItemRangeInserted(positionStart, items.size());
        }
    }

    public void setOnItemClickListener(OnClickListener l) {
        mItemClickListener = l;
    }

    @Override
    public int getItemCount() {
        return mDataItems == null ? 0 : mDataItems.size();
    }

    public void setThreshold(int threshold) {
        selectedThrehold = threshold;
    }
}
