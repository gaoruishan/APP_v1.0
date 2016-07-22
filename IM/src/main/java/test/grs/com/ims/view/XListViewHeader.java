/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package test.grs.com.ims.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import test.grs.com.ims.R;


public class XListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private int mState = STATE_NORMAL;
	
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;
	
	public XListViewHeader(Context context) {
		super(context);
		initView(context);
	}
	
	/**
	 * @param context
	 * @param attrs
	 */
	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xlistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

	}

	public void setState(int state) {
		if(state == mState)
			return;

		mState = state;
	}

	public void setVisiableHeight(int height) {
		if(height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams)mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}
	
	public int getVisiableHeight() {
		return mContainer.getHeight();
	}
	
}
