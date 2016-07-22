package test.grs.com.ims.message;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.littlec.sdk.business.MessageConstants;
import com.littlec.sdk.entity.CMGroup;
import com.littlec.sdk.entity.CMMember;
import com.littlec.sdk.entity.CMMessage;
import com.littlec.sdk.entity.messagebody.AudioMessageBody;
import com.littlec.sdk.entity.messagebody.ImageMessageBody;
import com.littlec.sdk.manager.CMIMHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.contact.SortModel;
import test.grs.com.ims.util.model.QHAttention;
import test.grs.com.ims.util.model.QHBlackList;
import test.grs.com.ims.util.model.QHMailList;
import test.grs.com.ims.util.model.QHRecommends;
import test.grs.com.ims.util.model.QHUserInfoLists;


/**
 * Created by gaoruishan on 15/9/28.
 */
public class MessageHandle {

    private static MessageHandle instance;
    private static DbUtils dbUtils;
    private static List<IMContact> cmContactsList;
    private static List<IMContactList> mAddContactsList;
    private static ArrayList<UNIMContact> scmContactsList = new ArrayList<UNIMContact>();
    private Context mContext;
    private int numCount;
    private String mCurrentRecipient;
    private int j;
    private int cout;
    public List<IMContactList> list;
    public String recommendedUsers = "";
    private CMMessage currentMsg;
    public List<CMGroup> mCMGroupLists;
    public boolean changeAttentionContacts;
    private OnRecivedMessageListeners mSListener;
    private CMMessage currentMsgroup;
    private boolean onlyOnce = true;

