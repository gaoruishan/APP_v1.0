package test.grs.com.ims.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.IMBaseActivity;
import test.grs.com.ims.R;

public class MessageCenterActivity extends IMBaseActivity {

    private ListView listView;
    private List<MessageCenter> list;
    private MessageCenterAdapter adapter;
    private int notify  =1;
    private DbUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        listView = (ListView) this.findViewById(R.id.lv_msg_center);
        initDatas();
        adapter = new MessageCenterAdapter(list,this);
        listView.setAdapter(adapter);
    }

    private void initDatas() {
        list = new ArrayList<MessageCenter>();
        list.add(new MessageCenter("系统通知","2.0版本升级用户，快来抢先注册吧",System.currentTimeMillis(),1,0));
        list.add(new MessageCenter("活动消息", "9折入住武汉大酒店，欣赏樱花", System.currentTimeMillis(), 2, 0));
//        list.add(new MessageCenter("回复提醒", "独行客在“青海的独特回忆记“，回复了你", System.currentTimeMillis(), 3, notify));
        dbUtils = IMApp.geDbUtils();
        try {
            MessageCenter center = dbUtils.findFirst(MessageCenter.class);
            if (center==null){//默认
                 //获取数据
                center = new MessageCenter("回复提醒", "独行客在“青海的独特回忆记“，回复了你", System.currentTimeMillis(), 3, 1);
                dbUtils.save(center);
            }
            list.add(center);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    class MessageCenterAdapter extends AppBaseAdapter<MessageCenter>{

        public MessageCenterAdapter(List<MessageCenter> list, Context context) {
            super(list, context);
        }

        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null){
                holder = new Holder();
                convertView = inflater.inflate(R.layout.item_message_center,null);
                holder.rl_msg = (RelativeLayout) convertView.findViewById(R.id.rl_msg);
                holder.title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.content = (TextView) convertView.findViewById(R.id.tv_content);
                holder.time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.notify = (ImageView) convertView.findViewById(R.id.iv_tag);
                convertView.setTag(holder);
            }else {
                holder = (Holder) convertView.getTag();
            }
            final MessageCenter mc = list.get(position);
            holder.title.setText(mc.getTitle());
            holder.content.setText(mc.getContent());
            holder.time.setText(IMUtil.getFormattedTime(context,mc.getTime()));
            if (mc.getTag()==0){
                holder.notify.setVisibility(View.GONE);
            }else {
                holder.notify.setVisibility(View.VISIBLE);
            }
            if (mc.getType()==3){//回复提醒
                holder.rl_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        sendBroadcast(new Intent(IMConst.ACTION_START_GUDERMESSAGE));
                        holder.notify.setVisibility(View.GONE);
                        mc.setTag(0);
                        try {
                            dbUtils.saveOrUpdate(mc);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            return convertView;
        }
       class Holder {
           private RelativeLayout rl_msg;
           private TextView title;
           private TextView content;
           private TextView time;
           private ImageView notify;
       }
    }
    public void doBack(View view){
        finish();
    }
}
