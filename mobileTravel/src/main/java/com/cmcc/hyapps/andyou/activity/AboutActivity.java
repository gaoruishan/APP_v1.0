/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

/**
 * @author kuloud
 */
public class AboutActivity extends BaseActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_about);
        initActionBar();
        if (Const.DEBUG) {
            ((TextView) findViewById(R.id.version_name)).setText(getString(R.string.about_version_name,
                    AppUtils.getVersionName(activity)+" build" + AppUtils.getBuild()));
            findViewById(R.id.riv_logo).setOnLongClickListener(new OnLongClickListener() {
                
                @Override
                public boolean onLongClick(View v) {
                    boolean debug = !PreferencesUtils.getBoolean(mContext, ServerAPI.KEY_DEBUG);
                    PreferencesUtils.putBoolean(mContext, ServerAPI.KEY_DEBUG, debug);
                    ServerAPI.switchServer(debug);
                    ToastUtils.show(mContext, debug ? "切换为测试服务器" : "切换为正式服务器");
                    return false;
                }
            });
        }

    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_about);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }
}
