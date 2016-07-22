
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.CompoundImage.TextImage;

public class ImageGalleryAdapter extends AppendableAdapter<TextImage> {
    private int mScreenPadding;

    public void setScreenPadding(int mGalleryPadding) {
        this.mScreenPadding = mGalleryPadding;
    }

    public int getScreenPadding() {
        return this.mScreenPadding;
    }

    public ImageGalleryAdapter(Activity context) {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_gallery,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final TextImage item = mDataItems.get(position);
        holder.itemView.setTag(item);

        if (item.image != null && item.image.smallImage != null) {
//            holder.imageView.setErrorImageResId(R.drawable.bg_image_hint);
//            holder.imageView.setDefaultImageResId(R.drawable.bg_image_hint);
//            holder.imageView.setImageUrl(item.image.smallImage, RequestManager.getInstance()
//                    .getImageLoader());
            ImageUtil.DisplayImage(item.image.smallImage, holder.imageView, R.drawable.bg_image_hint, R.drawable.bg_image_hint);
        }
        holder.name.setText(item.text);
        attachClickListener(holder, holder.itemView, position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView imageView;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_image);
            name = (TextView) itemView.findViewById(R.id.item_name);
        }

    }
}
