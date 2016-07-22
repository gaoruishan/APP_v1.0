
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.NavigationDetailActivity;
import com.cmcc.hyapps.andyou.activity.StrategyDetailActivity;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.List;

public class PublishRaidersListAdapter extends AppendableAdapter<QHStrategy> {
    private Activity mContext;
    private String user_id;
    public PublishRaidersListAdapter(Activity context,String user_id) {
        this.mContext = context;
        this.user_id = user_id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_travel, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        VideoViewHolder viewHolder = (VideoViewHolder) holder;
        final QHStrategy item = mDataItems.get(position);
        viewHolder.itemView.setTag(item);
        viewHolder.netImage.setTag(item);

        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View v) {

                QHStrategy item = (QHStrategy) v.getTag();
                Bundle bundle = new Bundle();
                bundle.putParcelable("guide", item);
                bundle.putInt("id", item.id);
                Intent intent = new Intent();
                intent.putExtra("guide", bundle);
//                intent.putExtra(Const.SPECIAL_DETAIL_DATA, item);
                intent.setClass(mContext, StrategyDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
        if (TextUtils.isEmpty(user_id)){
            viewHolder.itemView.setLongClickable(true);
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final QHStrategy item = (QHStrategy) v.getTag();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(R.string.confirm_delete_item);
                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            //     String url = ServerAPI.Guide.buildItemDetailUrl(item.id + "");
                            String url = ServerAPI.Guide.buildDeleteItemDetailUrl(item.id + "");
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.get(url, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    ToastUtils.show(mContext, R.string.delete_success);
                                    mDataItems.remove(position);
                                    notifyDataSetChanged();
                                    dialogInterface.dismiss();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    ToastUtils.show(mContext, R.string.delete_error);
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
        }else
            viewHolder.itemView.setLongClickable(false);

        if (null!=item.guide_info&&item.guide_info.size()>0&&null!=item.guide_info.get(0)
                &&!TextUtils.isEmpty(item.guide_info.get(0).image_url)){
//            RequestManager.getInstance().getImageLoader().get(
//                    item.guide_info.get(0).image_url,
//                    ImageLoader.getImageListener(
//                            viewHolder.netImage, R.drawable.recommand_bg,
//                            R.drawable.bg_image_error));
            ImageUtil.DisplayImage(item.guide_info.get(0).image_url, viewHolder.netImage, R.drawable.recommand_bg,
                    R.drawable.bg_image_error);
        }else
            viewHolder.netImage.setImageResource(R.drawable.recommand_bg);

        if (!TextUtils.isEmpty(item.user.user_info.avatar_url))
        {
//            RequestManager.getInstance().getImageLoader().get(
//                    item.user.user_info.avatar_url,
//                    ImageLoader.getImageListener(
//                            viewHolder.iv_me_avata, R.drawable.bg_image_hint,
//                            R.drawable.bg_image_hint));

            ImageUtil.DisplayImage(item.user.user_info.avatar_url, viewHolder.iv_me_avata, R.drawable.bg_image_hint,
                    R.drawable.bg_image_hint);
        }else {
            viewHolder.iv_me_avata.setImageResource(R.drawable.bg_avata_hint);
        }
        if (!TextUtils.isEmpty(item.title))
            viewHolder.travel_name.setText(item.title);
        if (item.user != null ){
            if (item.user.user_info != null && !TextUtils.isEmpty(item.user.user_info.nickname)){
                viewHolder.user_name.setText(item.user.user_info.nickname);
            }else
                viewHolder.user_name.setText(item.user.username);
        }

        if (!TextUtils.isEmpty(item.start_date))
            viewHolder.travel_time.setText(item.start_date);

    }

    static class VideoViewHolder extends ViewHolder {
        NetworkImageView netImage;
        NetworkImageView iv_me_avata;
        TextView travel_name,user_name,travel_time;
        public VideoViewHolder(View itemView) {
            super(itemView);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_cover_image);
            iv_me_avata = (NetworkImageView) itemView.findViewById(R.id.iv_me_avata);
            travel_name = (TextView) itemView.findViewById(R.id.travel_name);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            travel_time = (TextView) itemView.findViewById(R.id.travel_time);
        }

    }
}
