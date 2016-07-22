package com.cmcc.hyapps.andyou.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;

/**
 * Created by bingbing on 2015/10/13.
 */
public class GPSManager {
    public  static void toggleGPS(Context mContext) {
        if (!isGpsEnable(mContext)){
            Intent gpsIntent = new Intent();
            gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
            gpsIntent.setData(Uri.parse("custom:3"));
            try {
                PendingIntent.getBroadcast(mContext, 0, gpsIntent, 0).send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isGpsEnable(Context mContext) {
        LocationManager locationManager =
                ((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean  isHasLocationPermission(Context mContext){
        //  mContext.enforceCallingPermission("android.permission.ACCESS_COARSE_LOCATION", "TODO: message if thrown");
        PackageManager pm = mContext.getPackageManager();
        boolean permission_coarse = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.ACCESS_COARSE_LOCATION", mContext.getPackageName()));
        boolean permission_fine = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.ACCESS_FINE_LOCATION", mContext.getPackageName()));
        return permission_coarse && permission_fine;
    }
}
