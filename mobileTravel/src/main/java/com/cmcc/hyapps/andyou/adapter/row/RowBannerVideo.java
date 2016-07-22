/**
 *
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.example.rtspdemo.MainActivity;
import com.example.rtspdemo.PlayerActivity;
import com.kj.guradc.VideoActivity;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.NightLiveActivity;
import com.cmcc.hyapps.andyou.adapter.BannerPagerAdapter.IActionCallback;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.model.Video;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.lau.vlcdemo.VideoPlayerActivity;

import org.videolan.vlc.LibVLC;
import org.videolan.vlc.LibVlcException;

/**
 * @author kuloud
 */
public class RowBannerVideo {
    public static View getView(final Context context, final QHScenic.QHVideo item, View convertView,
            ViewGroup container,
            final IActionCallback<QHScenic.QHVideo> callback) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_video_banner,
                    container, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (item == null) {
            holder.imageView.setImageResource(R.drawable.bg_banner_hint);
        } else {
            if (item.image_url != null) {
//                holder.imageView.setDefaultImageResId(R.drawable.bg_banner_hint)
//                        .setErrorImageResId(R.drawable.bg_banner_hint);
//                holder.imageView.setImageUrl(item.image_url, RequestManager.getInstance()
//                        .getImageLoader());
                ImageUtil.DisplayImage(item.image_url, holder.imageView, R.drawable.bg_banner_hint, R.drawable.bg_banner_hint);
                holder.imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onValidClick(View v) {
//                        if (callback != null) {
//                            callback.doAction(bannerSilde);
//                        }
                        Intent intent = null;
                        switch (item.day_or_night){
                            case 0:
//                                intent = new Intent(context,VideoActivity.class);
//                                intent.putExtra("url",item.video_day);
//                                context.startActivity(intent);

                                /*try {
                                    LibVLC.useIOMX(context);
                                    LibVLC mLibVLC = LibVLC.getInstance();
                                } catch (LibVlcException e) {
                                    e.printStackTrace();
                                }*/

                                intent = new Intent(context, PlayerActivity.class);
                                intent.putExtra("url",item.video_day);
                                context.startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(context,PlayerActivity.class);
                                intent.putExtra("url",item.video_night);
                                context.startActivity(intent);
                                break;
                            default:
//                                intent = new Intent(context,VideoActivity.class);
//                                intent.putExtra("url",item.video_day);
//                                context.startActivity(intent);
                                /*try {
                                    LibVLC.useIOMX(context);
                                    LibVLC mLibVLC = LibVLC.getInstance();
                                } catch (LibVlcException e) {
                                    e.printStackTrace();
                                }*/

                                intent = new Intent(context, PlayerActivity.class);
                                intent.putExtra("url",item.video_day);
                                context.startActivity(intent);

                        }
                    }
                });
            }
            holder.titleText.setText(item.title);
        }
        return convertView;
    }

    private static class ViewHolder {
        NetworkImageView imageView;
        TextView titleText;

        public ViewHolder(View itemView) {
            imageView = (NetworkImageView) itemView.findViewById(R.id.banner_image);
            titleText = (TextView) itemView.findViewById(R.id.banner_title);
        }
    }
}
