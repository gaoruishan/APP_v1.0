package test.grs.com.ims.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import java.util.List;

import test.grs.com.ims.R;
import test.grs.com.ims.message.IMConst;

public class SortAdapterList extends BaseAdapter implements SectionIndexer {
    private BitmapUtils bitmapUtils;
    private List<SortModel> list = null;
    private Context mContext;

    public SortAdapterList(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.list = list;
        bitmapUtils = new BitmapUtils(mContext, IMConst.GLOBALSTORAGE_DOWNLOAD_PATH);
        bitmapUtils.configDiskCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            bitmapUtils.configMemoryCacheEnabled(false);
        }
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<SortModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        final ViewHolder viewHolder;
        final SortModel mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.fragment_phone_constactslist_item, null);
            viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
            viewHolder.message_send = (ImageView) view.findViewById(R.id.message_send);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.nick_phone = (TextView) view.findViewById(R.id.nick_phone);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.ll_constact = (LinearLayout) view.findViewById(R.id.ll_constact);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        final String username = mContent.getName();
        final String name =mContent.getNickname();
        viewHolder.tvTitle.setText(name + "");
//		viewHolder.nick_phone.setText(name+"");
        String avatar_url =mContent.getAvatar_url();
        if (avatar_url != null && !avatar_url.equals("")) {
            bitmapUtils.display(viewHolder.icon, avatar_url);
            bitmapUtils.configDefaultLoadFailedImage(R.drawable.recommand_bgs);
        }else {
            viewHolder.icon.setImageResource(R.drawable.recommand_bgs);
        }
        //点击添加的不再显示
//        if (viewHolder.message_send.getTag() != null&& viewHolder.message_send.getTag().equals("1")){
//            viewHolder.message_send.setVisibility(View.GONE);
//        }
//        viewHolder.message_send.setTag("0");
        viewHolder.message_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //加关注
                mContext.sendBroadcast(new Intent(IMConst.ACTION_ADD_ATTENTION).putExtra(IMConst.USER_ID, username));
//                viewHolder.message_send.setVisibility(View.GONE);
//                viewHolder.message_send.setTag("1");
            }
        });
        return view;

    }

    final static class ViewHolder {
        ImageView icon;
        ImageView message_send;
        TextView tvLetter;
        TextView tvTitle;
        TextView nick_phone;
        CheckBox cb_select;
        LinearLayout ll_constact;
    }


    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}