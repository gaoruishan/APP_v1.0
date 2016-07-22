
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.QHComment;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.model.ScenicDetails;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.TimeUtils;

import java.util.List;

/**
 * @author kuloud
 */
public class CommentAdapter extends BaseHeaderAdapter<QHStrategy, QHComment> implements
        OnClickListener {
    private static final String COMMENT_TIME_FORMAT = "yyyy-MM-dd kk:mm";
    private OnHeaderItemClickedListener mHeaderItemClickedListener;

    public CommentAdapter() {
        super();
    }

    public CommentAdapter(boolean headerEnable) {
        super(headerEnable);
    }

    public CommentAdapter(List<QHComment> items) {
        setDataItems(items);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView authorAvatar;
        TextView author;
        TextView content;
        TextView time;

        public CommentViewHolder(View itemView) {
            super(itemView);
            authorAvatar = (ImageView) itemView.findViewById(R.id.iv_avata);
            author = (TextView) itemView.findViewById(R.id.iv_name);
            content = (TextView) itemView.findViewById(R.id.iv_content);
            time = (TextView) itemView.findViewById(R.id.iv_time);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView introTitle;
        TextView introSummary;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            coverImage = (ImageView) itemView.findViewById(R.id.scenic_cover_image);
            introTitle = (TextView) itemView.findViewById(R.id.scenic_intro_title);
            introSummary = (TextView) itemView.findViewById(R.id.scenic_intro_summary);
        }
    }

    public OnHeaderItemClickedListener getHeaderItemClickedListener() {
        return mHeaderItemClickedListener;
    }

    public void setOnHeaderItemClickedListener(OnHeaderItemClickedListener headerItemClickedListener) {
        this.mHeaderItemClickedListener = headerItemClickedListener;
    }

    public interface OnHeaderItemClickedListener {
        void onHeaderItemClicked(View view);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        if (mHeaderItemClickedListener != null) {
            mHeaderItemClickedListener.onHeaderItemClicked(v);
        }
    }

    @Override
    public ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_detail, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBinderHeaderViewHolder(ViewHolder holder) {
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel_comment, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBinderItemViewHolder(ViewHolder holder, int position) {
        final QHComment comment = mDataItems.get(position);
        holder.itemView.setTag(comment);
        final CommentViewHolder commentHolder = (CommentViewHolder) holder;
        if (!TextUtils.isEmpty(comment.user.user_info.avatar_url))
        {
            ImageUtil.DisplayImage(comment.user.user_info.avatar_url, commentHolder.authorAvatar, R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
//            RequestManager.getInstance().getImageLoader().get(comment.user.user_info.avatar_url, ImageLoader.getImageListener(commentHolder.authorAvatar, R.drawable.bg_avata_hint,R.drawable.bg_avata_hint));
        }
        commentHolder.author.setText(comment.user.user_info.nickname);
        commentHolder.content.setText(comment.content);

        if (TextUtils.isEmpty(comment.created)) {
            commentHolder.time.setVisibility(View.GONE);
        } else {
            commentHolder.time.setVisibility(View.VISIBLE);
            commentHolder.time.setText(TimeUtils.formatTime(comment.created, COMMENT_TIME_FORMAT));
        }
    }


   @Override
    public int getItemCount() {
         return mDataItems.size();
    }
}
