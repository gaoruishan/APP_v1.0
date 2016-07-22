package com.cmcc.hyapps.andyou.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.AppBaseAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.model.Scenic;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.roundimageview.RoundedImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class QHScenicDetailPhotos extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qhscenic_detail_pics);
        GridView gridView=(GridView) this.findViewById(R.id.qhscenic_detail_pics_gv);
        ActionBar  mActionBar = (ActionBar) this.findViewById(R.id.action_bar);
        mActionBar.setBackgroundResource(R.color.title_bg);
        mActionBar.getTitleView().setText("图集");
        mActionBar.getLeftView().setImageResource(R.drawable.return_back);
        mActionBar.getLeftView().setOnClickListener(this);
        final QHScenic mScenic = getIntent().getParcelableExtra(Const.QH_SECNIC);
        final ArrayList<String> urllist=new ArrayList<String>();
        for (int i = 0; i <mScenic.photos.size() ; i++) {
            if (mScenic.photos.get(i)!=null){
                urllist.add(mScenic.photos.get(i).image_url);
            }
        }
        if (mScenic.photos!=null){
            gridView.setAdapter(new SortGridViewAdapter(mScenic.photos,this));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(QHScenicDetailPhotos.this,NewPhotoPreviewActivity.class);
//                    intent.putExtra(Const.EXTRA_IMAGE_DATA,  urllist);
                    intent.putStringArrayListExtra(Const.EXTRA_IMAGE_DATA, urllist);
                    intent.putExtra(Const.EXTRA_IMAGE_PREVIEW_START_INDEX, position);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left:
                this.finish();
                break;
        }
    }


    private class SortGridViewAdapter extends AppBaseAdapter<QHScenic.QHPhotos>{

        public SortGridViewAdapter(List<QHScenic.QHPhotos> list, Context context) {
            super(list, context);
        }
        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
             Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_pic_iv, parent,
                        false);
                holder = new Holder();
                ViewUtils.inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final QHScenic.QHPhotos bean = list.get(position);
            if (bean.image_url!=null){
//                holder.iv.setImageUrl(bean.image_url,
//                        RequestManager.getInstance().getImageLoader());
                ImageUtil.DisplayImage(bean.image_url, holder.iv);
            }
            return convertView;
        }
    }

    private static class Holder {
        @ViewInject(R.id.iv_pic)
        private RoundedImageView iv;
        @ViewInject(R.id.tv_pic)
        private TextView tv;
    }
}
