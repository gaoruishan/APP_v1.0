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
import com.lidroid.xutils.view.annotation.ViewInject;

import butterknife.ButterKnife;

/**
 * @author kuloud
 */
public class QHAboutActivity extends BaseActivity {
    private Context mContext;
    TextView version_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_about);
        initActionBar();
        initView();
    }

    private void initView() {
        String versionName = AppUtils.getVersionName(this);
        String buildVersion = AppUtils.getBuildVersion();
        version_number = (TextView) findViewById(R.id.version_number);
        version_number.setText("和畅游 "+versionName+"  build"+buildVersion);
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
