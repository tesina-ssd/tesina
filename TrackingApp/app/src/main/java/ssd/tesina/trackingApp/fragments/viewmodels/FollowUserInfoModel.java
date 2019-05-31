package ssd.tesina.trackingApp.fragments.viewmodels;

import java.io.Serializable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ssd.tesina.trackingApp.BR;

public class FollowUserInfoModel extends BaseObservable implements Serializable {
    private String name;
    private String activityType;
    private String startingTimeTime;
    private String startingTimeDate;
    private String finishTimeTime;
    private String finishTimeDate;
    private String lastKnownPosition;
    private String peopleNumber;
    private String picPath;

    public FollowUserInfoModel() {}

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

    public String getPeopleNumber() {
        return peopleNumber;
    }

    public void setLastKnownPosition(String lastKnownPosition) {
        this.lastKnownPosition = lastKnownPosition;
        notifyPropertyChanged(BR.lastKnownPosition);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public void setStartingTimeTime(String startingTimeTime) {
        this.startingTimeTime = startingTimeTime;
    }

    public void setStartingTimeDate(String startingTimeDate) {
        this.startingTimeDate = startingTimeDate;
    }

    public void setFinishTimeTime(String finishTimeTime) {
        this.finishTimeTime = finishTimeTime;
    }

    public void setFinishTimeDate(String finishTimeDate) {
        this.finishTimeDate = finishTimeDate;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public void setPeopleNumber(String peopleNumber) {
        this.peopleNumber = peopleNumber;
    }

}
