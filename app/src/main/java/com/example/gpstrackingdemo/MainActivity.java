package com.example.gpstrackingdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int PERMISSIONS_FINE_LOCATION = 99 ;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int DEFAULT_UPDATE_INTERVAL =30;

    //references to UI elements
    TextView tv_lat,tv_lon,tv_altitude,tv_accuracy,tv_sensor,tv_speed,tv_updates,tv_address,tv_waypointCount;
    Switch sw_locationupdates,sw_gps;
    Button btn_newWaypoint,btnShowWaypointList,btn_ShowMap;

    //declare a variable to remember if  we are tracking the location or not
    boolean UpdateOb=false;

    //Current location Variable
    Location currentLocation;

    //List of saved locations
    List<Location>SavedLocations;

    // Location request is a config file for all settings related to FusedLocationProviderClient
    LocationRequest locationRequest;
    LocationCallback locationCallBack;

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
        tv_waypointCount=findViewById(R.id.tv_CrumbCount);
        sw_gps=findViewById(R.id.sw_gps);
        sw_locationupdates=findViewById(R.id.sw_locationsupdates);
        btn_newWaypoint=findViewById(R.id.btn_NewWaypoint);
        btnShowWaypointList=findViewById(R.id.btn_ShowWaypointList);
        btn_ShowMap=findViewById(R.id.btn_showMap);

        //set all properties of LocationRequest
        locationRequest = new LocationRequest();

        //sets how often the GPS is checked or updated
        locationRequest.setInterval(1000*DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000*FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack=new LocationCallback()
        {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //save the location
                UpdateUIvalues(locationResult.getLastLocation());
            }
        };

        btn_newWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //get the GPS location


                //add the new location to list
                MyApplication myApplication=(MyApplication)getApplicationContext();
                SavedLocations=myApplication.getMyLocations();
                SavedLocations.add(currentLocation);
            }
        });


        btnShowWaypointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(MainActivity.this, ShowSavedLocationList.class);
                startActivity(i);
            }
        });


        btn_ShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i =new Intent(MainActivity.this,MapsActivity.class);
                        startActivity(i);
            }
        });

        sw_gps.setOnClickListener(new  View.OnClickListener() {
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

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(sw_locationupdates.isChecked())
                {
                    //turn on location updates
                    startLocationUpdates();
                }
                else
                {
                   //turn off tracking
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();


    }


    private void stopLocationUpdates()
    {
        tv_updates.setText("Location is not being tracked");
        tv_lat.setText("Location is not being  tracked");
        tv_lon.setText("Location is not being tracked");
        tv_speed.setText("Location is not being tracked");
        tv_address.setText("Location is not being tracked");
        tv_accuracy.setText("Location is not being tracked");
        tv_altitude.setText("Location is not being tracked");
        tv_sensor.setText("Location is not being tracked");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);

    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates()
    {
        tv_updates.setText("Location is  being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    updateGPS();
                }
                else
                {
                    Toast.makeText(this,"This app requires persmission",Toast.LENGTH_SHORT).show();
                }
        }
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
                public void onSuccess(Location location)
                {
                    UpdateUIvalues(location);
                    currentLocation =location;
                }
            });
        }
        else
        {
            //permission denied

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }

    }


    private void UpdateUIvalues(Location location)
    {
        //update all the text view objects with new location

        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        //check if the phone can read altitude

        if (location.hasAltitude())
        {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else
        {
            tv_altitude.setText("Not available");
        }

        //check if the phone can read speed

        if (location.hasSpeed())
        {
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else
        {
            tv_speed.setText("Not available");
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                                location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (IOException e)
        {
           tv_address.setText("Unable to get address");
        }

        MyApplication myApplication=(MyApplication)getApplicationContext();
        SavedLocations=myApplication.getMyLocations();

        //show the number of waypoints saved

        tv_waypointCount.setText(Integer.toString(SavedLocations.size()));
    }
}