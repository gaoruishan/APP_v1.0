/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;

/**
 * @author kuloud
 */
public class RowTimelineAdd {
    public static RecyclerView.ViewHolder onCreateLocalTripViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_add, parent,
                false);
        return new ViewHolder(v);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
