package test.grs.com.ims.message;

import android.content.Context;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import com.littlec.sdk.business.MessageConstants;
import com.littlec.sdk.entity.CMGroup;
import com.littlec.sdk.entity.CMMember;
import com.littlec.sdk.entity.CMMessage;
import com.littlec.sdk.entity.messagebody.AudioMessageBody;
import com.littlec.sdk.entity.messagebody.ImageMessageBody;
import com.littlec.sdk.entity.messagebody.TextMessageBody;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import test.grs.com.ims.R;
import test.grs.com.ims.util.model.QHAttention;

/**
 * Created by gaoruishan on 15/10/3.
 */
public class IMUtil {
//    public static String[] avatar_urls = {
//            "http://img5.duitang.com/uploads/item/201508/12/20150812204032_eiAQk.thumb.224_0.jpeg",
//            "http://img4.duitang.com/uploads/item/201508/10/20150810161432_5ujhU.thumb.224_0.jpeg",
//            "http://b.hiphotos.baidu.com/zhidao/pic/item/4b90f603738da97728164341b651f8198618e39b.jpg",
//            "http://www.qq1234.org/uploads/allimg/140610/3_140610105824_9.jpg",
//            "http://b.hiphotos.baidu.com/zhidao/pic/item/4b90f603738da97728164341b651f8198618e39b.jpg",
//            "http://www.qq1234.org/uploads/allimg/140610/3_140610105824_9.jpg",
//            "http://www.qq1234.org/uploads/allimg/140610/3_140610105824_9.jpg",
//            "http://img4.duitang.com/uploads/item/201508/10/20150810161432_5ujhU.thumb.224_0.jpeg",
//            "http://b.hiphotos.baidu.com/zhidao/pic/item/4b90f603738da97728164341b651f8198618e39b.jpg",
//            "http://www.qq1234.org/uploads/allimg/140610/3_140610105824_9.jpg",
//            "http://www.qq1234.org/uploads/allimg/140610/3_140610105824_9.jpg",
//            "http://img4.duitang.com/uploads/item/201508/10/20150810161432_5ujhU.thumb.224_0.jpeg",
//            "http://img4.duitang.com/uploads/item/201508/10/20150810161432_5ujhU.thumb.224_0.jpeg"
//    };

