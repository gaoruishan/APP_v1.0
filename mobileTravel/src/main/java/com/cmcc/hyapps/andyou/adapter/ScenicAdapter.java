package com.cmcc.hyapps.andyou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHHomeBanner;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.LocationUtil;
import com.cmcc.hyapps.andyou.widget.AutoScrollViewPager;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by Edward on 2015/5/1.
 */
public class ScenicAdapter extends BaseHeaderAdapter<QHHomeBanner.QHHomeBannerLists, QHScenic> implements View.OnClickListener {
    private final String TAG = "GuideAdapter";

    private AutoScrollViewPager mAutoScrollView;

    protected Context mContext;

    List<ImageView> mImageViews = new ArrayList<ImageView>();

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        public void onItemClicked(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public ScenicAdapter(Context context) {
        mContext = context;
    }

    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View view = View.inflate(mContext, R.layout.home_fragment_item, null);
        return new HeaderHolder(view);
    }

    @Override
    public void onBinderHeaderViewHolder(RecyclerView.ViewHolder holder) {
//        HeaderHolder header = (HeaderHolder)viewHolder;
//
//        mAutoScrollView = header.mAutoScrollView;
//        mAutoScrollView.setAdapter(new BannerAdapter(mContext, mHeader));
//        mAutoScrollView.startAutoScroll();
//
//        header.mStrategyButton.setOnClickListener(this);
//        header.mRouteButton.setOnClickListener(this);
//        header.mNavigationButton.setOnClickListener(this);
//        header.mSearchEdit.setOnClickListener(this);
    }

    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup viewGroup, int i) {
        View v = View.inflate(mContext, R.layout.home_fragment_item, null);
        return new HomeItemViewHolder(v);
    }

    @Override
    public void onBinderItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mDataItems.size() < position) {
            Log.e(TAG, "[onBinderItemViewHolder] position out of bound");
            return;
        }
        final QHScenic scenic = mDataItems.get(position);
        if (scenic == null) {
            Log.e(TAG, "[onBinderItemViewHolder] comment: " + scenic);
            return;
        }

        final HomeItemViewHolder commentHolder = (HomeItemViewHolder) holder;
        commentHolder.setDataTag(scenic);

        if (!TextUtils.isEmpty(scenic.image_url)) {
//            commentHolder.netImage.setDefaultImageResId(R.drawable.bg_banner_hint);
//            commentHolder.netImage.setErrorImageResId(R.drawable.bg_banner_hint);
//            commentHolder.netImage.setImageUrl(scenic.image_url,
//                    RequestManager.getInstance().getImageLoader());

            ImageUtil.DisplayImage(scenic.image_url, commentHolder.netImage,
                    R.drawable.bg_banner_hint, R.drawable.bg_banner_hint);
        }
        commentHolder.item_name.setText(scenic.name);
        commentHolder.item_intro.setText("");

        String distance = LocationUtil.getInstance(mContext).getDistance(scenic.latitude, scenic.longitude);

        commentHolder.item_distance.setText(distance);


        commentHolder.netImage.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (view.getId()){
            case R.id.iv_cover_image:
//                QHStrategy item = (QHStrategy) view.getTag();
//                Intent intent = new Intent(mContext,GuideDetailActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("id",item.id);
//                bundle.putParcelable("guide_info",item);
//                intent.putExtra("guide_bundle",bundle);
//                mContext.startActivity(intent);
                break;
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder{

        AutoScrollViewPager mAutoScrollView;
        TextView mSearchEdit;
        TextView mStrategyButton;
        TextView mRouteButton;
        TextView mNavigationButton;

        public HeaderHolder(View headerView) {
            super(headerView);
            mAutoScrollView = (AutoScrollViewPager) headerView.findViewById(R.id.banner_pager);
            mSearchEdit = (TextView) headerView.findViewById(R.id.home_search_et);
            mStrategyButton = (TextView) headerView.findViewById(R.id.home_tab_guide);
        }
    }

    class HomeItemViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView netImage;
        TextView item_name,item_intro,item_distance;
        ImageView is_audio;

        public HomeItemViewHolder(View itemView) {
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

}
