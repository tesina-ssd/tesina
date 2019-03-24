package com.example.trackingapp;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.trackingapp.CreateNotficationChannel.CHANNEL_ID;
import static com.example.trackingapp.SmsFragment.SHARED_PREFS;
import static com.example.trackingapp.SmsFragment.SWITCH_ENABLESMS;
import static com.example.trackingapp.SmsFragment.TEXT_KEYWORD;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UserinfoUpdateService extends Service {
    private FirebaseFirestore db = null;
    private static String userid = "";
    private final static String TAG_USERID = "USERID";
    private Calendar calendar = null;
    private static long time = 0;
    public  static volatile  boolean shouldContinue = true;
    private long systemTime = 0;
    private Date date;
    private WriteData wrData;
    private Handler mHandler = new Handler();
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private  BroadcastReceiver br;


    @Override
    public void onCreate() {
        super.onCreate();
        shouldContinue=true;
        IsServiceWorking.isWorking=false;
        Log.d(TAG, "onCreate");
        db = FirebaseFirestore.getInstance();
        wrData = new WriteData(getBaseContext(),null);
        calendar=new GregorianCalendar();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        userid = intent.getStringExtra(TAG_USERID);
        inizializeWriteclass();
        time=  intent.getLongExtra("Time",1L);
        Intent intentAction = new Intent(getApplicationContext(),NotificationReceiver.class);

        //This is optional if you have more than one buttons and want to differentiate between two
        intentAction.putExtra("action","Dismiss");

        PendingIntent pIntentlogin = PendingIntent.getBroadcast(getApplicationContext(),1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.titoloEscursione))
                .setContentText(getResources().getString(R.string.testoEscursione))
                .setSmallIcon(R.drawable.logo_facebook)
                .setContentIntent(pendingIntent)
                .setColor(Color.BLUE)
                .addAction(R.drawable.logo_google, "Chiudi la escursione",pIntentlogin)
                .build();

        doWorkInBackground();
        startForeground(1, notification);
        startBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SMS_RECEIVED);
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);

        registerReceiver(br, filter);


        return START_NOT_STICKY;
    }

    private void startBroadcast() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SMS_RECEIVED)) {
                    String keyword ;
                    SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    keyword = sharedPreferences.getString(TEXT_KEYWORD, "");
                    boolean isSmsEnabled = sharedPreferences.getBoolean(SWITCH_ENABLESMS,false);

                    if ((keyword.length() == 0) || (!isSmsEnabled)) {
                        //Log.d(TAG, "No keyword available. Exit");
                        return;
                    }
                    ArrayList<SmsMessage> list = null;
                    try {
                        list = getMessagesWithKeyword(keyword, intent.getExtras());
                    } catch (Exception e) {
                        return;
                    }
                    if (list.size() == 0) {
                        //Log.d(TAG, "No message available. Exit");
                        return;
                    }

                    //This is used to close the notification tray
                    Intent serviceIntent = new Intent(context, SmsSenderService.class);
                    serviceIntent.putExtra("phoneNumber", list.get(0).getOriginatingAddress());

                    SmsSenderService.enqueueWork(context, serviceIntent);


                }
            }
        };
    }

    private ArrayList<SmsMessage> getMessagesWithKeyword(String keyword, Bundle bundle) {
        ArrayList<SmsMessage> list = new ArrayList<SmsMessage>();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdus.length; i++) {
                SmsMessage sms = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = bundle.getString("format");
                    sms = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    sms = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                if (sms.getMessageBody().toString().equals(keyword)) {
                    list.add(sms);
                }
            }
        }
        return list;
    }
    private void inizializeWriteclass() {
        wrData.setDb(db).setUserid(userid);
    }

    private void doWorkInBackground() {
        mToastRunnable.run();
    }

    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            IsServiceWorking.isWorking=true;
            Log.d("time-",""+time);
            if(time!=0){
                    date= new Date();
                    systemTime =date.getTime();
                    Log.d("timesystem-",""+systemTime);
                    if(systemTime<time){
                        wrData.setUserLocation(LocationUpdater.getHash());
                        Log.i("Sthgkghopped","serviceggggggStopped");
                    }else {
                        wrData.setUserLocation(LocationUpdater.getHash());
                        Log.i("Stoppghked","serviceggggggStopped");
                    }
            }
            mHandler.postDelayed(this, 5000);
        }
    };




    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        mHandler.removeCallbacks(mToastRunnable);
        Log.d(TAG, "onDestroyKILLEDD");
        IsServiceWorking.isWorking=false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
