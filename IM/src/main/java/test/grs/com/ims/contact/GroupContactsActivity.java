package test.grs.com.ims.contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.littlec.sdk.manager.CMIMHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.message.AppBaseAdapter;
import test.grs.com.ims.message.ContactChangeListener;
import test.grs.com.ims.message.DialogFactory;
import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.IMGroup;
import test.grs.com.ims.message.IMMember;
import test.grs.com.ims.message.IMUtil;
import test.grs.com.ims.message.MessageHandle;
import test.grs.com.ims.view.CircularProgressBar;
import test.grs.com.ims.view.HorizontalListView;
import test.grs.com.ims.view.RoundImageView;

/**
 * 群组移除 查看群成员 群聊
 */
public class GroupContactsActivity extends IMBaseActivity implements ContactChangeListener {

    public static final String SINGLE_RESULT_USER_NAME = "single_result_user_name";
    public static final int SINGLE_RESULT_CODE = 100;
    private Context mContext;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog, tv_dian, tv_ok, title;
    private LinearLayout dialog_empty;
    private SortAdapterGroup adapter;
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
    public static int chatType;
    public static int navigate = -1;
    private CircularProgressBar loading_progress;
    private ArrayList<IMMember> IMMembers;
    private boolean b, o;
    private BitmapUtils bitmapUtils;
    private HorizontalListView hz_lv;
    private List<String> hList = new ArrayList<String>();
    private HorizontalLvAdapter hadapter;
    private LinearLayout ll_buttom;
    private FrameLayout fl_container;
    private ArrayList<String> initGroupMemb = new ArrayList<String>();
    private String groupId;
    private boolean isOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_groupcontract);
        mContext = GroupContactsActivity.this;
        findView();
        if (getIntent() != null) {
            // 查看群成员
            IMMembers = (ArrayList<IMMember>) getIntent().getSerializableExtra(IMConst.GROUPMEMEBER);
            b = getIntent().getBooleanExtra(IMConst.GROUPREMOVE, false);
            isOwner = getIntent().getBooleanExtra(IMConst.OWNER, false);
            title.setText("群组");
            if (b) {
                //群聊
                groupId = getIntent().getStringExtra(IMConst.GROUPID);
                o = getIntent().getBooleanExtra(IMConst.OTHER, false);
                title.setText("查看群成员");
                if (o) {
                    //群组移除
                    ll_buttom.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, IMUtil.dip2px(this, 55));
                    fl_container.setLayoutParams(params);
                    title.setText("群组移除");
                }

            }
        }
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (IMMembers == null) {
            //加载群组
            sendBroadcast(new Intent(IMConst.ACTION_GROUP));
//            loading_progress.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {

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
                if (adapter != null && s != null && s.length() > 0) {
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        sortListView.setSelection(position);
                    }
                } else {
                    sideBar.setEnabled(false);
                    sideBar.setClickable(false);
                    dialog.setVisibility(View.GONE);
                }
            }
        });
