/**
 *
 */

package com.kuloud.android.widget.recyclerview;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract RecyclerView.Adapter for add header view to the list.
 * 
 * @author kuloud
 */
public abstract class BaseHeaderAdapter<Header, T> extends Adapter<ViewHolder> {
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_CREATE_ITEM = 2;
    public static final int TYPE_BIND_ITEM = 3;
    protected Header mHeader;
    protected List<T> mDataItems;

    public boolean isHeaderEnable() {
        return mHeaderEnable;
    }

    protected boolean mHeaderEnable;

    public BaseHeaderAdapter() {
        this(true);
    }

    public BaseHeaderAdapter(boolean headerEnable) {
        mDataItems = new ArrayList<T>();
        mHeaderEnable = headerEnable;
        if (headerEnable) {
            mDataItems.add(0, null);
        }
    }

    public void setHeader(Header header) {
        if (!mHeaderEnable) {
            // throw new
            // IllegalAccessException("can't set header in none header mode");
            Log.w("kuloud", "can't set header in none header mode");
            return;
        }

        mHeader = header;
        notifyItemChanged(0);
    }

    public Header getBaseHeader(){
        return  mHeader;
    }

    /**
     * @return the mDataItems
     */
    public List<T> getDataItems() {
        List<T> dataItems = mHeaderEnable ? mDataItems.subList(1, mDataItems.size()) : mDataItems;
        return dataItems;
    }

    /**
     * @param dataItems the data items to set
     */
    public void setDataItems(List<T> dataItems) {
        if (dataItems == null) {
            dataItems = new ArrayList<T>();
        }
        mDataItems = dataItems;
        if (mHeaderEnable) {
            mDataItems.add(0, null);
            notifyItemRangeChanged(1, dataItems.size());
        } else {
            notifyDataSetChanged();
        }

    }
    public void setDataItemsNoHeader(List<T> dataItems) {
        if (dataItems == null) {
            dataItems = new ArrayList<T>();
        }
        mDataItems = dataItems;
        notifyDataSetChanged();
    }

    /**
     * @param dataItems the data items to append behind
     */
    public void appendDataItems(List<T> dataItems) {
        if (dataItems == null || dataItems.isEmpty()) {
            return;
        }
        int positionStart = mDataItems.size();
        mDataItems.addAll(dataItems);
        notifyItemRangeInserted(positionStart, dataItems.size());
    }

    public void instertDataItemsAhead(List<T> dataItems) {
        if (dataItems == null || dataItems.isEmpty()) {
            return;
        }
        final int AHEAD_OFFSET = mHeaderEnable ? 1 : 0;
        mDataItems.addAll(AHEAD_OFFSET, dataItems);
        notifyItemRangeInserted(AHEAD_OFFSET, dataItems.size());
    }

    public abstract ViewHolder onCreateHeaderViewHolder(ViewGroup parent);

    public abstract void onBinderHeaderViewHolder(ViewHolder holder);

    public abstract ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    public abstract void onBinderItemViewHolder(ViewHolder holder, int position);

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return onCreateHeaderViewHolder(parent);
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0 && mHeaderEnable) {
            if (mHeader != null) {
                onBinderHeaderViewHolder(holder);
            }
            return;
        }
        onBinderItemViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mHeaderEnable) {
            return TYPE_HEADER;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        if(mHeaderEnable){
            return mDataItems.size();
        }else{
            //原来为mDataItems.size()-1   是的设置头部为false后，显示少了一个
            return mDataItems.size();
        }

    }

    public void setHeaderEnable(boolean enable){
        mHeaderEnable = enable;
    }
}
