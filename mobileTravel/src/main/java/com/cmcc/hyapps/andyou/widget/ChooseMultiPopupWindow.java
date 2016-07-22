package com.cmcc.hyapps.andyou.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;

import java.util.List;

/**
 * Created by bingbing on 2015/10/9.
 */
public class ChooseMultiPopupWindow extends PopupWindow implements View.OnClickListener{
    private View mView,spaceView;
    private ListView mListView;
    private Context mContext;
    private TextView chooseTextView;
    //to show this position of image like "√"
    private OnPopupWindowsMultiSelectListener listener;
    private List<String> mList;
    private PopupMultiSelectAdapter multiSelectAdapter;
    private Button positiveButton;
    public ChooseMultiPopupWindow(Context mContext,List<String> mList){
        this.mContext = mContext;
        this.mList = mList;
        mView = LayoutInflater.from(mContext).inflate(R.layout.multi_choose_popup_window,null);
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
        positiveButton = (Button)parentView.findViewById(R.id.multi_select_button);
        positiveButton.setOnClickListener(this);
        mListView = (ListView) parentView.findViewById(R.id.choose_popup_window_list);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mListView.getLayoutParams();
        layoutParams.height = getPhoneHeight(mContext)/2;
        layoutParams.width = AbsListView.LayoutParams.MATCH_PARENT;
        mListView.setLayoutParams(layoutParams);
        multiSelectAdapter = new PopupMultiSelectAdapter(list, context);
        mListView.setAdapter(multiSelectAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.getCheckedItemPositions().put(0,true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if ( position == 0){
                    SparseBooleanArray sparseBooleanArray = getSelectPosition();
                    for (int i = 0; i < sparseBooleanArray.size(); i++) {
                        if (i != 0){
                            if (sparseBooleanArray.valueAt(i)){
                                sparseBooleanArray.put(sparseBooleanArray.keyAt(i),false);
                            }
                        }
                    }
                }else {
                    getSelectPosition().put(0,false);
                }
                multiSelectAdapter.notifyDataSetChanged();
            }
        });
    }

    public void showPopupWindow(View parentView){
        if (!isShowing())
            showAsDropDown(parentView,0,10);
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

    public void setDrawableRight(TextView mTextView,boolean ischoosed){
        Drawable drawable = null;
        if (ischoosed)
            drawable = mContext.getResources().getDrawable(R.drawable.pull_down_choosed);
        else
            drawable = mContext.getResources().getDrawable(R.drawable.icon_filter_more);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        mTextView.setCompoundDrawables(null,null,drawable,null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.multi_select_button:
                listener.onPopupMultiSelectClick();
                dismiss();
                break;
            case R.id.choose_popup_window_space:
                if (isShowing())
                    dismiss();
                break;
        }
    }

    public interface  OnPopupWindowsMultiSelectListener{
        void onPopupMultiSelectClick();
    }

    public void setOnPopupWindowsClickListener(OnPopupWindowsMultiSelectListener callBack){
        this.listener = callBack;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        chooseTextView.setTextColor(mContext.getResources().getColor(R.color.choose_popupwindow_text));
        setDrawableRight(chooseTextView, false);
    }

    protected class PopupMultiSelectAdapter extends BaseAdapter {
        private List<String> mList;
        private Context mContext;
        public PopupMultiSelectAdapter(List<String> strings,Context mContext){
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
            if (!TextUtils.isEmpty(mList.get(i))){
                    mViewHolder.textView.setText(mList.get(i));
            }
            if (mListView.isItemChecked(i)){
                mViewHolder.imageView.setVisibility(View.VISIBLE);
                mViewHolder.textView.setTextColor(mContext.getResources().getColor(R.color.market_actionbar));
            }
            else{
                mViewHolder.imageView.setVisibility(View.INVISIBLE);
                mViewHolder.textView.setTextColor(Color.BLACK);
            }
            return converView;
        }
        private class PopupViewHolder{
            TextView textView;
            ImageView imageView;
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

    public SparseBooleanArray getSelectPosition(){
        return  mListView.getCheckedItemPositions();
    }

}
