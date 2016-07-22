
package com.cmcc.hyapps.andyou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.ImageLoaderManager;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.ImageBucket;
import com.cmcc.hyapps.andyou.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author kuloud
 */
public class MultiSelectImageAdapter extends
        RecyclerView.Adapter<MultiSelectImageAdapter.ViewHolder> {

    private ImageBucket mDataItems;

    private ImageLoader mImageLoader;

    private int mMaxSelectCount;

    private List<Image> mSelectedImages;

    public MultiSelectImageAdapter(int maxSelection) {
        this.mMaxSelectCount = maxSelection;
        mImageLoader = ImageLoaderManager.getInstance().getLoader();
    }

    public MultiSelectImageAdapter(int maxSelection, ImageBucket items) {
        this(maxSelection);
        this.mDataItems = items;
    }

    public void setImageBucket(ImageBucket items, ArrayList<Image> selectedImages) {
        this.mDataItems = items;
        mSelectedImages = selectedImages;

        if (items.imageList == null) {
            return;
        }

        Iterator<Image> it = items.imageList.iterator();
        while (it.hasNext()) {
            Image image = it.next();
            if (isAlreadySelected(image)) {
                it.remove();
                items.count--;
            }
        }

        notifyDataSetChanged();
    }

    public ArrayList<Image> getSelection() {
        ArrayList<Image> list = new ArrayList<Image>();

        for (int i = 0; i < mDataItems.imageList.size(); i++) {
            if (mDataItems.imageList.get(i).isSelected) {
                list.add(mDataItems.imageList.get(i));
            }
        }

        return list;
    }

    private boolean isAlreadySelected(Image image) {
        if (mSelectedImages == null) {
            return false;
        }
        for (Image i : mSelectedImages) {
            if (image.imagePath.equals(i.imagePath)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo_picker_item, parent,
                false);
        ViewHolder vh = new ViewHolder(view);
        vh.selector.setVisibility(mMaxSelectCount > 1 ? View.VISIBLE : View.GONE);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Image item = mDataItems.imageList.get(position);
        holder.itemView.setTag(item);

        mImageLoader.displayImage(item.imagePath.toString(),
                holder.image, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.image
                                .setImageResource(R.drawable.bg_image_hint);
                        super.onLoadingStarted(imageUri, view);
                    }
                });

        if (mMaxSelectCount > 1) {
            holder.selector.setSelected(item.isSelected);
        }
    }

    @Override
    public int getItemCount() {
        return mDataItems == null ? 0 : mDataItems.count;
    }

    public void toggleSelection(View v, int position) {
        Image image = mDataItems.imageList.get(position);
        if (image.isSelected) {
            image.isSelected = false;
        } else if (getSelection().size() < mMaxSelectCount) {
            image.isSelected = true;
        } else if (mMaxSelectCount == 1) {
            image.isSelected = true;
            return;
        } else {
            Toast.makeText(
                    v.getContext(),
                    v.getContext().getResources()
                            .getString(R.string.pick_image_max_limit_hint, mMaxSelectCount),
                    Toast.LENGTH_SHORT).show();
        }

        ((ImageView) v.findViewById(R.id.image_selector)).setSelected(image.isSelected);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView selector;

        public ViewHolder(View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            image = (ImageView) itemView.findViewById(R.id.item_image);
            int spacing = ScreenUtils.dpToPxInt(context,
                    context.getResources().getDimension(R.dimen.gallery_image_spacing));
            int headerImageWidth = (ScreenUtils.getScreenWidth(context) - spacing * 2) / 3;
            image.setLayoutParams(new FrameLayout.LayoutParams(headerImageWidth,
                    headerImageWidth));

            selector = (ImageView) itemView.findViewById(R.id.image_selector);
        }
    }
}
