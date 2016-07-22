/**
 * 
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.fragment.DatePickFragment;
import com.cmcc.hyapps.andyou.fragment.DatePickFragment.DateSetListener;
import com.cmcc.hyapps.andyou.model.IdResponse;
import com.cmcc.hyapps.andyou.model.Trip;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.TimeUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author kuloud
 */
public class GuideAddActivity extends BaseActivity {
    private final String TAG_DIALOG_PICK_DATE = "tag_dialog_pick_date";

    private EditText mNameEditText;
    private TextView mStartTimeEditText;
    private View mNext;
    private Calendar mStartTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartTime = Calendar.getInstance(Locale.CHINA);
        String date = getIntent().getStringExtra(Const.EXTRA_DATE);
        if (TimeUtils.testFormat(date)) {
            mStartTime.setTimeInMillis(TimeUtils.parseTimeToMills(date));
        }
        setContentView(R.layout.activity_trip_edit1);
        initViews();
    }

    private void initViews() {
        initActionBar();
        final View datePickView = findViewById(R.id.date_pick);
        datePickView.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                pickDate();
            }
        });
        mStartTimeEditText = (TextView) datePickView.findViewById(R.id.et_start_date);
        mStartTimeEditText.setText(TimeUtils.formatDate(mStartTime.getTime()));
        mNameEditText = (EditText) findViewById(R.id.et_name);
        mNameEditText.addTextChangedListener(mTextWatcher);
        mNext = findViewById(R.id.tv_next);
        mNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                //
                /**
                 * TODO request add new trip 1. OK finish with resultOk and
                 * id/name, then jump to trip day edit page 2. Error Toast error
                 */
                postCreateTrip();
            }
        });
        mNext.setEnabled(false);
    }

    private void postCreateTrip() {
        final String name = mNameEditText.getText().toString().trim();
        if (name.length() < 3) {
            ToastUtils.show(getBaseContext(), R.string.error_title_less_length);
            return;
        }
        final String createDate = mStartTimeEditText.getText().toString();
        RequestManager.getInstance().sendGsonRequest(ServerAPI.Trips.TRIPS_ADD_URL,
                IdResponse.class,
                new Response.Listener<IdResponse>() {

                    @Override
                    public void onResponse(IdResponse response) {
                        Log.e("onResponse, response: " + response);
                        Intent data = getIntent();
                        Trip trip = new Trip();
                        trip.id = response.id;
                        trip.title = name;
                        // TODO should be got from server
                        trip.createTime = TimeUtils.formatTime(mStartTime.getTimeInMillis(),
                                TimeUtils.DATE_TIME_FORMAT);
                        trip.author = AppUtils.getUser(GuideAddActivity.this);
                        data.putExtra(Const.EXTRA_TRIP_DATA, trip);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse");
                        AppUtils.handleResponseError(activity, error);
                    }
                }, false, ServerAPI.Trips.buildAddTripParams(activity, name, createDate),
                requestTag);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(mNameEditText.getText())) {
                mNext.setEnabled(true);
            } else {
                mNext.setEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_trip_write);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    private void pickDate() {
        DatePickFragment datePickDialog = DatePickFragment.newInstance(mStartTime.getTimeInMillis());
        datePickDialog.show(getFragmentManager(), TAG_DIALOG_PICK_DATE);
        datePickDialog.setDateSetListener(new DateSetListener() {

            @Override
            public void onDateSet(Calendar calendar) {
                mStartTime = calendar;
                mStartTimeEditText.setText(TimeUtils.formatDate(mStartTime.getTime()));
            }
        });
    }
}
