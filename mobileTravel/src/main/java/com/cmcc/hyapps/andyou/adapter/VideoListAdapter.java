
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.CommonUtils;
import com.cmcc.hyapps.andyou.model.QHScenic.QHVideo;

import java.util.List;

public class VideoListAdapter extends AppendableAdapter<QHVideo> {
    private Activity mContext;

    public VideoListAdapter(Activity context) {
        this.mContext = context;
    }

    public VideoListAdapter(Activity context, List<QHVideo> items) {
        this(context);
        this.mDataItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_view, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoViewHolder viewHolder = (VideoViewHolder) holder;
        final QHVideo item = mDataItems.get(position);
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                QHVideo data = (QHVideo) v.getTag();
                if (data != null) {
                    CommonUtils.playVideo(mContext, data);
                }

            }
        });

        if (item.image_url != null) {
//            RequestManager.getInstance().getImageLoader().get(item.image_url,
//                    ImageLoader.getImageListener(viewHolder.thumbnail, R.drawable.bg_image_hint,
//                            R.drawable.bg_image_hint));

            ImageUtil.DisplayImage(item.image_url, viewHolder.thumbnail, R.drawable.bg_image_hint,
                    R.drawable.bg_image_hint);
        }

    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        public VideoViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
        }
    }
}
