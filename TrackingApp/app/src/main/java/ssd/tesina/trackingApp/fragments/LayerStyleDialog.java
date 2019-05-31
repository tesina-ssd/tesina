package ssd.tesina.trackingApp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import ssd.tesina.trackingApp.R;
import ssd.tesina.trackingApp.databinding.LayerStyleDialogBinding;
import ssd.tesina.trackingApp.fragments.viewmodels.LayerStyleDialogViewModel;
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

        final LayerStyleDialogViewModel viewModel = new LayerStyleDialogViewModel();

        LayerStyleDialogBinding binding = DataBindingUtil.inflate(inflater ,R.layout.layer_style_dialog, container, false);
        binding.setViewModel(viewModel);
        View v = binding.getRoot();

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton imgBtnOutDoor = v.findViewById(R.id.imageViewOutdoor);
        ImageButton imgBtnSatellite = v.findViewById(R.id.imageViewSat);
        ImageButton imgBtnMbStreets = v.findViewById(R.id.imageViewMbStreets);

        imgBtnOutDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLayerStyleClicked(Style.OUTDOORS);
                viewModel.setOutdoorSelected(true);
            }
        });

        imgBtnSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLayerStyleClicked(Style.SATELLITE);
                viewModel.setSatelliteSelected(true);
            }
        });

        imgBtnMbStreets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLayerStyleClicked(Style.MAPBOX_STREETS);
                viewModel.setStreetSelected(true);
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
