package test.grs.com.ims.contact;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.littlec.sdk.entity.CMGroup;
import com.littlec.sdk.entity.CMMember;
import com.littlec.sdk.entity.CMMessage;
import com.littlec.sdk.entity.messagebody.TextMessageBody;
import com.littlec.sdk.manager.CMIMHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.message.AppBaseAdapter;
import test.grs.com.ims.message.ContactChangeListener;
import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.IMContactList;
import test.grs.com.ims.message.IMMember;
import test.grs.com.ims.message.IMUtil;
import test.grs.com.ims.message.MessageActivity;
import test.grs.com.ims.message.MessageHandle;
import test.grs.com.ims.view.CircularProgressBar;
import test.grs.com.ims.view.HorizontalListView;
import test.grs.com.ims.view.RoundImageView;

/**
 * 转发 单聊添加 群聊添加
 */
public class ContactsActivity extends IMBaseActivity implements ContactChangeListener {

    public static final String SINGLE_RESULT_USER_NAME = "single_result_user_name";
    public static final String SINGLE_RESULT_USER_NICK = "single_result_user_nick";
    public static final int SINGLE_RESULT_CODE = 100;
    private Context mContext;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog, tv_dian, tv_ok, title;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
    private Map<String, String> callRecords;
    private LoadingView mLoadingView;
    /**
     * *汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    public int chatType = 0;
    public int navigate;
    public String chatUserName;
    private ArrayList<String> initGroupMemb = new ArrayList<String>();
    private ArrayList<String> initGroupMembNick = new ArrayList<String>();
    private RoundImageView ri_1, ri_2, ri_3, ri_4, ri_5;
    private BitmapUtils bitmapUtils;
    private ArrayList<CMMember> members;
    private LinearLayout ll_buttom, dialog_empty;
    private HorizontalListView hz_lv;
    private HorizontalLvAdapter hadapter;
    private List<String> hList = new ArrayList<String>();
    private ProgressDialog progressDialog;
    private CircularProgressBar loading_progress;
    private ArrayList<IMMember> mIMMembers = new ArrayList<IMMember>();
    private String groupId;
    private FrameLayout fl_container;
    private String chatUserNick;
    private String chatUserAvatar;
    private String groupDesc;
    private long fristTime;
    private long secondTime;
    private boolean onlyfirst = true;
    public static String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contract);
        mContext = ContactsActivity.this;
        findView();
        initData();

    }

    //注册监听回调
    private void registerLisenter() {
        MessageHandle.getInstance().setOnRecivedMessageListener(new MessageHandle.OnRecivedMessageListener() {
            @Override
            public void onSelected(int type) {

            }

            @Override
            public void onBackSuccess(List<IMContactList> list) {
                Log.e("==onBackSuccess", list.size() + "TAG" + list.toString());
                loading_progress.setVisibility(View.GONE);
                if (list != null) {
                    if (list.size() == 0) {
                        dialog_empty.setVisibility(View.VISIBLE);
                        sideBar.setEnabled(false);
                        sideBar.setClickable(false);
                        if (adapter == null) {
                            adapter = new SortAdapter(mContext, new ArrayList<SortModel>(), sortListView);
                        }
                        adapter.updateListView(new ArrayList<SortModel>());
                        return;
                    }
                } else {
                    list = new ArrayList<IMContactList>();
                }
                if (navigate == 1) {//只对我关注的人刷新数据
                    SourceDateList = filledData(list);
                    setSourceDatas();
                } else {//只对我关注的人刷新数据
                    SourceDateList = filledData(list);
                    setSourceDatas();
                }
            }

            @Override
            public void onBackContactSuccess(List<SortModel> list) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新数据(我关注的和关注我的)
        refreshDatas();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @SuppressLint("LongLogTag")
    private void initData() {
        final Intent intent = getIntent();
        if (intent != null) {
            chatType = intent.getIntExtra(IMConst.CHOOSE_CONTACT_TYPE, -1);
            navigate = intent.getIntExtra(IMConst.NAVIGATE_DESTINATION, -1);
            if (chatType == 0) {//单聊添加
                chatUserName = intent.getStringExtra(IMConst.USERNAME);
                chatUserNick = intent.getStringExtra(IMConst.NICKNAME);
                chatUserAvatar = intent.getStringExtra(IMConst.AVATARURL);
                if (chatUserName != null && !chatUserName.isEmpty()) {
                    //添加群组成员
                    if (members==null){
                        members = new ArrayList<CMMember>();
                    }
                    CMMember member = new CMMember(chatUserName, chatUserNick+","+chatUserAvatar);
                    member.setNeedApprovalRequired(false);
                    //为true时,成员会收到邀请信息,需得到对方同意才可入群。为false时,不需要对方同意直接入群。默认是false,即 不需要对方同意。
                    members.add(member);
                }
            } else if (chatType == 1) {//群聊添加
                mIMMembers = (ArrayList<IMMember>) getIntent().getSerializableExtra(IMConst.GROUPMEMEBER);
                groupId = getIntent().getStringExtra(IMConst.GROUPID);
            }
            //详情页ID
            user_id = intent.getStringExtra(IMConst.USER_ID);
        }
        Log.e("=navigate=" + navigate + "=" + chatType + "chatUserName=" + chatUserName + "mIMMembers=" + mIMMembers.size(), "TAG");
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @SuppressLint("NewApi")
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                if (adapter!=null&&s!=null&&s.length()>0){
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        sortListView.setSelection(position);
                    }
                }else {
                    sideBar.setEnabled(false);
                    sideBar.setClickable(false);
                    dialog.setVisibility(View.GONE);
                }
            }
        });

        new AsyncTaskConstact(loading_progress).execute(0);

    }

    public void refreshDatas() {
        if (navigate == 0 || navigate == 2) {//转发 或 显示我关注的人
            ll_buttom.setVisibility(View.GONE);
            if (user_id != null) {
                title.setText("TA关注的人");
                //加载TA关注的人
                sendBroadcast(new Intent(IMConst.ACTION_MYATTENTION_TA).putExtra(IMConst.USER_ID, user_id));
            } else {
                title.setText("我关注的人");
                // 加载我关注的人
                sendBroadcast(new Intent(IMConst.ACTION_MYATTENTION));
            }

        } else if (navigate == 1) {//显示关注我的人
            ll_buttom.setVisibility(View.GONE);
            if (user_id != null) {
                title.setText("关注TA的人");
                //加载关注TA的人
                sendBroadcast(new Intent(IMConst.ACTION_PAYATTENTIONTOME_TA).putExtra(IMConst.USER_ID, user_id));
            } else {
                title.setText("关注我的人");
                //加载关注我的人
                sendBroadcast(new Intent(IMConst.ACTION_PAYATTENTIONTOME));
            }
        } else {
            ll_buttom.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, IMUtil.dip2px(this, 55));
            fl_container.setLayoutParams(params);
            //加载我关注的人
            sendBroadcast(new Intent(IMConst.ACTION_MYATTENTION));
        }
        registerLisenter(); //注册监听
        Log.e("=navigate=" + navigate, "user_id=" + user_id);
    }

    private void findView() {
        mLoadingView = (LoadingView) findViewById(R.id.loading);
        loading_progress = (CircularProgressBar) findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);
        ll_buttom = (LinearLayout) findViewById(R.id.ll_buttom);
        dialog_empty = (LinearLayout) findViewById(R.id.dialog_empty);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        title = (TextView) findViewById(R.id.title);
        tv_dian = (TextView) findViewById(R.id.tv_dian);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        ri_1 = (RoundImageView) findViewById(R.id.ri_1);
        ri_2 = (RoundImageView) findViewById(R.id.ri_2);
        ri_3 = (RoundImageView) findViewById(R.id.ri_3);
        ri_4 = (RoundImageView) findViewById(R.id.ri_4);
        ri_5 = (RoundImageView) findViewById(R.id.ri_5);
        fl_container = (FrameLayout) findViewById(R.id.fl_container);
        bitmapUtils = new BitmapUtils(this, IMConst.GLOBALSTORAGE_DOWNLOAD_PATH);
        bitmapUtils.configDiskCacheEnabled(true);//防止溢出
        if (Build.VERSION.SDK_INT >= 21) {
            bitmapUtils.configMemoryCacheEnabled(false);
        }
        hz_lv = (HorizontalListView) findViewById(R.id.hz_lv);
        hadapter = new HorizontalLvAdapter(hList, ContactsActivity.this);
        hz_lv.setAdapter(hadapter);

    }

    public void doBack(View view) {
        finish();
    }

    /**
     * 点击完成
     *
     * @param view
     */
    public void doOK(View view) {

        if (initGroupMemb != null) {
            // 创建群
            if (initGroupMemb == null || initGroupMemb.size() < 1) {
                Toast.makeText(this, "请至少选择1个联系人",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (initGroupMemb.size() > 99) {
                Toast.makeText(this, "已超出群成员最多100人上限",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatType == 0) { //单聊 创建群组
                //创建群组
                groupDesc = IMApp.getCurrentAvataUrl() + "," + chatUserAvatar;
                creatGroup(IMApp.getCurrentUserNick() + "," + chatUserNick + ",");
            } else if (chatType == 1) {//群组添加成员
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < initGroupMemb.size(); i++) {
                    String meb = initGroupMemb.get(i);
                    String mebnick = initGroupMembNick.get(i);
                    if (members == null) {
                        members = new ArrayList<CMMember>();
                    }
                    members.add(new CMMember(meb, mebnick));
                    list.add(meb);
                }
//                for (String meb : initGroupMemb) {
//                    Log.e("用户名＝" + meb, "TAG");
//                    if (members == null) {
//                        members = new ArrayList<CMMember>();
//                    }
//                    members.add(new CMMember(meb, meb));
//                    list.add(meb);
//                }
                try {
                    if (groupId != null && members != null) {
                        CMIMHelper.getCmGroupManager().inviteMembersToGroup(groupId, members);
                        Log.e("添加成员", "TAG");
                        Toast.makeText(ContactsActivity.this, "添加成员成功", Toast.LENGTH_SHORT).show();
                        //添加群组更新
//                        CMMessage message = new CMMessage(1, groupId, new TextMessageBody(""));
//                        message.setContentType(5);//通知类
//                        message.setTime(System.currentTimeMillis());
//                        message.setFrom(IMApp.currentUserName);
//                        message.setFromNick(IMApp.getCurrentUserNick());// 创建群主的昵称
//                        message.setExtra(IMApp.getCurrentUserNick() + "," + IMApp.getCurrentUserNick());
//                        MessageHandle.getInstance().doReceivedAddMembersMessage(message,groupId,members);
                        setResult(IMConst.ADD_RESULTT_CODE, new Intent().putStringArrayListExtra(IMConst.STRINGLIST, list));
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 调用创建群接口
     */
    private void creatGroup(String groupName) {
        try {
            for (int i = 0; i < initGroupMemb.size(); i++) {
                String meb = initGroupMemb.get(i);
                String mebnick = initGroupMembNick.get(i);
                String mebAvatar = hList.get(i);
                members.add(new CMMember(meb, mebnick));
//                members.add(new CMMember(meb, mebnick+","+mebAvatar));
                if (i == initGroupMemb.size() - 1) {
                    groupName += mebnick;
                    groupDesc += mebAvatar;
                } else {
                    groupName += mebnick + ",";
                    groupDesc += mebAvatar + ",";
                }
            }
//            new CreateGroupTask().execute();
            Log.e("=groupName"+groupName,",groupDesc"+groupDesc+",members"+members.size());
            CMGroup cmGroup = CMIMHelper.getCmGroupManager().createGroup(groupName, groupDesc, IMApp.currentUserName, members);
            //保存message和contact
            Log.e("=members:", "" + cmGroup.getGroupDesc());
            CMMessage message = new CMMessage(1, cmGroup.getGroupId(), new TextMessageBody(""));
            message.setContentType(5);//通知类
            message.setTime(System.currentTimeMillis());
            message.setFrom(IMApp.currentUserName);
            message.setFromNick(IMApp.getCurrentUserNick());// 创建群主的昵称
            message.setExtra(IMApp.getCurrentUserNick()+","+IMApp.getCurrentUserNick());
//            message.setExtra(groupDesc);//所有的头像
            MessageHandle.getInstance().updateCreateLocalDataBase(message, cmGroup);
            //保存消息列表
            MessageHandle.getInstance().saveToIMContact(message, cmGroup, 0);
            // 保存群组
//            MessageHandle.getInstance().saveToIMGroup(cmGroup);
            Intent intent = new Intent(ContactsActivity.this, MessageActivity.class);
            intent.putExtra(IMConst.CHATTYPE, true);//是群聊
            intent.putExtra(IMConst.GROUPNAME, groupName);//USERNAME 群名
            intent.putExtra(IMConst.GROUPAVATAR, groupDesc);
            intent.putExtra(IMConst.GROUPID, cmGroup.getGroupId());
            startActivity(intent);
            if (progressDialog!=null){
                progressDialog.dismiss();
            }
            //更新
            sendBroadcast(new Intent(IMConst.ACTION_ONLY_GROUP));
            finish();
        } catch (Exception e) {
            e.printStackTrace();//创建过程出现
            Log.e("==",e.toString());
            Toast.makeText(this, "因网络连接，创建失败", Toast.LENGTH_SHORT).show();
//            IMApp.getInstance().doLogin(null, null);
        }
    }

    /**
     * a-z进行排序源数据h和 过滤搜索
     */
    private void setSourceDatas() {
        loading_progress.setVisibility(View.GONE);
        if (SourceDateList == null || SourceDateList.size() <= 0) {
            dialog_empty.setVisibility(View.VISIBLE);
            sideBar.setEnabled(false);
            sideBar.setClickable(false);
            Log.e("TAG", "数据为空");
            return;
        }
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        if (adapter==null){
            adapter = new SortAdapter(mContext, SourceDateList, sortListView);
            sortListView.setAdapter(adapter);
        }else {
            adapter.updateListView(SourceDateList);
        }
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onlyfirst) {
                    filterData(s.toString());
                    onlyfirst = false;
                    fristTime = System.currentTimeMillis();
                }
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                if (System.currentTimeMillis() - fristTime > 500) {
                    fristTime = System.currentTimeMillis();
                    // 根据a-z进行排序
                    filterData(s.toString());
                    Log.e("==filterData", "");
                }
                Log.e("==secondT - frist", "" + (System.currentTimeMillis() - fristTime));
            }
        });
    }

    /**
     * 水平livtview适配器
     */
    class HorizontalLvAdapter extends AppBaseAdapter<String> {

        public HorizontalLvAdapter(List<String> list, Context context) {
            super(list, context);
        }

        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            Holder holder;
//            if (convertView==null){
            convertView = inflater.inflate(R.layout.item_iv, null);
            holder = new Holder();
            holder.rv = (RoundImageView) convertView.findViewById(R.id.hz_lv_iv);
//                convertView.setTag(holder);
//            }else {
//               holder = (Holder) convertView.getTag();
//            }
            if (list.get(position)!=null&&!list.get(position).equals("")){
                bitmapUtils.display(holder.rv, list.get(position));
                bitmapUtils.configDefaultLoadFailedImage(R.drawable.recommand_bgs);
            }else {
                holder.rv.setImageResource(R.drawable.recommand_bgs);
            }
            return convertView;
        }
        class Holder {
            RoundImageView rv;
        }
    }

    private List<IMContactList> allContacts;
    private List<IMContactList> unallContacts;

    @Override
    public void onContactChanged() {

    }

    @Override
    public void onContactChanged(List<SortModel> selected) {
        initGroupMemb.clear();
        initGroupMembNick.clear();
        hList.clear();
        if (selected != null && selected.size() > 0) {
            Log.e("selected.size()=" + selected.size(), "TAG");
            for (int i = 0; i < selected.size(); i++) {
                tv_ok.setEnabled(true);
                initGroupMemb.add(selected.get(i).getName());
                initGroupMembNick.add(selected.get(i).getNickname());
                hList.add(selected.get(i).getAvatar_url());//头像集
            }
        } else {
            tv_ok.setEnabled(true);
        }
        //刷新
        hadapter.setDatasChange(hList);
    }


    private class AsyncTaskConstact extends AsyncTaskBase {

        public AsyncTaskConstact(CircularProgressBar loadingView) {
            super(loadingView);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int result = -1;
            if (navigate == 0 || navigate == 2 || navigate == -1) {  //我关注的人 －
//                try {
//                    allContacts = IMApp.geDbUtils().findAll(IMContactList.class);
//
//                } catch (DbException e) {
//                    e.printStackTrace();
//                }
                result = 1;
            } else if (navigate == 1) {//关注我的人，回调
                result = 1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 1) {
                if (navigate == 0 || navigate == 2 || navigate == -1) {
                    loading_progress.setVisibility(View.VISIBLE);
//                    SourceDateList = filledData(allContacts);
//                    setSourceDatas();
                } else if (navigate == 1) {
                    loading_progress.setVisibility(View.VISIBLE);
//                    SourceDateList = filledUnData(unallContacts);
                }

            }

        }

    }

    /**
     * 填充并转换数据 －－关注的人
     *
     * @param allContacts
     * @return
     */
    private List<SortModel> filledData(List<IMContactList> allContacts) {

        List<SortModel> mSortList = new ArrayList<SortModel>();
        if (allContacts == null || allContacts.size() <= 0) {
            mLoadingView.setVisibility(View.GONE);
            dialog_empty.setVisibility(View.VISIBLE);
            sideBar.setEnabled(false);
            sideBar.setClickable(false);
            Log.e("TAG", "数据为空");
            return mSortList;
        } else {
            dialog_empty.setVisibility(View.GONE);
        }
        for (int i = 0; i < allContacts.size(); i++) {
            SortModel sortModel = new SortModel();
            String userName = allContacts.get(i).getUserName();
            String nickName = allContacts.get(i).getNickname();
            String avatar_url = allContacts.get(i).getAvatarurl();
            String introduce = allContacts.get(i).getIntroduce();// 简介
            boolean friend = allContacts.get(i).isFriend();
            sortModel.setName(userName);
            sortModel.setNickname(nickName);
            sortModel.setAvatar_url(avatar_url);
            sortModel.setIntroduction(introduce);
            sortModel.setIsfriend(friend);
            //转发
            if (navigate == 0) {
                sortModel.setType(SortModel.TYPE_FORWARD);
            } else if (navigate == 1) {
                sortModel.setType(SortModel.TYPE_UNATTENTION);
            } else if (navigate == 2) {
                sortModel.setType(SortModel.TYPE_ATTENTION);
            } else {
                sortModel.setType(SortModel.TYPE_ADD);
            }
            //单聊添加
            if (userName.equals(chatUserName)) {
                sortModel.setSelect(true);// 选中当前聊天对象\
                sortModel.setIsfriend(true);
            }
            if (userName.equals(IMApp.currentUserName)) {
                sortModel.setSelect(true);// 选中自己
                sortModel.setIsfriend(true);
            }
            //群组添加
            if (chatType == 1) {
                sortModel.setType(SortModel.TYPE_ADD);
                for (IMMember gm : mIMMembers) {
                    if (gm.getUserName().equals(userName)) {
                        sortModel.setSelect(true);//选中已有成员对象
                        sortModel.setIsfriend(true);
                        break;//外层有匹配 结束此循环
                    }
                }
            }
            // 汉字转换成拼音 nickName
            String sortString = null;
            if (nickName != null && !nickName.equals("")) {
                String pinyin = characterParser.getSelling(nickName);
                sortString = pinyin.substring(0, 1).toUpperCase();
            }
            // 正则表达式，判断首字母是否是英文字母
            if (sortString != null && sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private synchronized void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();

            for (SortModel sortModel : SourceDateList) {
//                String name = sortModel.getName();//筛选昵称
                String name = sortModel.getNickname();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                        filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }
//        final List<SortModel> finalFilterDateList = filterDateList;
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
        Log.e("==postDelayed" + filterDateList, "");
//            }
//        }, 300);

    }

    public static class ChooseContactType {
        public static final int SINGLE_CONTACT = 0;
        public static final int MULTI_CONTACTS = 1;
        public static final int MULTI_GROUP_CONTACTS = 2;
        public static final int SINGLE_GROUP_MEMBER = 3;
    }

    /**
     * 选择联系人要执行的后续操作，在此添加类型
     *
     * @author xuyongjie
     */
    public static class NavigateDestination {
        /**
         * 转发
         */
        public static final int TO_FORWARD = 0;
        /**
         * 建群
         */
        public static final int TO_CREATE_GROUP = 1;
        /**
         * 邀请入群
         */
        public static final int TO_INVITE_MEMBERS_TO_GROUP = 2;
        /**
         * 发起 会议
         */
        public static final int TO_LAUNCH_MEETING = 3;
    }


    private class CreateGroupTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }
    }
}
