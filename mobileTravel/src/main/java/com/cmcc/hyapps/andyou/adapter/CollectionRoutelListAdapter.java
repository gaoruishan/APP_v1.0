
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.RouteDetailActivity;
import com.cmcc.hyapps.andyou.activity.StrategyDetailActivity;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHCollectionRoute;
import com.cmcc.hyapps.andyou.model.QHCollectionStrategy;
import com.cmcc.hyapps.andyou.support.OnClickListener;

import java.util.List;

public class CollectionRoutelListAdapter extends AppendableAdapter<QHCollectionRoute> {
    private Activity mContext;

    public CollectionRoutelListAdapter(Activity context) {
        this.mContext = context;
    }

    public CollectionRoutelListAdapter(Activity context, List<QHCollectionRoute> items) {
        this(context);
        this.mDataItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.collect_routes_item , parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoViewHolder viewHolder = (VideoViewHolder) holder;
        final QHCollectionRoute item = mDataItems.get(position);
        viewHolder.itemView.setTag(item);
        viewHolder.netImage.setTag(item);


        if (item.image_url != null) {
//            RequestManager.getInstance().getImageLoader().get(
//                    item.image_url,ImageLoader.getImageListener(
//                            viewHolder.netImage, R.drawable.bg_image_hint,
//                            R.drawable.bg_image_hint));
            ImageUtil.DisplayImage(item.image_url, viewHolder.netImage, R.drawable.bg_image_hint,
                    R.drawable.bg_image_hint);
        }

        viewHolder.netImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View v) {
                QHCollectionRoute item = (QHCollectionRoute) v.getTag();
                Bundle bundle = new Bundle();
//                bundle.putParcelable("guide",item);
                bundle.putInt("id",item.obj_id);
                Intent intent  = new Intent();
                intent.putExtra("route",bundle);
//                intent.putExtra(Const.SPECIAL_DETAIL_DATA, item);
                intent.setClass(mContext,RouteDetailActivity.class);
                mContext.startActivity(intent);
            }
        });

        viewHolder.item_name.setText(item.title);

        /*for(int i = 0;i<item.size();i++){
            if(i==scenic.spot_list.size()-1)
                result+=scenic.spot_list.get(i).scenic_name;
            else
                result+=(scenic.spot_list.get(i).scenic_name+"---");
        }*/
        viewHolder.item_routes.setText(item.title);


    }

    static class VideoViewHolder extends ViewHolder {
        NetworkImageView netImage;
        TextView item_name,item_routes;

        public VideoViewHolder(View itemView) {
            super(itemView);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            item_name = (TextView) itemView.findViewById(R.id.item_name);
            item_routes = (TextView) itemView.findViewById(R.id.item_routes);

        }

    }
}
