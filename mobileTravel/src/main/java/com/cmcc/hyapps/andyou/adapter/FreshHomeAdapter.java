
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

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHHomeBanner;
import com.cmcc.hyapps.andyou.model.Recommand;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.AutoScrollViewPager;
import com.cmcc.hyapps.andyou.widget.ScrollPoints;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;

import java.util.List;

import static android.support.v4.view.ViewPager.OnPageChangeListener;

//QHHomeBanner.QHHomeBannerLists
//HomeBanner.HomeBannerLists
public class FreshHomeAdapter extends BaseHeaderAdapter<QHHomeBanner.QHHomeBannerLists, Recommand> {
    private final String TAG = "FreshHomeAdapter";
    private Activity mActivity;
    private static final int BANNER_SCROLL_INTERVAL = 2500;
    private BannerPagerAdapter.IActionCallback<QHHomeBanner> mActionCallback;
    Location mLocation;

    public FreshHomeAdapter(Activity activity) {
        mActivity = activity;
    }

    public FreshHomeAdapter(Activity activity, List<Recommand> items) {
        this(activity);
        setDataItems(items);
    }

    public FreshHomeAdapter(Activity activity, BannerPagerAdapter.IActionCallback<QHHomeBanner> actionCallback) {
        this(activity);
        mActionCallback = actionCallback;
    }

    class HomeItemViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView netImage;
        TextView item_name, item_intro, item_distance,item_type;
        ImageView is_audio;

        public HomeItemViewHolder(View itemView) {
            super(itemView);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            is_audio = (ImageView) itemView.findViewById(R.id.home_item_isaudio);
            item_name = (TextView) itemView.findViewById(R.id.home_item_recommand_name);
            item_intro = (TextView) itemView.findViewById(R.id.home_item_recommand_intro);
            item_distance = (TextView) itemView.findViewById(R.id.home_item_secnic_distance);
            item_type = (TextView) itemView.findViewById(R.id.iv_recommand_type);
        }

        public void setDataTag(Recommand recommand) {
            itemView.setTag(recommand);
            netImage.setTag(recommand);
        }
    }

    private class HomeHeaderViewHolder extends RecyclerView.ViewHolder {
        public AutoScrollViewPager banner;
        public ScrollPoints points;
        private TextView tab_live, tab_strategy, tab_guide,tab_road_condition;
        //results
        private int all_points;

        public HomeHeaderViewHolder(View itemView, BannerPagerAdapter.IActionCallback<QHHomeBanner> actionCallback) {
            super(itemView);
            banner = (AutoScrollViewPager) itemView.findViewById(R.id.banner_pager);
            banner.setAdapter(new BannerPagerAdapter<QHHomeBanner>(itemView.getContext(), BannerPagerAdapter.Scene.HOME).setInfiniteLoop(true).setActionCallback(actionCallback));
            banner.setInterval(BANNER_SCROLL_INTERVAL);
            banner.startAutoScroll();
            points = (ScrollPoints) itemView.findViewById(R.id.points);
            tab_live = (TextView) itemView.findViewById(R.id.home_tab_live);
            tab_strategy = (TextView) itemView.findViewById(R.id.home_tab_strategy);
            tab_guide = (TextView) itemView.findViewById(R.id.home_tab_nav);
            tab_road_condition = (TextView) itemView.findViewById(R.id.home_tab_road_condition);
            banner.setOnPageChangeListener(listener);

            all_points = mHeader.results.size();
            points.initPoints(mActivity, all_points, 0);
        }

        OnPageChangeListener listener = new OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {
                points.changeSelectedPoint(all_points == 0 ? 0 : index %all_points);
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
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fresh_home_fragment_header, parent, false);
        return new HomeHeaderViewHolder(v, mActionCallback);
    }

    @Override
    public void onBinderHeaderViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder) {
        HomeHeaderViewHolder headerHolder = (HomeHeaderViewHolder) holder;
        //BannerPagerAdapter<HomeBanner> bannerAdapter = (BannerPagerAdapter<HomeBanner>) headerHolder.banner.getAdapter();
        BannerPagerAdapter<QHHomeBanner> bannerAdapter = (BannerPagerAdapter<QHHomeBanner>) headerHolder.banner.getAdapter();
        if (bannerAdapter != null) {
            //list
            bannerAdapter.setBannerSlide(mHeader.results);
            headerHolder.banner.onDateSetChanged();
        }
        attachClickListener(headerHolder, headerHolder.tab_live, 0);
        attachClickListener(headerHolder, headerHolder.tab_strategy, 0);
        attachClickListener(headerHolder, headerHolder.tab_guide, 0);
        attachClickListener(headerHolder, headerHolder.tab_road_condition, 0);
    }

    @Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext().getApplicationContext();
        View v = LayoutInflater.from(context).inflate(R.layout.fresh_home_fragment_item, parent, false);
        HomeItemViewHolder holder = new HomeItemViewHolder(v);
        return holder;
    }

    @Override
    public void onBinderItemViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder, int position) {
        if (mDataItems.size() <= position) {
            Log.e(TAG, "[onBinderItemViewHolder] position out of bound");
            return;
        }
        final Recommand recommand = mDataItems.get(position);

        if (recommand == null) {
            Log.e(TAG, "[onBinderItemViewHolder] comment: " + recommand);
            return;
        }


        final HomeItemViewHolder commentHolder = (HomeItemViewHolder) holder;
        commentHolder.setDataTag(recommand);
        ImageUtil.DisplayImage(recommand.image_url, commentHolder.netImage);
        commentHolder.item_name.setText(recommand.name);
        if (!TextUtils.isEmpty(recommand.content)){
            commentHolder.item_intro.setText(recommand.content);
        }
        String type = null;
        if (recommand.stype ==1){
            switch (recommand.getSecondStype()){
                case 1:
                    type = "酒店";
                    break;
                case 2:
                    type = "美食";
                    break;
                case 3:
                    type = "特产";
                    break;
                case 4:
                    type = "4s店";
                    break;
            }
        }else if (recommand.stype ==0){
            type = "景区";
        }
        if (!TextUtils.isEmpty(type)) {
            commentHolder.item_type.setText(type);
        }
        attachClickListener(commentHolder, commentHolder.netImage, position);
    }


    public void setMyLocation(Location mLocation) {
        this.mLocation = mLocation;
    }



}
