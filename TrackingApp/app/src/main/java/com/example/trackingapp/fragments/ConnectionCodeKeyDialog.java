package com.example.trackingapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trackingapp.R;
import com.example.trackingapp.fragments.viewmodels.FollowUserInfoModel;
import com.example.trackingapp.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.example.trackingapp.util.Constants.CHIAVE_ESCURSIONE;
import static com.example.trackingapp.util.Constants.COLLECTION_ESCURSIONE;

public class ConnectionCodeKeyDialog extends DialogFragment {

    private FragmentManager fragmentManager = null;
    private String connectionCode = null;
    private FirebaseFirestore db = null;

    static public ConnectionCodeKeyDialog newInstance() {
        ConnectionCodeKeyDialog dialog = new ConnectionCodeKeyDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Costruzione del layout
        fragmentManager = getChildFragmentManager();

        final View v = inflater.inflate(R.layout.connection_code_dialog_key, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((TextView)v.findViewById(R.id.ConnectionCodeKeyDialog_Code)).setText(CHIAVE_ESCURSIONE);
        return v;
    }
}
