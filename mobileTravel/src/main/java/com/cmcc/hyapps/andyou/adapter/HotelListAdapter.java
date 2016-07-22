
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.HotelDetailActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.HomeHotel;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ConstTools;

import java.util.List;

public class HotelListAdapter extends AppendableAdapter<HomeHotel> {
    private Activity mContext;
    Location myLocation;

    public HotelListAdapter(Activity context) {
        this.mContext = context;
    }

    public HotelListAdapter(Activity context, List<HomeHotel> items) {
        this(context);
        this.mDataItems = items;
    }
    public void initPosition(Location myLocation){
        this.myLocation = myLocation;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel, parent, false);
        return new RestViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RestViewHolder viewHolder = (RestViewHolder) holder;
        final HomeHotel item = mDataItems.get(position);
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                HomeHotel rest = (HomeHotel) v.getTag();
                Intent intent  = new Intent();
                intent.putExtra(Const.REST_DETAIL,rest);
                intent.setClass(mContext,HotelDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
        if (item.imageUrls.length>0) {
//            RequestManager.getInstance().getImageLoader().get(item.imageUrls[0],
//                    ImageLoader.getImageListener(viewHolder.rest_img,
//                            R.drawable.bg_image_hint,R.drawable.bg_image_hint));
            ImageUtil.DisplayImage(item.imageUrls[0], viewHolder.rest_img,
                    R.drawable.bg_image_hint,R.drawable.bg_image_hint);
        }
        viewHolder.rest_name.setText(item.name);
        viewHolder.rest_address.setText(item.address);

        if(ConstTools.isNumeric(item.mark)){
            viewHolder.rest_star.setRating(Float.parseFloat(item.mark));
        }
        else {
            viewHolder.rest_star.setRating(0.0f);
        }

        if(ConstTools.isNumeric(item.price)){
              viewHolder.rest_cost.setText("￥"+item.price);
            if("0".equals(item.price)){
                viewHolder.rest_cost.setText("暂无");
                viewHolder.rest_cost.setTextColor(0xffb4b4b4);
                viewHolder.rest_cost.setTextSize(12);
            }
        }
        else
        {
            viewHolder.rest_cost.setText("暂无");
            viewHolder.rest_cost.setTextColor(0xffb4b4b4);
            viewHolder.rest_cost.setTextSize(12);
//            viewHolder.rest_cost_tips.setVisibility(View.GONE);
        }
        LatLng start = new LatLng(Double.parseDouble(item.latitude),Double.parseDouble(item.longitude));
        LatLng end = new LatLng(ConstTools.myCurrentLoacation.latitude,ConstTools.myCurrentLoacation.longitude);
        viewHolder.rest_distance.setText(ConstTools.getDistance(start,end));


    }

    static class RestViewHolder extends ViewHolder {
        NetworkImageView rest_img;
        TextView rest_name,rest_address,rest_cost,rest_distance,rest_cost_tips;
        RatingBar rest_star;

        public RestViewHolder(View itemView) {
            super(itemView);
            rest_img = (NetworkImageView) itemView.findViewById(R.id.rest_icon);
            rest_name = (TextView) itemView.findViewById(R.id.restaurant_name);
            rest_address = (TextView) itemView.findViewById(R.id.restaurant_address);
            rest_cost = (TextView) itemView.findViewById(R.id.restaurant_average_cost);
            rest_cost_tips = (TextView) itemView.findViewById(R.id.restaurant_average_cost_tip);
            rest_distance = (TextView) itemView.findViewById(R.id.restaurant_distance);
            rest_star = (RatingBar) itemView.findViewById(R.id.testaurant_rating);

        }
    }
}
