/**
 * 
 */

package com.cmcc.hyapps.andyou.support;

import android.view.View;

import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;

/**
 * Samsung devices, click event trigger twice, so override the listener to avoid
 * this case.
 * 
 * @author Kuloud
 */
public abstract class OnClickListener implements android.view.View.OnClickListener {
    public abstract void onValidClick(View v);

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        onValidClick(v);
    }
}
