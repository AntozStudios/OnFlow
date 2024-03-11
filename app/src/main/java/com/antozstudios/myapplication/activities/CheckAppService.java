package com.antozstudios.myapplication.activities;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.renderscript.RenderScript;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.Nullable;


import com.antozstudios.myapplication.util.FileHelper;
import com.antozstudios.myapplication.util.GetApps;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckAppService extends Service {


    GetApps getApps;
    boolean doOpen = true;
    String oldData;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) { // Überprüfen, ob der Intent nicht null ist
            String profileName = intent.getStringExtra("profileName");
            if (profileName != null) { // Überprüfen, ob das Extra nicht null ist
                // Führe den gewünschten Code aus, da der Profilname gültig ist
                Log.e("Ausgabe", profileName);

                // Überprüfe, ob die Paketnamen für das Profil vorhanden sind
                List<String> packageNames = FileHelper.getPackageNameOfProfile(getApplicationContext(), profileName);
                if (packageNames != null && !packageNames.isEmpty()) {
                    // Überprüfe, ob die oberste App auf dem Bildschirm eine der blockierten Apps ist
                    String topAppPackageName = getLauncherTopApp(getApplicationContext());
                    if (!TextUtils.isEmpty(topAppPackageName)) {
                        for (String packageName : packageNames) {
                            if (packageName.contains("true") && packageName.contains(topAppPackageName)) {
                                // Starte die BlockAppActivity, wenn eine blockierte App erkannt wird
                                Intent blockIntent = new Intent(getApplicationContext(), BlockAppActivity.class);
                                blockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(blockIntent);
                                break;
                            }
                        }
                    }
                } else {
                    // Wenn keine Paketnamen für das Profil gefunden wurden, handle den Fall entsprechend
                    Log.e("CheckAppService", "Keine Paketnamen für das Profil gefunden: " + profileName);
                }
            } else {
                // Wenn das Extra "profileName" null ist, handle den Fall entsprechend
                Log.e("CheckAppService", "Profile Name ist null");
            }
        } else {
            // Wenn der Intent null ist, handle den Fall entsprechend
            Log.e("CheckAppService", "Intent ist null");
        }

        // Gib an, dass der Service im Vordergrund ausgeführt wird (falls erforderlich)
        // startForeground(...);

        // Gib zurück, wie der Service behandelt werden soll (z.B. START_STICKY)
        return START_STICKY;
    }



    public static String getLauncherTopApp(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        UsageStatsManager usageStatsManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> taskInfoList = manager.getRunningTasks(1);
            if (null != taskInfoList && !taskInfoList.isEmpty()) {
                return taskInfoList.get(0).topActivity.getPackageName();
            }
        } else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);

            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!TextUtils.isEmpty(result))
                Log.d("RESULT", result);
            return result;
        }
        return "";
    }
}
