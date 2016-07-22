
package com.cmcc.hyapps.andyou.util;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.kj.guradc.VideoActivity;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.VideoPlaybackActivity;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.model.QHScenic;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author calvin
 */
public class CommonUtils {
    // Throws AssertionError if the input is false.
    public static void assertTrue(boolean cond) {
        if (!cond) {
            throw new AssertionError();
        }
    }

    public static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                // ignored
            }
        }
    }

    public static void closeCursor(Cursor c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public static boolean isValidUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        return url.startsWith("file://") || Patterns.WEB_URL.matcher(url).matches();
    }

    public static void playVideo(Activity activity, QHScenic.QHVideo video) {
        if (!NetUtils.isNetworkAvailable(activity)) {
            Toast.makeText(activity, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }

        if (video == null /* ||!isValidUrl(video.url) */) {
            return;
        }

        if (video.id > 0) {
            Intent intent = new Intent(activity, VideoActivity.class);
            intent.putExtra(Const.EXTRA_ID, video.id);
            activity.startActivity(intent);
        }
//        else if (video.url.startsWith("rtsp")) {
//            Intent intent = new Intent(activity, VideoActivity.class);
//            intent.putExtra(Const.EXTRA_URI, Uri.parse(video.url));
//            activity.startActivity(intent);
//        }
        else {
            Intent intent = new Intent(activity,VideoPlaybackActivity.class);
            intent.putExtra(Const.EXTRA_VIDEO, video);
            activity.startActivity(intent);
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
    }

    public static String hexdigest(String s) {
        try {
            String hd;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hd = hash.toString(16); // BigInteger strips leading 0's
            while (hd.length() < 32) {
                hd = "0" + hd;
            } // pad with leading 0's
            return hd;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

}
