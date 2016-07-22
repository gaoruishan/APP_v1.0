/**
 * 
 */

package com.cmcc.hyapps.andyou.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;

import com.cmcc.hyapps.andyou.util.ScreenUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Common date pick dialog
 * 
 * @author Kuloud
 */
public class DatePickFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final String TAG_DIALOG_PICK_DATE = "tag_dialog_pick_date";
    private static final String ARG_MILLIS = "arg_millis";
    private Calendar mCalendar;
    private DateSetListener mDateSetListener;

    public DatePickFragment() {
        mCalendar = Calendar.getInstance(Locale.CHINA);
    }

    public static DatePickFragment newInstance(long milliseconds) {
        DatePickFragment fragment = new DatePickFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_MILLIS, milliseconds);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long millis = getArguments().getLong(ARG_MILLIS);
        if (millis > 0) {
            mCalendar.setTimeInMillis(millis);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);

        Calendar mStartTime = Calendar.getInstance(Locale.CHINA);
        long timeInMillis = mStartTime.getTimeInMillis();
        dialog.getDatePicker().setMaxDate(timeInMillis);

        return dialog;

    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                View v = getDialog().getCurrentFocus();
                if (v != null) {
                    ScreenUtils.dissmissKeyboard(getActivity(), v);
                }
            }
        }, 50);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mCalendar.set(year, month, day);
        if (mDateSetListener != null) {
            mDateSetListener.onDateSet(mCalendar);
        }
    }

    public void setDateSetListener(DateSetListener dateSetListener) {
        this.mDateSetListener = dateSetListener;
    }



    public interface DateSetListener {
        public void onDateSet(Calendar calendar);
    }
}
