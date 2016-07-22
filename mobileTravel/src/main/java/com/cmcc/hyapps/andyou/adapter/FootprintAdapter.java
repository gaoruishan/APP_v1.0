
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Footprint;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class FootprintAdapter extends Adapter<FootprintAdapter.ViewHolder> {
    private final String DATE_FORMAT = "yyyy-M-d k:m";

    private List<Footprint> mDataItems = new ArrayList<Footprint>();

    public FootprintAdapter() {
    }

    public FootprintAdapter(List<Footprint> items) {
        if (items == null) {
            return;
        }
        this.mDataItems = items;
    }

    public void appendDataItems(List<Footprint> items) {
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.item_footprint_detail, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Footprint footprint = mDataItems.get(position);
        holder.itemView.setTag(footprint);

//        RequestManager.getInstance().getImageLoader().get(footprint.coverImage,
//                ImageLoader.getImageListener(holder.image, R.drawable.bg_banner_hint,
//                        R.drawable.bg_banner_hint));
        ImageUtil.DisplayImage(footprint.coverImage, holder.image, R.drawable.bg_banner_hint,
                R.drawable.bg_banner_hint);

        holder.time.setText(TimeUtils.formatTime(footprint.createdTime, DATE_FORMAT));
    }

    @Override
    public int getItemCount() {
        return mDataItems == null ? 0 : mDataItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }

}
