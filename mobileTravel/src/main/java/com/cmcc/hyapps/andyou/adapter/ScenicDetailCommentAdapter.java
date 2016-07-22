
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.activity.NavigationDetailActivity;
import com.cmcc.hyapps.andyou.model.QHNavigation;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.WrapLinearLayout;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemClickListener;
import com.umeng.analytics.MobclickAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.PhotoPreviewActivity;
import com.cmcc.hyapps.andyou.activity.SecnicActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.CompoundImage;
import com.cmcc.hyapps.andyou.model.CompoundImage.TextImage;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.QHHomeBanner;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.AutoScrollViewPager;
import com.cmcc.hyapps.andyou.widget.ExpandableTextView;
import com.cmcc.hyapps.andyou.widget.ScrollPoints;

import java.util.ArrayList;
import java.util.List;

import static com.cmcc.hyapps.andyou.R.color.base_bg;

public class ScenicDetailCommentAdapter extends BaseHeaderAdapter<QHScenic, QHScenic> {
    private static final int BANNER_SCROLL_INTERVAL = 2500;
    private final String TAG = "ScenicDetailCommentAdapter";
    private Activity mActivity;
    private List<Comment> items;

    public ScenicDetailCommentAdapter(Activity activity) {
        mActivity = activity;
    }

    public ScenicDetailCommentAdapter(Activity activity, List<QHScenic> items) {
        this(activity);
        setDataItems(items);
    }

    static class NearbyViewHolder extends ViewHolder {

        View first_scenic;
        NetworkImageView first_image;
        TextView first_name;

        public NearbyViewHolder(View itemView) {
            super(itemView);
            first_scenic = itemView.findViewById(R.id.first_scenic);
            first_image = (NetworkImageView) itemView.findViewById(R.id.first_image);
            first_name = (TextView) itemView.findViewById(R.id.first_name);
//
//            second_scenic = itemView.findViewById(R.id.second_scenic);
//            second_image = (NetworkImageView)itemView.findViewById(R.id.second_image);
//            second_name = (TextView)itemView.findViewById(R.id.second_name);
        }

        public void setDataTag(QHScenic scenic) {
            first_scenic.setTag(scenic);
            first_image.setTag(scenic);
        }

    }


    private int size;
    private int size1;
    private int size2;
    private int tag;
    private int count;
    private String tagStr;

    class HeaderViewHolder extends ViewHolder {
        ImageView coverImage;

        NetworkImageView scenery_detail_buttom_photo1;
        NetworkImageView scenery_detail_buttom_photo2;
        NetworkImageView scenery_detail_buttom_photo3;

        NetworkImageView scenery_detail_buttom_shop1;
        NetworkImageView scenery_detail_buttom_shop2;
        NetworkImageView scenery_detail_buttom_shop3;
        NetworkImageView scenery_detail_buttom_pic1;
        NetworkImageView scenery_detail_buttom_pic2;
        NetworkImageView scenery_detail_buttom_pic3;
        TextView tv_pic_title1;
        TextView tv_pic_title12;
        TextView tv_pic_title13;
        TextView tv_shop_title1;
        TextView tv_shop_title12;
        TextView tv_shop_title13;
        TextView introTitle;
        TextView introSummary;
        TextView wishToGo;
        View introContainer;
        ExpandableTextView scenicInfo;
        TextView surveyDetail;
        TextView comment;
        RelativeLayout comment_total, pics_total, shop_total, scenci_total;

        TextView scenicName;
        RatingBar scenicRating;

        View downloadPackage, funcListen, funcNavi, funcLive, funcService;

        LinearLayout scenci_header_ll_tags;
        TextView nearbyCount;

        View weatherContainer;
        ImageView weatherIcon, detail_arrow_photos, detail_arrow_shops, detail_arrow_scenci;
        TextView weatherText;
        TextView tempRange;
        TextView pm25, ll_tags1, ll_tags2, ll_tags3;

        AutoScrollViewPager scenic_banner_pager;
        ScrollPoints points;
        RecyclerView nearby_scenic;
        private int all_points;

