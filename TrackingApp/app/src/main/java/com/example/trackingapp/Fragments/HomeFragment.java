package com.example.trackingapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trackingapp.R;

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
        v.findViewById(R.id.CardTrackingBtn).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_trackingMapFragment));
        v.findViewById(R.id.CardFollowBtn).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_followMapFragment));
        return v;
    }
}
