package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.QHEnjoy;
import com.cmcc.hyapps.andyou.model.RoadVideo;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.example.rtspdemo.PlayerActivity;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;

/**
 * Created by bingbing on 2015/10/9.
 */
public class RoadConditionListAdapter extends AppendableAdapter<RoadVideo> {
    private Activity mContext;
    public RoadConditionListAdapter(Activity mContext){
        this.mContext = mContext;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.road_list_item_layout,parent,false);
        return new RoadListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RoadListHolder roadListHolder = (RoadListHolder) holder;
        final RoadVideo item = mDataItems.get(position);
        holder.itemView.setTag(item);
        ((RoadListHolder) holder).networkImageView.setTag(item);
        if (!TextUtils.isEmpty(item.getVideo_name()))
            roadListHolder.contentTextView.setText(item.getVideo_name());
            ImageUtil.DisplayImage(item.getImage_url(), roadListHolder.networkImageView);
      //  attachClickListener(roadListHolder,roadListHolder.networkImageView,position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                        intent.putExtra("url",item.getVideo_url());
                        mContext.startActivity(intent);
            }
        });
    }

    class RoadListHolder extends RecyclerView.ViewHolder{

        public RoadListHolder(View itemView) {
            super(itemView);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.road_thumbnail);
            contentTextView = (TextView) itemView.findViewById(R.id.road_content);
        }

        private NetworkImageView networkImageView;
        private TextView contentTextView;
    }
}
