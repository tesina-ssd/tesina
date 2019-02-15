package com.example.trackingapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.DialogFragment;


public class ExcursionSheetFragment extends androidx.fragment.app.Fragment {

    OnExcursionSheetFragmentInteractionListener mListener;

    public ExcursionSheetFragment() {}

    public static ExcursionSheetFragment newInstance() {
        ExcursionSheetFragment fragment = new ExcursionSheetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.excursion_sheet, container, false);

        // Acquisizione degli elementi del layout
        Button btnNext = (Button) v.findViewById(R.id.ExcursionSheet_BtnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextPressed();
            }
        });

        Button btnCancel = (Button) v.findViewById(R.id.ExcursionSheet_BtnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelPressed();
            }
        });

        TextInputEditText txtStartingTimeTimeText = (TextInputEditText) v.findViewById(R.id.ExcursionSheet_StartingTimeTime_Text);
        txtStartingTimeTimeText.setInputType(InputType.TYPE_NULL);

        txtStartingTimeTimeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    DialogFragment timePicker = new TimePickerFragment();
                    timePicker.show(getChildFragmentManager(), "timePicker");
                }
            }
        });

        return v;
    }

    public void onCancelPressed() {
        if (mListener != null) {
            mListener.onExcursionSheetFragmentCancelPressed();
        }
    }

    public void onNextPressed() {
        if(mListener != null) {
            mListener.onExcursionSheetFragmentNextPressed();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            mListener = (OnExcursionSheetFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExcursionSheetFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnExcursionSheetFragmentInteractionListener {
        void onExcursionSheetFragmentCancelPressed();
        void onExcursionSheetFragmentNextPressed();
    }
}
