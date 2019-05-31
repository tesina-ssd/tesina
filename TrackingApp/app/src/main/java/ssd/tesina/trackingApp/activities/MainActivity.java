package ssd.tesina.trackingApp.activities;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import ssd.tesina.trackingApp.fragments.AccountSettings;
import ssd.tesina.trackingApp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;

import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Objects;

import ssd.tesina.trackingApp.util.Constants;

/**
 * Acticity principale, gestisce la navigazione all'interno dei fragment che compongono l'applicazione
 * (host di navigazione), la richiesta dei permessi relativi alle funzionalità dell'app.
 * All'interno dell'host vengono posizionati i fragment, gestiti attraverso il Navigation Component.
 */
public class MainActivity extends AppCompatActivity implements AccountSettings.OnFragmentInteractionListener {

    // Proprietà relative ai permessi dell'applicazione
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    /** Ritorna il context corrente */
    public Context getContex (){ return getApplicationContext(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prelevo il navigation controller corrente
        //TODO: così funziona, vedere se si può togliere
        final NavController nav = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Gestione della navigazione sul click sull'icona profilo
        findViewById(R.id.IconProfileImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.navigate(R.id.action_global_accountSettings);
            }
        });

        // Gestione della navigazione sul click sull'icona impostazioni
        findViewById(R.id.IconSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav.navigate(R.id.action_global_smsFragment);
            }
        });
        findViewById(R.id.privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://tracking-app-1b565.firebaseapp.com"));
                startActivity(browserIntent);
            }
        });
        // Controllo dell'esistenza del documento utente nel database
        // In caso di nuovo utente il documento non esiste e dovrà essere creato
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userid= FirebaseAuth.getInstance().getUid();
        db.collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(!doc.exists()){
                        // Il documento non esiste
                        /*FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.replace(R.id.frameLayout, AccountSettings.newInstance(true),"AccountSettings");
                        transaction.addToBackStack(null);
                        transaction.commit();*/
                        nav.navigate(R.id.action_global_accountSettings);
                    }else{
                        // Il documento esiste, le informazioni vengono prelevate ed inserite all'interno
                        // della classe costanti condivisa, per essere accessibili anche offline senza
                        // dover ogni volta prelevare nuovamente i dati dal database
                        Constants.CONNECTED_PHONE_NUMBER= doc.get(Constants.KEY_PHONE_CONNECTED_TO_USER).toString();
                        Constants.ALARM_PHONE_NUMBER =( doc.get(Constants.KEY_ALARM_PHONE).toString());
                        // Caricamento dell'immagine di profilo nella barra superiore
                        Picasso.get()
                                .load(Objects.requireNonNull(doc.get(Constants.KEY_IMAGE_PATH)).toString())
                                .into((ImageView) findViewById(R.id.IconProfileImage));
                        Log.d("conn", Constants.CONNECTED_PHONE_NUMBER);
                        Log.d("alarm", Constants.CONNECTED_PHONE_NUMBER);
                    }
                }
            }
        });

        // Definizione dei permessi necessari all'applicazione
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.BROADCAST_SMS);
        permissions.add(Manifest.permission.CALL_PHONE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE);
        }
        permissionsToRequest = permissionsToRequest(permissions);

        // Richiesta dei permessi all'utente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), Constants.ALL_PERMISSIONS_RESULT);
            }
        }
    }

    /**
      * Metodo che a partire da una lista di permessi controlla quelli che non
      * sono già stati assegnati all'app e ritorna un vettore con i permessi da
      * richiedere
      */
    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /** Metodo che a partire da un permesso determina se è già stato assegnato all'app */
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    //TODO: Singh commentatela pure tu
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.ALL_PERMISSIONS_RESULT) {
            permissionsRejected.clear();
            for (String perm : permissionsToRequest) {
                if (!hasPermission(perm)) {
                    permissionsRejected.add(perm);
                }
            }

            if (permissionsRejected.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                        new AlertDialog.Builder(MainActivity.this).
                                setMessage(R.string.permissionsMessage).
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestPermissions(permissionsRejected.
                                                toArray(new String[permissionsRejected.size()]), Constants.ALL_PERMISSIONS_RESULT);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Esce dall'applicazione
                                /*Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                homeIntent.addCategory(Intent.CATEGORY_HOME);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);*/
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);

                            }
                        }).create().show();

                        return;
                    }
                }
            }
        }
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
