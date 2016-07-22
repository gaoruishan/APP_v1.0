
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.volley.toolbox.ImageLoader;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemClickListener;
import com.umeng.analytics.MobclickAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.PhotoPreviewActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.CommentImage;
import com.cmcc.hyapps.andyou.model.CompoundImage;
import com.cmcc.hyapps.andyou.model.CompoundImage.TextImage;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.util.LocationUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class GuiderRaidersCommentListAdapter extends AppendableAdapter< Comment> {
    private final String TAG = "GuiderRaidersCommentListAdapter";
    private Activity mActivity;
    private List<Comment> items;

    public GuiderRaidersCommentListAdapter(Activity activity) {
        mActivity = activity;
    }
    public GuiderRaidersCommentListAdapter(Activity activity, List<Comment> items) {
        this(activity);
        setDataItems(items);
    }
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView authorAvatar;
        TextView author;
        TextView content;
        TextView commentCount;
        TextView voteCount;
        TextView comment_date;
//        RatingBar rating;
//        ImageView share;
        RecyclerView imagesRecyclerView;
        TextView navigation;

        public CommentViewHolder(View itemView) {
            super(itemView);
            authorAvatar = (ImageView) itemView.findViewById(R.id.author_avatar);
            author = (TextView) itemView.findViewById(R.id.comment_author_name);
            content = (TextView) itemView.findViewById(R.id.comment_content);
//            rating = (RatingBar) itemView.findViewById(R.id.comment_rating);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);
            voteCount = (TextView) itemView.findViewById(R.id.comment_vote_count);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
//            share = (ImageView) itemView.findViewById(R.id.comment_share);
            imagesRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerview);
            navigation = (TextView) itemView.findViewById(R.id.comment_location);
        }

        public void setDataTag(Comment comment) {
            itemView.setTag(comment);
            commentCount.setTag(comment);
            voteCount.setTag(comment);
            navigation.setTag(comment);
//            share.setTag(comment);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext().getApplicationContext();
        View v = LayoutInflater.from(context).inflate(R.layout.item_scenic_comment, parent, false);
        CommentViewHolder holder = new CommentViewHolder(v);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        holder.imagesRecyclerView.setLayoutManager(layoutManager);
        int scap = ScreenUtils.dpToPxInt(context, 3);
        DividerItemDecoration decor = new DividerItemDecoration(scap, scap);
        decor.initWithRecyclerView(holder.imagesRecyclerView);
        holder.imagesRecyclerView.addItemDecoration(decor);
        holder.imagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ImageGalleryAdapter adapter = new ImageGalleryAdapter(mActivity);
        int screenPadding = ScreenUtils.getDimenPx(mActivity,R.dimen.scenic_details_comment_image_padding);
        adapter.setScreenPadding(screenPadding);
        holder.imagesRecyclerView.setAdapter(adapter);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDataItems.size() <= position) {
            Log.e(TAG, "[onBinderItemViewHolder] position out of bound");
            return;
        }
        final Comment comment = mDataItems.get(position);

        if (comment == null) {
            Log.e(TAG, "[onBinderItemViewHolder] comment: " + comment);
            return;
        }
        final CommentViewHolder commentHolder = (CommentViewHolder) holder;
        commentHolder.setDataTag(comment);
        if (comment.user != null && comment.user.user_info != null && !TextUtils.isEmpty(comment.user.user_info.avatar_url)) {
            ImageUtil.DisplayImage(comment.user.user_info.avatar_url, commentHolder.authorAvatar, R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
//            RequestManager .getInstance() .getImageLoader().get(comment.user.user_info.avatar_url, ImageLoader.getImageListener(commentHolder.authorAvatar, R.drawable.bg_avata_hint,R.drawable.bg_avata_hint));
        }
        if (comment.user != null && comment.user.user_info != null && !TextUtils.isEmpty(comment.user.user_info.nickname)){
            commentHolder.author.setText(comment.user.user_info.nickname);
        }
        commentHolder.content.setText(comment.content);
        commentHolder.comment_date.setText(comment.created);
        commentHolder.commentCount.setText(String.valueOf(comment.commentCount));
        commentHolder.voteCount.setText(String.valueOf(comment.voteCount));
        //是否打开gps,从而判断是否显示
        LatLonPoint latLonPoint = new LatLonPoint(comment.latitude,comment.longitude);
        LocationUtil.getInstance(mActivity).getAddressName(mActivity,latLonPoint,new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if(rCode == 0){
                    if(result != null&&result.getRegeocodeAddress() != null
                            &&result.getRegeocodeAddress().getFormatAddress()!=null){
                        String addressName = result.getRegeocodeAddress().getFormatAddress();
                        commentHolder.navigation.setText(addressName);
                    }
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

        if (1==comment.voted) {
            commentHolder.voteCount.getCompoundDrawables()[0].setLevel(2);
            commentHolder.voteCount.setEnabled(true);
            commentHolder.voteCount.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ToastUtils.AvoidRepeatToastShow(mActivity,R.string.error_already_voted, Toast.LENGTH_SHORT);
                }
            });
        } else {
            commentHolder.voteCount.setEnabled(true);
            commentHolder.voteCount.getCompoundDrawables()[0].setLevel(1);
            attachClickListener(commentHolder, commentHolder.voteCount, position);
        }
