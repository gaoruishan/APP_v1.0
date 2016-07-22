/**
 * 
 */

package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.cmcc.hyapps.andyou.util.Log;

/**
 * Tab menus at bottom in index.
 * 
 * @author Kuloud
 */
public class BottomTab extends LinearLayout {
    private int mCurrentTabIndex;
    private OnTabSelected mOnTabSelected;
    private int mBeforeTabIndex;

    public int getBeforeTabIndex() {
        return mBeforeTabIndex;
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BottomTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param context
     * @param attrs
     */
    public BottomTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     */
    public BottomTab(Context context) {
        super(context);
    }

    //显示效果的实现方法
    public void selectTab(int index) {
        mBeforeTabIndex = mCurrentTabIndex;
        if (index == mCurrentTabIndex) {
            return;
        } else {
            getChildAt(mCurrentTabIndex).setSelected(false);
            mCurrentTabIndex = index;
            getChildAt(mCurrentTabIndex).setSelected(true);
        }
    }


    @Override
    protected void onFinishInflate() {
        //当xml布局文件完全填充后执行，添加点击事件
        super.onFinishInflate();
        Log.e("KKKK");
        for (int i = 0; i < getChildCount(); i++) {
            final int index = i;
            getChildAt(index).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //修改相应位置的显示效果
                    selectTab(index);
                    if (mOnTabSelected != null) {
                        mOnTabSelected.onTabSeledted(index);
                    }
                }
            });

        }
        // TODO If the last position need record, should be selected here.
        getChildAt(0).setSelected(true);
    }

    //接口，可选择的对象，回调
    public interface OnTabSelected {
        public void onTabSeledted(int index);
    }

    /**
     * 得到当前自定义控件对象选择的索引
     * @return the mCurrentTabIndex
     */
    public int getCurrentTabIndex() {
        return mCurrentTabIndex;
    }

    /**
     * 设置当前自定义控件对象选择的索引
     * @param currentTabIndex the mCurrentTabIndex to set
     */
    public void setCurrentTabIndex(int currentTabIndex) {
        this.mCurrentTabIndex = currentTabIndex;
    }

    /**
     * 得到当前自定义控件中的item对象
     * @return the mOnTabSelected
     */
    public OnTabSelected getOnTabSelected() {
        return mOnTabSelected;
    }

    /**
     * 设置当前自定义控件中的item对象
     * @param OnTabSelected the mOnTabSelected to set
     */
    public void setOnTabSelected(OnTabSelected OnTabSelected) {
        this.mOnTabSelected = OnTabSelected;
        this.mOnTabSelected.onTabSeledted(mCurrentTabIndex);
    }
}
