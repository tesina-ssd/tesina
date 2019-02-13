package com.example.trackingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Fragment rappresentate la mappa per il tracciamento dell'utente.
 * Contiene una mappa e un menu espandibile tramite un floating action button.
 */
public class TrackingMapFragment extends Fragment implements ConnectionDialog.ConnectionDialogListener {

    FragmentManager fragmentManager = null; //FragmentManager utilizzato per la gestione dei dialog
    MapView mapView; //Mappa
    TrackingMapFragment thisFragment = this; //Rappresenta l'istanza corrente
    ConnectionDialog connectionCodeGeneratorDialogFragment = null; // Rappresenta l'istanza di ConnectionCodeDialogFragment

    /**
     * Costruttore base vuoto, richiesto per l'implementazione (non chiedetemi il perchè)
     */
    public TrackingMapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Alla prima creazione viene richiesta l'istanza attraverso la chiave
        if(savedInstanceState == null)
            Mapbox.getInstance(getContext(), getString(R.string.access_token));

        // Settaggio del layout
        View view = inflater.inflate(R.layout.tracking_map_fragment, container, false);
        fragmentManager = getFragmentManager();

        // Da qui in poi è codice copiato da MapBox per mettere la mappa
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull MapboxMap mapboxMap) {

                                    mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                                        @Override
                                        public void onStyleLoaded(@NonNull Style style) {

                                            // Map is set up and the style has loaded. Now you can add data or make other map adjustments


                                        }
                                    });
                                }
                            });

        view.findViewById(R.id.menu_item_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Apertura del dialog
                connectionCodeGeneratorDialogFragment = ConnectionDialog.newInstance(randomCode(8));
                connectionCodeGeneratorDialogFragment.setTargetFragment((Fragment) thisFragment, 123);
                connectionCodeGeneratorDialogFragment.show(fragmentManager, "dialog");
            }
        });
        return  view;
    }

    @Override
    public void onDestroyView() {
        // Evita che la mappa chrashi quando ricaricata
        super.onDestroyView();
        mapView.onDestroy();
    }

    private String randomCode(int num) {
        Random random = new Random();
        String ret = "";
        for(int i = 0; i < num; i++)
            ret += (char) (random.nextInt(120 - 70) + 70);
        Log.i("ConnCode", ret);
        return ret;
    }

    @Override
    public void onConnectionDialogOkClicked() {
        onConnectionDialogCancelClicked();
    }

    @Override
    public void onConnectionDialogCancelClicked() {
        connectionCodeGeneratorDialogFragment.dismiss();
    }
}
