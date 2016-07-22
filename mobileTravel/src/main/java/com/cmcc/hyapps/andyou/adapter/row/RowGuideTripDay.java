/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.SecnicActivity;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHRouteDay;
import com.cmcc.hyapps.andyou.model.QHRouteScenic;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.widget.RatioImageView;


/**
 * @author kuloud
 */
public class RowGuideTripDay {
    public static RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.guide_route_detail_item_day, parent,false);
        return new ViewHolder(v);
    }

    public static void onBindViewHolder(final RecyclerView.ViewHolder holder, int position,
                                        final QHRouteDay tripDay, final Activity activity) {
        final ViewHolder h = (ViewHolder) holder;
        h.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SecnicActivity.class);
                QHRouteScenic mId = tripDay.scenic;
                intent.putExtra("scenicId",mId);
                activity.startActivity(intent);
            }
        });
        if (tripDay == null) {
            return;
        }
        if (tripDay.scenic.image_url != null && !TextUtils.isEmpty(tripDay.scenic.image_url)) {
//            h.coverImage.setRatio(tripDay.image.getLargeRadio());
//            h.coverImage.setDefaultImageResId(R.drawable.bg_image_hint)
//                    .setErrorImageResId(R.drawable.bg_image_hint);
//            h.coverImage.setImageUrl(tripDay.scenic.image_url, RequestManager.getInstance()
//                    .getImageLoader());

            ImageUtil.DisplayImage(tripDay.scenic.image_url, h.coverImage,
                    R.drawable.bg_image_hint, R.drawable.bg_image_hint);
        }

        if (!TextUtils.isEmpty(tripDay.scenic.intro_text)) {
            h.content.setVisibility(View.VISIBLE);
            h.content.setText(tripDay.scenic.intro_text);
        } else {
            h.content.setVisibility(View.GONE);
        }

//        if (tripDay.location != null && !TextUtils.isEmpty(tripDay.location.city)) {
//            h.scenicName.setVisibility(View.VISIBLE);
//            h.scenicName.setText(tripDay.location.city);
//        } else {
        h.scenicName.setVisibility(View.GONE);
        h.title.setText(tripDay.scenic.intro_title);
//        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RatioImageView coverImage;
        TextView scenicName;
        TextView content;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            coverImage = (RatioImageView) itemView.findViewById(R.id.cover_image);
            scenicName = (TextView) itemView.findViewById(R.id.scenic_name);
            content = (TextView) itemView.findViewById(R.id.content);
            title = (TextView) itemView.findViewById(R.id.title);

        }
    }
}
