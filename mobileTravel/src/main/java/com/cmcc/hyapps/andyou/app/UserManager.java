/**
 * 
 */

package com.cmcc.hyapps.andyou.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.cmcc.hyapps.andyou.activity.FreshLoginActivity;
import com.cmcc.hyapps.andyou.activity.LoginActivity;
import com.cmcc.hyapps.andyou.util.AppUtils;

/**
 * handle operations of user.
 * 
 * @author kuloud
 */
public final class UserManager {
    /**
     * Handle with local action with user
     * 
     * @param activity
     * @return
     */
    public static boolean makeSureLogin(Activity activity, int requestCode) {
        if (AppUtils.getQHUser(activity) != null) {
            return false;
        } else {
            Intent login = new Intent(activity, FreshLoginActivity.class);
            activity.startActivityForResult(login, requestCode);
            return true;
        }
    }

    public static boolean isLogin(Context activity){
        return  AppUtils.getQHUser(activity) != null ? true : false;
    }

}
