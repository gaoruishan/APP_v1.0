package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by bingbing on 2015/11/6.
 */
public class FriendsRecycleView extends RecyclerView {
    public FriendsRecycleView(Context context) {
        super(context);
    }

    public FriendsRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FriendsRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandSpec);
    }
}
