package test.grs.com.ims.message;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

import test.grs.com.ims.R;

public class EmojiManager {
    static Context mContext;

    private static final String EMOJI_ITEM_IMAGE = "ItemImage";
    private static final String EMOJI_ITEM_TEXT = "ItemText";
    private static final String EMOJI_ITEM_TYPE_DEL_BTN = "del_btn";

    private static EmojiParser mParser;
    private static int mType;

    public EmojiManager() {
        // TODO Auto-generated constructor stub
    }

    public static void initEmojiGrid(Context context, View parent, final EditText inputView, int type) {
        mContext = context;
        mType = type;
        ViewPager viewPager = (ViewPager) parent.findViewById(R.id.vvvPager);

        mParser = EmojiParser.getInstance(context.getApplicationContext(),type);
        ArrayList<View> pageViews = new ArrayList<View>();
        createEmojiGridPages(pageViews, context, inputView);

        ViewGroup pageIndicator = (ViewGroup) parent.findViewById(R.id.message_emoji_page_indicator);
        ImageView[] imageViews = new ImageView[pageViews.size()];
        pageIndicator.removeAllViews();
        for (int i = 0; i < pageViews.size(); i++) {
            ImageView imageView = new ImageView(context);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(lp);
            lp.leftMargin = 10;
            lp.rightMargin = 10;
            imageViews[i] = imageView;
            if (i == 0) {
                imageViews[i].setBackgroundResource(R.drawable.msg_smilelist_dot1);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.msg_smilelist_dot2);
            }

            pageIndicator.addView(imageViews[i]);
        }

        viewPager.setAdapter(new GuidePageAdapter(pageViews));
        viewPager.setOnPageChangeListener(new GuidePageChangeListener(imageViews));
    }

    private static void createEmojiGridPages(ArrayList<View> pageViews, Context context, EditText inputView) {
        int maxItems = 23;
        float density = context.getResources().getDisplayMetrics().density;
        if (density <= 1.5f) {
            maxItems = 17;
        }
        String[] emojiTexts = mParser.getEncodedSmilyTextArray();
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 1; i <= emojiTexts.length; i++) {
            int index = i - 1;
            HashMap<String, Object> map = new HashMap<String, Object>();

            final String emojiCode = emojiTexts[index];
            map.put(EMOJI_ITEM_TEXT, emojiCode);//real text which is sent to server
            map.put(EMOJI_ITEM_IMAGE, mParser.getSmileyDrawableId(emojiCode));
            lstImageItem.add(map);
            if ((i != 0 && i % maxItems == 0) || (i == emojiTexts.length)) {
                map = new HashMap<String, Object>();
                map.put(EMOJI_ITEM_IMAGE, R.drawable.emoji_del_btn_nor);
                map.put(EMOJI_ITEM_TEXT, EMOJI_ITEM_TYPE_DEL_BTN);
                lstImageItem.add(map);
                SimpleAdapter saImageItems = new SimpleAdapter(context,
                        (ArrayList<HashMap<String, Object>>) lstImageItem.clone(),
                        R.layout.im_emoji_grid_item_layout,
                        new String[]{EMOJI_ITEM_IMAGE},
                        new int[]{R.id.emojiImageView});
                GridView gridview = (GridView) LayoutInflater.from(context).inflate(R.layout.im_emoji_grid_layout, null);
                gridview.setAdapter(saImageItems);
                gridview.setOnItemClickListener(new ItemClickListener(inputView));
                pageViews.add(gridview);
                lstImageItem.clear();
            }
        }
    }


    static class ItemClickListener implements OnItemClickListener {
        EditText inputView;
        final KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);

        ItemClickListener(EditText et) {
            inputView = et;
        }

        public void onItemClick(AdapterView<?> adapter, View arg1,
                                int position, long arg3) {
            HashMap<String, Object> item = (HashMap<String, Object>) adapter.getItemAtPosition(position);
            String itemText = "" + item.get(EMOJI_ITEM_TEXT);

            String unicode = EmojiParser.getInstance(mContext,mType).parseEmoji(itemText);
            SpannableString spannableString = ParseEmojiMsgUtil.getExpressionString(mContext, unicode);

            int index = inputView.getSelectionStart();
            Editable editable = inputView.getText();
            if (itemText.equals(EMOJI_ITEM_TYPE_DEL_BTN)) {
                if (index > 0) {
                    inputView.onKeyDown(KeyEvent.KEYCODE_DEL, keyEventDown);
                }
            } else {
                if (mType == 0){
                    CharSequence emojiChar = addSmileySpans(itemText);
                    editable.insert(index, emojiChar);
                    Selection.setSelection(editable, index+emojiChar.length());
                }else if (mType == 1){
                    editable.insert(index, spannableString);
                    Selection.setSelection(editable, index + spannableString.length());
                }

            }
        }
    }

    static class GuidePageAdapter extends PagerAdapter {
        ArrayList<View> pageViews;

        GuidePageAdapter(ArrayList<View> pvs) {
            pageViews = pvs;
        }

        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub  
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub  

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub  
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub  

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub  

        }
    }

    static class GuidePageChangeListener implements OnPageChangeListener {
        ImageView[] imageViews;

        GuidePageChangeListener(ImageView[] ivs) {
            imageViews = ivs;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub  

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub  

        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[arg0].setBackgroundResource(R.drawable.msg_smilelist_dot1);

                if (arg0 != i) {
                    imageViews[i].setBackgroundResource(R.drawable.msg_smilelist_dot2);
                }
            }
        }
    }

    private static int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static CharSequence addSmileySpans(CharSequence text) {
        if (text == null) {
            text = "";
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Matcher matcher = EmojiParser.getInstance(mContext).mPattern.matcher(text);
        while (matcher.find()) {
            int resId = EmojiParser.getInstance(mContext).mSmileyToRes.get(matcher.group());
            Drawable drawable = mContext.getResources().getDrawable(resId);
//            float height = drawable.getIntrinsicHeight() * 0.5f;
//            drawable.setBounds(0, 0, (int) height, (int) height);
            drawable.setBounds(0, 0, dip2px(19), dip2px(19));
            builder.setSpan(new ImageSpan(drawable), matcher.start(),
                    matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }
}
