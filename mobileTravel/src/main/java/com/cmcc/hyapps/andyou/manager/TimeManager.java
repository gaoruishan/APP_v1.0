package com.cmcc.hyapps.andyou.manager;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import com.cmcc.hyapps.andyou.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bingbing on 2015/11/24.
 */
public class TimeManager {

    public static String getFormattedTime(Context context, long milliseconds) {
        Time msgTime = new Time();
        msgTime.set(milliseconds);

        Time nowTime = new Time();
        nowTime.setToNow();

        SimpleDateFormat sdf = null;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);
        int amOrPm = c.get(Calendar.AM_PM);

        Locale locale = Locale.getDefault();

        if (nowTime.year == msgTime.year && nowTime.month == msgTime.month && nowTime.monthDay == msgTime.monthDay) {
            if (amOrPm == Calendar.PM) {
                sdf = new SimpleDateFormat(context.getString(R.string.msg_time_pm) + "hh:mm", locale);
            } else {
                sdf = new SimpleDateFormat(context.getString(R.string.msg_time_am) + "hh:mm", locale);
            }
        } else if (nowTime.year == msgTime.year && nowTime.month == msgTime.month && (nowTime.monthDay - 1) == msgTime.monthDay) {
            if (amOrPm == Calendar.PM) {
                sdf = new SimpleDateFormat(context.getString(R.string.msg_time_yestoday) + "HH:mm", locale);
            } else {
                sdf = new SimpleDateFormat(context.getString(R.string.msg_time_yestoday) + "HH:mm", locale);
            }
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
        }
        return sdf.format(new Date(milliseconds));
    }

    public static String getTime(String old) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = "";
        try {
            Date d1 = df.parse(old);
            Date   d2 = new Date(System.currentTimeMillis());//你也可以获取当前时间
            long diff = d2.getTime() - d1.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
//            Log.e("TIME=======>","" + days + "天" + hours + "小时" + minutes + "分");
            if (days > 3){
                time = old;
            }
            if (1 <= days && days <=3){
                time = days + "天前";
            }
            if (days < 1 && hours != 0){
                time = hours + "小时前";
            }
            if (days < 1 && hours < 1 && minutes != 0){
                time = minutes + "分钟前";
            }
            if (days < 1 && hours < 1 && minutes == 0){
                time = "刚刚";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  time;
    }
}
