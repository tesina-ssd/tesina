package com.example.trackingapp;

import android.annotation.SuppressLint;
import android.app.job.JobScheduler;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static com.firebase.ui.auth.AuthUI.TAG;

/**
 * Fragment rappresentate la mappa per il tracciamento dell'utente.
 * Contiene una mappa e un menu espandibile tramite un floating action button.
 */
public class TrackingMapFragment extends Fragment implements ConnectionDialog.ConnectionDialogListener {
    private static final int JOB_ID = 122;
    static final String TAG = "0123";
    FragmentManager fragmentManager = null; //FragmentManager utilizzato per la gestione dei dialog
    private Button btnser;
    MapView mapView; //Mappa
    private static MapboxMap mapbox;
    TrackingMapFragment thisFragment = this; //Rappresenta l'istanza corrente
    ConnectionDialog connectionCodeGeneratorDialogFragment = null; // Rappresenta l'istanza di ConnectionCodeDialogFragment

    /**
     * Costruttore base vuoto, richiesto per l'implementazione (non chiedetemi il perchè)
     */
    public TrackingMapFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Alla prima creazione viene richiesta l'istanza attraverso la chiave
        if(savedInstanceState == null)
            Mapbox.getInstance(getContext(), getString(R.string.access_token));

        // Settaggio del layout
        View view = inflater.inflate(R.layout.tracking_map_fragment, container, false);
        fragmentManager = getFragmentManager();
        btnser = view.findViewById(R.id.btnStopService);
        if(IsServiceWorking.isWorking){
            btnser.setText("ExcusionWorking");
            btnser.setVisibility(View.VISIBLE);
        }else{
            btnser.setVisibility(View.GONE);
        }

        // Da qui in poi è codice copiato da MapBox per mettere la mappa
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                                    mapbox = mapboxMap;
                                    mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                                        @Override
                                        public void onStyleLoaded(@NonNull Style style) {
                                            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                                           enableLocationComponent();

                                        }
                                    });
                                }
                            });

        view.findViewById(R.id.menu_item_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mapbox.setCameraPosition();
            }
        });
        view.findViewById(R.id.menu_item_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!btnser.isShown()){
                    // Apertura del dialog
                    connectionCodeGeneratorDialogFragment = ConnectionDialog.newInstance(randomCode(8));
                    connectionCodeGeneratorDialogFragment.setTargetFragment((Fragment) thisFragment, 123);
                    connectionCodeGeneratorDialogFragment.show(fragmentManager, "dialog");
                }else{
                    Toast.makeText(getContext(),"ALREADY EXCURSION ACTIVE",Toast.LENGTH_LONG).show();
                }

            }
        });
        btnser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getContext(), UserinfoUpdateService.class);
                getContext().stopService(serviceIntent);
                IsServiceWorking.isWorking=false;
                Toast.makeText(getContext(),"STOPPED0",Toast.LENGTH_LONG).show();
                btnser.setVisibility(View.GONE);
            }

        });
        return  view;
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent() {
      /*  CameraPosition position = new CameraPosition.Builder()
                .zoom(10)
                .tilt(20)
                .build();*/
           // mapbox.setCameraPosition(position);
        // Get an instance of the component
            LocationComponent locationComponent = mapbox.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(getContext(), mapbox.getStyle());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.GPS);
            new LocationUpdater(locationComponent);

    }

    @Override
    public void onDestroyView() {
        // Evita che la mappa chrashi quando ricaricata
        super.onDestroyView();
        mapView.onDestroy();
    }


    @Override
    public void onConnectionDialogOkClicked() {
        onConnectionDialogCancelClicked();
        btnser.setText("ExcusionWorking");
        btnser.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionDialogCancelClicked() {
        connectionCodeGeneratorDialogFragment.dismiss();
    }
}
