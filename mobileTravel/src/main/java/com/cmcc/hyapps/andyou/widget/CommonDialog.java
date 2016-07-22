
package com.cmcc.hyapps.andyou.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;

/**
 * @author kuloud
 */
public class CommonDialog extends DialogView implements OnClickListener {
    private TextView mTitle;
    private TextView mContentView;
    private TextView mLeftBtn;
    private TextView mRightBtn;

    private OnDialogViewClickListener dialogViewClick;

    public CommonDialog(Context context) {
        this(context, R.layout.layout_dialog);
    }

    public CommonDialog(Context context, int layoutId) {
        super(context, layoutId);
        init();

    }

    private void init() {
        mTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        mContentView = (TextView) view.findViewById(R.id.tv_dialog_content);
        mLeftBtn = (TextView) view.findViewById(R.id.btn_dialog_left);
        mRightBtn = (TextView) view.findViewById(R.id.btn_dialog_right);
        if (mLeftBtn != null)
            mLeftBtn.setOnClickListener(this);
        if (mRightBtn != null)
            mRightBtn.setOnClickListener(this);
    }

    @Override
    protected void onLoadLayout(View view) {
    }

    public void setContentText(String content) {
        mContentView.setText(content);
    }

    public void setContentText(int strId) {
        mContentView.setText(strId);
    }

    public void setLeftButtonText(String leftString) {
        mLeftBtn.setText(leftString);
    }

    public void setRightButtonText(String rightString) {
        mRightBtn.setText(rightString);
    }

    public void setLeftButtonText(int leftStrId) {
        mLeftBtn.setText(leftStrId);
    }

    public void setRightButtonText(int rightStrId) {
        mRightBtn.setText(rightStrId);
    }

    public void setTitleText(int res) {
        mTitle.setText(res);
    }

    public TextView getContentView() {
        return mContentView;
    }

    public TextView getLeftBtn() {
        return mLeftBtn;
    }

    public TextView getRightBtn() {
        return mRightBtn;
    }

    public interface OnDialogViewClickListener {
        public void onLeftButtonClick();

        public void onRightButtonClick();
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_dialog_left:
                if (dialogViewClick != null) {
                    dialogViewClick.onLeftButtonClick();
                }
                break;
            case R.id.btn_dialog_right:
                if (dialogViewClick != null) {
                    dialogViewClick.onRightButtonClick();
                }
                break;
        }
        dismissDialog();
    }

    public void setOnDialogViewClickListener(OnDialogViewClickListener dialogViewClick) {
        this.dialogViewClick = dialogViewClick;
    }

    public View getRootView() {
        return view;
    }

    public Dialog getDialog() {
        return dialog;
    }
}