//        commentHolder.rating.setRating(comment.rating);

        bindCommentImageRecyclerView(commentHolder.imagesRecyclerView, comment);
        commentHolder.itemView.requestLayout();
        attachClickListener(commentHolder, commentHolder.commentCount, position);
        attachClickListener(commentHolder, commentHolder.itemView, position);
        attachClickListener(commentHolder, commentHolder.itemView, position);
        attachClickListener(commentHolder, commentHolder.navigation, position);
//        attachClickListener(commentHolder, commentHolder.share, position);
    }

    private void bindCommentImageRecyclerView(RecyclerView tWayView, Comment comment) {
        if (comment.comment_images == null || comment.comment_images.isEmpty()) {
            tWayView.setVisibility(View.GONE);
            return;
        }
        tWayView.setVisibility(View.VISIBLE);
        ItemClickSupport clickSupport = ItemClickSupport.addTo(tWayView);///1,ADD
        clickSupport.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                MobclickAgent.onEvent(mActivity, MobConst.ID_INDEX_SCENIC_COMMENT_IMAGE);
                List<TextImage> items = (List<TextImage>) parent.getTag();
                ArrayList<Image> imageList = new ArrayList<Image>();
                for (TextImage textImage : items) {
                    if (textImage != null && textImage.image != null) {
                        Image image = new Image();
                        if(null==textImage.image.largeImage||null==textImage.image.smallImage)
                            continue;
                        image.imagePath = Uri.parse(textImage.image.largeImage);
                        image.thumbnailPath = Uri.parse(textImage.image.smallImage);
                        imageList.add(image);
                    }
                }
                Intent intent = new Intent(mActivity,PhotoPreviewActivity.class);
                intent.putExtra(Const.EXTRA_IMAGE_DATA, imageList);
                intent.putExtra(Const.EXTRA_IMAGE_PREVIEW_START_INDEX, position);
                mActivity.startActivity(intent);
            }
        });

        List<TextImage> imageList = new ArrayList<TextImage>();
        for (int i=0;i<  comment.comment_images.size();i++) {
            CommentImage img = comment.comment_images.get(i);
            CompoundImage comImg = new CompoundImage(img.image_url,img.image_url);

            TextImage textImage = new TextImage(comImg, null, null);
            imageList.add(textImage);
        }

        ImageGalleryAdapter adapter = (ImageGalleryAdapter) tWayView.getAdapter();
        adapter.setDataItems(imageList);
        tWayView.setTag(imageList);

        int scap = ScreenUtils.dpToPxInt(mActivity, 3);
        int padding = ScreenUtils.getDimenPx(mActivity, R.dimen.scenic_details_comment_image_padding);
        int itemHeight = ((ScreenUtils.getScreenWidth(mActivity) - scap * 2) - padding * 2) / 3;
        int rowCount = (imageList.size() + 2) / 3;
        int height = itemHeight * rowCount + scap * (rowCount - 1)+ padding * 2;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,height);
        tWayView.setLayoutParams(lp);
        tWayView.setPadding(padding, padding, padding, padding);
    }
}