        private TextView addressTextView;
        private View addressView;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            coverImage = (ImageView) itemView.findViewById(R.id.scenic_cover_image);
            introTitle = (TextView) itemView.findViewById(R.id.scenic_intro_title);
            introSummary = (TextView) itemView.findViewById(R.id.scenic_intro_summary);
            wishToGo = (TextView) itemView.findViewById(R.id.scenic_wish_to_go);
            scenicInfo = (ExpandableTextView) itemView.findViewById(R.id.scenic_info);
            surveyDetail = (TextView) itemView.findViewById(R.id.survey_detail_tv);
            comment = (TextView) itemView.findViewById(R.id.comment);
            comment_total = (RelativeLayout) itemView.findViewById(R.id.comment_total);
            pics_total = (RelativeLayout) itemView.findViewById(R.id.pics_total);
            shop_total = (RelativeLayout) itemView.findViewById(R.id.shop_total);
            scenci_total = (RelativeLayout) itemView.findViewById(R.id.scenci_total);
            //scenci_header_ll_tags = (LinearLayout) itemView.findViewById(R.id.scenci_header_ll_tags);
            scenicName = (TextView) itemView.findViewById(R.id.scenic_name);
            scenicRating = (RatingBar) itemView.findViewById(R.id.scenic_rating);

            downloadPackage = itemView.findViewById(R.id.scenic_detail_download);
            funcListen = itemView.findViewById(R.id.scenic_detail_func_listen);
            funcNavi = itemView.findViewById(R.id.scenic_detail_func_navi);
            funcLive = itemView.findViewById(R.id.scenic_detail_func_live);
            funcService = itemView.findViewById(R.id.scenic_detail_func_service);
            introContainer = itemView.findViewById(R.id.scenic_intro_container);

            weatherContainer = itemView.findViewById(R.id.weather_container);
            weatherIcon = (ImageView) itemView.findViewById(R.id.weather_icon);
            scenery_detail_buttom_photo1 = (NetworkImageView) itemView.findViewById(R.id.scenery_detail_buttom_photo1);
            scenery_detail_buttom_photo2 = (NetworkImageView) itemView.findViewById(R.id.scenery_detail_buttom_photo2);
            scenery_detail_buttom_photo3 = (NetworkImageView) itemView.findViewById(R.id.scenery_detail_buttom_photo3);

            scenery_detail_buttom_shop1 = (NetworkImageView) itemView.findViewById(R.id.scenery_detail_buttom_shop1);
            scenery_detail_buttom_shop2 = (NetworkImageView) itemView.findViewById(R.id.scenery_detail_buttom_shop2);
            scenery_detail_buttom_shop3 = (NetworkImageView) itemView.findViewById(R.id.scenery_detail_buttom_shop3);

            scenery_detail_buttom_pic1 = (NetworkImageView) itemView.findViewById(R.id.scenery_detail_buttom_pic1);
            scenery_detail_buttom_pic2 = (NetworkImageView) itemView.findViewById(R.id.scenery_detail_buttom_pic2);
            scenery_detail_buttom_pic3 = (NetworkImageView) itemView.findViewById(R.id.scenery_detail_buttom_pic3);

            weatherIcon = (ImageView) itemView.findViewById(R.id.weather_icon);
            weatherIcon = (ImageView) itemView.findViewById(R.id.weather_icon);
            detail_arrow_photos = (ImageView) itemView.findViewById(R.id.detail_arrow_photos);
            detail_arrow_shops = (ImageView) itemView.findViewById(R.id.detail_arrow_shops);
            detail_arrow_scenci = (ImageView) itemView.findViewById(R.id.detail_arrow_scenci);
            weatherText = (TextView) itemView.findViewById(R.id.weather_text);
            tempRange = (TextView) itemView.findViewById(R.id.temp_range);
            pm25 = (TextView) itemView.findViewById(R.id.pm2_5);
//            ll_tags1 = (TextView) itemView.findViewById(R.id.ll_tags1);
//            ll_tags2 = (TextView) itemView.findViewById(R.id.ll_tags2);
//            ll_tags3 = (TextView) itemView.findViewById(R.id.ll_tags3);
            tv_pic_title1 = (TextView) itemView.findViewById(R.id.tv_pic_title1);
            tv_pic_title12 = (TextView) itemView.findViewById(R.id.tv_pic_title12);
            tv_pic_title13 = (TextView) itemView.findViewById(R.id.tv_pic_title13);
            tv_shop_title1 = (TextView) itemView.findViewById(R.id.tv_shop_title1);
            tv_shop_title12 = (TextView) itemView.findViewById(R.id.tv_shop_title12);
            tv_shop_title13 = (TextView) itemView.findViewById(R.id.tv_shop_title13);
            addressTextView = (TextView) itemView.findViewById(R.id.survey_detail_location_textview);
            addressView = itemView.findViewById(R.id.survey_detail_location_layout);

//            newComment = itemView.findViewById(R.id.scenic_new_comment);
            nearbyCount = (TextView) itemView.findViewById(R.id.nearby_count);

