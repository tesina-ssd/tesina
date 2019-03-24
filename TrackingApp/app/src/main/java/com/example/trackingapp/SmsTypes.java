package com.example.trackingapp;

import android.content.res.Resources;

import java.text.DecimalFormat;

public class SmsTypes {
    private int speedType = 0;
    public SmsTypes(){}

    public String getLocationMessage() {
        //Log.d(TAG, "sendLocationMessage()" + location.getAccuracy());


        DecimalFormat latAndLongFormat = new DecimalFormat("#.######");

        String text ="Location:\n";


        text += "Accuracy: " + " " + Math.round(LocationUpdater.getLocation().getAccuracy()) + "m\n";
        text += "Latitudine: " + " " + latAndLongFormat.format(LocationUpdater.getLocation().getLatitude()) + "\n";
        text += "Longitudine: " + " " + latAndLongFormat.format(LocationUpdater.getLocation().getLongitude()) + "";

        if (LocationUpdater.getLocation().hasSpeed()) {
            if (speedType == 0) {
                text += "\n" + "Speed: " + " " + ((int) convertMPStoKMH(LocationUpdater.getLocation().getSpeed())) + "KM/H";
            } else {
                text += "\n" + "Speed: " + " " + ((int) convertMPStoMPH(LocationUpdater.getLocation().getSpeed())) + "MPH";
            }
        }

        if (LocationUpdater.getLocation().hasAltitude() && LocationUpdater.getLocation().getAltitude() != 0) {
            text += "\n" + "Altitudine: " + " " + ((int) LocationUpdater.getLocation().getAltitude()) + "m";
        }

        return text;
    }

    private double convertMPStoKMH(double speed) {
        return speed * 3.6;
    }

    private double convertMPStoMPH(double speed) {
        return speed * 2.23694;
    }

    public String getGoogleMapsSMS() {
        //Log.d(TAG, "sendGoogleMapsMessage() " + location.getAccuracy());
        String text = "https://maps.google.com/maps?q=" + LocationUpdater.getLocation().getLatitude() + "," + LocationUpdater.getLocation().getLongitude();
        return text;
    }
}
