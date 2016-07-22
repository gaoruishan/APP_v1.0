
package com.cmcc.hyapps.andyou.widget.pinyinsidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.util.AttributeSet;
import android.view.View;

/**
 * {@link android.support.v7.widget.RecyclerView.ItemDecoration} that draws
 * vertical and horizontal dividers between the items of the target
 * {@link android.support.v7.widget.RecyclerView}.
 * 
 * @author kuloud
 */
public class SearchScenicDividerItemDecoration extends ItemDecoration {

    private int mVerticalGap;

    private Drawable mDivider;

    public SearchScenicDividerItemDecoration(int color, int verticalGap) {
        mDivider = new ColorDrawable(color);
        mVerticalGap = verticalGap;
    }

    public SearchScenicDividerItemDecoration(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, new int[] {
                android.R.attr.listDivider
        });
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent) {
        if (mDivider == null) {
            super.onDrawOver(c, parent);
            return;
        }

        if (mVerticalGap > 0) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();
            final int childCount = parent.getChildCount();

            for (int i = 1; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                if (i < childCount - 1) {
                    View nextChild = parent.getChildAt(i + 1);
                    if ((child.getTag() != null) && (Boolean) child.getTag() && child.getTag().equals(nextChild.getTag())) {
                        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                                .getLayoutParams();
                        final int size = mVerticalGap;
                        final int top = child.getTop() - params.topMargin;
                        final int bottom = top + size;
                        mDivider.setBounds(left, top, right, bottom);
                        mDivider.draw(c);
                    }
                }
            }
        }

    }

}
