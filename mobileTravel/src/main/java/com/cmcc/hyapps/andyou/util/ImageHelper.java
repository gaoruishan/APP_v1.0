
package com.cmcc.hyapps.andyou.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;

import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.ImageBucket;
import com.nostra13.universalimageloader.utils.L;
import com.qiniu.utils.InputStreamAt;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author kuloud
 */
public class ImageHelper {
    private Context mContext;
    private ContentResolver mContentResolver;

    private boolean mHasBuildImagesBucketList = false;

    private HashMap<String, String> mThumbnailList = new HashMap<String, String>();
    private HashMap<String, ImageBucket> mBucketList = new HashMap<String, ImageBucket>();

    public List<Image> selectBitmap = new ArrayList<Image>();
    public List<Image> tempSelectBitmap = new ArrayList<Image>();

    public static class ExifInfo {

        public final int rotation;
        public final boolean flipHorizontal;

        protected ExifInfo() {
            this.rotation = 0;
            this.flipHorizontal = false;
        }

        protected ExifInfo(int rotation, boolean flipHorizontal) {
            this.rotation = rotation;
            this.flipHorizontal = flipHorizontal;
        }
    }

    public ImageHelper(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    /**
     * public static ImageHelper getInstance() { if (sInstance == null) {
     * sInstance = new ImageHelper(); } return sInstance; } public void
     * init(Context context) { if (this.mContext == null) { this.mContext =
     * context; mContentResolver = context.getContentResolver(); } }
     */
    private void getThumbnail() {
        String[] projection = {
                Thumbnails._ID, Thumbnails.IMAGE_ID,
                Thumbnails.DATA
        };
        Cursor cursor = mContentResolver.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        getThumbnailColumnData(cursor);
        cursor.close();
    }

    private void getThumbnailColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int image_id;
            String image_path;
            int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

            do {
                // Get the field values
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);

                mThumbnailList.put("" + image_id, image_path);
            } while (cur.moveToNext());
        }
    }

    private void buildImagesBucketList() {
        getThumbnail();

        String columns[] = new String[] {
                Media._ID, Media.BUCKET_ID,
                Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
                Media.SIZE, Media.BUCKET_DISPLAY_NAME
        };

        String where = null;
        String sortOrder = Media.DATE_TAKEN + " desc";
        Cursor cur = mContentResolver.query(Media.EXTERNAL_CONTENT_URI, columns, where, null,
                sortOrder);
        if (cur.moveToFirst()) {
            int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
            int bucketDisplayNameIndex = cur
                    .getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);

            do {
                String _id = cur.getString(photoIDIndex);
                String path = cur.getString(photoPathIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex);
                String bucketId = cur.getString(bucketIdIndex);

                ImageBucket bucket = mBucketList.get(bucketId);
                if (bucket == null) {
                    bucket = new ImageBucket();
                    mBucketList.put(bucketId, bucket);
                    bucket.imageList = new ArrayList<Image>();
                    bucket.bucketName = bucketName;
                }
                bucket.count++;
                Image imageItem = new Image();
                imageItem.imageId = _id;
                imageItem.imagePath = Uri
                        .parse(Uri.decode(Uri.fromFile(new File(path)).toString()));
                if (mThumbnailList.containsKey(_id)) {
                    imageItem.thumbnailPath = Uri
                            .parse(Uri.decode(Uri.fromFile(new File(mThumbnailList.get(_id)))
                                    .toString()));
                }
                bucket.imageList.add(imageItem);
            } while (cur.moveToNext());
        }
        cur.close();

        mHasBuildImagesBucketList = true;
    }

    public List<ImageBucket> getImagesBucketList(boolean refresh) {
        if (refresh || (!refresh && !mHasBuildImagesBucketList)) {
            buildImagesBucketList();
        }
        List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
        Iterator<Entry<String, ImageBucket>> itr = mBucketList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Map.Entry<String, ImageBucket> entry = itr
                    .next();
            tmpList.add(entry.getValue());
        }
        return tmpList;
    }

    String getOriginalImagePath(String image_id) {
        String path = null;
        String[] projection = {
                Media._ID, Media.DATA
        };
        Cursor cursor = mContentResolver.query(Media.EXTERNAL_CONTENT_URI, projection,
                Media._ID + "=" + image_id, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(Media.DATA));
            cursor.close();
        }
        return path;
    }

    /*
     * NOTE : if the image is from network, it may cause ANR, so this method
     * should be called in background thread.
     */
    public static Bitmap revitionImageSize(Context ctx, Uri uri, int size) throws IOException {
        InputStream temp = ctx.getContentResolver().openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(temp, null, options);
        temp.close();

        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= size)
                    && (options.outHeight >> i <= size)) {
                temp = ctx.getContentResolver().openInputStream(uri);
                options.inSampleSize = 1 << i;
                options.inJustDecodeBounds = false;

                bitmap = BitmapFactory.decodeStream(temp, null, options);
                temp.close();
                break;
            }
            i++;
        }
        return bitmap;
    }

    public static String compressImageFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        ExifInfo exifInfo = ImageHelper.defineExifOrientation(srcPath);
        if (exifInfo.rotation != 0) {
            bitmap = rotateBitmap(bitmap, exifInfo.rotation);
        }
        String a = FileUtils.getExternalImageDir()+"/"+getFileNameString(srcPath);
        return getcompressesFile(bitmap, Bitmap.CompressFormat.JPEG, FileUtils.getExternalImageDir()+"/"+getFileNameString(srcPath), 75);
    }
    /**
     * get the file name,it's a string
     * @param pathString
     * @return
     */
    public static String getFileNameString(String pathString) {
        int number = pathString.lastIndexOf("/");
        String fileNameString = pathString.substring(number + 1);
        return fileNameString;
    }
    /**
     * compress the bitmap,then write it in the file
     * @param bitmap
     * @param format
     * @param pathString
     * @param percent
     * @return
     */
    public static String getcompressesFile(Bitmap bitmap, Bitmap.CompressFormat format, String pathString, int percent) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, percent, baos);
        File file = null;
        byte[] b = baos.toByteArray();
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            BufferedOutputStream stream = null;
            try {
                if (!TextUtils.isEmpty(pathString)) {
                    file = new File(pathString);
                    FileOutputStream fstream = new FileOutputStream(file);
                    stream = new BufferedOutputStream(fstream);
                    stream.write(b);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }else {
            return "";
        }
        return file.getAbsolutePath();

    }

    public static ExifInfo defineExifOrientation(String imageUri) {
        int rotation = 0;
        boolean flip = false;
        try {
            ExifInterface exif = new ExifInterface(imageUri);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    flip = true;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotation = 0;
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
        } catch (IOException e) {
            L.w("Can't read EXIF tags from file [%s]", imageUri);
        }
        return new ExifInfo(rotation, flip);
    }

    private static Bitmap rotateBitmap(Bitmap bm, int degree) {
        Matrix m = new Matrix();
        m.setRotate(degree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            int w = bm1.getWidth();
            int h = bm1.getHeight();
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

    public static InputStream bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        return sbs;
    }

    public static InputStreamAt bitmap2InputStreamAt(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        InputStreamAt isa = InputStreamAt.fromByte(baos.toByteArray());
        return isa;
    }

    public static Uri getOutputImageUri() {
        // get the mobile Pictures directory
        String picDir = FileUtils.getExternalImageDir();
        // get the current time
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE)
                .format(new Date());
        File imageFile = new File(picDir + File.separator + "ST_" + timeStamp + ".jpg");
        return Uri.fromFile(imageFile);
    }
}
