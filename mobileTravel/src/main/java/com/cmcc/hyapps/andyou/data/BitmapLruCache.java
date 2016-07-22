
package com.cmcc.hyapps.andyou.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by kuloud on 14-8-15.
 */
public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *            the maximum number of entries in the cache. For all other
     *            caches, this is the maximum sum of the sizes of the entries in
     *            this cache.
     */
    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public Bitmap getBitmap(String url) {
        if (url.contains("file://")) {
            String localPath = url.substring(url.indexOf("file://"));
            return ImageLoaderManager.getInstance().getLoader()
                    .loadImageSync(Uri.decode(localPath));
        } else {
            return get(url);
        }
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

}
