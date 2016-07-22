package com.cmcc.hyapps.andyou.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.NavigationDetailActivity;
import com.cmcc.hyapps.andyou.model.QHEnjoy;
import com.cmcc.hyapps.andyou.model.QHEnjoyInfo;
import com.cmcc.hyapps.andyou.model.QHNavigation;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.roundimageview.RoundedImageView;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/7/15 0015.
 */
public class QHEntertainmentsDetailAdapter extends BaseHeaderAdapter<QHEnjoy, QHEnjoyInfo> {
    private final String TAG = "QHEnjoyDetailAdapter";
    private Activity mActivity;
    private QHEnjoy qhEnjoy;

    public QHEntertainmentsDetailAdapter(Activity activity, List<QHEnjoyInfo> list, QHEnjoy qhEnjoy) {
        this.mActivity = activity;
        this.qhEnjoy = qhEnjoy;
        setDataItems(list);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entertainments_detail_header, parent, false);
        return new HomeHeaderViewHolder(v);
    }

    @Override
    public void onBinderHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HomeHeaderViewHolder homeHeaderViewHolder = (HomeHeaderViewHolder) holder;
//        if (!TextUtils.isEmpty(mHeader.intro_text))
//        homeHeaderViewHolder.titleTextView.setText(mHeader.intro_text);
        attachClickListener(homeHeaderViewHolder, homeHeaderViewHolder.titleTextView, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entertainments_detail, parent, false);
        return new HomeItemViewHolder(view);
    }

    @Override
    public void onBinderItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeItemViewHolder homeItemViewHolder = (HomeItemViewHolder) holder;
        QHEnjoyInfo qhRouteInfo = mDataItems.get(position);
//        android.util.Log.e("QHEnjoyInfo",qhRouteInfo.getImage_url());
        if (qhRouteInfo == null) {
            Log.e(TAG, "[onBinderItemViewHolder] comment: " + qhRouteInfo);
            return;
        }
        homeItemViewHolder.setDataTag(mDataItems.get(position));

        if (!TextUtils.isEmpty(qhRouteInfo.getImage_url())) {
            ImageUtil.DisplayImage(qhRouteInfo.getImage_url(), homeItemViewHolder.networkImageView,
                    R.drawable.recommand_bg, R.drawable.recommand_bg);
        } else {
            homeItemViewHolder.networkImageView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(qhRouteInfo.getContent()))
            homeItemViewHolder.contentTextView.setText(qhRouteInfo.getContent().trim());
        else
            homeItemViewHolder.contentTextView.setVisibility(View.GONE);
        if (position == mDataItems.size() - 1) {
//            android.util.Log.e("执行＝"+mDataItems.size(),"TAG");
            if (qhEnjoy != null && !TextUtils.isEmpty(qhEnjoy.getAddress()) && qhEnjoy.longitude != 0 && qhEnjoy.latitude != 0) {
                homeItemViewHolder.linearLayout.setVisibility(View.VISIBLE);
                homeItemViewHolder.shop_adrress_tv.setText(qhEnjoy.getAddress());
                homeItemViewHolder.linearLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onValidClick(View v) {
//                        ToastUtils.show(mActivity, "点击");
                        final QHNavigation tag = new QHNavigation(qhEnjoy.longitude, qhEnjoy.latitude);
                        //得到经纬度，打开一个dialog
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setPositiveButton("开始导航", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(mActivity, NavigationDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("navi_detail", tag);
                                intent.putExtra("navi_bundle", bundle);
                                mActivity.startActivity(intent);
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("取消导航", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            }

        } else {
            homeItemViewHolder.linearLayout.setVisibility(View.GONE);
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
        private LinearLayout linearLayout;
        private TextView shop_adrress_tv;

        public HomeItemViewHolder(View itemView) {
            super(itemView);
            networkImageView = (RoundedImageView) itemView.findViewById(R.id.route_detail_image);
            contentTextView = (TextView) itemView.findViewById(R.id.route_detail_tv);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.shop_adrress_ll);
            shop_adrress_tv = (TextView) itemView.findViewById(R.id.shop_adrress_tv);
        }

        public void setDataTag(QHEnjoyInfo recommand) {
            itemView.setTag(recommand);
            networkImageView.setTag(recommand);
            linearLayout.setTag(recommand);
            shop_adrress_tv.setTag(recommand);
        }
    }
}
