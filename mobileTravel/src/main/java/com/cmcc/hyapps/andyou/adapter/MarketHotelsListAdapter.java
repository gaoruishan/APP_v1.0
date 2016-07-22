
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.HotelDetailsActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.model.QHMarketShop;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;

import java.util.List;

public class MarketHotelsListAdapter extends AppendableAdapter<QHMarketShop> {
    private Activity mContext;

    public MarketHotelsListAdapter(Activity context) {
        this.mContext = context;
    }

    public MarketHotelsListAdapter(Activity context, List<QHMarketShop> items) {
        this(context);
        this.mDataItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_fragment_item, parent, false);
        return new FoodViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FoodViewHolder viewHolder = (FoodViewHolder) holder;
        final QHMarketShop item = mDataItems.get(position);
        viewHolder.itemView.setTag(item);
        viewHolder.item_click_panel.setTag(item);

        viewHolder.item_click_panel.setOnClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View v) {

                QHMarketShop item = (QHMarketShop) v.getTag();

                Intent intent  = new Intent();
                intent.putExtra(Const.REST_DETAIL, item);
                intent.setClass(mContext,HotelDetailsActivity.class);
                mContext.startActivity(intent);
            }
        });

        if (!TextUtils.isEmpty(item.image_url)){
//            viewHolder.netImage.setDefaultImageResId(R.drawable.recommand_bg);
//            viewHolder.netImage.setErrorImageResId(R.drawable.recommand_bg);
//            viewHolder.netImage.setImageUrl(item.image_url,
//                    RequestManager.getInstance().getImageLoader());

            ImageUtil.DisplayImage(item.image_url, viewHolder.netImage);
          //  RequestManager.getInstance().getImageLoader().get(item.image_url,ImageLoader.getImageListener(viewHolder.netImage, R.drawable.bg_image_hint,R.drawable.bg_image_hint));
        }


        viewHolder.item_name.setText(item.name);
        String format = mContext.getString(R.string.home_restaurant_average_cost);
        viewHolder.item_price.setText(String.format(format, "" + item.average));

        if (!TextUtils.isEmpty(item.address))
        viewHolder.item_introduction.setText(item.address);

    }

    static class FoodViewHolder extends ViewHolder {
        NetworkImageView netImage;
        TextView item_name, item_price, item_introduction;
        ImageView is_audio;
        View item_click_panel;

        public FoodViewHolder(View itemView) {
            super(itemView);
            item_click_panel = itemView.findViewById(R.id.item_click_panel);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            is_audio = (ImageView) itemView.findViewById(R.id.home_item_isaudio);
            item_name = (TextView) itemView.findViewById(R.id.home_item_secnic_name);
            item_price = (TextView) itemView.findViewById(R.id.home_item_intro);
            item_introduction = (TextView) itemView.findViewById(R.id.home_item_secnic_distance);

        }

    }
}
