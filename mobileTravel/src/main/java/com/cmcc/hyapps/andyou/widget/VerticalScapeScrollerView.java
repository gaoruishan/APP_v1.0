package com.cmcc.hyapps.andyou.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bingbing on 2016/1/12.
 */
public class VerticalScapeScrollerView extends LinearLayout {
    private OnItemClickListener mOnItemClickListener;
    private List<View> views = new ArrayList<View>();
    private View view;
    private LinearLayout linearLayout;
    //点击中的view
    private View currentView;
    //首次进来，默认的view的position
    private int first;

    public void setFirst(int first) {
        this.first = first;
    }

    public VerticalScapeScrollerView(Context context) {
        super(context);
    }

    public VerticalScapeScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VerticalScapeScrollerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.verticalscapeview, null);
        linearLayout = (LinearLayout) view.findViewById(R.id.vertical_scape_linear);
        addView(view);
    }

    public interface OnItemClickListener {
        void onItemOnclick(View view);
    }

    public void setOnItemClick(OnItemClickListener mOnItemClick) {
        this.mOnItemClickListener = mOnItemClick;
    }

    private void setCurrentView(View v, Context context) {

        if (currentView != null) {
            currentView.findViewById(R.id.vertical_item_tag).setVisibility(View.INVISIBLE);
            ((TextView)currentView.findViewById(R.id.vertical_item_content)).setTextColor(context.getResources().getColor(R.color.road_tab_text_color));
        }
        currentView = v;
        currentView.findViewById(R.id.vertical_item_tag).setVisibility(View.VISIBLE);
        ((TextView)currentView.findViewById(R.id.vertical_item_content)).setTextColor(context.getResources().getColor(R.color.market_actionbar));
    }

    public void addInnerView(LinkedHashMap<String,Boolean> list, final Context context) {
       Iterator iterator = list.entrySet().iterator();View v;
        while (iterator.hasNext()){
            Map.Entry<String,Boolean> item = (Map.Entry<String, Boolean>) iterator.next();
            if (item.getValue()){
                v = LayoutInflater.from(context).inflate(R.layout.vertical_item_parent, null);
            }else {
                v = LayoutInflater.from(context).inflate(R.layout.vertical_item, null);
            }
            v.setTag(first);
            ((TextView)v.findViewById(R.id.vertical_item_content)).setText(item.getKey());
            if (!item.getValue()){
                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null){
                            setCurrentView(v,context);
                            mOnItemClickListener.onItemOnclick(v);
                        }else {
                            throw new RuntimeException("mOnItemClickListener is null,please set it");
                        }
                    }
                });
            }
            if (0 == first){
                setCurrentView(v, context);
            }
            linearLayout.addView(v);
            views.add(v);
            first ++;
        }
    }
}
