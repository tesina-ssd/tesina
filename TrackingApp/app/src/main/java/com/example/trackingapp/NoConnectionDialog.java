package com.example.trackingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class NoConnectionDialog extends DialogFragment {
    int mNum;
     static NoConnectionDialog newInstance(int num) {
        NoConnectionDialog f = new NoConnectionDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mNum = getArguments().getInt("num");
        if (mNum == 1) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.noInternetConnection)
                    .setPositiveButton("OK", null);

            // Create the AlertDialog object and return it
            return builder.create();
        } else {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
            build.setMessage(R.string.connesioneLenta)
                    .setPositiveButton("OK", null);

            // Create the AlertDialog object and return it
            return build.create();
        }


    }



}