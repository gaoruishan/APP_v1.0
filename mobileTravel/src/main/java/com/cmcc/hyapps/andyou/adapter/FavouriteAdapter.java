
package com.cmcc.hyapps.andyou.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.FavoriteItem;
import com.cmcc.hyapps.andyou.util.ImageUtil;

import java.util.List;

/**
 * @author kuloud
 */
public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    private List<FavoriteItem> mDataItems;

    public FavouriteAdapter() {
    }

    public FavouriteAdapter(List<FavoriteItem> items) {
        this.mDataItems = items;
    }

    public List<FavoriteItem> getFavouriteItems() {
        return this.mDataItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite,
                parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FavoriteItem item = mDataItems.get(position);
        if (item == null) {
            return;
        }
        holder.itemView.setTag(item);
        holder.title.setText(item.name);
        if (!TextUtils.isEmpty(item.image)) {
            ImageUtil.DisplayImage(item.image, holder.image, R.drawable.bg_image_hint, R.drawable.bg_image_hint);
//            RequestManager.getInstance().getImageLoader().get(item.image,
//                    ImageLoader.getImageListener(holder.image, R.drawable.bg_image_hint,
//                            R.drawable.bg_image_hint));
        }
        attachClickListener(holder, holder.itemView, 0);
    }

    @Override
    public int getItemCount() {
        return mDataItems == null ? 0 : mDataItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title;

        // private TextView summary;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_image);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            // summary = (TextView) itemView.findViewById(R.id.tv_summary);
        }
    }

    public void appendDataItems(List<FavoriteItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        if (mDataItems == null || mDataItems.isEmpty()) {
            mDataItems = items;
            notifyDataSetChanged();
        } else {
            int positionStart = mDataItems.size();
            mDataItems.addAll(items);
            notifyItemRangeInserted(positionStart, items.size());
        }
    }
}
