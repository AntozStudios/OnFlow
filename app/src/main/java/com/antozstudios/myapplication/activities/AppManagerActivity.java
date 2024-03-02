package com.antozstudios.myapplication.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;

import com.antozstudios.myapplication.util.CreateTextFile;
import com.antozstudios.myapplication.util.GetApps;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;



public class AppManagerActivity extends AppCompatActivity {

    GetApps appInfo;
    Thread thread;

    private LinearLayout showAppsLin;

    private Button showApps;

    CreateTextFile createTextFile;



    StringBuilder stringBuilder;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stringBuilder = new StringBuilder();
        createTextFile = new CreateTextFile(getApplicationContext());
        setContentView(R.layout.appmanager);
        appInfo = new GetApps(getApplicationContext());
        List<String> appNames = appInfo.getAppNames();
        List<Drawable> appIcons = appInfo.getAppIcon();
        List<String> appPackageNames = appInfo.getAppPackageNames();

        showApps = findViewById(R.id.showApps);

        showApps.setOnClickListener((View)->{
            ScrollView showAppsScrollView = findViewById(R.id.showAppsScrollView);
            showAppsScrollView.setVisibility(View.VISIBLE);

            TextInputEditText profileName = findViewById(R.id.profileName);
            TextInputEditText maxSpeed = findViewById(R.id.maxSpeed);

            String data = profileName.getText().toString()+";"+maxSpeed.getText().toString()+";"+stringBuilder.toString();

            for(int i = 0;i<appPackageNames.size();i++){
                stringBuilder.append(appPackageNames.get(i)).append(";");
            }

                createTextFile.writeData("App-Profiles",data,profileName.getText().toString());



            
        });
        showAppsLin = findViewById(R.id.showAppsLin);

        appInfo = new GetApps(this);



         thread = new Thread(()->{
             runOnUiThread(()-> {
                 for (int i = 0; i < appNames.size(); i++) {

                     View view = View.inflate(getApplicationContext(), R.layout.mybutton, null);
                     @SuppressLint("UseSwitchCompatOrMaterialCode") Switch s = view.findViewById(R.id.sw);
                     String appName = appNames.get(i);
                     ImageView icon = view.findViewById(R.id.image_icon);
                     icon.setImageDrawable(appIcons.get(i));
                     s.setText(appName);
                     showAppsLin.addView(view);
                 }

             });
        });





    }

    @Override
    protected void onResume() {
        super.onResume();

        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.stop();
    }
}
