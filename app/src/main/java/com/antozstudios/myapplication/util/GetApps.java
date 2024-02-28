package com.antozstudios.myapplication.util;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.antozstudios.myapplication.activities.AppManagerActivity;

import java.util.ArrayList;
import java.util.List;

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
                appIcon.add(packageManager.getDefaultActivityIcon());
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

    void aa(Context context){
        ActivityManager am =(ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        ActivityManager.RunningTaskInfo task = tasks.get(0); // current task
        ComponentName rootActivity = task.baseActivity;


        String currentPackageName = rootActivity.getPackageName();
        if(currentPackageName.equals("com.sec.android.gallery3d")) {
            //Do whatever here
        }
    }

}
