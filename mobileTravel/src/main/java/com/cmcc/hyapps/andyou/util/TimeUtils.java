
package com.cmcc.hyapps.andyou.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.Time;

import com.cmcc.hyapps.andyou.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static final long ONE_DAY_TO_MILLS = 1000 * 60 * 60 * 24;
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
    public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;

    public static CharSequence formatDuration(Context ctx, long millis) {
        if (millis >= HOUR_IN_MILLIS) {
            final int hours = (int) ((millis + 1800000) / HOUR_IN_MILLIS);
            return ctx.getResources().getQuantityString(
                    R.plurals.duration_hours, hours, hours);
        } else if (millis >= MINUTE_IN_MILLIS) {
            final int minutes = (int) ((millis + 30000) / MINUTE_IN_MILLIS);
            return ctx.getResources().getQuantityString(
                    R.plurals.duration_minutes, minutes, minutes);
        } else {
            final int seconds = (int) ((millis + 500) / SECOND_IN_MILLIS);
            return ctx.getResources().getQuantityString(
                    R.plurals.duration_seconds, seconds, seconds);
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static Time parseTime(String s, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Time t = new Time();
        try {
            Date date = sdf.parse(s);
            t.set(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return t;
    }

    public static boolean testFormat(String s) {
        return testFormat(s, DATE_TIME_FORMAT);
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean testFormat(String s, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            sdf.parse(s);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Time parseTime(String s) {
        return parseTime(s, DATE_TIME_FORMAT);
    }

    public static long parseTimeToMills(String s) {
        return parseTime(s, DATE_TIME_FORMAT).toMillis(true);
    }

    public static Time parseDate(String s) {
        return parseTime(s, DATE_FORMAT);
    }

    public static long parseDateToMills(String s) {
        return parseTime(s, DATE_FORMAT).toMillis(true);
    }

    public static String formatDate(Date date) {
        return formatTime(date.getTime(), DATE_FORMAT);
    }

    public static String formatDateByMills(long millis) {
        return formatTime(millis, DATE_FORMAT);
    }

    public static String formatTime(Time time, String format) {
        return DateFormat.format(format,
                time.toMillis(false)).toString();
    }

    public static String formatTime(String time, String format) {
        if(null==time)return "";
        return formatTime(parseTime(time), format);
    }

    public static String formatTime(long millis, String format) {
        return DateFormat.format(format, millis).toString();
    }

    public static int dayDuration(String day1, String day2) {
        return (int) Math.abs((parseTimeToMills(day1) - parseTimeToMills(day2)) / ONE_DAY_TO_MILLS) + 1;
    }

    public static int dayDuration(Time day1, Time day2) {
        return (int) Math.abs((day1.toMillis(true) - day2.toMillis(true)) / ONE_DAY_TO_MILLS) + 1;
    }

    /**
     * Get date by offset(/day) Example: date = "2014-10-19" offset = 1 result
     * "2014-10-20" offset = -20 result "2014-09-30"
     * 
     * @param date
     * @param offset
     * @return
     */
    public static String getDateByOffset(String date, int offset) {
        return TimeUtils.formatDateByMills(TimeUtils.parseDateToMills(date) + offset
                * ONE_DAY_TO_MILLS);
    }
}
