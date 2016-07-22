
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.ViewHolder;
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
import com.kuloud.android.widget.recyclerview.AppendableAdapter;

import java.util.List;

public class RestaurantListAdapter extends AppendableAdapter<QHRoute> {
    private Activity mContext;
    public RestaurantListAdapter(Activity context) {
        this.mContext = context;
    }
    public RestaurantListAdapter(Activity context, List<QHRoute> items) {
        this(context);
        this.mDataItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommand_rounte_item, parent, false);
        return new RestViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RestViewHolder viewHolder = (RestViewHolder) holder;
        final QHRoute item = mDataItems.get(position);
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
            ImageUtil.DisplayImage(item.cover_image, viewHolder.rest_img);
//            RequestManager.getInstance().getImageLoader().get(item.cover_image,
//                    ImageLoader.getImageListener(viewHolder.rest_img, R.drawable.recommand_bg,
//                            R.drawable.recommand_bg));
        }else
            viewHolder.rest_img.setImageResource(R.drawable.recommand_bg);
        if (!TextUtils.isEmpty(item.title))
        viewHolder.recommand_name.setText(item.title);
        if (!TextUtils.isEmpty(item.intro_text))
        viewHolder.recommand_content.setText(item.intro_text);
    }

    static class RestViewHolder extends ViewHolder {
        NetworkImageView rest_img;
        TextView recommand_name,recommand_content;
        public RestViewHolder(View itemView) {
            super(itemView);
            rest_img = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            recommand_name = (TextView) itemView.findViewById(R.id.travel_name);
            recommand_content = (TextView) itemView.findViewById(R.id.travel_content);
        }
    }
}
