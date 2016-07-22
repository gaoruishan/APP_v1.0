/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;

/**
 * @author kuloud
 */
public class RowTimelineDate {

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView day;

        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            day = (TextView) itemView.findViewById(R.id.day);
        }
    }
}
