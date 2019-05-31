package ssd.tesina.trackingApp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ssd.tesina.trackingApp.R;

import ssd.tesina.trackingApp.util.Constants;

/**
 * Fragment di visualizzazione del codice di connessione
 */
public class ConnectionCodeFragment extends androidx.fragment.app.Fragment {

    private String connectionCode=""; // Codice di connessione

    /** Interfaccia di comunicazione con il chiamante */
    public interface OnConnectionCodeFragmentInteractionListener {
        void onConnectionCodeFragmentCancelPressed();
        void onConnectionCodeFragmentOkPressed();
    }

    private OnConnectionCodeFragmentInteractionListener mListener; // Fragment chiamante

    public ConnectionCodeFragment() {}

    /**
     * Genera una nuova istanza di ConnectionCodeFragment
     * @param connectionCode : il codice di connessione da visualizzare
     */
    public static ConnectionCodeFragment newInstance(String connectionCode) {
        ConnectionCodeFragment fragment = new ConnectionCodeFragment();

        Bundle args = new Bundle();
        args.putString("connectionCode", connectionCode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            connectionCode = getArguments().getString("connectionCode");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.connection_code_dialog, container, false);

        // Gestione del click sul tasto cancella
        Button btnCancel = v.findViewById(R.id.ConnectionCode_BtnCanel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConnectionCodeFragmentCancelPressed();
            }
        });
        v.findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("inShae","share");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Chiave escursione");
                shareIntent.putExtra(Intent.EXTRA_TEXT, Constants.CHIAVE_ESCURSIONE);
                startActivity(Intent.createChooser(shareIntent,"Condividi tramite"));

            }
        });
        // Gestione del click sul tasto ok
        Button btnOk = v.findViewById(R.id.ConnectionCode_BtnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConnectionCodeFragmentOkPressed();
            }
        });

        TextView code = v.findViewById(R.id.ConnectionCodeKeyDialog_Code);
        code.setText(connectionCode);

        return v;
    }

    /** Gestisce il click sul pulsante cancella */
    private void onConnectionCodeFragmentCancelPressed() {
        if (mListener != null) {
            // Viene demandata l'implementazione del metodo al chiamante
            mListener.onConnectionCodeFragmentCancelPressed();
        }
    }

    /** Gestisce il click sul tasto OK */
    private void onConnectionCodeFragmentOkPressed() {
        if (mListener != null) {
            // Viene demandata l'implementazione del metodo al chiamante
            mListener.onConnectionCodeFragmentOkPressed();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Connessione con il chiamante mediante l'interfaccia definita
        if (getParentFragment() != null) {
            mListener = (OnConnectionCodeFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnConnectionCodeFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
