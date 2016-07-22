
package com.cmcc.hyapps.andyou.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.android.volley.toolbox.NetworkImageView;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI.ScenicShops;
import com.cmcc.hyapps.andyou.model.Shop;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.LocationUtils;

public class ServiceListAdapter extends AppendableAdapter<Shop> {
    private ScenicShops.Type mType;

    public ServiceListAdapter(ScenicShops.Type type) {
        setServiceType(type);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (mType == ScenicShops.Type.HOTEL || mType == ScenicShops.Type.FOOD
                || mType == ScenicShops.Type.SHOPPING) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_service_hotel,
                    parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_scenic_service_restaurant,
                    parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final Shop item = mDataItems.get(position);
        holder.itemView.setTag(item);
        final Context context = holder.itemView.getContext();

        holder.name.setText(item.name);
        if (item.distance > 0) {
            holder.distanceTextView
                    .setText(context.getString(R.string.distance_less_than,
                            String.valueOf(LocationUtils.formatDistance((float) item.distance))));
        } else if (item.location != null) {
            holder.distanceTextView
                    .setText(context.getString(R.string.distance_less_than,
                            String.valueOf(LocationUtils.formatDistance(
                                    (float) AMapUtils.calculateLineDistance(LocationUtils.getLastKnownLocation(
                                            holder.distanceTextView.getContext()).toLatLng() , item.location.toLatLng())))));
        }

        // if (mType != ScenicShops.Type.HOTEL) {
        // if (TextUtils.isEmpty(item.image)) {
        // holder.icon.setVisibility(View.VISIBLE);
        // } else {
        // holder.icon.setVisibility(View.GONE);
        // holder.imageView.setDefaultImageResId(R.drawable.bg_image_hint).setErrorImageResId(
        // R.drawable.bg_image_error)
        // .setImageUrl(item.image,
        // RequestManager.getInstance().getImageLoader());
        // }
        // if (mType.hasRate()) {
        // holder.rating.setVisibility(View.VISIBLE);
        // } else {
        // holder.rating.setVisibility(View.GONE);
        // }
        //
        // holder.priceTextView.setVisibility(mType.hasPrice() ? View.VISIBLE :
        // View.GONE);
        // holder.priceTextView.setText(holder.priceTextView.getContext().getString(
        // R.string.general_cost, String.valueOf(item.price)));
        // } else {
        if (TextUtils.isEmpty(item.telphone)) {
            holder.icon.setEnabled(false);
        } else {
            holder.icon.setEnabled(true);
            final String number = item.telphone;
            holder.icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onValidClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                    context.startActivity(intent);
                }
            });
        }
        // }
        holder.location.setText(holder.location.getContext().getString(R.string.service_address,
                item.address));
    }

    public void setServiceType(ScenicShops.Type type) {
        this.mType = type;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView imageView;
        ImageView icon;
        TextView name;
        RatingBar rating;
        TextView priceTextView;
        TextView location;
        TextView distanceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.restaurant_name);
            location = (TextView) itemView.findViewById(R.id.restaurant_location);
            distanceTextView = (TextView) itemView.findViewById(R.id.restaurant_distance);
            icon = (ImageView) itemView.findViewById(R.id.restaurant_icon);
            if (mType != ScenicShops.Type.HOTEL) {
                priceTextView = (TextView) itemView.findViewById(R.id.restaurant_price);
                imageView = (NetworkImageView) itemView.findViewById(R.id.restaurant_image);
                rating = (RatingBar) itemView.findViewById(R.id.restaurant_rating);
            }
        }

    }
}
