
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
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
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHScenic;

import java.util.List;

public class NearbyScenicAdapter extends AppendableAdapter<QHScenic> {
    private Activity mContext;

    public NearbyScenicAdapter(Activity context) {
        this.mContext = context;
    }

    public NearbyScenicAdapter(Activity context, List<QHScenic> items) {
        this(context);
        this.mDataItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearby_scenic , parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoViewHolder viewHolder = (VideoViewHolder) holder;
        final QHScenic item = mDataItems.get(position);
        viewHolder.itemView.setTag(item);
        viewHolder.netImage.setTag(item);


        if (item.image_url != null) {
//            RequestManager.getInstance().getImageLoader().get(item.image_url,
//                    ImageLoader.getImageListener(viewHolder.netImage,
//                            R.drawable.bg_image_hint,
//                            R.drawable.bg_image_hint));

            ImageUtil.DisplayImage(item.image_url, viewHolder.netImage,
                    R.drawable.bg_image_hint,
                    R.drawable.bg_image_hint);
        }

        viewHolder.item_name.setText(item.name);


        /*for(int i = 0;i<item.size();i++){
            if(i==scenic.spot_list.size()-1)
                result+=scenic.spot_list.get(i).scenic_name;
            else
                result+=(scenic.spot_list.get(i).scenic_name+"---");
        }*/
        viewHolder.item_routes.setText("--长春---大连----青岛----香港----三亚"/*result*/);


    }

    static class VideoViewHolder extends ViewHolder {
        NetworkImageView netImage;
        TextView item_name,item_routes;

        public VideoViewHolder(View itemView) {
            super(itemView);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            item_name = (TextView) itemView.findViewById(R.id.item_title);

        }

    }
}
