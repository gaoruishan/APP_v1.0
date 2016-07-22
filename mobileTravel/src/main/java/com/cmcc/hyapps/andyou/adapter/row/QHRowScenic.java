/**
 * 
 */

package com.cmcc.hyapps.andyou.adapter.row;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.SecnicActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;


/**
 * @author kuloud
 */
public class QHRowScenic {
    public static RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

        //home_fragment_item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover_list,
                parent, false);
        return new ViewHolder(v);
    }

    public static void onBindViewHolder(final Context context,RecyclerView.ViewHolder holder, int position,
            QHScenic scenic) {

        final ViewHolder commentHolder = (ViewHolder) holder;
        commentHolder.setDataTag(scenic);

        if (!TextUtils.isEmpty(scenic.image_url)) {
//            RequestManager.getInstance().getImageLoader().get(scenic.image_url,
//                    ImageLoader.getImageListener(commentHolder.netImage,
//                            R.drawable.recommand_bg, R.drawable.recommand_bg));
            ImageUtil.DisplayImage(scenic.image_url, commentHolder.netImage);
        }
        if (!TextUtils.isEmpty(scenic.name))
        commentHolder.item_name.setText(scenic.name);
        if (!TextUtils.isEmpty(scenic.intro_text))
        commentHolder.item_intro.setText(scenic.intro_text.trim());
     //   LatLng start = new LatLng(scenic.latitude, scenic.longitude);
     //   LatLng end = new LatLng(ConstTools.myCurrentLoacation.latitude, ConstTools.myCurrentLoacation.longitude);

        commentHolder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                QHScenic mScenic = (QHScenic) v.getTag();
//                switch (v.getId()) {
//                    case R.id.iv_cover_image:
                        Intent intent = new Intent(context, SecnicActivity.class);
                        int mId = mScenic.id;
                        intent.putExtra(Const.QH_SECNIC,mScenic);
//                bundle.putInt("id",item.id);
                        intent.putExtra(Const.QH_SECNIC_ID,mId);
                        context.startActivity(intent);
//                        break;
//                    default:
//                        break;
//                }
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

        public void setDataTag(QHScenic scenic) {
            itemView.setTag(scenic);
            netImage.setTag(scenic);
        }
    }
}
