package com.example.trackingapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trackingapp.R;
import com.example.trackingapp.Util.UserinfoUpdateService;
import com.example.trackingapp.Util.WriteData;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.example.trackingapp.Util.Constants.AB;
import static com.example.trackingapp.Util.Constants.AUTH;

public class ConnectionDialog extends DialogFragment implements
        ExcursionSheetFragment.OnExcursionSheetFragmentInteractionListener,
        ConnectionCodeFragment.OnConnectionCodeFragmentInteractionListener {
    static SecureRandom rnd = new SecureRandom();
    private String connection_Key ="";

    private FragmentManager fragmentManager = null;
    ConnectionDialogListener mListener = null;

    private Map<String,Object> excursionSheet;

    public interface ConnectionDialogListener {
        void onConnectionDialogOkClicked();
        void onConnectionDialogCancelClicked();
    }

    static public ConnectionDialog newInstance(String connectionCode) {
        ConnectionDialog dialog = new ConnectionDialog();
        // I parametri non vengono passati al costruttore, ma sotto forma di parametro di newInstance e poi
        // passati come bundle al metodo OnCreate()
        Bundle args = new Bundle();
        args.putString("connectionCode", connectionCode); // Viene passato come parametro alla creazione del dialog il codice da visualizzare
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if(getTargetFragment() != null)
                // Viene collegato il chiamante
                mListener = (ConnectionDialogListener) getTargetFragment();
            else throw new ClassCastException();
        } catch (ClassCastException e) {
            throw  new ClassCastException(ConnectionDialog.class + ": Deve essere implementata l'interfaccia di comunicazione nel chiamante");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Costruzione del layout
        fragmentManager = getChildFragmentManager();

        View v = inflater.inflate(R.layout.connection_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Fragment avantiFragment = ExcursionSheetFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.cardViewConnectionDialog,  avantiFragment, "avantiTest").commit();

        // Viene ritornata la View corrente
        return v;
    }

    @Override
    public void onExcursionSheetFragmentCancelPressed() {
        mListener.onConnectionDialogCancelClicked();
    }

    @Override
    public void onExcursionSheetFragmentNextPressed(Map<String,Object> excursionSheet) {
        this.excursionSheet = excursionSheet;
        connection_Key = randomCode(10);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        transaction.replace(R.id.cardViewConnectionDialog,  ConnectionCodeFragment.newInstance(connection_Key), "connCode").commit();
    }

    @Override
    public void onConnectionCodeFragmentCancelPressed() {
        mListener.onConnectionDialogCancelClicked();
    }

    @Override
    public void onConnectionCodeFragmentOkPressed() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            WriteData wrd = new WriteData(getContext(),getFragmentManager());
            wrd.setDb(db)
                .setUserid(AUTH.getUid())
                .setMkey(connection_Key)
                .setEcursion(excursionSheet)
                .keysCollection();
            Date date = new Date();
            Calendar c = new GregorianCalendar();
                    Timestamp timestamp =  (Timestamp) excursionSheet.get(("finishingTimeDate"));
                    date=timestamp.toDate();
            c.setTime(date);
             long time = c.getTimeInMillis();

        Log.d("time-",""+time);
        Intent serviceIntent = new Intent(getContext(), UserinfoUpdateService.class);
        serviceIntent.putExtra("USERID", AUTH.getUid());
        serviceIntent.putExtra("Time", time);

        ContextCompat.startForegroundService(getContext(), serviceIntent);
        mListener.onConnectionDialogOkClicked();
    }



    private String randomCode( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}
