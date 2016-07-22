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
import com.cmcc.hyapps.andyou.adapter.TripDetailAdapter.CategoryDate;

/**
 * @author kuloud
 */
public class RowTripDate {
    public static RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_category_date,
                parent, false);
        return new ViewHolder(v);
    }

    public static void onBindViewHolder(RecyclerView.ViewHolder holder, int position,
            CategoryDate category) {
        if (category == null) {
            return;
        }
        ViewHolder h = (ViewHolder) holder;
//        h.date.setText(category.date);
        h.day.setText("DAY" + category.index);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView day;

        public ViewHolder(View itemView) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.day);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
