package com.example.trackingapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.trackingapp.Util.LocationUpdater;
import com.example.trackingapp.R;
import com.example.trackingapp.Util.UserinfoUpdateService;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.example.trackingapp.Util.Constants.IS_WORKING;

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
        if(IS_WORKING){
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
                                            new LocationUpdater(getContext());
                                          enableLocationComponent();

                                        }
                                    });
                                }
                            });

        view.findViewById(R.id.menu_item_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        btnser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getContext(), UserinfoUpdateService.class);
                getContext().stopService(serviceIntent);
                IS_WORKING=false;
                Toast.makeText(getContext(),"STOPPED0",Toast.LENGTH_LONG).show();
                btnser.setVisibility(View.GONE);
            }

        });

        showDialog();

        return  view;
    }
    public static MapboxMap getMapbox(){
        return mapbox;
    }

    private void showDialog() {
        Log.d("WORK", String.valueOf(IS_WORKING));
        if(!IS_WORKING){
            // Apertura del dialog
            connectionCodeGeneratorDialogFragment = ConnectionDialog.newInstance("aaaa");
            connectionCodeGeneratorDialogFragment.setTargetFragment((Fragment) thisFragment, 123);
            connectionCodeGeneratorDialogFragment.show(fragmentManager, "dialog");
        }else{
            Toast.makeText(getContext(),"ALREADY EXCURSION ACTIVE",Toast.LENGTH_LONG).show();
        }
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


    }

    @Override
    public void onDestroyView() {
        // Evita che la mappa chrashi quando ricaricata
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
