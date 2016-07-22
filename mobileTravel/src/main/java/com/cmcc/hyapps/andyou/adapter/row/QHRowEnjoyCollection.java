package com.cmcc.hyapps.andyou.adapter.row;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.EntertainmentsDetailActivity;
import com.cmcc.hyapps.andyou.model.QHCollectionStrategy;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;

/**
 * Created by bingbing on 2015/8/21.
 */
public class QHRowEnjoyCollection {
    public static RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_enjoy_item,
                parent, false);
        return new ViewHolder(v);
    }

    public static void onBindViewHolder(final Context mContext, RecyclerView.ViewHolder holder, int position,
                                        final QHCollectionStrategy item) {
        ViewHolder viewHolder = (ViewHolder) holder;
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                //     QHCollectionStrategy route = (QHCollectionStrategy) v.getTag();
//                Bundle bundle = new Bundle();
//                //    bundle.putParcelable("route",item);
//                bundle.putInt("id", item.obj_id);
                Intent intent = new Intent(mContext, EntertainmentsDetailActivity.class);
                intent.putExtra("entertainment", item.obj_id);
                mContext.startActivity(intent);
            }
        });
        ImageUtil.DisplayImage(item.image_url, viewHolder.route_img);
        if (!TextUtils.isEmpty(item.title))
            viewHolder.title.setText(item.title);
        if (!TextUtils.isEmpty(item.created))
            viewHolder.time.setText(item.created);
    }
}

class ViewHolder extends RecyclerView.ViewHolder {
    NetworkImageView route_img;
    TextView title,time;

    public ViewHolder(View itemView) {
        super(itemView);
        route_img = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
        title = (TextView) itemView.findViewById(R.id.enjoy_title);
        time = (TextView) itemView.findViewById(R.id.enjoy_time);
    }
}

