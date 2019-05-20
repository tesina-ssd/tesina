package com.example.trackingapp.fragments;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trackingapp.R;
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

import static android.graphics.Color.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lineProgress;
import static com.mapbox.mapboxsdk.style.expressions.Expression.linear;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineGradient;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class FollowMapFragment extends Fragment implements FollowConnectionDialog.FollowConnectionDialogListener,  LayerStyleDialog.LayerStyleDialogInterface {

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
    private MapboxMap mapboxMap = null;
    LayerStyleDialog layerStyleDialog = null;

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
                                    FollowMapFragment.this.mapboxMap = mapboxMap;

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
                                                    lineCap(Property.LINE_CAP_ROUND),
                                                    lineJoin(Property.LINE_JOIN_ROUND),
                                                    lineWidth(3f),
                                                    lineGradient(interpolate(
                                                            linear(), lineProgress(),
                                                            stop(0f, rgb(6, 1, 255)), // blue
                                                            stop(0.1f, rgb(59, 118, 227)), // royal blue
                                                            stop(0.3f, rgb(7, 238, 251)), // cyan
                                                            stop(0.5f, rgb(0, 255, 42)), // lime
                                                            stop(0.7f, rgb(255, 252, 0)), // yellow
                                                            stop(1f, rgb(255, 30, 0)) // red
                                                    ))));

                                            style.addLayer(new SymbolLayer("user-position-layer", "user-position").withProperties(
                                                    iconImage("marker-icon-default")
                                            ));
                                        }
                                    });
                                }
                            });


        view.findViewById(R.id.menu_item_layer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layerStyleDialog = LayerStyleDialog.newInstance();
                layerStyleDialog.setTargetFragment((Fragment) thisFragment, 125);
                layerStyleDialog.show(fragmentManager, "layer-dialog");
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
                if(connectionCode!=null){
                    followInfoDialog = FollowInfoDialog.newInstance(connectionCode);
                    followInfoDialog.setTargetFragment( thisFragment, followInfoDialogRequestCode);
                    followInfoDialog.show(fragmentManager, followInfoDialogTAG);
                }else{
                    Toast.makeText(getContext(),"Nessuna chiave inserita!",Toast.LENGTH_LONG).show();
                }
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

                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
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
        Log.d("ONX", "onDestroy");
        // Evita che la mappa chrashi quando ricaricata
        super.onDestroyView();
        //TODO: la chiusura va intercettata qua
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        Log.d("ONX", "onResume");
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        Log.d("ONX", "onStart");
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        Log.d("ONX", "onStop");
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        Log.d("ONX", "onPause");
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        Log.d("ONX", "onLowMemory");
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLayerStyleClicked(String layer) {
        layerStyleDialog.dismiss();
        mapboxMap.setStyle(layer);
    }
}