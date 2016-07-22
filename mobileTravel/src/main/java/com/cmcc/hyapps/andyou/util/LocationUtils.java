/**
 * 
 */

package com.cmcc.hyapps.andyou.util;

import android.content.Context;

import com.cmcc.hyapps.andyou.model.Location;

/**
 * @author kuloud
 */
public class LocationUtils {
    private static final String KEY_LATITUDE = "key_latitude";
    private static final String KEY_LONGITUDE = "key_longitude";
    private static final String KEY_CITY = "key_city";

    /**
     * Default location: Tiananmen
     */
    private static final float DEFAULT_LATITUDE = 39.915168F;
    private static final float DEFAULT_LONGITUDE = 116.403875F;

    /**
     * 
     */
    private LocationUtils() {
    }

    public static Location getLastKnownLocation(Context context) {
        Location location = new Location(PreferencesUtils.getDouble(context, KEY_LATITUDE,
                DEFAULT_LATITUDE),
                PreferencesUtils.getDouble(context, KEY_LONGITUDE, DEFAULT_LONGITUDE));
        location.city = PreferencesUtils.getString(context, KEY_CITY);
        return location;
    }

    public static void setLastKnownLocation(Context context, Location location) {
        PreferencesUtils.putDouble(context, KEY_LATITUDE, location.latitude);
        PreferencesUtils.putDouble(context, KEY_LONGITUDE, location.longitude);
        PreferencesUtils.putString(context, KEY_CITY, location.city);
    }

    public static String formatDistance(float meters) {
        if (meters < 1000) {
            return ((int) meters) + "m";
        } else if (meters < 10000) {
            return formatDec(meters / 1000f, 1) + "km";
        } else {
            return ((int) (meters / 1000f)) + "km";
        }
    }

    static String formatDec(float val, int dec) {
        int factor = (int) Math.pow(10, dec);

        int front = (int) (val);
        int back = (int) Math.abs(val * (factor)) % factor;

        return front + "." + back;
    }
}
