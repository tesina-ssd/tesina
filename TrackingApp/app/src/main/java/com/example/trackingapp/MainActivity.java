package com.example.trackingapp;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import static com.example.trackingapp.Constants.KEY_ALARM_PHONE;
import static com.example.trackingapp.Constants.KEY_PHONE_CONNECTED_TO_USER;
import static com.example.trackingapp.Constants.CONNECTED_PHONE_NUMBER;
import static com.example.trackingapp.Constants.ALARM_PHONE_NUMBER;


public class MainActivity extends AppCompatActivity implements AccountSettings.OnFragmentInteractionListener,FragSettings.OnFragmentInteractionListener {

    private TextView mTextMessage;
    private FragmentManager fragmentManager = null;
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private MenuItem selectedMenuItem = null;

    public Context getContex (){
        return getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: così funziona, vedere se si può togliere
        final NavController nav = Navigation.findNavController(this, R.id.nav_host_fragment);

        findViewById(R.id.IconProfileImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.navigate(R.id.action_global_accountSettings);
            }
        });

        findViewById(R.id.IconSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.navigate(R.id.action_global_smsFragment);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userid= FirebaseAuth.getInstance().getUid();
        db.collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(!doc.exists()){
                        /*FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.replace(R.id.frameLayout, AccountSettings.newInstance(true),"AccountSettings");
                        transaction.addToBackStack(null);
                        transaction.commit();*/
                    }else{
                        CONNECTED_PHONE_NUMBER= doc.get(KEY_PHONE_CONNECTED_TO_USER).toString();
                        ALARM_PHONE_NUMBER =( doc.get(KEY_ALARM_PHONE).toString());
                        Log.d("conn",CONNECTED_PHONE_NUMBER);
                        Log.d("alarm",CONNECTED_PHONE_NUMBER);
                    }
                }
            }
        });
        checkifGpsAvaible();
        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.BROADCAST_SMS);
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        permissions.add(Manifest.permission.INTERNET);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE);
        }
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);


        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // Loading fragment manager
        fragmentManager = getSupportFragmentManager();
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(MainActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                }

                break;
        }

    }

    private void checkifGpsAvaible() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }

    }
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
