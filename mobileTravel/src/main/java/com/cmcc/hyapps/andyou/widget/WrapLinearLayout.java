package com.cmcc.hyapps.andyou.widget;

        import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

        import com.cmcc.hyapps.andyou.util.ScreenUtils;

        import java.util.Hashtable;

public class WrapLinearLayout extends LinearLayout {
    private int row;
    int mLeft, mRight, mTop, mBottom;
    Hashtable map = new Hashtable();
    public WrapLinearLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    private final static String TAG = "MyViewGroup";
    private int widthMeasureSpec,  heightMeasureSpec;
    private final static int VIEW_MARGIN = 2;


    public WrapLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public WrapLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mCount = getChildCount();
        int mX = 0;
        int mY = 0;
        mLeft = 0;
        mRight = 0;
        mTop = 5;
        mBottom = 0;

        int j = 0;

        //View lastview = null;
        for (int i = 0; i < mCount; i++) {
            final View child = getChildAt(i);

            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            // 此处增加onlayout中的换行判断，用于计算所需的高度
            int childw = child.getMeasuredWidth();
            int childh = child.getMeasuredHeight();
            //控件在后面显示，不换行
            mX += childw + 10;  //将每次子控件宽度进行统计叠加，如果大于设定的高度则需要换行，高度即Top坐标也需重新设置

            Position position = new Position();
            mLeft = getPosition(i - j, i);
            mRight = mLeft + child.getMeasuredWidth();

            if (mX >= mWidth) {
                mX = childw;
                mY += childh;
                j = i;
                mLeft = 0;
                mRight = mLeft + child.getMeasuredWidth();
                if(i > 0)
                    mTop = mY + 10;
                //PS：如果发现高度还是有问题就得自己再细调了
            }

            mBottom = mTop + child.getMeasuredHeight();
            mY = mTop;  //每次的高度必须记录 否则控件会叠加到一起
            position.left = mLeft;
            position.top = mTop + 3;
            position.right = mRight;
            if(position.right > mWidth)
            {
                position.right = mWidth;
                ((TextView)child).setWidth(mWidth);
            }
            position.bottom = mBottom;

            map.put(child, position);
        }
        setMeasuredDimension(mWidth, mBottom);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            Position pos = (Position)map.get(child);
            if (pos != null) {
                child.layout(pos.left, pos.top, pos.right, pos.bottom);
            } else {
                Log.e(TAG, "error");
            }
        }
    }

    private class Position {
        int left, top, right, bottom;
    }

    public int getPosition(int IndexInRow, int childIndex) {
        if (IndexInRow > 0) {
            return getPosition(IndexInRow - 1, childIndex - 1)
                    + getChildAt(childIndex - 1).getMeasuredWidth() + ScreenUtils.dpToPxInt(getContext(), 5);//改变标签
        }
        return getPaddingLeft();
    }
}
