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
import com.cmcc.hyapps.andyou.model.QHCollectionStrategy;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;

/**
 * Created by Administrator on 2015/7/16 0016.
 */
public class QHRowRouteCollection {
    public static RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        //�ɵ�item_restaurant
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommand_rounte_item,
                parent, false);
        return new ViewHolder(v);
    }

    public static void onBindViewHolder(final Context mContext,RecyclerView.ViewHolder holder, int position,
                                        final QHCollectionStrategy item) {
        ViewHolder viewHolder = (ViewHolder) holder;
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
           //     QHCollectionStrategy route = (QHCollectionStrategy) v.getTag();
                Bundle bundle  = new Bundle();
            //    bundle.putParcelable("route",item);
                bundle.putInt("id",item.obj_id);
                Intent intent  = new Intent();
                intent.putExtra("route",bundle);
                intent.setClass(mContext,RouteDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
        if (!TextUtils.isEmpty(item.image_url)) {
//            RequestManager.getInstance().getImageLoader().get(item.image_url,
//                    ImageLoader.getImageListener(viewHolder.route_img, R.drawable.recommand_bg,
//                            R.drawable.recommand_bg));
            ImageUtil.DisplayImage(item.image_url, viewHolder.route_img);
        }
        if (!TextUtils.isEmpty(item.title))
            viewHolder.route_name.setText(item.title);
        //这里需要后台修改增加显示简介或者详情
        if (!TextUtils.isEmpty(item.created))
            viewHolder.route_content.setText(item.created);
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