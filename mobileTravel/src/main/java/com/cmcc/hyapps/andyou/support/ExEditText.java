/**
 * 
 */

package com.cmcc.hyapps.andyou.support;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Common EditText, filter emoji, etc.
 * 
 * @author Kuloud
 */
public class ExEditText extends EditText {

    public ExEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ExEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        setFilters(null);
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        InputFilter[] fs = new InputFilter[filters == null ? 1 : filters.length + 1];
        for (int i = 0; i < fs.length; i++) {
            if (i == 0) {
                fs[i] = new EmojiInputFilter(getContext());
            } else {
                fs[i] = filters[i - 1];
            }
        }
        super.setFilters(fs);
    }
}
