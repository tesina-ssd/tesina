package com.example.trackingapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.trackingapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.trackingapp.util.Constants.CHIAVE_ESCURSIONE;

/**
 * DialogFragment di inserimento del codice di connessine da parte del tracciante
 */
public class ConnectionCodeKeyDialog extends DialogFragment {

    private String connectionCode = null;

    /** Metodo che istanzia un nuovo dialog */
    static public ConnectionCodeKeyDialog newInstance() {
        ConnectionCodeKeyDialog dialog = new ConnectionCodeKeyDialog();
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Costruzione del layout
        FragmentManager fragmentManager = getChildFragmentManager();

        final View v = inflater.inflate(R.layout.connection_code_dialog_key, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((TextView)v.findViewById(R.id.ConnectionCodeKeyDialog_Code)).setText(CHIAVE_ESCURSIONE);

        return v;
    }




}
