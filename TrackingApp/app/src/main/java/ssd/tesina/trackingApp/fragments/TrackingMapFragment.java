package ssd.tesina.trackingApp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import ssd.tesina.trackingApp.util.LocationUpdater;
import ssd.tesina.trackingApp.R;
import ssd.tesina.trackingApp.util.UsefulMethods;
import ssd.tesina.trackingApp.util.UserinfoUpdateService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import java.util.Objects;

import ssd.tesina.trackingApp.util.Constants;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Fragment rappresentate la mappa per il tracciamento dell'utente.
 * Contiene una mappa e un menu espandibile tramite un floating action button.
 */
public class TrackingMapFragment extends Fragment implements ConnectionDialog.ConnectionDialogListener, LayerStyleDialog.LayerStyleDialogInterface {

    private FragmentManager fragmentManager = null; //FragmentManager utilizzato per la gestione dei dialog
    private Button btnser;
    private FloatingActionButton btnAlert;
    private MapView mapView; //Mappa
    private static MapboxMap mapbox;
    private TrackingMapFragment thisFragment = this; //Rappresenta l'istanza corrente
    private ConnectionDialog connectionCodeGeneratorDialogFragment = null; // Rappresenta l'istanza di ConnectionCodeDialogFragment
    LayerStyleDialog layerStyleDialog = null;

    private final int CONNECTION_DIALOG_REQ_CODE = 1;
    private final String CONNECTION_DIALOG_TAG = "CONN_DIALOG";

    /**
     * Costruttore base vuoto, richiesto per l'implementazione (non chiedetemi il perchè)
     */
    public TrackingMapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Alla prima creazione viene richiesta l'istanza attraverso la chiave
        if (savedInstanceState == null && getContext() != null)
            Mapbox.getInstance(getContext(), getString(R.string.access_token));

        // Settaggio del layout
        View view = inflater.inflate(R.layout.tracking_map_fragment, container, false);

        // Log delle impostazioni di trasmissione dei dati scelte dall'utente
        // Log.i("CONNECTION SETTINGS", "SMS: " + Constants.SMS_ENABLE + "INTERNET: " + Constants.INTERNET_ENABLE);

        fragmentManager = getFragmentManager();
        mapView = view.findViewById(R.id.mapView);
        btnser = view.findViewById(R.id.btnStopService);
        btnAlert = view.findViewById(R.id.fab_alert);
      
        checkService();
      
        // Inizializzazione della mappa di mapBox
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapbox = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        new LocationUpdater(getContext());
                        enableLocationComponent();
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

        view.findViewById(R.id.menu_item_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = getLocationMode(getContext());
                if(mode == 3){
                    showConnectionDialog();
                }else{
                    Toast.makeText(getContext(),R.string.changeGpsmode,Toast.LENGTH_LONG).show();
                }
            }
        });

        view.findViewById(R.id.menu_item_key).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(!Constants.CHIAVE_ESCURSIONE.equals("")){
                 ConnectionCodeKeyDialog dialog = ConnectionCodeKeyDialog.newInstance();
                 dialog.show(fragmentManager, "KEY_DIALOG");
             }else{
                 Toast.makeText(getContext(),"Nessuna Escursione Attiva",Toast.LENGTH_LONG).show();
             }

            }
        });

        btnser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().stopService(new Intent(getContext(), UserinfoUpdateService.class));
                Constants.IS_TRACKING_SERVICE_WORKING =false;

                // Log.i("TRACKING SERVICE", "Tracking service closed correctly");
                btnser.setVisibility(View.GONE);
                btnAlert.hide();
                UsefulMethods.deleteKeyFromDB();

            }

        });

        btnAlert.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_trackingMapFragment_to_alertFragment));

        // Viene visualizzato il dialog di inizio escursione
        //showConnectionDialog();

        return  view;
    }

    private void showConnectionDialog() {
        // Log.i("TRACKING SERVICE", "Is tracking service working : " + String.valueOf(IS_TRACKING_SERVICE_WORKING));
        // Se una sessione di tracciamento non è già attiva viene visualizzato il dialog
        if(!Constants.IS_TRACKING_SERVICE_WORKING){
            // Apertura del dialog
            connectionCodeGeneratorDialogFragment = ConnectionDialog.newInstance();
            connectionCodeGeneratorDialogFragment.setTargetFragment(thisFragment, CONNECTION_DIALOG_REQ_CODE);
            connectionCodeGeneratorDialogFragment.show(fragmentManager, CONNECTION_DIALOG_TAG);
        }
    }
  
    private void checkService(){
        // Controllo della variabile condivisa IS_TRACKING_SERVICE_WORKING: controlla se un servizio di tracciamento è già attualmente attivo
        if (Constants.IS_TRACKING_SERVICE_WORKING) {
            btnser.setVisibility(View.VISIBLE); // Il pulsante per chiudere il servizio viene visualizzato
            btnAlert.show();
        } else {
            btnser.setVisibility(View.GONE); // Il pulsante per chiudere il servizio non viene visualizzato
            //btnAlert.setVisibility(View.GONE);
            btnAlert.hide();
        }
    }
  
/*    private Task<String> addMessage (String text){
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("firstNumber", 2);
        data.put("secondNumber", 6);
         FirebaseFunctions.getInstance()
                .getHttpsCallable("addNumbers")
                .call(data).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                Log.d("inFucnt","fjj");
                String hello= (String) httpsCallableResult.getData().toString();
                Toast.makeText(getContext(),hello,Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("inFucnt","fjj");
            }
        });
         return null;
    }*/

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

    /**
     * Questo metodo controlla gps mode
     * @param context
     * @return 0 se il gps è disattivato , 1 se è device only , 2 se battery saving mode , 3 high accuracy
     */
    private int getLocationMode(Context context)
    {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Questo metodo non ha nessun parametro.
     * Viene chiamato per controllar ese il gps è attivo
     */
    private void checkifGpsAvaible() {
        LocationManager locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGPSDisabledAlertToUser();
        }
    }

    /**
     * Questo metodo non ha nessun parametro
     * viene chiamato da @checkifGpsAvaible()
     * Crea un alert dialog se l'utente non ha il gps attivo
     */
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        alertDialogBuilder.setMessage(R.string.gpsMessage)
                .setCancelable(false)
                .setPositiveButton("Abilità GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("No grazie",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
        checkService();
        checkifGpsAvaible();
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
        btnser.setVisibility(View.VISIBLE);
        btnAlert.show();
    }

    @Override
    public void onConnectionDialogCancelClicked() {
        connectionCodeGeneratorDialogFragment.dismiss();
    }

    @Override
    public void onLayerStyleClicked(String layer) {
        layerStyleDialog.dismiss();
        mapbox.setStyle(layer);
    }
}
