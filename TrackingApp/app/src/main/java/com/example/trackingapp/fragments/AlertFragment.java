package com.example.trackingapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackingapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class AlertFragment extends Fragment {

    private CountDownTimer timer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_alert,container,false);
        (view.findViewById(R.id.FragmentAlert_BtnAnnulla)).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_alertFragment_to_trackingMapFragment));

        timer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                ((TextView)view.findViewById(R.id.FragmentAlert_Text)).setText(
                        (millisUntilFinished / 1000 == 0) ? "Avvio chiamata in corso ..." : "Una chiamata verrà avviata automaticamente tra " + millisUntilFinished / 1000 + (millisUntilFinished / 1000 == 1 ?" secondo" : " secondi"));
            }

            public void onFinish() {
                Toast.makeText(getContext(), "Finito!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "3663186599"));
                startActivityForResult(intent, 1);
            }
        }.start();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1) Navigation.findNavController(getView()).navigate(R.id.action_alertFragment_to_trackingMapFragment);
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }
}
