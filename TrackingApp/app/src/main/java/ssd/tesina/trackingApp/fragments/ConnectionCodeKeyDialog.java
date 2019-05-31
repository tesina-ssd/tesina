package ssd.tesina.trackingApp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import ssd.tesina.trackingApp.R;

import ssd.tesina.trackingApp.util.Constants;

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
        ((TextView)v.findViewById(R.id.ConnectionCodeKeyDialog_Code)).setText(Constants.CHIAVE_ESCURSIONE);

        return v;
    }




}
