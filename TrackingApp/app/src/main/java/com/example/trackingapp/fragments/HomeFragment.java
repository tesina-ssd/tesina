package com.example.trackingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trackingapp.R;
import com.example.trackingapp.util.Constants;
import com.google.android.material.chip.Chip;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    public HomeFragment() {}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.home_layout, container, false);

        final Chip smsChip = v.findViewById(R.id.CardTrackingSmsChip);
        final Chip internetChip = v.findViewById(R.id.CardTrackingInternetChip);

        smsChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Chip)v).getError() != null) ((Chip)v).setError(null);
            }
        });

        internetChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Chip)v).getError() != null) ((Chip)v).setError(null);
            }
        });

        v.findViewById(R.id.CardTrackingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.SMS_ENABLE = smsChip.isChecked();
                Constants.INTERNET_ENABLE = internetChip.isChecked();

                if(!Constants.SMS_ENABLE && !Constants.INTERNET_ENABLE) {
                    (internetChip).setError("Selezionare almeno una opzione");
                    (smsChip).setError("Selezionare almeno una opzione");
                } else {
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_trackingMapFragment);
                }
            }
        });

        v.findViewById(R.id.CardFollowBtn).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_followMapFragment));

        return v;
    }
}
