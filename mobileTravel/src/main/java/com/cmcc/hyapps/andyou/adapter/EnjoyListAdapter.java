package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.QHEnjoy;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;

/**
 * Created by bingbing on 2015/8/20.
 */
public class EnjoyListAdapter extends AppendableAdapter<QHEnjoy> {
    private Activity mContext;
    public EnjoyListAdapter(Activity mContext){
        this.mContext = mContext;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.enjoy_list_item_layout,parent,false);
        return new EnjoyListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EnjoyListHolder enjoyListHolder = (EnjoyListHolder) holder;
       final QHEnjoy item = mDataItems.get(position);
        holder.itemView.setTag(item);
        ((EnjoyListHolder) holder).networkImageView.setTag(item);
        if (!TextUtils.isEmpty(item.getTitle()))
            enjoyListHolder.contentTextView.setText(item.getTitle());
        if (!TextUtils.isEmpty(item.getCover_image()))
            ImageUtil.DisplayImage(item.getCover_image(), enjoyListHolder.networkImageView);
        attachClickListener(enjoyListHolder,enjoyListHolder.networkImageView,position);
    }

    class EnjoyListHolder extends RecyclerView.ViewHolder{

        public EnjoyListHolder(View itemView) {
            super(itemView);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.enjoy_thumbnail);
            contentTextView = (TextView) itemView.findViewById(R.id.enjoy_content);
        }

        private NetworkImageView networkImageView;
        private TextView contentTextView;
    }
}
