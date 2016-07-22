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
import com.cmcc.hyapps.andyou.model.BannerSlide;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;

/**
 * @author kuloud
 */
public class RowBannerScenic {
    public static View getView(Context context, final BannerSlide bannerSilde, View convertView,
            ViewGroup container,
            final IActionCallback<BannerSlide> callback) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_scenic_banner,
                    container, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (bannerSilde == null) {
            holder.imageView.setImageResource(R.drawable.bg_banner_hint);
        } else {
            if (bannerSilde.image != null) {
//                holder.imageView.setDefaultImageResId(R.drawable.bg_banner_hint)
//                        .setErrorImageResId(R.drawable.bg_banner_hint);
//                holder.imageView.setImageUrl(bannerSilde.image, RequestManager.getInstance()
//                        .getImageLoader());
                ImageUtil.DisplayImage(bannerSilde.image, holder.imageView, R.drawable.bg_banner_hint, R.drawable.bg_banner_hint);
                holder.imageView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onValidClick(View v) {
                        if (callback != null) {
                            callback.doAction(bannerSilde);
                        }
                    }
                });
            }
            holder.titleText.setText(bannerSilde.title);
        }
        return convertView;
    }

    private static class ViewHolder {
        NetworkImageView imageView;
        TextView titleText;

        public ViewHolder(View itemView) {
            imageView = (NetworkImageView) itemView.findViewById(R.id.scenic_banner_image);
            titleText = (TextView) itemView.findViewById(R.id.scenic_banner_title);
        }
    }
}
