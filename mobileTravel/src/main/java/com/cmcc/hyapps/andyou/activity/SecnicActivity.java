/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.fragment.QHScenicDetailsFragment;
import com.cmcc.hyapps.andyou.model.QHScenic;

/**
 * @author kuloud
 */
public class SecnicActivity extends BaseActivity {
    private Context mContext;
    private QHScenicDetailsFragment secnicFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_secnic);
        secnicFragment = new QHScenicDetailsFragment();
        if(null!=findViewById(R.id.fragment_container)){
            Intent intent =  getIntent();
            QHScenic mScenic = intent.getParcelableExtra(Const.QH_SECNIC);
            int mId = intent.getIntExtra(Const.QH_SECNIC_ID, 0);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Const.QH_SECNIC,mScenic);
            bundle.putInt(Const.QH_SECNIC_ID,mId);
//            b.putInt(Const.EXTRA_ID, (int) intent.getIntExtra(Const.EXTRA_ID, 1));
//            b.putString(Const.EXTRA_NAME, (String)intent.getStringExtra(Const.EXTRA_NAME));
//            b.putParcelable(Const.EXTRA_COORDINATES, (Location)intent.getParcelableExtra(Const.EXTRA_COORDINATES));
            secnicFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.fragment_container,secnicFragment).commit();
        }
    }
}
