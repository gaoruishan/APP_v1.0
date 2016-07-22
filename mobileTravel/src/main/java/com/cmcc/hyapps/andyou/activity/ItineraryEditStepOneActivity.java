/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.fragment.DatePickFragment;
import com.cmcc.hyapps.andyou.fragment.DatePickFragment.DateSetListener;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.TimeUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.dragsortlistview.DragSortListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author kuloud
 */
public class ItineraryEditStepOneActivity extends BaseActivity {
    private static final int REQUEST_CODE_PICK_SPOT = 1;
    private TargetAdapter mAdapter;

    private ArrayList<String> mTargets = new ArrayList<String>();

    private TextView mStartTimeEditText;
    private Calendar mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartTime = Calendar.getInstance(Locale.CHINESE);
        setContentView(R.layout.activity_journey_edit1);
        initViews();
    }

    private void initViews() {
        initActionBar();
        mStartTimeEditText = (TextView) findViewById(R.id.et_start_date);
        mStartTimeEditText.setText(TimeUtils.formatDate(mStartTime.getTime()));
        mStartTimeEditText.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                pickDate();
            }
        });
        findViewById(R.id.tv_add_target).setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                Intent pickSpot = new Intent(ItineraryEditStepOneActivity.this,
                        SpotPickActivity.class);
                startActivityForResult(pickSpot, REQUEST_CODE_PICK_SPOT);
            }
        });
        DragSortListView listView = (DragSortListView) findViewById(R.id.drag_list);
        listView.setDropListener(onDrop);
        listView.setRemoveListener(onRemove);
        mAdapter = new TargetAdapter(mTargets);
        listView.setAdapter(mAdapter);
        findViewById(R.id.tv_next).setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                // Intent intent = new Intent(activity,
                // ItineraryEditStepTwoActivity.class);
                // startActivity(intent);
            }
        });
        ;
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_journey);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    String item = mAdapter.getItem(from);

                    mAdapter.remove(item);
                    mAdapter.insert(item, to);
                }
            };

    private DragSortListView.RemoveListener onRemove =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    mAdapter.remove(mAdapter.getItem(which));
                }
            };

    private class TargetAdapter extends ArrayAdapter<String> {

        public TargetAdapter(List<String> targets) {
            super(ItineraryEditStepOneActivity.this, R.layout.item_journey_target,
                    R.id.tv_target, targets);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                ViewHolder holder = new ViewHolder();

                TextView tv = (TextView) v.findViewById(R.id.tv_target);
                holder.targetView = tv;

                v.setTag(holder);
            }

            if (v != null) {
                ViewHolder holder = (ViewHolder) v.getTag();
                String albums = getItem(position);
                holder.targetView.setText(albums);
            }

            return v;
        }
    }

    private class ViewHolder {
        public TextView targetView;
    }

    private void pickDate() {
        String format = mStartTimeEditText.getText().toString();
        long milliseconds = TimeUtils.parseDate(format).toMillis(true);
        DatePickFragment datePickDialog = DatePickFragment.newInstance(milliseconds);
        datePickDialog.show(getFragmentManager(), DatePickFragment.TAG_DIALOG_PICK_DATE);
        datePickDialog.setDateSetListener(new DateSetListener() {

            @Override
            public void onDateSet(Calendar calendar) {
                mStartTimeEditText.setText(TimeUtils.formatDate(calendar.getTime()));
            }
        });
    }
}
