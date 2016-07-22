package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.PhotoPreviewActivity;
import com.cmcc.hyapps.andyou.activity.UserInformationActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.manager.TimeManager;
import com.cmcc.hyapps.andyou.model.CompoundImage;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.QHDelete;
import com.cmcc.hyapps.andyou.model.QHFriendInfo;
import com.cmcc.hyapps.andyou.model.QHFriendsComment;
import com.cmcc.hyapps.andyou.model.QHFriendsImage;
import com.cmcc.hyapps.andyou.model.QHFriendsUser;
import com.cmcc.hyapps.andyou.model.QHPublicInfoDetail;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.roundimageview.RoundedImageView;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingbing on 2015/11/9.
 */
public class FriendsCircleDescriptionAdapter extends BaseHeaderAdapter<QHPublicInfoDetail, QHFriendsComment> {
    private Activity mActivity;
    private FriendsCircleDescriptionHeader header;

    public FriendsCircleDescriptionAdapter(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friends_circle_description_header, parent, false);
        FriendsCircleDescriptionHeader friendsCircleDescriptionHeader = new FriendsCircleDescriptionHeader(view);
        initTrendRecycleView(parent.getContext(), friendsCircleDescriptionHeader.contentRecycleView);
        initVoteRecycleView(parent.getContext(), friendsCircleDescriptionHeader.avatorRecycleView);
        return friendsCircleDescriptionHeader;
    }

    public FriendsCircleDescriptionHeader getHeader() {
        return header;
    }

    @Override
    public void onBinderHeaderViewHolder(RecyclerView.ViewHolder holder) {
        header = (FriendsCircleDescriptionHeader) holder;
        QHFriendsUser publishUser = mHeader.getPublishUser();
        if (publishUser != null && !TextUtils.isEmpty(publishUser.getNickname()))
            header.name.setText(mHeader.getPublishUser().getNickname());
        if (publishUser != null)
            ImageUtil.DisplayImage(publishUser.getAvatarUrl(), header.mRoundedImageView,
                    R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
        if (!TextUtils.isEmpty(mHeader.getInfoText()))
            header.content.setText(mHeader.getInfoText());
        if (!TextUtils.isEmpty(mHeader.getCreateTime()))
            header.time.setText(TimeManager.getTime(mHeader.getCreateTime()));
        if (!TextUtils.isEmpty(mHeader.getAddress())) {
            header.address.setText(mHeader.getAddress());
        } else {
            header.address_main_layout.setVisibility(View.GONE);
        }
        header.vote.setText(mHeader.getPraiseNum() + "");
        if (mHeader.getIsPraised() == 1) {
            header.vote.setSelected(true);
        } else {
            header.vote.setSelected(false);
        }
        header.hide_argument.setVisibility(View.GONE);
        header.hide_vote.setVisibility(View.GONE);
        if (mHeader.getCommentNum() == 0) {
            header.argument_count.setVisibility(View.GONE);
        } else {
            header.argument_count.setVisibility(View.VISIBLE);
            header.argument_count.setText(mHeader.getCommentNum() + "条评论");
        }

        if (mHeader.getPublishUser().getUserId().equals(AppUtils.getQHUser(mActivity).id + "")) {
            header.deleteImageView.setVisibility(View.VISIBLE);
        } else
            header.deleteImageView.setVisibility(View.GONE);

        bindContentImageRecyclerView(header.contentRecycleView, mHeader);
        bindPraiseImageRecyclerView(header.avatorRecycleView, mHeader);
        attachClickListener(header, header.mRoundedImageView, 0);
        attachClickListener(header, header.vote, 0);
        attachClickListener(header, header.deleteImageView, 0);

    }

    private void bindPraiseImageRecyclerView(RecyclerView recyclerView, QHPublicInfoDetail qhPublicInfoDetail) {
        if (qhPublicInfoDetail.getImages() == null || qhPublicInfoDetail.getImages().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        }
        recyclerView.setVisibility(View.VISIBLE);
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                List<QHFriendsUser> qhFriendsUsers = (List<QHFriendsUser>) parent.getTag();
                Intent intent = new Intent(mActivity, UserInformationActivity.class);
                if (qhFriendsUsers != null && qhFriendsUsers.size() != 0){
                    intent.putExtra("user_ID", qhFriendsUsers.get(position).getUserId());
                    mActivity.startActivity(intent);
                }
            }
        });
        List<CompoundImage.TextImage> imageList = new ArrayList<CompoundImage.TextImage>();
        if (qhPublicInfoDetail.getPraiseUsers() != null) {
            for (int i = 0; i < qhPublicInfoDetail.getPraiseUsers().size(); i++) {
                QHFriendsUser qhFriendsUser = qhPublicInfoDetail.getPraiseUsers().get(i);
                CompoundImage comImg = new CompoundImage(qhFriendsUser.getAvatarUrl(), qhFriendsUser.getAvatarUrl());
                CompoundImage.TextImage textImage = new CompoundImage.TextImage(comImg, null, null);
                imageList.add(textImage);
            }
            ImageAvatorGalleryAdapter adapter = (ImageAvatorGalleryAdapter) recyclerView.getAdapter();
            adapter.setDataItems(imageList);
        }
        recyclerView.setTag(qhPublicInfoDetail.getPraiseUsers());

