package com.cmcc.hyapps.andyou.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.util.LogUtils;
import com.cmcc.hyapps.andyou.activity.GuiderRecommandActivity;
import com.cmcc.hyapps.andyou.model.Location;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/4/9.
 */
public class ConstTools {
    public  static int SECIAL_HEAD_TITLE_COLOR = 0xffffffff;
    public  static int SECIAL_HEAD_BG_COLOR = 0xff10d7f1;
    public  static int LINECOLOR = 0xffdbdbdb;
    public  static  Location myCurrentLoacation = null;
    public  static  int MAP_POSITION_INTERVAL = 60*1000;
    public  static  int AUTO_GUIDE_INTERVAL = 10*1000;//边走边听 定位请求时间间隔。 LocationService
    public  static  int MAP_SECNIC_RADIUS = 1*3000;
    public static final String FILE_PATH = Environment.getExternalStorageDirectory().toString()+ "/qinghai/IMG/";
    public static boolean checkTelPhone(String phone) {
//        String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
        String regExp = "1((3\\d)|(4[57])|(5[01256789])|(8\\d))\\d{8}";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phone);
        return m.find();//boolean
    }
    public static String getDistance(double star_lat,double star_lon,double end_lat,double end_lon){
        LatLng start = new LatLng(star_lat, star_lon);
        LatLng end = new LatLng(end_lat, end_lon);
        float my_distance = AMapUtils.calculateLineDistance(start, end);
        String str_distance;
        if (my_distance > 1000)
            str_distance = (int) (my_distance / 1000) + "Km";
        else str_distance = my_distance + "m";
        return str_distance;
    }
    public static String getDistance( LatLng start, LatLng end){
        float my_distance = AMapUtils.calculateLineDistance(start, end);
        String str_distance;
        if (my_distance > 1000)
        {
//            if((my_distance / 1000)>10000)str_distance = "未知";//有坐标经纬度值是0,0
//            else
                str_distance = (int) (my_distance / 1000) + "Km";
        }
        else str_distance = my_distance + "m";
        return str_distance;
    }
    public static double getDistanceFromMe( double star_lat,double star_lon){
        LatLng start = new LatLng(star_lat, star_lon);
        LatLng end = new LatLng(myCurrentLoacation.latitude, myCurrentLoacation.longitude);
        float my_distance = AMapUtils.calculateLineDistance(start, end);
        return my_distance;
    }
    public static boolean isNumeric(String str){

        if(null==str) return false;
        str = str.trim();
        Pattern pattern = Pattern.compile("-?[0-9]+.*[0-9]*");//-?[0-9]+.*[0-9]*//[0-9]*
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}d{1}-?d{8}$)|"
                + "(^0[3-9] {1}d{2}-?d{7,8}$)|"
                + "(^0[1,2]{1}d{1}-?d{8}-(d{1,4})$)|"
                + "(^0[3-9]{1}d{2}-? d{7,8}-(d{1,4})$))";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
    public static boolean checkStrEmpty(String str){
        if(null==str) return true;
        else if(str.length()<1)return true;
        return false;
    }

    public static Bitmap Create2DCode(Context c,String str) throws WriterException {
        int qr_width = ScreenUtils.getScreenWidth(c)*2/3;
        int qr_height = qr_width;
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, qr_width, qr_height);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
    public static Bitmap cretaeBitmap(Context c,String str,Bitmap mBitmap) throws WriterException {
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        int qr_width = ScreenUtils.getScreenWidth(c)*2/3;
        int qr_height = qr_width;
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, qr_width, qr_height);
//        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300);//如果要指定二维码的边框以及容错率，最好给encode方法增加一个参数：hints 一个Hashmap
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int halfW = width / 2;
        int halfH = height / 2;
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x > halfW - GuiderRecommandActivity.IMAGE_HALFWIDTH && x < halfW + GuiderRecommandActivity.IMAGE_HALFWIDTH && y > halfH - GuiderRecommandActivity.IMAGE_HALFWIDTH
                        && y < halfH + GuiderRecommandActivity.IMAGE_HALFWIDTH) {
                    pixels[y * width + x] = mBitmap.getPixel(x - halfW + GuiderRecommandActivity.IMAGE_HALFWIDTH, y
                            - halfH + GuiderRecommandActivity.IMAGE_HALFWIDTH);
                } else {
                    //此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                    pixels[y * width + x] = matrix.get(x, y)?0xff000000:0xfffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
}
    public static Spanned getColorRoutes(ArrayList<String> list,int index,String color){
        if(index>=list.size()) return Html.fromHtml(list.toString());
        String result = "";
        for(int i =0;i<list.size();i++){
            String temp = list.get(i);
            if (i==index){
                result+=("<font color=\"#"+color+"\">"+temp+"</font>"+"---");
            }else{
                result+=(temp+"---");
            }
        }
        return Html.fromHtml(result);
    }
    public static Spanned getFormatColorStr(String allStr,String color,String oldword,String newword) {
        String newStr ="<font color=\"#"+color+"\">"+newword+"</font>";
        allStr = allStr.replace(oldword,newStr);
        return Html.fromHtml(allStr);
    }

    /** textview 显示drawbale
     *position: 0 left,1 top,2 right,3 bottom
     **/
    public static void setTextViewIcon(Context con,TextView view,int resId,int position) {
        Drawable female= con.getResources().getDrawable(resId);
        female.setBounds(0, 0, female.getMinimumWidth(), female.getMinimumHeight());
        switch(position){
            case 0:
                view.setCompoundDrawables(female,null,null,null);
                break;
            case 1:
                view.setCompoundDrawables(null,female,null,null);
                break;
            case 2:
                view.setCompoundDrawables(null,null,female,null);
                break;
            case 3:
                view.setCompoundDrawables(null,null,null,female);
                break;
            case 4:
                view.setCompoundDrawables(null,null,null,null);
                break;
        }
    }
    /**
     * 将請求参数集转化为浏览器对应的参数字符串{@code username=zhangsan&password=666666&age=25}
     * @param params 請求参数集
     * @return 浏览器对应的参数字符串
     */
    public static String map2string(final Map<String, String> params) {
        final StringBuilder buf = new StringBuilder();

        for (Iterator<Map.Entry<String, String>> it = params.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> me = it.next();
            buf.append(me.getKey()).append('=').append(me.getValue()).append('&');
        }

        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
//		LogUtil.i(HttpAsyncTask.TAG, buf.toString());
        return buf.toString();
    }
    public static File saveBitmap(Bitmap bmp, String id, Context context) {
        File file = getFile(id, context);
        if (!file.exists() && !file.isDirectory()) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int quality = 100;
                bmp.compress(Bitmap.CompressFormat.PNG, quality, baos);
                LogUtils.d("图片压缩前大小：" + baos.toByteArray().length / 1024 + "kb");
                if(baos.toByteArray().length / 1024 > 50) {
                    quality = 80;
                    baos.reset();
                    bmp.compress(Bitmap.CompressFormat.PNG, quality, baos);
                    LogUtils.d("质量压缩到原来的" + quality + "%时大小为：" + baos.toByteArray().length / 1024 + "kb");
                }
                LogUtils.d("图片压缩后大小：" + baos.toByteArray().length / 1024 + "kb");
                baos.writeTo(out);
                baos.flush();
                baos.close();
                out.close();
//				bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    public static File getFile(String fileName, Context con) {
        File file;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            isExist(FILE_PATH);
            file = new File(FILE_PATH, fileName);
        } else
            file = new File(con.getCacheDir(), fileName);
        return file;
    }
    /**
     * 判断文件夹是否存在,如果不存在则创建文件夹
     */
    public static void isExist(String path) {
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
    }
}

