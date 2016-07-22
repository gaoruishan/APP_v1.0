package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.PhotoPreviewActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.manager.TimeManager;
import com.cmcc.hyapps.andyou.model.CommentImage;
import com.cmcc.hyapps.andyou.model.CompoundImage;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.QHFriendInfo;
import com.cmcc.hyapps.andyou.model.QHFriendsImage;
import com.cmcc.hyapps.andyou.model.QHFriendsUser;
import com.cmcc.hyapps.andyou.model.QHMessageCount;
import com.cmcc.hyapps.andyou.model.QHRoute;
import com.cmcc.hyapps.andyou.model.QHRouteInfo;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.roundimageview.RoundedImageView;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingbing on 2015/10/21.
 */
public class FriendsCircleAdapter extends BaseHeaderAdapter<QHMessageCount, QHFriendInfo> {

    private final String TAG = "FriendsCircleAdapter";
    private Activity mActivity;

    public FriendsCircleAdapter(Activity activity ) {
        mActivity = activity;
    }

    private View headView;
    private HomeFriendsCircleHeaderHolder homeFriendsCircleHeaderHolder;

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_circle_header_message_cout, parent, false);
        return new HomeFriendsCircleHeaderHolder(headView);
    }

    @Override
    public void onBinderHeaderViewHolder(RecyclerView.ViewHolder holder) {
        homeFriendsCircleHeaderHolder = (HomeFriendsCircleHeaderHolder) holder;
        if (mHeader.getNewMessageNum() != 0) {
            homeFriendsCircleHeaderHolder.message_count_textview.setText(mHeader.getNewMessageNum() + "条消息");
        } else {
            homeFriendsCircleHeaderHolder.mView.setVisibility(View.GONE);
        }
        attachClickListener(homeFriendsCircleHeaderHolder, homeFriendsCircleHeaderHolder.mView, 0);
    }

    public HomeFriendsCircleHeaderHolder getHeader() {
        return homeFriendsCircleHeaderHolder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_circle_recycle_item, parent, false);
        HomeFriendsCircleItemHolder homeFriendsCircleItemHolder = new HomeFriendsCircleItemHolder(view);
        initRecycleView(parent.getContext(), homeFriendsCircleItemHolder.mRecyclerView);
        return homeFriendsCircleItemHolder;
    }

    @Override
    public void onBinderItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeFriendsCircleItemHolder homeFriendsCircleItemHolder = (HomeFriendsCircleItemHolder) holder;
        QHFriendInfo qhFriendInfo = mDataItems.get(position);
        if (qhFriendInfo == null)
            return;
        homeFriendsCircleItemHolder.setDataTag(qhFriendInfo);
        if (qhFriendInfo.getPublishUser() != null) {
            QHFriendsUser qhFriendsUser = qhFriendInfo.getPublishUser();
            ImageUtil.DisplayImage(qhFriendsUser.getAvatarUrl(), homeFriendsCircleItemHolder.avatorImageView,
                    R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
            if (!TextUtils.isEmpty(qhFriendsUser.getNickname())) {
                homeFriendsCircleItemHolder.nameTextview.setText(qhFriendsUser.getNickname());
            }
        }

            if (!TextUtils.isEmpty(qhFriendInfo.getCreateTime())) {
                homeFriendsCircleItemHolder.timeTextview.setText(TimeManager.getTime(qhFriendInfo.getCreateTime()));
            }
            if (!TextUtils.isEmpty(qhFriendInfo.getInfoText())) {
                homeFriendsCircleItemHolder.contentTextView.setText(qhFriendInfo.getInfoText());
            }
            if (!TextUtils.isEmpty(qhFriendInfo.getAddress())) {
                homeFriendsCircleItemHolder.locationTextview.setVisibility(View.VISIBLE);
                homeFriendsCircleItemHolder.locationTextview.setText(qhFriendInfo.getAddress());
            } else {
                homeFriendsCircleItemHolder.locationTextview.setVisibility(View.INVISIBLE);
            }
            homeFriendsCircleItemHolder.voteTextview.setText(qhFriendInfo.getPraiseNum() + "");
            if (qhFriendInfo.getIsPraised() != 0) {
                homeFriendsCircleItemHolder.voteTextview.setSelected(true);
            } else
                homeFriendsCircleItemHolder.voteTextview.setSelected(false);

            if (qhFriendInfo.getPublishUser().getUserId().equals(AppUtils.getQHUser(mActivity).id + "")) {
                homeFriendsCircleItemHolder.deleteImageView.setVisibility(View.VISIBLE);
            } else
                homeFriendsCircleItemHolder.deleteImageView.setVisibility(View.GONE);

            homeFriendsCircleItemHolder.argumentTextview.setText(qhFriendInfo.getCommentNum() + "");
            bindImageRecyclerView(homeFriendsCircleItemHolder.mRecyclerView, qhFriendInfo);
            homeFriendsCircleItemHolder.itemView.requestLayout();
            attachClickListener(homeFriendsCircleItemHolder, homeFriendsCircleItemHolder.avatorImageView, position);
            attachClickListener(homeFriendsCircleItemHolder, homeFriendsCircleItemHolder.itemView, position);
            attachClickListener(homeFriendsCircleItemHolder, homeFriendsCircleItemHolder.voteTextview, position);
            attachClickListener(homeFriendsCircleItemHolder, homeFriendsCircleItemHolder.deleteImageView, position);
            attachClickListener(homeFriendsCircleItemHolder, homeFriendsCircleItemHolder.nameTextview, position);

    }

    public class HomeFriendsCircleHeaderHolder extends RecyclerView.ViewHolder {
        //        private RoundedImageView mRoundedImageView;
        private TextView message_count_textview;
        private View mView;

        public HomeFriendsCircleHeaderHolder(View itemView) {
            super(itemView);
//            mRoundedImageView = (RoundedImageView) itemView.findViewById(R.id.friends_circle_header_message_avator);
            message_count_textview = (TextView) itemView.findViewById(R.id.friends_circle_header_message_count);
            mView = itemView.findViewById(R.id.friends_circle_header_message_layout);
        }

        public TextView getMessage_count_textview() {
            return message_count_textview;
        }
    }

    private class HomeFriendsCircleItemHolder extends RecyclerView.ViewHolder {
        private RoundedImageView avatorImageView;
        private TextView nameTextview, contentTextView, timeTextview, locationTextview, voteTextview, argumentTextview;
        private RecyclerView mRecyclerView;
        private View mainView;
        private ImageView deleteImageView;

        public HomeFriendsCircleItemHolder(View itemView) {
            super(itemView);
            avatorImageView = (RoundedImageView) itemView.findViewById(R.id.friends_circle_recycle_item_avator);
            nameTextview = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_name);
            timeTextview = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_time);
            contentTextView = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_content);
            locationTextview = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_location);
            voteTextview = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_vote);
            argumentTextview = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_argument);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.friends_circle_recycle_item_recycleview);
            mainView = itemView.findViewById(R.id.friends_circle_recycle_item_main_layout);
            deleteImageView = (ImageView) itemView.findViewById(R.id.friends_circle_recycle_item_delete);

        }

        public void setDataTag(QHFriendInfo qhFriendInfo) {
            avatorImageView.setTag(qhFriendInfo);
            mainView.setTag(qhFriendInfo);
            voteTextview.setTag(qhFriendInfo);
            deleteImageView.setTag(qhFriendInfo);
            nameTextview.setTag(qhFriendInfo);
        }
    }

    private void initRecycleView(Context context, RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        int scap = ScreenUtils.dpToPxInt(context, 3);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(scap, scap);
        dividerItemDecoration.initWithRecyclerView(recyclerView);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ImageGalleryAdapter adapter = new ImageGalleryAdapter(mActivity);
        int screenPadding = ScreenUtils.getDimenPx(mActivity, R.dimen.scenic_details_comment_image_padding);
        adapter.setScreenPadding(screenPadding);
        recyclerView.setAdapter(adapter);
    }

    private void bindImageRecyclerView(RecyclerView recyclerView, QHFriendInfo qhFriendInfo) {
        if (qhFriendInfo.getImages() == null || qhFriendInfo.getImages().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                List<QHFriendsImage> qhFriendsImageList = (List<QHFriendsImage>) parent.getTag();
                ArrayList<Image> imageList = new ArrayList<Image>();
                for (QHFriendsImage item : qhFriendsImageList) {
                    if (item != null && !TextUtils.isEmpty(item.getImgPath())) {
                        Image image = new Image();
                        image.imagePath = Uri.parse(item.getImgPath());
                        image.thumbnailPath = Uri.parse(item.getImgPath());
                        imageList.add(image);
                    }
                }
                Intent intent = new Intent(mActivity, PhotoPreviewActivity.class);
                intent.putExtra(Const.EXTRA_IMAGE_DATA, imageList);
                intent.putExtra(Const.EXTRA_IMAGE_PREVIEW_START_INDEX, position);
                mActivity.startActivity(intent);
            }
        });
        List<CompoundImage.TextImage> imageList = new ArrayList<CompoundImage.TextImage>();
        for (int i = 0; i < qhFriendInfo.getImages().size(); i++) {
            QHFriendsImage item = qhFriendInfo.getImages().get(i);
            CompoundImage comImg = new CompoundImage(item.getImgPath(), item.getImgPath());
            CompoundImage.TextImage textImage = new CompoundImage.TextImage(comImg, null, null);
            imageList.add(textImage);
        }

        ImageGalleryAdapter adapter = (ImageGalleryAdapter) recyclerView.getAdapter();
        if (qhFriendInfo.getImages() != null)
            adapter.setDataItems(imageList);
        recyclerView.setTag(qhFriendInfo.getImages());

        int scap = ScreenUtils.dpToPxInt(mActivity, 3);
        int padding = ScreenUtils.getDimenPx(mActivity, R.dimen.scenic_details_comment_image_padding);
        LinearLayout.LayoutParams lp;
        if (imageList.size() == 1) {
            int width = ScreenUtils.getScreenWidth(mActivity) /5 * 4 * 3;
            int height = width /3;
            lp = new LinearLayout.LayoutParams(width,height);
        } else {
            int itemHeight = ((ScreenUtils.getScreenWidth(mActivity) - scap * 2) - padding * 2) / 3;
            int rowCount = (imageList.size() + 2) / 3;
            int height = itemHeight * rowCount + scap * (rowCount - 1) + padding * 2;
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        }
        recyclerView.setLayoutParams(lp);
        recyclerView.setPadding(padding, padding, padding, padding);
    }
}