//        int scap = ScreenUtils.dpToPxInt(mActivity, 3);
//        int padding = ScreenUtils.getDimenPx(mActivity, R.dimen.scenic_details_comment_image_padding);
//        int itemHeight = ((ScreenUtils.getScreenWidth(mActivity) - scap * 2) - padding * 2) / 3;
//        int rowCount = (imageList.size() + 2) / 3;
//        int height = itemHeight * rowCount + scap * (rowCount - 1) + padding * 2;
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
//        recyclerView.setLayoutParams(lp);
//        recyclerView.setPadding(padding, padding, padding, padding);
    }

    private void bindContentImageRecyclerView(RecyclerView recyclerView, QHPublicInfoDetail qhPublicInfoDetail) {
        if (qhPublicInfoDetail.getImages() == null || qhPublicInfoDetail.getImages().isEmpty()) {
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
        for (int i = 0; i < qhPublicInfoDetail.getImages().size(); i++) {
            QHFriendsImage item = qhPublicInfoDetail.getImages().get(i);
            CompoundImage comImg = new CompoundImage(item.getImgPath(), item.getImgPath());
            CompoundImage.TextImage textImage = new CompoundImage.TextImage(comImg, null, null);
            imageList.add(textImage);
        }

        ImageGalleryAdapter adapter = (ImageGalleryAdapter) recyclerView.getAdapter();
        if (qhPublicInfoDetail.getImages() != null)
            adapter.setDataItems(imageList);
        recyclerView.setTag(qhPublicInfoDetail.getImages());

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

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friends_circle_description_item, parent, false);
        return new FriendsCircleDescriptionItem(view);
    }

    @Override
    public void onBinderItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        FriendsCircleDescriptionItem item = (FriendsCircleDescriptionItem) holder;
        QHFriendsComment qhFriendsComment = mDataItems.get(position);
        //type =0 评论    1  回复
        if (qhFriendsComment.getType() == 0 && !TextUtils.isEmpty(qhFriendsComment.getCommentText())) {
            item.content.setText(qhFriendsComment.getCommentText());
        } else {
            if (qhFriendsComment.getToUser() != null && qhFriendsComment.getFromUser() != null) {
//                String conten = qhFriendsComment.getFromUser().getNickname() + " 回复 " + qhFriendsComment.getToUser().getNickname() +" " + qhFriendsComment.getCommentText();
                String conten ="回复: " + qhFriendsComment.getToUser().getNickname() +" " + qhFriendsComment.getCommentText();
                SparseIntArray sparseIntArray = new SparseIntArray(2);
//                sparseIntArray.put(0, qhFriendsComment.getFromUser().getNickname().length());
                sparseIntArray.put(conten.indexOf(qhFriendsComment.getToUser().getNickname()), conten.indexOf(qhFriendsComment.getToUser().getNickname()) + qhFriendsComment.getToUser().getNickname().length());
                AppUtils.setAreaTextColor(item.content, sparseIntArray, conten, mActivity);
            }
        }

        if (qhFriendsComment.getFromUser() != null) {
            if (!TextUtils.isEmpty(qhFriendsComment.getFromUser().getNickname())) {
                item.name.setText(qhFriendsComment.getFromUser().getNickname());
            }
            ImageUtil.DisplayImage(qhFriendsComment.getFromUser().getAvatarUrl(), item.mRoundedImageView,
                    R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
        }
        if (!TextUtils.isEmpty(qhFriendsComment.getCreateTime())) {
            item.time.setText(TimeManager.getTime(qhFriendsComment.getCreateTime()));
        }
        item.setTag(qhFriendsComment);
        attachClickListener(item, item.mainView, position);
        attachClickListener(item, item.mRoundedImageView, position);
        if (qhFriendsComment.getFromUser().getUserId().equals(AppUtils.getQHUser(mActivity).id + "")) {
            item.mainView.setLongClickable(true);
            item.mainView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final QHFriendsComment qhFriendsComment = (QHFriendsComment) v.getTag();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage(R.string.confirm_delete_item);
                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                          //  deleteComment(qhFriendsComment);
                            if (mOnItemSubLongClickListener != null)
                                //这里-1是因为有头
                            mOnItemSubLongClickListener.onItemLongClick(position -1 ,qhFriendsComment.getCommentId());
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    return false;
                }
            });
        } else
            item.mainView.setLongClickable(false);
    }

    public class FriendsCircleDescriptionHeader extends RecyclerView.ViewHolder {
        private RoundedImageView mRoundedImageView;
        private TextView name, time, content, address, vote, hide_vote, hide_argument, argument_count;
        private RecyclerView contentRecycleView, avatorRecycleView;
        private View address_main_layout;
        private ImageView deleteImageView;

        public FriendsCircleDescriptionHeader(View itemView) {
            super(itemView);
            mRoundedImageView = (RoundedImageView) itemView.findViewById(R.id.friends_circle_recycle_item_avator);
            name = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_name);
            time = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_time);
            content = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_content);
            address = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_location);
            address_main_layout = itemView.findViewById(R.id.friends_circle_recycle_item_address_mainlayout);
            hide_vote = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_vote);
            hide_argument = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_argument);
            argument_count = (TextView) itemView.findViewById(R.id.friends_circle_description_header_argument_count_textview);
            vote = (TextView) itemView.findViewById(R.id.friends_circle_description_header_vote_textview);
            contentRecycleView = (RecyclerView) itemView.findViewById(R.id.friends_circle_recycle_item_recycleview);
            avatorRecycleView = (RecyclerView) itemView.findViewById(R.id.friends_circle_description_header_vote_recycleview);
            deleteImageView = (ImageView) itemView.findViewById(R.id.friends_circle_recycle_item_delete);
        }

        public TextView getArgument_count() {
            return argument_count;
        }

        public TextView getVote() {
            return vote;
        }

        public RecyclerView getAvatorRecycleView() {
            return avatorRecycleView;
        }

    }

    private class FriendsCircleDescriptionItem extends RecyclerView.ViewHolder {
        private RoundedImageView mRoundedImageView;
        private TextView name, time, content;
        private View mainView;

        public FriendsCircleDescriptionItem(View itemView) {
            super(itemView);
            mRoundedImageView = (RoundedImageView) itemView.findViewById(R.id.friends_circle_recycle_item_avator);
            name = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_name);
            time = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_time);
            content = (TextView) itemView.findViewById(R.id.friends_circle_recycle_item_content);
            mainView = itemView.findViewById(R.id.friends_circle_recycle_item_main_layout);
        }

        public void setTag(QHFriendsComment qhFriendsComment) {
            mRoundedImageView.setTag(qhFriendsComment);
            mainView.setTag(qhFriendsComment);
        }
    }

    private void initTrendRecycleView(Context context, RecyclerView recyclerView) {
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

    private void initVoteRecycleView(Context context, RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
        recyclerView.setLayoutManager(gridLayoutManager);
        int scap = ScreenUtils.dpToPxInt(context, 1);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(scap, scap);
        dividerItemDecoration.initWithRecyclerView(recyclerView);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ImageAvatorGalleryAdapter adapter = new ImageAvatorGalleryAdapter(mActivity);
//        int screenPadding = ScreenUtils.getDimenPx(mActivity, R.dimen.scenic_details_comment_image_padding);
//        adapter.setScreenPadding(screenPadding);
        recyclerView.setAdapter(adapter);
    }

    public void setOnItemSubLongClickListener(OnItemSubLongClickListener onItemSubLongClickListener) {
        mOnItemSubLongClickListener = onItemSubLongClickListener;
    }

    private OnItemSubLongClickListener mOnItemSubLongClickListener;

    public interface OnItemSubLongClickListener {
        void onItemLongClick(int position,int id);
    }
}
