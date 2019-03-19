package com.example.trackingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import java.util.Random;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Fragment rappresentate la mappa per il tracciamento dell'utente.
 * Contiene una mappa e un menu espandibile tramite un floating action button.
 */
public class TrackingMapFragment extends Fragment implements ConnectionDialog.ConnectionDialogListener {

    // Stringa rappresentate il fragment che visualizza il codice di connessione
    private final String connectionCodeGeneratorDialogFragmentTAG = "connCodeDialogFrag";
    private final int connectionCodeGenreatorDialogFragmentRequestCode = 1;

    private FragmentManager fragmentManager = null; //FragmentManager utilizzato per la gestione dei dialog
    private MapView mapView; //Mappa
    private TrackingMapFragment thisFragment = this; //Rappresenta l'istanza corrente
    private ConnectionDialog connectionCodeGeneratorDialogFragment = null; // Rappresenta l'istanza di ConnectionCodeDialogFragment

    public TrackingMapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Alla prima creazione viene richiesta l'istanza attraverso la chiave
        if(savedInstanceState == null)
            Mapbox.getInstance(container.getContext(), getString(R.string.access_token));

        // Settaggio del layout
        View view = inflater.inflate(R.layout.tracking_map_fragment, container, false);
        fragmentManager = getFragmentManager();

        // Da qui in poi è codice copiato da MapBox per mettere la mappa
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull MapboxMap mapboxMap) {
                                    mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                                        @Override
                                        public void onStyleLoaded(@NonNull Style style) {}
                                    });
                                }
                            });

        view.findViewById(R.id.menu_item_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Apertura del dialog
                connectionCodeGeneratorDialogFragment = ConnectionDialog.newInstance(randomCode());
                connectionCodeGeneratorDialogFragment.setTargetFragment(thisFragment, connectionCodeGenreatorDialogFragmentRequestCode);
                connectionCodeGeneratorDialogFragment.show(fragmentManager, connectionCodeGeneratorDialogFragmentTAG);
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

    private String randomCode() {
        Random random = new Random();
        String ret = "";
        for(int i = 0; i < 8; i++)
            ret += (char) (random.nextInt(120 - 70) + 70);
        return ret;
    }

    @Override
    public void onConnectionDialogOkClicked() {
        // Le operazioni di caricamento dei dati vengono effettuate direttamente dentro il dialog
        onConnectionDialogCancelClicked();
    }

    @Override
    public void onConnectionDialogCancelClicked() {
        connectionCodeGeneratorDialogFragment.dismiss();
    }
}
