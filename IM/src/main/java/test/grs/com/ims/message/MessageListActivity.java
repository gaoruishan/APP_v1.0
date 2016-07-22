package test.grs.com.ims.message;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.contact.AddContactActivity;
import test.grs.com.ims.contact.GroupContactsActivity;
import test.grs.com.ims.contact.SortModel;
import test.grs.com.ims.view.RoundImageView;
import test.grs.com.ims.view.TextViewSnippet;

public class MessageListActivity extends IMBaseActivity {
    private static final String USERNAME = "gao";
    private static final String PASSWORD = "123456";
    private static final String USERNAME1 = "18813067473";
    private static final String USERNAME2 = "xiao";
    private static final String USERNAME3 = "hua";
    private static final String USERNAME4 = "wang";
    private static final String USERNAME5 = "yang";
    private static final String USERNAME6 = "13913067473";
    private static final String USERNAME7 = "zhang";
    private static final String USERNAME8 = "wei";

    private static final String USERNAME9 = "18713067473";
    private static final String USERNAME10 = "liu";
    private static final String USERNAME11 = "cheng";
    private static final String USERNAME12 = "song";
    private static final String USERNAME13 = "15113067473";

    private EditText mSearchEditText;
    private ListView mListView;
    private ImageView mLoadingView, unMsg_notify;
    private ImageView cancelSearchImageView;
    private ConversationAdapter mAdapter;
    private ArrayList<IMContact> conversations;
    public static boolean isDelete = false;
    private List<IMContact> cmContactsList = new ArrayList<IMContact>();
    private TextView tv_no_conversation_hint;
    private TextView contact_title_textview1;
    private Dialog mLoadingDialog;
    private Intent serviceIntent;
    private ArrayList<UNIMContact> scmContactsList;
    private LinearLayout ll_msg_center, ll_msg_unnotice;
    public LinearLayout ll_netErr, title_convers;
    private long exitTime;
    private boolean isDisConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "TAG");
        //开启监听登录的广播
        IntentFilter filter = new IntentFilter(IMConst.ACTION_NET_CONNECT);
        filter.addAction(IMConst.ACTION_LOGIN_SUCCESS);
        filter.addAction(IMConst.ACTION_LOGIN_FAIL);
        filter.addAction(IMConst.NET_DISCONNECT);
        filter.addAction(IMConst.NET_RECONNECT);
        registerReceiver(receiverService, filter);
        //开启后台服务
        startServiceToLogin();

        setContentView(R.layout.activity_message_list);
        initView();
        mAdapter = new ConversationAdapter(cmContactsList, this);
        mListView.setAdapter(mAdapter);
//        mListView.setEnabled(false);//登录后可点击
//        mListView.setClickable(false);
//        mListView.setItemsCanFocus(false);
        //监听
        registerListener();
        // 注册网络监听
//        NetworkStateReceiver.registerNetworkStateReceiver(this);
//        NetworkStateReceiver.registerObserver(observer);
    }
