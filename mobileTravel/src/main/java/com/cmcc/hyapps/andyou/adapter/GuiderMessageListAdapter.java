
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.Message;
import com.cmcc.hyapps.andyou.util.ConstTools;

import java.util.List;

public class GuiderMessageListAdapter extends AppendableAdapter<Message> {
    private Activity mContext;
    public GuiderMessageListAdapter(Activity context) {
        this.mContext = context;
    }
    public GuiderMessageListAdapter(Activity context, List<Message> items) {
        this(context);
        this.mDataItems = items;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.guide_message_item, parent, false);
        return new GuiderItemHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GuiderItemHolder mHolder = (GuiderItemHolder)holder;
        Message item = mDataItems.get(position);
        mHolder.name.setText(item.from_user.user_info.nickname+"评论了你的"+item.title);
        if(item.read==0) {
            ConstTools.setTextViewIcon(mContext, mHolder.name, R.drawable.new_message, 0);
        }else if(item.read==1) {
            ConstTools.setTextViewIcon(mContext, mHolder.name,R.drawable.new_message,4);
        }
        attachClickListener(mHolder,mHolder.item,position);
        mHolder.setDataTag(item);
    }
    class GuiderItemHolder extends ViewHolder {
        TextView name;
        RelativeLayout item;
        public GuiderItemHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_name);
            item = (RelativeLayout) itemView.findViewById(R.id.item_message);
        }
        public void setDataTag(Message scenic) {
            itemView.setTag(scenic);
            item.setTag(scenic);
        }
    }
}
