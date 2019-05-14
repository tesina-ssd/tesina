package com.example.trackingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.example.trackingapp.util.ExcursionSheetMapBuilder;
import com.example.trackingapp.R;
import com.google.android.material.textfield.TextInputEditText;
import java.util.GregorianCalendar;
import java.util.Map;

import androidx.fragment.app.DialogFragment;

public class ExcursionSheetFragmentPt1 extends androidx.fragment.app.Fragment implements TimePickerFragment.TimePickerFragmentListener, DatePickerFragment.DatePickerFragmentListener {

    private String startingTimeTimePicker = "strTmTmePckr";
    private String finishingTimeTimePicker = "fnshTmTmePckr";
    private String startingTimeDatePicker = "strDtDtPck";
    private String finishingTimeDatePicker = "fnshDtDtPck";

    private TextInputEditText txtStartingTimeTimeText;
    private TextInputEditText txtFinishingTimeTimeText;
    private TextInputEditText txtStartingTimeDateText;
    private TextInputEditText txtFinishingTimeDateText;
    private TextInputEditText txtPeopleNumberText;
    private Spinner spnActivityType;

    private int startingTimeDateYear;
    private int startingTimeDateMonth;
    private int startingTimeDateDay;
    private int finishingTimeDateYear;
    private int finishingTimeDateMonth;
    private int finishingTimeDateDay;
    private int startingTimeTimeHour;
    private int startingTimeTimeMinute;
    private int finishingTimeTimeHour;
    private int finishingTimeTimeMinute;

    private OnExcursionSheetFragmentPt1InteractionListener mListener;

    public interface OnExcursionSheetFragmentPt1InteractionListener {
        void onExcursionSheetFragmentPt1CancelPressed();
        void onExcursionSheetFragmentPt1NextPressed(ExcursionSheetMapBuilder excursionSheet);
    }

    public ExcursionSheetFragmentPt1() {}

    public static ExcursionSheetFragmentPt1 newInstance() {
        return new ExcursionSheetFragmentPt1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.excursion_sheet_pt1, container, false);

