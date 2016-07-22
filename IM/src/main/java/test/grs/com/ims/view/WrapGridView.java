/**
 * 项目名称：EIMClient
 * 类 名 称：WrapGridView
 * 类 描 述：(描述信息)
 * 创 建 人：XUYONGJIE
 * 创建时间：2014年12月10日 下午9:13:31
 * 修 改 人：XUYONGJIE
 * 修改时间：2014年12月10日 下午9:13:31
 * 修改备注：
 * @version
 * 
*/
package test.grs.com.ims.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * @包名：com.littlec.chatdemo.view
 * @类名：WrapGridView
 * @描述：(描述这个类的作用)
 * @作者：XUYONGJIE
 * @时间：2014年12月10日下午9:13:31
 * @版本：1.0.0
 * 
 */
public class WrapGridView extends GridView {
	
	private static final int Blank_POSITION = -1;
	boolean expanded = false;
    private OnTouchBlankPositionListener mTouchBlankPosListener;

    public WrapGridView(Context context) {
        super(context);
    }

    public WrapGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isExpanded()) {
            // Calculate entire height by providing a very large height hint.
            // View.MEASURED_SIZE_MASK represents the largest height possible.
            int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    
    public interface OnTouchBlankPositionListener {
        /**
         * 
         * @return 是否要终止事件的路由
         */
        boolean onTouchBlankPosition();
    }
    
    public void setOnTouchBlankPositionListener(OnTouchBlankPositionListener listener) {
        mTouchBlankPosListener = listener;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        if(mTouchBlankPosListener != null) {
            if (!isEnabled()) {
                // A disabled view that is clickable still consumes the touch
                // events, it just doesn't respond to them.
                return isClickable() || isLongClickable();
            }
                
            if(event.getActionMasked() == MotionEvent.ACTION_UP) {
                final int motionPosition = pointToPosition((int)event.getX(), (int)event.getY());
                if( motionPosition == Blank_POSITION ) {
                    return mTouchBlankPosListener.onTouchBlankPosition();
                }
            }
        }

        return super.onTouchEvent(event);
    }
	
}