    /**
     * 将String 转为字节
     *
     * @param args
     */
    public static String StringToBytes(String args) {
        byte[] b = null;
        try {
            b = args.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String fString = new String(b);
        Log.e("==StringToBytes", fString);
        return fString;
    }

    /**
     * 将String 转为字节
     *
     * @param
     */
    public static void BytesToString(String args) {
        int parseInt = Integer.parseInt(args);
        String string = "王";
        byte[] b = null;
        try {
            b = args.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String s = null;
        for (int i = 0; i < b.length; i++) {
            s += Integer.toBinaryString(b[i] & 0xff);
        }
        Log.e("==StringToBytes", s);

        String fString = new String(b);
        System.out.print(fString);

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取时间格式
     *
     * @param context
     * @param milliseconds
     * @return
     */
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


    public static IMContact getIMContactInstance(int id, boolean blackList, boolean groupChat, int num, String userName, String phoneNumber, String nickName, String avatar_url, long time, String message, long guid) {
        IMContact cmContacts = new IMContact();
        cmContacts.setId(id);
        cmContacts.setBlackList(blackList);
        cmContacts.setGroupChat(groupChat);
        cmContacts.setMsgNum(num);
        cmContacts.setNickname(nickName);
        cmContacts.setPhoneNumber(phoneNumber);
        cmContacts.setUserName(userName);
        cmContacts.setAvatarurl(avatar_url);
        cmContacts.setTime(time);
        cmContacts.setMessage(message);
        cmContacts.setGuid(guid);
        cmContacts.setTop(false);
        cmContacts.setIgnore(false);
        return cmContacts;
    }

    public static IMContactList getIMContactListInstance(int id, boolean blackList, boolean groupChat, int num, String userName, String phoneNumber, String nickName, String avatar_url, long time, String message, long guid, String introduce, boolean isfrend) {
        IMContactList cmContacts = new IMContactList();
        cmContacts.setId(id);
        cmContacts.setBlackList(blackList);
        cmContacts.setGroupChat(groupChat);
        cmContacts.setMsgNum(num);
        cmContacts.setNickname(nickName);
        cmContacts.setPhoneNumber(phoneNumber);
        cmContacts.setUserName(userName);
        cmContacts.setAvatarurl(avatar_url);
        cmContacts.setTime(time);
        cmContacts.setMessage(message);
        cmContacts.setGuid(guid);
        cmContacts.setTop(false);
        cmContacts.setIgnore(false);
        cmContacts.setIntroduce(introduce);
        cmContacts.setIsFriend(isfrend);
        return cmContacts;
    }

    public static UNIMContact getUNIMContactInstance(int id, boolean blackList, boolean groupChat, int num, String userName, String phoneNumber, String nickName, String avatar_url, long time, String message, long guid) {
        UNIMContact cmContacts = new UNIMContact();
        cmContacts.setId(id);
        cmContacts.setBlackList(blackList);
        cmContacts.setGroupChat(groupChat);
        cmContacts.setMsgNum(num);
        cmContacts.setNickname(nickName);
        cmContacts.setPhoneNumber(phoneNumber);
        cmContacts.setUserName(userName);
        cmContacts.setAvatarurl(avatar_url);
        cmContacts.setTime(time);
        cmContacts.setMessage(message);
        cmContacts.setGuid(guid);
        cmContacts.setTop(false);
        cmContacts.setIgnore(false);
        return cmContacts;
    }

    public static UNIMContactList getUNIMContactListInstance(int id, boolean blackList, boolean groupChat, int num, String userName, String phoneNumber, String nickName, String avatar_url, long time, String message, long guid, String introduce, boolean isfrend) {
        UNIMContactList cmContacts = new UNIMContactList();
        cmContacts.setId(id);
        cmContacts.setBlackList(blackList);
        cmContacts.setGroupChat(groupChat);
        cmContacts.setMsgNum(num);
        cmContacts.setNickname(nickName);
        cmContacts.setPhoneNumber(phoneNumber);
        cmContacts.setUserName(userName);
        cmContacts.setAvatarurl(avatar_url);
        cmContacts.setTime(time);
        cmContacts.setMessage(message);
        cmContacts.setGuid(guid);
        cmContacts.setTop(false);
        cmContacts.setIgnore(false);
        cmContacts.setIntroduce(introduce);
        cmContacts.setIsFriend(isfrend);
        return cmContacts;
    }

    public static String getContentType(CMMessage message) {
        String content = "";
        switch (message.getContentType()) {
            case MessageConstants.Message.TYPE_AUDIO:
                content = "[语音]";
                break;
            case MessageConstants.Message.TYPE_PIC:
                content = "[图片]";
                break;
            default:
                content = message.getMessageBody().getContent();
                break;
        }
        return content;
    }

    public static String geMemeberString(List<CMMember> members) {
        String str = "";
        if (members != null && members.size() > 0) {
            for (int i = 0; i < members.size(); i++) {
                CMMember m = members.get(i);
                if (i == members.size() - 1) {
                    str += m.getMemberNick();
                } else {
                    str += m.getMemberNick() + ",";
                }
            }
        }
        return str;
    }

    public static String getIMMemeberString(ArrayList<IMMember> members) {
        String str = "";
        if (members != null && members.size() > 0) {
            for (int i = 0; i < members.size(); i++) {
                IMMember m = members.get(i);
                if (i == members.size() - 1) {
                    str += m.getUserName();
                } else {
                    str += m.getUserName() + ",";
                }
            }
        }
        return str;
    }

    public static ArrayList<String> geMemeberList(List<CMMember> members) {
        ArrayList<String> list = new ArrayList<String>();
        String str = "";
        if (members != null && members.size() > 0) {
            for (int i = 0; i < members.size(); i++) {
                CMMember m = members.get(i);
                list.add(m.getMemberId());
            }
        }
        return list;
    }

    public static IMGroup getCMGroupToIMGroup(CMGroup cmGroup) {
        IMGroup imGroup = new IMGroup();
        imGroup.setGroupId(cmGroup.getGroupId());
        imGroup.setGroupName(cmGroup.getGroupName());
        imGroup.setGroupDesc(cmGroup.getGroupDesc());
        imGroup.setIsMute(cmGroup.isMute());
        imGroup.setIsPublic(cmGroup.isPublic());
        if (cmGroup.getMembers() != null) {
            for (CMMember cmMember : cmGroup.getMembers()) {
                imGroup.setMemberId(imGroup.getGroupId() + cmMember.getMemberId() + ",");
                imGroup.setMemberNick(imGroup.getMemberNick() + cmMember.getMemberNick() + ",");
            }
        }
        return imGroup;
    }

    public static List<CMMessage> getIMMessageToCMMessage(List<IMMessage> imMessage, List<CMMessage> cmMessage) {
        if (cmMessage == null) {
            cmMessage = new ArrayList<CMMessage>();
        }
//        Log.e("imMessage.size()=" + imMessage.size(), "TAG");
        for (IMMessage im : imMessage) {
            CMMessage cm = null;
//            Log.e("=getExtra", im.getExtra() + "");
            switch (im.getContentType()) {
                case MessageConstants.Message.TYPE_AUDIO:
                    AudioMessageBody audioMessageBody = new AudioMessageBody(new File(im.getLocalPath()), im.getDuration());
                    if (im.getChatType() == 1) {//群聊
                        cm = new CMMessage(im.getChatType(), im.getFrom(), audioMessageBody, im.getId(), im.getSendOrRecv(), im.getContentType(), im.getStatus(), im.getTime());
                    } else {
                        cm = new CMMessage(im.getChatType(), im.getTo(), audioMessageBody, im.getId(), im.getSendOrRecv(), im.getContentType(), im.getStatus(), im.getTime());
                    }
                    break;
                case MessageConstants.Message.TYPE_PIC:
                    ImageMessageBody imageMessageBody = new ImageMessageBody(new File(im.getLocalPath()));
                    imageMessageBody.setMiddleUri(im.getMiddleUri() + "");
                    imageMessageBody.setSmallUri(im.getSmallUri() + "");
                    if (im.getChatType() == 1) {//群聊
                        cm = new CMMessage(im.getChatType(), im.getFrom(), imageMessageBody, im.getId(), im.getSendOrRecv(), im.getContentType(), im.getStatus(), im.getTime());
                    } else {
                        cm = new CMMessage(im.getChatType(), im.getTo(), imageMessageBody, im.getId(), im.getSendOrRecv(), im.getContentType(), im.getStatus(), im.getTime());
                    }
                    break;
                default:
                    TextMessageBody textMessageBody = new TextMessageBody(im.getContent());
                    if (im.getChatType() == 1) {//群聊
                        cm = new CMMessage(im.getChatType(), im.getFrom(), textMessageBody, im.getId(), im.getSendOrRecv(), im.getContentType(), im.getStatus(), im.getTime());
                    } else {
                        cm = new CMMessage(im.getChatType(), im.getTo(), textMessageBody, im.getId(), im.getSendOrRecv(), im.getContentType(), im.getStatus(), im.getTime());
                    }
                    break;
            }
            if (cm != null)
                cm.setExtra(im.getExtra() + "");//头像
            cmMessage.add(cm);
        }
        return cmMessage;
    }


    public static File saveFileFromServer(String path) throws Exception {
        {

            //如果相等的话表示当前的sdcard挂载在手机上并且是可用的

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                URL url = new URL(path);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(5000);

                //获取到文件的大小

//                pd.setMax(conn.getContentLength());

                InputStream is = conn.getInputStream();

                File file = new File(Environment.getExternalStorageDirectory(), "xmpp/download/" + System.currentTimeMillis() + ".amr");

                FileOutputStream fos = new FileOutputStream(file);

                BufferedInputStream bis = new BufferedInputStream(is);

                byte[] buffer = new byte[1024];

                int len;

                int total = 0;

                while ((len = bis.read(buffer)) != -1) {

//                    if(isDownload){
                    fos.write(buffer, 0, len);

                    total += len;

                    //获取当前下载量

//                        pd.setProgress(total);
//                    }

                }

                fos.close();

                bis.close();

                is.close();

                return file;

            } else {

                return null;

            }

        }
    }


    public static List<IMContactList> addContactDefultDatas(List<QHAttention.ResultsEntity> results) {
        ArrayList<IMContactList> contactsList = new ArrayList<IMContactList>();
        for (int i = 0; i < results.size(); i++) {
            QHAttention.ResultsEntity entity = results.get(i);
            if (entity.getUserId() != null) {
                int userId = Integer.parseInt(entity.getUserId());
                //将用户名＝>userId,phoneNumber=>用户的Introduction
                IMContactList instance = getIMContactListInstance(userId, false, false, 0, entity.getUserId(), "", entity.getNickname(), entity.getAvatarUrl(), System.currentTimeMillis(), "", 0l, entity.getIntroduction() + "", entity.getIsfriend() == 0 ? false : true);
                contactsList.add(instance);
            }
        }
        return contactsList;
    }

    public static List<UNIMContactList> addUNContactDefultDatas(List<QHAttention.ResultsEntity> results) {
        ArrayList<UNIMContactList> contactsList = new ArrayList<UNIMContactList>();
        for (int i = 0; i < results.size(); i++) {
            QHAttention.ResultsEntity entity = results.get(i);
            if (entity.getUserId() != null) {
                int userId = Integer.parseInt(entity.getUserId());
                //将用户名＝>userId,phoneNumber=>用户的Introduction
                UNIMContactList instance = getUNIMContactListInstance(userId, false, false, 0, entity.getUserId(), "", entity.getNickname(), entity.getAvatarUrl(), System.currentTimeMillis(), "", 0l, entity.getIntroduction(), entity.getIsfriend() == 0 ? false : true);

                contactsList.add(instance);
            }
        }
        return contactsList;
    }

    //gao
//    public static void sendOneMessage() {
//        MessageActivity messageActivity = new MessageActivity();
//        CMMessage message = new CMMessage(0, "gao", new TextMessageBody("测试信息"));//text为文本内容，String类型，不能为空
//        message.setContentType(MessageConstants.Message.TYPE_TEXT);
//
//        messageActivity.sendMessage(message);
//    }
}
