
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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.RestaurantDetailActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHMarketShop;
import com.cmcc.hyapps.andyou.support.OnClickListener;

import java.util.List;

public class MarketSearchListAdapter extends AppendableAdapter<QHMarketShop> {
    private Activity mContext;

    public MarketSearchListAdapter(Activity context) {
        this.mContext = context;
    }

    public MarketSearchListAdapter(Activity context, List<QHMarketShop> items) {
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

        if (!TextUtils.isEmpty(item.image_url)) {
            ImageUtil.DisplayImage(item.image_url, viewHolder.netImage,
                    R.drawable.bg_image_hint, R.drawable.bg_image_hint);
        }
        if (item.stype == 4) {
            viewHolder.item_sales.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(item.promotion))
                viewHolder.item_sales.setText(item.promotion);
            viewHolder.item_price_layout.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(item.promotion)){
                viewHolder.sales.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.sales.setVisibility(View.GONE);
            viewHolder.item_sales.setVisibility(View.GONE);
            viewHolder.item_price_layout.setVisibility(View.VISIBLE);
            String format = mContext.getString(R.string.home_restaurant_average_cost);
            viewHolder.item_price.setText(String.format(format, "" + item.average));
        }
        viewHolder.item_name.setText(item.name);
        viewHolder.item_address.setText(item.address);

        attachClickListener(viewHolder, viewHolder.item_click_panel, position);
    }

    static class FoodViewHolder extends ViewHolder {
        NetworkImageView netImage;
        TextView item_name, item_price, item_address, item_sales;
        ImageView sales;
        View item_click_panel, item_price_layout;

        public FoodViewHolder(View itemView) {
            super(itemView);
            item_click_panel = itemView.findViewById(R.id.item_click_panel);
            item_price_layout = itemView.findViewById(R.id.home_item_price_layout);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            sales = (ImageView) itemView.findViewById(R.id.home_item_secnic_sales);
            item_name = (TextView) itemView.findViewById(R.id.home_item_secnic_name);
            item_price = (TextView) itemView.findViewById(R.id.home_item_intro);
            item_sales = (TextView) itemView.findViewById(R.id.home_item_sales);
            item_address = (TextView) itemView.findViewById(R.id.home_item_secnic_distance);

        }

    }

    @Override
    public void appendDataItems(List<QHMarketShop> dataItems) {
        List<QHMarketShop> shops = getDataItems();
        for (QHMarketShop shop : shops) {
            for (int i = 0; i < dataItems.size(); i++) {
                QHMarketShop item = dataItems.get(i);
                if (shop.id == item.id && shop.stype == item.stype) {
                    dataItems.remove(item);
                }
            }
        }

        if (dataItems.isEmpty())
            return;

        super.appendDataItems(dataItems);
    }
}
