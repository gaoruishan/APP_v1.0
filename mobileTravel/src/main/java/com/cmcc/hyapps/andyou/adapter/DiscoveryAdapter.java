
package com.cmcc.hyapps.andyou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHHomeBanner;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.BannerPagerAdapter.IActionCallback;
import com.cmcc.hyapps.andyou.adapter.BannerPagerAdapter.Scene;
import com.cmcc.hyapps.andyou.adapter.TripListAdapter.TripAdapterHelper;
import com.cmcc.hyapps.andyou.model.Trip;
import com.cmcc.hyapps.andyou.model.Trip.TripList;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.AutoScrollViewPager;

import java.util.List;

/**
 * @author kuloud
 */
public class DiscoveryAdapter extends BaseHeaderAdapter<QHHomeBanner.QHHomeBannerLists, QHScenic> {
    private static final int BANNER_SCROLL_INTERVAL = 2500;
    private IActionCallback<QHScenic> mActionCallback;
    private boolean mNoMorePage;
    private final String TAG = "DiscoveryAdapter";
    public DiscoveryAdapter() {
    }

    public DiscoveryAdapter(List<QHScenic> items) {
        setDataItems(items);
    }

    public DiscoveryAdapter(IActionCallback<QHScenic> actionCallback) {
        mActionCallback = actionCallback;
    }


    class DiscoverItemViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView netImage;
        TextView item_name, item_intro, item_distance;
        ImageView is_audio;

        public DiscoverItemViewHolder(View itemView) {
            super(itemView);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            is_audio = (ImageView) itemView.findViewById(R.id.home_item_isaudio);
            item_name = (TextView) itemView.findViewById(R.id.home_item_secnic_name);
            item_intro = (TextView) itemView.findViewById(R.id.home_item_intro);
            item_distance = (TextView) itemView.findViewById(R.id.home_item_secnic_distance);
        }

        public void setDataTag(QHScenic scenic) {
            itemView.setTag(scenic);
            netImage.setTag(scenic);
        }
    }

    @Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateHeaderViewHolder(
            ViewGroup parent) {
        return null;
    }

    @Override
    public void onBinderHeaderViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder) {
    }

    @Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext().getApplicationContext();
        View v = LayoutInflater.from(context).inflate(R.layout.home_fragment_item, parent, false);
        DiscoverItemViewHolder holder = new DiscoverItemViewHolder(v);
        return holder;
    }

    @Override
    public void onBinderItemViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder, int position) {
        if (mDataItems.size() <= position) {
            com.cmcc.hyapps.andyou.util.Log.e(TAG, "[onBinderItemViewHolder] position out of bound");
            return;
        }
        final QHScenic scenic = mDataItems.get(position);

        if (scenic == null) {
            com.cmcc.hyapps.andyou.util.Log.e(TAG, "[onBinderItemViewHolder] comment: " + scenic);
            return;
        }


        final DiscoverItemViewHolder commentHolder = (DiscoverItemViewHolder) holder;
        commentHolder.setDataTag(scenic);


        if (!TextUtils.isEmpty(scenic.image_url)) {
//            RequestManager.getInstance().getImageLoader().get(scenic.image_url,
//                    ImageLoader.getImageListener(commentHolder.netImage,
//                            R.drawable.bg_banner_hint, R.drawable.bg_banner_hint));
//            commentHolder.netImage.setImageUrl(scenic.image_url,
//                    RequestManager.getInstance().getImageLoader());
//            commentHolder.netImage.setErrorImageResId(R.color.transparency);
//            commentHolder.netImage.setDefaultImageResId(R.color.transparency);

            ImageUtil.DisplayImage(scenic.image_url, commentHolder.netImage,
                    R.color.transparency, R.color.transparency);
        }

//        if (scenic.have_video == 1)
//            commentHolder.is_audio.setVisibility(View.VISIBLE);
//        else commentHolder.is_audio.setVisibility(View.GONE);

        commentHolder.item_name.setText(scenic.name);
        commentHolder.item_intro.setText(scenic.intro_text.trim());
        LatLng start = new LatLng(scenic.latitude, scenic.longitude);
        LatLng end = new LatLng(ConstTools.myCurrentLoacation.latitude, ConstTools.myCurrentLoacation.longitude);

        commentHolder.item_distance.setText(ConstTools.getDistance(start,end));
        attachClickListener(commentHolder, commentHolder.netImage, position);
    }
    @Override
    public long getItemId(int position) {
        if (mDataItems != null && mDataItems.size() > position) {
            QHScenic trip = mDataItems.get(position);
            if (trip != null) {
                return trip.id;
            }
        }
        return super.getItemId(position);
    }

    public void setNoMorePage(boolean noMorePage) {
        this.mNoMorePage = noMorePage;
    }

}
