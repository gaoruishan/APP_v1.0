package test.grs.com.ims.contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.IMContactList;
import test.grs.com.ims.message.MessageHandle;
import test.grs.com.ims.view.CircularProgressBar;

/**
 *  通讯录  Created by gaoruishan on 15/10/12.
 */
public class ContractListActivity extends IMBaseActivity {

    private Context mContext;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapterList adapter;
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
    private LinearLayout contract_content,dialog_empty;
    private LinearLayout contract_empty;
    private CircularProgressBar loading_progress;
    private TextView recommend_to_friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contractlist);
        mContext = ContractListActivity.this;
        findView();
        initData();
        registerListener();
    }

    private void registerListener() {
        MessageHandle.getInstance().setOnRecivedMessageListener(new MessageHandle.OnRecivedMessageListener() {
            @Override
            public void onSelected(int type) {

            }

            @Override
            public void onBackSuccess(List<IMContactList> user) {

            }

            @Override
            public void onBackContactSuccess(List<SortModel> list) {
                if (list != null) {
                    if (list.size() == 0) {
                        loading_progress.setVisibility(View.GONE);
//                        dialog_empty.setVisibility(View.VISIBLE);
                        contract_empty.setVisibility(View.VISIBLE);
                        sideBar.setEnabled(false);
                        sideBar.setClickable(false);
                    }
                }
                if (list.size() > 0) {
                    setContactsDatas(list);
                }
            }
        });
    }

    private void findView() {
        mLoadingView = (LoadingView) findViewById(R.id.loading);
        loading_progress = (CircularProgressBar) findViewById(R.id.loading_progress);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        contract_content = (LinearLayout) findViewById(R.id.contract_content);
        dialog_empty = (LinearLayout) findViewById(R.id.dialog_empty);
        contract_empty = (LinearLayout) findViewById(R.id.contract_empty);
        recommend_to_friend = (TextView)findViewById(R.id.recommend_to_friend);
        recommend_to_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadcast(new Intent(IMConst.ACTION_STARTACTIVITY1));//推荐好友
            }
        });
    }


    public void doBack(View view) {
        finish();
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

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                String number = callRecords.get(((SortModel) adapter
                        .getItem(position)).getName());
                String name = ((SortModel) adapter.getItem(position)).getName();
//                Toast.makeText(ContractListActivity.this,"点击："+name+"="+number,Toast.LENGTH_SHORT).show();

            }
        });

        new AsyncTaskConstact(loading_progress).execute(0);


    }
    private class AsyncTaskConstact extends AsyncTaskBase {

        public AsyncTaskConstact(CircularProgressBar loadingView) {
            super(loadingView);
            // TODO Auto-generated constructor stub
        }


        @Override
        protected Integer doInBackground(Integer... params) {
            int result = -1;
            callRecords = ConstactUtil.getAllCallRecords(mContext);
            result = 1;
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            super.onPostExecute(result);
            if (result == 1) {

                ArrayList<String> constact = new ArrayList<String>();
                ArrayList<String> constact1 = new ArrayList<String>();
                for (Iterator<String> keys = callRecords.keySet().iterator(); keys
                        .hasNext(); ) {
                    String key = keys.next();
                    constact.add(key);

                    String s =callRecords.get(key);
                    if (s!=null&&!s.equals("null")&&!s.equals("")){
                        constact1.add(s);
                    }
                }
                 //更新UI
                if (callRecords!=null){//拒绝访问通讯录
                    if (callRecords.size()<=0){
                        contract_content.setVisibility(View.GONE);
                        contract_empty.setVisibility(View.VISIBLE);
                        sideBar.setEnabled(false);
                        sideBar.setClickable(false);
                    }else { // 接受访问通讯录
                        contract_content.setVisibility(View.VISIBLE);
                        contract_empty.setVisibility(View.GONE);
                        // 向服务器发送 所有联系人－－返回已注册的用户
                        sendBroadcast(new Intent(IMConst.ACTION_MAILLISTUSER).putStringArrayListExtra("list",constact1));
                    }
                }
                //设置适配器数据
//                setContactsDatas(constact);
            }
        }
    }

    private void setContactsDatas(List<SortModel> list) {
//        String[] names = new String[]{};
//        names = constact.toArray(names);
        SourceDateList = filledData(list);

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapterList(mContext, SourceDateList);
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


    private List<SortModel> filledData(List<SortModel> list) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < list.size(); i++) {
            SortModel sortModel = list.get(i);
            // 汉字转换成拼音
            String sortString = null;
            if (sortModel.getNickname()!=null&&!sortModel.getNickname().equals("")){
                String pinyin = characterParser.getSelling(sortModel.getNickname());
                sortString = pinyin.substring(0, 1).toUpperCase();
            }

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
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
//                String name = sortModel.getName();
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


}
