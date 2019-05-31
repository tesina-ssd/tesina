package ssd.tesina.trackingApp.fragments.viewmodels;

import android.graphics.Color;

import androidx.databinding.BaseObservable;

import java.io.Serializable;

public class LayerStyleDialogViewModel extends BaseObservable implements Serializable {
    private boolean streetSelected = false;
    private boolean satelliteSelected = false;
    private boolean outdoorSelected = false;

    private int color = Color.parseColor("#409fff");

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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
