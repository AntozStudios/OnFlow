package com.antozstudios.myapplication.activities;

import static android.content.Intent.ACTION_INSERT;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.antozstudios.myapplication.util.FileHelper;
import com.antozstudios.myapplication.util.GetApps;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class CheckAppService extends Service {

    GetApps getApps;

    private final String TAG = "Aktuelle App ist";
    boolean isActiv;

    public String profileName;
    private final IBinder binder = (IBinder) new LocalBinder();
    // Random number generator.

    Intent blockApp;


    @Override
    public void onCreate() {
        super.onCreate();
        getApps = GetApps.getInstance(getApplicationContext());

        blockApp = new Intent(this, BlockAppActivity.class);
        blockApp.addFlags(FLAG_ACTIVITY_NEW_TASK);

    }

    public class LocalBinder extends Binder {
        CheckAppService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return CheckAppService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (profileName != null) {
                    check(profileName);
                }


                handler.postDelayed(this, 500);
            }
        };
        runnable.run();
        return super.onStartCommand(intent, flags, startId);
    }


    void check(String profileName) {
        ArrayList<String> packageNames = FileHelper.getPackageNameOfProfile(getApplicationContext(), profileName);
        ActivityManager activityManager = getSystemService(ActivityManager.class);


        for (String s : packageNames) {
            if (s.contains(getApps.getLauncherTopApp(getApplicationContext())) && s.contains("true") && !isActiv) {
                blockApp.putExtra("packageName", s.replace(";true", ""));
                activityManager.killBackgroundProcesses(s.replace(";true", ""));
                startActivity(blockApp);
                isActiv = true;
            }

        }



    }


}