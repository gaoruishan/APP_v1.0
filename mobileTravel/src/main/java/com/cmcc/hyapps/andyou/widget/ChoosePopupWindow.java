package com.cmcc.hyapps.andyou.widget;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.util.ScreenUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/6/23 0023.
 */
public class ChoosePopupWindow extends PopupWindow implements View.OnClickListener{
    private View mView,spaceView;
    private ListView mListView;
    private Context mContext;
    private TextView chooseTextView;
    //to show this position of image like "√"
    private int currentPosition = 0;
    private OnPopupWindowsClickListener listener;
    private List<String> mList;
    private boolean isSet = true;
    private int style;// 0  distance   ;  1  price  2 else ;
    public ChoosePopupWindow(Context mContext,List<String> mList,int style){
        this.mContext = mContext;
        this.style = style;
        this.mList = mList;
        mView = LayoutInflater.from(mContext).inflate(R.layout.choose_popup_window,null);

        setContentView(mView);
        initView(mView, mList, mContext);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        setBackgroundDrawable(dw);
    }

    private void initView(View parentView, final List<String> list,Context context){
        spaceView = parentView.findViewById(R.id.choose_popup_window_space);
        spaceView.setOnClickListener(this);
        mListView = (ListView) parentView.findViewById(R.id.choose_popup_window_list);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mListView.getLayoutParams();
        layoutParams.height = getPhoneHeight(mContext)/2;
        layoutParams.width = AbsListView.LayoutParams.MATCH_PARENT;
        mListView.setLayoutParams(layoutParams);
        // set default currentposition
        setCurrentPosition(style);
        mListView.setAdapter(new PopupAdapter(list, context));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentPosition = i;
                if (style == 0 && !TextUtils.isEmpty(list.get(i))) {
                    //distance
                    if (i != list.size() - 1)
                        chooseTextView.setText(list.get(i) + "米");
                    else
                        chooseTextView.setText(list.get(i));
                }
                if (style == 1 && !TextUtils.isEmpty(list.get(i))) {
                    //price
                    if (i != list.size() - 1)
                        chooseTextView.setText(setPopupPriceTextshow(list.get(i)));
                    else
                        chooseTextView.setText(list.get(i));
                }
                if (style == 2 && !TextUtils.isEmpty(list.get(i))) {
                    //else
                    chooseTextView.setText(list.get(i));
                }
                dismiss();
                listener.onPopupItemClick();

            }
        });
    }

    public void showPopupWindow(View parentView){
        if (!isShowing())
            showAsDropDown(parentView,0,10);
        else
            dismiss();
    }

    public void showFriendsPopupWindow(View parentView ,int offY){
        if (!isShowing()){
            showAsDropDown(parentView,0,ScreenUtils.dpToPxInt(mContext, offY));
        }
        else
            dismiss();
    }

    /**
     * bind textview  to control color of it
     * @param mView
     */
    public void setChoosedView(TextView mView){
        chooseTextView = mView;
    }

    /**
     * 是否设置textview右边图片
     * @param isSet
     * @return
     */
    public void  isSetDrawableRight(boolean isSet){
        this.isSet = isSet;
    }

    public void dissmissPopupWindow(){
        if (isShowing())
            dismiss();
    }

    public void setDrawableRight(TextView mTextView,boolean ischoosed){
        Drawable drawable = null;
        if (ischoosed)
            drawable = mContext.getResources().getDrawable(R.drawable.pull_down_choosed);
        else
            drawable = mContext.getResources().getDrawable(R.drawable.icon_filter_more);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        mTextView.setCompoundDrawables(null,null,drawable,null);
    }
    public interface  OnPopupWindowsClickListener{
        void onPopupItemClick();
    }

    public void setOnPopupWindowsClickListener(OnPopupWindowsClickListener callBack){
        this.listener = callBack;
    }
    @Override
    public void dismiss() {
        super.dismiss();
        if (isSet){
            chooseTextView.setTextColor(mContext.getResources().getColor(R.color.choose_popupwindow_text));
            setDrawableRight(chooseTextView, false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.choose_popup_window_space:
                if (isShowing())
                    dismiss();
                break;
        }
    }

    protected class PopupAdapter extends BaseAdapter{
       private List<String> mList;
       private Context mContext;
        public PopupAdapter(List<String> strings,Context mContext){
            mList = strings;
            this.mContext = mContext;
        }
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View converView, ViewGroup viewGroup) {
            PopupViewHolder mViewHolder;
            if (converView == null){
                mViewHolder = new PopupViewHolder();
                converView = LayoutInflater.from(mContext).inflate(R.layout.choose_popupwindow_list_item, null);
                mViewHolder.textView = (TextView) converView.findViewById(R.id.choose_popupwindow_item_text);
                mViewHolder.imageView = (ImageView)converView.findViewById(R.id.choose_popupwindow_item_image);
                converView.setTag(mViewHolder);
            }
            mViewHolder = (PopupViewHolder) converView.getTag();
            if (style == 0 && !TextUtils.isEmpty(mList.get(i))){
                //distance
                if (i != mList.size()-1)
                    mViewHolder.textView.setText(mList.get(i)+"米");
                else
                    mViewHolder.textView.setText(mList.get(i));
            }
            if (style == 1 && !TextUtils.isEmpty(mList.get(i))){
                //price
                if (i != mList.size()-1)
                    mViewHolder.textView.setText(setPopupPriceTextshow(mList.get(i)));
                else
                    mViewHolder.textView.setText(mList.get(i));
            }
            if (style == 2 && !TextUtils.isEmpty(mList.get(i))){
                //else
                mViewHolder.textView.setText(mList.get(i));
            }
            if (currentPosition == i)
                mViewHolder.imageView.setVisibility(View.VISIBLE);
            else
                mViewHolder.imageView.setVisibility(View.INVISIBLE);
            return converView;
        }
       private class PopupViewHolder{
           TextView textView;
           ImageView imageView;
       }
    }
    public int getCurrentPosition(){
        return  currentPosition;
    }

    public String setPopupPriceTextshow(String item){
        String text = null;
        //格式为eg: 100_200
        if (!TextUtils.isEmpty(item) && item.contains("_")){
            int position = item.indexOf("_");
            String from = item.substring(0, position);
            String to = item.substring(position+1);
            StringBuffer buffer = new StringBuffer();
            if (to.contains("inf")){
                buffer.append(from).append("元以上");
            }else {
                buffer.append(from).append("-").append(to).append("元");
            }
            text = buffer.toString();
        }
        return text;
    }
    // is distance or price
    public void setStyle(int style){
        this.style = style;
    }

    private void setCurrentPosition(int style){
        switch (style){
            case 0:
                currentPosition = 1;
                break;
            case 1:
                currentPosition = mList.size()-1;
                break;
            default:
                currentPosition = 0;
        }
    }

    private int getPhoneHeight(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;     // 屏幕宽度（像素）
        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        //  float widthDP = width/density;
        return height;
    }
}
