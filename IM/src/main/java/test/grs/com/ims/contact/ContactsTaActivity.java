package test.grs.com.ims.contact;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.littlec.sdk.entity.CMMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.IMContactList;
import test.grs.com.ims.message.IMMember;
import test.grs.com.ims.message.MessageHandle;
import test.grs.com.ims.view.CircularProgressBar;
import test.grs.com.ims.view.HorizontalListView;
import test.grs.com.ims.view.RoundImageView;

/**
 * Ta关注的人和关注Ta的人
 */
public class ContactsTaActivity extends IMBaseActivity {

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
    public static int navigate;
    private String chatUserName;
    private ArrayList<String> initGroupMemb = new ArrayList<String>();
    private ArrayList<String> initGroupMembNick = new ArrayList<String>();
    private RoundImageView ri_1, ri_2, ri_3, ri_4, ri_5;
    private BitmapUtils bitmapUtils;
    private ArrayList<CMMember> members;
    private LinearLayout ll_buttom, dialog_empty;
    private HorizontalListView hz_lv;
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
        mContext = ContactsTaActivity.this;
        findView();
        initData();
        //刷新数据(Ta关注的和关注TA的)
        refreshDatas();
    }

    //注册监听回调
    private void registerLisenter() {
        MessageHandle.getInstance().setOnRecivedMessageListener(new MessageHandle.OnRecivedMessageListener() {
            @Override
            public void onSelected(int type) {

            }

            @Override
            public void onBackSuccess(final List<IMContactList> list) {
                Log.e("==Ta-onBackSuccess", list.size() + "TAG" + list.toString());
                loading_progress.setVisibility(View.GONE);
                if (list != null) {
                    if (list.size() == 0) {
                        dialog_empty.setVisibility(View.VISIBLE);
                        sideBar.setEnabled(false);
                        sideBar.setClickable(false);
                        return;
                    }
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
    }

    @SuppressLint("LongLogTag")
    private void initData() {
        final Intent intent = getIntent();
        if (intent != null) {
            chatType = intent.getIntExtra(IMConst.CHOOSE_CONTACT_TYPE, -1);
            navigate = intent.getIntExtra(IMConst.NAVIGATE_DESTINATION, -1);
            //详情页ID
            user_id = intent.getStringExtra(IMConst.USER_ID);
        }
        Log.e("navigate=" + navigate + "=" + chatType + "chatUserName=" + chatUserName + "mIMMembers=" + mIMMembers.size(), "TAG");
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
            }

        } else if (navigate == 1) {//显示关注我的人
            ll_buttom.setVisibility(View.GONE);
            if (user_id != null) {
                title.setText("关注TA的人");
                //加载关注TA的人
                sendBroadcast(new Intent(IMConst.ACTION_PAYATTENTIONTOME_TA).putExtra(IMConst.USER_ID, user_id));
            }
        }
        registerLisenter(); //注册监听
        Log.e("navigate=" + navigate, "user_id=" + user_id);
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
    }

    public void doBack(View view) {
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
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
        adapter = new SortAdapter(mContext, SourceDateList, sortListView);
        sortListView.setAdapter(adapter);
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
                }
            }
        });
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
                sortModel.setSelect(true);// 选中当前聊天对象
            }
            if (userName.equals(IMApp.currentUserName)) {
                sortModel.setSelect(true);// 选中自己
            }
            //群组添加
            if (chatType == 1) {
                for (IMMember gm : mIMMembers) {
                    if (gm.getUserName().equals(userName)) {
                        sortModel.setSelect(true);//选中已有成员对象
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

    public class SortAdapter extends BaseAdapter implements SectionIndexer {
        private BitmapUtils bitmapUtils;
        public List<SortModel> list;
        private Context mContext;
        private List<SortModel> selectlist = new ArrayList<SortModel>();

        public SortAdapter(Context mContext, List<SortModel> list, ListView listView) {
            this.mContext = mContext;
            this.list = list;
            bitmapUtils = new BitmapUtils(mContext, IMConst.GLOBALSTORAGE_DOWNLOAD_PATH);
            bitmapUtils.configDiskCacheEnabled(true);
            if (Build.VERSION.SDK_INT == 21) {
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
            if (username.equals(IMApp.currentUserName)) {
                viewHolder.cb_select.setChecked(true);
                viewHolder.cb_select.setEnabled(false);
            }
            String avatar_url = sortModel.getAvatar_url();
            if (avatar_url != null && !avatar_url.equals("")) {
                bitmapUtils.display(viewHolder.icon, avatar_url);
                bitmapUtils.configDefaultLoadFailedImage(R.drawable.recommand_bgs);
            } else {
                viewHolder.icon.setImageResource(R.drawable.recommand_bgs);
            }
            if (sortModel.getName().equals(IMApp.currentUserName)){
                viewHolder.ll_constact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ContactsTaActivity.this, "别点了，是“我”", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
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
            }

            return view;

        }

        class ViewHolder {
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
