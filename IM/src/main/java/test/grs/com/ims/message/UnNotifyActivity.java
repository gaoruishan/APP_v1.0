package test.grs.com.ims.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.view.RoundImageView;
import test.grs.com.ims.view.TextViewSnippet;

public class UnNotifyActivity extends IMBaseActivity {
    private ArrayList<UNIMContact> cmContactsList;
    private ConversationAdapter mAdapter;
    private LinearLayout dialog_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_notify);
        ListView mListView = (ListView) findViewById(R.id.converstaion_list);
        dialog_empty = (LinearLayout) findViewById(R.id.dialog_empty);
        cmContactsList = getIntent().getParcelableArrayListExtra("list");
        mAdapter = new ConversationAdapter(cmContactsList, this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cmContactsList = MessageHandle.getInstance().getDbUNIMContact();
        if (cmContactsList.size()==0){
            dialog_empty.setVisibility(View.VISIBLE);
        }
        mAdapter.setDatasChange(cmContactsList);
    }

    public void doBack(View view) {
        finish();
    }

    /**
     * 消息列表适配器
     */
    private class ConversationAdapter extends AppBaseAdapter<UNIMContact> {

        private final BitmapUtils bitmapUtils;

        public ConversationAdapter(List<UNIMContact> list, Context context) {
            super(list, context);
            bitmapUtils = new BitmapUtils(context);
            bitmapUtils.configDiskCacheEnabled(true);
            bitmapUtils.configMemoryCacheEnabled(false);
        }

        @Override
        public View createView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_conversation, parent, false);
            final UNIMContact cmContacts = list.get(position);
            if (cmContacts == null) {
                return null;
            }
            TextViewSnippet conv_name = (TextViewSnippet) convertView.findViewById(R.id.conv_name);
            RoundImageView conv_portrait = (RoundImageView) convertView.findViewById(R.id.conv_portrait);
            TextViewSnippet conv_note = (TextViewSnippet) convertView.findViewById(R.id.conv_note);
            TextView conv_count = (TextView) convertView.findViewById(R.id.conv_count);
            TextView conv_date = (TextView) convertView.findViewById(R.id.conv_date);
            if (cmContacts.isTop()) {
                convertView.setBackgroundResource(R.color.public_line_vertical_gray);
            }
            if (cmContacts.getAvatarurl() != null && !cmContacts.getAvatarurl().equals("")) {
                bitmapUtils.configDefaultLoadingImage(R.drawable.recommand_bgs);
                bitmapUtils.display(conv_portrait, cmContacts.getAvatarurl());
                bitmapUtils.configDefaultLoadFailedImage(R.drawable.recommand_bgs);
            }
            conv_name.setText(cmContacts.getNickname()+"");//显示昵称
            String msg_content = cmContacts.getMessage();
            String unicode = EmojiParser.getInstance(context).parseEmoji(msg_content);
            SpannableString spannableString = ParseEmojiMsgUtil.getExpressionString(context, unicode);

            conv_note.setText(spannableString);
            conv_date.setText(IMUtil.getFormattedTime(context, cmContacts.getTime()));
            if (cmContacts.getMsgNum() == 0) {
                conv_count.setVisibility(View.GONE);
            } else {
                conv_count.setVisibility(View.VISIBLE);
            }
            conv_count.setText(cmContacts.getMsgNum() + "");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(UnNotifyActivity.this, MessageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    intent.putExtra(IMConst.USERNAME, cmContacts.getUserName());
                    intent.putExtra(IMConst.NICKNAME, cmContacts.getNickname());
                    intent.putExtra(IMConst.AVATARURL, cmContacts.getAvatarurl());
                    intent.putExtra(IMConst.CHATTYPE, cmContacts.isGroupChat());
                    intent.putExtra(IMConst.GUID, cmContacts.getGuid());
                    startActivity(intent);

                    //清除
                    cmContacts.setMsgNum(0);
                    cmContactsList.remove(position);
                    cmContactsList.add(position, cmContacts);
                    notifyDataSetChanged();
//                    //保存数据
                    MessageHandle.updateToUNDB(cmContactsList);
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    View.OnClickListener okBtnEvent = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cmContactsList.remove(position);
                            notifyDataSetChanged();
                            //保存数据
                            MessageHandle.updateToUNDB(cmContactsList);
//
                        }
                    };
                    DialogFactory.getConfirmDialog2(context, getResources().getString(R.string.del_chat_data1),
                            "你确定要删除和“"+cmContacts.getNickname()+"”的聊天会话？", getResources().getString(R.string.btn_subject_cancel),
                            getResources().getString(R.string.btn_subject_confirm), null, okBtnEvent).show();

                    return false;
                }
            });
            return convertView;
        }
    }

}
