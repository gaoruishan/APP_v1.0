
package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.circularprogressbar.CircularProgressBar;
import com.tenthbit.view.ZoomImageView;

import java.util.ArrayList;

/**
 * @author kuloud
 */

public class PhotoPreviewActivity extends BaseActivity implements ZoomImageView.OnPhotoTapListener {
    public static final String TAG_ID = "photo_id";

    private ArrayList<View> mPagerViews = null;
    private ViewPager mViewPager;
    private ActionBar mActionBar;
    private PhotoPageAdapter mPhotoAdapter;

    private ArrayList<Image> mImageData;
    private TextView mPageIndicator;
    private TextView mInfoLocation;
    private TextView mInfoTime;
    private boolean createFinished = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageData = getIntent().getParcelableArrayListExtra(Const.EXTRA_IMAGE_DATA);
        if (mImageData == null || mImageData.isEmpty()) {
            finish();
            return;
        }

        setContentView(R.layout.activity_photo_preview);
    //    initActionBar();

        mInfoLocation = ((TextView) findViewById(R.id.image_info_location));
        mInfoTime = ((TextView) findViewById(R.id.image_info_time));
        mPageIndicator = (TextView) findViewById(R.id.pager_indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOnPageChangeListener(pageChangeListener);
        populateImageViews();

        mPhotoAdapter = new PhotoPageAdapter(mPagerViews);
        mViewPager.setAdapter(mPhotoAdapter);

        Intent intent = getIntent();
        int startIndex = intent.getIntExtra(Const.EXTRA_IMAGE_PREVIEW_START_INDEX, 0);
        if (startIndex >= mImageData.size() || startIndex < 0) {
            Log.d("Invalid startIndex %d against image data list size %d", startIndex,
                    mImageData.size());
            startIndex = 0;
        }
        mViewPager.setCurrentItem(startIndex);

        loadImageAtIndex(startIndex);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createFinished = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        createFinished = false;
    }

    private void initActionBar(View view) {
        mActionBar = (ActionBar) view.findViewById(R.id.action_bar);
        mActionBar.setBackgroundResource(R.drawable.fg_top_shadow);
        mActionBar.getLeftView()
                .setImageResource(R.drawable.ic_action_bar_back_selecter);
        mActionBar.getLeftView().setOnClickListener(mOnClickListener);
    }

    private void populateImageViews() {
        if (mPagerViews == null) {
            mPagerViews = new ArrayList<View>();
        }

        for (int i = 0; i < mImageData.size(); i++) {
            View itemView = LayoutInflater.from(activity).inflate(R.layout.item_photo_preview,
                    mViewPager, false);
            mPagerViews.add(itemView);
        }
    }

    private void loadImageAtIndex(int index) {
        View itemView = mPagerViews.get(index);
        Image image = mImageData.get(index);
        ZoomImageView imageView = (ZoomImageView) itemView.findViewById(R.id.image);
        imageView.setOnPhotoTapListener(PhotoPreviewActivity.this);
        if (TextUtils.isEmpty(image.infoLocation)) {
            mInfoLocation.setVisibility(View.GONE);
        } else {
            mInfoLocation.setVisibility(View.VISIBLE);
            mInfoLocation.setText(image.infoLocation);
        }

        if (TextUtils.isEmpty(image.infoTime)) {
            mInfoTime.setVisibility(View.GONE);
        } else {
            mInfoTime.setVisibility(View.VISIBLE);
            mInfoTime.setText(image.infoTime);
        }

        CircularProgressBar progressBar = (CircularProgressBar) itemView
                .findViewById(R.id.loading_progress);
        if (image.bitmap != null) {
            progressBar.setVisibility(View.GONE);
            imageView.setImageBitmap(image.bitmap);
        } else if (image.imagePath != null) {
            // if ("file".equals(image.imagePath.getScheme())) {
            // progressBar.setVisibility(View.GONE);
            // ImageLoaderManager.getInstance().getLoader()
            // .displayImage(image.imagePath.toString(), imageView);
            // } else {
            Log.d("Kuloud", "setErrorImageResId:" + System.currentTimeMillis());
          //  imageView.setLoadingView(progressBar);
//            imageView.setErrorImageResId(R.drawable.bg_image_error).setLoadingView(progressBar);
//            imageView.setImageUrl(image.imagePath.toString(), RequestManager.getInstance()
//                    .getImageLoader());
            progressBar.setVisibility(View.GONE);
            ImageUtil.DisplayImage(image.imagePath.toString(), imageView, R.drawable.recommand_bg, R.drawable.bg_image_error);
            // }
        }

        mPageIndicator.setText(getString(R.string.page_index, index + 1, mImageData.size()));
    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            loadImageAtIndex(arg0);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public void onPhotoTap(View view, float x, float y) {
        if(createFinished) {
            showPopupWindow();
        }
    }

    class PhotoPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;

        private int size;

        public PhotoPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(listViews.get(arg1 % size));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onValidClick(View v) {
            switch (v.getId()) {
                case R.id.action_bar_left:
                    finish();
                    break;

                default:
                    break;
            }
        }
    };

    private void showPopupWindow(){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_action_bar,null);
        initActionBar(view);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, 102,true);
        popupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        popupWindow.setBackgroundDrawable(dw);

        popupWindow.showAtLocation(findViewById(R.id.photo_layout), Gravity.TOP, 0, 50);
    }
}
