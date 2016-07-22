
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.BasicScenicData;
import com.cmcc.hyapps.andyou.util.ImageUtil;

public class DefaultScenicDataListAdapterImp extends BasicScenicDataListAdapter {

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scenic_spot,
                parent, false);
        return new VH(v);
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView name;
//        private RatingBar rate;
//        private TextView price;
        private NetworkImageView image;

        public VH(View itemView) {
            super(itemView);
            image = (NetworkImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
//            rate = (RatingBar) itemView.findViewById(R.id.rate);
//            price = (TextView) itemView.findViewById(R.id.price);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh,
            int position) {
        VH holder = (VH) vh;
        final BasicScenicData item = mDataItems.get(position);
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(mItemClickListener);

        if (item.coverImage() != null) {
//            holder.image.setErrorImageResId(R.drawable.bg_image_hint);
//            holder.image.setDefaultImageResId(R.drawable.bg_image_hint);
//            holder.image.setImageUrl(item.coverImage(), RequestManager.getInstance()
//                    .getImageLoader());

            ImageUtil.DisplayImage(item.coverImage(), holder.image,
                    R.drawable.bg_image_hint, R.drawable.bg_image_hint);
        }

        holder.name.setText(item.name());
//        holder.rate.setRating(item.rating());
//        holder.price.setText(String.valueOf(item.ticketPrice()));
    }
}
