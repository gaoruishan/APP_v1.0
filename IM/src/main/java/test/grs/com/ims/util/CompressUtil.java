package test.grs.com.ims.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import test.grs.com.ims.message.UriImage;

/**
 *  压缩工具类
 * Created by gaoruishan on 16/4/28.
 */
public class CompressUtil {
    private static final int MAX_IMAGE_HEIGHT = 768;
    private static final int MAX_IMAGE_WIDTH = 1024;
    public static final int MAX_IMAGE_SIZE = 500 * 1024; // max 500k

    /**
     * 压缩－>旋转－>压缩输出
     * @param context
     * @param filePath
     * @param newFilePath
     * @param q
     * @return
     * @throws FileNotFoundException
     */
    public static long compressImage(Context context, String filePath,
                                     String newFilePath, int q) throws FileNotFoundException {
        //1,压缩
        Bitmap bm = zipPicture(context, Uri.fromFile(new File(filePath)));

        int degree = readPictureDegree(filePath);
        //2,旋转
        if (degree != 0) {
            bm = rotatePicture(bm, degree);
        }

        File outputFile = new File(newFilePath);

        FileOutputStream out = new FileOutputStream(outputFile);
        //3,输出
        bm.compress(Bitmap.CompressFormat.JPEG, q, out);

        if(out != null){
            try{
                out.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return outputFile.length();

    }

    /**
     * 压缩
     * @param context
     * @param picUri
     * @return
     */
    public static Bitmap zipPicture(Context context, Uri picUri) {
        if(picUri == null){
            return null;
        }

        final UriImage uriImage = new UriImage(context, picUri);
        Bitmap pic = null;
        final byte[] result = uriImage.getResizedImageData(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT, MAX_IMAGE_SIZE);
        if (result == null) {
            return null;
        }
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inPurgeable = true;
        //option.inSampleSize = 4; //too aggressive: the bitmap will be uploaded to server, not the the thumbnail
        pic = BitmapFactory.decodeByteArray(result, 0, result.length, option);
        return pic;
    }

    /**
     * 读图片的角度
     * @param var0
     * @return
     */
    public static int readPictureDegree(String var0) {
        short var1 = 0;

        try {
            ExifInterface var2 = new ExifInterface(var0);
            int var3 = var2.getAttributeInt("Orientation", 1);
            switch(var3) {
                case 3:
                    var1 = 180;
                    break;
                case 6:
                    var1 = 90;
                    break;
                case 8:
                    var1 = 270;
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return var1;
    }

    /**
     * 旋转图片
     * @param var0
     * @param var1
     * @return
     */
    public static Bitmap rotatePicture(Bitmap var0, int var1) {
        if(var0 == null) {
            return null;
        } else {
            Bitmap var2 = var0;
            if(var1 != 0) {
                boolean var3 = var0.isMutable();
                Matrix var4 = new Matrix();
                var4.postRotate((float)var1);

                try {
                    var2 = Bitmap.createBitmap(var0, 0, 0, var0.getWidth(), var0.getHeight(), var4, false);
                } catch (OutOfMemoryError var6) {
                    System.gc();
                    System.runFinalization();
                    var2 = Bitmap.createBitmap(var0, 0, 0, var0.getWidth(), var0.getHeight(), var4, false);
                }

                var0.recycle();
                var0 = null;
            }

            return var2;
        }
    }
}