            points = (ScrollPoints) itemView.findViewById(R.id.points);

            scenic_banner_pager = (AutoScrollViewPager) itemView.findViewById(R.id.scenic_banner_pager);
            scenic_banner_pager.setOnPageChangeListener(listener);
            scenic_banner_pager.setInterval(BANNER_SCROLL_INTERVAL);
            scenic_banner_pager.startAutoScroll();
//            nearby_scenic = (RecyclerView) itemView.findViewById(R.id.nearby_scenic);
            if (mHeader != null) {
                if (mHeader.scenic_video != null) {
                    all_points = mHeader.scenic_video.size();
                    points.initPoints(mActivity, all_points, 0);
                }else {
                    scenic_banner_pager.setVisibility(View.GONE);
                    coverImage.setVisibility(View.VISIBLE);
                }

            }
        }

        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
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

    private void setTextView(Context context, int j, LinearLayout ll) {
        final TextView tv = new TextView(context);

        int scap_Maxwidth = ScreenUtils.dpToPxInt(context, 100);

        tv.setTag("true");
        final String txt = mHeader.tag.get(j).toString();
        tv.setTextColor(COLOR_CHANGE);
        tv.setBackgroundResource(R.drawable.bg_tv_corner_stroke);
        tv.setText(txt);
        tv.setMaxWidth(scap_Maxwidth);
        tv.setGravity(Gravity.CENTER);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setSingleLine(true);
        ll.addView(tv);

    }

    @Override
    public ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_scenic_detail_header, parent, false);
        WrapLinearLayout ll = (WrapLinearLayout) v.findViewById(R.id.discover_tags_ll);
