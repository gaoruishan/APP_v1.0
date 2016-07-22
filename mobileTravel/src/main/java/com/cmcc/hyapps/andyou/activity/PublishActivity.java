/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.os.Bundle;
import android.view.View;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.fragment.TripListFragment;
import com.cmcc.hyapps.andyou.model.Trip.TripList;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.widget.ActionBar;

/**
 * @author kuloud
 */
public class PublishActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initActionBar();

        Bundle args = new Bundle();
        TripListFragment myTripsFragment = new TripListFragment();
        args.putString(Const.ARGS_KEY_MODEL_CLASS, TripList.class.getName());
        args.putString(Const.ARGS_LOADER_URL, ServerAPI.User.URL_USER_TRIPS);
        args.putBoolean("editable", true);
        myTripsFragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .add(R.id.container, myTripsFragment).commit();
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_trips);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

}
