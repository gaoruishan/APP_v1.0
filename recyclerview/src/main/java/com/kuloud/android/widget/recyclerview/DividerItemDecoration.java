
package com.kuloud.android.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.GridLayoutManager;
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
public class DividerItemDecoration extends ItemDecoration {

    private int mVerticalGap;
    private int mHorizontalGap;

    private Drawable mDivider;

    public DividerItemDecoration() {
        this(Color.TRANSPARENT, 0, 0);
    }

    public DividerItemDecoration(int gap) {
        this(Color.TRANSPARENT, gap, 0);
    }

    public DividerItemDecoration(int verticalGap, int horizontalGap) {
        this(Color.TRANSPARENT, verticalGap, horizontalGap);
    }

    public DividerItemDecoration(int color, int verticalGap, int horizontalGap) {
        mDivider = new ColorDrawable(color);
        mVerticalGap = verticalGap;
        mHorizontalGap = horizontalGap;
    }

    public DividerItemDecoration(Drawable divider, int verticalGap,int horizontalGap) {
        mDivider = divider;
        mVerticalGap = verticalGap;
        mHorizontalGap = horizontalGap;
    }

    public DividerItemDecoration(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs,new int[] {
                android.R.attr.listDivider
        });
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    public void initWithRecyclerView(RecyclerView recyclerView) {
        int left = recyclerView.getPaddingLeft();
        int top = recyclerView.getPaddingTop();
        int right = recyclerView.getPaddingRight();
        int bottom = recyclerView.getPaddingBottom();
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            // Grid
            left -= mHorizontalGap >> 1;
            top -= mVerticalGap >> 1;
            right -= mHorizontalGap >> 1;
            bottom -= mVerticalGap >> 1;
        } else {
            // Linear
            top -= mVerticalGap >> 1;
            bottom -= mVerticalGap >> 1;
        }
        recyclerView.setPadding(left, top, right, bottom);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
            RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mDivider == null)
            return;

        outRect.top = mVerticalGap >> 1;
        outRect.left = mHorizontalGap >> 1;
        outRect.bottom = mVerticalGap >> 1;
        outRect.right = mHorizontalGap >> 1;

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
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int size = mVerticalGap;
                final int top = child.getTop() - params.topMargin;
                final int bottom = top + size;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        if (mHorizontalGap > 0) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();
            final int childCount = parent.getChildCount();

            for (int i = 1; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int size = mHorizontalGap;
                final int left = child.getLeft() - params.leftMargin;
                final int right = left + size;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}