        // Acquisizione degli elementi del layout
        Button btnNext = v.findViewById(R.id.ExcursionSheet_BtnNext_Pt1);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFields())
                    onNextPressed();
            }
        });

        Button btnCancel = v.findViewById(R.id.ExcursionSheet_BtnCancel_Pt1);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelPressed();
            }
        });

        txtStartingTimeTimeText = v.findViewById(R.id.ExcursionSheet_StartingTimeTime_Text);
        txtStartingTimeTimeText.setInputType(InputType.TYPE_NULL);

        txtStartingTimeTimeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    DialogFragment timePicker = new TimePickerFragment();
                    timePicker.show(getChildFragmentManager(), startingTimeTimePicker);
                }
            }
        });

        txtFinishingTimeTimeText = v.findViewById(R.id.ExcursionSheet_FinishingTimeTime_Text);
        txtFinishingTimeTimeText.setInputType(InputType.TYPE_NULL);

        txtFinishingTimeTimeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    DialogFragment timePicker = new TimePickerFragment();
                    timePicker.show(getChildFragmentManager(), finishingTimeTimePicker);
                }
            }
        });

        txtStartingTimeDateText = v.findViewById(R.id.ExcursionSheet_StartingTimeDate_Text);
        txtStartingTimeDateText.setInputType(InputType.TYPE_NULL);

        txtStartingTimeDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getChildFragmentManager(), startingTimeDatePicker);
                }
            }
        });

        txtFinishingTimeDateText = v.findViewById(R.id.ExcursionSheet_FinishingTimeDate_Text);
        txtFinishingTimeDateText.setInputType(InputType.TYPE_NULL);

        txtFinishingTimeDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getChildFragmentManager(), finishingTimeDatePicker);
                }
            }
        });

        txtPeopleNumberText = v.findViewById(R.id.ExcursionSheet_PeopleNumber_Text);
        txtPeopleNumberText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    txtPeopleNumberText.setError(null);
                }
            }
        });

        spnActivityType = v.findViewById(R.id.ExcursionSheet_ActivityType);

        return v;
    }


    private void onCancelPressed() {
        if (mListener != null) {
            mListener.onExcursionSheetFragmentPt1CancelPressed();
        }
    }

    private void onNextPressed() {
        if(mListener != null) {
            mListener.onExcursionSheetFragmentPt1NextPressed(
                    new ExcursionSheetMapBuilder()
                            .setActivityType((String) spnActivityType.getSelectedItem())
                            .setStartingTimeDate(new GregorianCalendar(startingTimeDateYear, startingTimeDateMonth, startingTimeDateDay, startingTimeTimeHour, startingTimeTimeMinute).getTime())
                            .setFinishingTimeDate(new GregorianCalendar(finishingTimeDateYear, finishingTimeDateMonth, finishingTimeDateDay, finishingTimeTimeHour, finishingTimeTimeMinute).getTime())
                            .setPeopleNumber(Integer.parseInt(txtPeopleNumberText.getText().toString()))
            );
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            mListener = (OnExcursionSheetFragmentPt1InteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnExcursionSheetFragmentPt1InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTimeSelected(String tag, int hour, int minute) {
        if(tag.equals(startingTimeTimePicker)) {
            txtStartingTimeTimeText.clearFocus();

            startingTimeTimeHour = hour;
            startingTimeTimeMinute = minute;

            txtStartingTimeTimeText.setText(String.format("%02d:%02d", hour, minute));
            txtStartingTimeTimeText.setError(null);
        }

        if(tag.equals(finishingTimeTimePicker)) {
            txtFinishingTimeTimeText.clearFocus();

            finishingTimeTimeHour = hour;
            finishingTimeTimeMinute = minute;

            txtFinishingTimeTimeText.setText(String.format("%02d:%02d", hour, minute));
            txtFinishingTimeTimeText.setError(null);
        }
    }

    @Override
    public void onTimeDismiss(String tag) {
        if(tag.equals(startingTimeTimePicker)) txtStartingTimeTimeText.clearFocus();

        if(tag.equals(finishingTimeTimePicker)) txtFinishingTimeTimeText.clearFocus();
    }

    @Override
    public void onDateSelected(String tag, int year, int month, int day) {
        if(tag.equals(startingTimeDatePicker)) {
            txtStartingTimeDateText.clearFocus();

            startingTimeDateYear = year;
            startingTimeDateMonth = month;
            startingTimeDateDay = day;

            txtStartingTimeDateText.setText(String.format("%02d/%02d/%02d", day, month, year));
            txtStartingTimeDateText.setError(null);
        }

        if(tag.equals(finishingTimeDatePicker)) {
            txtFinishingTimeDateText.clearFocus();

            finishingTimeDateYear = year;
            finishingTimeDateMonth = month;
            finishingTimeDateDay = day;

            txtFinishingTimeDateText.setText(String.format("%02d/%02d/%02d", day, month, year));
            txtFinishingTimeDateText.setError(null);
        }
    }

    @Override
    public void onDateDismiss(String tag) {
        if(tag.equals(startingTimeDatePicker)) txtStartingTimeDateText.clearFocus();
        if(tag.equals(finishingTimeDatePicker)) txtFinishingTimeDateText.clearFocus();
    }

    private boolean checkFields() {

        boolean ret = true;

        if(txtStartingTimeTimeText.getText().toString().equals("")) {
            txtStartingTimeTimeText.setError("Compilare il campo");
            ret = false;
        }

        if(txtFinishingTimeTimeText.getText().toString().equals("")) {
            txtFinishingTimeTimeText.setError("Compilare il campo");
            ret = false;
        }

        if(txtStartingTimeDateText.getText().toString().equals("")) {
            txtStartingTimeDateText.setError("Compilare il campo");
            ret = false;
        }

        if(txtFinishingTimeDateText.getText().toString().equals("")) {
            txtFinishingTimeDateText.setError("Compilare il campo");
            ret = false;
        }

        // Data inizio maggiore di data fine
        // TODO : il codice sotto è funzionante, non è prefetto perchè non rimuove correttamente i messaggi di errore quando una data valida viene settata
        /* if(ret &&
                new GregorianCalendar(startingTimeDateYear, startingTimeDateMonth, startingTimeDateDay, startingTimeTimeHour, startingTimeTimeMinute).getTime().getTime() >
                new GregorianCalendar(finishingTimeDateYear, finishingTimeDateMonth, finishingTimeDateDay, finishingTimeTimeHour, finishingTimeTimeMinute).getTime().getTime()) {
            txtStartingTimeTimeText.setError("Date incompatibili");
            txtFinishingTimeTimeText.setError("Date incompatibili");
            txtStartingTimeDateText.setError("Date incompatibili");
            txtFinishingTimeDateText.setError("Date incompatibili");
            ret = false;
        } */

        if(txtPeopleNumberText.getText().toString().equals("")) {
            txtPeopleNumberText.setError("Compilare il campo");
            ret = false;
        }

        return ret;

    }
}
