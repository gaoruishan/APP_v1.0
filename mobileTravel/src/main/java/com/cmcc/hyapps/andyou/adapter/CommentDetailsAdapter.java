
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemClickListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.PhotoPreviewActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.CompoundImage;
import com.cmcc.hyapps.andyou.model.CompoundImage.TextImage;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class CommentDetailsAdapter extends BaseHeaderAdapter<Comment, Comment> {
    private static final String COMMENT_TIME_FORMAT = "MM-dd kk:mm";

    private Activity mActivity;
    private HeaderViewHolder mHeaderHolder;
    private static final int MAX_DISPLAY_IMAGES = 3;

    public CommentDetailsAdapter(Activity activity) {
        mActivity = activity;
    }

    public CommentDetailsAdapter(Activity activity, List<Comment> items) {
        this(activity);
        setDataItems(items);
    }

    public Comment getHeader() {
        return mHeader;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView coverImage;
        ImageView authorAvatar;
        TextView author;
        TextView content;
        TextView commentCount;
        TextView voteCount;
        RatingBar rating;
        TextView commentTime;
        RecyclerView imagesRecyclerView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            coverImage = (NetworkImageView) itemView.findViewById(R.id.comment_cover_image);
            authorAvatar = (ImageView) itemView.findViewById(R.id.author_avatar);
            author = (TextView) itemView.findViewById(R.id.comment_author_name);
            content = (TextView) itemView.findViewById(R.id.comment_content);
            rating = (RatingBar) itemView.findViewById(R.id.comment_rating);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);
            voteCount = (TextView) itemView.findViewById(R.id.comment_vote_count);
            commentTime = (TextView) itemView.findViewById(R.id.comment_time);
            imagesRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerview);
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView authorAvatar;
        TextView authorName;
        TextView commentTime;
        TextView commentContent;

        public CommentViewHolder(View itemView) {
            super(itemView);
            authorAvatar = (ImageView) itemView.findViewById(R.id.iv_avata);
            authorName = (TextView) itemView.findViewById(R.id.iv_name);
            commentContent = (TextView) itemView.findViewById(R.id.iv_content);
            commentTime = (TextView) itemView.findViewById(R.id.iv_time);
        }
    }

    @Override
    public ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = View.inflate(parent.getContext(), R.layout.item_scenic_comment_details_header,
                null);
        mHeaderHolder = new HeaderViewHolder(v);

        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        mHeaderHolder.imagesRecyclerView.setLayoutManager(layoutManager);
        int scap = ScreenUtils.dpToPxInt(mActivity, 3);
        DividerItemDecoration decor = new DividerItemDecoration(scap, scap);
        decor.initWithRecyclerView(mHeaderHolder.imagesRecyclerView);
        mHeaderHolder.imagesRecyclerView.addItemDecoration(decor);
        mHeaderHolder.imagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ImageGalleryAdapter adapter = new ImageGalleryAdapter(mActivity);
        adapter.setScreenPadding(ScreenUtils.getDimenPx(mActivity,
                R.dimen.scenic_details_comment_image_padding));
        mHeaderHolder.imagesRecyclerView.setAdapter(adapter);
        return mHeaderHolder;
    }

    @Override
    public void onBinderHeaderViewHolder(ViewHolder holder) {
        Comment comment = mHeader;
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        if (comment.comment_images != null && !comment.comment_images.isEmpty()&& comment.comment_images.get(0).small_url != null) {
            headerHolder.coverImage.setDefaultImageResId(R.drawable.bg_banner_hint).setErrorImageResId(R.drawable.bg_banner_hint);
//            headerHolder.coverImage.setImageUrl(comment.comment_images.get(0).small_url, RequestManager .getInstance().getImageLoader());
            ImageUtil.DisplayImage(comment.comment_images.get(0).small_url, headerHolder.coverImage);
            bindCommentImageRecyclerView(headerHolder.imagesRecyclerView, comment);
        }

        if (comment.user != null&&comment.user.user_info!=null) {
            if (comment.user.user_info.avatar_url != null) {
                ImageUtil.DisplayImage(comment.user.user_info.avatar_url, headerHolder.authorAvatar, R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
//                RequestManager.getInstance().getImageLoader().get(comment.user.user_info.avatar_url,
//                                ImageLoader.getImageListener(headerHolder.authorAvatar,R.drawable.bg_avata_hint,R.drawable.bg_avata_hint));
            }
            headerHolder.author.setText(comment.user.user_info.nickname);
        }

        headerHolder.content.setText(comment.content);
        headerHolder.commentCount.setText(String.valueOf(comment.commentCount));
        headerHolder.voteCount.setText(String.valueOf(comment.voteCount));
        headerHolder.rating.setRating(comment.rating);
        if (TextUtils.isEmpty(comment.created)) {
            headerHolder.commentTime.setVisibility(View.GONE);
        } else {
            headerHolder.commentTime.setVisibility(View.VISIBLE);
            headerHolder.commentTime.setText(TimeUtils.formatTime(comment.created, COMMENT_TIME_FORMAT));
        }

        attachClickListener(headerHolder, headerHolder.coverImage, 0);
    }

    private void bindCommentImageRecyclerView(RecyclerView tWayView, Comment comment) {
        if (comment.images == null || comment.images.isEmpty()) {
            tWayView.setVisibility(View.GONE);
            return;
        }

        tWayView.setVisibility(View.VISIBLE);

        ItemClickSupport clickSupport = ItemClickSupport.addTo(tWayView);
        clickSupport.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                MobclickAgent.onEvent(mActivity, MobConst.ID_INDEX_SCENIC_COMMENT_IMAGE);
                List<TextImage> items = (List<TextImage>) parent.getTag();
                ArrayList<Image> imageList = new ArrayList<Image>();
                for (TextImage textImage : items) {
                    if (textImage != null && textImage.image != null) {
                        Image image = new Image();
                        image.imagePath = Uri.parse(textImage.image.largeImage);
                        image.thumbnailPath = Uri.parse(textImage.image.smallImage);
                        imageList.add(image);
                    }
                }

                Intent intent = new Intent(mActivity,
                        PhotoPreviewActivity.class);
                intent.putExtra(Const.EXTRA_IMAGE_DATA, imageList);
                intent.putExtra(Const.EXTRA_IMAGE_PREVIEW_START_INDEX,
                        position);
                mActivity.startActivity(intent);
            }
        });

        List<TextImage> imageList = new ArrayList<TextImage>();
        for (CompoundImage i : comment.images) {
            TextImage textImage = new TextImage(i, null, null);
            imageList.add(textImage);
        }

        ImageGalleryAdapter adapter = (ImageGalleryAdapter) tWayView.getAdapter();
        adapter.setDataItems(imageList);
        tWayView.setTag(imageList);

        int scap = ScreenUtils.dpToPxInt(mActivity, 3);
        int padding = ScreenUtils.getDimenPx(mActivity,
                R.dimen.scenic_details_comment_image_padding);
        int itemHeight = ((ScreenUtils.getScreenWidth(mActivity) - scap * 2)
                - padding * 2) / 3;
        int rowCount = (imageList.size() + 2) / 3;
        int height = itemHeight * rowCount + scap * (rowCount - 1)
                + padding * 2;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                height);
        tWayView.setLayoutParams(lp);
        tWayView.setPadding(padding, padding, padding, padding);
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel_comment, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBinderItemViewHolder(ViewHolder holder, int position) {
        final Comment comment = mDataItems.get(position);
        holder.itemView.setTag(comment);
        final CommentViewHolder commentHolder = (CommentViewHolder) holder;

        if (comment.user != null) {
            if (comment.user.user_info.avatar_url != null) {
                ImageUtil.DisplayImage(comment.user.user_info.avatar_url, commentHolder.authorAvatar, R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
//                RequestManager.getInstance().getImageLoader() .get(comment.user.user_info.avatar_url, ImageLoader.getImageListener(commentHolder.authorAvatar,R.drawable.bg_avata_hint, R.drawable.bg_avata_hint));
            }
            commentHolder.authorName.setText(comment.user.user_info.nickname);
        }

        commentHolder.commentContent.setText(comment.content.trim());
        commentHolder.commentTime.setText(TimeUtils.formatTime(comment.created, COMMENT_TIME_FORMAT));
    }

    public TextView getVoteCountView() {
        return mHeaderHolder != null ? mHeaderHolder.voteCount : null;
    }
}
