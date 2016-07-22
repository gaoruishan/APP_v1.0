
package com.cmcc.hyapps.andyou.widget.blur;

import android.graphics.Bitmap;

/**
 * @modified by Kuloud
 */
interface BlurProcess {
    /**
     * Process the given image, blurring by the supplied radius. If radius is 0,
     * this will return original
     * 
     * @param original the bitmap to be blurred
     * @param radius the radius in pixels to blur the image
     * @param limit (0, 1] to resize the bitmap
     * @return the blurred version of the image.
     */
    public Bitmap blur(Bitmap original, float radius, float limit);
}
