package com.example.trackingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements AccountSettings.OnFragmentInteractionListener,FragSettings.OnFragmentInteractionListener {

    private TextView mTextMessage;
    private FragmentManager fragmentManager = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_track:
                    //loadMapboxMap();
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, new TrackingMapFragment(), "map").commit();

                    return true;
                case R.id.navigation_follow:
                    return true;
                case R.id.navigation_notification_center:
                    return true;
                case R.id.navigation_settings:

                    fragmentManager.beginTransaction().replace(R.id.frameLayout, new FragSettings(), "map")
                            .addToBackStack(null).commit();
                    return true;
            }
            return false;
        }
    };
    public Context getContex (){


        return getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userid= FirebaseAuth.getInstance().getUid();
        db.collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(!doc.exists()){
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.replace(R.id.frameLayout, AccountSettings.newInstance(true),"AccountSettings");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            }
        });
        // Loading fragment manager
        fragmentManager = getSupportFragmentManager();

        // Navigation bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_track);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
