/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.HomeHotel;
import com.cmcc.hyapps.andyou.model.HomeHotelDetail;
import com.cmcc.hyapps.andyou.model.HomeRestuarantDetail;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuloud
 */
public class HotelDetailActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private GridView food_type_grid;
    private TextView img_num,rest_name,rest_average_cost,rest_average_cost_tip,rest_tel,rest_address/*,rest_special_foods*/;
    private RatingBar star;
    public  ArrayList<String> food_types = new ArrayList();
    protected String mRequestTag = HotelDetailActivity.class.getName();
    private LayoutInflater mInflater;
    private LinearLayout lineMyroom;
    private HomeHotel hotelDeatil;
    private ViewPager viewpager;
    private List<View> viewList = new ArrayList<View>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mInflater = LayoutInflater.from(this);
        setContentView(R.layout.activity_hotel_detail);
        hotelDeatil = (HomeHotel)getIntent().getParcelableExtra(Const.REST_DETAIL);
        initView();
        initData();
        initViewPagers();
        initActionBar();
        resetBedInfo(null);
    }
    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.home_nearby_hotel_detail);
        actionBar.getTitleView().setTextColor(ConstTools.SECIAL_HEAD_TITLE_COLOR);
        actionBar.setBackgroundColor(ConstTools.SECIAL_HEAD_BG_COLOR);
        actionBar.getLeftView() .setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left: {
               finish();
                break;
            }
            case R.id.rest_phone: {
                if(ConstTools.checkStrEmpty(hotelDeatil.telephone)){
                    ToastUtils.show(this,"电话号格式不正确");
                }else{
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" +hotelDeatil.telephone ));
                    activity.startActivity(intent);
                }
                break;
            }
        }
    }
    private void initView(){
        food_type_grid = (GridView)findViewById(R.id.rest_comment_type);
        star = (RatingBar)findViewById(R.id.testaurant_rating);
        img_num = (TextView)findViewById(R.id.rest_imgs);
        rest_name = (TextView)findViewById(R.id.rest_name);
        rest_average_cost = (TextView)findViewById(R.id.average_cost);
        rest_average_cost_tip = (TextView)findViewById(R.id.average_cost_tip);
        rest_tel = (TextView)findViewById(R.id.rest_tel);
        rest_address = (TextView)findViewById(R.id.rest_address);
        lineMyroom = (LinearLayout)findViewById(R.id.myroom_line);
        findViewById(R.id.rest_phone).setOnClickListener(this);
    }
    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener(){
         @Override
         public void onPageSelected(int index) {
         }
         @Override
         public void onPageScrolled(int arg0, float arg1, int arg2) {
         }
         @Override
         public void onPageScrollStateChanged(int arg0) {
         }
    };
    private void initData(){
        if(ConstTools.isNumeric(hotelDeatil.mark)){
            star.setRating(Float.parseFloat(hotelDeatil.mark));
        }
        else {
            star.setRating(0.0f);
        }
        if(ConstTools.isNumeric(hotelDeatil.price)){
            rest_average_cost.setText("￥"+hotelDeatil.price);
            if("0".equals(hotelDeatil.price)){
                rest_average_cost.setText("暂无");
                rest_average_cost.setTextColor(0xffb4b4b4);
                rest_average_cost.setTextSize(12);
            }
        }
        else
        {
            rest_average_cost.setText("暂无");
            rest_average_cost.setTextColor(0xffb4b4b4);
            rest_average_cost.setTextSize(12);
        }
        rest_tel.setText("酒店电话："+ hotelDeatil.telephone);
        rest_address.setText(getString( R.string.rest_address, hotelDeatil.address));
        img_num.setText(""+hotelDeatil.imageUrls.length);
        rest_name.setText(hotelDeatil.name);
    }
    private void loadHotelDetail() {
        final String url;
        url = ServerAPI.Home.buildUrl("beijing"/*mLocation.city*/);
        RequestManager.getInstance().sendGsonRequest(Request.Method.GET, url,
                HomeRestuarantDetail.class, null,
                new Response.Listener<HomeRestuarantDetail>() {
                    @Override
                    public void onResponse(HomeRestuarantDetail response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error loading scenic details from %s", url);
                    }
                }, true, mRequestTag);
    }
    private void resetBedInfo(ArrayList<HomeHotelDetail> data){
        lineMyroom.removeAllViews();
        for (int i = 0; i < 1; i++) {
            View subView = mInflater.inflate(R.layout.item_hotel_detail, null);
            TextView bed_name = (TextView) subView.findViewById(R.id.bed_name);
            bed_name.setVisibility(View.GONE);
            TextView bed_price = (TextView) subView.findViewById(R.id.room_prise);
            bed_price.setVisibility(View.GONE);
            TextView room_info = (TextView) subView.findViewById(R.id.room_info);
            room_info.setText(hotelDeatil.services);
            lineMyroom.addView(subView);
        }
    }
    public  void initViewPagers(){
        viewpager = (ViewPager) findViewById(R.id.guide_viewpager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(viewList);
        for(int i = 0;i<hotelDeatil.imageUrls.length;i++){
            View view = mInflater.inflate(R.layout.hotel_detail_header_viewpage_item, null);
            viewList.add(view);
        }
        viewpager.setAdapter(pagerAdapter);
        viewpager.setOnPageChangeListener(listener);
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
            View myView =  viewList.get(position);
            NetworkImageView netImg =(NetworkImageView)myView.findViewById(R.id.iv_cover_image);
            String url = hotelDeatil.imageUrls[position];
            if (url != null) {
                ImageUtil.DisplayImage(url, netImg, R.drawable.bg_image_hint,R.drawable.bg_image_hint);
//                RequestManager.getInstance().getImageLoader().get(url, ImageLoader.getImageListener(netImg, R.drawable.bg_image_hint,R.drawable.bg_image_hint));
            }
            return data.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(data.get(position));
        }
    }
}
