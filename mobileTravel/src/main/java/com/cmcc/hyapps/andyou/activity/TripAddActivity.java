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

import com.cmcc.hyapps.andyou.widget.circularprogressbar.CircularProgressBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.StringConverter;
import com.cmcc.hyapps.andyou.fragment.DatePickFragment;
import com.cmcc.hyapps.andyou.fragment.DatePickFragment.DateSetListener;
import com.cmcc.hyapps.andyou.model.QHStrategy;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.TimeUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author kuloud
 */
public class TripAddActivity extends BaseActivity implements  UploadTask.UploadCallBack{
    private final String TAG_DIALOG_PICK_DATE = "tag_dialog_pick_date";

    private EditText mNameEditText;
    private TextView mStartTimeEditText;
    private View mNext;
    private Calendar mStartTime = null;
    private CircularProgressBar progressBar;
    private View title_trip;
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
        progressBar = (CircularProgressBar) findViewById(R.id.loading_progress);
        title_trip = findViewById(R.id.title_add_trip);
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

        List<File> imgFiles = new ArrayList<File>();
        String url = ServerAPI.User.BASE_WRITE_RAIDERS_URL;
        Map<String, String> params = new HashMap<String, String>();
        Map<String, File> fileParams = new HashMap<String, File>();
        params.put("title",name);
        params.put("start_date",createDate);
        upLoading();
        new UploadTask(getApplicationContext(), url,2,fileParams , params,this).execute("", "");

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
        actionBar.setTitle("创建攻略");
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

    @Override
    public void onSuccess(String result) {
        Gson mGson = new Gson();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(String.class, new StringConverter());
        mGson = gb.create();
        QHStrategy qhStrategy =  mGson.fromJson(result.toString(), QHStrategy.class);
        ToastUtils.show(getApplicationContext(),R.string.strategy_titile_create_success);
        progressBar.setVisibility(View.INVISIBLE);
        Intent data = getIntent();

        qhStrategy.created = TimeUtils.formatTime(mStartTime.getTimeInMillis(), TimeUtils.DATE_TIME_FORMAT);
        qhStrategy.user = AppUtils.getQHUser(TripAddActivity.this);
        data.putExtra(Const.EXTRA_QHSTRATEGY_DATA, qhStrategy);
//        data.putExtra("startDate",  mStartTimeEditText.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onFailed() {
       ToastUtils.show(getApplicationContext(), R.string.strategy_titile_create_fail);
        progressBar.setVisibility(View.GONE);
        title_trip.setVisibility(View.VISIBLE);
        mNext.setVisibility(View.VISIBLE);
    }

    public void upLoading() {
        progressBar.setVisibility(View.VISIBLE);
        title_trip.setVisibility(View.INVISIBLE);
        mNext.setVisibility(View.INVISIBLE);
    }
}
