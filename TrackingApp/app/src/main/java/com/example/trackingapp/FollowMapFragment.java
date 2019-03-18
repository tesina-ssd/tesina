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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FollowMapFragment extends Fragment implements FollowConnectionDialog.FollowConnectionDialogListener  {

    FragmentManager fragmentManager = null; //FragmentManager utilizzato per la gestione dei dialog
    MapView mapView; //Mappa
    FollowMapFragment thisFragment = this; //Rappresenta l'istanza corrente
    FollowConnectionDialog followConnectionDialog = null;
    FollowInfoDialog followInfoDialog = null;
    String connectionCode = null;

    /**
     * Costruttore base vuoto, richiesto per l'implementazione (non chiedetemi il perchè)
     */
    public FollowMapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Alla prima creazione viene richiesta l'istanza attraverso la chiave
        if(savedInstanceState == null)
            Mapbox.getInstance(getContext(), getString(R.string.access_token));

        // Settaggio del layout
        View view = inflater.inflate(R.layout.follow_map_fragment, container, false);
        fragmentManager = getFragmentManager();

        // Da qui in poi è codice copiato da MapBox per mettere la mappa
        mapView = (MapView) view.findViewById(R.id.FollowMapFragment_Map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull MapboxMap mapboxMap) {
                                    mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                                        @Override
                                        public void onStyleLoaded(@NonNull Style style) {
                                            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                                        }
                                    });
                                }
                            });
        view.findViewById(R.id.FollowMapFragment_FabMenu_StartFollowing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followConnectionDialog = FollowConnectionDialog.newInstance();
                followConnectionDialog.setTargetFragment((Fragment) thisFragment, 123);
                followConnectionDialog.show(fragmentManager, "followConnectionDialog");
            }
        });

        view.findViewById(R.id.FollowMapFragment_FabMenu_ShowInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followInfoDialog = FollowInfoDialog.newInstance(connectionCode);
                followInfoDialog.setTargetFragment((Fragment) thisFragment, 124);
                followInfoDialog.show(fragmentManager, "followInfoDialog");
            }
        });


        return  view;
    }

    @Override
    public void onDestroyView() {
        // Evita che la mappa chrashi quando ricaricata
        super.onDestroyView();
        //TODO: la chiusura va intercettata qua
        mapView.onDestroy();
    }

    @Override
    public void onFollowConnectionDialogOkClicked(String connectionCode) {
        this.connectionCode = connectionCode;
        onFollowConnectionDialogCancelClicked();
    }

    @Override
    public void onFollowConnectionDialogCancelClicked() {
        followConnectionDialog.dismiss();
    }
}
