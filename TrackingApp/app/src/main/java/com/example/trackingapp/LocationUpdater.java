package com.example.trackingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.mapboxsdk.location.LocationComponent;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocationUpdater {

    private static Map<String, Object> userLocation = null;
    static LocationComponent comp;
    LocationUpdater(LocationComponent component) {
        comp= component;
        userLocation = new HashMap<>();

    }

    @SuppressLint("MissingPermission")
    public static Location getLocation() {
        return comp.getLastKnownLocation();
    }

    @SuppressLint("MissingPermission")
    public static Map<String, Object> getHash() {
        userLocation.put("userLocation", new GeoPoint(comp.getLastKnownLocation().getLatitude(), comp.getLastKnownLocation().getLongitude()));
        return userLocation;
    }

}
