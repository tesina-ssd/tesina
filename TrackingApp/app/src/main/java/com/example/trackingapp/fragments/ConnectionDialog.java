package com.example.trackingapp.fragments;

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
import com.example.trackingapp.util.ExcursionSheetMapBuilder;
import com.example.trackingapp.util.UsefulMethods;
import com.example.trackingapp.util.UserinfoUpdateService;
import com.example.trackingapp.util.WriteData;
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

import static com.example.trackingapp.util.Constants.AB;
import static com.example.trackingapp.util.Constants.AUTH;
import static com.example.trackingapp.util.Constants.CHIAVE_ESCURSIONE;

public class ConnectionDialog extends DialogFragment implements
        ExcursionSheetFragmentPt1.OnExcursionSheetFragmentPt1InteractionListener,
        ExcursionSheetFragmentPt2.OnExcursionSheetFragmentPt2InteractionListener,
        ExcursionSheetFragmentPt3.OnExcursionSheetFragmentPt3InteractionListener,
        ConnectionCodeFragment.OnConnectionCodeFragmentInteractionListener {

    private static SecureRandom rnd = new SecureRandom();
    private WriteData wrd = null;
    private FirebaseFirestore db = null;
    private FragmentManager fragmentManager = null;
    ConnectionDialogListener mListener = null;

    private Map<String,Object> excursionSheet;

    public interface ConnectionDialogListener {
        void onConnectionDialogOkClicked();
        void onConnectionDialogCancelClicked();
    }

    static public ConnectionDialog newInstance() {
        ConnectionDialog dialog = new ConnectionDialog();
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
        db = FirebaseFirestore.getInstance();
        wrd = new WriteData(getContext(),getFragmentManager());
        View v = inflater.inflate(R.layout.connection_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.cardViewConnectionDialog,  ExcursionSheetFragmentPt1.newInstance(), "excS1").commit();

        // Viene ritornata la View corrente
        return v;
    }

    @Override
    public void onExcursionSheetFragmentPt1CancelPressed() {
        mListener.onConnectionDialogCancelClicked();
    }

    @Override
    public void onExcursionSheetFragmentPt1NextPressed(ExcursionSheetMapBuilder excursionSheet) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        transaction.replace(R.id.cardViewConnectionDialog,  ExcursionSheetFragmentPt2.newInstance(excursionSheet), "excS2").commit();
    }

    @Override
    public void onExcursionSheetFragmentPt2CancelPressed() {
        mListener.onConnectionDialogCancelClicked();
    }

    @Override
    public void onExcursionSheetFragmentPt2NextPressed(ExcursionSheetMapBuilder excursionSheet) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        transaction.replace(R.id.cardViewConnectionDialog,  ExcursionSheetFragmentPt3.newInstance(excursionSheet), "excS3").commit();
    }

    @Override
    public void onExcursionSheetFragmentPt3CancelPressed() {
        mListener.onConnectionDialogCancelClicked();
    }

    @Override
    public void onExcursionSheetFragmentPt3NextPressed(Map excursionSheet) {
        this.excursionSheet = excursionSheet;
        String connection_Key = randomCode(10);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        transaction.replace(R.id.cardViewConnectionDialog,  ConnectionCodeFragment.newInstance(connection_Key), "connCode").commit();
        CHIAVE_ESCURSIONE = connection_Key;
        wrd.setDb(db)
                .setUserid(AUTH.getUid())
                .setMkey(connection_Key)
                .setEcursion(excursionSheet)
                .keysCollection();
    }

    @Override
    public void onConnectionCodeFragmentCancelPressed() {
        mListener.onConnectionDialogCancelClicked();
        UsefulMethods.deleteKeyFromDB();
    }

    @Override
    public void onConnectionCodeFragmentOkPressed() {

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