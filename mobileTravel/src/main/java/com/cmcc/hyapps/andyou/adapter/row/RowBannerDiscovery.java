/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.BannerPagerAdapter.IActionCallback;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Trip;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;

/**
 * @author kuloud
 */
public class RowBannerDiscovery {
    public static View getView(Context context, final Trip trip, View convertView,
            ViewGroup container,
            final IActionCallback<Trip> callback) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_banner_discovery,
                    container, false);
            holder = new ViewHolder();
            holder.image = (NetworkImageView) convertView.findViewById(R.id.discovery_banner_image);
            holder.title = (TextView) convertView.findViewById(R.id.discovery_banner_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (trip == null) {
            holder.image.setImageResource(R.drawable.bg_banner_hint);
        }
        else {
//            holder.image.setDefaultImageResId(R.drawable.bg_banner_hint)
//                    .setErrorImageResId(R.drawable.bg_banner_hint);
//            holder.image.setImageUrl(trip.coverImage, RequestManager.getInstance()
//                    .getImageLoader());
            ImageUtil.DisplayImage(trip.coverImage, holder.image, R.drawable.bg_banner_hint, R.drawable.bg_banner_hint);
            holder.title.setText(trip.title);
            holder.image.setOnClickListener(new OnClickListener() {

                @Override
                public void onValidClick(View v) {
                    if (callback != null) {
                        callback.doAction(trip);
                    }
                }
            });
        }
        return convertView;
    }

    private static class ViewHolder {
        NetworkImageView image;
        TextView title;
    }
}