//        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
//                String nameId = ((SortModel) adapter.getItem(position)).getName();
//                String name = ((SortModel) adapter.getItem(position)).getNickname();
//                Intent intent = new Intent(GroupContactsActivity.this, MessageActivity.class);
//                intent.putExtra(IMConst.CHATTYPE, true);
//                intent.putExtra(IMConst.GROUPNAME, name);
//                intent.putExtra(IMConst.GROUPID, nameId);
//                startActivity(intent);
//            }
//        });

        new AsyncTaskConstact(loading_progress).execute(0);

        MessageHandle.getInstance().setOnGroupListener(new MessageHandle.OnGroupListener() {
            @Override
            public void setGroupList(List<CMGroup> groups) {
                loading_progress.setVisibility(View.GONE);
                dialog_empty.setVisibility(View.GONE);
                if (groups == null || groups.size() == 0) {
                    dialog_empty.setVisibility(View.VISIBLE);
                    sideBar.setEnabled(false);
                    sideBar.setClickable(false);
                    Log.e("=TAG", "数据为空");
                    return;
                }
                if (groups != null && groups.size() > 0) {
                    SourceDateList = filledData(groups);
                    //刷新
                    setSrcDatas();
                    Log.e("==CMGroup" + groups.get(0).getGroupName(), "," + groups.get(0).getGroupDesc() + "," + groups.get(0).getMembers());
                    Log.e("==groups" + groups.size(), "," + groups.get(groups.size() - 1).getGroupName() + ",=" + groups.get(groups.size() - 1).getGroupDesc());
                }
            }
        });
    }

    private void findView() {
        mLoadingView = (LoadingView) findViewById(R.id.loading);
        loading_progress = (CircularProgressBar) findViewById(R.id.loading_progress);
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        title = (TextView) findViewById(R.id.title);
        dialog_empty = (LinearLayout) findViewById(R.id.dialog_empty);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        ll_buttom = (LinearLayout) findViewById(R.id.ll_buttom);
        fl_container = (FrameLayout) findViewById(R.id.fl_container);
        bitmapUtils = new BitmapUtils(this, IMConst.GLOBALSTORAGE_DOWNLOAD_PATH);
        bitmapUtils.configDiskCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            bitmapUtils.configMemoryCacheEnabled(false);
        }
        hz_lv = (HorizontalListView) findViewById(R.id.hz_lv);
        hadapter = new HorizontalLvAdapter(hList, GroupContactsActivity.this);
        hz_lv.setAdapter(hadapter);
    }

    public void doBack(View view) {
        finish();
    }

    /**
     * 移除成员
     *
     * @param view
     */
    public void doOK(View view) {
        if (initGroupMemb != null) {
            // 移除成员
            if (initGroupMemb == null || initGroupMemb.size() < 1) {
                Toast.makeText(this, "请至少选择1个联系人",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            View.OnClickListener okBtnEvent = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //在群 ID 为 currentGroup.getGroupId()的群组里将 id 为 kickid 的群成员剔除掉
                        for (String meb : initGroupMemb) {
                            Log.e("用户名＝" + meb, "TAG");
                            CMIMHelper.getCmGroupManager().kickMemberFromGroup(groupId, meb);
                        }
                        Toast.makeText(GroupContactsActivity.this, "移除成员成功", Toast.LENGTH_SHORT).show();
                        setResult(IMConst.REMOVER_RESULTT_CODE, new Intent().putStringArrayListExtra(IMConst.STRINGLIST, initGroupMemb));
                        finish();
                    } catch (Exception e) {
                        //剔除过程中出现了异常
                        e.printStackTrace();
                    }
                }
            };
            DialogFactory.getConfirmDialog(GroupContactsActivity.this, getResources().getString(R.string.del_chatmember_data),
                    getResources().getString(R.string.dialog_clear_member_prompt_content), getResources().getString(R.string.btn_subject_cancel),
                    getResources().getString(R.string.btn_subject_confirm), null, okBtnEvent).show();

        }

    }


    @Override
    public void onContactChanged() {

    }

    @Override
    public void onContactChanged(List<SortModel> selected) {
        initGroupMemb.clear();
        hList.clear();
        if (selected != null && selected.size() > 0) {
            Log.e("selected.size()=" + selected.size(), "TAG");
            tv_ok.setEnabled(true);
            for (int i = 0; i < selected.size(); i++) {
                initGroupMemb.add(selected.get(i).getName());
                hList.add(selected.get(i).getAvatar_url());
            }
        } else {
            tv_ok.setEnabled(false);
        }
        //刷新
        hadapter.setDatasChange(hList);
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
            if (list.get(position) != null && !list.get(position).equals("")) {
                bitmapUtils.display(holder.rv, list.get(position));
                bitmapUtils.configDefaultLoadFailedImage(R.drawable.recommand_bgs);
            } else {
                holder.rv.setImageResource(R.drawable.recommand_bgs);
            }
            return convertView;
        }

        class Holder {
            RoundImageView rv;
        }
    }

    private static List<CMGroup> allContacts;


    private int result;

    private class AsyncTaskConstact extends AsyncTaskBase {

        public AsyncTaskConstact(CircularProgressBar loadingView) {
            super(loadingView);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            result = -1;
            if (IMMembers != null && IMMembers.size() > 0) {
                result = 2;
            } else {
//                if (MessageHandle.mCMGroupLists != null && MessageHandle.mCMGroupLists.size() > 0) {
//                    allContacts = MessageHandle.mCMGroupLists;
//                    result = 1;
//                } else {
                //加载群组
//                sendBroadcast(new Intent(IMConst.ACTION_GROUP));
//                CMIMHelper.getCmGroupManager().getGroupListFromServer(new CMChatListener.OnGroupListener() {
//                    @Override
//                    public void onSuccess(List<CMGroup> groups) {
//                        //获取群列表成功的处理,groups 为拥有的群列表,包含有群 id,群名称,群的免打扰状态
//                        allContacts = groups;
//                        result = 1;
//                    }
//
//                    @Override
//                    public void onFailed(String failedMsg) {
//                        // 获取群列表失败的处理,failedMsg 为失败原因
//                    }
//                });
//                }
            }


            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            loading_progress.setVisibility(View.GONE);
            if (result == 1) {//网络获取群组列表
                if (allContacts == null || allContacts.size() == 0) {
                    dialog_empty.setVisibility(View.VISIBLE);
                    sideBar.setEnabled(false);
                    sideBar.setClickable(false);
                    Log.e("=TAG", "数据为空");
                    return;
                }
                SourceDateList = filledData(allContacts);
                //刷新
                setSrcDatas();

            } else if (result == 2) {// 查看群组成员
                SourceDateList = filledGroupData(IMMembers);
                //刷新
                setSrcDatas();
            }
        }
    }

    private void setSrcDatas() {
        if (SourceDateList == null || SourceDateList.size() <= 0) {
            dialog_empty.setVisibility(View.VISIBLE);
            return;
        }
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapterGroup(mContext, SourceDateList);
        sortListView.setAdapter(adapter);
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private List<SortModel> filledGroupData(ArrayList<IMMember> gm) {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        int size;
        if (isOwner) {
            size = gm.size() - 2;
        } else {
            size = gm.size() - 1;
        }
        for (int i = 0; i < size; i++) {
            SortModel sortModel = new SortModel();
            String userName = gm.get(i).getUserName();
            String nickName = gm.get(i).getName();
            sortModel.setName(userName);
            sortModel.setNickname(nickName);
            sortModel.setAvatar_url(gm.get(i).getAvatarUri());
            sortModel.setType(SortModel.TYPE_GROUP);
            if (b) {
                sortModel.setType(SortModel.MEMBER);
                if (o) {
                    sortModel.setType(SortModel.TYPE_REMOVE);
                }
            }
            // 汉字转换成拼音
            Log.e(userName, "TAG");
            // 汉字转换成拼音
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
     * 填充并转换数据
     *
     * @param allContacts
     * @return
     */
    private List<SortModel> filledData(List<CMGroup> allContacts) {

        List<SortModel> mSortList = new ArrayList<SortModel>();
        //获得取消保存
        List<IMGroup> groups = MessageHandle.getInstance().getIMGroups();
        for (int i = 0; i < allContacts.size(); i++) {
            //不显示的取消本次循环
            boolean is = false;
            if (groups.size() > 0) {
                for (IMGroup m : groups) {
                    if (m.getGroupId().equals(allContacts.get(i).getGroupId())) {
                        is = true;
                        break;
                    }
                }
                if (is) {
                    continue;
                }
            }
            SortModel sortModel = new SortModel();
            String nickName = allContacts.get(i).getGroupName();
            String userName = allContacts.get(i).getGroupId();
            String groupAvatar = allContacts.get(i).getGroupDesc();
            sortModel.setName(userName);
            sortModel.setNickname(nickName);
            sortModel.setAvatar_url(groupAvatar);//使用描述（整个群的成员头像）代替头像
            sortModel.setType(SortModel.TYPE_GROUP);
            if (b) {
                sortModel.setType(SortModel.MEMBER);
                if (o) {
                    sortModel.setType(SortModel.TYPE_REMOVE);
                }
            }
            // 汉字转换成拼音
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
            //添加到集合中并保存数据库
//            imGroups.add(IMUtil.getCMGroupToIMGroup(allContacts.get(i)));
//            if (i == allContacts.size() - 1) {
//                try {
//                    IMApp.geDbUtils().deleteAll(IMGroup.class);
//                    IMApp.geDbUtils().saveAll(imGroups);
//                } catch (DbException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        return mSortList;

    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getNickname();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                        filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
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

}
