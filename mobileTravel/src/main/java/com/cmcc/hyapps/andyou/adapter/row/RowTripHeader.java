/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.TripDetail;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.TimeUtils;

/**
 * @author kuloud
 */
public class RowTripHeader {
    private static final String DATE_FARMAT = "yyyy. MM. dd";

    public static RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_trip_detail,
                parent, false);
        return new ViewHolder(v);
    }

    public static void onBindViewHolder(RecyclerView.ViewHolder holder, int position,
            TripDetail detail, int dayCount) {
        ViewHolder h = (ViewHolder) holder;
        if (!TextUtils.isEmpty(detail.coverImage)) {
//            h.coverImage.setDefaultImageResId(R.drawable.bg_banner_hint)
//                    .setErrorImageResId(R.drawable.bg_banner_hint);
//            h.coverImage.setImageUrl(detail.coverImage, RequestManager.getInstance()
//                    .getImageLoader());
//
            ImageUtil.DisplayImage(detail.coverImage, h.coverImage, R.drawable.bg_banner_hint, R.drawable.bg_banner_hint);
        }
        if (detail.author != null && !TextUtils.isEmpty(detail.author.avatarUrl)) {
//            RequestManager
//                    .getInstance()
//                    .getImageLoader()
//                    .get(detail.author.avatarUrl,
//                            ImageLoader.getImageListener(h.avatar, R.drawable.bg_banner_hint,
//                                    R.drawable.bg_banner_hint));
            ImageUtil.DisplayImage(detail.author.avatarUrl, h.avatar, R.drawable.bg_banner_hint, R.drawable.bg_banner_hint);
        }
        if (detail.author != null) {
            h.name.setText(detail.author.name);
        }
        Time createTime = TimeUtils.parseTime(detail.createTime);
        h.date.setText(TimeUtils.formatTime(createTime, DATE_FARMAT));
        h.daysNum.setText(h.itemView.getContext().getString(R.string.trip_format_date_count,
                dayCount));
        h.title.setText(detail.title);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView coverImage;
        ImageView avatar;
        TextView title;
        TextView name;
        TextView date;
        TextView daysNum;

        public ViewHolder(View itemView) {
            super(itemView);
            coverImage = (NetworkImageView) itemView.findViewById(R.id.cover_image);
            avatar = (ImageView) itemView.findViewById(R.id.author_avatar);
            title = (TextView) itemView.findViewById(R.id.title);
            name = (TextView) itemView.findViewById(R.id.author_name);
            date = (TextView) itemView.findViewById(R.id.date);
            daysNum = (TextView) itemView.findViewById(R.id.days_num);
        }
    }
}
