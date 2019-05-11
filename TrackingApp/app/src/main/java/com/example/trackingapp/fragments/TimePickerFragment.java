package com.example.trackingapp.fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerFragmentListener mListener;

    public interface TimePickerFragmentListener {
        void onTimeSelected(String tag, int hour, int minute);
        void onTimeDismiss(String tag);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        setCancelable(false);

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        mListener.onTimeSelected(this.getTag(), hour, minute);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            mListener = (TimePickerFragment.TimePickerFragmentListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TimePickerFragmentListener");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener.onTimeDismiss(this.getTag());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
