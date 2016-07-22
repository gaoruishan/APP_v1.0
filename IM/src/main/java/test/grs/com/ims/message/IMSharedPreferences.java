package test.grs.com.ims.message;

import android.content.Context;
import android.content.SharedPreferences;

import com.littlec.sdk.utils.DESEncrypt;

import test.grs.com.ims.IMApp;

public class IMSharedPreferences {
	private final static String CM_PREFERENCES = "im_preferences";
	private final static SharedPreferences sp = (SharedPreferences) IMApp.mContext.getSharedPreferences(CM_PREFERENCES,
			Context.MODE_PRIVATE);
	
	public static final String ACCOUNT = "account";
	public static final String PASSWORD = "password";
	public static final String AVATARURL = "avataUrl";
	public static final String NICKNAME = "nickname";

	public static int getInt(String key, int defValue) {
		return sp.getInt(key, defValue);
	}
	
	public static boolean getBoolean(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}
	
	public static boolean getSettingBoolean(String key, boolean defValue) {
		return sp.getBoolean(key + IMApp.getInstance().getCurrentUserName(), defValue);
	}
	
	public static boolean putSettingBoolean(String key, boolean value) {
		return sp.edit().putBoolean(key + IMApp.getInstance().getCurrentUserName(), value).commit();
	}
	
	public static String getString(String key, String defValue) {
		String value = sp.getString(key, defValue);
		if(value == null || value.equals(defValue)) {
			return defValue;
		}else {
			String mingStr;
			try {
				mingStr = DESEncrypt.decrypt(value);
			}
			catch(Exception e) {
				e.printStackTrace();
				mingStr = value;
			}
			return mingStr;
		}
	}
	
	public static boolean putInt(String key, int value) {
		return sp.edit().putInt(key, value).commit();
	}
	
	public static boolean putBoolean(String key, boolean value) {
		return sp.edit().putBoolean(key, value).commit();
	}
	
	public static boolean putString(String key, String value) {
		String miStr;
		try {
			miStr = DESEncrypt.encrypt(value);
		}
		catch(Exception e) {
			e.printStackTrace();
			miStr = value;
		}
		return sp.edit().putString(key, miStr).commit();
	}
	
	public static boolean deleteString(String key) {
		return sp.edit().remove(key).commit();
	}
}
