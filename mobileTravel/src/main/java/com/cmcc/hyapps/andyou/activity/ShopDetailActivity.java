package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.GsonRequest;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHMarketShop;
import com.cmcc.hyapps.andyou.model.QHNavigation;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.umeng.socialize.view.LoginAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/23 0023.
 */
public class ShopDetailActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private TextView hotelName, hotelTel, mAverage,hotelPrice, hotelLocation, hotelFavorbale, hotelArgumentNumber, hotelDescription;
    private ViewPager mViewPager;
    private List<View> viewList = new ArrayList<View>();
    private QHMarketShop hotelDeatil;
    private View locationLayout, argumentLayout;
    private ImageView saleImageView;
    private List<String> imageUrls = new ArrayList<String>();
    private GsonRequest<QHMarketShop> marketShopLoader;
    private String shopID;
    private long mSearchTiem;
    private ImageView pictureImageView;
    @Override
    public void onClick(View view) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.action_bar_left:
                finish();
                break;
            case R.id.hotel_details_tel_number:
                if (hotelDeatil!= null){
                    callTelephone();
                }
                break;
            case R.id.hotel_details_location_layout:
                if (hotelDeatil!= null){
                    gotoNavigation();
                }
                break;
            case R.id.hotel_details_argument_layout:
                if (hotelDeatil!= null){
                    Intent argumentIntent = new Intent(this, GuiderMarketCommentActivity.class);
                    argumentIntent.putExtra(Const.QH_SECNIC, hotelDeatil);
                    argumentIntent.putExtra(Const.QH_SECNIC_ID, hotelDeatil.id);
                    startActivity(argumentIntent);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details_layout);
        //   hotelDeatil = getIntent().getParcelableExtra(Const.REST_DETAIL);
        shopID = getIntent().getStringExtra("shopID");
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getQhMarketShopDetail();
    }

    private void getQhMarketShopDetail() {
        String url = ServerAPI.MarketShopList.URL + shopID+"/";
        marketShopLoader = RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url, QHMarketShop.class, null, new Response.Listener<QHMarketShop>() {
            @Override
            public void onResponse(QHMarketShop response) {
                if ((System.currentTimeMillis() - mSearchTiem) < 300) {
                    return; // Too fast
                }
                mSearchTiem = System.currentTimeMillis();
                if (response == null){
                    ToastUtils.show(ShopDetailActivity.this,R.string.delete_information);
                    return;
                }
                if (response.stype != 4){
                    hotelPrice.setVisibility(View.VISIBLE);
                    mAverage.setVisibility(View.VISIBLE);
                }
                hotelDeatil = response;
                initActionBar();
             //   initViewPager();
                initData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                if (marketShopLoader != null && marketShopLoader.getStateCode() == 0){
//                    ToastUtils.show(ShopDetailActivity.this,R.string.delete_information);
//                }
            }
        }, false, requestTag);
    }

    private void initView() {
        pictureImageView = (ImageView)this.findViewById(R.id.hotel_details_picture);
        hotelName = (TextView) this.findViewById(R.id.hotel_details_name);
        hotelTel = (TextView) this.findViewById(R.id.hotel_details_tel_number);
        hotelTel.setOnClickListener(this);
        saleImageView = (ImageView) this.findViewById(R.id.hotel_details_sale_image);
        hotelPrice = (TextView) this.findViewById(R.id.hotel_details_price);
        hotelPrice.setVisibility(View.INVISIBLE);
        mAverage = (TextView) this.findViewById(R.id.hotel_details_average);
        mAverage.setVisibility(View.INVISIBLE);
        hotelLocation = (TextView) this.findViewById(R.id.hotel_details_location);
        hotelFavorbale = (TextView) this.findViewById(R.id.hotel_details_favorable);
        hotelArgumentNumber = (TextView) this.findViewById(R.id.hotel_details_argument);
        hotelDescription = (TextView) this.findViewById(R.id.hotel_details_merchant_description);
        locationLayout = this.findViewById(R.id.hotel_details_location_layout);
        locationLayout.setOnClickListener(this);

        argumentLayout = this.findViewById(R.id.hotel_details_argument_layout);
        argumentLayout.setOnClickListener(this);
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        if (!TextUtils.isEmpty(hotelDeatil.name))
            actionBar.setTitle(hotelDeatil.name);
        else
            actionBar.setTitle("酒店");
        actionBar.getTitleView().setTextColor(ConstTools.SECIAL_HEAD_TITLE_COLOR);
        actionBar.setBackgroundColor(ConstTools.SECIAL_HEAD_BG_COLOR);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    private void initViewPager() {
        if (!TextUtils.isEmpty(hotelDeatil.image_url))
            imageUrls.add(hotelDeatil.image_url);
        else
            imageUrls.add("@");
        mViewPager = (ViewPager) this.findViewById(R.id.hotel_details_guide_viewpager);
        for (int i = 0; i < imageUrls.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.hotel_detail_header_viewpage_item, null);
            viewList.add(view);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(viewList));
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private class ViewPagerAdapter extends PagerAdapter {
        private List<View> data;

        public ViewPagerAdapter(List<View> data) {
            super();
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(data.get(position));
            //   View myView =  viewList.get(position);
            NetworkImageView netImg = (NetworkImageView) data.get(position).findViewById(R.id.iv_cover_image);
            String url = imageUrls.get(position);
            if (!TextUtils.isEmpty(url)) {
//                netImg.setDefaultImageResId(R.drawable.recommand_bg);
//                netImg.setErrorImageResId(R.drawable.recommand_bg);
//                netImg.setImageUrl(url, RequestManager.getInstance().getImageLoader());
                ImageUtil.DisplayImage(url, netImg);
              //  RequestManager.getInstance().getImageLoader().get(url, ImageLoader.getImageListener(netImg, R.drawable.recommand_bg, R.drawable.recommand_bg));
            }
            return data.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(data.get(position));
        }
    }

    private void initData() {
        if (hotelDeatil == null)
            return;
        String url = hotelDeatil.image_url;
        if (!TextUtils.isEmpty(url)) {
            ImageUtil.DisplayImage(url, pictureImageView);
        }
            hotelPrice.setText("￥" + String.valueOf(hotelDeatil.average));
        hotelTel.setText(String.valueOf(hotelDeatil.telephone));
        if (!TextUtils.isEmpty(hotelDeatil.address))
            hotelLocation.setText(hotelDeatil.address.trim());
        if (!TextUtils.isEmpty(hotelDeatil.name))
            hotelName.setText(hotelDeatil.name);
        if (!TextUtils.isEmpty(hotelDeatil.introduction))
            hotelDescription.setText(hotelDeatil.introduction.trim());

        if (hotelDeatil.comment_count > 0)
            hotelArgumentNumber.setText("评论(" + String.valueOf(hotelDeatil.comment_count) + ")");
        else
            hotelArgumentNumber.setText("评论(0)");

        if (!TextUtils.isEmpty(hotelDeatil.promotion)) {
            hotelFavorbale.setText(hotelDeatil.promotion.trim());
            saleImageView.setVisibility(View.VISIBLE);
        } else
            hotelFavorbale.setText("无");


    }

    /**
     * call telephone
     */
    private void callTelephone() {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + hotelDeatil.telephone));
                startActivity(intent);
            } catch (Exception e) {
                ToastUtils.AvoidRepeatToastShow(this, R.string.market_no_permission, Toast.LENGTH_LONG);
            }
    }

    private QHNavigation getmLocation() {
        return new QHNavigation(hotelDeatil.longitude, hotelDeatil.latitude);
    }

    private void gotoNavigation() {
        Intent navigationInten = new Intent(this, NavigationDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("navi_detail", getmLocation());
        navigationInten.putExtra("navi_bundle", bundle);
        startActivity(navigationInten);
    }

    private boolean isHasCallPermission() {
        PackageManager pm = getPackageManager();
        return (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.CALL_PHONE", getPackageName()));
    }
}
