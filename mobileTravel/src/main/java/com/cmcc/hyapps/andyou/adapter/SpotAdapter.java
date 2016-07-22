
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.ScenicSpot;

import java.util.List;

/**
 * @author kuloud
 */
public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {

    private List<ScenicSpot> mDataItems;

    public SpotAdapter() {
    }

    public SpotAdapter(List<ScenicSpot> items) {
        this.mDataItems = items;
    }

    public void setSpotList(List<ScenicSpot> items) {
        this.mDataItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spot,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ScenicSpot item = mDataItems.get(position);
        holder.itemView.setTag(item);

        holder.text.setText(item.name);
    }

    @Override
    public int getItemCount() {
        return mDataItems == null ? 0 : mDataItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.tv_text);
        }

    }
}
