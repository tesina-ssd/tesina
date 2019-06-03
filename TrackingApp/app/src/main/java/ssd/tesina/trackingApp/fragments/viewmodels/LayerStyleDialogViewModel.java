package ssd.tesina.trackingApp.fragments.viewmodels;

import android.graphics.Color;

import androidx.databinding.BaseObservable;

import java.io.Serializable;

public class LayerStyleDialogViewModel extends BaseObservable implements Serializable {

    private boolean streetSelected;
    private boolean satelliteSelected;
    private boolean outdoorSelected;

    private final int color = Color.parseColor("#409fff");

    public LayerStyleDialogViewModel(boolean streetSelected, boolean satelliteSelected, boolean outdoorSelected) {
        this.streetSelected = streetSelected;
        this.satelliteSelected = satelliteSelected;
        this.outdoorSelected = outdoorSelected;
    }

    public int getColor() {
        return color;
    }

    public boolean isStreetSelected() {
        return streetSelected;
    }

    public void setStreetSelected(boolean streetSelected) {
        this.streetSelected = streetSelected;
        this.outdoorSelected = false;
        this.satelliteSelected = false;
    }

    public boolean isSatelliteSelected() {
        return satelliteSelected;
    }

    public void setSatelliteSelected(boolean satelliteSelected) {
        this.satelliteSelected = satelliteSelected;
        this.streetSelected = false;
        this.outdoorSelected = false;
    }

    public boolean isOutdoorSelected() {
        return outdoorSelected;
    }

    public void setOutdoorSelected(boolean outdoorSelected) {
        this.outdoorSelected = outdoorSelected;
        this.streetSelected = false;
        this.satelliteSelected = false;
    }
}