//    public MyNetChngeOberver observer = new MyNetChngeOberver();
//    public class MyNetChngeOberver implements NetChangeObserver {
//
//        @Override
//        public void onConnect(NetWorkUtil.NetType type) {
//            Log.e("onConnect", "TAG");
//            if (isDisConn){
//                startService(new Intent(MessageListActivity.this, BackgroundService.class));
//                Log.e("startService", "TAG");
//            }
//            ll_netErr.setVisibility(View.GONE);
//        }
//
//        @Override
//        public void onDisConnect() {
//            isDisConn = true;
//            ll_netErr.setVisibility(View.VISIBLE);
//        }
//    }

    /**
     * 开启登录服务
     */
    private void startServiceToLogin() {
        serviceIntent = new Intent(this, BackgroundService.class);
        if (IMApp.currentUserName!=null){
            serviceIntent.putExtra("userName", IMApp.currentUserName);
            serviceIntent.putExtra("passWord", IMApp.currentUserPsw);
            startService(serviceIntent);
        }
    }

    /**
     * 消息中心
     *
     * @param view
     */
    public void doShowMesageList(View view) {
        sendBroadcast(new Intent(IMConst.ACTION_START_GUDERMESSAGE));
//        startActivity(new Intent(MessageListActivity.this, MessageCenterActivity.class));
    }

    private int sum = 0;

    public void dododdo(View v) {
//        if (sum < 3) {
//            sum++;
//        } else if (sum == 3) {
//            title_convers.setVisibility(View.VISIBLE);
//        } else {
//            title_convers.setVisibility(View.GONE);
//        }
    }

    /**
     * 未关注的人
     *
     * @param view
     */
    public void doShowUnnoticeList(View view) {
        Intent intent = new Intent(this, UnNotifyActivity.class);
        intent.putParcelableArrayListExtra("list", scmContactsList);
        startActivity(intent);
        unMsg_notify.setVisibility(View.GONE);
    }

    /**
     * 群聊
     */
    public void doGroupChat(View view) {
        Intent intent = new Intent(this, GroupContactsActivity.class);
        startActivity(intent);
    }

    private void initView() {
        mSearchEditText = (EditText) findViewById(R.id.et_search);
        mListView = (ListView) findViewById(R.id.converstaion_list);
        mListView.setEmptyView(findViewById(R.id.tv_no_conversation_hint));
        mLoadingView = (ImageView) findViewById(R.id.loading_img_progress);
        unMsg_notify = (ImageView) findViewById(R.id.unMsg_notify);
        cancelSearchImageView = (ImageView) findViewById(R.id.bt_cancel_search);
        ll_msg_center = (LinearLayout) findViewById(R.id.ll_msg_center);
        ll_msg_unnotice = (LinearLayout) findViewById(R.id.ll_msg_unnotice);
        ll_netErr = (LinearLayout) findViewById(R.id.ll_netErr);
        title_convers = (LinearLayout) findViewById(R.id.title_convers);

        tv_no_conversation_hint = (TextView) findViewById(R.id.tv_no_conversation_hint);
        contact_title_textview1 = (TextView) findViewById(R.id.contact_title_textview1);
    }

    private BroadcastReceiver receiverService = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IMConst.ACTION_LOGIN_SUCCESS.equals(action)) {
                // 登录成功
//                hideDialog();
//                Toast.makeText(MessageListActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                mListView.setEnabled(true);
                mListView.setClickable(true);
                mListView.setItemsCanFocus(true);
            } else if (IMConst.ACTION_LOGIN_START.equals(action)) {
                Log.e("Logining", "TAG");
//                 showLogining();
            } else if (IMConst.ACTION_LOGIN_FAIL.equals(action)) {
                //登录失败
                String errorMsg = intent.getStringExtra(IMConst.LOGIN_FAIL_MSG);
                Toast.makeText(MessageListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                //停止服务
//                stopService(serviceIntent);
//                hideDialog();
                ll_netErr.setVisibility(View.VISIBLE);

            } else if (IMConst.NET_DISCONNECT.equals(action)) {
                ll_netErr.setVisibility(View.VISIBLE);
                mListView.setEnabled(false);//登录后可点击
                mListView.setClickable(false);
                mListView.setItemsCanFocus(false);
            } else if (IMConst.NET_RECONNECT.equals(action)) {
                ll_netErr.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //取消

//        //更新
        cmContactsList = MessageHandle.getInstance().getDbIMContact();
        //删除
        if (isDelete) {
            cmContactsList.remove(0);
        }
        mAdapter.setDatasChange(cmContactsList);
    }

    private void showLogining() {
        mLoadingDialog = DialogFactory.getLoadingDialog(this, this.getString(R.string.hint_logining), false, null);

        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    private void hideDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    private void registerListener() {
        //点击通讯录
        contact_title_textview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageListActivity.this, AddContactActivity.class));
            }
        });
        //监听通知信息
        MessageHandle.getInstance().setOnRecivedMessageListener(new MessageHandle.OnRecivedMessageListener() {
            @Override
            public void onSelected(final int type) {
                Log.e("onSelected", "TAG");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (type == 0||type == 1) {
                            cmContactsList = MessageHandle.getInstance().getDbIMContact();
                            mAdapter.setDatasChange(cmContactsList);
                        }
                        if (type == 10||type == 11) {
                            unMsg_notify.setVisibility(View.VISIBLE);
                            scmContactsList = MessageHandle.getInstance().getDbUNIMContact();
                        }
                    }
                });
            }

            @Override
            public void onBackSuccess(List<IMContactList> user) {

            }

            @Override
            public void onBackContactSuccess(List<SortModel> list) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        //保存数据
        if (cmContactsList!=null&&cmContactsList.size()>0){
            MessageHandle.getInstance().updateToDB(cmContactsList);
        }
        //注销服务
        if (receiverService != null) {
            unregisterReceiver(receiverService);
        }
        super.onDestroy();

        //退出登录,和服务器断开连接
