
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
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.PhotoPreviewActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.CommentImage;
import com.cmcc.hyapps.andyou.model.CompoundImage;
import com.cmcc.hyapps.andyou.model.CompoundImage.TextImage;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class GuiderPublishedCommentListAdapter extends AppendableAdapter<Comment> {
    private final String TAG = "GuiderRaidersCommentListAdapter";
    private Activity mActivity;
    private List<Comment> items;
    //用来判断是自己的发布还是被人的发布，来确定能否出现长按删除
    private String user_id;

    public GuiderPublishedCommentListAdapter(Activity activity, String user_id) {
        mActivity = activity;
        this.user_id = user_id;
    }

    static class CommentViewHolder extends ViewHolder {
        TextView name;
        TextView date;
        TextView content;
        RecyclerView imagesRecyclerView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.comment_author_name);
            date = (TextView) itemView.findViewById(R.id.comment_date);
            content = (TextView) itemView.findViewById(R.id.comment_content);
            imagesRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerview);
        }

        public void setDataTag(Comment comment) {
            itemView.setTag(comment);
            name.setTag(comment);
            name.setTag(comment);
            content.setTag(comment);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext().getApplicationContext();
        View v = LayoutInflater.from(context).inflate(R.layout.publish_item_comment, parent, false);
        CommentViewHolder holder = new CommentViewHolder(v);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        holder.imagesRecyclerView.setLayoutManager(layoutManager);
        int scap = ScreenUtils.dpToPxInt(context, 3);
        DividerItemDecoration decor = new DividerItemDecoration(scap, scap);
        decor.initWithRecyclerView(holder.imagesRecyclerView);
        holder.imagesRecyclerView.addItemDecoration(decor);
        holder.imagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ImageGalleryAdapter adapter = new ImageGalleryAdapter(mActivity);
        int screenPadding = ScreenUtils.getDimenPx(mActivity, R.dimen.scenic_details_comment_image_padding);
        adapter.setScreenPadding(screenPadding);
        holder.imagesRecyclerView.setAdapter(adapter);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (mDataItems.size() <= position) {
            Log.e(TAG, "[onBinderItemViewHolder] position out of bound");
            return;
        }
        final Comment comment = mDataItems.get(position);

        if (comment == null || comment.user == null) {
            Log.e(TAG, "[onBinderItemViewHolder] comment: " + comment);
            return;
        }
        final CommentViewHolder commentHolder = (CommentViewHolder) holder;
        commentHolder.setDataTag(comment);


        commentHolder.name.setText(comment.obj_name);
        commentHolder.content.setText(comment.content);
        commentHolder.date.setText(comment.created);

        bindCommentImageRecyclerView(commentHolder.imagesRecyclerView, comment);
        commentHolder.itemView.requestLayout();
        if (TextUtils.isEmpty(user_id)) {
            commentHolder.itemView.setLongClickable(true);
            commentHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final Comment item = (Comment) v.getTag();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage(R.string.confirm_delete_item);
                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
//                        String url = ServerAPI.User.buildItemDetailUrl(item.id + "");
                            String url = ServerAPI.BASE_URL + "deletecomments/" + item.id + "/";
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.get(url, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    ToastUtils.show(mActivity, R.string.delete_success);
                                    mDataItems.remove(position);
                                    notifyDataSetChanged();
                                    dialogInterface.dismiss();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    ToastUtils.show(mActivity, R.string.delete_error);
                                }
                            });
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        } else
            commentHolder.itemView.setLongClickable(false);


        attachClickListener(commentHolder, commentHolder.itemView, position);
    }

    private void bindCommentImageRecyclerView(RecyclerView tWayView, Comment comment) {
        if (comment.comment_images == null || comment.comment_images.isEmpty()) {
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
                        if (null == textImage.image.largeImage || null == textImage.image.smallImage)
                            continue;
                        image.imagePath = Uri.parse(textImage.image.largeImage);
                        image.thumbnailPath = Uri.parse(textImage.image.smallImage);
                        imageList.add(image);
                    }
                }
                Intent intent = new Intent(mActivity, PhotoPreviewActivity.class);
                intent.putExtra(Const.EXTRA_IMAGE_DATA, imageList);
                intent.putExtra(Const.EXTRA_IMAGE_PREVIEW_START_INDEX, position);
                mActivity.startActivity(intent);
            }
        });

        List<TextImage> imageList = new ArrayList<TextImage>();
        for (int i = 0; i < comment.comment_images.size(); i++) {
            CommentImage img = comment.comment_images.get(i);
            CompoundImage comImg = new CompoundImage(img.image_url, img.image_url);

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
        int height = itemHeight * rowCount + scap * (rowCount - 1) + padding * 2;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
        tWayView.setLayoutParams(lp);
        tWayView.setPadding(padding, padding, padding, padding);
    }
}
