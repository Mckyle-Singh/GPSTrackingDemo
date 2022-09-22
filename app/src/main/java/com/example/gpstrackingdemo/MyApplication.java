package com.example.gpstrackingdemo;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application
{
    private static MyApplication singleton;
    private List<Location>MyLocations;

    public List<Location> getMyLocations() {
        return MyLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        MyLocations = myLocations;
    }

    public MyApplication getInstance()
    {
        return singleton;
    }

    public void onCreate()
    {
        super.onCreate();
        singleton=this;
        MyLocations = new ArrayList<>();
    }
}
