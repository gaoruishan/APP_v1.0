package test.grs.com.ims.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.littlec.sdk.entity.CMContact;
import com.littlec.sdk.manager.CMContactManager;
import com.littlec.sdk.manager.CMIMHelper;
import com.littlec.sdk.utils.CMChatListener;
import com.littlec.sdk.utils.NetworkUtil;
import com.littlec.sdk.utils.SdkUtils;

import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.view.CircularProgressBar;

public class SearchContactActivity extends IMBaseActivity {

    private EditText searchEditText;
    private TextView searchTextView;
    private InputMethodManager manager;
    private ListView resultListView;
    private TextView noResultTextView;
    public static final int SEARCH_CONTACTS_START=0;
    public static final int SEARCH_CONTACTS_OK=1;
    public static final int SEARCH_CONTACTS_FAILED=2;
    public  static final int SEARCH_CONTACTS_NO_RESULT=3;
    private ContactSearchResultAdapter adapter;
    private Handler uiHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SEARCH_CONTACTS_START:
                    loading_progress.setVisibility(View.VISIBLE);
                    break;
                case SEARCH_CONTACTS_OK:
                    resultListView.setVisibility(View.VISIBLE);
                    noResultTextView.setVisibility(View.GONE);
                    loading_progress.setVisibility(View.GONE);
                    ArrayList list=msg.getData().getParcelableArrayList("search_result_contacts");
                    adapter=new ContactSearchResultAdapter(SearchContactActivity.this,(ArrayList<CMContact>)list.get(0));
                    resultListView.setAdapter(adapter);
                    break;
                case SEARCH_CONTACTS_FAILED:
                    String _errMsg=msg.getData().getString("error_msg");
                    loading_progress.setVisibility(View.GONE);
                    Toast.makeText(SearchContactActivity.this, TextUtils.isEmpty(_errMsg)?"查询失败":_errMsg, Toast.LENGTH_SHORT).show();
                    break;
                case SEARCH_CONTACTS_NO_RESULT:
                    resultListView.setVisibility(View.GONE);
                    loading_progress.setVisibility(View.GONE);
                    noResultTextView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        };
    };
    private CircularProgressBar loading_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);
        searchEditText=(EditText)findViewById(R.id.search_editText);
        loading_progress=(CircularProgressBar)findViewById(R.id.loading_progress);
        searchTextView=(TextView)findViewById(R.id.search_contact_textView);
        manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        resultListView=(ListView)findViewById(R.id.search_result_contacts_listView);
        noResultTextView=(TextView)findViewById(R.id.none_result_textView);
        resultListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                hideKeyboard();
                return false;
            }
        });
        searchTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!NetworkUtil.isNetworkConnected(SearchContactActivity.this)) {
                    Toast.makeText(SearchContactActivity.this, "网络不通，请检查网络设置",Toast.LENGTH_SHORT).show();
                    return;
                }
                final String key = searchEditText.getText().toString();
                resultListView.setAdapter(null);
                if (key == null || key.isEmpty()) {
                    noResultTextView.setVisibility(View.VISIBLE);
                    Toast.makeText(SearchContactActivity.this, "请输入查找关键字", Toast.LENGTH_SHORT).show();
                } else if (!SdkUtils.checkUserName(key)) {
                    noResultTextView.setVisibility(View.VISIBLE);
                    Toast.makeText(SearchContactActivity.this, "请输入合法关键字", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread() {
                        public void run() {
                            uiHandler.sendEmptyMessage(SEARCH_CONTACTS_START);
                            CMContactManager cmContactManager = CMIMHelper.getCmContactManager();
                            if(cmContactManager!=null){
                                try {

                                    cmContactManager.searchContactsFromServer(key, new CMChatListener.OnContactListener() {

                                        @Override
                                        public void onSuccess(List<CMContact> contacts) {
                                            // TODO Auto-generated method stub
                                            if (contacts == null || contacts.size() == 0) {
                                                uiHandler.sendEmptyMessage(SEARCH_CONTACTS_NO_RESULT);
                                                return;
                                            }
                                            Bundle bundle = new Bundle();
                                            ArrayList list = new ArrayList();
                                            list.add(contacts);
                                            bundle.putParcelableArrayList("search_result_contacts", list);
                                            Message message = new Message();
                                            message.setData(bundle);
                                            message.what = SEARCH_CONTACTS_OK;
                                            uiHandler.sendMessage(message);
                                        }

                                        @Override
                                        public void onFailed(String failedMsg) {
                                            // TODO Auto-generated method stub
                                            Bundle bundle = new Bundle();
                                            bundle.putString("error_msg", failedMsg);
                                            Message message = new Message();
                                            message.setData(bundle);
                                            message.what = SEARCH_CONTACTS_FAILED;
                                            uiHandler.sendMessage(message);
                                        }
                                    });
                                }catch (Exception e){
                                    Intent serviceIntent = new Intent(SearchContactActivity.this, BackgroundService.class);
                                    if (IMApp.currentUserName!=null){
                                        serviceIntent.putExtra("userName", IMApp.currentUserName);
                                        serviceIntent.putExtra("passWord", "123456");
                                        startService(serviceIntent);
                                    }
                                }
                            }
                        }

                    }.start();
                }
            }
        });
    }

    private void hideKeyboard() {
        if(getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if(getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public  void  doBack(View view){finish();}

    public class ContactSearchResultAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<CMContact> contactList;
        private LayoutInflater mInflater;

        public ContactSearchResultAdapter(Context context,ArrayList<CMContact> contacts)
        {
            this.context=context;
            this.contactList=contacts;

            this.mInflater=LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return contactList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if(convertView==null)
            {
                convertView=mInflater.inflate(R.layout.item_contact_search_result, null);
            }
            TextView nameTextView=(TextView)convertView.findViewById(R.id.name_textView);
            TextView username_textView=(TextView)convertView.findViewById(R.id.username_textView);
            LinearLayout ll_search_name=(LinearLayout)convertView.findViewById(R.id.ll_search_name);
            final CMContact contact=contactList.get(position);
            nameTextView.setText(contact.getName()+"");
            username_textView.setText(contact.getUserName()+"");
            TextView addTextView=(TextView)convertView.findViewById(R.id.search_result_add_contact_textView);
            ll_search_name.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(contact.getUserName().equals(IMApp.getInstance().getCurrentUserName()))
                    {
                        Toast.makeText(context,"不能添加自己为好友！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                   Intent intent = new Intent(SearchContactActivity.this, MessageActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                    intent.putExtra(IMConst.CHATTYPE, false);//单聊
                    intent.putExtra(IMConst.USERNAME, contact.getUserName());// 对于群组 username 即群名字
                    intent.putExtra(IMConst.AVATARURL, "");
                    intent.putExtra(IMConst.GROUPID, "");
                    intent.putExtra(IMConst.GUID, 0l);
                    SearchContactActivity.this.startActivity(intent);
                    Log.e("contact.getUserName()="+contact.getUserName(),"TAG");
//                    Toast.makeText(context,context.getString(R.string.add_contact_msg), Toast.LENGTH_SHORT).show();
                }
            });
            addTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendBroadcast(new Intent(IMConst.ACTION_ADD_ATTENTION).putExtra("userId",contact.getUserName()));
                }
            });
            return convertView;
        }

    }
}
