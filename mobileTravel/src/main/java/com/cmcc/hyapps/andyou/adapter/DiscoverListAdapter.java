
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.model.QHScenic.QHVideo;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.CommonUtils;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;

import java.util.List;

public class DiscoverListAdapter extends AppendableAdapter<QHScenic> {
    private Activity mContext;

    public DiscoverListAdapter(Activity context) {
        this.mContext = context;
    }

    public DiscoverListAdapter(Activity context, List<QHScenic> items) {
        this(context);
        this.mDataItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover_list, parent, false);
        return new DiscoverViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DiscoverViewHolder viewHolder = (DiscoverViewHolder) holder;
        final QHScenic item = mDataItems.get(position);
        viewHolder.item_name.setText(item.name);
        viewHolder.item_intro.setText(item.intro_text.trim().replace(" ",""));
//        viewHolder.netImage.setImageUrl(item.image_url, RequestManager.getInstance()
//                .getImageLoader());
        ImageUtil.DisplayImage(item.image_url, viewHolder.netImage);
        viewHolder.netImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                ToastUtils.show(mContext,"点击了："+item.id);
//                QHScenic data = (QHScenic) v.getTag();
//                if (data != null) {
////                    CommonUtils.playVideo(mContext, data);
//                }

            }
        });



    }


    static class DiscoverViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView netImage;
        TextView item_name, item_intro, item_distance;
        ImageView is_audio;

        public DiscoverViewHolder(View itemView) {
            super(itemView);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_item_discover);
//            is_audio = (ImageView) itemView.findViewById(R.id.home_item_isaudio);
            item_name = (TextView) itemView.findViewById(R.id.item_title);
            item_intro = (TextView) itemView.findViewById(R.id.item_contents);
//            item_distance = (TextView) itemView.findViewById(R.id.home_item_secnic_distance);
        }
    }
}