//        CMIMHelper.getCmAccountManager().doLogOut();
        //注销网络状态
//        NetworkStateReceiver.removeRegisterObserver(observer);
//        NetworkStateReceiver.unRegisterNetworkStateReceiver(this);
    }

    public void doBack(View view) {
        finish();
    }
    /**
     * 按返回键
     */
//    @Override
//    public void onBackPressed() {
//        if (System.currentTimeMillis() - exitTime > 2000) {
//           // 可直接调用的吐司
//            Toast.makeText(this,"再按一次退出" , Toast.LENGTH_SHORT).show();
//            exitTime = System.currentTimeMillis();
//        } else {
//            moveTaskToBack(false);
//        }
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    /**
     * 消息列表适配器
     */
    private class ConversationAdapter extends AppBaseAdapter<IMContact> {

        private final BitmapUtils bitmapUtils;

        public ConversationAdapter(List<IMContact> list, Context context) {
            super(list, context);
            bitmapUtils = new BitmapUtils(context);
            bitmapUtils.configDiskCacheEnabled(true);
            bitmapUtils.configMemoryCacheEnabled(false);

        }

        @Override
        public View createView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_conversation, parent, false);
            final IMContact cmContacts = list.get(position);
            if (cmContacts == null) {
                return null;
            }
            TextViewSnippet conv_name = (TextViewSnippet) convertView.findViewById(R.id.conv_name);
            RoundImageView conv_portrait = (RoundImageView) convertView.findViewById(R.id.conv_portrait);
            TextView conv_note = (TextView) convertView.findViewById(R.id.conv_note);
            TextView conv_count = (TextView) convertView.findViewById(R.id.conv_count);
            TextView conv_date = (TextView) convertView.findViewById(R.id.conv_date);
            if (cmContacts.isTop()) {
                convertView.setBackgroundResource(R.color.public_line_vertical_gray);
            }
            conv_portrait.setImageResource(R.drawable.recommand_bgs);
            if (cmContacts.getAvatarurl() != null && !cmContacts.getAvatarurl().equals("")) {
                bitmapUtils.display(conv_portrait, cmContacts.getAvatarurl());
                bitmapUtils.configDefaultLoadFailedImage(R.drawable.recommand_bgs);
            } else {
                if (cmContacts.isGroupChat()) {
                    conv_portrait.setImageResource(R.drawable.tx);
                } else {
                    conv_portrait.setImageResource(R.drawable.recommand_bgs);
                }
            }
            conv_name.setText(cmContacts.getNickname()+"");
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

                    Intent intent = new Intent(MessageListActivity.this, MessageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    intent.putExtra(IMConst.USERNAME, cmContacts.getUserName());
                    intent.putExtra(IMConst.AVATARURL, cmContacts.getAvatarurl());
                    intent.putExtra(IMConst.CHATTYPE, cmContacts.isGroupChat());
                    intent.putExtra(IMConst.NICKNAME, cmContacts.getNickname());// 对于群组 username 即群名字
                    intent.putExtra(IMConst.GROUPNAME, cmContacts.getNickname());// 对于群组 username 即群名字
                    intent.putExtra(IMConst.GROUPID, cmContacts.getGroupId());
                    intent.putExtra(IMConst.GUID, cmContacts.getGuid());
                    startActivity(intent);
                    //清除
                    cmContacts.setMsgNum(0);
                    cmContactsList.remove(position);
                    cmContactsList.add(position, cmContacts);
                    notifyDataSetChanged();
                    //保存数据
                    MessageHandle.getInstance().updateToDB(cmContactsList);
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
                            MessageHandle.getInstance().updateToDB(cmContactsList);
//
                        }
                    };
                    DialogFactory.getConfirmDialog2(context, getResources().getString(R.string.del_chat_data1),
                            "你确定要删除和“" + cmContacts.getNickname() + "”的聊天会话？", getResources().getString(R.string.btn_subject_cancel),
                            getResources().getString(R.string.btn_subject_confirm), null, okBtnEvent).show();

                    return false;
                }
            });
            return convertView;
        }
    }


}
