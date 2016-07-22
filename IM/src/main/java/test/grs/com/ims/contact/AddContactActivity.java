package test.grs.com.ims.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;
import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.IMContactList;
import test.grs.com.ims.message.MessageHandle;
import test.grs.com.ims.message.SearchContactActivity;
import test.grs.com.ims.view.CircularProgressBar;
import test.grs.com.ims.view.RefreshLoadListView;
import test.grs.com.ims.view.RoundImageView;

public class AddContactActivity extends IMBaseActivity {

    private LinearLayout ll_contact_friends;
    private RefreshLoadListView lv_recommand_user;
    private CircularProgressBar loading_progress;
    private ArrayList<IMContactList> list;
    private static AddContactActivity mInstance;
    private AddContactAdapter adapter;
    private int cout;

    public static AddContactActivity getInstance() {
        if (mInstance == null) {
            mInstance = new AddContactActivity();
        }
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        initView();
        list = new ArrayList<IMContactList>();
        adapter = new AddContactAdapter(list, this);
        lv_recommand_user.setAdapter(adapter);
        lv_recommand_user.setHeaderEnable(false);
        registerListener();
        //发送广播加载推荐数据
        sendBroadcast(new Intent(IMConst.ACTION_RECOMMEND));
    }

    private void registerListener() {
        //监听通知信息
        MessageHandle.getInstance().setOnRecivedMessageListeners(new MessageHandle.OnRecivedMessageListener() {
            @Override
            public void onSelected(int type) {
            }

            @Override
            public void onBackSuccess(List<IMContactList> lists) {
                Log.e("==加载推荐数据onBackSuccess", lists.size() + "TAG" + lists.toString());
//                        Collections.reverse(lists);//翻转顺序
                lv_recommand_user.loadComplete();
                adapter.setDatasChange(lists);
//              lv_recommand_user.setSelection(adapter.getCount()-10);
                lv_recommand_user.setVisibility(View.VISIBLE);
                loading_progress.setVisibility(View.GONE);
                if (lists.size()>190){
                    lv_recommand_user.setHeaderEnable(false);
                    lv_recommand_user.setFooterEnable(false);
//                    Toast.makeText(AddContactActivity.this, "亲，没有更多啦！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBackContactSuccess(List<SortModel> list) {

            }
        });

        lv_recommand_user.setListViewListener(new RefreshLoadListView.OnListViewListener() {
            @Override
            public void onLoad() {
                cout++;
                Log.e("=onLoad:", "" + cout);
                if (cout <= 5) {
                    sendBroadcast(new Intent(IMConst.ACTION_RECOMMEND_AGAIN));
                } else {
                    lv_recommand_user.loadComplete();
                    lv_recommand_user.setFooterEnable(false);
                    lv_recommand_user.setHeaderEnable(false);
                    Toast.makeText(AddContactActivity.this, "亲，没有更多啦！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onReflash() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mInstance == null) {
            mInstance = this;
        }
    }


    private void initView() {
        ll_contact_friends = (LinearLayout) findViewById(R.id.ll_contact_friends);
        lv_recommand_user = (RefreshLoadListView) findViewById(R.id.lv_recommand_user);
        loading_progress = (CircularProgressBar) findViewById(R.id.loading_progress1);
    }

    //点击查询
    public void doSearch(View view) {
        startActivity(new Intent(AddContactActivity.this, SearchContactActivity.class));
    }

    /**
     * 点击通讯录
     */
    public void doShowContactsList(View view) {
        // 获取手机通讯录
        startActivity(new Intent(AddContactActivity.this, ContractListActivity.class));
    }

    public void doBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        MessageHandle.getInstance().list = null;
        MessageHandle.getInstance().recommendedUsers = "";
        super.onDestroy();
    }

    class AddContactAdapter extends AppBaseAdapters<IMContactList> {

        private final BitmapUtils bitmapUtils;

        public AddContactAdapter(List<IMContactList> list, Context context) {
            super(list, context);
            bitmapUtils = new BitmapUtils(context, IMConst.GLOBALSTORAGE_DOWNLOAD_PATH);
            bitmapUtils.configDiskCacheEnabled(true);//防止溢出
            if (Build.VERSION.SDK_INT >= 21) {
                bitmapUtils.configMemoryCacheEnabled(false);
            }
        }

        @Override
        public View createView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.add_constacts_item, null);
                viewHolder.icon = (RoundImageView) convertView.findViewById(R.id.icon);
                viewHolder.message_send = (ImageView) convertView.findViewById(R.id.message_send);
                viewHolder.sex = (ImageView) convertView.findViewById(R.id.sex);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
                viewHolder.nick_phone = (TextView) convertView.findViewById(R.id.nick_phone);
                viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
                viewHolder.ll_constact = (LinearLayout) convertView.findViewById(R.id.ll_constact);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (list.size() <= 0) {
                Log.e("TAG", "数据    为空 ");
                return convertView;
            }
            final String username = this.list.get(position).getUserName();
            String name = this.list.get(position).getNickname();
            if (name != null && !name.equals("")) {
                viewHolder.tvTitle.setText(name + "");
            } else {
                viewHolder.tvTitle.setText(username + "");
            }

            String avatar_url = list.get(position).getAvatarurl();
            if (avatar_url != null && !avatar_url.equals("")) {
                bitmapUtils.display(viewHolder.icon, avatar_url);
                bitmapUtils.configDefaultLoadFailedImage(R.drawable.recommand_bgs);
            } else {
                viewHolder.icon.setImageResource(R.drawable.recommand_bgs);
            }
            //其中消息数量表示 性别 0男 1女
            if (list.get(position).getMsgNum() == 1) {
                viewHolder.sex.setImageResource(R.drawable.nv);
            } else {
                viewHolder.sex.setImageResource(R.drawable.nan);
            }
            //点击添加的不再显示
//            if (viewHolder.message_send.getTag() != null && viewHolder.message_send.getTag().equals("1")) {
//                viewHolder.message_send.setVisibility(View.GONE);
//            }
//            viewHolder.message_send.setTag("0");
            viewHolder.message_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendBroadcast(new Intent(IMConst.ACTION_ADD_ATTENTION).putExtra("userId", username));
//                    viewHolder.message_send.setVisibility(View.GONE);
//                    viewHolder.message_send.setTag("1");
                }
            });
            viewHolder.ll_constact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转用户详情
                    context.sendBroadcast(new Intent(IMConst.ACTION_STARTACTIVITY).putExtra(IMConst.USER_ID, username));
//                    list.remove(position);
                }
            });
            return convertView;
        }

        class ViewHolder {
            RoundImageView icon;
            ImageView message_send;
            ImageView sex;
            TextView tvLetter;
            TextView tvTitle;
            TextView nick_phone;
            LinearLayout ll_constact;
        }
    }

    public abstract class AppBaseAdapters<T> extends BaseAdapter {
        public List<T> list;
        public Context context;
        public LayoutInflater inflater;

        public AppBaseAdapters(List<T> list, Context context) {
            super();
            this.list = list;
            this.context = context;
//            inflater = LayoutInflater.from(context);
            notifyDataSetChanged();
        }

        public void setDatasChange(List<T> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list != null && !list.isEmpty() ? list.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convetView, ViewGroup parent) {
            return createView(position, convetView, parent);
        }

        public abstract View createView(int position, View convertView,
                                        ViewGroup parent);

    }
}
