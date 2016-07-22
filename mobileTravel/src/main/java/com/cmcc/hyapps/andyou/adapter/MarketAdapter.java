
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHMarketShop;
import com.cmcc.hyapps.andyou.model.QHShopsBanner;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.AutoScrollViewPager;
import com.cmcc.hyapps.andyou.widget.ScrollPoints;
import com.cmcc.hyapps.andyou.widget.roundimageview.RoundedImageView;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;

import java.util.List;

import static android.support.v4.view.ViewPager.OnPageChangeListener;

//QHHomeBanner.QHHomeBannerLists
//HomeBanner.HomeBannerLists
public class MarketAdapter extends BaseHeaderAdapter<QHShopsBanner.QHShopsBannerList, QHMarketShop> {
    private final String TAG = "MarketAdapter";
    private Activity mActivity;
    private static final int BANNER_SCROLL_INTERVAL = 2500;
    private BannerPagerAdapter.IActionCallback<QHShopsBanner> mActionCallback;
    Location mLocation;


    public MarketAdapter(Activity activity) {
        mActivity = activity;
    }

    public MarketAdapter(Activity activity, List<QHMarketShop> items) {
        this(activity);
        setDataItems(items);
    }

    public MarketAdapter(Activity activity, BannerPagerAdapter.IActionCallback<QHShopsBanner> actionCallback) {
        this(activity);
        mActionCallback = actionCallback;
    }

    class MarketItemViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView netImage;
        TextView item_name, item_price, item_address, item_sales;
        ImageView sales;
        View item_click_panel, item_price_layout;

        public MarketItemViewHolder(View itemView) {
            super(itemView);
            item_click_panel = itemView.findViewById(R.id.item_click_panel);
            item_price_layout = itemView.findViewById(R.id.home_item_price_layout);
            netImage = (RoundedImageView) itemView.findViewById(R.id.iv_cover_image);
            sales = (ImageView) itemView.findViewById(R.id.home_item_secnic_sales);
            item_name = (TextView) itemView.findViewById(R.id.home_item_secnic_name);
            item_price = (TextView) itemView.findViewById(R.id.home_item_intro);
            item_sales = (TextView) itemView.findViewById(R.id.home_item_sales);
            item_address = (TextView) itemView.findViewById(R.id.home_item_secnic_distance);
        }

        public void setDataTag(QHMarketShop shop) {
            itemView.setTag(shop);
            item_click_panel.setTag(shop);
        }
    }

    private class MarketHeaderViewHolder extends RecyclerView.ViewHolder {
        public AutoScrollViewPager banner;
        public ScrollPoints points;
        private TextView tab_food, tab_hotel, tab_special, tab_4s;
        //results
        private int all_points;

        public MarketHeaderViewHolder(View itemView, BannerPagerAdapter.IActionCallback<QHShopsBanner> actionCallback) {
            super(itemView);
            banner = (AutoScrollViewPager) itemView.findViewById(R.id.banner_pager);
            banner.setAdapter(new BannerPagerAdapter<QHShopsBanner>(itemView.getContext(), BannerPagerAdapter.Scene.MARKET).setInfiniteLoop(true).setActionCallback(actionCallback));
            banner.setInterval(BANNER_SCROLL_INTERVAL);
            banner.startAutoScroll();
            points = (ScrollPoints) itemView.findViewById(R.id.points);
            tab_food = (TextView) itemView.findViewById(R.id.home_tab_food);
            tab_hotel = (TextView) itemView.findViewById(R.id.home_tab_hotel);
            tab_special = (TextView) itemView.findViewById(R.id.home_tab_special);
            tab_4s = (TextView) itemView.findViewById(R.id.home_tab_4s);
            banner.setOnPageChangeListener(listener);

            all_points = mHeader.results.size();
            points.initPoints(mActivity, all_points, 0);
        }

        OnPageChangeListener listener = new OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {
                points.changeSelectedPoint(all_points == 0 ? 0 : index % all_points);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_fragment_header, parent, false);
        return new MarketHeaderViewHolder(v, mActionCallback);
    }

    @Override
    public void onBinderHeaderViewHolder(RecyclerView.ViewHolder holder) {
        MarketHeaderViewHolder headerHolder = (MarketHeaderViewHolder) holder;
        //BannerPagerAdapter<HomeBanner> bannerAdapter = (BannerPagerAdapter<HomeBanner>) headerHolder.banner.getAdapter();
        BannerPagerAdapter<QHShopsBanner> bannerAdapter = (BannerPagerAdapter<QHShopsBanner>) headerHolder.banner.getAdapter();
        if (bannerAdapter != null) {
            //list
            bannerAdapter.setBannerSlide(mHeader.results);
            headerHolder.banner.onDateSetChanged();
        }
        attachClickListener(headerHolder, headerHolder.tab_food, 0);
        attachClickListener(headerHolder, headerHolder.tab_hotel, 0);
        attachClickListener(headerHolder, headerHolder.tab_special, 0);
        attachClickListener(headerHolder, headerHolder.tab_4s, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext().getApplicationContext();
        View v = LayoutInflater.from(context).inflate(R.layout.market_fragment_item, parent, false);
        MarketItemViewHolder holder = new MarketItemViewHolder(v);
        return holder;
    }

    @Override
    public void onBinderItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mDataItems.size() <= position) {
            Log.e(TAG, "[onBinderItemViewHolder] position out of bound");
            return;
        }
        final QHMarketShop shop = mDataItems.get(position);

        if (shop == null) {
            Log.e(TAG, "[onBinderItemViewHolder] comment: " + shop);
            return;
        }


        final MarketItemViewHolder commentHolder = (MarketItemViewHolder) holder;
        commentHolder.setDataTag(shop);


        if (!TextUtils.isEmpty(shop.image_url)) {
            ImageUtil.DisplayImage(shop.image_url, commentHolder.netImage, R.drawable.recommand_bg,
                    R.drawable.recommand_bg);
        }
        if (shop.stype == 4) {
            commentHolder.item_sales.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(shop.promotion))
                commentHolder.item_sales.setText(shop.promotion);
            commentHolder.item_price_layout.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(shop.promotion)) {
                commentHolder.sales.setVisibility(View.VISIBLE);
            }
        } else {
            commentHolder.sales.setVisibility(View.GONE);
            commentHolder.item_sales.setVisibility(View.GONE);
            commentHolder.item_price_layout.setVisibility(View.VISIBLE);
            String format = mActivity.getString(R.string.home_restaurant_average_cost);
            commentHolder.item_price.setText(String.format(format, "" + shop.average));
        }
        commentHolder.item_name.setText(shop.name.trim());
        if (!TextUtils.isEmpty(shop.address))
            commentHolder.item_address.setText(shop.address.trim());
        attachClickListener(commentHolder, commentHolder.item_click_panel, position);
    }


    public void setMyLocation(Location mLocation) {
        this.mLocation = mLocation;
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
