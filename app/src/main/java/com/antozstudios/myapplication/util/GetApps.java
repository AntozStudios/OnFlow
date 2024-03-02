package com.antozstudios.myapplication.util;

import static android.content.Context.ACTIVITY_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.antozstudios.myapplication.activities.AppManagerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetApps {

    private Context mContext;
    List<String> appNames = new ArrayList<>();
    List<String> appPackageNames = new ArrayList<>();

    List<Drawable> appIcon = new ArrayList<>();

    public GetApps(Context context) {
        mContext = context;

        PackageManager packageManager = mContext.getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);


        for (ApplicationInfo packageInfo : packages) {
            //Checks if its a user app
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packageManager.getApplicationLabel(packageInfo).toString();
                appNames.add(appName);
                appPackageNames.add(packageInfo.packageName);
                try {
                    appIcon.add(packageManager.getApplicationIcon(packageInfo.packageName));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public List<String> getAppNames() {
        return appNames;
    }

    public List<String> getAppPackageNames() {
        return appPackageNames;
    }

    public List<Drawable> getAppIcon() {
        return appIcon;
    }


    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {

            for(int i = 0;i<activityManager.getRunningAppProcesses().size();i++){
                if(packageName.contains(activityManager.getRunningAppProcesses().get(i).processName)){
                    return true;
                }

            }
        }


        return false;
    }

}
