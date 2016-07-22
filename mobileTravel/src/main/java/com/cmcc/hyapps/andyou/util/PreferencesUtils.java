
package com.cmcc.hyapps.andyou.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

/**
 * PreferencesUtils, easy to get or put data
 * <ul>
 * <strong>Preference Name</strong>
 * <li>you can change preference name by {@link #PREFERENCE_NAME}</li>
 * </ul>
 * <ul>
 * <strong>Put Value</strong>
 * <li>put string {@link #putString(android.content.Context, String, String)}</li>
 * <li>put int {@link #putInt(android.content.Context, String, int)}</li>
 * <li>put long {@link #putLong(android.content.Context, String, long)}</li>
 * <li>put float {@link #putFloat(android.content.Context, String, float)}</li>
 * <li>put boolean {@link #putBoolean(android.content.Context, String, boolean)}
 * </li>
 * </ul>
 * <ul>
 * <strong>Get Value</strong>
 * <li>get string {@link #getString(android.content.Context, String)},
 * {@link #getString(android.content.Context, String, String)}</li>
 * <li>get int {@link #getInt(android.content.Context, String)},
 * {@link #getInt(android.content.Context, String, int)}</li>
 * <li>get long {@link #getLong(android.content.Context, String)},
 * {@link #getLong(android.content.Context, String, long)}</li>
 * <li>get float {@link #getFloat(android.content.Context, String)},
 * {@link #getFloat(android.content.Context, String, float)}</li>
 * <li>get boolean {@link #getBoolean(android.content.Context, String)},
 * {@link #getBoolean(android.content.Context, String, boolean)}</li>
 * </ul>
 * 
 * @author kuloud
 */
public class PreferencesUtils {

    private static final String PREFERENCE_NAME = "pref_travel";

    public static final String KEY_INDEX_FIRST_START = "key_index_first_start";
    public static final String KEY_AUDIO_FIRST_START = "key_audio_first_start";


    /**
     * put string preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putString(Context context, String key, String value) {
        if (context == null) {
            return false;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * get string preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a string
     * @see #getString(android.content.Context, String, String)
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    public static boolean putEncryptString(Context context, String key, String value) {
        if (value == null) {
            return false;
        }
        return putString(context, key,
                new String(Base64.encode(AESUtil.CBCEncrypt(value.getBytes()), Base64.DEFAULT)));
    }

    public static String getEncryptString(Context context, String key) {
        String encryptString = getString(context, key, null);
        if (!TextUtils.isEmpty(encryptString)) {
            byte[] decryption = null;
            try {
                decryption = AESUtil.CBCDdecryption(Base64.decode(encryptString.getBytes(),
                        Base64.DEFAULT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (decryption != null) {
                encryptString = new String(decryption);
            }
        }
        return encryptString;
    }

    /**
     * get string preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a string
     */
    public static String getString(Context context, String key, String defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    /**
     * put int preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putInt(Context context, String key, int value) {
        if (context == null) {
            return false;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * get int preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a int
     * @see #getInt(android.content.Context, String, int)
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * get int preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a int
     */
    public static int getInt(Context context, String key, int defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    /**
     * put long preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putLong(Context context, String key, long value) {
        if (context == null) {
            return false;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static void addLong(Context context, String key, long value) {
        putLong(context, key, getLong(context, key) + value);
    }

    /**
     * get long preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a long
     * @see #getLong(android.content.Context, String, long)
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

    /**
     * get long preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a long
     */
    public static long getLong(Context context, String key, long defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    /**
     * put float preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putFloat(Context context, String key, float value) {
        if (context == null) {
            return false;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     * get float preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a float
     * @see #getFloat(android.content.Context, String, float)
     */
    public static float getFloat(Context context, String key) {
        return getFloat(context, key, -1);
    }

    /**
     * get float preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a float
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    /**
     * put boolean preferences
     * 
     * @param context
     * @param key The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        if (context == null) {
            return false;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * get boolean preferences, default is false
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a boolean
     * @see #getBoolean(android.content.Context, String, boolean)
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * get boolean preferences
     * 
     * @param context
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a boolean
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    public static boolean putDouble(Context context, String key, double value) {
        if (context == null) {
            return false;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, Double.doubleToLongBits(value));
        return editor.commit();
    }

    public static double getDouble(Context context, String key, double defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        return Double
                .longBitsToDouble(settings.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    /**
     * Remove preference by key
     * 
     * @param context
     * @param key
     * @return true if contains key, or false other cases.
     */
    public static boolean clear(Context context, String key) {
        if (context == null) {
            return false;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        if (settings.contains(key)) {
            settings.edit().remove(key).commit();
            return true;
        }
        return false;
    }
}
