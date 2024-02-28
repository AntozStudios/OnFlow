package com.antozstudios.myapplication.activities;


import android.annotation.SuppressLint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import org.osmdroid.views.MapView;

import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


public class MainActivity extends AppCompatActivity {



    private IMapController controller;
    private MyLocationNewOverlay mMyLocationOverlay;
    TextView textView;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetDragHandleView bottomSheetDragHandleView;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.currentSpeed);
        bottomSheetDragHandleView = findViewById(R.id.drag);

        setupDialog();
        setupOSM();



        LinearLayout l = bottomSheetDialog.findViewById(R.id.linearLayout);
        Button b = l.findViewById(R.id.addProfil);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,AppManagerActivity.class);
            startActivity(intent);

            }
        });
        System.out.println(b);




    }



    void setupOSM()  {
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


        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(mMap);
        mRotationGestureOverlay.setEnabled(true);
        mMap.getOverlays().add(mRotationGestureOverlay);

        mMyLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(()->{

                        if (mMyLocationOverlay.getMyLocation() != null) {
                            controller.setCenter(mMyLocationOverlay.getMyLocation());
                            controller.animateTo(mMyLocationOverlay.getMyLocation());
                            textView.setText(String.valueOf(mMyLocationOverlay.getMyLocation().getLatitude()));
                        }
                    });

            }


        });
        //init zoom
        controller.setZoom(18.0);



        Log.e("TAG", "onCreate:in " + controller.zoomIn());
        Log.e("TAG", "onCreate: out  " + controller.zoomOut());

        mMap.getOverlays().add(mMyLocationOverlay);

        mMap.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                Log.e("TAG", "onCreate:la " + event.getSource().getMapCenter().getLatitude());
                Log.e("TAG", "onCreate:lo " + event.getSource().getMapCenter().getLongitude());
                return true;
            }


            @Override
            public boolean onZoom(ZoomEvent event) {

                Log.e("TAG", "onZoom zoom level: " + event.getZoomLevel() + "   source:  " + event.getSource());


                return false;
            }


        });


    }


    @SuppressLint("ClickableViewAccessibility")
    void setupDialog(){
         bottomSheetDialog = new BottomSheetDialog(this);
        View view =  View.inflate(getApplicationContext(),R.layout.bottom_sheet,null);

        bottomSheetDialog.setContentView(view);

        bottomSheetDragHandleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        bottomSheetDialog.show();
                        break;
                }
                return true;
            }
        });





    }



}

