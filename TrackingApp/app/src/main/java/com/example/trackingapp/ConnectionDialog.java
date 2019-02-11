package com.example.trackingapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ConnectionDialog extends DialogFragment implements  AvantiTestFragment.OnFragmentInteractionListener {
    private String connectionCode = null;
    private FragmentManager fragmentManager = null;
    //ConnectionDialogListener mListener = null;

    /*public interface ConnectionDialogListener {
        void onConnectionDialogOkClicked();
        void onConnectionDialogCancelClicked();
    }*/

    static ConnectionDialog newInstance(String connectionCode) {
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
        /*try {
            if(getTargetFragment() != null)
                // Viene collegato il chiamante
                mListener = (ConnectionDialogListener) getTargetFragment();
            else throw new ClassCastException();
        } catch (ClassCastException e) {
            throw  new ClassCastException(ConnectionCodeGeneratorDialogFragment.class + ": Deve essere implementata l'interfaccia di comunicazione nel chiamante");
        }*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Viene prelevato il codice passato come parametro e valorizzata la proprietà corrispondente dell'istanza corrente
        connectionCode = getArguments().getString("connectionCode");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Costruzione del layout
        fragmentManager = getChildFragmentManager();

        View v = inflater.inflate(R.layout.connection_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Fragment avantiFragment = AvantiTestFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.cardViewConnectionDialog,  avantiFragment, "avantiTest").commit();


        // Viene ritornata la View corrente
        return v;
    }

    @Override
    public void onFragmentInteraction() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        transaction.replace(R.id.cardViewConnectionDialog,  ConnectionCodeFragment.newInstance("xdrt56ghy"), "connCode").commit();
    }
}
