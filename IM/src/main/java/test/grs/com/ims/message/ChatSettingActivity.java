package test.grs.com.ims.message;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.littlec.sdk.business.MessageConstants;
import com.littlec.sdk.entity.CMGroup;
import com.littlec.sdk.entity.CMMember;
import com.littlec.sdk.manager.CMIMHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.contact.GroupContactsActivity;
import test.grs.com.ims.util.model.QHUserInfoLists;
import test.grs.com.ims.view.WrapGridView;

/**
 * Created by gaoruishan on 15/9/10.
 */
public class ChatSettingActivity extends IMBaseActivity {
    private TextView titleTextView, tv_groupName, tv_groupMemeber;
    private String address;
    private int convId;
    private WrapGridView membersGridView;
    private ToggleButton ignoreGroupMessageToggleButton;
    private TextView exit_button;
    private int chatType = 0;
    private IMContact currentUser;
    private ArrayList<IMMember> membersList;
    private ArrayList<String> newListName = new ArrayList<String>();
    private ChatMemberAdapter memberAdapter;
    private String name;
    private String username;
    private DbUtils dbUtils;
    private ToggleButton top_message_toggleButton, save_to_toggleButton;
    private LinearLayout ll_msg_unnotice, ll_msg_center, ll_grp_center, ll_grp_center1;
    private ArrayList<IMMember> mIMMembers;
//    private ArrayList<IMMember> tempIMMembers = new ArrayList<IMMember>();
    private String groupName, groupId;
    private ProgressDialog progressDialog;
    public String currenOwner;
    public boolean isOwner;
    public boolean isAdd = false;
    private RelativeLayout rl_save_to;
    private String nickname, avatarUrl;
    private boolean backSuccess;
    private ArrayList<IMMember> tempList = new ArrayList<IMMember>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        dbUtils = IMApp.geDbUtils();
        //单聊是用户名，群聊是群ID
        username = getIntent().getStringExtra(IMConst.USERNAME);
        nickname = getIntent().getStringExtra(IMConst.NICKNAME);
        avatarUrl = getIntent().getStringExtra(IMConst.AVATARURL);
        chatType = getIntent().getIntExtra(IMConst.CHATTYPE, MessageConstants.Conversation.TYPE_SINGLE);
        if (chatType == 1) {
            groupName = getIntent().getStringExtra(IMConst.GROUPNAME);
            groupId = getIntent().getStringExtra(IMConst.GROUPID);
            mIMMembers = getIntent().getParcelableArrayListExtra(IMConst.GROUPMEMEBER);
            String userIds ="";
            for (int i = 0; i < mIMMembers.size(); i++) {
                IMMember member = mIMMembers.get(i);
                if (i==mIMMembers.size()-1){
                    userIds += member.getUserName();
                }else {
                    userIds +=member.getUserName()+",";
                }
            }
            // 加载头像
            sendBroadcast(new Intent(IMConst.ACTION_GET_USERLIST).putExtra(IMConst.USER_ID, userIds));
            MessageHandle.getInstance().setOnRecivedMessageListener1(new MessageHandle.OnRecivedMessageListeners() {
                @Override
                public void onBackSuccess(QHUserInfoLists user) {
                    if (user == null || user.getResults() == null) {
                        return;
                    }
                    for (int i = 0; i < mIMMembers.size(); i++) {
                        IMMember imMember = mIMMembers.get(i);
                        for (QHUserInfoLists.ResultsEntity entity : user.getResults()) {
                            if ((entity.getId() + "").equals(imMember.getUserName())) {
                                imMember.setAvatarUri(entity.getAvatar_url());
                                imMember.setName(entity.getNick_name());
                                break;
                            }
                        }
                    }
//                    for (int i = 0; i < user.getResults().size(); i++) {
//                        IMMember imMember = mIMMembers.get(i);
//                        QHUserInfoLists.ResultsEntity entity = user.getResults().get(i);
//                        if ((entity.getId() + "").equals(imMember.getUserName())) {
//                            imMember.setAvatarUri(entity.getAvatar_url());
//                        }
//                    }
                    if (memberAdapter == null) {
                        memberAdapter = new ChatMemberAdapter(ChatSettingActivity.this, new ArrayList<IMMember>(), groupId);
                    } else {
                        memberAdapter.setDatasChanged(mIMMembers);
                        tempList.addAll(mIMMembers);
                    }
                    backSuccess = true;
                }
            });
//            mIMMembers = (ArrayList<IMMember>) getIntent().getSerializableExtra(IMConst.GROUPMEMEBER);
        }
        initView();
        setListenr();
        initMembers();
        Log.e("username=" + username + "," + nickname, "chatType=" + chatType + ",mIMMembers=" + mIMMembers);
    }

    private void initMembers() {
        titleTextView.setText("聊天设置");
        membersList = new ArrayList<IMMember>();
        if (chatType == MessageConstants.Conversation.TYPE_SINGLE) {
            try {
                IMContact currentUser = dbUtils.findFirst(Selector.from(IMContact.class).where("userName", "=", username));
                if (currentUser == null) {//未关注
                    UNIMContact ucurrentUser = dbUtils.findFirst(Selector.from(UNIMContact.class).where("userName", "=", username));
                    if (ucurrentUser != null) {
                        initSetting(ucurrentUser.isTop(), ucurrentUser.isIgnore(), nickname + "", ucurrentUser.getUserName() + "");
                    } else {//默认
                        initSetting(false, false, nickname, username);
                    }
                } else if (currentUser != null) {//关注的人
                    initSetting(currentUser.isTop(), currentUser.isIgnore(), nickname + "", currentUser.getUserName() + "");
                }
            } catch (DbException e) {
                e.printStackTrace();
            }

        } else {
            if (chatType == MessageConstants.Conversation.TYPE_GROUP) {
                mIMMembers.add(new IMMember("", "添加", "",
                        IMMember.TYPE_ADD));
                //根据群 ID 获取群主
                CMMember owner = CMIMHelper.getCmGroupManager().getGroupOwnerfromServer(groupId);
//                Log.e("id=" + owner.getMemberId() + "=nick=" + owner.getMemberNick(), "TAG");
                currenOwner = owner.getMemberId();
                if (IMApp.currentUserName.equals(currenOwner)) {
                    exit_button.setText("解散该群");
                    isOwner = true;
                    mIMMembers.add(new IMMember("", "移除", "",
                            IMMember.TYPE_REMOVE));
                }
                //获取群成员设置
                try {
                    IMContact currentGrp = dbUtils.findFirst(Selector.from(IMContact.class).where("groupId", "=", username));
                    if (currentGrp != null) {
                        //设置开关
                        top_message_toggleButton.setChecked(currentGrp.isTop());
                        ignoreGroupMessageToggleButton.setChecked(currentGrp.isIgnore());
                        save_to_toggleButton.setChecked(currentGrp.isSave());
                    }else {
                        save_to_toggleButton.setChecked(true);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                memberAdapter = new ChatMemberAdapter(this, mIMMembers, groupId);
                membersGridView.setAdapter(memberAdapter);
            }
        }

    }

    /**
     * 初始化设置
     *
     * @param top
     * @param checked
     * @param nickName
     * @param userName
     */
    private void initSetting(boolean top, boolean checked, String nickName, String userName) {
        top_message_toggleButton.setChecked(top);
        ignoreGroupMessageToggleButton.setChecked(checked);
        membersList.add(new IMMember(avatarUrl, nickName + "", userName + "", IMMember.TYPE_COMMON));
        membersList.add(new IMMember("", "添加", "",
                IMMember.TYPE_ADD));
        memberAdapter = new ChatMemberAdapter(this, membersList, groupId);
        membersGridView.setAdapter(memberAdapter);
    }
    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }
    public static boolean StringFilter(String str)throws PatternSyntaxException {
//        String regEx = "[/\\:*?<>|\"\n\t]"; //要过滤掉的字符
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
    /**
     * 点击群名
     */
    public void doGroupName(View view) {
        //修改群名字
        final Dialog dialog = new Dialog(this,
                R.style.Theme_CustomDialog);
        View dialogview = LayoutInflater.from(this)
                .inflate(R.layout.dialog_common_edit, null);
        final EditText et_groupName = (EditText) dialogview
                .findViewById(R.id.et_msg);
        dialog.setContentView(dialogview);
        TextView titleView = (TextView) dialogview
                .findViewById(R.id.dialog_title);
        titleView.setText("修改群名称");
        et_groupName.setText(groupName + "");
        TextView okButton = (TextView) dialogview
                .findViewById(R.id.dialog_ok_btn);
        TextView cancelButton = (TextView) dialogview
                .findViewById(R.id.dialog_cancel_btn);
        dialog.show();
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String groupName = et_groupName.getText().toString();
                dialog.dismiss();
                if (groupName.contains(",")||StringFilter(groupName)) {
                    Toast.makeText(ChatSettingActivity.this, "群名称不能全为空或者特殊字符",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isEmpty(groupName)) {
                    Toast.makeText(ChatSettingActivity.this, "群名称不能全为空或者特殊字符",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
//                String str = "^[0-9].*";
                Pattern p = Pattern.compile("^[0-9].*");
                Matcher m = p.matcher(groupName);
                if (m.matches()) {
                    Toast.makeText(ChatSettingActivity.this, "不能以数字开头",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (groupName.length() > 50 || groupName.length() < 4) {
                    Toast.makeText(ChatSettingActivity.this, "群名称要求4-50个字符",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new ProgressDialog(
                        ChatSettingActivity.this);
                progressDialog.setMessage("正在修改群名称...");
                progressDialog.show();
                try {
                    CMIMHelper.getCmGroupManager().changeGroupName(groupId + "", groupName + "");
                    //更新
                    tv_groupName.setText(groupName + "");
                    MessageHandle.getInstance().updateGroupName(groupId, groupName);
                    MessageActivity.mGroupName = groupName;//赋值
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == IMConst.ADD_RESULTT_CODE) {//添加成员返回更新
            if (data == null) {
                return;
            }
            ArrayList<String> list = data.getStringArrayListExtra(IMConst.STRINGLIST);
            ArrayList<IMMember> members = MessageHandle.getInstance().getBDMembers(list);
            mIMMembers.remove(mIMMembers.size() - 1);
            if (isOwner) {
                mIMMembers.remove(mIMMembers.size() - 1);
            }
            mIMMembers.addAll(members);

            mIMMembers.add(new IMMember("", "添加", "",
                    IMMember.TYPE_ADD));
            tv_groupMemeber.setText(mIMMembers.size() - 1 + "");
            if (isOwner) {
                mIMMembers.add(new IMMember("", "移除", "",
                        IMMember.TYPE_REMOVE));
                tv_groupMemeber.setText(mIMMembers.size() - 2 + "");
            }
            memberAdapter.setDatasChanged(mIMMembers);
            tempList.clear();
            tempList.addAll(mIMMembers);

        } else if (resultCode == IMConst.REMOVER_RESULTT_CODE) {//移除成员返回更新
            if (data == null) {
                return;
            }
            ArrayList<String> list = data.getStringArrayListExtra(IMConst.STRINGLIST);
            for (String s : list) {
                for (int i = 0; i < tempList.size(); i++) {
                    if (tempList.get(i).getUserName().equals(s)){
                        tempList.remove(i);
                        break;
                    }
                }
            }
//            ArrayList<IMMember> members = MessageHandle.getInstance().getBDMembers(list);
//            for (IMMember gm:members) {
//                String userName = gm.getUserName();
//                for (int i = 0; i < mIMMembers.size(); i++) {
//                    if (mIMMembers.get(i).getUserName().equals(userName)){
//                        mIMMembers.remove(i);
//                        break;
//                    }
//                }
//            }
            tv_groupMemeber.setText(tempList.size() - 2 + "");
//            if (isOwner) {
//                tv_groupMemeber.setText(mIMMembers.size() - 2 + "");
//            }
            memberAdapter.setDatasChanged(tempList);
            mIMMembers.clear();
            mIMMembers.addAll(tempList);
        }

    }

    /**
     * 点击群成员
     */
    public void doGroupMemeber(View view) {
        //查看好友 通讯录模式
        Intent i = new Intent(ChatSettingActivity.this, GroupContactsActivity.class);
        i.putExtra(IMConst.GROUPMEMEBER, mIMMembers);
        i.putExtra(IMConst.GROUPREMOVE, true);//只是查看成员
        i.putExtra(IMConst.OTHER, false);//移除的标识
        i.putExtra(IMConst.OWNER,isOwner);
        if (backSuccess&&mIMMembers.size() > 0)//获得头像后再跳转
            startActivity(i);
    }

    private void setListenr() {
        //屏蔽消息
        ignoreGroupMessageToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chatType == MessageConstants.Conversation.TYPE_SINGLE) {// 单聊
                    try {
                        IMContact currentUser = dbUtils.findFirst(Selector.from(IMContact.class).where("userName", "=", username));
                        if (currentUser == null) {
                            UNIMContact ucurrentUser = dbUtils.findFirst(Selector.from(UNIMContact.class).where("userName", "=", username));
                            if (ucurrentUser != null) {
                                ucurrentUser.setIgnore(isChecked);
                                dbUtils.saveOrUpdate(ucurrentUser);
                            }
                        } else {
                            currentUser.setIgnore(isChecked);
                            dbUtils.saveOrUpdate(currentUser);
                        }
                        Log.e("屏蔽消息", "TAG");
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else if (chatType == MessageConstants.Conversation.TYPE_GROUP) {//群聊
                    try {
                        IMContact currentGrp = dbUtils.findFirst(Selector.from(IMContact.class).where("groupId", "=", username));
                        if (currentGrp != null) {
                            currentGrp.setIgnore(isChecked);
                            dbUtils.saveOrUpdate(currentGrp);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        //置顶
        top_message_toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chatType == MessageConstants.Conversation.TYPE_SINGLE) {//单聊
                    try {
                        IMContact currentUser = dbUtils.findFirst(Selector.from(IMContact.class).where("userName", "=", username));
                        if (currentUser == null) {//未关注
                            UNIMContact ucurrentUser = dbUtils.findFirst(Selector.from(UNIMContact.class).where("userName", "=", username));
                            if (ucurrentUser != null) {
                                ucurrentUser.setTop(isChecked);
                                dbUtils.deleteById(UNIMContact.class, ucurrentUser.getId());
                                List<UNIMContact> uall = dbUtils.findAll(UNIMContact.class);
                                if (isChecked) {
                                    uall.add(0, ucurrentUser);
                                } else {
                                    uall.add(ucurrentUser);
                                }
                                Log.e("置顶=" + isChecked, "TAG");
                                dbUtils.deleteAll(UNIMContact.class);
                                dbUtils.saveAll(uall);
                            } else {
                                Toast.makeText(ChatSettingActivity.this, "消息列表无记录，置顶失败！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            currentUser.setTop(isChecked);
                            dbUtils.deleteById(IMContact.class, currentUser.getId());
                            List<IMContact> all = dbUtils.findAll(IMContact.class);
                            if (isChecked) {
                                all.add(0, currentUser);
                            } else {
                                all.add(currentUser);
                            }
                            dbUtils.deleteAll(IMContact.class);
                            dbUtils.saveAll(all);
                        }

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else if (chatType == MessageConstants.Conversation.TYPE_GROUP) {//群聊
                    try {
                        IMContact currentGrp = dbUtils.findFirst(Selector.from(IMContact.class).where("groupId", "=", username));
                        if (currentGrp != null) {
                            currentGrp.setTop(isChecked);
                            dbUtils.deleteById(IMContact.class, currentGrp.getId());
                            List<IMContact> all = dbUtils.findAll(IMContact.class);
                            if (isChecked) {
                                all.add(0, currentGrp);
                            } else {
                                all.add(currentGrp);
                            }
                            dbUtils.deleteAll(IMContact.class);
                            dbUtils.saveAll(all);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        //保存到群聊消息列表
        save_to_toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chatType == MessageConstants.Conversation.TYPE_GROUP) {//群聊
                    try {
                        //保存消息列表
                        IMContact imcurrentGrp = dbUtils.findFirst(Selector.from(IMContact.class).where("groupId", "=", username));
                        if (imcurrentGrp != null) {
                            imcurrentGrp.setSave(isChecked);
                            dbUtils.saveOrUpdate(imcurrentGrp);
                            Log.e("保存消息列表=" + isChecked, "TAG");
                        }
                        //修改群组－
                        IMGroup currentGrp = dbUtils.findFirst(Selector.from(IMGroup.class).where("groupId", "=", username));
                        if (currentGrp != null) {
                            currentGrp.setIsPublic(isChecked);
                            dbUtils.saveOrUpdate(currentGrp);
                            Log.e("保存消息列表=2" + isChecked, "TAG");
                        }else {
                            IMGroup g = IMUtil.getCMGroupToIMGroup(new CMGroup(groupId, groupName));
                            g.setIsPublic(isChecked);
                            dbUtils.save(g);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void initView() {
        titleTextView = (TextView) findViewById(R.id.title);
        tv_groupName = (TextView) findViewById(R.id.tv_groupName);
        tv_groupMemeber = (TextView) findViewById(R.id.tv_groupMemeber);
        ll_msg_unnotice = (LinearLayout) findViewById(R.id.ll_msg_unnotice);
        ll_msg_center = (LinearLayout) findViewById(R.id.ll_msg_center);
        ll_grp_center = (LinearLayout) findViewById(R.id.ll_grp_center);
        rl_save_to = (RelativeLayout) findViewById(R.id.rl_save_to);
        ll_grp_center1 = (LinearLayout) findViewById(R.id.ll_grp_center1);
        titleTextView = (TextView) findViewById(R.id.title);
        membersGridView = (WrapGridView) findViewById(R.id.gridview);
        membersGridView.setExpanded(true);
        ignoreGroupMessageToggleButton = (ToggleButton) findViewById(R.id.ignore_message_toggleButton);
        top_message_toggleButton = (ToggleButton) findViewById(R.id.top_message_toggleButton);
        save_to_toggleButton = (ToggleButton) findViewById(R.id.save_to_toggleButton);
        exit_button = (TextView) findViewById(R.id.exit_button);
        if (chatType == MessageConstants.Conversation.TYPE_SINGLE) {
            exit_button.setText(getString(R.string.del_contact));
            titleTextView.setText("对话详情");
        }
        if (chatType == 1) {
            ll_grp_center.setVisibility(View.VISIBLE);
            ll_grp_center1.setVisibility(View.VISIBLE);
            rl_save_to.setVisibility(View.VISIBLE);
            exit_button.setText("退出该群");
//            tv_groupName.setText(IMUtil.getIMMemeberString(mIMMembers) + "");
            tv_groupName.setText(groupName + "");
            tv_groupMemeber.setText(mIMMembers.size() + "");
        }
    }

    public void doBack(View v) {
        finish();
    }

    /**
     * 删除聊天记录
     *
     * @param v
     */
    public void doDeleteChatData(View v) {

        View.OnClickListener okBtnEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (chatType == MessageConstants.Conversation.TYPE_SINGLE) {
                        List<IMMessage> from = dbUtils.findAll(Selector.from(IMMessage.class).where("_from", "=", username).or(WhereBuilder.b("_to", "=", username)).orderBy("id"));
                        if (from != null) {
                            dbUtils.deleteAll(from);
                        }
                        IMContact cmContact = dbUtils.findFirst(Selector.from(IMContact.class).where("userName", "=", username));
                        if (cmContact != null) {
                            cmContact.setMessage("");
                            dbUtils.saveOrUpdate(cmContact);
                        }
                    } else if (chatType == MessageConstants.Conversation.TYPE_GROUP) {
                        dbUtils.delete(IMMessage.class, WhereBuilder.b("groupId", "=", username));
                        IMContact cmContact = dbUtils.findFirst(Selector.from(IMContact.class).where("groupId", "=", username));
                        if (cmContact != null) {
                            cmContact.setMessage("");
                            dbUtils.saveOrUpdate(cmContact);
                        }
                    }
                    MessageActivity.isdelete = true;
                } catch (DbException e) {
                    e.printStackTrace();
                }
//
            }
        };
        DialogFactory.getConfirmDialog(ChatSettingActivity.this, getResources().getString(R.string.del_chat_data),
                getResources().getString(R.string.dialog_clear_message_prompt_content), getResources().getString(R.string.btn_subject_cancel),
                getResources().getString(R.string.btn_subject_confirm), null, okBtnEvent).show();
    }

    /**
     * 加入黑名单or 退出该群
     *
     * @param v
     */
    public void doAddBlackList(View v) {
        View.OnClickListener okBtnEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (chatType == MessageConstants.Conversation.TYPE_SINGLE) {
                        IMContact cmContact = dbUtils.findFirst(Selector.from(IMContact.class).where("userName", "=", username));
                        if (cmContact == null) {
                            UNIMContact ucmContact = dbUtils.findFirst(Selector.from(UNIMContact.class).where("userName", "=", username));
                            if (ucmContact != null) {
                                ucmContact.setBlackList(true);
                                dbUtils.saveOrUpdate(ucmContact);
                            }
                        } else {
                            cmContact.setBlackList(true);
                            dbUtils.saveOrUpdate(cmContact);
                        }
                        //发送广播 加入黑名单
                        sendBroadcast(new Intent(IMConst.ACTION_ADD_BLACKLIST).putExtra("userId", username));
                    } else if (chatType == MessageConstants.Conversation.TYPE_GROUP) {
                        IMContact cmContact = dbUtils.findFirst(Selector.from(IMContact.class).where("groupId", "=", username));
                        if (cmContact != null) {
                            cmContact.setBlackList(true);
                            dbUtils.delete(cmContact);//删除
                        }
                        //退出群
                        if (!isOwner) {
                            try {
                                //退出指定群 ID 的群
                                CMIMHelper.getCmGroupManager().exitGroup(groupId);
                                Log.e("退出群", "TAG");
                            } catch (Exception e) {
                                //退出群过程中出现了异常
                                e.printStackTrace();
                            }
                        } else {
                            //解散
                            try {
                                //解散指定群 ID 的群
                                CMIMHelper.getCmGroupManager().destroyGroup(groupId);
                                Log.e("解散群", "TAG");
                            } catch (Exception e) {
                                // 解散群过程中出现了异常
                                e.printStackTrace();
                            }
                        }
                        //更新群数
                        sendBroadcast(new Intent(IMConst.ACTION_ONLY_GROUP));
                        Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
                        notificationIntent.setClass(ChatSettingActivity.this, MessageListActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(notificationIntent);
                        setResult(IMConst.REQUEST_CODE_FINISH);
                        finish();//结束
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        };
        if (chatType == 0) {
            DialogFactory.getConfirmDialog(ChatSettingActivity.this, getResources().getString(R.string.del_contact),
                    getResources().getString(R.string.dialog_clear_contact), getResources().getString(R.string.btn_subject_cancel),
                    getResources().getString(R.string.btn_subject_confirm), null, okBtnEvent).show();

        } else if (chatType == 1) {
            DialogFactory.getConfirmDialog(ChatSettingActivity.this, getResources().getString(R.string.exit_group),
                    getResources().getString(R.string.dialog_quit_group_prompt_content), getResources().getString(R.string.btn_subject_cancel),
                    getResources().getString(R.string.btn_subject_confirm), null, okBtnEvent).show();

        }

    }


//    @Override
//    public void onContactChanged() {
//        if (chatType == MessageConstants.Conversation.TYPE_GROUP) {
////            currentGroup = getGroupByAddress(address);
////            initMembers();
//        } else {
////            currentUser = IMApp.getInstance().getUserByUsername(address);
//            initMembers();
//        }
//    }
//
//    @Override
//    public void onContactChanged(List<SortModel> list) {
//
//    }
//    public static OnUpdateListener mListener;
//
//    public  void setUpdateListener(OnUpdateListener mListener) {
//        this.mListener = mListener;
//    }
//    public interface OnUpdateListener {
//        void onUpdateDates(int var, String var1);
//    }
//
//    public static ChatSettingActivity getInstance (){
//        if (instance == null){
//            instance = new ChatSettingActivity();
//        }
//      return  instance;
//    }
}
