package com.example.trackingapp.Util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocationUpdater {
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private static Location lastLocation;
    private static final long UPDATE_INTERVAL = 60000, FASTEST_INTERVAL = 5000; // = 5 seconds

    public LocationUpdater(Context context) {
        this.context = context;
        requestLocation();
    }

    private void requestLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }


    @SuppressLint("MissingPermission")
    public static Location getLocation() {
        return lastLocation;
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                lastLocation = locationList.get(locationList.size() - 1);

            }
        }
    };
    public void stopLocationUpdates(){
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }
    @SuppressLint("MissingPermission")
    public static Map<String, Object> getHash() {
        Log.d("Location"," " +lastLocation.getLatitude()+""+ lastLocation.getLongitude());
        Map<String,Object> userLocation = new HashMap<>();
        userLocation.put("userLocation",new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude()));
        userLocation.put("locationTime", new Timestamp(new Date()));
        return userLocation;
    }


}
