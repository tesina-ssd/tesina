package com.example.trackingapp;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentManager;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UserinfoUpdateService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private PowerManager.WakeLock wakeLock;
    private FirebaseFirestore db = null;
    private String userid = "";
    private final static String TAG_USERID = "USERID";
    private Calendar calendar = null;
    private long time = 0;
    public  static volatile  boolean shouldContinue = true;
    private long systemTime = 0;
    private Context context = null;
    private Location location = null;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;;
    private Date date;
    private WriteData wrData;
    private Map<String,Object> userLocation=null;
    private static final long UPDATE_INTERVAL = 30000, FASTEST_INTERVAL = 5000; // = 5 seconds


    public UserinfoUpdateService() {
        super("UserinfoUpdateService");
        setIntentRedelivery(true);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startService(Context context, String userid) {
        Intent intent = new Intent(context, UserinfoUpdateService.class);
        //intent.setAction(ACTION_FOO);
        intent.putExtra(TAG_USERID, userid);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        shouldContinue=true;
        IsServiceWorking.isWorking=false;
        Log.d(TAG, "onCreate");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "ExampleApp:Wakelock");
        wakeLock.acquire();
        Log.d(TAG, "Wakelock acquired");
        db = FirebaseFirestore.getInstance();
        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        wrData = new WriteData(getBaseContext(),null);
        userLocation=new HashMap<>();
        calendar=new GregorianCalendar();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            IsServiceWorking.isWorking=true;
            if (googleApiClient != null) {
                googleApiClient.connect();
            }

            userid = intent.getStringExtra(TAG_USERID);
            db.collection("excursion").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        Timestamp timestamp =  (Timestamp)doc.get("finishingTimeDate");
                        date=timestamp.toDate();
                        calendar.setTime(date);
                        time = calendar.getTimeInMillis();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    stopSelf();
                }
            });
            while(true){
                if(time!=0){
                    if(shouldContinue){
                        date= new Date();
                        systemTime =date.getTime();
                        if(systemTime<time){
                            wrData.setDb(db)
                                    .setUserid(userid)
                                    .setUserLocation(userLocation);
                            Log.i("Sthgkghopped","serviceggggggStopped");
                        }else {
                            wrData.setDb(db)
                                    .setUserid(userid).setUserLocation(userLocation);
                            Log.i("Stoppghked","serviceggggggStopped");
                        }

                    }else{
                        Log.i("Stopped","serviceStopped");
                        stopSelf();
                    }
                }
                SystemClock.sleep(10000);
            }
        }
    }
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                mLastLocation = location;
                userLocation.put("userLocation",new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            }
        }
    };




    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        wakeLock.release();
        IsServiceWorking.isWorking=false;
        Log.d(TAG, "Wakelock released");
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
