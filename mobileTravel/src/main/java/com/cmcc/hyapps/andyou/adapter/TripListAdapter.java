
package com.cmcc.hyapps.andyou.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.TripDetailEditActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.ResultCodeResponse;
import com.cmcc.hyapps.andyou.model.Trip;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.TimeUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;

import java.util.List;

public class TripListAdapter extends AppendableAdapter<Trip> {
    private static final String DATE_TIME_FORMAT = "MM-dd kk:mm";
    private boolean mEditMode;
    private boolean mEditable;

    public TripListAdapter(boolean editable) {
        mEditable = editable;
    }

    public interface IActionCallback {
        public void onRemove(int position);

        public void onItemClicked(int position);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return TripAdapterHelper.onCreateItemViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        TripAdapterHelper.onBinderItemViewHolder(holder, position, mDataItems);
        if (mEditable) {
            TripViewHolder viewHolder = (TripViewHolder) holder;
            final Context context = holder.itemView.getContext();
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onValidClick(View v) {
                    Intent intent = new Intent(context, TripDetailEditActivity.class);
                    intent.putExtra(Const.EXTRA_ID, (int) getItemId(position));
                    context.startActivity(intent);
                }
            });
            if (mEditMode) {
                viewHolder.delete.setVisibility(View.VISIBLE);
                viewHolder.delete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onValidClick(View v) {
                        if (mDataItems != null && mDataItems.size() > position) {
                            RequestManager.getInstance().sendGsonRequest(
                                    ServerAPI.User.buildTripDeleteUrl(),
                                    ResultCodeResponse.class,
                                    new Response.Listener<ResultCodeResponse>() {
                                        @Override
                                        public void onResponse(ResultCodeResponse result) {
                                            mDataItems.remove(position);
                                            notifyDataSetChanged();
                                            ToastUtils.show(context, "已删除该条数据");
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e(error, "onErrorResponse");
                                            ToastUtils.show(context, "删除失败");
                                        }
                                    }, false,
                                    ServerAPI.User.buildTripDeleteParams(getItemId(position)),
                                    "deleteTrip");
                        }
                    }
                });
            } else {
                viewHolder.delete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataItems == null ? 0 : mDataItems.size();
    }

    @Override
    public long getItemId(int position) {
        if (mDataItems != null && mDataItems.size() > position) {
            Trip trip = mDataItems.get(position);
            if (trip != null) {
                return trip.id;
            }
        }
        return super.getItemId(position);
    }

    public boolean isEditMode() {
        return mEditMode;
    }

    public void setEditMode(boolean editMode) {
        this.mEditMode = editMode;
        notifyDataSetChanged();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView coverImage;
        TextView viewsCount;
        TextView commentCount;
        ImageView avatarImage;
        TextView title;
        TextView userName;
        TextView createTime;
        View delete;

        public TripViewHolder(View itemView) {
            super(itemView);
            coverImage = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            viewsCount = (TextView) itemView.findViewById(R.id.tv_views_count);
            commentCount = (TextView) itemView.findViewById(R.id.tv_comment_count);
            avatarImage = (ImageView) itemView.findViewById(R.id.iv_avatar);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            userName = (TextView) itemView.findViewById(R.id.tv_user_name);
            createTime = (TextView) itemView.findViewById(R.id.tv_create_time);
            delete = (TextView) itemView.findViewById(R.id.tv_remove);
        }
    }

    // TODO
    public static class TripAdapterHelper {

        public static TripViewHolder onCreateItemViewHolder(
                ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_headline,
                    parent, false);
            return new TripViewHolder(v);
        }

        public static void onBinderItemViewHolder(
                android.support.v7.widget.RecyclerView.ViewHolder holder,
                final int position, final List<Trip> dataItems) {
            TripViewHolder viewHolder = (TripViewHolder) holder;
            final Trip trip = dataItems.get(position);
            if (trip == null) {
                return;
            }
            holder.itemView.setTag(trip);

            if (trip.coverImage != null) {
//                viewHolder.coverImage.setDefaultImageResId(R.drawable.bg_banner_hint)
//                        .setErrorImageResId(R.drawable.bg_banner_hint);
//                viewHolder.coverImage.setImageUrl(trip.coverImage, RequestManager
//                        .getInstance().getImageLoader());

                ImageUtil.DisplayImage(trip.coverImage, viewHolder.coverImage,
                        R.drawable.bg_banner_hint, R.drawable.bg_banner_hint);
            }
            viewHolder.viewsCount.setText(String.valueOf(trip.viewsCount));
            viewHolder.commentCount.setText(String.valueOf(trip.commentCount));
            if (trip.author != null) {
                viewHolder.userName.setText(trip.author.name);
                if (trip.author.avatarUrl != null) {
//                    RequestManager
//                            .getInstance()
//                            .getImageLoader()
//                            .get(trip.author.avatarUrl,
//                                    ImageLoader.getImageListener(viewHolder.avatarImage,
//                                            R.drawable.bg_image_hint,
//                                            R.drawable.bg_image_hint));

                    ImageUtil.DisplayImage(trip.author.avatarUrl, viewHolder.avatarImage,
                            R.drawable.bg_image_hint,
                            R.drawable.bg_image_hint);
                }
            }
            viewHolder.title.setText(trip.title);
            String time = TimeUtils.formatTime(trip.createTime, DATE_TIME_FORMAT);
            viewHolder.createTime.setText(time);
        }
    }
}
