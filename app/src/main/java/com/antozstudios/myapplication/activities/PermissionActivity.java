package com.antozstudios.myapplication.activities;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.antozstudios.myapplication.R;
import com.facebook.shimmer.ShimmerFrameLayout;

public class PermissionActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUESTCODE = 100;
    private static final int PERMISSION_REQUESTCODE_OVERLAY = 101;
    private static final int PERMISSION_REQUESTCODE_STORAGE = 102;
    Button gpsoutButton, storageButton, overlayButton;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);




        gpsoutButton = findViewById(R.id.gpsButton);
        storageButton = findViewById(R.id.storageButton);
        overlayButton = findViewById(R.id.overlayButton);

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
            if (!checkStorage()) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUESTCODE_STORAGE);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        setColorForButtons();

        if(checkOverlay() && checkLocation()){
            Intent intent = new Intent(PermissionActivity.this,MainActivity.class);
            startActivities(new Intent[]{intent});
            finish();
        }




    }
    void setColorForButtons(){
        if (!checkLocation()) {
            gpsoutButton.setBackgroundColor(Color.RED);
        } else {
            gpsoutButton.setBackgroundColor(Color.GREEN);
        }

        if (!checkOverlay()) {
            overlayButton.setBackgroundColor(Color.RED);
        } else {
            overlayButton.setBackgroundColor(Color.GREEN);
        }

        if (!checkStorage()) {
            storageButton.setBackgroundColor(Color.RED);
        } else {
            storageButton.setBackgroundColor(Color.GREEN);
        }
    }


    boolean checkLocation(){
        return ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    boolean checkOverlay(){
        return Settings.canDrawOverlays(PermissionActivity.this);
    }

    boolean checkStorage(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }


}
