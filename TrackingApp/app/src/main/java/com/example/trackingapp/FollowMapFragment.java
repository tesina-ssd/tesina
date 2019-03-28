package com.example.trackingapp;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FollowMapFragment extends Fragment implements FollowConnectionDialog.FollowConnectionDialogListener  {

    private final String followConnectionDialogTAG = "fllwConnDialogTAG";
    private final int followConnectionDialogRequestCode = 1;
    private final String followInfoDialogTAG = "fllwInfoDialogTAG";
    private final int followInfoDialogRequestCode = 2;

    private FragmentManager fragmentManager = null; //FragmentManager utilizzato per la gestione dei dialog
    private MapView mapView; //Mappa
    private FollowMapFragment thisFragment = this; //Rappresenta l'istanza corrente
    private FollowConnectionDialog followConnectionDialog = null;
    private FollowInfoDialog followInfoDialog = null;
    private String connectionCode = null;
    private GeoJsonSource userPositionGeoJson = null;
    private GeoJsonSource userPositionLineGeoJson = null;
    private ArrayList<Point> userPostionsArray = new ArrayList<Point>();
    private MapboxMap map = null;

    /**
     * Costruttore base vuoto, richiesto per l'implementazione (non chiedetemi il perchè)
     */
    public FollowMapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Alla prima creazione viene richiesta l'istanza attraverso la chiave
        if(savedInstanceState == null)
            Mapbox.getInstance(container.getContext(), getString(R.string.access_token));

        // Settaggio del layout
        View view = inflater.inflate(R.layout.follow_map_fragment, container, false);
        fragmentManager = getFragmentManager();

        // Da qui in poi è codice copiato da MapBox per mettere la mappa
        mapView = view.findViewById(R.id.FollowMapFragment_Map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                                    map = mapboxMap;

                                    mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                                        @Override
                                        public void onStyleLoaded(@NonNull Style style) {
                                            style.addImage("marker-icon-default",
                                                    BitmapFactory.decodeResource(
                                                            FollowMapFragment.this.getResources(), R.drawable.mapbox_marker_icon_default));

                                            userPositionLineGeoJson = new GeoJsonSource("user-postion-line");
                                            style.addSource(userPositionLineGeoJson);

                                            userPositionGeoJson = new GeoJsonSource("user-position");
                                            style.addSource(userPositionGeoJson);

                                            style.addLayer(new LineLayer("user-postion-line-layer", "user-postion-line").withProperties(
                                                    PropertyFactory.lineDasharray(new Float[] {0.01f, 2f}),
                                                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                                    PropertyFactory.lineWidth(4f),
                                                    PropertyFactory.lineColor(Color.parseColor("#D81B60"))
                                            ));

                                            style.addLayer(new SymbolLayer("user-position-layer", "user-position").withProperties(
                                                    PropertyFactory.iconImage("marker-icon-default")
                                            ));

                                        }
                                    });
                                }
                            });


        view.findViewById(R.id.FollowMapFragment_FabMenu_StartFollowing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followConnectionDialog = FollowConnectionDialog.newInstance();
                followConnectionDialog.setTargetFragment(thisFragment, followConnectionDialogRequestCode);
                followConnectionDialog.show(fragmentManager, followConnectionDialogTAG);
            }
        });

        view.findViewById(R.id.FollowMapFragment_FabMenu_ShowInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followInfoDialog = FollowInfoDialog.newInstance(connectionCode);
                followInfoDialog.setTargetFragment( thisFragment, followInfoDialogRequestCode);
                followInfoDialog.show(fragmentManager, followInfoDialogTAG);
            }
        });

        return  view;
    }

    @Override
    public void onFollowConnectionDialogOkClicked(String connectionCode) {
        this.connectionCode = connectionCode;
        startFollowing();
        onFollowConnectionDialogCancelClicked();
    }

    @Override
    public void onFollowConnectionDialogCancelClicked() {
        followConnectionDialog.dismiss();
    }

    private void startFollowing() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference excDoc = db.collection("excursion").document(connectionCode);
        excDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Snackbar.make(getView().findViewById(R.id.FollowMapFragment_Map),"Errore di connessione, riprovare", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    GeoPoint geo = snapshot.getGeoPoint("userLocation");
                    userPositionGeoJson.setGeoJson(Feature.fromGeometry(
                            Point.fromLngLat(geo.getLongitude(), geo.getLatitude())));

                    map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(geo.getLatitude(), geo.getLongitude()))
                            .build()));

                    userPostionsArray.add(Point.fromLngLat(geo.getLongitude(), geo.getLatitude()));
                    userPositionLineGeoJson.setGeoJson(FeatureCollection.fromFeatures(new Feature[] {Feature.fromGeometry(
                            LineString.fromLngLats(userPostionsArray)
                    )}));
                } else {
                    Snackbar.make(getView().findViewById(R.id.FollowMapFragment_Map),"Errore di connessione, riprovare", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        // Evita che la mappa chrashi quando ricaricata
        super.onDestroyView();
        //TODO: la chiusura va intercettata qua
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
