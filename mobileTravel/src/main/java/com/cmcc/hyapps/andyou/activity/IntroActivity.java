/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.support.OnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Guide page for new version/features.
 * 
 * @author kuloud
 */
public class IntroActivity extends BaseActivity {

    private ViewPager mPager;
//    private LinearLayout mDotsLayout;
    private View mBtn;

    private List<View> viewList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mPager = (ViewPager) findViewById(R.id.guide_viewpager);
//        mDotsLayout = (LinearLayout) findViewById(R.id.guide_dots);
        mBtn = findViewById(R.id.guide_btn);

        initPager();
        mPager.setAdapter(new ViewPagerAdapter(viewList));
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
//                for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
//                    if (i == index) {
//                        mDotsLayout.getChildAt(i).setSelected(true);
//                    } else {
//                        mDotsLayout.getChildAt(i).setSelected(false);
//                    }
//                }
                if (index == viewList.size() - 1) {
                    mBtn.setVisibility(View.VISIBLE);
                } else {
                    mBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        mBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    private void initPager() {
        viewList = new ArrayList<View>();
        int[] images = new int[] {
                R.drawable.guide1, R.drawable.guide2, R.drawable.guide3,R.drawable.guide4
        };
//        int[] texts0 = new int[] {
//                R.string.guide_text0_0, R.string.guide_text0_1, R.string.guide_text0_2
//        };
//        int[] texts1 = new int[] {
//                R.string.guide_text1_0, R.string.guide_text1_1, R.string.guide_text1_2
//        };
        for (int i = 0; i < images.length; i++) {
            viewList.add(initView(images[i]/*, texts0[i], texts1[i]*/));
        }
//        initDots(images.length);
    }

//    private void initDots(int count) {
//        for (int j = 0; j < count; j++) {
//            mDotsLayout.addView(initDot());
//        }
//        mDotsLayout.getChildAt(0).setSelected(true);
//    }

    @SuppressLint("InflateParams")
    private View initDot() {
        return LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_dot, null);
    }

    @SuppressLint("InflateParams")
    private View initView(int res/*, int text0, int text1*/) {
//        ImageView image = new ImageView(activity);
//        image.setScaleType(ScaleType.FIT_XY);
//        image.setImageResource(res);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_guide, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iguide_img);
//        TextView textview0 = (TextView) view.findViewById(R.id.iguide_text0);
//        TextView textview1 = (TextView) view.findViewById(R.id.iguide_text1);
        imageView.setImageResource(res);
//        textview0.setText(text0);
//        textview1.setText(text1);
        return view;
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
            return data.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(data.get(position));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Exit application if back pressed.
        System.exit(0);
    }
}