//        int scap_Mar = ScreenUtils.dpToPxInt(parent.getContext(), 20);
//        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(scap_Mar, scap_Mar / 2, scap_Mar, scap_Mar / 2);
//        ll.setLayoutParams(layoutParams);
//        ll.setBackgroundColor(base_bg);
        if (mHeader != null && mHeader.tag != null) {
            for (int i = 0; i < mHeader.tag.size(); i++) {
                setTextView(parent.getContext(), i, ll);
            }
        }
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBinderHeaderViewHolder(ViewHolder holder) {
         final QHScenic scenic = mHeader;
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.scenicName.setText(scenic.name);

        // headerHolder.introTitle.setText(scenic.introTitle);
        headerHolder.introTitle.setText(R.string.scenery_detail_play_title);
//        if (Double.parseDouble(scenic.ticket_price) == 0){
//             introText = scenic.intro_text + '\n' + '\n' + "门票价格：免费";
//        }else
        String introText = scenic.intro_text + '\n' + '\n' + "门票价格：" + scenic.ticket_price ;
        if (!TextUtils.isEmpty(introText)) {
            introText = introText.trim();
        }
        if (!TextUtils.isEmpty(scenic.address))
        headerHolder.addressTextView.setText(scenic.address.trim());
        headerHolder.addressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    final QHNavigation tag = new QHNavigation(scenic.longitude,scenic.latitude);
                    //得到经纬度，打开一个dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setPositiveButton("开始导航",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(mActivity,NavigationDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("navi_detail",tag);
                            intent.putExtra("navi_bundle",bundle);
                            mActivity.startActivity(intent);
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消导航",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
        });
        headerHolder.introSummary.setText("");
        headerHolder.scenicInfo.setText("");

        headerHolder.surveyDetail.setText(introText);
        headerHolder.comment.setText("评论" + mHeader.comment_count + "条");

        if (mHeader.scenic_video != null&&mHeader.scenic_video.size()>0) {
            headerHolder.coverImage.setVisibility(View.GONE);
            BannerPagerAdapter<QHScenic.QHVideo> bannerAdapter = new BannerPagerAdapter<QHScenic.QHVideo>(mActivity, BannerPagerAdapter.Scene.VIDEO);
            bannerAdapter.setBannerSlide(mHeader.scenic_video);
            headerHolder.scenic_banner_pager.setAdapter(bannerAdapter);
            if (mHeader.scenic_video.size() == 1)
                headerHolder.points.setVisibility(View.INVISIBLE);
        } else {
            headerHolder.scenic_banner_pager.setVisibility(View.GONE);
            headerHolder.coverImage.setVisibility(View.VISIBLE);
            ImageUtil.DisplayImage(scenic.image_url, headerHolder.coverImage, R.drawable.recommand_bg,
                    R.drawable.recommand_bg);
        }


        attachClickListener(headerHolder, headerHolder.coverImage, 0);
        attachClickListener(headerHolder, headerHolder.wishToGo, 0);
        attachClickListener(headerHolder, headerHolder.downloadPackage, 0);
        attachClickListener(headerHolder, headerHolder.comment_total, 0);


        attachClickListener(headerHolder, headerHolder.funcListen, 0);
        attachClickListener(headerHolder, headerHolder.funcNavi, 0);
        attachClickListener(headerHolder, headerHolder.funcLive, 0);
        attachClickListener(headerHolder, headerHolder.funcService, 0);
        attachClickListener(headerHolder, headerHolder.introContainer, 0);
        attachClickListener(headerHolder, headerHolder.weatherContainer, 0);
        attachClickListener(headerHolder, headerHolder.scenery_detail_buttom_photo1, 0);
        attachClickListener(headerHolder, headerHolder.scenery_detail_buttom_photo2, 0);
        attachClickListener(headerHolder, headerHolder.scenery_detail_buttom_photo3, 0);
        attachClickListener(headerHolder, headerHolder.scenery_detail_buttom_shop1, 0);
        attachClickListener(headerHolder, headerHolder.scenery_detail_buttom_shop2, 0);
        attachClickListener(headerHolder, headerHolder.scenery_detail_buttom_shop3, 0);
        attachClickListener(headerHolder, headerHolder.scenery_detail_buttom_pic1, 0);
        attachClickListener(headerHolder, headerHolder.scenery_detail_buttom_pic2, 0);
        attachClickListener(headerHolder, headerHolder.scenery_detail_buttom_pic3, 0);


        if (scenic.photos != null) {
            size = scenic.photos.size();
        }
