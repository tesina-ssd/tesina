package com.example.trackingapp;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
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
import android.util.Log;

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

        return START_NOT_STICKY;
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
