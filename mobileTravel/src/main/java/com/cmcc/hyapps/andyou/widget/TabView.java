
package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

import com.cmcc.hyapps.andyou.util.ScreenUtils;

/**
 * Tab view for indicate current fragment
 * 
 * @author Kuloud
 */
public class TabView extends CheckedTextView {
    private final int UNDERLINE_HEIGHT = 3; // dp
    private final int UNDERLINE_MARGIN_BOTTOM = 0; // dp
    private final int UNDERLINE_COLOR = 0xff2196f3;

    private final int UNDERLINE_FULL = 1;
    private final int UNDERLINE_TEXT_WRAP = 2;

    private Paint mPaint;
    private int mTextWidth;
    private float mUnderlineMarginBottom;
    private int mUnderlineMode;

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(UNDERLINE_COLOR);
        mPaint.setStrokeWidth(ScreenUtils.dpToPxInt(context, UNDERLINE_HEIGHT));

        mUnderlineMarginBottom = ScreenUtils.dpToPx(context, UNDERLINE_MARGIN_BOTTOM);

        mUnderlineMode = UNDERLINE_FULL;

        // for UNDERLINE_TEXT_WRAP
        // Rect bounds = new Rect();
        // // As the text is immutable in this case, just get width when
        // // initialize.
        // getPaint().getTextBounds(getText().toString(), 0, getText().length(),
        // bounds);
        // mTextWidth = bounds.width();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isChecked()) {
            int y = (int) (getHeight() - mPaint.getStrokeWidth() / 2 - mUnderlineMarginBottom);
            switch (mUnderlineMode) {
                case UNDERLINE_FULL:
                    canvas.drawLine(0, y, getWidth(), y, mPaint);
                    break;
                case UNDERLINE_TEXT_WRAP:
                    int startX = (getWidth() - mTextWidth) / 2;
                    int endX = startX + mTextWidth;
                    canvas.drawLine(startX, y, endX, y, mPaint);
                    break;

                default:
                    break;
            }
        }
    }
}
