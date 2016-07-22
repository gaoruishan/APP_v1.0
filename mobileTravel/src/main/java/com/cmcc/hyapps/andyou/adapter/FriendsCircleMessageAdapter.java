package com.cmcc.hyapps.andyou.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.manager.TimeManager;
import com.cmcc.hyapps.andyou.model.QHMessages;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.widget.roundimageview.RoundedImageView;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;

/**
 * 圈子消息列表适配器
 * Created by bingbing on 2015/10/27.
 */
public class FriendsCircleMessageAdapter extends AppendableAdapter<QHMessages> {
    private Context mContext;

    public FriendsCircleMessageAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_circle_message_list_item, parent, false);
        return new MessageHolderView(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageHolderView messageHolderView = (MessageHolderView) holder;
        QHMessages qhMessages = mDataItems.get(position);
        messageHolderView.setTag(qhMessages);
        if (qhMessages.getFromUser() != null) {
            ImageUtil.DisplayImage(qhMessages.getFromUser().getAvatarUrl(), messageHolderView.mRoundedImageView,
                    R.drawable.recommand_bg, R.drawable.recommand_bg);
            if (!TextUtils.isEmpty(qhMessages.getFromUser().getNickname())) {
                messageHolderView.name.setText(qhMessages.getFromUser().getNickname());
            }
        }
        if (!TextUtils.isEmpty(qhMessages.getCreateTime())) {
            messageHolderView.time.setText(TimeManager.getTime(qhMessages.getCreateTime()));
        }
        //0表示评论 1 表示回复评论 2  表示点赞
        if (qhMessages.getMessType() == 2) {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.friends_circle_vote);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            messageHolderView.conent.setCompoundDrawables(drawable, null, null, null);
        }
        if (qhMessages.getMessType() == 0) {
            if (qhMessages.getOperateObject() != null && !TextUtils.isEmpty(qhMessages.getContentText())) {
                messageHolderView.conent.setText(qhMessages.getContentText());
            }
        }
        if (qhMessages.getMessType() == 1) {
            if (qhMessages.getFromUser() != null && qhMessages.getToUser() != null) {
                String content = "回复: " + qhMessages.getToUser().getNickname() + " " + qhMessages.getContentText();
                SparseIntArray sparseIntArray = new SparseIntArray(2);
//                sparseIntArray.put(0, qhMessages.getFromUser().getNickname().length());
                sparseIntArray.put(content.indexOf(qhMessages.getToUser().getNickname()), content.indexOf(qhMessages.getToUser().getNickname()) + qhMessages.getToUser().getNickname().length());
                AppUtils.setAreaTextColor(messageHolderView.conent, sparseIntArray, content, mContext);
            }
        }
        attachClickListener(messageHolderView, messageHolderView.mRoundedImageView, position);
        attachClickListener(messageHolderView, messageHolderView.mView, position);

    }

    private class MessageHolderView extends RecyclerView.ViewHolder {
        private RoundedImageView mRoundedImageView;
        private TextView name, conent, time;
        private View mView;

        public MessageHolderView(View itemView) {
            super(itemView);
            mRoundedImageView = (RoundedImageView) itemView.findViewById(R.id.friends_message_item_avator);
            name = (TextView) itemView.findViewById(R.id.friends_message_item_name);
            conent = (TextView) itemView.findViewById(R.id.friends_message_item_content);
            time = (TextView) itemView.findViewById(R.id.friends_message_item_time);
            mView = itemView.findViewById(R.id.friends_message_item_linear);
        }

        public void setTag(QHMessages qhMessages) {
            mRoundedImageView.setTag(qhMessages);
            mView.setTag(qhMessages);
        }
    }
}
