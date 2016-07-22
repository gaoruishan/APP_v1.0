package test.grs.com.ims.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.littlec.sdk.business.MessageConstants;
import com.littlec.sdk.entity.CMMember;
import com.littlec.sdk.entity.CMMessage;
import com.littlec.sdk.entity.groupinfo.GroupInfo;
import com.littlec.sdk.entity.messagebody.AudioMessageBody;
import com.littlec.sdk.entity.messagebody.ImageMessageBody;
import com.littlec.sdk.entity.messagebody.TextMessageBody;
import com.littlec.sdk.manager.CMIMHelper;
import com.littlec.sdk.utils.CMChatListener;
import com.littlec.sdk.utils.CommonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.contact.ContactsActivity;
import test.grs.com.ims.view.XListView;

public class MessageActivity extends IMBaseActivity {


    public static boolean isReload = false;
    private TextView textView;
    private static String from;
    private TextView textView2;
    private boolean isture = true;
    // 给谁发送消息
    private String mCurrentRecipient;
    public int chatType = 0;
    private int convId = -1;
    private long guid;
    private XListView mListView;
    private EditText mEditTextContent;
    private Button buttonSend;
    private Button btnMore;
    private View more;
    private ImageView micImage;
    private TextView recordingHint;
    private TextView title, tv_groupNum;
    private String toUserName;
    public String avatarurl = "";//聊天对象
    private View recordingContainer;
    private View buttonSetModeVoice;
    private View buttonPressToSpeak;
    private RelativeLayout edittext_layout;
    private RelativeLayout expressionContainer;
    private LinearLayout btnContainer;
    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private View buttonSetModeKeyboard;
    private Drawable[] micImages;
    private PowerManager.WakeLock wakeLock;
    public final static int VOICE_REFRESH = 1;
    public final static int VOICE_TIP = 2;
    public final static int VOICE_LONG = 3;
    public final int SEND_FORWARD_MESSAGE = 99;
    private InputMethodManager manager;
    private boolean ischatType = false;
    private boolean notShowTip;
    private List<CMMessage> list = new ArrayList<CMMessage>();
    private List<CMMessage> templist = new ArrayList<CMMessage>();
    private MessageAdapter adapter;
    public static VoiceRecorder voiceRecorder;
    private DbUtils dbUtils;
    public static boolean isdelete = false;
    public static String mGroupName;
    private String groupName = "";
    private String groupId = "";
    private String groupMebAvatar = "";
    private String groupTitleName = "";
    private ArrayList<IMMember> IMMembers = new ArrayList<IMMember>();
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
        dbUtils = IMApp.geDbUtils();
        adapter = new MessageAdapter(list, MessageActivity.this);
        mListView.setAdapter(adapter);
        setLisener();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            // 单聊还是群聊
            ischatType = intent.getBooleanExtra(IMConst.CHATTYPE, false);
            Log.e("===TAG", ":" + ischatType);
            if (ischatType) {
                chatType = 1;//群聊
//                groupName = intent.getStringExtra(IMConst.USERNAME);// 对于群组 username 即群名字
                groupName = intent.getStringExtra(IMConst.GROUPNAME);// 对于群组 nickname 即群名字
                groupId = intent.getStringExtra(IMConst.GROUPID);
                groupMebAvatar = intent.getStringExtra(IMConst.GROUPAVATAR);// 整个成员的头像
                toUserName = groupId;//重新赋值为 群组到ID
                Log.e("==groupId=" + groupId + "groupName=" + groupName, "TAg" + groupMebAvatar);
                updateGroupMessageDates();
//                updateDatesGroupMembers();
                //设置聊天用户
                MessageHandle.getInstance().setCurrentRecipient(toUserName);
            } else {
                chatType = 0;//单聊
                guid = intent.getLongExtra(IMConst.GUID, 0l);
                toUserName = intent.getStringExtra(IMConst.USERNAME);
                nickName = intent.getStringExtra(IMConst.NICKNAME);
                avatarurl = intent.getStringExtra(IMConst.AVATARURL);
                title.setText(nickName + "");
                Log.e("==toUserName=" + toUserName + "avatarurl=" + avatarurl, "TAG");
                updateSingleMessageDates();
                //设置聊天用户
                MessageHandle.getInstance().setCurrentRecipient(toUserName);
            }

        }
    }

    /**
     * 更新 群成员
     */
    private void updateDatesGroupMembers() {
        IMMembers.clear();
        //获取群 ID 为 groupId 的群成员列表
        ArrayList<CMMember> members = CMIMHelper.getCmGroupManager().getGroupMembersfromServer(groupId);
        title.setText("" + groupName);
        if (members != null && members.size() > 0) {
//            title.setText("" + IMUtil.geMemeberString(members));
            tv_groupNum.setVisibility(View.VISIBLE);
            tv_groupNum.setText("(" + members.size() + ")");
        }
        if (groupName != null && !groupName.contains(",")) {
            tv_groupNum.setVisibility(View.GONE);
        }
        Log.e("==群成员列表members", "" + members);
        //转换－获得群成员
//        ArrayList<String> memeberList = IMUtil.geMemeberList(members);
//        Log.e("==群成员列表memeberList", "" + memeberList);
//        IMMembers = MessageHandle.getBDMembers(memeberList);
        if (members != null)
            for (int i = 0; i < members.size(); i++) {
                CMMember member = members.get(i);
                if (member.getMemberNick().contains(",")) {
                    String[] split = member.getMemberNick().split(",");
                    String nick = split[0];
                    String avtaval = null;
                    if (split.length > 1) {
                        avtaval = split[1];
                    }
                    IMMembers.add(new IMMember(avtaval + "", nick + "", member.getMemberId(), IMMember.TYPE_COMMON));
                } else {
                    IMMembers.add(new IMMember("", member.getMemberNick() + "", member.getMemberId(), IMMember.TYPE_COMMON));
                }
            }
        if (groupName == null || groupName.isEmpty() || groupName.equals(" ")) {
            //根据群 ID 为 groupId 的群名称
            groupName = CMIMHelper.getCmGroupManager().getGroupNamefromServer(groupId);
            title.setText(groupName + "");
        }
    }

    /**
     * 更新 单聊数据
     */
    protected void updateSingleMessageDates() {
        try {
            List<IMMessage> from = dbUtils.findAll(Selector.from(IMMessage.class).where("chatType", "=", 0).and(WhereBuilder.b("_from", "=", toUserName)).or(WhereBuilder.b("_to", "=", toUserName)).orderBy("id"));
            if (from != null && from.size() > 0) {
                list = IMUtil.getIMMessageToCMMessage(from, list);
            } else {
                CMMessage message = new CMMessage(MessageConstants.Conversation.TYPE_SINGLE, toUserName, new TextMessageBody("聊点什么吧！"));
                message.setContentType(MessageConstants.Message.TYPE_NOTIFY);
                message.setSendOrRecv(MessageConstants.Message.MSG_SEND);
                message.setTime(System.currentTimeMillis());
                list.add(message);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        adapter.setDatasChange(list);
        if (adapter.getCount() != 0)
            mListView.setSelection(adapter.getCount());
    }

    private void updateGroupMessageDates() {
        try {
            List<IMMessage> from = dbUtils.findAll(Selector.from(IMMessage.class).where("groupId", "=", groupId).orderBy("id"));
            if (from != null) {
                list = IMUtil.getIMMessageToCMMessage(from, list);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        adapter.setDatasChange(list);
        if (adapter.getCount() != 0)
            mListView.setSelection(adapter.getCount());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //设置当前接收者
        MessageHandle.getInstance().setCurrentRecipient(toUserName);
        //接收通知消息
        NotificationController.getInstance().init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //删除聊天记录
        if (isdelete) {
            list.clear();
            adapter.setDatasChange(list);
        }
        //更新 群组成员
        if (chatType == 1) {
            updateDatesGroupMembers();
            if (mGroupName != null) {
                groupName = mGroupName;
                title.setText("" + mGroupName);
                tv_groupNum.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "TAG");
        try {
            if (VoiceRecorder.isPlaying
                    && VoiceRecorder.currentPlayListener != null) {
                // 停止语音播放
                VoiceRecorder.currentPlayListener.stopPlayVoice();
            }
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                recordingContainer.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageHandle.getInstance().setCurrentRecipient(null);
        IMMembers = null;
        mGroupName = null;
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VOICE_LONG:
                    recordingHint.setText(R.string.msg_msg_voice_press_speak);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    int length = msg.arg1;
                    if (length > 0) {
                        sendMessageAudio(voiceRecorder.getVoiceFilePath(length),
                                length);
                    } else {
                        Toast.makeText(getApplicationContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case VOICE_REFRESH:
                    // 切换msg切换图片

                    if (msg.arg1 <= 1)
                        micImage.setImageDrawable(micImages[0]);
                    else
                        micImage.setImageDrawable(micImages[msg.arg1 >= 14 ? 14
                                : msg.arg1 - 1]);
                    break;
                case VOICE_TIP:
                    if (!recordingHint
                            .getText()
                            .toString()
                            .equals(getString(R.string.msg_msg_voice_do_cancel_send_2))
                            || !notShowTip) {
                        recordingHint.setText("您最多还可以说 " + msg.arg1 + " 秒");
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    break;
            }
        }
    };
    /**
     * 记录转发的消息
     **/
    public static ConcurrentHashMap<Integer, CMMessage> mForwardMessageMap = new ConcurrentHashMap<Integer, CMMessage>();
    public static CMMessage mForwardMessage = null;

    public void doForwardMessage(CMMessage message) {
        mForwardMessage = message;
        Intent intent = new Intent(MessageActivity.this,
                ContactsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(IMConst.CHOOSE_CONTACT_TYPE,//单聊
                ContactsActivity.ChooseContactType.SINGLE_CONTACT);
        bundle.putInt(IMConst.NAVIGATE_DESTINATION,//转发
                ContactsActivity.NavigateDestination.TO_FORWARD);
        intent.putExtras(bundle);
        startActivityForResult(intent, SEND_FORWARD_MESSAGE);
    }

    private void sendMessageAudio(String voiceFilePath, int length) {
        CMMessage message = new CMMessage(chatType, toUserName,
                new AudioMessageBody(new File(voiceFilePath), length));
        message.setGroupInfo(new GroupInfo(groupId));
        message.setContentType(MessageConstants.Message.TYPE_AUDIO);
        if (chatType == 1) {
            message.setFromNick(groupName);//群聊名
        } else {
            message.setFromNick(IMApp.getCurrentUserNick());//当前用户昵称
        }
        message.setAttribute("nickName", (String) (IMApp.getCurrentUserNick()));
        message.setFrom(toUserName);// 为了查找 更新列表
        message.setExtra(IMApp.getCurrentAvataUrl() + "," + IMApp.getCurrentUserNick());// 为了头像 更新列表
        //更新显示
        updateShowContent(message);
        sendMessage(message);
    }

    private void sendMessagePic(String filePath) {
        CMMessage message = new CMMessage(chatType, toUserName,
                new ImageMessageBody(new File(filePath)));
        message.setGroupInfo(new GroupInfo(groupId));
        message.setContentType(MessageConstants.Message.TYPE_PIC);
        if (chatType == 1) {
            message.setFromNick(groupName);//群聊名
        } else {
            message.setFromNick(IMApp.getCurrentUserNick());//当前用户昵称
        }
        message.setAttribute("nickName", (String) (IMApp.getCurrentUserNick()));
        message.setFrom(toUserName);// 为了查找 更新列表
        message.setExtra(IMApp.getCurrentAvataUrl() + "," + IMApp.getCurrentUserNick());// 为了头像 更新列表
        //更新显示
        updateShowContent(message);
        //发送到服务器
        sendMessage(message);
    }

    /**
     * 更新聊天显示
     *
     * @param message
     */
    private void updateShowContent(CMMessage message) {
        message.setSendOrRecv(MessageConstants.Message.MSG_SEND);
        message.setTime(getChatTime());
        current_time = 0;
        list.add(message);
        templist.add(message);
        adapter.setDatasChanges(list);
        mListView.setSelection(adapter.getCount() - 1);
    }

    /**
     * 发送到服务器
     *
     * @param message
     */
    public void sendMessage(CMMessage message) {
        //保存数据
        MessageHandle.getInstance().updateLocalDataBase(message);
        try {
            //更新消息列表
            if (chatType == 0) {//单聊
                IMContact im = dbUtils.findFirst(Selector.from(IMContact.class).where("userName", "=", message.getTo()));
                if (im == null) {//未关注的人
                    UNIMContact uim = dbUtils.findFirst(Selector.from(UNIMContact.class).where("userName", "=", message.getTo()));
                    if (uim != null) {
                        uim.setMessage(IMUtil.getContentType(message));
                        dbUtils.saveOrUpdate(uim);
                    } else {//查找－聊天
                        //相当有接受消息，创建条目
//                        message.setFromNick(nickName);//聊天昵称
                        MessageHandle.getInstance().doRecivedChatMessage(message, false);
                    }
                } else {
                    im.setMessage(IMUtil.getContentType(message));
                    dbUtils.saveOrUpdate(im);
                }
            } else if (chatType == 1) {//群聊
                IMContact im = dbUtils.findFirst(Selector.from(IMContact.class).where("groupId", "=", groupId));
                if (im != null) {
                    im.setMessage(IMUtil.getContentType(message));
                    dbUtils.saveOrUpdate(im);
                } else {
                    message.setFromNick(groupName);
                    MessageHandle.getInstance().doRecivedGroupChatMessage(message, false);
                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
        //发送服务器
        CMChatListener.CMCallBack callBack = new CMChatListener.CMCallBack() {
            @Override
            public void onSuccess(final CMMessage message) {
                Log.e("onSuccess", "TAG");
                //发送消息成功的处理
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter != null) {
                            adapter.updateProgress(message);
                            adapter.notifyDataSetChanged();
                        }
//                        Toast.makeText(MessageActivity.this, "发送成功", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(CMMessage message, String errorString) {
                //发送消息失败的处理
                Log.e("onError=" + errorString, "TAG");
                if (adapter != null) {
                    adapter.updateProgress(message);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onProgress(CMMessage message, int progress) {
                //发送消息过程中
                message.setProgress(progress);
                if (progress == CMMessage.PREPARED) {
                    //准备好了发送
                    Log.e("准备好了发送", "TAG");
                } else {
                    //发送进度状态更新
                    if (adapter != null) {
                        adapter.updateProgress(message);
                    }
                }
            }
        };
        CMIMHelper.getCmMessageManager().sendMessage(message, callBack);
    }

    private Timer mTimer;
    private long current_time;
    private boolean isFrist = true;

    public long getChatTime() {
        if (isFrist) {
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    current_time = System.currentTimeMillis();
                }
            };
            mTimer = new Timer();
            mTimer.schedule(task, 0, 5 * 30 * 1000);
            isFrist = false;
            return System.currentTimeMillis();
        }

        return current_time;
    }

    private void initView() {
        mListView = (XListView) this.findViewById(R.id.list_message);
        mListView.setPullRefreshEnable(false);
        mEditTextContent = (EditText) this.findViewById(R.id.et_sendmessage);//mEditTextContent
        buttonSend = (Button) this.findViewById(R.id.btn_send);
        btnMore = (Button) this.findViewById(R.id.btn_more);
        more = findViewById(R.id.more);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        title = (TextView) findViewById(R.id.title);
        tv_groupNum = (TextView) findViewById(R.id.tv_groupNum);
        recordingContainer = findViewById(R.id.recording_container);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        expressionContainer = (RelativeLayout) findViewById(R.id.ll_face_container);
        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        findViewById(R.id.show_single_detail).setVisibility(View.VISIBLE);
        edittext_layout.requestFocus();
        mEditTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout
                            .setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    edittext_layout
                            .setBackgroundResource(R.drawable.input_bar_bg_normal);
                }
            }
        });
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{
                getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04),
                getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06),
                getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08),
                getResources().getDrawable(R.drawable.record_animate_09),
                getResources().getDrawable(R.drawable.record_animate_10),
                getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12),
                getResources().getDrawable(R.drawable.record_animate_13),
                getResources().getDrawable(R.drawable.record_animate_14),};
        // 初始化表情
        EmojiManager.initEmojiGrid(this, findViewById(R.id.ll_face_container),
                mEditTextContent, 0);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cmchat");
        voiceRecorder = new VoiceRecorder(MessageActivity.this, mHandler);
        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
        mListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();//点击隐藏
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                expressionContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                return false;
            }
        });
        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                Log.e("TAG", "onRefresh");
                loadHistoryMessage();
            }

            @Override
            public void onLoadMore() {
                Log.e("TAG", "onLoadMore");
            }
        });
    }

    //加载历史记录
    private void loadHistoryMessage() {

    }

    private void setLisener() {
        // 当前用户聊天界面更新消息
        MessageHandle.getInstance().setOnRecivedToActivityListener(new MessageHandle.OnRecivedToActivityListener() {
            @Override
            public void onSelected(final int type) {
                NotificationController.getInstance().init();//取消通知显示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (type == 1) {
                            Log.e("onSelected", "TAG=" + type);
                            updateSingleMessageDates();
                        } else if (type == 2) {
                            updateGroupMessageDates();
                        }
                    }
                });
            }
        });
        mEditTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("onTextChanged", "TAG");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("afterTextChanged", "TAG");
                if (editable != null && editable.length() > 0) {
                    buttonSend.setVisibility(View.VISIBLE);
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setEnabled(true);
                } else {
                    buttonSend.setEnabled(false);
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }

            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mEditTextContent.getText().toString();
                Log.e(text, "text TAG");
                if (text != null && !text.isEmpty()) {
                    if (toUserName != null || groupId != null) {
                        CMMessage message = new CMMessage(chatType, toUserName, new TextMessageBody(text));//text为文本内容，String类型，不能为空
                        message.setGroupInfo(new GroupInfo(groupId + ""));
                        message.setContentType(MessageConstants.Message.TYPE_TEXT);
                        if (chatType == 1) {
                            message.setFromNick(groupName);//群聊名
                        } else {
                            message.setFromNick(IMApp.getCurrentUserNick());//当前用户昵称
                        }
                        message.setFrom(toUserName);// 为了查找 更新列表
                        message.setExtra(IMApp.getCurrentAvataUrl() + "," + IMApp.getCurrentUserNick());// 为了头像 更新列表
//                        message.setExtra(IMApp.getCurrentUserNick() + "");// 为了头像 更新列表

                        mEditTextContent.setText("");
                        //更新显示
                        updateShowContent(message);
                        sendMessage(message);
                    } else {
                        Toast.makeText(MessageActivity.this, "自动登录超时,无法发送", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MessageActivity.this, "请输入内容", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * @方法名：setModeVoice
     * @描述：显示语音按钮
     */
    public void setModeVoice(View view) {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        btnMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        btnContainer.setVisibility(View.VISIBLE);
        expressionContainer.setVisibility(View.GONE);

    }

    /**
     * 显示或隐藏 更多中的图标按钮页面
     */
    public void setModeMore(View view) {
        if (more.getVisibility() == View.GONE) {
            hideKeyboard();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    more.setVisibility(View.VISIBLE);
                    btnContainer.setVisibility(View.VISIBLE);
                }
            }, 100);
            expressionContainer.setVisibility(View.GONE);
        } else {
            if (expressionContainer.getVisibility() == View.VISIBLE) {
                expressionContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }

        }

    }

    /**
     * @方法名：editClick
     * @描述：点击文字输入框
     */
    public void setEditClicked(View v) {

        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        }

        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
        more.setVisibility(View.GONE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        expressionContainer.setVisibility(View.GONE);
        btnContainer.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        mListView.setSelection(adapter.getCount() - 1);

    }

    /**
     * @方法名：setModeEmoticonsChecked
     * @描述：显示表情页面
     */
    public void setModeEmoticonsNormal(View v) {
        Log.e("setModeEmoticonsNormal", "TAG");
        iv_emoticons_normal.setVisibility(View.INVISIBLE);
        iv_emoticons_checked.setVisibility(View.VISIBLE);
        hideKeyboard();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                btnContainer.setVisibility(View.GONE);
                more.setVisibility(View.VISIBLE);
                expressionContainer.setVisibility(View.VISIBLE);
            }
        }, 100);
    }

    /**
     * @方法名：setModeEmoticonsNormal
     * @描述：隐藏表情页面
     */
    public void setModeEmoticonsChecked(View v) {
        Log.e("setModeEmoticonsChecked", "TAG");
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        btnContainer.setVisibility(View.VISIBLE);
        expressionContainer.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
    }

    /**
     * @方法名：hideKeyboard
     * @描述：隐藏软键盘
     */
    private void hideKeyboard() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * @方法名：setModeKeyboard
     * @描述：显示键盘图标
     */
    public void setModeKeyboard(View view) {

        edittext_layout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);

        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }


    /**
     * @方法名：doSelectPicture
     * @描述：选择本地图片
     */
    public static final int REQUEST_CODE_PIC_LOCAL = 19;

    public void doSelectPicture(View v) {
        Intent intent;
        if (Build.VERSION.SDK_INT <= 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_PIC_LOCAL);
    }

    /**
     * @方法名：doTakePicture
     * @描述：拍照
     */
    private File cameraFile;
    public static final int REQUEST_CODE_PIC_CAMERA = 18;

    public void doTakePicture(View v) {
        if (!CommonUtils.isExitsSdcard()) {
            Toast.makeText(getApplicationContext(), "SD卡不存在，不能拍照", Toast.LENGTH_LONG).show();
            return;
        }

        cameraFile = new File(IMConst.GLOBALSTORAGE_DOWNLOAD_PATH,
                System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_PIC_CAMERA);
    }


    /**
     * @方法名：getRealPathFromURI
     * @描述：获取图片的绝对路径 将虚拟路径转换真是路径： content://media/external/images/.... －－》/storage/emulated/0/360Browser/download/t0193c82230e91cdfa0.jpg
     */

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null,
                null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }
        return res;
    }

    public String getDataColumn(Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMConst.REQUEST_CODE_FINISH:
                finish();
                break;
            case REQUEST_CODE_PIC_LOCAL:// 发送本机图片
                if (data == null)
                    return;
                Uri selectedImage = data.getData();
                Log.e("TAG-selectedImage", selectedImage.toString());
                if (selectedImage != null) {
                    try {
                        String realSelectedPath;
                        if (selectedImage.toString().contains("com.android.providers.media.documents")) {//4.4 最近打开
                            String docId = DocumentsContract.getDocumentId(selectedImage);
                            String[] split = docId.split(":");
                            String type = split[0];
                            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            String selection = "_id=?";
                            String[] selectionArgs = new String[]{
                                    split[1]
                            };
                            Log.e("=docId:" + docId + "type:" + type, "contentUri:" + contentUri);
                            realSelectedPath = getDataColumn(contentUri, selection, selectionArgs);
                        } else if (selectedImage.toString().contains("file://")) {//小米图库
                            realSelectedPath = selectedImage.toString().replace("file://", "").trim();
                        } else {
                            realSelectedPath = getRealPathFromURI(selectedImage);//一般图库
                        }
                        String localFilePath = IMConst.GLOBALSTORAGE_DOWNLOAD_PATH + System.currentTimeMillis() + ".jpeg";
                        Log.e("TAG-realSelectedPath", realSelectedPath + " ");
                        CompressPicUtil.compressImage(MessageActivity.this,
                                realSelectedPath, localFilePath, 100);

                        sendMessagePic(localFilePath);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                break;
            case REQUEST_CODE_PIC_CAMERA:// 发送拍照后的图片
                if (cameraFile != null && cameraFile.exists()) {
                    Log.e("TAG-cameraFile", cameraFile.toString() + "=");
                    String localFilePath = IMConst.GLOBALSTORAGE_DOWNLOAD_PATH
                            + System.currentTimeMillis() + ".jpeg";
                    String filePath = cameraFile.getAbsolutePath();
                    Log.e("TAG-filePath", filePath);
                    try {
                        long length = CompressPicUtil.compressImage(
                                MessageActivity.this, filePath, localFilePath, 100);
                        sendMessagePic(localFilePath);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case SEND_FORWARD_MESSAGE://转发
                if (resultCode == IMConst.SINGLE_RESULT_CODE) {
                    String toUsername = data.getStringExtra(IMConst.SINGLE_RESULT_USER_NAME);
                    String toNickname = data.getStringExtra(IMConst.SINGLE_RESULT_USER_NICK);
//                    mForwardMessage.setFrom(IMApp.getInstance().getCurrentUserName());
                    mForwardMessage.setFrom(toUsername);
                    mForwardMessage.setFromNick(toNickname);
                    mForwardMessage.setAttribute("nickName", IMApp.getCurrentUserNick());
                    mForwardMessage.setTo(toUsername);
                    mForwardMessage.setTime(System.currentTimeMillis());
                    sendMessage(mForwardMessage);
                    break;
                }
        }
    }

    /**
     * 返回  c h
     */
    public void doBack(View v) {
        finish();
    }

    /**
     * app字体不随系统字体变化
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * @方法名：showSingleDetail
     * @描述：(显示单聊会话详情页面)
     */

    public void showSingleDetail(View v) {
        Intent intent = new Intent(MessageActivity.this,
                ChatSettingActivity.class);
        intent.putExtra(IMConst.NICKNAME, nickName);
        intent.putExtra(IMConst.AVATARURL, avatarurl);//当前聊天头像
        intent.putExtra(IMConst.USERNAME, toUserName);//单聊是用户，群聊是ID
        intent.putExtra(IMConst.CHATTYPE, chatType);
        intent.putExtra(IMConst.GROUPNAME, groupName);
        intent.putExtra(IMConst.GROUPID, groupId);
        Log.e("TAG" + nickName, ",USERNAME=" + toUserName + ",CHATTYPE=" + chatType + ",GROUPNAME=" + groupName + ",GROUPID=" + groupId);
        if (chatType == 1 && IMMembers != null && IMMembers.size() > 0) {
            intent.putExtra(IMConst.GROUPMEMEBER, IMMembers);
            startActivityForResult(intent, 1234);
            IMMembers.clear();
        } else if (chatType == 0) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "获取网络失败，无法跳转", Toast.LENGTH_SHORT).show();
        }
    }

    private class PressToSpeakListen implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.isExitsSdcard()) {
                        Toast.makeText(MessageActivity.this, "发送语音需要sdcard支持！",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoiceRecorder.isPlaying)
                            VoiceRecorder.currentPlayListener.stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint
                                .setText(getString(R.string.msg_msg_voice_do_cancel_send_1));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(null, getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(MessageActivity.this, "录音失败！",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    v.setPressed(true);
                    if (event.getY() < 0) {
                        // 上滑
                        notShowTip = true;
                        recordingHint
                                .setText(getString(R.string.msg_msg_voice_do_cancel_send_2));
                        recordingHint
                                .setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        notShowTip = false;
                        if (VoiceRecorder.MAX_DURATION
                                - voiceRecorder.voice_duration > VoiceRecorder.TIME_TO_COUNT_DOWN) {
                            recordingHint
                                    .setText(getString(R.string.msg_msg_voice_do_cancel_send_1));
                            recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    if (recordingContainer.getVisibility() == View.VISIBLE) {
                        recordingContainer.setVisibility(View.INVISIBLE);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (event.getY() < 0) {
                            // discard the recorded audio.
                            voiceRecorder.discardRecording();
                        } else {
                            // stop recording and send voice file
                            try {
                                int length = voiceRecorder.stopRecoding();
                                if (length > 0) {
                                    sendMessageAudio(voiceRecorder.getVoiceFilePath(length),
                                            length);
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "录音时间太短", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                Toast.makeText(MessageActivity.this,
                                        "语音文件格式错误，目前支持mp3、amr格式",
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                // TODO: handle exception
                                e.printStackTrace();
                            }

                        }
                    }
                    return true;
            }
            return false;
        }
    }
}