    private MessageHandle() {
        mContext = IMApp.mContext;
        dbUtils = IMApp.geDbUtils();
        try {
            //保存默认测试信息
            IMMessage imMessage = new IMMessage();
            if (dbUtils != null) {
                dbUtils.save(imMessage);
                dbUtils.save(new IMContactList());
                dbUtils.save(new IMGroup());
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized static MessageHandle getInstance() {
        if (instance == null) {
            instance = new MessageHandle();
        }
        return instance;
    }

    public void doDeleteMessage(int id) {
        try {
            dbUtils.deleteById(IMMessage.class, id);
            Log.e("deleteById=" + id, "TAG");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @描述：接收到群聊信息
     */
    public synchronized void doRecivedGroupChatMessage(CMMessage message, Boolean notity) {
        downLoadAudio(message);
        if (notity) {
            cout = 1;
        } else {
            cout = 0;
        }
        //获取头像,昵称
        if (message.getExtra() == null || !message.getExtra().contains(",")) {
            mContext.sendBroadcast(new Intent(IMConst.ACTION_GET_USERDETAIL).putExtra("userId", message.getFrom()));
            currentMsgroup = message;
            notity =false;//不去执行提醒
        }
        // 当前聊天用户
        if (mAListener != null && mCurrentRecipient != null) {
            if (mCurrentRecipient.equals(message.getGroupInfo().getGroupId())) {
                cout = 0;
            }
        }
        Log.e("接收到群聊信息", "TAG");
        cmContactsList = getDbIMContact();
        int i = isContainIMGroup(message);
        if (i == -1) {
            return;
        }
        if (i == 0) {
            //新建对话条目
            // 据群 ID 为 groupId 的群名称
//            if (message.getFromNick()!=null&&!message.getFromNick().equals("")&&!message.getFromNick().equals("null")){
//                groupName = message.getFromNick();
//            }else {
            String groupName = CMIMHelper.getCmGroupManager().getGroupNamefromServer(message.getGroupInfo().getGroupId());
//            }
            //新建对话条目
            if (groupName != null) {
                IMContact contact = IMUtil.getIMContactInstance(cmContactsList.size() + 1, false, true, cout, message.getGroupInfo().getGroupId() + "", "", groupName + "", "", message.getTime(), IMUtil.getContentType(message), message.getGuid());
                contact.setGroupId(message.getGroupInfo().getGroupId());
                cmContactsList.add(contact);
            }
        }
        //更新对话列表
        updateToDB(cmContactsList);
        if (notity) {
            NotificationController.getInstance().showNotification1(IMConst.NEW_MESSAGE_NOTIFICATION, message);
            //更新message
            updateLocalDataBase(message);
            //回调接收到消息
            if (mListener != null) {
                mListener.onSelected(i);
            }
            if (mAListener != null && mCurrentRecipient != null) {
                if (mCurrentRecipient.equals(message.getGroupInfo().getGroupId())) {
                    mAListener.onSelected(2);
                }
            }
        }
    }

    /**
     * 判断是否有群聊对话
     *
     * @param message
     * @return
     */
    private int isContainIMGroup(CMMessage message) {
        //未收到任何消息
        if (cmContactsList.size() <= 0) {
            return 0;
        }
        for (int i = 0; i < cmContactsList.size(); i++) {
            IMContact imContact = cmContactsList.get(i);
            if (imContact.isGroupChat()) {//是群聊
                if (imContact.getGroupId().equals(message.getGroupInfo().getGroupId())) {
                    //判断是否屏蔽消息
                    if (imContact.isIgnore()) {
                        Log.e("屏蔽", "TAG");
                        return -1;
                    }
                    if (imContact.isBlackList()) {
                        Log.e("黑名单", "TAG");
                        return -1;
                    }
                    Log.e("已存在列表中", "TAG");
                    //更新
                    imContact.setMsgNum(imContact.getMsgNum() + 1);
                    imContact.setTime(message.getTime());
                    imContact.setMessage(IMUtil.getContentType(message));
                    cmContactsList.remove(i);
                    cmContactsList.add(i, imContact);
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * 保存创建群消息
     *
     * @param
     * @param cmGroup
     */
    public void doReceivedCreateGroupMessage(CMMessage message, CMGroup cmGroup) {
        try {
            NotificationController.getInstance().showNotification1(IMConst.NEW_MESSAGE_NOTIFICATION, message);
            //保存消息列表
            saveToIMContact(message, cmGroup, 1);

            //保存message
            updateCreateLocalDataBase(message, cmGroup);

            //保存群组
            saveToIMGroup(cmGroup);
            Log.e("保存新建群消息", "TAG");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void saveToIMGroup(CMGroup cmGroup) throws DbException {
        IMGroup imGroup = IMUtil.getCMGroupToIMGroup(cmGroup);
        dbUtils.save(imGroup);
    }

    public List<IMGroup> getIMGroups() {
        List<IMGroup> all = null;
        try {
            if (dbUtils != null){
                all = dbUtils.findAll(IMGroup.class, WhereBuilder.b("isPublic", "=", 0));
            }
            if (all == null) {
                all = new ArrayList<IMGroup>();
            }
            return all;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return all;
    }

    public void saveToIMContact(CMMessage cmMessage, CMGroup cmGroup, int numTag) {
        //新建对话条目
        if (cmContactsList == null) {
            cmContactsList = getDbIMContact();
        }
        IMContact contact = IMUtil.getIMContactInstance(cmContactsList.size() + 1, false, true, numTag, cmGroup.getGroupId() + "", "", cmGroup.getGroupName() + "", "", cmMessage.getTime(), cmGroup.getGroupName().split(",")[0] + "创建了群,成员包括 " + cmGroup.getGroupName(), cmMessage.getGuid());
        contact.setGroupId(cmGroup.getGroupId() + "");
        contact.setSave(true);
        cmContactsList.add(contact);
        //更新对话列表
        updateToDB(cmContactsList);
    }

    public ArrayList<IMMember> getBDMembers(ArrayList<String> memeberList) {
        ArrayList<IMMember> list = new ArrayList<IMMember>();

        try {
            for (String s : memeberList) {
                IMContactList u = dbUtils.findFirst(IMContactList.class, WhereBuilder.b("userName", "=", s));
                if (u != null) {
                    list.add(new IMMember(u.getAvatarurl(), u.getNickname(), u.getUserName(), IMMember.TYPE_COMMON));
                }
                continue;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 群主移除成员通知
     *
     * @param message
     * @param groupId
     * @param member
     */
    public void doReceivedKickMemberMessage(CMMessage message, String groupId, CMMember member) {
        String s = member.getMemberNick() + "被群主移除群组";
        if (message.getFrom().equals(IMApp.currentUserName) || member.getMemberNick().equals(IMApp.currentUserNick)) {
            try {
                dbUtils.delete(IMMessage.class, WhereBuilder.b("groupId", "=", groupId));
                dbUtils.delete(IMContact.class, WhereBuilder.b("groupId", "=", groupId));
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else {
            updateNotifyMessageDataBase(message, groupId, s, null);
        }
        //通知
        NotificationController.getInstance().showNotification(IMConst.NEW_MESSAGE_NOTIFICATION, message, s);
    }

    /**
     * XX退群消息处理代码
     *
     * @param cmMessage
     * @param groupId
     */
    public void doReceivedExitGroupMessage(CMMessage cmMessage, String groupId) {
        String name;
        if (cmMessage.getFromNick() == null) {
            if (cmMessage.getExtra() == null) {
                name = cmMessage.getFrom();
            } else {
                name = cmMessage.getExtra().split(",")[1];
            }
        } else {
            name = cmMessage.getFromNick();
        }
        updateNotifyMessageDataBase(cmMessage, groupId, name + "退出群组了。", null);
        //通知
        NotificationController.getInstance().showNotification(IMConst.NEW_MESSAGE_NOTIFICATION, cmMessage, cmMessage.getFromNick() + "退出群组了。");
    }

    /**
     * 群名称变更消息
     *
     * @param cmMessage
     * @param s
     * @param s1
     */
    public void doReceivedSetGroupNameMessage(CMMessage cmMessage, String s, String s1) {
        updateNotifyMessageDataBase(cmMessage, s, cmMessage.getFromNick() + "将群名称改为" + s1 + "", s1);
        updateGroupName(s, s1);//更新
        //通知
        NotificationController.getInstance().showNotification(IMConst.NEW_MESSAGE_NOTIFICATION, cmMessage, cmMessage.getFromNick() + "将群名称改为" + s1);

    }

    /**
     * 群成员被XXX添加的处理代码
     *
     * @param message
     * @param groupId
     * @param list
     */
    public void doReceivedAddMembersMessage(CMMessage message, String groupId, List<CMMember> list) {
        String members = " ";
        for (CMMember m : list) {
            members += m.getMemberNick() + ",";
        }
        updateNotifyMessageDataBase(message, groupId, message.getFromNick() + "邀请" + members + "加入了群组", null);
        NotificationController.getInstance().showNotification(IMConst.NEW_MESSAGE_NOTIFICATION, message, message.getFromNick() + "邀请" + members + "加入了群组");
    }

    /**
     * 解散群组的消息
     *
     * @param cmMessage
     */
    public void doReceivedGroupDestoryedMessage(CMMessage cmMessage) {
        //通知
        NotificationController.getInstance().showNotification(IMConst.NEW_MESSAGE_NOTIFICATION, cmMessage, "群主解散了群组");
        String groupId = cmMessage.getGroupInfo().getGroupId();
        //更新消息列表
        try {
            dbUtils.delete(IMContact.class, WhereBuilder.b("groupId", "=", groupId));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存我关注的人
     */
    public void saveToAttentionContacts(List<QHAttention.ResultsEntity> results) {
        changeAttentionContacts = true;
        new SQWorkTask(results, "").execute();
    }

    public void saveToIMGroupList(List<CMGroup> groups) {
        if (mGListener != null) {
            mGListener.setGroupList(groups);
        }
        //只清理一次聊天列表
        if (onlyOnce){
            if (cmContactsList == null) {
                cmContactsList = getDbIMContact();
            }
            for (CMGroup g:groups) {
                for (IMContact c:cmContactsList) {
                    if (!c.getUserName().equals(g.getGroupId())){
                        try {
                            dbUtils.delete(IMContact.class,WhereBuilder.b("userName", "=", c.getUserName()));
//                            IMContact cmContact = dbUtils.findFirst(Selector.from(IMContact.class).where("userName", "=", c.getUserName()));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            onlyOnce = false;
        }
//        mCMGroupLists = groups;
//        if (groups != null && groups.size() > 0) {
//            if (dbUtils == null) {
//                return;
//            }
//            List<IMGroup> all = null;
//            try {
//                all = dbUtils.findAll(IMGroup.class);
//                if (all != null && all.size() > 0) {
//                    //先删除
//                    dbUtils.deleteAll(IMGroup.class);
//                }
//                //保存
//                for (CMGroup cg : groups) {
//                    saveToIMGroup(cg);
//                }
//            } catch (DbException e) {
//                e.printStackTrace();
//            }

//        }
    }

    public void doUserInfoList(QHUserInfoLists user) {
        if (user == null) {
            return;
        }
        if (mSListener != null) {
            mSListener.onBackSuccess(user);
        }
//        for (int i = 0; i < user.getUser_info(); i++) {
//
//        }
    }

    public void updateGroupName(String groupId, String groupName) {
        IMContact currentGrp = null;
        try {
            currentGrp = dbUtils.findFirst(Selector.from(IMContact.class).where("groupId", "=", groupId));
            if (currentGrp != null) {
                currentGrp.setNickname(groupName);
                dbUtils.saveOrUpdate(currentGrp);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    class SQWorkTask extends AsyncTask<Object, String, String> {
        private List<QHAttention.ResultsEntity> results;
        private String args;

        public SQWorkTask(List<QHAttention.ResultsEntity> results, String args) {
            this.results = results;
            this.args = args;
        }

        @Override
        protected String doInBackground(Object... objects) {
            try {
                if (dbUtils == null) {
                    return "0";
                }
                List<IMContactList> all = dbUtils.findAll(IMContactList.class);
                if (all != null && all.size() > 0) {
                    //先删除
                    dbUtils.deleteAll(IMContactList.class);
                }
                if (results == null || results.size() > 0) {
                    //获取我关注的人
                    mAddContactsList = IMUtil.addContactDefultDatas(results);
                    //保存－我关注的人
                    dbUtils.saveAll(mAddContactsList);
                    return "1";
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("1")) {
                Log.e("==TAG", "saveToAttentionContacts=success");
            } else {
                Log.e("==TAG", "saveToAttentionContacts=fail");
            }
        }
    }

    /**
     * 保存黑名单
     */
    class BLWorkTask extends AsyncTask<Object, String, String> {
        private List<QHBlackList.ResultsEntity> results;
        private String args;

        public BLWorkTask(List<QHBlackList.ResultsEntity> results, String args) {
            this.results = results;
            this.args = args;
        }

        @Override
        protected String doInBackground(Object... objects) {
            try {
//                List<IMBlackList> all = dbUtils.findAll(IMBlackList.class);
//                if (all != null && all.size() > 0) {
                //先删除
                if (dbUtils != null) {

                    dbUtils.deleteAll(IMBlackList.class);
//                }
                    if (results != null && results.size() > 0) {
                        //保存－黑名单
                        int i = 0;
                        final ArrayList<IMBlackList> blackLists = new ArrayList<IMBlackList>();
                        for (QHBlackList.ResultsEntity r : results) {
                            IMBlackList black = new IMBlackList();
                            if (r.getUserId() != null) {
                                black.setId(Integer.parseInt(r.getUserId()));
                            } else {
                                black.setId(i++);
                            }
                            black.setUserName(r.getUserId() + "");
                            black.setNickname(r.getNickname() + "");
                            black.setIntroduce(r.getIntroduction() + "");
                            black.setAvatarurl(r.getAvatarUrl() + "");
                            blackLists.add(black);
                        }
                        dbUtils.saveAll(blackLists);
                        return "1";
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("1")) {
                Log.e("==TAG", "saveBlackListContacts=success");
            } else {
                Log.e("==TAG", "saveBlackListContacts=fail");
            }
        }
    }

    /**
     * 保存黑名单
     *
     * @param user
     */
    public void saveTOIMBlackLists(QHBlackList user) {
        new BLWorkTask(user.getResults(), "").execute();
    }

    /**
     * 获得黑名单列表
     *
     * @param
     */
    public List<IMBlackList> getBlackLists() {
        List<IMBlackList> all = null;
        try {
            all = dbUtils.findAll(IMBlackList.class);
            if (all == null) {
                all = new ArrayList<IMBlackList>();
            }
            Log.e("==获得黑名单列表" + all.toString(), "");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return all;
    }

    /**
     * 推荐用户
     *
     * @param user
     * @return
     */
    public String setFreshQHRecommendDatas(QHRecommends user) {
        if (user == null || user.getResults().size() <= 0) {
            return "";
        }
//        Log.e("TAG", user.toString());
        for (int i = 0; i < user.getResults().size(); i++) {
            QHRecommends.ResultsEntity entity = user.getResults().get(i);
            if (entity.getUserId() != 0) {
                //其中消息数量表示 性别 0男 1女 -1未设置
                IMContactList instance = IMUtil.getIMContactListInstance(entity.getUserId(), false, false, entity.getGender(), entity.getUserId() + "", "", entity.getNickname()+"", entity.getAvatarUrl()+"", System.currentTimeMillis(), "", 0l, "", false);
                if (list == null) {
                    list = new ArrayList<IMContactList>();
                }
                list.add(instance);
            }
        }

        //第二次请求
        if (recommendedUsers.length() > 0) {
            recommendedUsers = recommendedUsers + ",";
        }
        for (int i = 0; i < user.getResults().size(); i++) {
            QHRecommends.ResultsEntity qc = user.getResults().get(i);
            if (user.getResults().size() - 1 == i) {
                recommendedUsers += qc.getUserId() + "";
            } else {
                recommendedUsers += qc.getUserId() + ",";
            }
        }
        //回调
        if (list.size() > 0) {
            mRListener.onBackSuccess(list);
        }
        return recommendedUsers;
    }

    /**
     * 刷新关注用户
     *
     * @param user
     * @return
     */
    public void setFreshAttentionDatas(QHAttention user) {
        ArrayList<IMContactList> list = null;
        //数据为空
        if (user == null || user.getResults().size() <= 0) {
            if (mListener != null) {
                mListener.onBackSuccess(new ArrayList<IMContactList>());
            }
            return;
        }
        if (mListener != null) {
            for (int i = 0; i < user.getResults().size(); i++) {
                QHAttention.ResultsEntity entity = user.getResults().get(i);
                if (entity.getUserId() != null) {
                    int parseInt = Integer.parseInt(entity.getUserId());
                    IMContactList instance = IMUtil.getIMContactListInstance(parseInt, false, false, 0, entity.getUserId() + "", "", entity.getNickname(), entity.getAvatarUrl(), System.currentTimeMillis(), "", 0l, entity.getIntroduction() + "", entity.getIsfriend() == 0 ? false : true);
                    if (list == null) {
                        list = new ArrayList<IMContactList>();
                    }
                    list.add(instance);
                }
            }
            if (list.size() > 0) {
                mListener.onBackSuccess(list);
            }
        }
    }

    /**
     * 刷通讯录
     *
     * @param user
     */
    public void setFreshMailLists(QHMailList user) {
        ArrayList<SortModel> list = null;
        if (user.getResults().size() <= 0) {
            if (mListener != null) {
                mListener.onBackContactSuccess(new ArrayList<SortModel>());
            }
            return;
        }
        if (mListener != null) {
            for (int i = 0; i < user.getResults().size(); i++) {
                QHMailList.ResultsEntity entity = user.getResults().get(i);
                if (entity.getUserId() != null) {
                    SortModel sortModel = new SortModel();
                    sortModel.setName(entity.getUserId() + "");
                    sortModel.setNickname(entity.getNickname() + "");
                    sortModel.setAvatar_url(entity.getAvatarUrl() + "");
                    sortModel.setIntroduction(entity.getPhoneNum() + "");//介绍＝＝手机号
                    if (list == null) {
                        list = new ArrayList<SortModel>();
                    }
                    list.add(sortModel);
                }
            }
            if (list.size() > 0) {
                mListener.onBackContactSuccess(list);
            }
        }
    }

    /**
     * 添加为关注的人消息列表
     *
     * @param avatarUrl
     */
    public void doAddUnContact(String avatarUrl, String nickName) {
        if (nickName != null) {
            currentMsgNick = nickName;
        }
        if (currentMsg != null) {
            UNIMContact contact = IMUtil.getUNIMContactInstance(Integer.parseInt(currentMsg.getFrom()), false, false, cout, currentMsg.getFrom(), "", nickName, avatarUrl, currentMsg.getTime(), IMUtil.getContentType(currentMsg), currentMsg.getGuid());
            scmContactsList.add(contact);
            // 更新消息列表
            updateToUNDB(scmContactsList);
            //通知和保存消息
            saveAndNotify(currentMsg, true, 1);
        }
        if (currentMsgroup!= null){// 群聊
            currentMsgroup.setExtra(avatarUrl+","+nickName);
            currentMsgroup.setFromNick(nickName);
            NotificationController.getInstance().showNotification1(IMConst.NEW_MESSAGE_NOTIFICATION, currentMsgroup);
            //更新message
            updateLocalDataBase(currentMsgroup);
            //回调接收到消息
            if (mListener != null) {
                mListener.onSelected(0);
            }
            if (mAListener != null && mCurrentRecipient != null) {
                if (mCurrentRecipient.equals(currentMsgroup.getGroupInfo().getGroupId())) {
                    mAListener.onSelected(2);
                }
            }
        }
    }


    /**
     * 接口回调实现与UI监听交互
     */
    public interface OnRecivedMessageListener {
        void onSelected(int type);

        void onBackSuccess(List<IMContactList> user);

        void onBackContactSuccess(List<SortModel> list);
    }

    public interface OnRecivedMessageListeners {
        void onBackSuccess(QHUserInfoLists user);

    }

    public interface OnRecivedToActivityListener {
        void onSelected(int type);

    }

    public void setCurrentRecipient(String mCurrentRecipient) {
        this.mCurrentRecipient = mCurrentRecipient;
    }


    public String getCurrentRecipient() {
        return this.mCurrentRecipient;
    }

    public OnGroupListener mGListener;
    public OnRecivedMessageListener mListener;
    public OnRecivedMessageListener mRListener;
    public OnRecivedToActivityListener mAListener;

    /**
     * 用于回调
     */
    public interface OnGroupListener {
        void setGroupList(List<CMGroup> groups);
    }

    public void setOnRecivedMessageListener(OnRecivedMessageListener mListener) {
        this.mListener = mListener;
    }

    public void setOnRecivedMessageListeners(OnRecivedMessageListener mListener) {
        this.mRListener = mListener;
    }

    public void setOnRecivedMessageListener1(OnRecivedMessageListeners mListener) {
        this.mSListener = mListener;
    }

    public void setOnGroupListener(OnGroupListener mListener) {
        this.mGListener = mListener;
    }

    public void setOnRecivedToActivityListener(OnRecivedToActivityListener mListener) {
        this.mAListener = mListener;
    }

    /**
     * @描述：接收到单聊信息
     */
    public synchronized void doRecivedChatMessage(CMMessage message, Boolean notity) {
        String extra = message.getExtra();
        if (extra != null && extra.contains(",")) {
            String[] split = extra.split(",");
            currentMsgAvataUrl = split[0];
            currentMsgNick = split[1];
        }
        //黑名单 返回
        List<IMBlackList> lists = getBlackLists();
        for (IMBlackList ic : lists) {
            if (ic.getUserName().equals(message.getFrom())) {
                return;
            }
        }
        //下载语音
        downLoadAudio(message);
        if (notity) {
            cout = 1;
        } else {
            cout = 0;
        }
        // 当前聊天用户
        if (mAListener != null && mCurrentRecipient != null) {
            if (mCurrentRecipient.equals(message.getFrom())) {
                cout = 0;
            }
        }
        final int i;
        boolean contain = isMyNoticeIMContact(message);
        if (contain) {//1 获取我关注的人
            cmContactsList = getDbIMContact();
            //判断是否在黑名单或存在列表中
            i = isContainIMContact(message);//-1黑名单或屏蔽，0 新建，1已存在列表
            if (i == -1) {
                return;
            }
            if (i == 0) {
                //新建对话条目
                if (currentMsgAvataUrl == null || currentMsgAvataUrl.isEmpty()) {
                    if (message.getExtra() != null && !message.getExtra().isEmpty())
                        currentMsgAvataUrl = message.getExtra();
                }
                IMContact contact = IMUtil.getIMContactInstance(cmContactsList.size() + 1, false, false, cout, message.getFrom(), "", currentMsgNick + "", currentMsgAvataUrl + "", message.getTime(), IMUtil.getContentType(message), message.getGuid());
                if (currentMsgNick != null && !currentMsgNick.isEmpty()) {
                    contact.setNickname(currentMsgNick);
                }
                cmContactsList.add(contact);
            }
            //更新
            updateToDB(cmContactsList);
        } else {//2 获取未关注的人
            scmContactsList = getDbUNIMContact();
            //判断是否存在列表中
            i = isContainUNIMContact(message);//-1黑名单或屏蔽，10 新建，11已存在列表
            if (i == -1) {
                return;
            }
            if (i == 10) {
                //getExtra    头像
                if (message.getExtra() != null && !message.getExtra().isEmpty()) {//&&message.getFromNick()!=null
                    UNIMContact contact = IMUtil.getUNIMContactInstance(scmContactsList.size() + 1, false, false, cout, message.getFrom(), "", currentMsgNick + "", currentMsgAvataUrl + "", message.getTime(), IMUtil.getContentType(message), message.getGuid());
                    scmContactsList.add(contact);
                } else {
                    //获取头像,昵称  回调添加未关注的人
                    mContext.sendBroadcast(new Intent(IMConst.ACTION_GET_USERDETAIL).putExtra("userId", message.getFrom()));
                    //未关注的人
                    currentMsg = message;
                    Log.e("=加载未关注的个人详情sendBroadt" + message.getFrom(), "");
                    return;
                }
            }
            // 更新
            updateToUNDB(scmContactsList);
        }

        //通知和保存消息
        saveAndNotify(message, notity, i);

    }

    private void saveAndNotify(CMMessage message, Boolean notity, int i) {
        if (notity) {
            if (currentMsgNick != null) {
                message.setFromNick(currentMsgNick);
            }

            NotificationController.getInstance().showNotification1(IMConst.NEW_MESSAGE_NOTIFICATION, message);
            updateLocalDataBase(message);
            //回调接收到消息
            if (mListener != null) {
                mListener.onSelected(i);
            }
            if (mAListener != null && mCurrentRecipient != null) {
                if (mCurrentRecipient.equals(message.getFrom())) {
                    mAListener.onSelected(1);
                }
            }
        }
    }

    /**
     * 下载语音数据
     *
     * @param message
     */
    private void downLoadAudio(CMMessage message) {
        //下载语音到本地
        if (message.getContentType() == MessageConstants.Message.TYPE_AUDIO) {
            String uri = ((AudioMessageBody) message.getMessageBody()).getOriginalUri();
            try {
                File file = IMUtil.saveFileFromServer(uri);
                Log.e("==downLoadAudio-file", file.getAbsolutePath());
                int duration = ((AudioMessageBody) message.getMessageBody()).getDuration();
                message.setMessageBody(new AudioMessageBody(file, duration));
            } catch (Exception e) {
                Log.e("=下载语音数据", "" + e.toString());
                e.printStackTrace();
            }
        }
    }

    private String currentMsgAvataUrl, currentMsgNick;

    private boolean isMyNoticeIMContact(CMMessage message) {
        if (mAddContactsList == null || mAddContactsList.size() <= 0) {
            return false;
        }
        for (IMContactList ic : mAddContactsList) {
            if (ic.getUserName().equals(message.getFrom())) {
                currentMsgAvataUrl = ic.getAvatarurl();
                currentMsgNick = ic.getNickname();
                return true;
            }
        }
        return false;
    }

    /**
     * 获取非黑名单的
     */
    public List<IMContact> getDbIMContact() {
        if (dbUtils == null) {
            dbUtils = IMApp.geDbUtils();
            return new ArrayList<IMContact>();
        }
        try {
            cmContactsList = dbUtils.findAll(Selector.from(IMContact.class).where("blackList", "=", 0));
        } catch (DbException e) {
//            e.printStackTrace();
            Log.e("getDbIMContact=", "" + e.toString());
        }
        if (cmContactsList == null) {
            cmContactsList = new ArrayList<IMContact>();
        }
        return cmContactsList;
    }

    /**
     * 获取未关注的人
     */
    public ArrayList<UNIMContact> getDbUNIMContact() {
        if (dbUtils == null) {
            return new ArrayList<UNIMContact>();
        }
        try {
            scmContactsList = (ArrayList<UNIMContact>) dbUtils.findAll(UNIMContact.class);
        } catch (DbException e) {
//            e.printStackTrace();
            Log.e("getDbUNIMContact=", "" + e.toString());
        }

        if (scmContactsList == null) {
            scmContactsList = new ArrayList<UNIMContact>();
        }
        return scmContactsList;
    }

    /**
     * 是否包含消息列表中
     *
     * @param message
     * @return
     */
    private int isContainIMContact(CMMessage message) {
        //未收到任何消息
        if (cmContactsList.size() <= 0) {
            return 0;
        }
        //已保存过消息
        for (int i = 0; i < cmContactsList.size(); i++) {
            IMContact imContact = cmContactsList.get(i);
            //遍历查找 来自消息的用户
            if (imContact.getUserName() != null && imContact.getUserName().equals(message.getFrom())) {
                //判断是否屏蔽消息
                if (imContact.isIgnore()) {
                    return -1;
                }
                if (imContact.isBlackList()) {
                    Log.e("黑名单", "TAG");
                    return -1;
                } else {
                    Log.e("已存在列表中", "TAG");
                    //更新
                    imContact.setMsgNum(imContact.getMsgNum() + cout);
                    imContact.setTime(message.getTime());
                    if (currentMsgAvataUrl != null && !currentMsgAvataUrl.isEmpty()) {
                        imContact.setAvatarurl(currentMsgAvataUrl);
                    }
                    if (currentMsgNick != null && !currentMsgNick.isEmpty()) {
                        imContact.setNickname(currentMsgNick);
                    }
                    imContact.setMessage(IMUtil.getContentType(message));
                    cmContactsList.remove(i);
                    cmContactsList.add(i, imContact);
                    return 1;
                }
            }
        }
        return 0;
    }

    private int isContainUNIMContact(CMMessage message) {
        //未关注的人消息列表
        //未收到任何消息
        if (scmContactsList.size() <= 0) {
            return 10;
        }
        if (scmContactsList.size() > 0) {
            for (int i = 0; i < scmContactsList.size(); i++) {
                UNIMContact im = scmContactsList.get(i);
                if (im.getUserName().equals(message.getFrom())) {
                    //判断是否屏蔽消息
                    if (im.isIgnore()) {
                        return -1;
                    }
                    if (im.isBlackList()) {
                        Log.e("黑名单", "TAG");
                        return -1;
                    }
                    Log.e("已存在列表中", "TAG");
                    //更新
                    im.setMsgNum(im.getMsgNum() + cout);
                    im.setTime(message.getTime());
                    im.setMessage(IMUtil.getContentType(message));
                    scmContactsList.remove(i);
                    scmContactsList.add(i, im);
                    return 11;
                }
            }
        }
        return 10;
    }

    public synchronized void updateToDB(List<IMContact> cmContactsList) {
        try {
            //更新保存最后一次数据
            dbUtils.deleteAll(IMContact.class);
            dbUtils.saveAll(cmContactsList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新列表中黑名单数据
     *
     * @param userId
     */
    public synchronized static void updateToIMContact(String userId) {
        try {
            //更新列表中黑名单数据
            IMContact cmContact = dbUtils.findFirst(Selector.from(IMContact.class).where("userName", "=", userId));
            if (cmContact == null) {
                UNIMContact ucmContact = dbUtils.findFirst(Selector.from(UNIMContact.class).where("userName", "=", userId));
                if (ucmContact != null) {
                    ucmContact.setBlackList(true);
                    dbUtils.saveOrUpdate(ucmContact);
                }
                Log.e("===更新列表中黑名单数据", ":ucmContact=" + ucmContact);
            } else {
                cmContact.setBlackList(true);
                dbUtils.saveOrUpdate(cmContact);
            }
            Log.e("===更新列表中黑名单数据", ":cmContact=" + cmContact);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void updateToUNDB(List<UNIMContact> cmContactsList) {
        try {
            //更新保存最后一次数据
            dbUtils.deleteAll(UNIMContact.class);
            dbUtils.saveAll(cmContactsList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新数据库
     *
     * @param message
     */
    public void updateLocalDataBase(CMMessage message) {

        IMMessage msg = new IMMessage();
        msg.setId(message.getId());
        msg.setPacketId(message.getPacketId());
        msg.setFrom(message.getFrom());
        msg.setFromNick(message.getFromNick());
        msg.setTo(message.getTo());
        msg.setSendOrRecv(message.getSendOrRecv());
        msg.setContentType(message.getContentType());
        if (message.getChatType() == 1) {
            msg.setChatType(message.getChatType());
            msg.setGroupId(message.getGroupInfo().getGroupId());
        } else {
            msg.setChatType(message.getChatType());
        }
        msg.setChatType(message.getChatType());
        msg.setStatus(message.getStatus());
        msg.setTime(message.getTime());
        msg.setGuid(message.getGuid());
        msg.setExtra(message.getExtra());//头像
        switch (message.getContentType()) {
            case MessageConstants.Message.TYPE_AUDIO:
                AudioMessageBody bodyAudio = (AudioMessageBody) message.getMessageBody();
                msg.setLocalPath(bodyAudio.getLocalPath());
                msg.setFileName(bodyAudio.getFileName());
                msg.setFileLength(bodyAudio.getFileLength());
                msg.setOriginalUri(bodyAudio.getOriginalUri());
                msg.setDuration(bodyAudio.getDuration());
                msg.setContent(bodyAudio.getContent());
                break;
            case MessageConstants.Message.TYPE_PIC:
                ImageMessageBody bodyImage = (ImageMessageBody) message.getMessageBody();
                msg.setLocalPath(bodyImage.getLocalPath());
                msg.setFileName(bodyImage.getFileName());
                msg.setHeight(bodyImage.getHeight());
                msg.setWidth(bodyImage.getWidth());
                msg.setMiddleUri(bodyImage.getMiddleUri());
                msg.setSmallUri(bodyImage.getSmallUri());
                msg.setContent(bodyImage.getContent());
                break;
            default:
                msg.setContent(message.getMessageBody().getContent() + "");
                break;
        }
        try {
            //保存
            dbUtils.save(msg);
            Log.e("updateLocalDataBase", "TAG");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存创建群的提示消息
     *
     * @param message
     * @param cmGroup
     */
    public void updateCreateLocalDataBase(CMMessage message, CMGroup cmGroup) {
        IMMessage msg = new IMMessage();
        msg.setId(message.getId());
        msg.setPacketId(message.getPacketId() + "");
        msg.setFrom(message.getFrom() + "");
        msg.setFromNick(message.getFromNick() + "");
        msg.setTo(message.getTo() + "");
        msg.setSendOrRecv(message.getSendOrRecv());
        msg.setContentType(message.getContentType());
        msg.setChatType(message.getChatType());
        msg.setStatus(message.getStatus());
        msg.setTime(message.getTime());
        msg.setGuid(message.getGuid());
        msg.setGroupId(cmGroup.getGroupId() + "");
        msg.setContent(cmGroup.getGroupName().split(",")[0] + "创建了群,成员包括 " + cmGroup.getGroupName() + "");
//        List<CMMember> members = cmGroup.getMembers();
//        msg.setContent(IMApp.getCurrentUserNick() + "创建了群(" + cmGroup.getGroupName() + "),成员包括 " + IMUtil.geMemeberString(members) + "");
        try {
            //保存
            dbUtils.save(msg);
            Log.e("updateLocalDataBase", "TAG");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新消息列表提示消息
     *
     * @param message
     * @param groupId
     * @param msgcontent
     * @param groupName
     */
    public void updateNotifyMessageDataBase(CMMessage message, String groupId, String msgcontent, String groupName) {
        IMMessage msg = new IMMessage();
        msg.setId(message.getId());
        msg.setPacketId(message.getPacketId());
        msg.setFrom(message.getFrom());
        msg.setFromNick(message.getFromNick());
        msg.setTo(message.getTo());
        msg.setSendOrRecv(message.getSendOrRecv());
        msg.setContentType(message.getContentType());
        msg.setChatType(message.getChatType());
        msg.setStatus(message.getStatus());
        msg.setTime(message.getTime());
        msg.setGuid(message.getGuid());

        msg.setGroupId(groupId);
        msg.setContent(msgcontent + "");
        try {

            //更新消息列表
            IMContact imContact = dbUtils.findFirst(IMContact.class, WhereBuilder.b("groupId", "=", groupId));
            if (imContact != null) {
                imContact.setMessage(msgcontent);
                if (groupName != null) {//修改群组名称
                    imContact.setUserName(groupId);
                    imContact.setNickname(groupName);
                }
                dbUtils.saveOrUpdate(imContact);
            } else {//创建
                groupName = CMIMHelper.getCmGroupManager().getGroupNamefromServer(message.getGroupInfo().getGroupId());
                //新建对话条目
                if (groupName != null) {
                    IMContact contact = IMUtil.getIMContactInstance(cmContactsList.size() + 1, false, true, cout, message.getGroupInfo().getGroupId() + "", "", groupName + "", "", message.getTime(), msgcontent, message.getGuid());
                    contact.setGroupId(message.getGroupInfo().getGroupId());
                    if (cmContactsList == null) {
                        cmContactsList = getDbIMContact();
                    }
                    cmContactsList.add(contact);
                }
                updateToDB(cmContactsList);
            }
            //保存message
            dbUtils.save(msg);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
