package test.grs.com.ims.contact;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.R;
import test.grs.com.ims.message.ContactChangeListener;
import test.grs.com.ims.message.DialogFactory;
import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.MessageActivity;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private BitmapUtils bitmapUtils;
    public List<SortModel> list;
    private Context mContext;
    private ContactChangeListener mListener;
    private List<SortModel> selectlist = new ArrayList<SortModel>();

    public SortAdapter(Context mContext, List<SortModel> list, ListView listView) {
        this.mContext = mContext;
        this.mListener = (ContactChangeListener) mContext;
        this.list = list;
        bitmapUtils = new BitmapUtils(mContext, IMConst.GLOBALSTORAGE_DOWNLOAD_PATH);
        bitmapUtils.configDiskCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            bitmapUtils.configMemoryCacheEnabled(false);
        }
        // 滑动时加载图片，快速滑动时不加载图片
        listView.setOnScrollListener(new PauseOnScrollListener(bitmapUtils, false, true));
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<SortModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<SortModel> getSelected() {
        return selectlist;
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
        //添加复用
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.constacts_item, null);
            viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
            viewHolder.message_send = (ImageView) view.findViewById(R.id.message_send);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.nick_phone = (TextView) view.findViewById(R.id.nick_phone);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.cb_select = (ToggleButton) view.findViewById(R.id.cb_select);
            viewHolder.ll_constact = (LinearLayout) view.findViewById(R.id.ll_constact);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final SortModel sortModel = list.get(position);
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(sortModel.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        final String username = sortModel.getName();//userId
        final String name = sortModel.getNickname();
        viewHolder.tvTitle.setText(name + "");//显示昵称
        if (sortModel.getIntroduction() != null && !sortModel.getIntroduction().equals("null")) {
            viewHolder.nick_phone.setVisibility(View.VISIBLE);
            viewHolder.nick_phone.setText(sortModel.getIntroduction() + "");//介绍
        }
//
        String avatar_url = sortModel.getAvatar_url();
        if (avatar_url != null && !avatar_url.equals("")) {
            bitmapUtils.display(viewHolder.icon, avatar_url);
            bitmapUtils.configDefaultLoadFailedImage(R.drawable.recommand_bgs);
        } else {
            viewHolder.icon.setImageResource(R.drawable.recommand_bgs);
        }

        //添加群成员
        if (sortModel.getType() == SortModel.TYPE_ADD) {
            viewHolder.cb_select.setVisibility(View.VISIBLE);
            if (sortModel.isSelect()) {
                viewHolder.cb_select.setChecked(true);
//                if (username.equals(((ContactsActivity) mContext).chatUserName)) {
//                    viewHolder.cb_select.setEnabled(false);
//                }
                if (sortModel.isfriend()){
                    viewHolder.cb_select.setEnabled(false);
                }else {
                    viewHolder.cb_select.setEnabled(true);
                }
            } else {
//                viewHolder.cb_select.setTag("0");
                viewHolder.cb_select.setEnabled(true);
                viewHolder.cb_select.setChecked(false);
            }
//            if (viewHolder.cb_select.getTag() != null && viewHolder.cb_select.getTag().equals("1")) {
//                viewHolder.cb_select.setChecked(true);
//            }
            viewHolder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!compoundButton.isPressed()) {
                        return;    //加这一条，否则当我setChecked()时会触发此listener
                    }
                    Log.e("=b=" + b, "TAG:" + position);
                    if (b) {
                        selectlist.add(sortModel);
//                        viewHolder.cb_select.setTag("1");
                        Log.e("==add", "TAG");
                    } else {
                        selectlist.remove(sortModel);
//                        viewHolder.cb_select.setTag("0");
                        Log.e("==remove", "TAG");
                    }
                    //添加到bean
                    sortModel.setSelect(b);
                    list.remove(position);
                    list.add(position, sortModel);
                    notifyDataSetChanged();
//                    //回调
                    mListener.onContactChanged(selectlist);
                }
            });
        } else if (sortModel.getType() == SortModel.TYPE_FORWARD) {
            //点击转发
            viewHolder.ll_constact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((ContactsActivity) mContext).navigate == 0) {
                        Dialog foreardDialog = DialogFactory.getConfirmDialog2(mContext, "转发消息",
                                String.format("确认要将消息转发给\"%s\"？", name),
                                "取消", "确认", null, new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent backIntent = new Intent();
                                        Bundle backBundle = new Bundle();
                                        backBundle.putString(IMConst.SINGLE_RESULT_USER_NAME,
                                                username);
                                        backBundle.putString(IMConst.SINGLE_RESULT_USER_NICK,
                                                name);
                                        backIntent.putExtras(backBundle);
                                        ((Activity) mContext).setResult(IMConst.SINGLE_RESULT_CODE, backIntent);
                                        ((Activity) mContext).finish();
                                    }
                                });
                        foreardDialog.show();
                    } else {
                        Intent intent = new Intent(mContext, MessageActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("username", username);
                        mContext.startActivity(intent);
                    }
                }
            });
        } else if (sortModel.getType() == SortModel.TYPE_ATTENTION) {
            //我关注
            if (sortModel.isfriend()) {
                viewHolder.message_send.setVisibility(View.VISIBLE);
                viewHolder.message_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //同步关注
                        Toast.makeText(mContext, "已互相关注", Toast.LENGTH_SHORT).show();
                        viewHolder.message_send.setEnabled(false);
                    }
                });
            }
            viewHolder.ll_constact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转用户详情
                    mContext.sendBroadcast(new Intent(IMConst.ACTION_STARTACTIVITY).putExtra(IMConst.USER_ID, sortModel.getName()));
                }
            });
            viewHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转用户详情
                    mContext.sendBroadcast(new Intent(IMConst.ACTION_STARTACTIVITY).putExtra(IMConst.USER_ID, sortModel.getName()));
                }
            });
        } else if (sortModel.getType() == SortModel.TYPE_UNATTENTION) {
            //关注我人
            viewHolder.message_send.setVisibility(View.VISIBLE);
            if (!sortModel.isfriend()) {
                viewHolder.message_send.setImageResource(R.drawable.jgz1);
            }
            //点击添加的不再显示
//            if (viewHolder.message_send.getTag() != null && viewHolder.message_send.getTag().equals("1")) {
//                viewHolder.message_send.setVisibility(View.GONE);
//            }
//            viewHolder.message_send.setTag("0");
            viewHolder.message_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sortModel.isfriend()) {
                        //同步关注
                        Toast.makeText(mContext, "已互相关注", Toast.LENGTH_SHORT).show();
                    } else {
                        //加关注
                        mContext.sendBroadcast(new Intent(IMConst.ACTION_ADD_ATTENTION).putExtra(IMConst.USER_ID, sortModel.getName()));
                        //调用刷新
                        ((ContactsActivity) mContext).refreshDatas();
//                        viewHolder.message_send.setVisibility(View.GONE);
//                        viewHolder.message_send.setTag("1");
                    }
                }
            });
            viewHolder.ll_constact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转用户详情
                    mContext.sendBroadcast(new Intent(IMConst.ACTION_STARTACTIVITY).putExtra(IMConst.USER_ID, sortModel.getName()));
                }
            });
        }

        return view;

    }

    final static class ViewHolder {
        ImageView icon;
        ImageView message_send;
        TextView tvLetter;
        TextView tvTitle;
        TextView nick_phone;
        ToggleButton cb_select;
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