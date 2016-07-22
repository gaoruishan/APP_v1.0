package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ShareManager;


/**
 * Created by bingbing on 2015/12/7.
 */
public class SharePopupWindows extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private View mView,extra;
    private TextView wechat_friends,wechat_circle,qq,sina,cancle;
    private OnSharePopupWindowsBack mOnSharePopupWindowsBack;
    public SharePopupWindows(Context context) {
        this.mContext = context;
        mView = LayoutInflater.from(mContext).inflate(R.layout.share_popup_windows_layout,null);
        setContentView(mView);
        initView();
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        setBackgroundDrawable(dw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.share_wechat_friends:
                mOnSharePopupWindowsBack.onWeChatShareBack();
                break;
            case  R.id.share_wechat_circle:
                mOnSharePopupWindowsBack.onWeChatCircleShareBack();
                break;
            case  R.id.share_qq:
                mOnSharePopupWindowsBack.onQQShareBack();
                break;
            case  R.id.share_sina:
                mOnSharePopupWindowsBack.onSinaShareBack();
                break;
            case  R.id.share_cancel:
            case R.id.share_extra:
                if (isShowing())
                    dismiss();
                break;
        }
    }

    private void initView(){
        wechat_friends = (TextView) mView.findViewById(R.id.share_wechat_friends);
        wechat_friends.setOnClickListener(this);
        wechat_circle = (TextView) mView.findViewById(R.id.share_wechat_circle);
        wechat_circle.setOnClickListener(this);
        qq = (TextView) mView.findViewById(R.id.share_qq);
        qq.setOnClickListener(this);
        sina = (TextView) mView.findViewById(R.id.share_sina);
        sina.setOnClickListener(this);
        cancle = (TextView) mView.findViewById(R.id.share_cancel);
        cancle.setOnClickListener(this);
        extra =  mView.findViewById(R.id.share_extra);
        extra.setOnClickListener(this);
    }

    public void setOnSharePopupWindowsBack(OnSharePopupWindowsBack onSharePopupWindowsBack) {
        mOnSharePopupWindowsBack = onSharePopupWindowsBack;
    }

    public interface OnSharePopupWindowsBack{
        void onQQShareBack();
        void onSinaShareBack();
        void onWeChatShareBack();
        void onWeChatCircleShareBack();
    }
}
