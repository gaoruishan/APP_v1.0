package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingbing on 2015/10/8.
 */
public class LandScapeScrollerView extends LinearLayout {

    private OnItemClickListener mOnItemClickListener;
    private List<View> views = new ArrayList<View>();
    private LinearLayout linearLayout;
    private View view;
    //点击中的view
    private TextView currentView;
    //首次进来，默认的view的position
    private int first;

    public void setFirst(int first) {
        this.first = first;
    }

    public LandScapeScrollerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LandScapeScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LandScapeScrollerView(Context context) {
        super(context);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.landscapeview, null);
        linearLayout = (LinearLayout) view.findViewById(R.id.land_scape_linear);
        addView(view);
    }

    public void addInnerView(SparseArray<String> list, final Context context) {

        for (int j = 0;j < list.size(); j ++) {
            final TextView mTextView = new TextView(context);
            MarginLayoutParams layoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(25, 0, 25, 0);
            LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(layoutParams);
            mTextView.setLayoutParams(pm);
            mTextView.setTag(j);
            mTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        setCurrentView((TextView) v, context);
                        mOnItemClickListener.onItemOnclick(v);
                    } else {
                        throw new RuntimeException("mOnItemClickListener is null,please set it");
                    }
                }
            });
            mTextView.setText(list.valueAt(j));
            mTextView.setTextSize(14);
            mTextView.setGravity(Gravity.CENTER);
            mTextView.setSingleLine(true);
            if (j == first){
                mTextView.setTextColor(context.getResources().getColor(R.color.market_actionbar));
                setCurrentView(mTextView, context);
            }
            linearLayout.addView(mTextView);
            views.add(mTextView);
        }
    }

    public interface OnItemClickListener {
        void onItemOnclick(View view);
    }

    public void setOnItemClick(OnItemClickListener mOnItemClick) {
        this.mOnItemClickListener = mOnItemClick;
    }

    private void setCurrentView(TextView v, Context context) {
        if (currentView != null) {
            currentView.setTextColor(context.getResources().getColor(R.color.road_tab_text_color));
        }
        currentView = v;
        currentView.setTextColor(context.getResources().getColor(R.color.market_actionbar));
    }


}
