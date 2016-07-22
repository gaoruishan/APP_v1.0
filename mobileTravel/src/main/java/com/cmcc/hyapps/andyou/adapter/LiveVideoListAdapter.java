
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.SecnicActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHScenic.QHVideo;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.example.rtspdemo.PlayerActivity;
import com.kuloud.android.widget.recyclerview.AppendableAdapter;
import com.umeng.analytics.MobclickAgent;

import org.videolan.vlc.LibVLC;

import java.util.List;

public class LiveVideoListAdapter extends AppendableAdapter<QHVideo> {
    private Activity mContext;
    private LibVLC mLibVLC;

    public LiveVideoListAdapter(Activity context) {
        this.mContext = context;
    }

    public LiveVideoListAdapter(Activity context, List<QHVideo> items) {
        this(context);
        this.mDataItems = items;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.item_live_video_view, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoViewHolder viewHolder = (VideoViewHolder) holder;
        final QHVideo item = mDataItems.get(position);
        holder.itemView.setTag(item);
        if (!TextUtils.isEmpty(item.scenic_name))
        ((VideoViewHolder) holder).videoName.setText(item.scenic_name);
        if (!TextUtils.isEmpty(item.title))
        ((VideoViewHolder) holder).title.setText(item.title);
        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                MobclickAgent.onEvent(mContext, MobConst.ID_HOME_PPT1);
                Intent intent = new Intent(mContext, SecnicActivity.class);
            //    int mId = Integer.parseInt("1");
                int mId = item.scenic;
                //  intent.putExtra(Const.QH_SECNIC, mScenic);
                intent.putExtra(Const.QH_SECNIC_ID, mId);
                mContext.startActivity(intent);
            }
        });
        ((VideoViewHolder) holder).playImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View view) {
                MobclickAgent.onEvent(mContext, MobConst.ID_INDEX_LIVE_ITEM);
             //   final QHVideo item = (QHVideo) view.getTag();

                // 0：使用白天视频 1：使用夜间视频
                Intent intent = null;
                switch (item.day_or_night){
                    case 0:
//                        intent = new Intent(mContext,VideoPlayerActivity.class);
//                        intent.putExtra("url",item.video_day);
//                        mContext.startActivity(intent);

                        /*try {
                            LibVLC.useIOMX(mContext);
                            LibVLC mLibVLC = LibVLC.getInstance();
                        } catch (LibVlcException e) {
                            e.printStackTrace();
                        }*/

                        intent = new Intent(mContext, PlayerActivity.class);
                        intent.putExtra("url",item.video_day);
                        mContext.startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(mContext,PlayerActivity.class);
                        intent.putExtra("url",item.video_night);
                        mContext.startActivity(intent);
                        break;
                    default:
                       /* try {
                            LibVLC.useIOMX(mContext.getApplicationContext());
                            mLibVLC = LibVLC.getInstance();
                        } catch (LibVlcException e) {
                            e.printStackTrace();
                        }*/

                        intent = new Intent(mContext, PlayerActivity.class);
                        intent.putExtra("url",item.video_day);
                        mContext.startActivity(intent);

                }
                //TODO 修改以下代码，通过返回的day_or_night判断要播放哪一个信息，结果如上代码
//                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setPositiveButton("白天",new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(mContext,VideoActivity.class);
//                        intent.putExtra("url",item.video_day);
//                        mContext.startActivity(intent);
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.setNegativeButton("夜间",new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(mContext,NightLiveActivity.class);
//                        intent.putExtra("url",item.video_night);
//                        mContext.startActivity(intent);
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.show();
            }
        });

        if (item.image_url != null) {
            Log.d("position"+position+", thumbnail: " + item.image_url);
//            viewHolder.thumbnail.setDefaultImageResId(R.drawable.recommand_bg)
//                    .setErrorImageResId(R.drawable.recommand_bg);
//            viewHolder.thumbnail.setImageUrl(item.image_url, RequestManager.getInstance()
//                    .getImageLoader());

            ImageUtil.DisplayImage(item.image_url, viewHolder.thumbnail);
        }

    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView thumbnail;
        TextView playTimes,title;
        TextView duration;
        TextView videoName;
        ImageView playImage;
        public VideoViewHolder(View itemView) {
            super(itemView);
            thumbnail = (NetworkImageView) itemView.findViewById(R.id.video_thumbnail);
            playTimes = (TextView) itemView.findViewById(R.id.play_times);
            title = (TextView) itemView.findViewById(R.id.video_description);
            duration = (TextView) itemView.findViewById(R.id.duration);
            videoName = (TextView) itemView.findViewById(R.id.video_name);
            playImage = (ImageView)itemView.findViewById(R.id.video_play);
        }
    }
}
