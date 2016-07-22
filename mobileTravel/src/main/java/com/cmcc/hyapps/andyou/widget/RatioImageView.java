
package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;

/**
 * @author kuloud
 */
public class RatioImageView extends NetworkImageView {
    private float ratio = 1.0f;
    private int width;

    public static final int ADJUST_SIZE_BY_WIDTH = 1;
    public static final int ADJUST_SIZE_BY_HEIGHT = 2;
    private int limit = ADJUST_SIZE_BY_WIDTH;

    public RatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RatioImageView);
        ratio = typedArray.getFloat(R.styleable.RatioImageView_imageRatio,
                1.0f);
        limit = typedArray.getInt(R.styleable.RatioImageView_limit, 1);
        typedArray.recycle();
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RatioImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (limit == 1) {
            if (width > 0) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
                        MeasureSpec.EXACTLY);
            }
            int w = MeasureSpec.getSize(widthMeasureSpec);
            w = w - getPaddingLeft() - getPaddingRight();
            int h = (int) (w / ratio) + getPaddingTop() + getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        } else {
            int h = MeasureSpec.getSize(heightMeasureSpec);
            int w = (int) (h * ratio);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * @param ratio w/h
     */
    public void setRatio(float ratio) {
        this.ratio = ratio;
        requestLayout();
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
