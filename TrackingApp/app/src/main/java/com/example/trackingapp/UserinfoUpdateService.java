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
import androidx.core.app.JobIntentService;
import androidx.fragment.app.FragmentManager;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UserinfoUpdateService extends JobIntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private FirebaseFirestore db = null;
    private static String userid = "";
    private final static String TAG_USERID = "USERID";
    private Calendar calendar = null;
    private static long time = 0;
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
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    private static final int JOB_ID = 122;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, UserinfoUpdateService.class, JOB_ID, work);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        shouldContinue=true;
        IsServiceWorking.isWorking=false;
        Log.d(TAG, "onCreate");
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
        IsServiceWorking.isWorking=true;
        if (googleApiClient != null) {
            googleApiClient.connect();
        }


    }


    @Override
    protected void onHandleWork(Intent intent) {
        if (intent != null) {
            IsServiceWorking.isWorking=true;
            userid = intent.getStringExtra(TAG_USERID);
            time=  intent.getLongExtra("Time",1L);
            while(true){
                Log.d("time-",""+time);
                if(time!=0){
                    if(shouldContinue){
                        date= new Date();
                        systemTime =date.getTime();
                        Log.d("timesystem-",""+systemTime);
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
                        return;
                    }
                }
                SystemClock.sleep(5000);
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
    public boolean onStopCurrentWork() {
        return super.onStopCurrentWork();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroyKILLEDD");
        IsServiceWorking.isWorking=false;
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
