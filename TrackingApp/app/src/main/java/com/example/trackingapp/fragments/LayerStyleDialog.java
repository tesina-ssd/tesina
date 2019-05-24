package com.example.trackingapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.example.trackingapp.R;
import com.mapbox.mapboxsdk.maps.Style;

public class LayerStyleDialog extends DialogFragment {

    public interface LayerStyleDialogInterface {
        void onLayerStyleClicked(String layer);
    }

    LayerStyleDialogInterface mListener = null;

    static LayerStyleDialog newInstance() {
        LayerStyleDialog dialog = new LayerStyleDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if(getTargetFragment() != null)
                // Viene collegato il chiamante
                mListener = (LayerStyleDialogInterface) getTargetFragment();
            else throw new ClassCastException();
        } catch (ClassCastException e) {
            throw  new ClassCastException(ConnectionDialog.class + ": Deve essere implementata l'interfaccia di comunicazione nel chiamante");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layer_style_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton imgBtnOutDoor =(ImageButton) v.findViewById(R.id.imageViewOutdoor);
        ImageButton imgBtnSatellite =(ImageButton) v.findViewById(R.id.imageViewSat);
        ImageButton imgBtnMbStreets =(ImageButton) v.findViewById(R.id.imageViewMbStreets);

        imgBtnOutDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLayerStyleClicked(Style.OUTDOORS);
            }
        });

        imgBtnSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLayerStyleClicked(Style.SATELLITE);
            }
        });

        imgBtnMbStreets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLayerStyleClicked(Style.MAPBOX_STREETS);
            }
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
