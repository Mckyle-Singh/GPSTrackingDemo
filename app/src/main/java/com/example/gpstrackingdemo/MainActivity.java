package com.example.gpstrackingdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {


//    public static final int FAST_UPDATE_INTERVAL = 5;
//    private static final int DEFAULT_UPDATE_INTERVAL =30;
    //references to UI elements
    TextView tv_lat,tv_lon,tv_altitude,tv_accuracy,tv_sensor,tv_speed,tv_updates,tv_address;
    Switch sw_locationupdates,sw_gps;

    //declare a variable to remember if  we are tracking the location or not

    boolean UpdateOb=false;

    // Location request is a config file for all settings related to FusedLocationProviderClient
    LocationRequest locationRequest;

    //Googles API for location services
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //give each UI variable a value

        tv_lat=findViewById(R.id.tv_lat);
        tv_lon=findViewById(R.id.tv_lon);
        tv_altitude=findViewById(R.id.tv_altitude);
        tv_accuracy=findViewById(R.id.tv_accuracy);
        tv_sensor=findViewById(R.id.tv_sensor);
        tv_speed=findViewById(R.id.tv_speed);
        tv_updates=findViewById(R.id.tv_updates);
        tv_address=findViewById(R.id.tv_address);

        sw_gps=findViewById(R.id.sw_gps);
        sw_locationupdates=findViewById(R.id.sw_locationsupdates);

        //sets how often the GPS is checked or updated
//        locationRequest.setInterval(30000);
//        locationRequest.setFastestInterval(5000);
//
//        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(sw_gps.isChecked())
                {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                }
                else
                {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers or Wifi");
                }

            }
        });//end of on create method


    }

    private void updateGPS()
    {   //GET permissions from the user to track GPS
        //get the current Location from the fused client
        //update the UI

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient
                                     (MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            //user provides permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                }
            })
        }
        else
        {
            //permission denied

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, PERMISSIONS_FINE_LOCATION});
            }
        }

    }
}