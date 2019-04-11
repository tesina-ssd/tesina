package com.example.trackingapp.Fragments.ViewModels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class FollowUserInfoModel extends BaseObservable {
    private String name;
    private String activityType;
    private String startingTimeTime;
    private String startingTimeDate;
    private String finishTimeTime;
    private String finishTimeDate;
    private String lastKnownPosition;

    public FollowUserInfoModel(String name, String activityType, String startingTimeTime, String startingTimeDate, String finishTimeTime, String finishTimeDate, String lastKnownPosition) {
        this.name = name;
        this.activityType = activityType;
        this.startingTimeTime = startingTimeTime;
        this.startingTimeDate = startingTimeDate;
        this.finishTimeTime = finishTimeTime;
        this.finishTimeDate = finishTimeDate;
        this.lastKnownPosition = lastKnownPosition;
    }

    @Bindable
    public String getLastKnownPosition() {
        return this.lastKnownPosition;
    }

    public String getName() {
        return name;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getStartingTimeTime() {
        return startingTimeTime;
    }

    public String getStartingTimeDate() {
        return startingTimeDate;
    }

    public String getFinishTimeTime() {
        return finishTimeTime;
    }

    public String getFinishTimeDate() {
        return finishTimeDate;
    }

    public void setLastKnownPosition(String lastKnownPosition) {
        this.lastKnownPosition = lastKnownPosition;
        notifyPropertyChanged(com.example.trackingapp.BR.lastKnownPosition);
    }
}
