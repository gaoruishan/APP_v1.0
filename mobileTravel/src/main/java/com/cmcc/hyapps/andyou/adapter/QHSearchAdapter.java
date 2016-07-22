package com.cmcc.hyapps.andyou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.cmcc.hyapps.andyou.adapter.row.QHRowEnjoy;
import com.cmcc.hyapps.andyou.adapter.row.QHRowGuide;
import com.cmcc.hyapps.andyou.adapter.row.QHRowRoute;
import com.cmcc.hyapps.andyou.adapter.row.QHRowScenic;
import com.cmcc.hyapps.andyou.model.QHSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/16.
 */
public class QHSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;

    public static final int ITEM_SCENIC = 0;
    public static final int ITEM_GUIDE = 1;
    public static final int ITEM_ROUTE = 2;
    public static final int ITEM_NONE = 3;
    public static final int ITEM_ENJOY = 4;

    List<QHSearch> search = new ArrayList<QHSearch>();

    public QHSearchAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case ITEM_SCENIC:
                viewHolder = QHRowScenic.onCreateViewHolder(parent);
                break;
            case ITEM_GUIDE:
                viewHolder = QHRowGuide.onCreateViewHolder(parent);
                break;
            case ITEM_ROUTE:
                viewHolder = QHRowRoute.onCreateViewHolder(parent);
                break;
            case ITEM_ENJOY:
                viewHolder = QHRowEnjoy.onCreateViewHolder(parent);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String type = search.get(position).type;
        if("scenic".equals(type)){
            QHRowScenic.onBindViewHolder(context,holder,position,search.get(position).qhScenic);
        }
        if("route".equals(type)){
            QHRowRoute.onBindViewHolder(context,holder,position,search.get(position).qhRoute);
        }
        if("guide".equals(type)){
            QHRowGuide.onBindViewHolder(context,holder,position,search.get(position).qhStrategy);
        }
        if("entertainment".equals(type)){
            QHRowEnjoy.onBindViewHolder(context, holder, position, search.get(position).qhEnjoy);
        }
    }

    @Override
    public int getItemCount() {
        return search.size();
    }

    @Override
    public int getItemViewType(int position) {
        String type = search.get(position).type;
        if("scenic".equals(type)){
            return ITEM_SCENIC;
        }
        if("route".equals(type)){
            return ITEM_ROUTE;
        }
        if("guide".equals(type)){
            return ITEM_GUIDE;
        }
        if("entertainment".equals(type)){
            return ITEM_ENJOY;
        }
        return ITEM_NONE;
    }

    public void addItem(List<QHSearch> search){
        this.search = search;
    }
}
