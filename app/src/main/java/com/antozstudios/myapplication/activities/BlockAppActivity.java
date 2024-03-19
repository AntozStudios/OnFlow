package com.antozstudios.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.antozstudios.myapplication.R;
import com.antozstudios.myapplication.util.GetApps;

public class BlockAppActivity extends AppManagerActivity{


    Button returnButton;
    ImageView imageIcon;
    TextView appName;

    String packageName;
    GetApps getApps;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockapp);
        getApps = GetApps.getInstance(getApplicationContext());
        imageIcon = findViewById(R.id.imageIcon);
        appName = findViewById(R.id.appName);
        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra("packageName")){
            String name = intent.getStringExtra("packageName");
            imageIcon.setImageDrawable(getApps.getIconFromPackage(getApplicationContext(),name));
            appName.setText(getApps.getAppNameFromPackage(getApplicationContext(),name));

        }


        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
