package com.example.trackingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class ConnectionCodeFragment extends androidx.fragment.app.Fragment {

    private String connectionCode;
    private OnConnectionCodeFragmentInteractionListener mListener;

    public ConnectionCodeFragment() {}

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

        Button btnCancel = (Button) v.findViewById(R.id.ConnectionCode_BtnCanel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConnectionCodeFragmentCancelPressed();
            }
        });

        Button btnOk = (Button) v.findViewById(R.id.ConnectionCode_BtnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConnectionCodeFragmentOkPressed();
            }
        });

        TextView code = v.findViewById(R.id.ConnectionFialog_Code);
        code.setText(connectionCode);

        return v;
    }

    public void onConnectionCodeFragmentCancelPressed() {
        if (mListener != null) {
            mListener.onConnectionCodeFragmentCancelPressed();
        }
    }

    public void onConnectionCodeFragmentOkPressed() {
        if (mListener != null) {
            mListener.onConnectionCodeFragmentOkPressed();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

   public interface OnConnectionCodeFragmentInteractionListener {
        void onConnectionCodeFragmentCancelPressed();
        void onConnectionCodeFragmentOkPressed();
    }
}