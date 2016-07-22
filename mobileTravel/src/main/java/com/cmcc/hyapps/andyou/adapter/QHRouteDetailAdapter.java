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
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHRoute;
import com.cmcc.hyapps.andyou.model.QHRouteInfo;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.roundimageview.RoundedImageView;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/7/15 0015.
 */
public class QHRouteDetailAdapter extends BaseHeaderAdapter<QHRoute, QHRouteInfo> {
    private final String TAG = "QHRouteDetailAdapter";
    private Activity mActivity;
    public QHRouteDetailAdapter(Activity activity,List<QHRouteInfo> list){
        this.mActivity = activity;
        setDataItems(list);
    }
    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_detail_header, parent, false);
        return new HomeHeaderViewHolder(v);
    }

    @Override
    public void onBinderHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HomeHeaderViewHolder homeHeaderViewHolder = (HomeHeaderViewHolder) holder;
        if (!TextUtils.isEmpty(mHeader.intro_text))
        homeHeaderViewHolder.titleTextView.setText(mHeader.intro_text);
        attachClickListener(homeHeaderViewHolder, homeHeaderViewHolder.titleTextView, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_detail1, parent, false);
        return new HomeItemViewHolder(view);
    }

    @Override
    public void onBinderItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeItemViewHolder homeItemViewHolder = (HomeItemViewHolder) holder;
        QHRouteInfo qhRouteInfo = mDataItems.get(position);
        if (qhRouteInfo == null) {
            Log.e(TAG, "[onBinderItemViewHolder] comment: " + qhRouteInfo);
            return;
        }
        homeItemViewHolder.setDataTag(mDataItems.get(position));

        if (!TextUtils.isEmpty(qhRouteInfo.getImage_url())) {
//            homeItemViewHolder.networkImageView.setImageUrl(qhRouteInfo.getImage_url(),
//                    RequestManager.getInstance().getImageLoader());
//            homeItemViewHolder.networkImageView.setDefaultImageResId(R.drawable.recommand_bg);
//            homeItemViewHolder.networkImageView.setErrorImageResId(R.drawable.bg_image_error);
            ImageUtil.DisplayImage(qhRouteInfo.getImage_url(), homeItemViewHolder.networkImageView,
                    R.drawable.recommand_bg, R.drawable.bg_image_error);
        }else
            homeItemViewHolder.networkImageView.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(qhRouteInfo.getContent()))
            homeItemViewHolder.contentTextView.setText(qhRouteInfo.getContent());
        else {
            homeItemViewHolder.contentTextView.setVisibility(View.GONE);
        }
    }

    private class HomeHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        public HomeHeaderViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.route_detail_header);
        }
    }

    private class HomeItemViewHolder extends RecyclerView.ViewHolder {
        private NetworkImageView networkImageView;
        private TextView contentTextView;

        public HomeItemViewHolder(View itemView) {
            super(itemView);
            networkImageView = (RoundedImageView) itemView.findViewById(R.id.route_detail_image);
            contentTextView = (TextView) itemView.findViewById(R.id.route_detail_tv);
        }

        public void setDataTag(QHRouteInfo recommand) {
            itemView.setTag(recommand);
            networkImageView.setTag(recommand);
        }
    }
}
