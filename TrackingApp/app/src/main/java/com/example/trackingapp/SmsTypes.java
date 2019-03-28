package com.example.trackingapp;

import android.content.Context;
import android.content.res.Resources;

import java.text.DecimalFormat;

public class SmsTypes {
    private int speedType = 0;

    public SmsTypes(){

    }

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
        return "https://maps.google.com/maps?q=" + LocationUpdater.getLocation().getLatitude() + "," + LocationUpdater.getLocation().getLongitude();
    }

    public String getSingleMessage(){
        return "Sms Singolo:\n"+getLocationMessage()+"\n"+getGoogleMapsSMS();
    }
    public String getConnectedMessage(){
        return "Il nostro servizio di rientro non e' stato disattivato sono passati 30 minuti dall'ora prevista di rientro , controllare";
    }
    public String getAlarmMessage(){
        return "Il nostro servizio di rientro non e' stato disattivato sono passati 60 minuti dall'ora prevista di rientro , controllare";
    }
}
