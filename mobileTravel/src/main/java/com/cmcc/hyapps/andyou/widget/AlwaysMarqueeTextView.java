/**
 * 
 */

package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Kuloud
 */
public class AlwaysMarqueeTextView extends TextView {

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlwaysMarqueeTextView(Context context) {
        super(context);
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        setEllipsize(TruncateAt.MARQUEE);
        setSingleLine();
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
