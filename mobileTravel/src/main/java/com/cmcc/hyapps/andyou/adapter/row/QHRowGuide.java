/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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


/**
 * @author kuloud
 */
public class QHRowGuide {
    public static RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        //old home_special_item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_travel,
                parent, false);
        return new ViewHolder(v);
    }

    public static void onBindViewHolder(final Context mContext,RecyclerView.ViewHolder holder, int position, final QHStrategy item) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putParcelable("guide",item);
                bundle.putInt("id",item.id);
                Intent intent  = new Intent();
                intent.putExtra("guide",bundle);
//                intent.putExtra();
//                intent.putExtra(Const.SPECIAL_DETAIL_DATA, item);
                intent.setClass(mContext,StrategyDetailActivity.class);
                mContext.startActivity(intent);
            }
        });

//        if (null!=item.guide_info&&item.guide_info.size()>0&&null!=item.guide_info.get(0)
//                &&!TextUtils.isEmpty(item.guide_info.get(0).image_url)){
//            ImageUtil.DisplayImage(item.guide_info.get(0).image_url, viewHolder.netImage, R.drawable.recommand_bg,
//                    R.drawable.recommand_bg);
//        }else {
//            viewHolder.netImage.setImageResource(R.drawable.recommand_bg);
//        }

        ImageUtil.DisplayImage(item.cover_image, viewHolder.netImage, R.drawable.recommand_bg,
                    R.drawable.recommand_bg);
            if (item.user!= null && item.user.user_info != null)
            ImageUtil.DisplayImage(item.user.user_info.avatar_url, viewHolder.iv_me_avata,
                    R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);

        if (!TextUtils.isEmpty(item.title))
        viewHolder.item_title.setText(item.title);
        if (item.user != null && !TextUtils.isEmpty(item.user.username))
        viewHolder.item_user_name.setText(item.user.username);
        if (!TextUtils.isEmpty(item.start_date))
        viewHolder.item_time.setText(item.start_date);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView netImage;
        NetworkImageView iv_me_avata;
        TextView item_title,item_user_name,item_time;

        public ViewHolder(View itemView) {
            super(itemView);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            iv_me_avata = (NetworkImageView) itemView.findViewById(R.id.iv_me_avata);
            item_title = (TextView) itemView.findViewById(R.id.travel_name);
            item_user_name = (TextView) itemView.findViewById(R.id.user_name);
            item_time = (TextView) itemView.findViewById(R.id.travel_time);
        }
    }
}