//
        if (size >= 3) {
            ImageUtil.DisplayImage(scenic.photos.get(0).image_url,
                    headerHolder.scenery_detail_buttom_photo1);
            ImageUtil.DisplayImage(scenic.photos.get(1).image_url,
                    headerHolder.scenery_detail_buttom_photo2);
            ImageUtil.DisplayImage(scenic.photos.get(2).image_url,
                    headerHolder.scenery_detail_buttom_photo3);
//            headerHolder.scenery_detail_buttom_photo1.setImageUrl(scenic.photos.get(0).image_url, RequestManager.getInstance()
//                    .getImageLoader());
//            headerHolder.scenery_detail_buttom_photo2.setImageUrl(scenic.photos.get(1).image_url, RequestManager.getInstance()
//                    .getImageLoader());
//            headerHolder.scenery_detail_buttom_photo3.setImageUrl(scenic.photos.get(2).image_url, RequestManager.getInstance()
//                    .getImageLoader());
            headerHolder.scenery_detail_buttom_photo1.setVisibility(View.VISIBLE);
            headerHolder.scenery_detail_buttom_photo2.setVisibility(View.VISIBLE);
            headerHolder.scenery_detail_buttom_photo3.setVisibility(View.VISIBLE);
            attachClickListener(headerHolder, headerHolder.pics_total, 0);
        } else if (size == 2) {
            ImageUtil.DisplayImage(scenic.photos.get(0).image_url,
                    headerHolder.scenery_detail_buttom_photo1);
            ImageUtil.DisplayImage(scenic.photos.get(1).image_url,
                    headerHolder.scenery_detail_buttom_photo2);

//            headerHolder.scenery_detail_buttom_photo1.setImageUrl(scenic.photos.get(0).image_url, RequestManager.getInstance()
//                    .getImageLoader());
//            headerHolder.scenery_detail_buttom_photo2.setImageUrl(scenic.photos.get(1).image_url, RequestManager.getInstance()
//                    .getImageLoader());
            headerHolder.scenery_detail_buttom_photo1.setVisibility(View.VISIBLE);
            headerHolder.scenery_detail_buttom_photo2.setVisibility(View.VISIBLE);
            attachClickListener(headerHolder, headerHolder.pics_total, 0);
        } else if (size == 1) {
            ImageUtil.DisplayImage(scenic.photos.get(0).image_url,
                    headerHolder.scenery_detail_buttom_photo1);
//            headerHolder.scenery_detail_buttom_photo1.setImageUrl(scenic.photos.get(0).image_url, RequestManager.getInstance()
//                    .getImageLoader());
            headerHolder.scenery_detail_buttom_photo1.setVisibility(View.VISIBLE);
            attachClickListener(headerHolder, headerHolder.pics_total, 0);
        } else {
//            ToastUtils.show(mActivity, "信息不完整！");
            headerHolder.detail_arrow_photos.setVisibility(View.GONE);
        }
        if (scenic.nearby_shop != null) {
            size1 = scenic.nearby_shop.size();
        }
        if (size1 >= 3) {
            ImageUtil.DisplayImage(scenic.nearby_shop.get(0).image_url,
                    headerHolder.scenery_detail_buttom_shop1);
            ImageUtil.DisplayImage(scenic.nearby_shop.get(1).image_url,
                    headerHolder.scenery_detail_buttom_shop2);
            ImageUtil.DisplayImage(scenic.nearby_shop.get(2).image_url,
                    headerHolder.scenery_detail_buttom_shop3);
//            headerHolder.scenery_detail_buttom_shop1.setImageUrl(scenic.nearby_shop.get(0).image_url, RequestManager.getInstance()
//                    .getImageLoader());
//            headerHolder.scenery_detail_buttom_shop2.setImageUrl(scenic.nearby_shop.get(1).image_url, RequestManager.getInstance()
//                    .getImageLoader());
//            headerHolder.scenery_detail_buttom_shop3.setImageUrl(scenic.nearby_shop.get(2).image_url, RequestManager.getInstance()
//                    .getImageLoader());
            headerHolder.tv_shop_title1.setText(scenic.nearby_shop.get(0).name);
            headerHolder.tv_shop_title12.setText(scenic.nearby_shop.get(1).name);
            headerHolder.tv_shop_title13.setText(scenic.nearby_shop.get(2).name);
            attachClickListener(headerHolder, headerHolder.shop_total, 0);

        } else if (size1 == 2) {
            ImageUtil.DisplayImage(scenic.nearby_shop.get(0).image_url,
                    headerHolder.scenery_detail_buttom_shop1);
            ImageUtil.DisplayImage(scenic.nearby_shop.get(1).image_url,
                    headerHolder.scenery_detail_buttom_shop2);
//            headerHolder.scenery_detail_buttom_shop1.setImageUrl(scenic.nearby_shop.get(0).image_url, RequestManager.getInstance()
//                    .getImageLoader());
//            headerHolder.scenery_detail_buttom_shop2.setImageUrl(scenic.nearby_shop.get(1).image_url, RequestManager.getInstance()
//                    .getImageLoader());
            headerHolder.tv_shop_title1.setText(scenic.nearby_shop.get(0).name);
            headerHolder.tv_shop_title12.setText(scenic.nearby_shop.get(1).name);
            headerHolder.tv_shop_title13.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_shop3.setVisibility(View.GONE);
            attachClickListener(headerHolder, headerHolder.shop_total, 0);
        } else if (size1 == 1) {
//            headerHolder.scenery_detail_buttom_shop1.setImageUrl(
//                    scenic.nearby_shop.get(0).image_url, RequestManager.getInstance()
//                    .getImageLoader());

            ImageUtil.DisplayImage(scenic.nearby_shop.get(0).image_url,
                    headerHolder.scenery_detail_buttom_shop1);
            headerHolder.tv_shop_title1.setText(scenic.nearby_shop.get(0).name);
            headerHolder.tv_shop_title12.setVisibility(View.GONE);
            headerHolder.tv_shop_title13.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_shop2.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_shop3.setVisibility(View.GONE);
            attachClickListener(headerHolder, headerHolder.shop_total, 0);
        } else {
//            ToastUtils.show(mActivity,"信息不完整！");
            headerHolder.tv_shop_title1.setVisibility(View.GONE);
            headerHolder.tv_shop_title12.setVisibility(View.GONE);
            headerHolder.tv_shop_title13.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_shop1.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_shop2.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_shop3.setVisibility(View.GONE);
            headerHolder.detail_arrow_shops.setVisibility(View.GONE);
        }
        if (scenic.nearby_scenic != null) {
            size2 = scenic.nearby_scenic.size();
        }
