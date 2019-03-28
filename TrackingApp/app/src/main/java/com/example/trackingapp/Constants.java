package com.example.trackingapp;

import com.google.firebase.auth.FirebaseAuth;

public class Constants {
    public static boolean IS_WORKING=false;
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String WHO_CALLING = "imcalling";
    public static final String BOOL_CONNECTED_MSG = "connectedmsg";
    public static final String BOOL_ALARM_MSG = "alarmmsg";
    public static final String KEY_IMAGE_PATH = "PathImg";
    public static final String KEY_USER_PHONE = "NumeroDiTelefono";
    public static final String KEY_PHONE_CONNECTED_TO_USER = "NumeroPersonaConnessa";
    public static final String KEY_ALARM_PHONE = "NumeroDiAllarme";
    public static final int SLOWCONN = 2;
    public static final int NO_CONN = 1;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LOCATION_MSG = "thislocationmsg";
    public static final String EMERGENCY_MSG = "thisemergencymsg";
    public static final String TEXT_KEYWORD = "keyword";
    public static final String SWITCH_ENABLESMS = "enableSms";
    public static final String SWITCH_GOOGLEMAPS_SMS = "smsgooglemaps";
    public static final String SWITCH_LOCATION_SMS = "smsLocation";
    public static final String SWITCH_SINGLE_SMS = "smsSingle";
    public static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String CHANNEL_ID = "serviceChannel";
    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    public static final int RC_SIGN_IN = 1234;
    public static String CONNECTED_PHONE_NUMBER ="";
    public static String  ALARM_PHONE_NUMBER = "";
}
