
package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ViewSquare extends View {

    public ViewSquare(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ViewSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewSquare(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
