/**
 * 
 */

package com.cmcc.hyapps.andyou.util;

/**
 * Helper class to avoid fast double click on some devices.
 * 
 * @author Kuloud
 */
public final class ExcessiveClickBlocker {
    private static long sLastClickTime;

    public static boolean isExcessiveClick() {
        long time = System.currentTimeMillis();
        long timeD = time - sLastClickTime;
        if (timeD > 0 && timeD < 800) {
            return true;
        }
        sLastClickTime = time;
        return false;
    }
}
