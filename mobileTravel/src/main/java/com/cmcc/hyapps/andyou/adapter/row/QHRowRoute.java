/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.RouteDetailActivity;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHRoute;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;


/**
 * @author kuloud
 */
public class QHRowRoute {
    public static RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        //item_restaurant
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommand_rounte_item,
                parent, false);
        return new ViewHolder(v);
    }

    public static void onBindViewHolder(final Context mContext,RecyclerView.ViewHolder holder, int position,
            QHRoute item) {
        ViewHolder viewHolder = (ViewHolder) holder;
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                QHRoute route = (QHRoute) v.getTag();
                Bundle bundle  = new Bundle();
                bundle.putParcelable("route",route);
                bundle.putInt("id",route.id);
                Intent intent  = new Intent();
                intent.putExtra("route",bundle);
                intent.setClass(mContext,RouteDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
        if (item.cover_image != null) {
//            RequestManager.getInstance().getImageLoader().get(item.cover_image,
//                    ImageLoader.getImageListener(viewHolder.route_img, R.drawable.recommand_bg,
//                            R.drawable.recommand_bg));
            ImageUtil.DisplayImage(item.cover_image, viewHolder.route_img);
        }
        if (!TextUtils.isEmpty(item.title))
        viewHolder.route_name.setText(item.title);
        if (!TextUtils.isEmpty(item.intro_text))
        viewHolder.route_content.setText(item.intro_text);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView route_img;
        TextView route_name,route_content;
        public ViewHolder(View itemView) {
            super(itemView);
            route_img = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            route_name = (TextView) itemView.findViewById(R.id.travel_name);
            route_content = (TextView) itemView.findViewById(R.id.travel_content);
        }
    }
}
