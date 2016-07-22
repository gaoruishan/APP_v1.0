
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.QHNavigation;
import com.cmcc.hyapps.andyou.support.OnClickListener;

import java.util.List;

public class NaviListAdapter extends AppendableAdapter<QHNavigation> {
    private Activity mContext;
    Location myLocation;

    public OnClickedListener mOnClickedListener;
    public interface OnClickedListener{
        public void onItemClicked(View v);
    }

    public NaviListAdapter(Activity context) {
        this.mContext = context;
    }

    public NaviListAdapter(Activity context,OnClickedListener mOnClickedListener) {
        this.mContext = context;
        this.mOnClickedListener = mOnClickedListener;
    }

    public NaviListAdapter(Activity context, List<QHNavigation> items) {
        this(context);
        this.mDataItems = items;
    }
    public void initPosition(Location myLocation){
        this.myLocation = myLocation;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel, parent, false);
        View v = View.inflate(mContext,R.layout.item_navigation,null);
        return new NaviViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NaviViewHolder viewHolder = (NaviViewHolder) holder;
        final QHNavigation item = mDataItems.get(position);
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                mOnClickedListener.onItemClicked(v);
//                QHScenic rest = (QHScenic) v.getTag();
//                Intent intent  = new Intent();
//                intent.putExtra(Const.REST_DETAIL,rest);
//                intent.setClass(mContext,HotelDetailActivity.class);
//                mContext.startActivity(intent);
            }
        });
        viewHolder.navigation_name.setText(item.name);

    }

    class NaviViewHolder extends ViewHolder {

        public TextView navigation_name;

        public NaviViewHolder(View itemView) {
            super(itemView);
            navigation_name = (TextView) itemView.findViewById(R.id.navigation_item);

        }
    }
}
