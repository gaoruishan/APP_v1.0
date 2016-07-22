/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.SecnicActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHRouteDay;
import com.cmcc.hyapps.andyou.util.ImageUtil;


/**
 * @author kuloud
 */
public class RowRouteDetail {
    public static RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_detail, parent,false);
        return new ViewHolder(v);
    }

    public static void onBindViewHolder(final RecyclerView.ViewHolder holder, int position,
                                        final QHRouteDay routeDay, final Context context) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            跳转到对应scenic页面
                Intent intent = new Intent(context, SecnicActivity.class);
//                int mId = routeDay.scenic;
//                intent.putExtra("scenicId",mId);
//                Bundle bundle = new Bundle();
//                bundle.putExtra("scenic",mScenic);
                int mId = routeDay.scenic.id;
                intent.putExtra(Const.QH_SECNIC_ID,mId);
//                intent.putExtra(Const.QH_SECNIC,routeDay.scenic);
                context.startActivity(intent);
            }
        });
        if (routeDay == null) {
            return;
        }
        if (!TextUtils.isEmpty(routeDay.scenic.image_url)) {
//            viewHolder.coverImage.setDefaultImageResId(R.drawable.bg_banner_hint);
//            viewHolder.coverImage.setErrorImageResId(R.drawable.bg_banner_hint);
//            viewHolder.coverImage.setImageUrl(routeDay.scenic.image_url, RequestManager.getInstance()
//                    .getImageLoader());

            ImageUtil.DisplayImage(routeDay.scenic.image_url,
                    viewHolder.coverImage, R.drawable.bg_banner_hint, R.drawable.bg_banner_hint);
        }

        if (!TextUtils.isEmpty(routeDay.scenic.intro_text)) {
            viewHolder.content.setVisibility(View.VISIBLE);
            viewHolder.content.setText(routeDay.scenic.intro_text);
        } else {
            viewHolder.content.setVisibility(View.GONE);
        }

//        if (tripDay.location != null && !TextUtils.isEmpty(tripDay.location.city)) {
//            h.scenicName.setVisibility(View.VISIBLE);
//            h.scenicName.setText(tripDay.location.city);
//        } else {
        viewHolder.title.setText(routeDay.scenic.name);
//        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView coverImage;
        TextView content;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            coverImage = (NetworkImageView) itemView.findViewById(R.id.cover_image);
            content = (TextView) itemView.findViewById(R.id.content);
            title = (TextView) itemView.findViewById(R.id.title);

        }
    }
}
