package com.cmcc.hyapps.andyou.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ScreenUtils;

/**
 * Created by Kuloud on 3/16/15.
 */
public class ScenicServiceAdapter extends BaseAdapter {
    private OnClickListener mServiceClickListener = null;
    private Context mContext;
    private int[] mServiceViewIds = {
            R.id.service_food, R.id.service_hotel, R.id.service_traffic,
            R.id.service_specialty,
            R.id.service_complaint, R.id.service_wc
    };

    private int[] mServiceTextIds = {
            R.string.service_food, R.string.service_hotel, R.string.service_traffic,
            R.string.service_specialty,
            R.string.service_complaint,
            R.string.service_wc
    };

    private int[] mServiceIconIds = {
            R.drawable.service_food_selecter, R.drawable.service_hotel_selecter,
            R.drawable.service_traffic_selecter,
            R.drawable.service_specialty_selecter, R.drawable.service_complaint_selecter,
            R.drawable.service_wc_selecter
    };

    public ScenicServiceAdapter(Context c, OnClickListener serviceClickListener) {
        mContext = c;
        mServiceClickListener = serviceClickListener;
    }

    @Override
    public int getCount() {
        return mServiceIconIds.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_scenic_service, parent, false);
            convertView.setLayoutParams(new GridView.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ScreenUtils.getDimenPx(mContext, R.dimen.scenic_service_height)));
            convertView.setId(mServiceViewIds[position]);
        }

        ((ImageView) convertView.findViewById(R.id.service_icon)).setImageResource(mServiceIconIds[position]);
        ((TextView) convertView.findViewById(R.id.service_name)).setText(mServiceTextIds[position]);
        convertView.setOnClickListener(mServiceClickListener);
        return convertView;
    }
}
