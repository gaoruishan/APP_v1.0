package com.cmcc.hyapps.andyou.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.util.ScreenUtils;

/**
 * 
 * 页脚点类
 */
public class ScrollPoints extends LinearLayout {
	private List<ImageView> mPoints = new ArrayList<ImageView>();
	private LinearLayout mPointBox;
    private Context mcontext;
	public ScrollPoints(Context context) {
		super(context);
        mcontext = context;
	}

	public ScrollPoints(Context context, AttributeSet attrs) {
		super(context, attrs);
        mcontext = context;
	}

	/**
	 * 
	 * 创建页脚点
	 * 
	 * @param context 关系
	 * @param count 当前控件里元素的总数
	 * @param selected 当前控件里哪个元素被选择
	 */
	public void initPoints(Context context, int count, int selected) {
		if(getChildCount() != 0 ){
			removeAllViews();
		}
		mPoints.clear();
		mPointBox = new LinearLayout(context);
		for (int i = 0; i < count; i++) {
			ImageView slidePot = new ImageView(context);
            int padding_l = ScreenUtils.dpToPxInt(mcontext,5.0f);
			slidePot.setPadding(padding_l,padding_l, padding_l, padding_l);
			if (i == selected) {
				slidePot.setImageResource(R.drawable.guide_indicate_highlight);
			} else {
				slidePot.setImageResource(R.drawable.guide_indicate);
			}
			mPoints.add(slidePot);
			mPointBox.addView(slidePot);
		}
		addView(mPointBox);
	}

	public void addPoint(Context context, int count) {
		if (count > mPoints.size()) {
			ImageView slidePot = new ImageView(context);
			slidePot.setPadding(5, 5, 5, 5);
			mPoints.add(slidePot);
			mPointBox.addView(slidePot);
		}
	}

	/**
	 * 
	 * 被标记控件被拖拽后，调用该接口对页脚点进行更新
	 * 
	 * @author bikehua@infohold.com.cn
	 * 
	 */
	public interface SelectPoint {
		/**
		 * 
		 * 更新页脚点调用此方法
		 * 
		 * @param position 当前控件被选择的索引位置
		 */
		public void changeSelectedPoint(int position);
	}

	/**
	 * 
	 * 更新页脚点调用此方法
	 * 
	 * @param position 当前控件被选择的索引位置
	 */
	public void changeSelectedPoint(int position) {
		for (int i = 0; i < mPoints.size(); i++) {
			ImageView point = mPoints.get(i);
			if (i == position) {
				point.setImageResource(R.drawable.guide_indicate_highlight);
			} else {
				point.setImageResource(R.drawable.guide_indicate);
			}
		}
	}

}
