
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.fragment.DiscoverFragment;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.model.Tag;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.WrapLinearLayout;
import com.cmcc.hyapps.andyou.widget.roundimageview.RoundedImageView;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;

public class DiscoverAdapter extends BaseHeaderAdapter<Tag.TagList, QHScenic> {
    private Context mContext;
    public static final int COLOR_WHITE       = 0xFFFFFFFF;
    public static final int COLOR_CHANGE       = 0xFF0FD5F2;
    private final String TAG = "DiscoverAdapter";
    boolean b=false;
    private BannerPagerAdapter.IActionCallback<Tag> mActionCallback;
    public DiscoverAdapter(Context context) {
        this.mContext = context;
    }
    private BitmapUtils bitmapUtils;
    private DisplayImageOptions options;
    private DiscoverFragment intstance;
    public DiscoverAdapter(Activity context, List<QHScenic> items) {
        this(context);
        this.mDataItems = items;

    }

    public DiscoverAdapter(Context activity, BannerPagerAdapter.IActionCallback<Tag> actionCallback) {
        this(activity);
        mActionCallback = actionCallback;
        bitmapUtils =new BitmapUtils(activity);
        intstance = DiscoverFragment.getIntstance();
        //显示图片的配置
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.recommand_bg)
                .showImageOnFail(R.drawable.recommand_bg)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        Log.e("TAG","1-onBinderHeaderViewHolder");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_discover_newtags, parent, false);
        WrapLinearLayout ll=(WrapLinearLayout)v.findViewById(R.id.discover_tags_ll);
        //ToastUtils.show(mContext, "执行了吗");
        if (mHeader!=null&&mHeader.results!=null)
            if (mHeader.results.size() > 0) {
            Log.e("TAG","1-onBinderHeaderViewHolder == add tags");
//            int size;
            int count = mHeader.results.size();
                for (int i = 0; i < count; i++) {
                    setTextView(parent.getContext(),i,ll);
                }
//            if (count % 3 == 0) {
//                size = count / 3;
//            } else {
//                size = (int) count / 3 + 1;
//            }
//            for (int i = 0; i < size; i++) {
//                LinearLayout layout = new LinearLayout(parent.getContext());
//                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                int scap_Mar = ScreenUtils.dpToPxInt(parent.getContext(), 10);
////                layoutParams.setMargins(scap_Mar,10,10,10);
//                layout.setLayoutParams(layoutParams);
//                layout.setGravity(Gravity.CENTER);
//                layout.setPadding(scap_Mar,0,0,0);
//                if (count%3==0){
//                    for (int j = 3*i; j <3*(i+1) ; j++) {
//                        setTextView(parent.getContext(),j,layout);
//                    }
//                }else if (count<3){
//                    for (int j = 0; j < size; j++) {
//                        setTextView(parent.getContext(),j,layout);
//                    }
//                }else {
//                    if (i+1==size){
//                        for (int j = 3*i; j < 3*i+size%3; j++) {
//                            setTextView(parent.getContext(),j,layout);
//                        }
//                    }else {
//                        for (int j = 3*i; j <3*(i+1) ; j++) {
//                            setTextView(parent.getContext(),j,layout);
//                        }
//                    }
//
//                }
//                ll.addView(layout);
//            }

        }

        final DiscoverHeaderViewHolder holder = new DiscoverHeaderViewHolder(v);
        return holder;
    }

    private  void setTextView(Context context,int j,LinearLayout ll){
        final TextView tv=new TextView(context);
        int scap_Mar = ScreenUtils.dpToPxInt(context, 20);
        int scap_width = ScreenUtils.dpToPxInt(context, 30);
        int scap_Maxwidth = ScreenUtils.dpToPxInt(context, 100);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(0,scap_width ,1.0f);
        layoutParams.setMargins(scap_Mar, scap_Mar/2, scap_Mar, scap_Mar/2);
        tv.setLayoutParams(layoutParams);
        tv.setTag("true");
        final String txt=mHeader.results.get(j).name;
        tv.setTextColor(COLOR_CHANGE);
        tv.setBackgroundResource(R.drawable.bg_tv_corner_stroke);
        tv.setText(txt);
        tv.setMaxWidth(scap_Maxwidth);
        tv.setGravity(Gravity.CENTER);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setSingleLine(true);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onValidClick(View v) {
                if (v.getTag().equals("true")) {
                    tv.setTextColor(COLOR_WHITE);
                    tv.setBackgroundResource(R.drawable.bg_tv_corner_stroke_click);
                   // ToastUtils.show(mContext,txt);
                    tags.add(txt);
                    tv.setTag("false");
                } else {
                    tv.setTextColor(COLOR_CHANGE);
                    tv.setBackgroundResource(R.drawable.bg_tv_corner_stroke);
                    tags.remove(txt);
                    tv.setTag("true");
                }
                setSelectDatas(tags);
               // ToastUtils.show(mContext,"Tag:"+tags.size());
            }
        });

        ll.addView(tv);

    }
    private void setSelectDatas(List<String> tags){

        if (tags.size()<1){
            tagsAddTemp.clear();
          //  setDataItems(itemDatas);
            setDataItemsNoHeader(itemDatas);
            DiscoverFragment.getIntstance().setHideEmpty_hint_view();
          //  notifyDataSetChanged();
        }else {
            tagsAddTemp.clear();
            for (int i = 1; i < itemDatas.size(); i++) {
                if (itemDatas.get(i)!=null){
                    if (compareTags(tags,itemDatas.get(i).tag)){
                        tagsAddTemp.add(itemDatas.get(i));
                    }
                }
            }
            setDataItems(null);
            notifyDataSetChanged();
            setDataItems(tagsAddTemp);
            if (tagsAddTemp.size()==1){
                DiscoverFragment.getIntstance().setShowEmpty_hint_view();
            }else {
                DiscoverFragment.getIntstance().setHideEmpty_hint_view();
            }
            notifyDataSetChanged();

        }
    }
    private  boolean compareTags(List<String> tag1,List<Object> tag2){
        if (tag1==null){return false;}
        if (tag2==null){return false;}
        if (tag1.size()>tag2.size()){
            return false;
        }else {
            for (int i = 0; i < tag1.size(); i++) {// 点击 标签
                int j = 0;
                for(; j < tag2.size(); j++) {//景区 标签
                    if (tag2.get(j).toString().equals(tag1.get(i))) {
                        break;//结束本循环
                    }
                }
                //没有匹配成功
                if(j == tag2.size())
                    return false;
            }
        }
        return true;
    }
    private List<String> tags=new ArrayList<String>();
    private List<QHScenic> tagsAddTemp=new ArrayList<QHScenic>();
    private static List<QHScenic> itemDatas=new ArrayList<QHScenic>();

    @Override
    public void onBinderHeaderViewHolder(ViewHolder holder) {
        Log.e("TAG","2-onBinderHeaderViewHolder");
      //  notifyDataSetChanged();
//        notifyItemChanged(0);
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        Log.e("TAG","3-onCreateItemViewHolder");

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover_list, parent, false);

        return new DiscoverViewHolder(v);
    }

    @Override
    public void onBinderItemViewHolder(ViewHolder holder, int position) {
        Log.e("TAG","4-onBinderHeaderViewHolder");
        final DiscoverViewHolder viewHolder = (DiscoverViewHolder) holder;
        if (mDataItems.size() <= position) {
            Log.e(TAG, "[onBinderItemViewHolder] position out of bound");
          //  viewHolder.empty_hint_view.setVisibility(View.VISIBLE);
          //  return;
        }
//        if (mDataItems.size()==0){
//            viewHolder.empty_hint_view.setVisibility(View.VISIBLE);
//        }else {
//            viewHolder.empty_hint_view.setVisibility(View.GONE);
//        }

        final QHScenic scenic = mDataItems.get(position);

        if (scenic == null) {
            Log.e(TAG, "[onBinderItemViewHolder] comment: " + scenic);
          //  viewHolder.empty_hint_view.setVisibility(View.VISIBLE);
            return;
        }

        viewHolder.setDataTag(scenic);
        if (!TextUtils.isEmpty(scenic.image_url)) {
//            bitmapUtils.display(((DiscoverViewHolder) holder).netImage, scenic.image_url);
//            bitmapUtils.display(viewHolder.netImage, scenic.image_url,
//                    new BitmapLoadCallBack<ImageView>() {
//
//                        @Override
//                        public void onLoadCompleted(ImageView arg0, String arg1,
//                                                    Bitmap bitmap, BitmapDisplayConfig arg3,
//                                                    BitmapLoadFrom arg4) {
//                            viewHolder.netImage.setImageBitmap(bitmap);// 设置图片
//                            ToastUtils.show(mContext,"成功了");
//                        }
//
//                        @Override
//                        public void onLoadFailed(ImageView arg0, String arg1,
//                                                 Drawable arg2) {
//                            ToastUtils.show(mContext,"失败");
//                        }
//
//                    });
//            viewHolder.netImage.setImageUrl(scenic.image_url, RequestManager.getInstance().getImageLoader());
//            viewHolder.netImage.setErrorImageResId(R.drawable.recommand_bg);
//            viewHolder.netImage.setDefaultImageResId(R.drawable.recommand_bg);
            ImageUtil.DisplayImage(scenic.image_url, viewHolder.netImage,R.drawable.recommand_bg,R.drawable.recommand_bg);
//   viewHolder.setErrorImageResId(R.color.transparency);
//            viewHolder.netImage.setDefaultImageResId(R.color.transparency);
        }

        viewHolder.item_name.setText(scenic.name);
        viewHolder.item_intro.setText(scenic.intro_text.trim().replaceAll(" ","").replaceAll("\r\n",""));
        attachClickListener(viewHolder, viewHolder.rl_discover_item, position);
//        attachClickListener(viewHolder, viewHolder.item_intro, position);

    }

    public void setSaveTemp(List<QHScenic> results) {
        itemDatas=results;
    }


   static class DiscoverHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tags_selfDrivingTravel, tags_parentChildTravel, tags_outdoorBarbecue, tags_natureContast, tags_rapeFlowerAppr, tags_summerVacation;

        public DiscoverHeaderViewHolder(View itemView) {
            super(itemView);
            tags_selfDrivingTravel = (TextView) itemView.findViewById(R.id.tags_selfDrivingTravel);
            tags_parentChildTravel = (TextView) itemView.findViewById(R.id.tags_parentChildTravel);
            tags_outdoorBarbecue = (TextView) itemView.findViewById(R.id.tags_outdoorBarbecue);
            tags_natureContast = (TextView) itemView.findViewById(R.id.tags_natureContast);
            tags_rapeFlowerAppr = (TextView) itemView.findViewById(R.id.tags_rapeFlowerAppr);
            tags_summerVacation = (TextView) itemView.findViewById(R.id.tags_summerVacation);
        }
    }

    class DiscoverViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView netImage;
        TextView item_name, item_intro;
        View empty_hint_view;
        RelativeLayout rl_discover_item;

        public DiscoverViewHolder(View itemView) {
            super(itemView);
            rl_discover_item = (RelativeLayout) itemView.findViewById(R.id.rl_discover_item);
            netImage = (RoundedImageView) itemView.findViewById(R.id.iv_item_discover);
            item_name = (TextView) itemView.findViewById(R.id.item_title);
            item_intro = (TextView) itemView.findViewById(R.id.item_contents);
//            empty_hint_view =  itemView.findViewById(R.id.empty_hint_view)
        }

        public void setDataTag(QHScenic scenic) {
            rl_discover_item.setTag(scenic);
            itemView.setTag(scenic);
            netImage.setTag(scenic);
            item_intro.setTag(scenic);
//            empty_hint_view.setTag(scenic);
        }
    }
}
