package test.grs.com.ims.message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.littlec.sdk.entity.CMMessage;

import java.util.Date;
import java.util.LinkedHashSet;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.R;

/**
 * Created by gaoruishan on 15/9/28.
 */
public class NotificationController {
    private static NotificationController controller;
    public static LinkedHashSet<String> userNameHashSet;
    public static int msgCount = 0;

    private final Context mContext;
    private long firstTime = 0;
    private long secondTime = 0;

    private static LinkedHashSet<String> ackuserNameHashSet = new LinkedHashSet<String>();

    private NotificationManager manager;
    private long secondTime1;
    private long firstTime1;

    private NotificationController() {
        manager = (NotificationManager) IMApp.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mContext = IMApp.mContext;
        init();
    }

    public void init() {
        firstTime = 0;
        secondTime = 0;
        userNameHashSet = new LinkedHashSet<String>();
        msgCount = 0;
        manager.cancel(-1);
    }

    public static NotificationController getInstance() {
        if (controller == null) {
            controller = new NotificationController();
        }
        return controller;
    }

    public synchronized void showNotification(int notificationType, CMMessage message ,String content) {
        if (notificationType == IMConst.NEW_MESSAGE_NOTIFICATION) {
            msgCount++;
            if (message.getFromNick()!=null){
                ackuserNameHashSet.add(message.getFromNick());
            }else {
                ackuserNameHashSet.add(message.getFrom());
            }

            String title = getTitle(notificationType);
            String subtitle = content;
            Notification notification = new Notification();
            secondTime = new Date().getTime();
            if (secondTime - firstTime < 800) {
                //时间间隔内多条消息，不震动，不响铃
            } else {
                notification.vibrate = new long[]{50, 100, 50, 100};
                notification.defaults = Notification.DEFAULT_SOUND;
            }
            firstTime = secondTime;
            notification.icon = R.drawable.ic_launcher;
            notification.tickerText = title;
            notification.when = System.currentTimeMillis();
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults = Notification.DEFAULT_SOUND;
            Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            notificationIntent.setClass(mContext, MessageListActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(mContext, title, subtitle, pendingIntent);
            manager.notify((notificationType == IMConst.NEW_MESSAGE_NOTIFICATION) ? notificationType : IMConst.NEW_FRIEND_APPLY_NOTIFICATION, notification);
        }
    }
    public synchronized void showNotification1(int notificationType, CMMessage message) {
        if (notificationType == IMConst.NEW_MESSAGE_NOTIFICATION) {
            msgCount++;
            if (message.getFromNick()!=null){
                ackuserNameHashSet.add(message.getFromNick());
            }else {
                ackuserNameHashSet.add(message.getFrom());
            }
            String title = getTitle(notificationType);
            String subtitle = getMultiSenderSubtitle();
            Notification notification = new Notification();
            secondTime1 = new Date().getTime();
            if (secondTime1 - firstTime1 < 800) {
                //时间间隔内多条消息，不震动，不响铃
            } else {
                notification.vibrate = new long[]{50, 100, 50, 100};
                notification.defaults = Notification.DEFAULT_SOUND;
            }
            firstTime1 = secondTime1;
            notification.icon = R.drawable.ic_launcher;
            notification.tickerText = title;
            notification.when = System.currentTimeMillis();
            notification.defaults = Notification.DEFAULT_SOUND;
            notification.flags=Notification.FLAG_AUTO_CANCEL;
            Intent notificationIntent=new Intent(IMConst.NOTIFICATION_ACTION_SUCCUSS);
            Bundle bundle = new Bundle();
            bundle.putParcelable("message", message);
            notificationIntent.putExtra("bundle", bundle);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//            if (ackuserNameHashSet.size() == 1)
//                notificationIntent.setClass(mContext, MessageActivity.class);
//            else
                notificationIntent.setClass(mContext, MessageListActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(mContext, title, subtitle, pendingIntent);
            manager.notify((notificationType == IMConst.NEW_MESSAGE_NOTIFICATION) ? notificationType : IMConst.NEW_FRIEND_APPLY_NOTIFICATION, notification);
        }
    }

    private String getMultiSenderSubtitle() {
        String result = "来自:";
        int index = 0;
        for (String name : ackuserNameHashSet) {
            if (index == 0) {
                result += name;
            } else {
                result += "、" + name;
            }
            index++;
        }
        return index > 1 ? result + "等" : result;
    }

    private String getTitle(int notificationType) {
        String result = "";
        switch (notificationType) {
            case IMConst.NEW_MESSAGE_NOTIFICATION:
                result = "收到新消息（共" + msgCount + "条）";
                break;
            default:
                break;
        }
        return result;
    }
}
