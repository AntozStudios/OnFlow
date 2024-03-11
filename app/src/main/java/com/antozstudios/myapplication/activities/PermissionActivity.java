package com.antozstudios.myapplication.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.antozstudios.myapplication.R;


import android.app.AppOpsManager;


public class PermissionActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUESTCODE = 100;
    private static final int PERMISSION_REQUESTCODE_OVERLAY = 101;
    private static final int PERMISSION_REQUESTCODE_STORAGE = 102;

    Button gpsoutButton, storageButton, overlayButton,usageButton;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);




        gpsoutButton = findViewById(R.id.gpsButton);
        storageButton = findViewById(R.id.storageButton);
        overlayButton = findViewById(R.id.overlayButton);
        usageButton = findViewById(R.id.usageButton);

        gpsoutButton.setOnClickListener(view -> {
           if(!checkLocation()){
               requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUESTCODE);
           }
        });

        overlayButton.setOnClickListener(view -> {
            if (!checkOverlay()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERMISSION_REQUESTCODE_OVERLAY);
            }
        });

        storageButton.setOnClickListener(view ->{

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                startActivity(new Intent(Settings.ACTION_APP_LOCALE_SETTINGS));
            }


        });
        usageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasUsageStatsPermission(PermissionActivity.this)){
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    finish();
                }
            }
        });






    }

    @Override
    protected void onResume() {
        super.onResume();
        setColorForButtons();

        if(checkOverlay() && checkLocation() && hasUsageStatsPermission(PermissionActivity.this)){
            startActivity(new Intent(PermissionActivity.this,MainActivity.class));
            finish();
        }

    }
    void setColorForButtons(){
        if (checkLocation()) {
            gpsoutButton.setBackgroundColor(Color.GREEN);
        } else {
            gpsoutButton.setBackgroundColor(Color.RED);
        }

        if (checkOverlay()) {
            overlayButton.setBackgroundColor(Color.GREEN);
        } else {
            overlayButton.setBackgroundColor(Color.RED);
        }

        if (hasUsageStorageRead(PermissionActivity.this)) {
            storageButton.setBackgroundColor(Color.GREEN);
        } else {
            storageButton.setBackgroundColor(Color.RED);
        }


        if (hasUsageStatsPermission(PermissionActivity.this)) {
            usageButton.setBackgroundColor(Color.GREEN);
        } else {
            usageButton.setBackgroundColor(Color.RED);
        }



    }


    boolean checkLocation(){
        return ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    boolean checkOverlay(){
        return Settings.canDrawOverlays(PermissionActivity.this);
    }






    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public static boolean hasUsageStorageRead(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_READ_EXTERNAL_STORAGE, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

}
