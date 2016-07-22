
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.StrategyDetailActivity;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;

import java.util.List;

public class SpecialListAdapter extends AppendableAdapter<QHStrategy> {
    private Activity mContext;

    public SpecialListAdapter(Activity context) {
        this.mContext = context;
    }

    public SpecialListAdapter(Activity context, List<QHStrategy> items) {
        this(context);
        this.mDataItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_travel, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoViewHolder viewHolder = (VideoViewHolder) holder;
        final QHStrategy item = mDataItems.get(position);
        viewHolder.itemView.setTag(item);
        viewHolder.netImage.setTag(item);

        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View v) {

                QHStrategy item = (QHStrategy) v.getTag();
                Bundle bundle = new Bundle();
                bundle.putParcelable("guide",item);
                bundle.putInt("id",item.id);
                Intent intent  = new Intent();
                intent.putExtra("guide",bundle);
//                intent.putExtra(Const.SPECIAL_DETAIL_DATA, item);
                intent.setClass(mContext,StrategyDetailActivity.class);
                mContext.startActivity(intent);
            }
        });

         if (null!=item.guide_info&&item.guide_info.size()>0&&null!=item.guide_info.get(0)
                 &&!TextUtils.isEmpty(item.guide_info.get(0).image_url)){
//            RequestManager.getInstance().getImageLoader().get(
//                    item.guide_info.get(0).image_url,
//                    ImageLoader.getImageListener(viewHolder.netImage,
//                            R.drawable.recommand_bg,R.drawable.recommand_bg));
             ImageUtil.DisplayImage(item.guide_info.get(0).image_url, viewHolder.netImage,
                     R.drawable.recommand_bg,R.drawable.recommand_bg);
        }else
            viewHolder.netImage.setImageResource(R.drawable.recommand_bg);

        if (item.user.user_info!=null&&!TextUtils.isEmpty(item.user.user_info.avatar_url))
         {
//            RequestManager.getInstance().getImageLoader().get(
//                    item.user.user_info.avatar_url,
//                    ImageLoader.getImageListener(viewHolder.iv_me_avata,
//                            R.drawable.bg_image_hint,R.drawable.bg_image_hint));
             ImageUtil.DisplayImage(item.user.user_info.avatar_url, viewHolder.iv_me_avata,
                     R.drawable.bg_image_hint,R.drawable.bg_image_hint);
        }else {
            viewHolder.iv_me_avata.setImageResource(R.drawable.bg_avata_hint);
        }
        if (!TextUtils.isEmpty(item.title))
        viewHolder.travel_name.setText(item.title);
        if (item.user != null && item.user.user_info != null && !TextUtils.isEmpty(item.user.user_info.nickname))
        viewHolder.user_name.setText(item.user.user_info.nickname);
        if (!TextUtils.isEmpty(item.start_date)){
            String data = item.start_date.substring(0,item.start_date.indexOf(" "));
            viewHolder.travel_time.setText(data);
        }

    }

    static class VideoViewHolder extends ViewHolder {
        NetworkImageView netImage;
        NetworkImageView iv_me_avata;
        TextView travel_name,user_name,travel_time;
        public VideoViewHolder(View itemView) {
            super(itemView);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            iv_me_avata = (NetworkImageView) itemView.findViewById(R.id.iv_me_avata);
            travel_name = (TextView) itemView.findViewById(R.id.travel_name);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            travel_time = (TextView) itemView.findViewById(R.id.travel_time);
        }

    }
}
