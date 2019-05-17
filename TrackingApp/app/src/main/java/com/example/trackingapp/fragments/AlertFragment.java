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

/**
 * Fragment di chiamata di soccorso:  viee visualizzata una schermata contenente
 * un avviso di chiamata che può essere disattivato entro 10 secondi, terminato
 * il tempo viene avviata una chiamata verso il numero di telefono di allarme
 * impostato all'interno delle impostazioni */
public class AlertFragment extends Fragment {

    private CountDownTimer timer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_alert,container,false);

        // Gestione del click sul pulsante annulla
        (view.findViewById(R.id.FragmentAlert_BtnAnnulla)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).popBackStack(); // Torna indietro al fragment precedente
            }
        });

        // Viene impostato il timer a 10 secondi, risvegliato ogni secondo per decerementare la lable
        // di segnalazione
        timer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                ((TextView)view.findViewById(R.id.FragmentAlert_Text)).setText(
                        (millisUntilFinished / 1000 == 0) ? "Avvio chiamata in corso ..." : "Una chiamata verrà avviata automaticamente tra " + millisUntilFinished / 1000 + (millisUntilFinished / 1000 == 1 ?" secondo" : " secondi"));
            }

            // Al termine del countDown viene avviata la chiamata
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
        // Terminata la chiamata torna indietro al fragment precedente
        if(requestCode == 1) Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }
}
