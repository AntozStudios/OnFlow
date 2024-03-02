package com.antozstudios.myapplication.activities;


import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import android.os.Handler;
import android.provider.Settings;
import android.telecom.Call;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;


import com.antozstudios.myapplication.util.GetApps;
import com.antozstudios.myapplication.util.SpeedCalculator;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;



import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import org.osmdroid.views.MapView;


import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class MainActivity extends AppCompatActivity {


    SpeedCalculator speedCalculator;

    int a =0;
    private IMapController controller;
    private MyLocationNewOverlay mMyLocationOverlay;
    TextView currentSpeed;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetDragHandleView bottomSheetDragHandleView;


    double oldLat;
    double oldLon;

    String speedText;

    TextView setupSpeed;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        speedCalculator = new SpeedCalculator();
        setContentView(R.layout.activity_main);
        currentSpeed = findViewById(R.id.currentSpeed);
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);

        bottomSheetDragHandleView = findViewById(R.id.drag);

        setupSpeed = findViewById(R.id.setupSpeed);

        startService(new Intent(MainActivity.this, CheckAppService.class));

        StringBuilder stringBuilder = new StringBuilder();

        Handler handler =  new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,3000);
                for(String a : getUsedAppNames(getApplicationContext())){
                   stringBuilder.append(a);
                   setupSpeed.setText(stringBuilder.toString());
                }
            }
        };
        runnable.run();


        setupDialog();
        setupOSM();


        LinearLayout l = bottomSheetDialog.findViewById(R.id.linearLayout);
        Button b = l.findViewById(R.id.addProfil);
        b.setOnClickListener((View) -> {

            Intent a = new Intent(MainActivity.this, AppManagerActivity.class);
            startActivity(a);


        });


    }


    void setupOSM() {
        MapView mMap = findViewById(R.id.mapview);
        Configuration.getInstance().load(
                this,
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );


        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMultiTouchControls(true);
        mMap.getLocalVisibleRect(new Rect());
        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mMap);
        controller = mMap.getController();
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);


        mMyLocationOverlay.runOnFirstFix(() -> {
            runOnUiThread(() -> {
                if (mMyLocationOverlay.getMyLocation() != null) {
                    controller.setCenter(mMyLocationOverlay.getMyLocation());
                    controller.animateTo(mMyLocationOverlay.getMyLocation());

                }
            });

        });
        //init zoom
        controller.setZoom(18.0);


        mMap.getOverlays().add(mMyLocationOverlay);


    }


    @SuppressLint("ClickableViewAccessibility")
    void setupDialog() {
        bottomSheetDialog = new BottomSheetDialog(this);
        View view = View.inflate(getApplicationContext(), R.layout.bottom_sheet, null);

        bottomSheetDialog.setContentView(view);

        bottomSheetDragHandleView.setOnTouchListener((View, event) -> {
            {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    bottomSheetDialog.show();
                }
                return true;
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        Thread thread = new Thread(new Runnable() {
            private volatile boolean isRunning = true; // Kontrollvariable für die Schleife

            @Override
            public void run() {
                while (isRunning) { // Schleife läuft, solange isRunning true ist
                    try {
                        Thread.sleep(1000); // Wartezeit zwischen den Aktualisierungen (1 Sekunde)

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mMyLocationOverlay.getMyLocation() != null) {
                                    double newLat = mMyLocationOverlay.getMyLocation().getLatitude();
                                    double newLon = mMyLocationOverlay.getMyLocation().getLongitude();

                                    // Berechne die Geschwindigkeit basierend auf den alten und neuen Koordinaten
                                    int speed = (int) SpeedCalculator.calculateSpeed(oldLat, oldLon, newLat, newLon, (double) 1 / 36000); // Zeitdifferenz von 1 Stunde

                                    if (oldLat == 0 && oldLon == 0) {
                                        speedText = "0";
                                    } else {
                                        speedText = String.valueOf(speed / 10);

                                    }
                                    currentSpeed.setText(speedText);


                                    // Aktualisiere die alten Koordinaten für den nächsten Schleifendurchlauf
                                    oldLat = newLat;
                                    oldLon = newLon;


                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        // Hier könntest du eine angemessene Fehlerbehandlung implementieren
                    }
                }
            }
        });
        thread.start();

    }
    public static List<String> getUsedAppNames(Context context) {
        List<String> appNames = new ArrayList<>();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return appNames;
        }

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Get usage stats from 1 day ago
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        Map<String, String> packageToAppNameMap = getPackageToAppNameMap(context);

        if (usageStatsList != null) {
            for (UsageStats usageStats : usageStatsList) {
                String packageName = usageStats.getPackageName();
                if (packageToAppNameMap.containsKey(packageName)) {
                    String appName = packageToAppNameMap.get(packageName);
                    appNames.add(appName);
                }
            }
        }

        return appNames;
    }

    private static Map<String, String> getPackageToAppNameMap(Context context) {
        Map<String, String> packageToAppNameMap = new HashMap<>();
        List<String> packageNames = new ArrayList<>();
        packageNames.addAll(packageToAppNameMap.keySet());

        for (String packageName : packageNames) {
            String appName = getAppNameFromPackageName(context, packageName);
            packageToAppNameMap.put(packageName, appName);
        }

        return packageToAppNameMap;
    }

    private static String getAppNameFromPackageName(Context context, String packageName) {
        String appName = "";
        try {
            appName = context.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(packageName, 0)).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appName;
    }

}

