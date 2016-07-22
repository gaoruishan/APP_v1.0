/**
 *
 */

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
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHMarketShop;
import com.cmcc.hyapps.andyou.model.QHNavigation;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuloud
 */
public class RestaurantDetailActivity extends BaseActivity implements View.OnClickListener,ViewPager.OnPageChangeListener  {
    private TextView mRestName, mRestTel, mRestPrice, mRestLocation, mRestFavorbale, mRestArgumentNumber, mRestDescription;
    private ViewPager mViewPager;
    private List<View> viewList = new ArrayList<View>();
    private QHMarketShop mRestaurant;
    private View locationLyout,argumentLayout;
    private List<String> imageUrls = new ArrayList<String>();
    private ImageView saleImageView;
    private Request<QHMarketShop> marketShopLoader;
    private ImageView pictureImageView;
    @Override
    public void onClick(View view) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (view.getId()){
            case R.id.action_bar_left:
                finish();
                break;
            case R.id.hotel_details_tel_number:
                callTelephone();
                break;
            case R.id.hotel_details_location_layout:
               gotoNavigation();
                break;
            case R.id.hotel_details_argument_layout:
                Intent argumentIntent = new Intent(this,GuiderMarketCommentActivity.class);
                argumentIntent.putExtra(Const.QH_SECNIC,mRestaurant);
                argumentIntent.putExtra(Const.QH_SECNIC_ID,mRestaurant.id);
                startActivity(argumentIntent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getQhMarketShopDetail();
    }

    private void getQhMarketShopDetail(){
        String url = ServerAPI.MarketShopList.URL + mRestaurant.id+"/";
        marketShopLoader = RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url, QHMarketShop.class, null, new Response.Listener<QHMarketShop>() {
            @Override
            public void onResponse(QHMarketShop response) {
                mRestArgumentNumber.setText("评论(" + String.valueOf(response.comment_count) + ")");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, true, requestTag);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details_layout);
        mRestaurant = getIntent().getParcelableExtra(Const.REST_DETAIL);
    //    getmLocation();
        initView();
        initActionBar();
    //    initViewPager();
        initData();
    }

    private void initView(){
        pictureImageView = (ImageView)this.findViewById(R.id.hotel_details_picture);
        mRestName = (TextView)this.findViewById(R.id.hotel_details_name);
        mRestTel = (TextView)this.findViewById(R.id.hotel_details_tel_number);
        mRestTel.setOnClickListener(this);
        saleImageView = (ImageView)this.findViewById(R.id.hotel_details_sale_image);
        mRestPrice = (TextView)this.findViewById(R.id.hotel_details_price);
        mRestLocation = (TextView)this.findViewById(R.id.hotel_details_location);
        mRestFavorbale = (TextView)this.findViewById(R.id.hotel_details_favorable);
        mRestArgumentNumber = (TextView)this.findViewById(R.id.hotel_details_argument);
        mRestDescription = (TextView) this.findViewById(R.id.hotel_details_merchant_description);

        locationLyout = this.findViewById(R.id.hotel_details_location_layout);
        locationLyout.setOnClickListener(this);

        argumentLayout = this.findViewById(R.id.hotel_details_argument_layout);
        argumentLayout.setOnClickListener(this);
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(mRestaurant.name);
        actionBar.getTitleView().setTextColor(ConstTools.SECIAL_HEAD_TITLE_COLOR);
        actionBar.setBackgroundColor(ConstTools.SECIAL_HEAD_BG_COLOR);
        actionBar.getLeftView() .setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    private void initViewPager(){
        if (!TextUtils.isEmpty(mRestaurant.image_url))
            imageUrls.add(mRestaurant.image_url);
        else
            imageUrls.add("@");
        mViewPager = (ViewPager)this.findViewById(R.id.hotel_details_guide_viewpager);
        for(int i = 0;i<imageUrls.size();i++){
            View view = LayoutInflater.from(this).inflate(R.layout.hotel_detail_header_viewpage_item, null);
            viewList.add(view);
        }
        mViewPager.setAdapter( new ViewPagerAdapter(viewList));
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
            NetworkImageView netImg =(NetworkImageView)data.get(position).findViewById(R.id.iv_cover_image);
            String url = imageUrls.get(position);
            if (!TextUtils.isEmpty(url)) {
//                netImg.setDefaultImageResId(R.drawable.recommand_bg);
//                netImg.setErrorImageResId(R.drawable.recommand_bg);
//                netImg.setImageUrl(url, RequestManager.getInstance().getImageLoader());

                ImageUtil.DisplayImage(url, netImg);
               // RequestManager.getInstance().getImageLoader().get(url, ImageLoader.getImageListener(netImg, R.drawable.bg_image_hint, R.drawable.bg_image_hint));
            }
            return data.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(data.get(position));
        }
    }
    private void initData(){
        if (mRestaurant == null)
            return;
        String url = mRestaurant.image_url;
        if (!TextUtils.isEmpty(url)) {
            ImageUtil.DisplayImage(url, pictureImageView);
        }
      //  if (mRestaurant.average > 0) {
            String format = getString(R.string.home_restaurant_average_cost);
            mRestPrice.setText(String.format(format, String.valueOf(mRestaurant.average)));
       // } else {
       //     mRestPrice.setText("暂无");
    //    }
        mRestTel.setText(String.valueOf(mRestaurant.telephone));
        if (!TextUtils.isEmpty(mRestaurant.address))
            mRestLocation.setText( mRestaurant.address.trim());
        if (!TextUtils.isEmpty(mRestaurant.name))
            mRestName.setText(mRestaurant.name);

        mRestDescription.setText(mRestaurant.introduction.trim());

        if (mRestaurant.comment_count > 0)
            mRestArgumentNumber.setText("评论("+ String.valueOf(mRestaurant.comment_count) +")");
        else
            mRestArgumentNumber.setText("评论(0)");

        if (!TextUtils.isEmpty(mRestaurant.promotion)){
            mRestFavorbale.setText(mRestaurant.promotion.trim());
            saleImageView.setVisibility(View.VISIBLE);
        }
        else
            mRestFavorbale.setText("无");
    }

    /**
     * call telephone
     */
    private void callTelephone() {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mRestaurant.telephone));
                startActivity(intent);
            } catch (Exception e) {
                ToastUtils.AvoidRepeatToastShow(this, R.string.market_no_permission, Toast.LENGTH_LONG);
            }
    }

    private QHNavigation getmLocation(){
        return new QHNavigation(mRestaurant.longitude, mRestaurant.latitude);
    }

    private void gotoNavigation(){
        Intent navigationInten  = new Intent(this,NavigationDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("navi_detail",getmLocation());
        navigationInten.putExtra("navi_bundle", bundle);
        startActivity(navigationInten);
    }
    private boolean  isHasCallPermission(){
        PackageManager pm = getPackageManager();
        return  (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.CALL_PHONE", getPackageName()));
    }
}
