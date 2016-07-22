
package com.cmcc.hyapps.andyou.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.ImageLoaderManager;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.widget.BadgeView;
import com.cmcc.hyapps.andyou.widget.RatioImageView;

import java.util.ArrayList;
import java.util.List;

public class SelectedImageAdapter extends
        RecyclerView.Adapter<SelectedImageAdapter.ViewHolder> {

    private List<Image> mDataItems;
    private int mMaxImageCount = 8;
    private static final String DUMMY_ID = "dummy_id";

    public SelectedImageAdapter(Context context) {
        mDataItems = new ArrayList<Image>();
        // Placeholder for the add image button
        Image dummy = new Image();
        dummy.imageId = DUMMY_ID;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.comment_edit_add_selecter);
        dummy.bitmap = bitmap;
        mDataItems.add(dummy);
    }

    public void setMaxImageLimit(int maxImageCount) {
        mMaxImageCount = maxImageCount;
    }

    public void addImage(Image image) {
        addImageInternal(image);
        notifyDataSetChanged();
    }

    public void addEndImage(Context context){
        Image dummy = new Image();
        dummy.imageId = DUMMY_ID;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.comment_edit_add_selecter);
        dummy.bitmap = bitmap;
        mDataItems.add(dummy);
    }

    public void addImages(List<Image> images) {
        for (Image image : images) {
            addImageInternal(image);
        }
        notifyDataSetChanged();
    }

    private void addImageInternal(Image image) {
        if (mDataItems.size() == mMaxImageCount) {
            // Remove the dummy placeholder
            mDataItems.remove(mDataItems.size() - 1);
            mDataItems.add(image);
        } else {
            mDataItems.add(mDataItems.size() - 1, image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout layout = new FrameLayout(parent.getContext());
        RatioImageView imageView = new RatioImageView(parent.getContext());
        layout.setPadding(1, 1, 1, 1);
        imageView.setRatio(1);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setBackgroundResource(R.drawable.comment_edit_add_selecter);
        layout.addView(imageView);
        BadgeView badge = new BadgeView(parent.getContext(), imageView);
        layout.setAddStatesFromChildren(false);
        badge.setBackgroundResource(R.drawable.ic_image_delete_selecter);
        return new ViewHolder(layout, imageView, badge);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Image item = mDataItems.get(position);
        if (DUMMY_ID.equals(item.imageId)) {
            // DO nothing
            holder.badge.hide();
            holder.image.setImageResource(R.drawable.comment_edit_add_selecter);
        } else if (item.bitmap != null) {
            holder.itemView.setTag(item);
            holder.image.setImageBitmap(item.bitmap);
            holder.badge.show();
        } else if (item.imagePath != null) {
            String path = item.imagePath.toString();
            ImageLoaderManager.getInstance().getLoader().displayImage(path, holder.image);
            holder.badge.show();
        }
        attachClickListener(holder, holder.image, position);
        attachClickListener(holder, holder.badge, position);
//        if (holder.badge.isShown()) {
//            holder.badge.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onValidClick(View v) {
//                    mDataItems.remove(item);
//                    notifyItemRemoved(position);
//                }
//            });
//        }
    }

    public List<Image> getDataItems() {
        return mDataItems;
    }

    @Override
    public int getItemCount() {
        return mDataItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        BadgeView badge;
        ImageView image;

        public ViewHolder(View parent, View image, BadgeView badge) {
            super(parent);
            this.image = (ImageView) image;
            this.badge = badge;
        }
    }
}
