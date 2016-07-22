package test.grs.com.ims.message;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.littlec.sdk.constants.CMSdkContants;
import com.littlec.sdk.entity.AckMessage;
import com.littlec.sdk.entity.CMGroup;
import com.littlec.sdk.entity.CMMember;
import com.littlec.sdk.entity.CMMessage;
import com.littlec.sdk.entity.SystemMessage;
import com.littlec.sdk.manager.CMIMHelper;
import com.littlec.sdk.utils.CMChatListener;
import com.littlec.sdk.utils.CMContactListener;

import java.util.HashMap;
import java.util.List;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.util.netstate.NetChangeObserver;
import test.grs.com.ims.util.netstate.NetWorkUtil;
import test.grs.com.ims.util.netstate.NetworkStateReceiver;

public class BackgroundService extends Service {
    private String userName;
    private String passWord;
    private String from = " ";
    public boolean mIsLoginSuccess = false;

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        startForeground(0, new Notification());
        super.onCreate();
        // 注册网络监听
        NetworkStateReceiver.registerNetworkStateReceiver(this);
        NetworkStateReceiver.registerObserver(observer);
    }
    private boolean isDisConn;
    public MyNetChngeOberver observer = new MyNetChngeOberver();

    public class MyNetChngeOberver implements NetChangeObserver {

        @Override
        public void onConnect(NetWorkUtil.NetType type) {
            Log.e("=onConnect", "TAG");
            if (isDisConn){
                //重新连接
                mIsLoginSuccess = false;
                String userName = IMSharedPreferences.getString(IMSharedPreferences.ACCOUNT, null);
                String passWord = IMSharedPreferences.getString(IMSharedPreferences.PASSWORD, null);
                if (userName != null && passWord != null) {
                    doLogin(userName, passWord);
                    Log.e("RedoLogin", "TAG");
                }
                Log.e("startService", "TAG");
            }
        }

        @Override
        public void onDisConnect() {
            isDisConn = true;
            Log.e("=onDisConnect", "TAG");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            userName = IMSharedPreferences.getString(
                    IMSharedPreferences.ACCOUNT, "");
            passWord = IMSharedPreferences.getString(
                    IMSharedPreferences.PASSWORD, "");
            doLogin(userName, passWord);
        } else {
            userName = intent.getStringExtra("userName");
            passWord = intent.getStringExtra("passWord");
            if (userName == null || passWord == null) {
                userName = IMSharedPreferences.getString(
                        IMSharedPreferences.ACCOUNT, "");
                passWord = IMSharedPreferences.getString(
                        IMSharedPreferences.PASSWORD, "");
            }
            doLogin(userName, passWord);
        }
        sendBroadcast(new Intent(IMConst.ACTION_LOGIN_START));
        IMApp.isBackGroundServiceRunning = true;

        return START_STICKY;
    }

    /**
     * @方法名：doLogin
     * @描述：登录按钮的操作
     */
    private synchronized void doLogin(String userName, String passWord) {
        if (!mIsLoginSuccess) {
            //退出登录,和服务器断开连接
            CMIMHelper.getCmAccountManager().doLogOut();
            new LoginWorkTask(userName, passWord, false).execute();
        }
    }

    //异步登陆
    class LoginWorkTask extends AsyncTask<Object, String, String> {
        private String userName;
        private String passWord;

        public LoginWorkTask(String userName, String passWord, boolean isLoginSuccess) {
            this.userName = userName;
            this.passWord = passWord;
        }

        @Override
        protected String doInBackground(Object... objects) {

            CMIMHelper.getCmAccountManager().doLogin(userName, passWord, new CMChatListener.OnCMListener() {
                @Override
                public void onSuccess() {
                    //登录成功的处理代码
                    Log.e("==登录 ＝login onSuccess", "TAG");
//                        isLoginSuccess = true;
                    mIsLoginSuccess = true;
                    sendBroadcast(new Intent(IMConst.ACTION_GROUP));
                    sendBroadcast(new Intent(IMConst.ACTION_LOGIN_SUCCESS));
                    // 添加对服务器对连接监听
                    addNetStateListener();

                    //添加监听消息处理
                    addReceiveMsgListener();

                    IMSharedPreferences.putString(IMSharedPreferences.ACCOUNT, userName.toLowerCase());
                    IMSharedPreferences.putString(IMSharedPreferences.PASSWORD, passWord);
                    IMApp.getInstance().setCurrentUserName(userName.toLowerCase());

                }

                @Override
                public void onFailed(String errorMsg) {
                    //登录失败的处理代码，参数errorMsg为失败的原因的描述字符串
                    //失败后 自动注册
//                doRegister(userName, "and畅", passWord);
                    mIsLoginSuccess = false;
                    Log.e("==登录＝login onFailed", "TAG" + errorMsg);
                    IMApp.isBackGroundServiceRunning = false;
//                Intent loginFail = new Intent(IMConst.ACTION_LOGIN_FAIL);
//                loginFail.putExtra(IMConst.LOGIN_FAIL_MSG, errorMsg);
//                //发送广播
//                sendBroadcast(loginFail);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    /**
     * 注册成功后登录
     */
    private void doRegister(final String userName, final String nickName, final String passWord) {
        HashMap<String, String> registerInfo = new HashMap<String, String>();
        registerInfo.put(CMSdkContants.CM_USER_NAME, userName);
        registerInfo.put(CMSdkContants.CM_NICK_NAME, nickName);
        registerInfo.put(CMSdkContants.CM_PASSWORD, passWord);
        registerInfo.put(CMSdkContants.CM_CONFIRM_PASSWORD,
                passWord);
        CMIMHelper.getCmAccountManager().createAccount(registerInfo, new CMChatListener.OnCMListener() {
            @Override
            public void onSuccess() {
                Log.e("doRegister－onSuccess", "TAG");
//                doLogin(userName, passWord);
            }

            @Override
            public void onFailed(String s) {
                Log.e("doRegister－onFailed", "TAG");
            }
        });
    }

    /**
     * 联系人变化以及接收消息监听
     */
    public void addReceiveMsgListener() {
        CMContactListener cmContactListener = new CMContactListener() {
            @Override
            public void onContactRefused(String fromUserName) {
                // 被对方拒绝好友请求时的处理代码
                Log.e("被对方拒绝好友", "onContactRefused");
            }

            @Override
            public void onContactInvited(String fromUserName, String reason) {
                // 被申请加好友时的处理代码
                Log.e("被申请加好友", "onContactInvited");
                //true 表示同意
                CMIMHelper.getCmContactManager().dealWithInvitation(fromUserName, true, new CMChatListener.OnCMListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("同意＝onSuccess", "TAG");
                    }

                    @Override
                    public void onFailed(String s) {

                    }
                });
            }

            @Override
            public void onContactDeleted(String userName) {
                // 被某个好友删除时的处理代码
            }

            @Override
            public void onContactAgreed(String fromUserName) {
                // 对方同意自己的好友请求时的处理代码
                Log.e("对方同意自己的好友", "onContactAgreed");
            }

            @Override
            public void onContactAdded(String userName) {
                // 通讯录增加了联系人的处理代码
            }

            @Override
            public void onContactInfoUpdated(String from, String newPhoneNumber, String newNickName) {
                // 通讯联系人变更的通知
            }
        };
        CMChatListener.CMMessageReceivedCallBack cmMessageReceivedCallBack = new CMChatListener.CMMessageReceivedCallBack() {
            @Override
            public void onReceivedChatMessage(CMMessage message) {
                /**
                 *  单聊消息收到时候的处理代码
                 */
                Log.e("=收到消息：", message.getFrom());
                MessageHandle.getInstance().doRecivedChatMessage(message, true);
            }

            @Override
            public void onReceivedToPullMessages(CMMessage cmMessage, int count) {
                //当平台为客户端开启离线消息的推拉结合功能，且此会话中离线消息条数>=2时会走该回调，message为此会话中所有离线消息的最后一条，
                // count为所有的离线消息条数（包含message这条），客户端调用拉取历史消息接口，拉取剩下的离线消息
            }

            @Override
            public void onReceivedGroupChatMessage(CMMessage cmMessage) {
                /**
                 *群聊消息收到时候的处理代码
                 */
                Log.e("=群聊消息：" + cmMessage.getFrom(), "TAG");
                MessageHandle.getInstance().doRecivedGroupChatMessage(cmMessage, true);

            }

            @Override
            public void onReceivedCreateGroupMessage(CMMessage cmMessage, CMGroup cmGroup) {
                /**
                 * XXX新建群的消息处理代码
                 */
                Log.e("=新建群:" + cmMessage.getFrom() + cmGroup.getGroupName(), "TAG");
                if (!from.equals(cmMessage.getFrom())) {
                    from = cmMessage.getFrom();
                    MessageHandle.getInstance().doReceivedCreateGroupMessage(cmMessage, cmGroup);
                }
                sendBroadcast(new Intent(IMConst.ACTION_ONLY_GROUP));
            }

            @Override
            public void onReceivedExitGroupMessage(CMMessage cmMessage, String s) {
                /**
                 *  XX退群消息处理代码
                 */
                Log.e("=退群消息:" + cmMessage.getMessageBody().getContent() + s, "TAG");
                MessageHandle.getInstance().doReceivedExitGroupMessage(cmMessage, s);
            }

            @Override
            public void onReceivedKickMemberMessage(CMMessage cmMessage, String s, CMMember cmMember) {
                /**
                 *  群主 XXX 将 kickedMember 移出该群?
                 */
                Log.e("=群主 XXX 将 XX 移出该群=" + cmMessage.getMessageBody().getContent() + s + cmMember.getMemberId(), "TAG");
                MessageHandle.getInstance().doReceivedKickMemberMessage(cmMessage, s, cmMember);
                sendBroadcast(new Intent(IMConst.ACTION_ONLY_GROUP));
            }

            @Override
            public void onReceivedSetGroupNameMessage(CMMessage cmMessage, String s, String s1) {
                /**
                 * 群名称变更消息
                 */
                Log.e("=群名称变更=" + cmMessage.getMessageBody().getContent() + s + s1, "TAG");
                MessageHandle.getInstance().doReceivedSetGroupNameMessage(cmMessage, s, s1);
            }

            @Override
            public void onReceivedMemberNickChangedMessage(CMMessage cmMessage, String s, String s1) {

            }

            @Override
            public void onReceivedInvitationMessage(CMMessage cmMessage) {

            }

            @Override
            public void onReceivedGroupDestoryedMessage(CMMessage cmMessage) {
                /**
                 * 收到解散群组的消息
                 */
                Log.e("=解散群组:" + cmMessage.getFrom() + "-" + cmMessage.toString(), "TAG");
                MessageHandle.getInstance().doReceivedGroupDestoryedMessage(cmMessage);
                sendBroadcast(new Intent(IMConst.ACTION_ONLY_GROUP));
            }

            @Override
            public void onReceivedAddMembersMessage(CMMessage cmMessage, String s, List<CMMember> list) {
                /**
                 * 群成员被XXX添加的处理代码
                 */
                Log.e("=被XXX添加:" + cmMessage.getMessageBody().getContent() + s + "--" + list.size(), "TAG");
                MessageHandle.getInstance().doReceivedAddMembersMessage(cmMessage, s, list);
                sendBroadcast(new Intent(IMConst.ACTION_ONLY_GROUP));
            }

            @Override
            public void onReceivedSystemMessage(SystemMessage systemMessage) {
                //接收系统通知
                Log.e("=接收系统通知:" + systemMessage.getContent(), "TAG");
            }

            @Override
            public void onReceivedAckMessage(AckMessage ackMessage) {
                //接收到某条消息已读
                Log.e("=消息已读:" + ackMessage.getFrom() + "=" + ackMessage.getGuid(), "TAG");
                AckMessage ack = new AckMessage(ackMessage.getFrom(), ackMessage.getGuid());
                CMIMHelper.getCmMessageManager().sendMessage(ack, new CMChatListener.OnCMListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("=消息已读:onSuccess", "TAG");
                    }

                    @Override
                    public void onFailed(String s) {
                        Log.e("=消息已读:onFailed", "TAG");
                    }
                });
            }

            @Override
            public void onReceivedOwnerChangedMessage(CMMessage cmMessage, String s, CMMember cmMember) {

            }
        };
        CMIMHelper.addListeners(cmContactListener, cmMessageReceivedCallBack);
    }


    private void addNetStateListener() {
        CMIMHelper.getCmAccountManager().addConnectionListener(new CMChatListener.OnConnectionListener() {

            @Override
            public void onReConnected() {
                //账号与服务器断开连接后，自动重连成功
                Log.e("==onReConnected", "TAG");
                Intent onReConnected = new Intent(IMConst.NET_RECONNECT);
                //发送广播
                sendBroadcast(onReConnected);

            }

            @Override
            public void onDisConnected() {
                //账号与服务器异常断开连接
                Log.e("==onDisConnected", "TAG");
                Intent onDisConnected = new Intent(IMConst.NET_DISCONNECT);
                //发送广播
                sendBroadcast(onDisConnected);
                //重新连接
                mIsLoginSuccess = false;
                String userName = IMSharedPreferences.getString(IMSharedPreferences.ACCOUNT, null);
                String passWord = IMSharedPreferences.getString(IMSharedPreferences.PASSWORD, null);
                if (userName != null && passWord != null) {
                    doLogin(userName, passWord);
                    Log.e("RedoLogin", "TAG");
                }
            }

            @Override
            public void onAccountConflict() {
                //刷新数据
                IMApp.mContext.sendBroadcast(new Intent(IMConst.ACTION_RGISTER));
                //账号冲突的回调处理代码，同一个账号在其它终端上又登陆了
                Log.e("==账号冲突的onAccoct", "TAG");
            }

            @Override
            public void onAccountDestroyed() {
                //账号被后台删除的回调处理代码
                Log.e("onAccountDestroyed", "TAG");
            }
        });
    }

    @Override
    public void onDestroy() {
        IMApp.isBackGroundServiceRunning = false;
        stopForeground(true);
        //注销网络状态
        NetworkStateReceiver.removeRegisterObserver(observer);
        NetworkStateReceiver.unRegisterNetworkStateReceiver(this);
        super.onDestroy();
    }

}
