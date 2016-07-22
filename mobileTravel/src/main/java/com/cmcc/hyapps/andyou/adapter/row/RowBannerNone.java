/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.BannerPagerAdapter.IActionCallback;

/**
 * @author kuloud
 */
public class RowBannerNone {
    public static View getView(Context context, int position, View convertView,
            final IActionCallback<?> callback) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = holder.imageView = new ImageView(context);
            holder.imageView.setScaleType(ScaleType.FIT_XY);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageView.setImageResource(R.drawable.bg_banner_hint);
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }
}
