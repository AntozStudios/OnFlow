package com.antozstudios.myapplication.activities;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;

import com.antozstudios.myapplication.util.GetApps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CheckAppService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {




            }

        };
        runnable.run();
        handler.postDelayed(runnable,1000);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
