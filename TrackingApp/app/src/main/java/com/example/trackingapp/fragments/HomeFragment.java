package com.example.trackingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trackingapp.R;
import com.example.trackingapp.util.Constants;
import com.google.android.material.chip.Chip;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    public HomeFragment() {}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_layout, container, false);
        v.findViewById(R.id.CardTrackingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Chip)getView().findViewById(R.id.CardTrackingSmsChip)).isChecked()) Constants.SMS_ENABLE = true;
                else Constants.SMS_ENABLE = false;

                if(((Chip)getView().findViewById(R.id.CardTrackingInternetChip)).isChecked()) Constants.INTERNET_FALSE = true;
                else Constants.INTERNET_FALSE = false;

                if(!Constants.SMS_ENABLE && !Constants.INTERNET_FALSE) {
                    ((Chip)getView().findViewById(R.id.CardTrackingInternetChip)).setError("Selezionare almeno una opzione");
                    ((Chip)getView().findViewById(R.id.CardTrackingSmsChip)).setError("Selezionare almeno una opzione");
                } else {
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_trackingMapFragment);
                }
            }
        });

        v.findViewById(R.id.CardFollowBtn).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_followMapFragment));
        return v;
    }
}