//
        if (size2 >= 3) {
            ImageUtil.DisplayImage(scenic.nearby_scenic.get(0).image_url,
                    headerHolder.scenery_detail_buttom_pic1);
            ImageUtil.DisplayImage(scenic.nearby_scenic.get(1).image_url,
                    headerHolder.scenery_detail_buttom_pic2);
            ImageUtil.DisplayImage(scenic.nearby_scenic.get(2).image_url,
                    headerHolder.scenery_detail_buttom_pic3);
//            headerHolder.scenery_detail_buttom_pic1.setImageUrl(scenic.nearby_scenic.get(0).image_url, RequestManager.getInstance()
//                    .getImageLoader());
//            headerHolder.scenery_detail_buttom_pic2.setImageUrl(scenic.nearby_scenic.get(1).image_url, RequestManager.getInstance()
//                    .getImageLoader());
//            headerHolder.scenery_detail_buttom_pic3.setImageUrl(scenic.nearby_scenic.get(2).image_url, RequestManager.getInstance()
//                    .getImageLoader());
            headerHolder.tv_pic_title1.setText(scenic.nearby_scenic.get(0).name);
            headerHolder.tv_pic_title12.setText(scenic.nearby_scenic.get(1).name);
            headerHolder.tv_pic_title13.setText(scenic.nearby_scenic.get(2).name);
            attachClickListener(headerHolder, headerHolder.scenci_total, 0);
        } else if (size2 == 2) {
            ImageUtil.DisplayImage(scenic.nearby_scenic.get(0).image_url,
                    headerHolder.scenery_detail_buttom_pic1);
            ImageUtil.DisplayImage(scenic.nearby_scenic.get(1).image_url,
                    headerHolder.scenery_detail_buttom_pic2);
//            headerHolder.scenery_detail_buttom_pic1.setImageUrl(scenic.nearby_scenic.get(0).image_url, RequestManager.getInstance()
//                    .getImageLoader());
//            headerHolder.scenery_detail_buttom_pic2.setImageUrl(scenic.nearby_scenic.get(1).image_url, RequestManager.getInstance()
//                    .getImageLoader());
            headerHolder.tv_pic_title1.setText(scenic.nearby_scenic.get(0).name);
            headerHolder.tv_pic_title12.setText(scenic.nearby_scenic.get(1).name);
            headerHolder.scenery_detail_buttom_pic3.setVisibility(View.GONE);
            headerHolder.tv_pic_title13.setVisibility(View.GONE);
            attachClickListener(headerHolder, headerHolder.scenci_total, 0);
        } else if (size2 == 1) {
            ImageUtil.DisplayImage(scenic.nearby_scenic.get(0).image_url,
                    headerHolder.scenery_detail_buttom_pic1);
//            headerHolder.scenery_detail_buttom_pic1.setImageUrl(scenic.nearby_scenic.get(0).image_url, RequestManager.getInstance()
//                    .getImageLoader());
            headerHolder.tv_pic_title1.setText(scenic.nearby_scenic.get(0).name);
            headerHolder.tv_pic_title12.setVisibility(View.GONE);
            headerHolder.tv_pic_title13.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_pic2.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_pic3.setVisibility(View.GONE);
            attachClickListener(headerHolder, headerHolder.scenci_total, 0);
        } else {
            headerHolder.tv_pic_title1.setVisibility(View.GONE);
            headerHolder.tv_pic_title12.setVisibility(View.GONE);
            headerHolder.tv_pic_title13.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_pic1.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_pic2.setVisibility(View.GONE);
            headerHolder.scenery_detail_buttom_pic3.setVisibility(View.GONE);
            headerHolder.detail_arrow_scenci.setVisibility(View.GONE);
        }


    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext().getApplicationContext();
        View view = View.inflate(context, R.layout.layout_emptys, null);
        NearbyViewHolder holder = new NearbyViewHolder(view);
        return holder;

    }


    @Override
    public void onBinderItemViewHolder(ViewHolder holder, int position) {
//        if (mDataItems.size() <= position) {
//            Log.e(TAG, "[onBinderItemViewHolder] position out of bound");
//            return;
//        }
//
//        final NearbyViewHolder nearbyViewHolder = (NearbyViewHolder) holder;
//        nearbyViewHolder.setDataTag(mDataItems.get(position));
//
//        String image_url = mDataItems.get(position).image_url;
//        if(!TextUtils.isEmpty(image_url)){
//            nearbyViewHolder.first_image.setDefaultImageResId(R.drawable.bg_image_hint);
//            nearbyViewHolder.first_image.setDefaultImageResId(R.drawable.bg_image_hint);
//            nearbyViewHolder.first_image.setImageUrl(image_url,RequestManager.getInstance().getImageLoader());
//        }
//        if(!TextUtils.isEmpty(mDataItems.get(position).name)){
//            nearbyViewHolder.first_name.setText(mHeader.nearby_scenic.get(position).name);
//        }
//
//        nearbyViewHolder.first_image.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onValidClick(View v) {
//                QHScenic mScenic = (QHScenic)v.getTag();
//                Intent intent = new Intent(mActivity, SecnicActivity.class);
////                Bundle bundle = new Bundle();
////                bundle.putExtra("scenic",mScenic);
//                int mId = mScenic.id;
//                intent.putExtra(Const.QH_SECNIC,mScenic);
//                intent.putExtra(Const.QH_SECNIC_ID,mId);
//                mActivity.startActivity(intent);
//            }
//        });
//
//        if (comment == null || comment.author == null) {
//            Log.e(TAG, "[onBinderItemViewHolder] comment: " + comment);
//            return;
//        }
//        final CommentViewHolder commentHolder = (CommentViewHolder) holder;
//        commentHolder.setDataTag(comment);
//        if (!TextUtils.isEmpty(comment.author.avatarUrl)) {
//            RequestManager
//                    .getInstance()
//                    .getImageLoader()
//                    .get(comment.author.avatarUrl,
//                            ImageLoader.getImageListener(commentHolder.authorAvatar,
//                                    R.drawable.bg_avata_hint,
//                                    R.drawable.bg_avata_hint));
//        }
//        commentHolder.author.setText(comment.author.name);
//        commentHolder.content.setText(comment.content);
//        commentHolder.commentCount.setText(String.valueOf(comment.commentCount));
//        commentHolder.voteCount.setText(String.valueOf(comment.voteCount));
//        if (comment.isVoted) {
//            commentHolder.voteCount.getCompoundDrawables()[0].setLevel(2);
//            commentHolder.voteCount.setEnabled(false);
//        } else {
//            commentHolder.voteCount.setEnabled(true);
//            commentHolder.voteCount.getCompoundDrawables()[0].setLevel(1);
//            attachClickListener(commentHolder, commentHolder.voteCount, position);
//        }
//        commentHolder.rating.setRating(comment.rating);
//
//        bindCommentImageRecyclerView(commentHolder.imagesRecyclerView, comment);
//        commentHolder.itemView.requestLayout();
//        attachClickListener(commentHolder, commentHolder.commentCount, position);
//        attachClickListener(commentHolder, commentHolder.itemView, position);
//        attachClickListener(commentHolder, commentHolder.share, position);
    }

    public static final int COLOR_WHITE = 0xFFFFFFFF;
    public static final int COLOR_CHANGE = 0xFF0FD5F2;

}
