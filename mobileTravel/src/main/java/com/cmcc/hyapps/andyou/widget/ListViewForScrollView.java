package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;
import android.widget.ScrollView;

public class ListViewForScrollView extends ListView implements OnTouchListener {

	public ListViewForScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ListViewForScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListViewForScrollView(Context context) {
		super(context);
	}

	/**
	 * 重写该方法，达到使ListView适应ScrollView的效果 让其失去滑动特性
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, mExpandSpec);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 赋值给mLastY //alllinear是scrollview里包括的那个linearlayout布局
		/*
		 * int index; switch (event.getAction()) { case MotionEvent.ACTION_DOWN:
		 * break; case MotionEvent.ACTION_MOVE: index++; break; default: break;
		 * } if (event.getAction() == MotionEvent.ACTION_UP && index > 0) {
		 * index = 0; mLastY = scrollView.getScrollY(); if (mLastY ==
		 * (alllinear.getHeight() - scrollView.getHeight())) { // TODO
		 * //滑动到底部，你要做的事 } }
		 */
		return false;
	}

	/*
	 * @Override public boolean dispatchTouchEvent(MotionEvent ev) { if
	 * (ev.getAction() == MotionEvent.ACTION_MOVE) { return true; // 禁止滑动 }
	 * return super.dispatchTouchEvent(ev);
	 * 
	 * }
	 */
}
