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
public class RowTimelineScenic {

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
