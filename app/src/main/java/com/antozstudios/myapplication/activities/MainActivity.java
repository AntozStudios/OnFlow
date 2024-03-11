package com.antozstudios.myapplication.activities;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.antozstudios.myapplication.R;


import com.antozstudios.myapplication.util.CustomTileFactory;
import com.antozstudios.myapplication.util.FileHelper;
import com.antozstudios.myapplication.util.SpeedCalculator;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;



import org.osmdroid.views.MapView;


import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {



    SpeedCalculator speedCalculator;
    Spinner spinner;
    ArrayAdapter<String> arrayAdapter;
    private MyLocationNewOverlay mMyLocationOverlay;
    TextView currentSpeed;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetDragHandleView bottomSheetDragHandleView;







    double oldLat;
    double oldLon;

    TextView setupSpeed;
    private final double maxZoom = 22.0;
    private final double minZoom = 20.0;

    TextView currentProfile;
    private Intent checkService;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


         checkService = new Intent(MainActivity.this,CheckAppService.class);

        speedCalculator = new SpeedCalculator();
        setContentView(R.layout.activity_main);
        currentSpeed = findViewById(R.id.currentSpeed);


        bottomSheetDragHandleView = findViewById(R.id.drag);

        setupSpeed = findViewById(R.id.setupSpeed);


        setupDialog();
        setupOSM();
        setupSpinner();
        createThread();





        currentProfile = findViewById(R.id.currentProfile);

        if(!arrayAdapter.isEmpty()){

            currentProfile.setText(FileHelper.getProfileFromProfile(getApplicationContext(),spinner.getSelectedItem().toString()));
            setupSpeed.setText(FileHelper.getSpeedFromProfile(getApplicationContext(),spinner.getSelectedItem().toString()));

        }

        startService(checkService);





}




    void setupOSM() {
        MapView mMap = findViewById(R.id.mapview);
        Configuration.getInstance().load(
                this,
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );

        mMap.setTileSource(CustomTileFactory.Dark);
        mMap.setMultiTouchControls(true);
        mMap.setMaxZoomLevel(maxZoom);
        mMap.setMinZoomLevel(minZoom);
        mMap.getLocalVisibleRect(new Rect());
        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mMap);
        IMapController controller = mMap.getController();
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(false);
        controller.setZoom(minZoom);

        mMap.setBackgroundColor(Color.BLACK);
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

        LinearLayout l = bottomSheetDialog.findViewById(R.id.linearLayout);
        Button b = l.findViewById(R.id.addProfil);
        b.setOnClickListener((View) -> {
            Intent a = new Intent(MainActivity.this, AppManagerActivity.class);
            startActivity(a);



        });


    }


    @Override
    protected void onResume() {
        super.onResume();


        if(!arrayAdapter.isEmpty()){

            currentProfile.setText(FileHelper.getProfileFromProfile(getApplicationContext(),spinner.getSelectedItem().toString()));
            setupSpeed.setText(FileHelper.getSpeedFromProfile(getApplicationContext(),spinner.getSelectedItem().toString()));

            if(checkService!=null){
                checkService.putExtra("profileName",spinner.getSelectedItem().toString());

            }


        }

    }




    void createThread() {


        Handler handler = new Handler();

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                handler.postDelayed(this, 100);

                if (mMyLocationOverlay.getLastFix() != null) {

                    double newLat = mMyLocationOverlay.getLastFix().getLatitude();
                    double newLon = mMyLocationOverlay.getLastFix().getLongitude();

                    // Berechne die Geschwindigkeit basierend auf den alten und neuen Koordinaten
                    if (oldLat != 0 && oldLon != 0) {
                        int speed = (int) SpeedCalculator.calculateSpeed(oldLat, oldLon, newLat, newLon, (double) 1 / 36000) /10 ; // Zeitdifferenz von 1 Stunde
                        currentSpeed.setText(String.valueOf(speed));
                    }




                    // Aktualisiere die alten Koordinaten für den nächsten Schleifendurchlauf
                    oldLat = newLat;
                    oldLon = newLon;
                }

            }


        };
        runnable.run();







        }



    void setupSpinner(){
        spinner = bottomSheetDialog.findViewById(R.id.selectProfileSpinner);
         arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        File dir = new File(getApplicationContext().getExternalFilesDir(null), "App-Profiles");


        if(dir.exists()){
            for(File f: dir.listFiles()){
                arrayAdapter.add(f.getName().replace(".txt",""));
            }
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);

        }



    }





}

