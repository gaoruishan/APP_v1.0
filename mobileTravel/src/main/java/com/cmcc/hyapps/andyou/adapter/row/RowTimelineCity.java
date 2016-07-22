/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.Location;

/**
 * @author kuloud
 */
public class RowTimelineCity {
    public static RecyclerView.ViewHolder onCreateTripViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_city, parent,
                false);
        return new ViewHolder(v);
    }

    public static RecyclerView.ViewHolder onCreateLocalTripViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_trip_city,
                parent, false);
        return new ViewHolder(v);
    }

    public static void onBindTripViewHolder(RecyclerView.ViewHolder holder, int position,
            Location location) {
        ViewHolder h = (ViewHolder) holder;
        if (TextUtils.isEmpty(location.city)) {
            h.text.setText(R.string.timeline_add_city);
        } else {
            h.text.setText(location.city);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
