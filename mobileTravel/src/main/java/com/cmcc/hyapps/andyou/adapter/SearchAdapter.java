
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Scenic;
import com.cmcc.hyapps.andyou.util.ImageUtil;

import java.util.List;

/**
 * @author kuloud
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<Scenic> mDataItems;

    public SearchAdapter() {
    }

    public SearchAdapter(List<Scenic> items) {
        this();
        this.mDataItems = items;
    }

    public void setSpotList(List<Scenic> items) {
        this.mDataItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_search_scenic,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Scenic item = mDataItems.get(position);
        holder.itemView.setTag(item);

        holder.name.setText(item.name);
        if (item.imageUrl != null) {
//            holder.image.setErrorImageResId(R.drawable.bg_image_hint);
//            holder.image.setDefaultImageResId(R.drawable.bg_image_hint);
//            holder.image.setImageUrl(item.imageUrl, RequestManager.getInstance()
//                    .getImageLoader());

            ImageUtil.DisplayImage(item.imageUrl, holder.image,
                    R.drawable.bg_image_hint, R.drawable.bg_image_hint);
        }
    }

    @Override
    public int getItemCount() {
        return mDataItems == null ? 0 : mDataItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private NetworkImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.scenic_name);
            image = (NetworkImageView) itemView.findViewById(R.id.scenic_image);

        }
    }
}
