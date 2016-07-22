
package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.cmcc.hyapps.andyou.R;

/**
 * @author kuloud
 */
public class DialogView {

    protected Context context;
    protected View view;
    protected android.app.Dialog dialog;

    protected DialogView(Context context) {
    }

    protected DialogView(Context context, int layoutId) {
        this.context = context;
        this.view = loadLayout(layoutId);
        initDialog();
    }

    private View loadLayout(int layoutId) {
        View v = LayoutInflater.from(context).inflate(layoutId, null);
        onLoadLayout(v);
        return v;
    }

    /**
     * @param context
     * @param view layout of dialog
     */
    public DialogView(Context context, View view) {
        this.context = context;
        this.view = view;
        initDialog();
    }

    /**
     * Should be override if init with #DialogView(Context context, int
     * layoutId)
     * 
     * @param view
     */
    protected void onLoadLayout(View view) {
    }

    protected void initDialog() {
        dialog = new android.app.Dialog(context, R.style.dialog_view_theme);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.dialog_view_animation);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);
    }

    public void setGravity(int gravity) {
        dialog.getWindow().setGravity(gravity);
    }

    public void showDialog() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setFullWidth(boolean isFull) {
        if (isFull && dialog != null) {
            LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = LayoutParams.MATCH_PARENT;
            lp.height = LayoutParams.WRAP_CONTENT;
        }
    }

}
