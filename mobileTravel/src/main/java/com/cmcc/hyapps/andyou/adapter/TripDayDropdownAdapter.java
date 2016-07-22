
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.model.Trip;

/**
 * @author kuloud
 */
public class TripDayDropdownAdapter extends BaseHeaderAdapter<Object, QHStrategy> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }

    @Override
    public ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_trip_dropdown, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBinderHeaderViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder) {

    }

    @Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateItemViewHolder(
            ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_trip_dropdown, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBinderItemViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder,
            int position) {
        ViewHolder vh = (ViewHolder) holder;
        vh.itemView.setTag(mDataItems.get(position));
        vh.content.setText(mDataItems.get(position).title);
        attachClickListener(vh, vh.itemView, position);
    }

}
