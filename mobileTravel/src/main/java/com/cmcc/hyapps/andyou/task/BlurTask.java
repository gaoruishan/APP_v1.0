
package com.cmcc.hyapps.andyou.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.widget.blur.StackBlurManager;

/**
 * @author kuloud
 */
public class BlurTask extends BaseTask<Bitmap, Integer, Bitmap> {
    private Context mContext;

    public BlurTask(Context context) {
        mContext = context;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap bitmap = params[0];
        if (bitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            // Get 1/16 of the background
            options.inSampleSize = 1 << 4;
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_base,
                    options);
        }

        StackBlurManager stackBlurManager = new StackBlurManager(bitmap);
        stackBlurManager.process(75);
        bitmap = stackBlurManager.returnBlurredImage();
        return bitmap;
    }

}
