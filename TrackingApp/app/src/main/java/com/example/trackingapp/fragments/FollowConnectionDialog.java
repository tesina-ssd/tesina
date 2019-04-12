package com.example.trackingapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.trackingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class FollowConnectionDialog extends DialogFragment {

    FirebaseFirestore db = null;

    public interface FollowConnectionDialogListener {
        void onFollowConnectionDialogOkClicked(String connectionCode);
        void onFollowConnectionDialogCancelClicked();
    }

    FollowConnectionDialogListener mListener = null;

    static public FollowConnectionDialog newInstance() {
        FollowConnectionDialog dialog = new FollowConnectionDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if(getTargetFragment() != null)
                // Viene collegato il chiamante
                mListener = (FollowConnectionDialogListener) getTargetFragment();
            else throw new ClassCastException();
        } catch (ClassCastException e) {
            throw  new ClassCastException(ConnectionDialog.class + ": Deve essere implementata l'interfaccia di comunicazione nel chiamante");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.follow_connection_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Viene ritornata la View corrente
        Button btnOk =  v.findViewById(R.id.FollowConnectionDialog_BtnOk);
        Button btnCancel = v.findViewById(R.id.FollowConnectionDialog_BtnCancel);

        final TextInputEditText txtConnectionCode = v.findViewById(R.id.FollowConnectionDialod_CodeText);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String connectionCode = txtConnectionCode.getText().toString();
                if(connectionCode.equals("")) {
                    txtConnectionCode.setError("Inserire un codice valido");
                } else {
                    DocumentReference docRef = db.collection("excursionKeys").document(connectionCode);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()) {
                                    mListener.onFollowConnectionDialogOkClicked(document.get("useid").toString());
                                } else {
                                    txtConnectionCode.setError("Codice di connessione non valido");
                                }
                            } else {
                                //TODO: gestire l'errore in maniera migliore
                                Toast.makeText(getContext(), "Errore di connessione", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFollowConnectionDialogCancelClicked();
            }
        });

        return v;
    }

}
