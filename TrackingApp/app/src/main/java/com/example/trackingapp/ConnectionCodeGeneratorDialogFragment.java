package com.example.trackingapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class ConnectionCodeGeneratorDialogFragment extends DialogFragment {

    private String connectionCode; // Codice che viene passato come parametro alla creazione del dialog
    NoticeConnectionCodeGenerationDialogListener mListener; //Rappresenta l'istanza del chiamante (se chiamato da un fragment del fragment chiamante)

    public interface NoticeConnectionCodeGenerationDialogListener {
        // Interfacce che verranno implementate nel chiamante
        public  void onConnectionCodeGeneratorOkClick(DialogFragment dialogFragment);
        public void onConnectionCodeGeneratorCancellaClick(DialogFragment dialogFragment);
    }

    static ConnectionCodeGeneratorDialogFragment newInstance(String connectionCode) {
        ConnectionCodeGeneratorDialogFragment dialog = new ConnectionCodeGeneratorDialogFragment();

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
                mListener = (NoticeConnectionCodeGenerationDialogListener) getTargetFragment();
            else throw new ClassCastException();
        } catch (ClassCastException e) {
            throw  new ClassCastException(ConnectionCodeGeneratorDialogFragment.class + "Deve essere implementata l'interfaccia di comunicazione nel chiamante");
        }
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
        View v = inflater.inflate(R.layout.connection_code_dialog, container, false);
        Button btnOk = v.findViewById(R.id.btn_ok);
        Button btnCancella = v.findViewById(R.id.btn_cancella);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtCode = v.findViewById(R.id.textView6);
        txtCode.setText(connectionCode);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Richiama il metodo onConnectionCodeGeneratorOkClick del chiamante (quello valorizzato in onAttach())
                mListener.onConnectionCodeGeneratorOkClick(ConnectionCodeGeneratorDialogFragment.this);
            }
        });

        btnCancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Richiama il metodo onConnectionCodeGeneratorCancellaClick del chiamante (quello valorizzato in onAttach())
                mListener.onConnectionCodeGeneratorCancellaClick(ConnectionCodeGeneratorDialogFragment.this);
            }
        });

        // Viene ritornata la View corrente
        return v;
    }

}
