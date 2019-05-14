package com.example.trackingapp.util;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExcursionSheetMapBuilder implements Serializable {
    private String activityType;
    private Date startingTimeDate;
    private Date finishingTimeDate;
    private int peopleNumber;
    private String startingLocation;
    private String finishLocation;
    private String trackPath;
    private String otherComponentsNames;
    private String otherComponentsNumbers;
    private String photoPath;

    public ExcursionSheetMapBuilder setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public ExcursionSheetMapBuilder setOtherComponentsNames(String otherComponentsNames) {
        this.otherComponentsNames = otherComponentsNames;
        return this;
    }

    public ExcursionSheetMapBuilder setOtherComponentsNumbers(String otherComponentsNumbers) {
        this.otherComponentsNumbers = otherComponentsNumbers;
        return this;
    }

    public ExcursionSheetMapBuilder setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
        return this;
    }

    public ExcursionSheetMapBuilder setFinishLocation(String finishLocation) {
        this.finishLocation = finishLocation;
        return this;
    }

    public ExcursionSheetMapBuilder setTrackPath(String trackPath) {
        this.trackPath = trackPath;
        return this;
    }

    public ExcursionSheetMapBuilder setActivityType(String activityTime) {
        this.activityType = activityTime;
        return this;
    }

    public ExcursionSheetMapBuilder setStartingTimeDate(Date startingTimeDate) {
        this.startingTimeDate = startingTimeDate;
        return this;
    }

    public ExcursionSheetMapBuilder setFinishingTimeDate(Date finishingTimeDate) {
        this.finishingTimeDate = finishingTimeDate;
        return this;
    }

    public ExcursionSheetMapBuilder setPeopleNumber(int peopleNumber) {
        this.peopleNumber = peopleNumber;
        return this;
    }

    public Map<String, Object>  build() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("activityType", activityType);
        ret.put("startingTimeDate", new Timestamp(startingTimeDate));
        ret.put("finishingTimeDate", new Timestamp(finishingTimeDate));
        ret.put("peopleNumber", peopleNumber);
        ret.put("startingLocation", startingLocation);
        ret.put("finishLocation", finishLocation);
        ret.put("trackPath", trackPath);
        ret.put("otherComponentsNames", otherComponentsNames);
        ret.put("otherComponentsNumbers", otherComponentsNumbers);
        return ret;
    }
}
