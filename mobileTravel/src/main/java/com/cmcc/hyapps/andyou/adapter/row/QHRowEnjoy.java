package com.cmcc.hyapps.andyou.adapter.row;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.EntertainmentsDetailActivity;
import com.cmcc.hyapps.andyou.model.QHEnjoy;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;

/**
 * Created by bingbing on 2015/10/19.
 */
public class QHRowEnjoy {
    public static RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

        //home_fragment_item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover_list,
                parent, false);
        return new ViewHolder(v);
    }

    public static void onBindViewHolder(final Context context,RecyclerView.ViewHolder holder, int position,
                                        QHEnjoy scenic) {

        final ViewHolder commentHolder = (ViewHolder) holder;
        commentHolder.setDataTag(scenic);
        ImageUtil.DisplayImage(scenic.cover_image, commentHolder.netImage);
        if (!TextUtils.isEmpty(scenic.title))
            commentHolder.item_name.setText(scenic.title);
        if (!TextUtils.isEmpty(scenic.intro_text))
            commentHolder.item_intro.setText(scenic.intro_text.trim());

        commentHolder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                QHEnjoy qhEnjoy = (QHEnjoy) v.getTag();
                Intent intent = new Intent(context, EntertainmentsDetailActivity.class);
                int mId = qhEnjoy.id;
                intent.putExtra("entertainment", mId);
                context.startActivity(intent);
            }
        });
//        attachClickListener(commentHolder, commentHolder.netImage, position);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView netImage;
        TextView item_name, item_intro;

        public ViewHolder(View itemView) {
            super(itemView);
            netImage = (NetworkImageView) itemView.findViewById(R.id.iv_item_discover);
            item_name = (TextView) itemView.findViewById(R.id.item_title);
            item_intro = (TextView) itemView.findViewById(R.id.item_contents);
        }

        public void setDataTag(QHEnjoy qhEnjoy) {
            itemView.setTag(qhEnjoy);
            netImage.setTag(qhEnjoy);
        }
    }
}
