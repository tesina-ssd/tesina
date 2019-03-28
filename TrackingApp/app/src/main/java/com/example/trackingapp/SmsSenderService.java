package com.example.trackingapp;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.core.app.JobIntentService;

import static com.example.trackingapp.Constants.BOOL_ALARM_MSG;
import static com.example.trackingapp.Constants.BOOL_CONNECTED_MSG;
import static com.example.trackingapp.Constants.EMERGENCY_MSG;
import static com.example.trackingapp.Constants.PHONE_NUMBER;
import static com.example.trackingapp.Constants.SHARED_PREFS;
import static com.example.trackingapp.Constants.SWITCH_GOOGLEMAPS_SMS;
import static com.example.trackingapp.Constants.SWITCH_LOCATION_SMS;
import static com.example.trackingapp.Constants.SWITCH_SINGLE_SMS;
import static com.example.trackingapp.Constants.WHO_CALLING;

public class SmsSenderService extends JobIntentService {
    private final static String TAG = SmsSenderService.class.getSimpleName();


    private Resources r = null;
    private Context context = null;
    private String phoneNumber = null;
    private SmsTypes smsTypes;
    private boolean locationSms = false;
    private boolean googleMapsSms = false;
    private boolean singleSms = false;
    private boolean connectedSms = false;
    private boolean alarmSms = false;
    private String caller = "";



    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SmsSenderService.class, 123, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        smsTypes = new SmsTypes();
    }

    @Override
    protected void onHandleWork(Intent intent) {
       // Log.d(TAG, "onHandleIntent");
        //Toast.makeText(getApplicationContext(),"WOW",Toast.LENGTH_LONG).show();
        this.phoneNumber = intent.getExtras().getString(PHONE_NUMBER);
        this.caller = intent.getExtras().getString(WHO_CALLING);
        if(caller.equals(EMERGENCY_MSG)){
            this.connectedSms=  intent.getExtras().getBoolean(BOOL_CONNECTED_MSG);
            this.alarmSms = intent.getExtras().getBoolean(BOOL_ALARM_MSG);
        }

        //this.phoneNumber="5556";
        if (this.phoneNumber.length() == 0) {
            //Log.d(TAG, "Phonenumber empty, return.");
            return;
        }

        this.context = this;
        this.r = context.getResources();
        initSending();
    }


    private void initSending() {
        //Log.d(TAG, "initSending()");
        readSettings();
        if(connectedSms){
            sendConnectedMessage(phoneNumber);
        }
        if(alarmSms){
            sendAlarmMessage(phoneNumber);
        }
        if(!singleSms){
            sendDefaultMessage(phoneNumber);
            if(locationSms){
                sendLocationMessage(phoneNumber);
            }
            if(googleMapsSms){
                sendGoogleMapsMessage(phoneNumber);
            }

        }else {
            sendSingleMessage(phoneNumber);
        }
    }

    private void sendDefaultMessage(String phoneNumber) {
        SmsSenderService.this.sendSMS(phoneNumber, "Parola chiave trovata\nSms in arrivo");
    }


    private void readSettings() {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        locationSms = settings.getBoolean(SWITCH_LOCATION_SMS, false);
        googleMapsSms = settings.getBoolean(SWITCH_GOOGLEMAPS_SMS, false);
        singleSms = settings.getBoolean(SWITCH_SINGLE_SMS, false);
        //networkSms = settings.getBoolean("settings_network_sms", false);
        // speedType = Integer.parseInt(settings.getString("settings_kmh_or_mph", "0"));

    }





    public void sendGoogleMapsMessage(String phoneNumber) {
        //Log.d(TAG, "sendGoogleMapsMessage() " + location.getAccuracy());
        SmsSenderService.this.sendSMS(phoneNumber, smsTypes.getGoogleMapsSMS());
    }
    public void sendConnectedMessage(String phoneNumber) {
        //Log.d(TAG, "sendGoogleMapsMessage() " + location.getAccuracy());
        SmsSenderService.this.sendSMS(phoneNumber, smsTypes.getConnectedMessage());
    }
    public void sendAlarmMessage(String phoneNumber) {
        //Log.d(TAG, "sendGoogleMapsMessage() " + location.getAccuracy());
        SmsSenderService.this.sendSMS(phoneNumber, smsTypes.getAlarmMessage());
    }
    public void sendSingleMessage(String phoneNumber) {
        //Log.d(TAG, "sendGoogleMapsMessage() " + location.getAccuracy());
        SmsSenderService.this.sendSMS(phoneNumber, smsTypes.getSingleMessage());
    }
    public void sendLocationMessage(String phoneNumber) {
        //Log.d(TAG, "sendGoogleMapsMessage() " + location.getAccuracy());
        SmsSenderService.this.sendSMS(phoneNumber, smsTypes.getLocationMessage());
    }



    @Override
    public void onDestroy() {
        //Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public boolean onStopCurrentWork() {
        Log.d(TAG, "onStopCurrentWork");
        return super.onStopCurrentWork();
    }
    public void sendSMS(String phoneNumber, String message) {
        //Log.d(TAG, "Send SMS: " + phoneNumber + ", " + message);
        //on samsung intents can't be null. the messages are not sent if intents are null
        ArrayList<PendingIntent> samsungFix = new ArrayList<>();
        samsungFix.add(PendingIntent.getBroadcast(context, 0, new Intent("SMS_RECEIVED"), 0));

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(message);
        smsManager.sendMultipartTextMessage(phoneNumber, null, parts, samsungFix, samsungFix);
    }
}