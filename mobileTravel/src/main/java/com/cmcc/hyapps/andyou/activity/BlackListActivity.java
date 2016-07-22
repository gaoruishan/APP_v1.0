package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.AppBaseAdapter;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.widget.RoundImageViews;
import com.cmcc.hyapps.andyou.widget.circularprogressbar.CircularProgressBar;
import com.lidroid.xutils.BitmapUtils;
import com.littlec.sdk.entity.CMGroup;

import java.util.ArrayList;
import java.util.List;

import test.grs.com.ims.message.DialogFactory;
import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.util.model.QHBlackList;

public class BlackListActivity extends BaseActivity {

    private ListView lv_blacklist;
    private List<QHBlackList.ResultsEntity> list;
    private BlackListAdapter adapter;
    private CircularProgressBar loading_progress;
    private LinearLayout dialog_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        initView();
        list = new ArrayList<QHBlackList.ResultsEntity>();
        adapter = new BlackListAdapter(list, this);
        lv_blacklist.setAdapter(adapter);
        registerLisenter();
        sendBroadcast(new Intent(IMConst.ACTION_BLACKLIST));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        this.unbindService(serviceConnection);
    }

//    private MobileTravelToIMService.BindBackDatas countService;
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            countService = (MobileTravelToIMService.BindBackDatas) service;//对于本地服务，获取的实例和服务onBind()返回的实例是同一个
//            QHBlackList blackList = countService.onBlackListSuceess();
//            Log.e("CountService", "blackList is " + blackList.toString());
//            if (blackList.getResults() != null) {
//                if (blackList.getResults().size() <= 0) {
//                    return;
//                }
//                adapter.setDatasChange(blackList.getResults());
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            countService = null;
//        }
//    };

    private void registerLisenter() {
//        this.bindService(new Intent(BlackListActivity.this, MobileTravelToIMService.class), this.serviceConnection, BIND_AUTO_CREATE);

        AppUtils.getInstance().setOnRecivedMessageListener(new AppUtils.OnSetBackSuceessListener() {
            @Override
            public void onBlackListSuceess(QHBlackList lists) {
                com.cmcc.hyapps.andyou.util.Log.e("====onBlackListSuceess: " + lists.toString());
                if (lists.getResults() != null) {
                    loading_progress.setVisibility(View.GONE);
                    if (lists.getResults().size() <= 0) {
                        dialog_empty.setVisibility(View.VISIBLE);
                        return;
                    }
                    list = lists.getResults();
                    adapter.setDatasChange(lists.getResults());
                }
            }

            @Override
            public void onGroupListSuceess(List<CMGroup> groups) {

            }
        });
        lv_blacklist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
//                Toast.makeText(BlackListActivity.this, "item=" + i, Toast.LENGTH_LONG).show();
                View.OnClickListener oKBtnEvent = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //刷新数据
                        String userId = list.get(i).getUserId();
                        list.remove(i);
                        adapter.setDatasChange(list);
                        BlackListActivity.this.sendBroadcast(new Intent(IMConst.ACTION_REMOVE_BLACKLIST).putExtra("userId",userId));
                    }
                };
                DialogFactory.getConfirmDialog(BlackListActivity.this, "删除黑名单", "你确定要删除黑名单吗？", " 取消", "确定", null, oKBtnEvent).show();

                return true;
            }
        });

    }

    private void initView() {
        lv_blacklist = (ListView) findViewById(R.id.lv_blacklist);
        loading_progress = (CircularProgressBar) findViewById(R.id.loading_progress);
        dialog_empty = (LinearLayout) findViewById(R.id.dialog_empty);
    }

    class BlackListAdapter extends AppBaseAdapter<QHBlackList.ResultsEntity> {

        private BitmapUtils bitmapUtils;

        public BlackListAdapter(List<QHBlackList.ResultsEntity> list, Context context) {
            super(list, context);
            bitmapUtils = new BitmapUtils(context);
        }

        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (list.size() <= 0) {
                return convertView;
            }
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_blacklist_result, null);
                holder = new ViewHolder();
                holder.iv_black_icon = (RoundImageViews) convertView.findViewById(R.id.iv_black_icon);
                holder.tv_blackname = (TextView) convertView.findViewById(R.id.tv_blackname);
                holder.tv_black_introduce = (TextView) convertView.findViewById(R.id.tv_black_introduce);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final QHBlackList.ResultsEntity entity = list.get(position);
            if (entity.getIntroduction() != null && !entity.getIntroduction().equals("")) {
                holder.tv_black_introduce.setVisibility(View.VISIBLE);
                holder.tv_black_introduce.setText(entity.getIntroduction() + "");
            }
            if (entity.getNickname() != null) {
                holder.tv_blackname.setText(entity.getNickname() + "");
            }
            if (entity.getAvatarUrl() != null && !entity.getAvatarUrl().equals("")) {
                bitmapUtils.display(holder.iv_black_icon, entity.getAvatarUrl());
                bitmapUtils.configDefaultLoadFailedImage(R.drawable.recommand_bgs);
            } else {
                holder.iv_black_icon.setImageResource(R.drawable.recommand_bgs);
            }
            return convertView;
        }

        class ViewHolder {
            private RoundImageViews iv_black_icon;
            private TextView tv_blackname;
            private TextView tv_black_introduce;

        }
    }

    public void doBack(View view) {
        finish();
    }
}
